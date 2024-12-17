package com.ptit.e_commerce_website_be.do_an_nhom.services.chatRoom;


import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ChatRoom;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Shop;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.ShopResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ChatRoomService {
    public Optional<String> getChatRoomId(
            Long senderId,
            Long recipientId,
            boolean createNewRoomIfNotExists
    );

    public String createChatId(Long senderId, Long recipientId);

    public List<ShopResponse> getListShop(Long id);
}

