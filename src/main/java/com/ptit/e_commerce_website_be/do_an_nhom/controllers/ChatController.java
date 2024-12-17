package com.ptit.e_commerce_website_be.do_an_nhom.controllers;


import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ChatMessage;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ChatRoom;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.ChatNotification;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.ShopResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.UserRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.services.chatMessage.ChatMessageService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.chatRoom.ChatRoomService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        chatMessage.setSenderId(user.getId());
        ChatMessage savedMsg = chatMessageService.save(chatMessage);
        User userNew = userRepository.findById(chatMessage.getRecipientId()).get();
        messagingTemplate.convertAndSendToUser(
                userNew.getEmail(), "/queue/messages",
                new ChatNotification(
                        savedMsg.getId(),
                        savedMsg.getSenderId(),
                        savedMsg.getRecipientId(),
                        savedMsg.getContent()
                )
        );
    }

    @GetMapping("/messages/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(
                                                              @PathVariable Long recipientId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(user.getId(), recipientId));
    }

    @GetMapping("/list-messages")
    public ResponseEntity<List<ShopResponse>> getListMessages(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return  ResponseEntity
                .ok(chatRoomService.getListShop(user.getId()));
    }
}

