package com.ptit.e_commerce_website_be.do_an_nhom.services.user;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.UserAlreadyExistedException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.LoginUserDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.RegisterUserDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Role;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.RoleEnum;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.LoginResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.RoleRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.UserRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.services.JwtService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

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
            if (existingRoles.contains(userRole)) {
                throw new UserAlreadyExistedException(input.getEmail());
            } else {
                existingRoles.add(userRole);
                existingUser.setRoles(existingRoles);
                return userRepository.save(existingUser);
            }
        } else {
            Set<Role> roles = new HashSet<>(List.of(optionalRole.get()));
            var user = User.builder()
                    .fullName(input.getFullName())
                    .email(input.getEmail())
                    .status(Boolean.TRUE)
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
    public User getUserDetailsFromToken(String token){
        String username = jwtService.extractUsername(token);
        Optional<User> user;
        user = userRepository.findByEmail(username);
        return user.orElseThrow(() -> new DataNotFoundException("User not found"));
    }
}
