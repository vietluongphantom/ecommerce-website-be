package com.ghtk.ecommercewebsite.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ghtk.ecommercewebsite.models.entities.User;
import com.ghtk.ecommercewebsite.models.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleEnum name;

    @Column(nullable = false)
    private String description;

    @ManyToMany(targetEntity = User.class, mappedBy = "roles",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JsonBackReference
//    @JsonIgnore
    private Set<User> users;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

}
