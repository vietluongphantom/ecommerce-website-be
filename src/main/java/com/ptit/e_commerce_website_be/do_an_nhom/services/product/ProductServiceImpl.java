package com.ptit.e_commerce_website_be.do_an_nhom.services.product;


import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.ProductMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.ProductDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.*;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.ProductResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.*;
import com.ptit.e_commerce_website_be.do_an_nhom.services.CloudinaryService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.images.ImagesService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.productitem.ProductItemServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {


    private final CloudinaryService cloudinaryService;
    private final ProductRepository productsRepository;
    private final ProductItemServiceImpl productItemService;
    private final ProductItemRepository productItemRepository;
    private final ProductMapper productMapper;
    private final ImagesService imagesService;
    private final CategoryProductRepository categoryProductRepository ;
    private final ShopRepository shopRepository;
    private final BrandRepository brandRepository;
    private final RateRepository rateRepository;
    private final ImagesRepository imagesRepository;
    private final ProductAttributesRepository productAttributesRepository;
    private final AttributeValuesRepository attributeValuesRepository;

    public List<Product> findAll() {
        return productsRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productsRepository.findById(id);
    }


    @Transactional
    public Product save(ProductDTO productDTO, Long userId){
        Shop shop = shopRepository.findByUserId(userId);
        productDTO.setShopId(shop.getId());
        productDTO.setIsDelete(Boolean.FALSE);
        Product product = productsRepository.save(productMapper.toEntity(productDTO));

        for (int i = 0; i < productDTO.getCategoryIds().size(); i++){
            ProductCategory productCategory = ProductCategory.builder()
                    .categoryId(productDTO.getCategoryIds().get(i))
                    .productId(product.getId())
                    .build();
            categoryProductRepository.save(productCategory);
        }

        for (String file: productDTO.getImages()){
            imagesService.addImageTextProduct(file, product.getId());
        }
        return product;
    }


    @Transactional
    public void deleteById(Long id){
        Product product = productsRepository.findById(id)
                .orElseThrow(()->  new DataNotFoundException("Cannot find product by id"));
        product.setIsDelete(Boolean.TRUE);

        productsRepository.save(product);
        productAttributesRepository.softDeleteProductAttributesByProductId(id);
        productItemRepository.softDeleteProductItemByProductId(id);
    }


    @Override
    public List<Product> findByBrandId(Long brandId) {
        return productsRepository.findByBrandId(brandId);
    }

    @Override
    public List<Product> searchProductsByName(String keyword) {
        return productsRepository.findByNameContaining(keyword);
    }

    @Override
    public List<Product> searchProductsByDes(String keyword) {
        return productsRepository.findByDescriptionContaining(keyword);
    }

    @Override
    public List<Product> getAllProducts(){
        return List.of();
    }

    @Override
    public Page<ProductResponse> searchProducts(
            List<Long> categoryIds,
            long categoryCount,
            List<Long> brandIds,
            String keyword,
            Long fromPrice,
            Long toPrice,
            Float rateStar,
            PageRequest pageRequest
    ) {
        return productsRepository.searchProducts(categoryIds, brandIds, keyword, fromPrice , toPrice, rateStar, pageRequest)
                .map(product -> {
                    List<String> categoryNames = product.getCategoryList().stream()
                            .map(Category::getName)
                            .collect(Collectors.toList());

                    Rate rate = rateRepository.findByProductId(product.getId());
                    // Xử lý Optional Brand để tránh NoSuchElementException
                    Optional<Brand> brand = brandRepository.findById(product.getBrandId());
                    List<String> imageList = imagesRepository.findLinkByProductId(product.getId());


                    return ProductResponse.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .description(product.getDescription())
                            .minPrice(product.getMinPrice())
                            .totalSold(product.getTotalSold())
                            .thumbnail(product.getThumbnail())
                            .averageRate(rate == null? BigDecimal.valueOf(0):rate.getAverageStars())
                            .brandId(brand.get().getId())
                            .brandName(brand.get().getName())
                            .quantityRate(rate==null?0:rate.getQuantity())
                            .images(imageList)
                            .categoryNames(categoryNames)
                            .categories(product.getCategoryList())
                            .createdAt(product.getCreatedAt())
                            .modifiedAt(product.getModifiedAt())
                            .build();
                });
    }

    @Override
    public Page<ProductResponse> searchProductsSeller(
            List<Long> categoryIds,
            long categoryCount,
            List<Long> brandIds,
            String keyword,
            Long userId,
            PageRequest pageRequest
    ){
        Shop shop = shopRepository.findByUserId(userId);
        if(shop == null){
            throw  new DataNotFoundException("shop not found by userID");
        }
        return productsRepository.searchProductsSeller(categoryIds, categoryCount, brandIds, keyword,shop.getId(), pageRequest)
                .map(product -> {
                    List<String> categoryNames = product.getCategoryList().stream()
                            .map(Category::getName)
                            .collect(Collectors.toList());

                    Rate rate = rateRepository.findByProductId(product.getId());
                    // Xử lý Optional Brand để tránh NoSuchElementException
                    Optional<Brand> brand = brandRepository.findById(product.getBrandId());
                    List<String> imageList = imagesRepository.findLinkByProductId(product.getId());

                    return ProductResponse.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .minPrice(product.getMinPrice())
                            .totalSold(product.getTotalSold())
                            .description(product.getDescription())
                            .thumbnail(product.getThumbnail())
                            .averageRate(rate == null? BigDecimal.valueOf(0):rate.getAverageStars())
                            .brandId(brand.get().getId())
                            .brandName(brand.get().getName())
                            .quantityRate(rate==null?0:rate.getQuantity())
                            .images(imageList)
                            .categoryNames(categoryNames)
                            .categories(product.getCategoryList())
                            .createdAt(product.getCreatedAt())
                            .modifiedAt(product.getModifiedAt())
                            .build();
                });
    }

    @Override
    @Transactional
    public Product updateProductById(Long id, ProductDTO productDTO, Long userId){
        Product product = productsRepository.findById(id)
                .orElseThrow(()-> new DataNotFoundException("Cannot find product"));

        Shop shop = shopRepository.findByUserId(userId);
        if(!product.getShopId().equals(shop.getId())){
            throw new AccessDeniedException("you do not have access");
        }

        productDTO.setShopId(shop.getId());
        productDTO.setId(product.getId());
        productDTO.setIsDelete(Boolean.FALSE);
        productDTO.setMinPrice(product.getMinPrice());

        imagesRepository.deleteByProductId(product.getId());
        productsRepository.save(productMapper.toEntity(productDTO));

        for (int i = 0; i < productDTO.getCategoryIds().size(); i++){
            ProductCategory productCategoryCheck = categoryProductRepository.findByProductIdAndCategoryId(product.getId(), productDTO.getCategoryIds().get(i));
            if(productCategoryCheck != null){
                continue;
            }
            ProductCategory productCategory = ProductCategory.builder()
                    .categoryId(productDTO.getCategoryIds().get(i))
                    .isDelete(Boolean.FALSE)
                    .productId(product.getId())
                    .build();
            categoryProductRepository.save(productCategory);
        }
        for (String file: productDTO.getImages()){
            imagesService.addImageTextProduct(file, product.getId());
        }
        return product;
    }

    @Override
    @Transactional
    public Product insertAProduct(ProductDTO productDTO){
        productDTO.setIsDelete(Boolean.FALSE);
        Product product = productsRepository.save(productMapper.toEntity(productDTO));

        for (int i = 0; i < productDTO.getCategoryIds().size(); i++){
            ProductCategory productCategory = ProductCategory.builder()
                    .categoryId(productDTO.getCategoryIds().get(i))
                    .productId(product.getId())
                    .build();
            categoryProductRepository.save(productCategory);
        }

        for (String file: productDTO.getImages()){
            imagesService.insertNotDelete(file, product.getId());
        }
        return product;
    }


    @Override
    public ProductResponse getProductById(Long id){
        Product product = productsRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("product not found"));
        Rate rate = rateRepository.findByProductId(id);
        Optional<Brand> brand = brandRepository.findById(product.getBrandId());
        List<String> imageList = imagesRepository.findLinkByProductId(product.getId());
        List<ProductAttributes> productAttributes= productAttributesRepository.findAllByProductId(id);
        ArrayList<Object> attributeAndValues = new ArrayList<>();
        for(ProductAttributes productAttribute : productAttributes){
            Map<String,Object> result = new HashMap<>();
            result.put("id",productAttribute.getId());
            result.put("attribute",productAttribute.getName());
            result.put("values",attributeValuesRepository.findAttributeValuesByAttributeId(productAttribute.getId()));
            attributeAndValues.add(result);
        }
        Long quantity = productItemRepository.getQuantityProduct(id);
        return ProductResponse.builder()
                .attributeAndValues(attributeAndValues)
                .id(product.getId())
                .quantity(quantity)
                .name(product.getName())
                .status(product.getStatus())
                .totalSold(product.getTotalSold())
                .minPrice(product.getMinPrice())
                .description(product.getDescription())
                .thumbnail(product.getThumbnail())
                .averageRate(rate == null? BigDecimal.valueOf(0):rate.getAverageStars())
                .brandId(brand.get().getId())
                .images(imageList)
                .brandName(brand.get().getName())
                .quantityRate(rate==null?0:rate.getQuantity())
                .categories(product.getCategoryList())
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .build();
    }
}

