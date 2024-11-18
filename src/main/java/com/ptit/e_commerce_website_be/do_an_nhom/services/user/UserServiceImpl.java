package com.ptit.e_commerce_website_be.do_an_nhom.services.user;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.SellerAlreadyExistedException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.*;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.*;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.RoleEnum;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.*;
import com.ptit.e_commerce_website_be.do_an_nhom.services.EmailService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.JwtService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.RedisOtpService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.UserAlreadyExistedException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.LoginResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationService authenticationService;
    private final RedisOtpService redisOtpService;
    private final EmailService emailService;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    @CacheEvict(value = "sellers", allEntries = true)
    public User signUp(RegisterUserDto input) throws UserAlreadyExistedException {
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        if (optionalRole.isEmpty()) { return null; }

        Role userRole = optionalRole.get();

        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            Set<Role> existingRoles = existingUser.getRoles();
//            Role userRole = Role.builder().name(RoleEnum.USER).build();

            if (existingRoles.contains(userRole)) {
                throw new UserAlreadyExistedException(input.getEmail());
            } else {
                existingRoles.add(userRole);
                existingUser.setRoles(existingRoles);
//                sendMail(input.getEmail());
                return userRepository.save(existingUser);
            }
        } else {
            Set<Role> roles = new HashSet<>(List.of(optionalRole.get()));
            var user = User.builder()
                    .fullName(input.getFullName())
                    .email(input.getEmail())
                    .password(passwordEncoder.encode(input.getPassword()))
                    .phone(input.getPhone())
                    .gender(input.getGender())
                    .roles(roles)
                    .build();
            return userRepository.save(user);
        }
    }

    @Override
    public LoginResponse authenticateUserAndGetLoginResponse(LoginUserDto loginUserDto){
        return authenticationService.authenticateUserAndGetLoginResponse(loginUserDto);
    }

    @Override
    public User getAuthenticatedUser() {
        return (User) authenticationService.getAuthentication().getPrincipal();
    }

    @Override
