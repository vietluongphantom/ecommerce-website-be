package com.ptit.e_commerce_website_be.do_an_nhom.models.entities;


import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "error_message")
public class ErrorMessage {

    private HttpStatus status;

    private String message;

}
