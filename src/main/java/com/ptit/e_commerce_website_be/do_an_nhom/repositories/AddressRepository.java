package com.ptit.e_commerce_website_be.do_an_nhom.repositories;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
//    Optional<List<Address>>  findByUserId(Long userId) throws Exception;
    Optional<Address> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
