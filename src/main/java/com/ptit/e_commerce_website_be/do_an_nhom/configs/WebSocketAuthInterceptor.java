package com.ptit.e_commerce_website_be.do_an_nhom.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, org.springframework.messaging.MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // Lấy thông tin từ simpUser
        if (accessor.getUser() instanceof UsernamePasswordAuthenticationToken authentication) {
            // Tạo một SecurityContext mới và lưu Authentication
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }


        return message;
    }

    private boolean validateToken(String token) {
        // Thêm logic xác thực token tại đây
        return token.startsWith("Bearer ");
    }

    private Object getUserFromToken(String token) {
        // Parse thông tin user từ token
        return token.substring(7); // Giả định token lưu user ID trong token
    }
}

