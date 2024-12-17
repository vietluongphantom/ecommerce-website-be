package com.ptit.e_commerce_website_be.do_an_nhom.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;



@Entity
@Table(name = "chat_message")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String chatId;
    private Long senderId;
    private Long recipientId;
    private String content;
    private Date timestamp;
}

