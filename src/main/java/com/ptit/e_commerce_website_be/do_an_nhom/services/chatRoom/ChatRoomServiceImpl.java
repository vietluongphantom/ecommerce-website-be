package com.ptit.e_commerce_website_be.do_an_nhom.services.chatRoom;


import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ChatRoom;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.ShopResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ChatRoomRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ShopRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    private final ShopRepository shopRepository;

    private final UserRepository userRepository;
    public Optional<String> getChatRoomId(
            Long senderId,
            Long recipientId,
            boolean createNewRoomIfNotExists
    ) {
//        Optional<ChatRoom> c
        return chatRoomRepository
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if(!createNewRoomIfNotExists) {
                        var chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }
                    return  Optional.empty();
                });
    }

    public String createChatId(Long senderId, Long recipientId) {
        String chatId;
        if(senderId > recipientId){
            chatId = String.format("%s_%s", senderId, recipientId);
        }else {
            chatId = String.format("%s_%s", recipientId, senderId);
        }
        ChatRoom senderRecipient = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        ChatRoom recipientSender = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();

        chatRoomRepository.save(senderRecipient);
//        chatRoomRepository.save(recipientSender);

        return chatId;
    }

//    @Override
//    public List<User> getListShop(Long userId) {
//        List<ChatRoom> chatRoomList = chatRoomRepository.findBySenderId(userId);
//        List<User> userList = new ArrayList<>();
//        for(ChatRoom chatRoom : chatRoomList){
//            userList.add();
//        }
//        return userList;
//    }


    @Override
    public List<ShopResponse> getListShop(Long userId) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findBySenderId(userId);
        List<ShopResponse> shopList = new ArrayList<>();
        for(ChatRoom chatRoom : chatRoomList) {
            ShopResponse shopResponse = ShopResponse.builder()
                    .user(userRepository.findById(chatRoom.getRecipientId()).get())
                    .shop(shopRepository.findByUserId(chatRoom.getRecipientId()))
                    .build();
            shopList.add(shopResponse);
        }
        return shopList;
    }
}

