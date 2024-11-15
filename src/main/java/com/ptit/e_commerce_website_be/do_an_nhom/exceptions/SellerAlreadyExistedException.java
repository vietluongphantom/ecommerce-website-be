package com.ptit.e_commerce_website_be.do_an_nhom.exceptions;

public class SellerAlreadyExistedException extends RuntimeException {

    public SellerAlreadyExistedException(String email) {
        super("Seller with email " + email + " already existed.");
    }

}
