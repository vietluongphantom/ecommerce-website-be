package com.ptit.e_commerce_website_be.do_an_nhom.services.images;

import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CloudinaryResponse;

public interface ImagesService {
    void addImageProduct(CloudinaryResponse cloudinaryResponse, Long productId);
    void  addImageTextProduct(String text, Long id);
    void insertNotDelete (String text, Long id);
}
