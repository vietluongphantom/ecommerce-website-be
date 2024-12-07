package com.ptit.e_commerce_website_be.do_an_nhom.services.seller;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.UserAlreadyExistedException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.*;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.*;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.RoleEnum;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.*;

import com.ptit.e_commerce_website_be.do_an_nhom.services.EmailService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.RedisOtpService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.auth.AuthenticationServiceImpl;
import lombok.RequiredArgsConstructor;
import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.SellerAlreadyExistedException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.LoginResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService{

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationServiceImpl authenticationService;
    private final SellerRepository sellerRepository;
    private final AddressRepository addressRepository;
    private final ShopRepository shopRepository;
    private final RedisOtpService redisOtpService;
    private final EmailService emailService;
    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;
    private final OrdersRepository ordersRepository;
    private final WarehouseRepository warehouseRepository;
    private final VoucherRepository voucherRepository;

    @Override
    @Transactional
    public User signUpSeller(SellerRegisterDto input){
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.SELLER);
        if (optionalRole.isEmpty()) { return null; }

        Role sellerRole = optionalRole.get();

        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            Set<Role> existingRoles = existingUser.getRoles();
//            Role sellerRole = Role.builder().name(RoleEnum.SELLER).build();

            if (existingRoles.contains(sellerRole) && existingUser.isEnabled()) {
                throw new SellerAlreadyExistedException(input.getEmail());
            } else {
                existingRoles.add(sellerRole);
                existingUser.setRoles(existingRoles);
                return userRepository.save(existingUser);
            }
        } else {
            Set<Role> roles = new HashSet<>(List.of(optionalRole.get()));
            User user = User.builder()
                    .fullName(input.getFullName())
                    .email(input.getEmail())
                    .password(passwordEncoder.encode(input.getPassword()))
                    .phone(input.getPhone())
                    .gender(input.getGender())
                    .roles(roles)
                    .build();
            userRepository.save(user);

            Shop shop = Shop.builder().build();
            shop.setUserId(user.getId());
            shopRepository.save(shop);
            Seller seller = new Seller();
            seller.setTax(input.getTax());
            seller.setCccd(input.getCccd());
            seller.setShopId(input.getShopId());
            seller.setUserId(user.getId());
            seller.setShopId(shop.getId());
            sellerRepository.save(seller);
            return user;
        }
    }

    @Override
    public LoginResponse authenticateSellerAndGetLoginResponse(LoginUserDto loginUserDto) {
        return authenticationService.authenticateSellerAndGetLoginResponse(loginUserDto);
    }

    @Override
    public User getAuthenticatedSeller() {
        return (User) authenticationService.getAuthentication().getPrincipal();
    }


    @Override
    public DetailSellerInfoDTO getSellerInfo(Long userId){
        DetailSellerInfoDTO detailSellerInfoDTO = sellerRepository.getDetailSellerInfo(userId)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));
        return detailSellerInfoDTO;
    }

    @Override
    @Transactional
    public DetailSellerInfoDTO updateSellerInfo(DetailSellerInfoDTO detailSellerInfoDTO,Long userId){
        Seller seller = sellerRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find seller by id"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find user by id"));

        Address address = addressRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find address by id"));
        seller.setTax(detailSellerInfoDTO.getTax());
        seller.setCccd(detailSellerInfoDTO.getCccd());
        user.setFullName(detailSellerInfoDTO.getFullName());
        user.setPhone(detailSellerInfoDTO.getPhone());
        user.setGender(detailSellerInfoDTO.getGender());
        address.setAddressDetail(detailSellerInfoDTO.getAddressDetail());
        address.setCommune(detailSellerInfoDTO.getCommune());
        address.setCountry(detailSellerInfoDTO.getCountry());
        address.setProvince(detailSellerInfoDTO.getProvince());
        address.setDistrict(detailSellerInfoDTO.getDistrict());
        sellerRepository.save(seller);
        userRepository.save(user);
        addressRepository.save(address);

        return detailSellerInfoDTO;
    }

    @Override
    public User viewDetailsOfAnSeller(Long id){
        Optional<User> user = sellerRepository.findUserWithSellerRoleById(id);
        if (user.isEmpty()) {
            throw new DataNotFoundException("There is no seller with this id");
        } else {
            return user.get();
        }
    }

    @Override
    public Seller updateSellerInfo(SellerDTO sellerDTO){
        Optional<Seller> optionalSeller = sellerRepository.findById(sellerDTO.getUserId());

        if (optionalSeller.isEmpty()) {
            throw new DataNotFoundException("There is no seller with this id");
        }
        Seller seller = optionalSeller.get();
        seller.setTax(sellerDTO.getTax());
        seller.setCccd(sellerDTO.getCccd());

        Optional<User> user = userRepository.findById(sellerDTO.getUserId());
        // The data is separated (data from UserDto and SellerDto)
        return sellerRepository.save(seller);
    }

    @Override
    @Transactional
    public Shop updateShopInfo(Long userId, ShopDTO shopDTO){
        Seller seller = sellerRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Seller not found for userId: " + userId));
        Shop shop = shopRepository.findById(seller.getShopId())
                .orElseThrow(() -> new DataNotFoundException("Shop not found for shopId: " + seller.getShopId()));

        shop.setName(shopDTO.getName());
        shop.setMail(shopDTO.getMail());
        shop.setPhone(shopDTO.getPhone());
        shop.setAddressId(shopDTO.getAddressId());
        shopRepository.save(shop);
        return shop;
    }

    @Override
    @Transactional
    public User signUpNewVersion(SellerRegisterDto sellerRegisterDto) {
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.SELLER);
        if (optionalRole.isEmpty()) { return null; }

        Role sellerRole = optionalRole.get();
        Optional<User> optionalUser = userRepository.findByEmail(sellerRegisterDto.getEmail());

        if (optionalUser.isPresent()) {
            // If user is already existing
            User existingUser = optionalUser.get();
            Set<Role> existingRoles = existingUser.getRoles();

            if (existingRoles.contains(sellerRole)) {
                throw new SellerAlreadyExistedException(sellerRegisterDto.getEmail());
            } else {
                existingRoles.add(sellerRole);
                existingUser.setRoles(existingRoles);
                existingUser.setPassword(passwordEncoder.encode(sellerRegisterDto.getPassword()));
                existingUser.setFullName(sellerRegisterDto.getFullName());
                existingUser.setPhone(sellerRegisterDto.getPhone());
                existingUser.setGender(sellerRegisterDto.getGender());
                existingUser.setStatus(false);
                // Save as inactive account
                userRepository.save(existingUser);

                Seller seller = Seller.builder()
                        .tax(sellerRegisterDto.getTax())
                        .cccd(sellerRegisterDto.getCccd())
                        .userId(existingUser.getId())
                        .build();

                Shop shop = Shop.builder().build();
                shop.setUserId(existingUser.getId());
                shopRepository.save(shop);

                seller.setShopId(shop.getId());
                sellerRepository.save(seller);

                Integer otp = redisOtpService.generateAndSaveOtp(sellerRegisterDto.getEmail());
                String roleNames = String.valueOf(existingRoles.stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toList()));
                MailBody mailBody = MailBody.builder()
                        .to(sellerRegisterDto.getEmail())
                        .text("You are already an " + roleNames + " in our system. This is the OTP for your request: " + otp)
                        .build();
                emailService.sendSimpleMessage(mailBody);

                return existingUser;
            }
        } else {
            Set<Role> roles = new HashSet<>(List.of(optionalRole.get()));
            User user = User.builder()
                    .fullName(sellerRegisterDto.getFullName())
                    .email(sellerRegisterDto.getEmail())
                    .password(passwordEncoder.encode(sellerRegisterDto.getPassword()))
                    .phone(sellerRegisterDto.getPhone())
                    .gender(sellerRegisterDto.getGender())
                    .status(false)
                    .roles(roles)
                    .build();

            userRepository.save(user);

            Shop shop = Shop.builder().build();
            shop.setUserId(user.getId());
            shopRepository.save(shop);

            Seller seller = Seller.builder()
                    .tax(sellerRegisterDto.getTax())
                    .cccd(sellerRegisterDto.getCccd())
                    .userId(user.getId())
                    .shopId(shop.getId())
                    .build();
            sellerRepository.save(seller);

            Integer otp = redisOtpService.generateAndSaveOtp(sellerRegisterDto.getEmail());
            MailBody mailBody = MailBody.builder()
                    .to(sellerRegisterDto.getEmail())
                    .text("This is the OTP for your request: " + otp)
                    .build();
            emailService.sendSimpleMessage(mailBody);

            return user;
        }
    }

    @Override
    public User signUpNewestVersion(RegisterUserDto registerUserDto){
        Role sellerRole = roleRepository.findByName(RoleEnum.SELLER)
                .orElseThrow(() -> new DataNotFoundException("Role not found"));
        Role userRole = roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> new DataNotFoundException("Role not found"));

        Optional<User> optionalUser = userRepository.findByEmail(registerUserDto.getEmail());

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            Set<Role> existingRoles = existingUser.getRoles();

            if (existingRoles.contains(sellerRole) && existingUser.isEnabled()) {
                throw new SellerAlreadyExistedException(registerUserDto.getEmail());
            }

            existingRoles.add(sellerRole);
            existingUser.setRoles(existingRoles);

            existingUser.setFullName(registerUserDto.getFullName());
            existingUser.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
            existingUser.setPhone(registerUserDto.getPhone());
            existingUser.setGender(registerUserDto.getGender());
            userRepository.save(existingUser);

            Address existingAddress = addressRepository.findById(existingUser.getAddressId()).orElse(null);
            if (isSameAddress(existingAddress, registerUserDto)) {
                existingUser.setAddressId(existingAddress.getId());
            } else {
                existingAddress.setCountry(registerUserDto.getCountry());
                existingAddress.setProvince(registerUserDto.getProvince());
                existingAddress.setDistrict(registerUserDto.getDistrict());
                existingAddress.setCommune(registerUserDto.getCommune());
                existingAddress.setAddressDetail(registerUserDto.getAddressDetail());
                existingAddress.setUserId(existingUser.getId());
            }
            addressRepository.save(existingAddress);
            existingUser.setAddressId(existingAddress.getId());
            existingUser.setStatus(false);

            userRepository.save(existingUser);
            Shop shop = Shop.builder().userId(existingUser.getId()).build();
            shopRepository.save(shop);
            Seller seller = Seller.builder()
                    .userId(existingUser.getId())
                    .shopId(shop.getId())
                    .build();
            sellerRepository.save(seller);

            Integer otp = redisOtpService.generateAndSaveOtp(registerUserDto.getEmail());
            MailBody mailBody = MailBody.builder()
                    .to(registerUserDto.getEmail())
                    .text(
                            "This is the OTP for your seller registration request: "
                                    + otp
                                    + "\nPlease verify your email to activate your account, you will be added the seller role."
                    ).build();
            emailService.sendSimpleMessage(mailBody);

            return existingUser;
        } else {
            Set<Role> roles = new HashSet<>(List.of(sellerRole, userRole));

            User user = User.builder()
                    .fullName(registerUserDto.getFullName())
                    .email(registerUserDto.getEmail())
                    .password(passwordEncoder.encode(registerUserDto.getPassword()))
                    .phone(registerUserDto.getPhone())
                    .gender(registerUserDto.getGender())
                    .status(false)
                    .roles(roles)
                    .build();
            userRepository.save(user);

            Address address = Address.builder()
                    .country(registerUserDto.getCountry())
                    .province(registerUserDto.getProvince())
                    .district(registerUserDto.getDistrict())
                    .commune(registerUserDto.getCommune())
                    .addressDetail(registerUserDto.getAddressDetail())
                    .userId(user.getId())
                    .build();
            addressRepository.save(address);

            user.setAddressId(address.getId());
            userRepository.save(user);

            Shop shop = Shop.builder().build();
            shop.setUserId(user.getId());
            shopRepository.save(shop);

            Seller seller = Seller.builder()
//                    .tax(registerUserDto.getTax())
//                    .cccd(registerUserDto.getCccd())
                    .userId(user.getId())
                    .shopId(shop.getId())
                    .build();
            sellerRepository.save(seller);

            Integer otp = redisOtpService.generateAndSaveOtp(registerUserDto.getEmail());
            MailBody mailBody = MailBody.builder()
                    .to(registerUserDto.getEmail())
                    .text(
                            "This is the OTP for your seller registering request: "
                                    + otp
                                    + "\nPlease verify your email to activate your account. You will become seller after verifying your email."
                                    + "\nYou will also use this as user account then."
                                    + "\nIf you didn't request this, please ignore this email."
                    )
                    .build();
            emailService.sendSimpleMessage(mailBody);

            return user;
        }
    }

    private boolean isSameAddress(Address existingAddress, RegisterUserDto registerUserDto) {
        return Objects.equals(existingAddress.getCountry(), registerUserDto.getCountry())
                && Objects.equals(existingAddress.getProvince(), registerUserDto.getProvince())
                && Objects.equals(existingAddress.getDistrict(), registerUserDto.getDistrict())
                && Objects.equals(existingAddress.getCommune(), registerUserDto.getCommune())
                && Objects.equals(existingAddress.getAddressDetail(), registerUserDto.getAddressDetail());
    }

    @Override
    public Map<String, Long> getBasicInfo(Long userId){
        Map<String, Long> result = new HashMap<>();
        Shop shop = shopRepository.findShopByUserId(userId)
                .orElseThrow(()-> new DataNotFoundException("shop not found by user id"));
        Long quantityProduct = productRepository.getQuantityByShopId(shop.getId());
        result.put("product_quantity", quantityProduct);
        Long quantityWarehouse  =  warehouseRepository.getQuantityByShopId(shop.getId());
        result.put("warehouse_quantity", quantityWarehouse);
        Long quantityVoucher = voucherRepository.getQuantityByShopId(shop.getId());
        result.put("voucher_quantity", quantityVoucher);
        Long quantityOrder = ordersRepository.getQuantityByShopId(shop.getId());
        result.put("order_quantity", quantityOrder);
        return result;
    }
}
