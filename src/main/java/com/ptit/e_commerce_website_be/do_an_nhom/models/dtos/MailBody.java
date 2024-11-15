package com.ptit.e_commerce_website_be.do_an_nhom.models.dtos;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String text) {
}
