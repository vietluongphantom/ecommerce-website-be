package com.ptit.e_commerce_website_be.do_an_nhom.exceptions;

public class UserAlreadyExistedException extends RuntimeException {

    public UserAlreadyExistedException(String email) {
        super("User with email " + email + " already existed.");
    }

}

