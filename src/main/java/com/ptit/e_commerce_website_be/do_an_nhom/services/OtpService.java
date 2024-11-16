package com.ptit.e_commerce_website_be.do_an_nhom.services;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.MailBody;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request.ChangePasswordDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ForgotPassword;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ForgotPasswordRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.UserRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.utils.ChangePassword;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisOtpService redisOtpService;

    // New version
    public CommonResult<String> verifyEmailAndSendOtpNewVersion(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email!"));

        int otp = redisOtpService.generateAndSaveOtp(email);
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your Forgot Password request: " + otp)
                .subject("OTP for Forgot Password request")
                .build();
        emailService.sendSimpleMessage(mailBody);
        // Return the email for the verification step
        return CommonResult.success(email, "Email sent for verification!");
    }

    public ResponseEntity<String> verifyEmailAndSendOtp(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email!"));

//        int otp = otpGenerator();
        int otp = redisOtpService.generateAndSaveOtp(email);
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your Forgot Password request: " + otp)
                .subject("OTP for Forgot Password request")
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 70 * 1000))
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);
        return ResponseEntity.ok("Email sent for verification!");
    }

    public ResponseEntity<String> verifyOtp(Integer otp, String email) {
        boolean isValid = redisOtpService.verifyOtp(email, otp);
        if (isValid) {
            return ResponseEntity.ok("OTP verified!");
        }
        return new ResponseEntity<>("Invalid or expired OTP!", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> changePassword(ChangePassword changePassword, String email) {
        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {
            return new ResponseEntity<>("Please enter the password again!", HttpStatus.EXPECTATION_FAILED);
        }
        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encodedPassword);

        return ResponseEntity.ok("Password has been changed!");
    }

    public ResponseEntity<String> resendOtp(String email) {
        try {
            int otp = redisOtpService.generateAndSaveOtp(email);

            MailBody mailBody = MailBody.builder()
                    .to(email)
                    .text("This is the OTP for your request: " + otp)
                    .subject("OTP for Forgot Password request")
                    .build();

            emailService.sendSimpleMessage(mailBody);
            return ResponseEntity.ok("OTP has been resent!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many OTP requests. Please wait before trying again.");
        }
    }

    public CommonResult<String> resendOtpForSigningUp(String email) {
        try {
            int otp = redisOtpService.generateAndSaveOtp(email);

            MailBody mailBody = MailBody.builder()
                    .to(email)
                    .text("This is the OTP for your signing up request: " + otp)
                    .subject("OTP for signing up request")
                    .build();

            emailService.sendSimpleMessage(mailBody);
            return CommonResult.success("OTP has been resent!");
        } catch (RuntimeException e) {
            return CommonResult.tooManyRequests("Too many OTP requests. Please wait before trying again.");
        }
    }

    public CommonResult<String> resendOtpForForgotPassword(String email) {
        try {
            int otp = redisOtpService.generateAndSaveOtp(email);

            MailBody mailBody = MailBody.builder()
                    .to(email)
                    .text("This is the OTP for your Forgot Password request: " + otp)
                    .subject("OTP for Forgot Password request")
                    .build();

            emailService.sendSimpleMessage(mailBody);
            return CommonResult.success("OTP has been resent!");
        } catch (RuntimeException e) {
            return CommonResult.tooManyRequests("Too many OTP requests. Please wait before trying again.");
        }
    }

    public CommonResult<String> changePasswordNewVersion(ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findByEmail(changePasswordDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email!"));
//        String encodedOldPassword = passwordEncoder.encode(changePasswordDTO.getOldPassword());
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            return CommonResult.failed("Invalid old password!");
        }

        if (!Objects.equals(changePasswordDTO.getNewPassword(), changePasswordDTO.getConfirmPassword())) {
            return CommonResult.failed("Please enter the password again!");
        }
        String encodedPassword = passwordEncoder.encode(changePasswordDTO.getNewPassword());
        userRepository.updatePassword(user.getEmail(), encodedPassword);

        return CommonResult.success("Password has been changed!");
    }

    public CommonResult<String> changePasswordForgotPasswordVersion(String email, String password, String confirmPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email!"));
        if (!Objects.equals(password, confirmPassword)) {
            return CommonResult.failed("Please enter the password again!");
        }
        String encodedPassword = passwordEncoder.encode(password);
        userRepository.updatePassword(user.getEmail(), encodedPassword);

        return CommonResult.success("Password has been changed!");
    }
}

