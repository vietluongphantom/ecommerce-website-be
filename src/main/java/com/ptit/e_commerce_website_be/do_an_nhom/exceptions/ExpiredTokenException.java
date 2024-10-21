package com.ptit.e_commerce_website_be.do_an_nhom.exceptions;

public class ExpiredTokenException extends RuntimeException{
    public ExpiredTokenException(String message){super(message);}
}