//    @Cacheable(value = "sellers")
    public List<User> findAllSellers() {
        Optional<Role> roleOptional = roleRepository.findByName(RoleEnum.SELLER);
        if (roleOptional.isPresent()) {
            return userRepository.findByRolesContaining(roleOptional.get().getName());
        } else {
            return Collections.emptyList();
        }
    }

    private Page<User> convertListToPage(List<User> sellers, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<User> pagedSellers;

        if (sellers.size() < startItem) {
            pagedSellers = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, sellers.size());
            pagedSellers = sellers.subList(startItem, toIndex);
        }

        return new PageImpl<>(pagedSellers, pageable, sellers.size());
    }

    // From controller
    @Override
    public Page<User> allSellers(Pageable pageable) {
        List<User> sellers = findAllSellers();
        return convertListToPage(sellers, pageable);
    }

    @Override
    public Page<User> allUsers(Pageable pageable) {
        Optional<Role> roleOptional = roleRepository.findByName(RoleEnum.USER);
        if (roleOptional.isPresent()) {
            return userRepository.findByRolesContaining(roleOptional.get(), pageable);
        } else {
            return Page.empty();
        }
    }

    @Override
    public User getUserDetailsFromToken(String token){
        String username = jwtService.extractUsername(token);
        Optional<User> user;
        user = userRepository.findByEmail(username);
        return user.orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    @Override
    public User getUserDetailsFromRefreshToken(String refreshToken){
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        return getUserDetailsFromToken(existingToken.getToken());
    }

    @Override
    public User viewDetailsOfAnUser(Long id){
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new DataNotFoundException("There is no user with this id");
        } else {
            return user.get();
        }
    }

    @Override
    public User updateUserInfo(UserDTO userDTO){
        Optional<User> optionalUser = userRepository.findUserByEmail(userDTO.getEmail());

        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("There is no user with this email");
        }

        User user = optionalUser.get();
        user.setFullName(userDTO.getFullName());
        user.setPhone(userDTO.getPhone());
        user.setGender(userDTO.getGender());
        return userRepository.save(user);
    }

    @Override
    public String signUpWithOtp(RegisterUserDto registerUserDto) {
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        if (optionalRole.isEmpty()) { return null; }

        Role userRole = optionalRole.get();

        Optional<User> optionalUser = userRepository.findByEmail(registerUserDto.getEmail());
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            Set<Role> existingRoles = existingUser.getRoles();
            if (existingRoles.contains(userRole)) {
                throw new UserAlreadyExistedException(registerUserDto.getEmail());
            } else {
                existingRoles.add(userRole);
                existingUser.setRoles(existingRoles);
//                sendMail(input.getEmail());
                Integer otp = redisOtpService.generateAndSaveOtp(registerUserDto.getEmail());
                String roleNames = String.valueOf(existingRoles.stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toList()));
                MailBody mailBody = MailBody.builder()
                        .to(registerUserDto.getEmail())
                        .text("You are already an " + roleNames + " in our system. This is the OTP for your request: " + otp)
                        .build();
                emailService.sendSimpleMessage(mailBody);
            }
        } else {
            Set<Role> roles = new HashSet<>(List.of(optionalRole.get()));
            var user = User.builder()
                    .fullName(registerUserDto.getFullName())
                    .email(registerUserDto.getEmail())
                    .password(passwordEncoder.encode(registerUserDto.getPassword()))
                    .phone(registerUserDto.getPhone())
                    .gender(registerUserDto.getGender())
                    .roles(roles)
                    .build();
        }
        return null;
    }

    @Override
    @Transactional
    public User signUpNewVersion(RegisterUserDto registerUserDto){
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        if (optionalRole.isEmpty()) { return null; }

        Role userRole = optionalRole.get();
        Optional<User> optionalUser = userRepository.findByEmail(registerUserDto.getEmail());

        if (optionalUser.isPresent()) {
            // If user is already existing
            User existingUser = optionalUser.get();
            Set<Role> existingRoles = existingUser.getRoles();

            if (existingRoles.contains(userRole) && existingUser.isEnabled()) {
                throw new UserAlreadyExistedException(registerUserDto.getEmail());
            } else if (existingUser.getEmail().equals(registerUserDto.getEmail()) && !existingUser.isEnabled()) {
                existingUser.setFullName(registerUserDto.getFullName());
                existingUser.setPassword(registerUserDto.getPassword());
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

                Integer otp = redisOtpService.generateAndSaveOtp(registerUserDto.getEmail());
                MailBody mailBody = MailBody.builder()
                        .to(registerUserDto.getEmail())
                        .text("This is the OTP for your request: " + otp)
                        .build();
                emailService.sendSimpleMessage(mailBody);
                return existingUser;
            }
        } else {
            Set<Role> roles = new HashSet<>(List.of(optionalRole.get()));
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

            Integer otp = redisOtpService.generateAndSaveOtp(registerUserDto.getEmail());
            MailBody mailBody = MailBody.builder()
                    .to(registerUserDto.getEmail())
                    .text("This is the OTP for your request: " + otp)
                    .build();
            emailService.sendSimpleMessage(mailBody);

            return user;
        }
        return null;
    }

    private boolean isSameAddress(Address existingAddress, RegisterUserDto registerUserDto) {
        return Objects.equals(existingAddress.getCountry(), registerUserDto.getCountry())
                && Objects.equals(existingAddress.getProvince(), registerUserDto.getProvince())
                && Objects.equals(existingAddress.getDistrict(), registerUserDto.getDistrict())
                && Objects.equals(existingAddress.getCommune(), registerUserDto.getCommune())
                && Objects.equals(existingAddress.getAddressDetail(), registerUserDto.getAddressDetail());
    }

    @Override
    public void activateUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setStatus(true);
            userRepository.save(user);
        }
    }

    @Override
    public UserProfileDTO getUserProfile() {
        User user = getAuthenticatedUser();
        Address address = addressRepository.findById(user.getAddressId()).orElse(null);
        return UserProfileDTO.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .gender(user.getGender())
                .country(address.getCountry())
                .province(address.getProvince())
                .district(address.getDistrict())
                .commune(address.getCommune())
                .addressDetail(address.getAddressDetail())
                .build();
//        return userProfileMapper.toDTO(user);
    }

    @Override
    @Transactional
    public User updateUserProfile(UserProfileDTO userProfileDTO) {
        User currentUser = getAuthenticatedUser();
        currentUser.setFullName(userProfileDTO.getFullName());
        currentUser.setPhone(userProfileDTO.getPhone());
        currentUser.setGender(userProfileDTO.getGender());

        Address address = Address.builder()
                .country(userProfileDTO.getCountry())
                .province(userProfileDTO.getProvince())
                .district(userProfileDTO.getDistrict())
                .commune(userProfileDTO.getCommune())
                .addressDetail(userProfileDTO.getAddressDetail())
                .build();
        addressRepository.save(address);
        currentUser.setAddressId(address.getId());
        return userRepository.save(currentUser);
    }

    @Override
    public User sendMail(String email) {
        Set<Role> existingRoles = getAuthenticatedUser().getRoles();
        if (!existingRoles.contains(Role.builder().name(RoleEnum.SELLER).build())) {
            Integer otp = redisOtpService.generateAndSaveOtp(email);
            MailBody mailBody = MailBody.builder()
                    .to(email)
                    .text("This is the OTP for your request: " + otp)
                    .build();
            emailService.sendSimpleMessage(mailBody);
        } else {
            throw new SellerAlreadyExistedException("You are already a seller");
        }
        return getAuthenticatedUser();
    }

    @Override
    public void addSellerRole(String email) {
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.SELLER);
        if (optionalRole.isEmpty()) { return; }
        Role sellerRole = optionalRole.get();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            Set<Role> existingRoles = existingUser.getRoles();
            existingRoles.add(sellerRole);
            existingUser.setRoles(existingRoles);
            userRepository.save(existingUser);
        }
    }

    @Override
    public String sendOtpForForgotPasswordRequest(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("There is no user with this email");
        }
