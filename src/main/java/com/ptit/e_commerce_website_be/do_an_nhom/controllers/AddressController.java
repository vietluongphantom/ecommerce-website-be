package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.services.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AddressController {

    @Autowired
    private OrdersService ordersService;

    @GetMapping("/address-detail/count")
    public ResponseEntity<Map<String, Integer>> getCountOfProvinces(@RequestParam("shopId") Long shopId) {
        List<String> addressDetails = ordersService.getAddressDetailsByShopId(shopId);
        Map<String, Integer> provinceCount = countProvinces(addressDetails);
        return ResponseEntity.ok(provinceCount);
    }

    /**
     * Hàm nhận diện và tính toán số lượng tỉnh thành trong danh sách địa chỉ.
     */
    private Map<String, Integer> countProvinces(List<String> addressDetails) {
        // Danh sách các tỉnh thành của Việt Nam
        List<String> provinces = Arrays.asList(
                "Ha Noi", "Ho Chi Minh", "Hai Phong", "Can Tho", "Da Nang", "Binh Duong",
                "Dong Nai", "Quang Ninh", "Kien Giang", "Khanh Hoa", "Nghe An", "Hai Duong",
                "Ha Tinh", "Thanh Hoa", "Soc Trang", "Ben Tre", "Binh Dinh", "Dak Lak",
                "Dak Nong", "Lai Chau", "Lam Dong", "Lang Son", "Lao Cai", "Quang Nam",
                "Quang Tri", "Quang Binh", "Ninh Binh", "Ninh Thuan", "Gia Lai", "Phu Tho",
                "Tien Giang", "Tra Vinh", "Bac Ninh", "Bac Giang", "Yen Bai", "Hoa Binh"
        );

        // Tạo bản đồ đếm
        Map<String, Integer> provinceCountMap = new HashMap<>();

        for (String address : addressDetails) {
            for (String province : provinces) {
                if (address.contains(province)) { // Kiểm tra địa chỉ chứa tỉnh thành
                    provinceCountMap.put(province, provinceCountMap.getOrDefault(province, 0) + 1);
                }
            }
        }

        return provinceCountMap;
    }
}
