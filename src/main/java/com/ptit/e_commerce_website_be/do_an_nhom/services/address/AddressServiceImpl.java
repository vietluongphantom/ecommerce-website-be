package com.ptit.e_commerce_website_be.do_an_nhom.services.address;

import com.ptit.e_commerce_website_be.do_an_nhom.repositories.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    public boolean isUserInAddress(Long userId) {
        return addressRepository.existsByUserId(userId);
    }
}

