package com.ptit.e_commerce_website_be.do_an_nhom.services.rate;

import java.math.BigDecimal;

public interface RateService {

    void updateRate(Long productId);

    BigDecimal getAverageStarsByProductId(Long productId) ;
}
