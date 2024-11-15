package com.ptit.e_commerce_website_be.do_an_nhom.exceptions;


import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ErrorMessage;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception) {
        ProblemDetail problemDetail = null;

        if (exception instanceof BadCredentialsException) {
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            problemDetail.setProperty("description", "The username or password is incorrect");
            return problemDetail;
        }

        if (exception instanceof AccountStatusException) {
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            problemDetail.setProperty("description", "The account is locked");
        }

        if (exception instanceof AccessDeniedException) {
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            problemDetail.setProperty("description", "You are not authorized to access this resource");
        }

        if (exception instanceof SignatureException) {
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            problemDetail.setProperty("description", "The JWT token has expired");
        }

        if (problemDetail == null) {
            problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
            problemDetail.setProperty("description", "Unknown internal server error");
        }

        return problemDetail;
    }

    @ExceptionHandler(SellerAlreadyExistedException.class)
    public ResponseEntity<ErrorMessage> sellerAlreadyExistedException(SellerAlreadyExistedException exception, WebRequest request) {
        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(UserAlreadyExistedException.class)
    public ResponseEntity<ErrorMessage> userAlreadyExistedException(UserAlreadyExistedException exception, WebRequest request) {
        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult handleResourceNotFoundException(DataNotFoundException exception) {
        return CommonResult.validateFailed(exception.getMessage());
    }

//    @ExceptionHandler(OrderNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public CommonResult handleResourceNotFoundException(OrderNotFoundException exception) {
//        return CommonResult.validateFailed(exception.getMessage());
//    }
    @ExceptionHandler(QuantityExceededException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult handleQuantityExceededException(QuantityExceededException exception) {
        return CommonResult.validateFailed(exception.getMessage());
    }

    @ExceptionHandler(AlreadyExistedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonResult handleAlreadyExistedException(AlreadyExistedException exception) {
        return CommonResult.validateFailed(exception.getMessage());
    }
}

