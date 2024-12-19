package com.ptit.e_commerce_website_be.do_an_nhom.controllers;
import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.UserProfileDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.RefreshTokenDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Token;
import com.ptit.e_commerce_website_be.do_an_nhom.services.OtpService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.RedisOtpService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.token.TokenService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.UserAlreadyExistedException;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.LoginUserDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.RegisterUserDto;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final TokenService tokenService;
    private final RedisOtpService redisOtpService;
    private final OtpService otpService;

    @PostMapping("/signup")
    public CommonResult<User> signup(@Valid @RequestBody RegisterUserDto registerUserDto){
        User user = userService.signUp(registerUserDto);
        return CommonResult.success(user);
    }

    // New version start from here
    @PostMapping("/signUpNewVersion")
    public CommonResult<User> verifyOtpForSigningUp(@Valid @RequestBody RegisterUserDto registerUserDto) {
        return CommonResult.success(userService.signUpNewVersion(registerUserDto));
    }
    // New version here
    @PostMapping("/verifyOtp")
    public CommonResult<String> verifyOtp(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        Integer otp = Integer.parseInt(requestBody.get("otp"));
        boolean isOtpValid = redisOtpService.verifyOtp(email, otp);
        if (!isOtpValid) { return CommonResult.failed("Invalid OTP"); }
        userService.activateUser(email);
        return CommonResult.success("User activated successfully");
    }
    // New version here
    @PostMapping("/resendOtp")
    public CommonResult<String> resendOtp(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        return otpService.resendOtpForSigningUp(email);
    }

    @PostMapping("/login")
    public CommonResult<LoginResponse> authenticate(@Valid @RequestBody LoginUserDto loginUserDto) {
        String token = userService.authenticateUserAndGetLoginResponse(loginUserDto).getToken();
        User userDetail = userService.getUserDetailsFromToken(token);
        Token jwtToken = tokenService.addToken(userDetail, token);

        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .build();
        return CommonResult.success(loginResponse);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public CommonResult<User> authenticatedUser() {
        return CommonResult.success(userService.getAuthenticatedUser());
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public CommonResult<UserProfileDTO> showUserProfile() {
        return CommonResult.success(userService.getUserProfile());
    }

    @PostMapping("/updateProfile")
    @PreAuthorize("hasRole('USER')")
    public CommonResult<User> updateUserProfile(@Valid @RequestBody UserProfileDTO userProfileDTO) {
        return CommonResult.success(userService.updateUserProfile(userProfileDTO));
    }

    @GetMapping("/registerAsSellerInHomePage")
    @PreAuthorize("hasRole('USER')")
    public CommonResult<User> registerAsSellerInHomePage() {
        User user = userService.getAuthenticatedUser();
        return CommonResult.success(userService.sendMail(user.getEmail()));
    }

    @PostMapping("/verifyOtpForAddingSellerRole")
    public CommonResult<String> verifyOtpForAddingSellerRole(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        Integer otp = Integer.parseInt(requestBody.get("otp"));
        boolean isOtpValid = redisOtpService.verifyOtp(email, otp);
        if (!isOtpValid) { return CommonResult.failed("Invalid OTP"); }
        userService.addSellerRole(email);
        return CommonResult.success("User activated successfully");
    }

    @PostMapping("/resendOtpForAddingSellerRole")
    public CommonResult<String> resendOtpForAddingSellerRole(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        return otpService.resendOtpForSigningUp(email);
    }

    @PostMapping("/refreshToken")
    public CommonResult<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO
    ){

        User userDetail = userService.getUserDetailsFromRefreshToken(refreshTokenDTO.getRefreshToken());
        Token jwtToken = tokenService.refreshToken(refreshTokenDTO.getRefreshToken(), userDetail);
        LoginResponse loginResponse = LoginResponse.builder()
                .message("Refresh token successfully")
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .build();
        return CommonResult.success(loginResponse);
    }

    @GetMapping("/sellers")
//    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<List<User>> getAllSellers() {
        List<User> sellers = userService.findAllSellers();
        return CommonResult.success(sellers, "Get all sellers successfully");
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return CommonResult.success(users, "Get all users successfully");
    }


    @PutMapping("/updateUserStatus")
    public CommonResult<Object> updateUserStatus(
            @RequestBody Map<String, Object> requestBody
    ) {
        try {
            Long userId = Long.valueOf(requestBody.get("userId").toString());
            Boolean status = Boolean.valueOf(requestBody.get("status").toString());

            userService.updateUserStatus(userId, status);
            return CommonResult.success("User status updated successfully");
        } catch (Exception e) {
            log.error("Failed to update user status: {}", e.getMessage(), e);
            return CommonResult.forbidden("Failed to update user status: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public CommonResult<User> getAllUsers( @PathVariable("id") Long id) {
        userService.getInforUser(id);
        return CommonResult.success(userService.getInforUser(id), "Get user info successfully");
    }

//    @PutMapping("/{id}/status")
//    public ResponseEntity<?> updateUserStatus(
//            @PathVariable Long id,
//            @RequestParam Boolean status) {
//        try {
//            // Gọi hàm service để cập nhật trạng thái user
//            User updatedUser = userService.updateUserStatus(id, status);
//
//            // Trả về kết quả nếu cập nhật thành công
//            return ResponseEntity.ok(updatedUser);
//        } catch (Exception e) {
//            // Xử lý lỗi
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//    }

}