//        User existingUser = optionalUser.get();
        Integer otp = redisOtpService.generateAndSaveOtp(email);
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your forgot password request: " + otp
                + ". Please make sure to use this OTP to reset your password."
                + "The OTP will be expired in 5 minutes. You can resend the OTP after 1 minute.")
                .build();
        emailService.sendSimpleMessage(mailBody);
        if (otp == null) {
            return "OTP request rate limit exceeded. Please wait before trying again.";
        } else {
            return "OTP has been sent to your email. Please check your email.";
        }
    }


    // Better approach
    @Override
    public void sendMailForSignUpUser(RegisterUserDto registerUserDto) {
//        Optional<User> existingUser = userRepository.findByEmail(registerUserDto.getEmail());
//        User userToAddUserRole = existingUser.orElseGet(User::new);

        Integer otp = redisOtpService.generateAndSaveOtp(registerUserDto.getEmail());
        MailBody mailBody = MailBody.builder()
                .to(registerUserDto.getEmail())
                .text("Hi! Please use this OTP for user signing up request: " + otp)
                .build();
        emailService.sendSimpleMessage(mailBody);
        // I think there are some problems with this process
        redisOtpService.storeTemporaryUser(registerUserDto);
    }

    @Override
    public void checkUserExistence(RegisterUserDto registerUserDto) {
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        if (optionalRole.isEmpty()) { return; }
        Role userRole = optionalRole.get();

        Optional<User> optionalUser = userRepository.findByEmail(registerUserDto.getEmail());
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            Set<Role> existingRoles = existingUser.getRoles();
            if (existingRoles.contains(userRole)) {
                throw new UserAlreadyExistedException(registerUserDto.getEmail());
            }
        }
    }
}
