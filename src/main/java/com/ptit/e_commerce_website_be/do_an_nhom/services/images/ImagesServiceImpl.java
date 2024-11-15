package com.ptit.e_commerce_website_be.do_an_nhom.services.images;


import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Image;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CloudinaryResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ImagesRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImagesServiceImpl implements ImagesService{
    private  final ImagesRepository imagesRepository;
    private  final ProductRepository productsRepository;
    @Override
    public void addImageProduct(CloudinaryResponse cloudinaryResponse, Long productId){
        Image image = Image.builder()
                .productId(productId)
                .link(cloudinaryResponse.getUrl())
                .name(cloudinaryResponse.getPublicId())
                .build();
        imagesRepository.save(image);
    }

    @Override
    @Transactional
    public void addImageTextProduct(String img, Long id){
        imagesRepository.deleteByLink(img);
        Image image = Image.builder()
                .link(img)
                .productId(id)
                .build();
        imagesRepository.save(image);
    }

    @Override
    public void insertNotDelete(String img, Long id){
        Image image = Image.builder()
                .link(img)
                .productId(id)
                .build();
        imagesRepository.save(image);
    }
}

