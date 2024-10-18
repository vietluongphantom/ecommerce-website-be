package com.ghtk.ecommercewebsite.models.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "blacklist_token")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlacklistToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", length = 255)
    private String token;

    @Column(name="token_type", length = 50)
    private String tokenType;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
