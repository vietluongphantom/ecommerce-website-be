package com.ptit.e_commerce_website_be.do_an_nhom.services.user;

import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.UserAlreadyExistedException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.LoginUserDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.RegisterUserDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.UserDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.UserProfileDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.LoginResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface UserService {
    User signUp(RegisterUserDto input);
    LoginResponse authenticateUserAndGetLoginResponse(LoginUserDto loginUserDto);
    User getAuthenticatedUser();
    Page<User> allUsers(Pageable pageable);
    List<User> findAllSellers();
    List<User> findAllUsers();
    Page<User> allSellers(Pageable pageable);
    User getUserDetailsFromToken(String token);
    User getUserDetailsFromRefreshToken(String refreshToken);
    User viewDetailsOfAnUser(Long id);

    User updateUserInfo(UserDTO userDTO);

    String signUpWithOtp(RegisterUserDto registerUserDto);

    void checkUserExistence(RegisterUserDto registerUserDto);
    void sendMailForSignUpUser(RegisterUserDto registerUserDto);

    User signUpNewVersion(RegisterUserDto registerUserDto);

    void activateUser(String email);

    UserProfileDTO getUserProfile();

    User updateUserProfile(UserProfileDTO userProfileDTO);

    User sendMail(String email);

    void addSellerRole(String email);

    String sendOtpForForgotPasswordRequest(String email);

    void updateUserStatus(Long userId, Boolean status);

    User getInforUser(Long id);
}
