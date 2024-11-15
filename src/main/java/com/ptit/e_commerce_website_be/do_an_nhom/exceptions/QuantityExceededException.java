package com.ptit.e_commerce_website_be.do_an_nhom.exceptions;

public class QuantityExceededException extends RuntimeException{
    public QuantityExceededException(String message) {super(message);}
}
