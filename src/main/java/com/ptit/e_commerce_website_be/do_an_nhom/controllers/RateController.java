package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

//import com.ptit.e_commerce_website_be.do_an_nhom.models.responses.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.services.rate.RateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rates")
public class RateController {

    private final RateService rateService;

    @GetMapping("/product/{productId}/average-stars")
    public CommonResult<BigDecimal> getAverageStarsByProductId(@PathVariable("productId") Long productId) {
        try {
            BigDecimal averageStars = rateService.getAverageStarsByProductId(productId);
            return CommonResult.success(averageStars, "Average stars retrieved successfully");
        } catch (Exception e) {
            return CommonResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while retrieving average stars");
        }
    }
}
