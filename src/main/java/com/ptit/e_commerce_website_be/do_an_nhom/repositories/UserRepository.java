package com.ptit.e_commerce_website_be.do_an_nhom.repositories;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Role;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.enums.RoleEnum;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findById(Long userId);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.email = :email AND r.name = 'USER'")
    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRolesContaining(@Param("roleName") RoleEnum roleName);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = ?2 WHERE u.email = ?1")
    void updatePassword(String email, String password);

    Page<User> findByRolesContaining(Role role, Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findAllByRoleName(@Param("roleName") String roleName);
}
