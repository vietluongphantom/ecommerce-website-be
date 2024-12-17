package com.ptit.e_commerce_website_be.do_an_nhom.services.chatMessage;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ChatMessage;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;

import java.util.ArrayList;
import java.util.List;

public interface ChatMessageService {
    public ChatMessage save(ChatMessage chatMessage);

    public List<ChatMessage> findChatMessages(Long senderId, Long recipientId);
}
