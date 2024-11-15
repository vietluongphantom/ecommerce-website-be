package com.ptit.e_commerce_website_be.do_an_nhom.controllers;


import com.github.javafaker.Faker;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.ProductMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.ProductDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Product;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CloudinaryResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.ProductListResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.ProductResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.services.CloudinaryService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.images.ImagesService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductsController {
    private final IProductService iProductService;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;
    private final ImagesService imagesService;


    @GetMapping("/user")
    public CommonResult<ProductListResponse> getAllProductsUser(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "", name = "category-ids") String categoryIds,
            @RequestParam(defaultValue = "", name = "brand-ids") String brandIds,
            @RequestParam(defaultValue = "default", name ="sort") String sortOption,
            @RequestParam(required = false, name = "fromPrice") Long fromPrice,
            @RequestParam(required = false, name = "toPrice") Long toPrice,
            @RequestParam(defaultValue = "", name ="rate") Float rate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit
    ){
        Sort sort = switch (sortOption) {
            case "latest" -> Sort.by("createdAt").descending();
            case "desc" -> Sort.by("minPrice").descending();
            case "asc" -> Sort.by("minPrice").ascending();
            default -> Sort.by("id").ascending();
        };

        PageRequest pageRequest = PageRequest.of(
                page, limit,
//                Sort.by("createdAt").descending());
                sort);
        Page<ProductResponse> productPage = null;
        List<Long> brandList = null;
        if (!brandIds.equals("")) {
            brandList = Arrays.stream(brandIds.split(","))
                    .map(Long::parseLong)
                    .toList();
            if (brandList.isEmpty()) {
                brandList = null;
            }
        }

        List<Long> categoryList = null;
        if (!categoryIds.equals("")) {
            categoryList = Arrays.stream(categoryIds.split(","))
                    .map(Long::parseLong)
                    .toList();
            if (categoryList.isEmpty()) {
                categoryList = null;
            }
        }

        int totalPages = 0;
        List<ProductResponse> productResponses = null;
        if (productPage == null) {
            productPage = iProductService.searchProducts(categoryList, categoryList == null ? 0 : categoryList.size(), brandList, keyword, fromPrice , toPrice ,rate, pageRequest);
            // Get total pages
            totalPages = productPage.getTotalPages();
            productResponses = productPage.getContent();
        }

        return CommonResult.success(ProductListResponse.builder()
                .productResponses(productResponses)
                .totalPages(totalPages)
                .build(), "Get all products successfully");
    }

    @GetMapping("/seller")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<ProductListResponse> getAllProductsAdmin(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "", name = "category-ids") String categoryIds,
            @RequestParam(defaultValue = "", name = "brand-ids") String brandIds,
            @RequestParam(defaultValue = "default", name ="sort") String sortOption,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ){
        Sort sort = switch (sortOption) {
            case "latest" -> Sort.by("createdAt").descending();
            case "desc" -> Sort.by("minPrice").descending();
            case "asc" -> Sort.by("minPrice").ascending();
            default -> Sort.by("id").ascending();
        };

        PageRequest pageRequest = PageRequest.of(
                page, limit,
//                Sort.by("createdAt").descending());
                sort);
        Page<ProductResponse> productPage = null;
        List<Long> brandList = null;
        if (!brandIds.equals("")) {
            brandList = Arrays.stream(brandIds.split(","))
                    .map(Long::parseLong)
                    .toList();
            if (brandList.isEmpty()) {
                brandList = null;
            }
        }

        List<Long> categoryList = null;
        if (!categoryIds.equals("")) {
            categoryList = Arrays.stream(categoryIds.split(","))
                    .map(Long::parseLong)
                    .toList();
            if (categoryList.isEmpty()) {
                categoryList = null;
            }
        }

        int totalPages = 0;
        List<ProductResponse> productResponses = null;
        if (productPage == null) {
            User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            productPage = iProductService.searchProductsSeller(categoryList, categoryList == null ? 0 : categoryList.size(), brandList, keyword, user.getId(), pageRequest);
            // Get total pages
            totalPages = productPage.getTotalPages();
            productResponses = productPage.getContent();
        }

        return CommonResult.success(ProductListResponse.builder()
                .productResponses(productResponses)
                .totalPages(totalPages)
                .build(), "Get all products successfully");
    }


    @GetMapping("/{id}")
    public CommonResult<ProductResponse> getProductById(@PathVariable Long id){
        ProductResponse productResponse = iProductService.getProductById(id);
        return CommonResult.success(productResponse, "Get product by id successfully");
    }


    @PostMapping
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<ProductDTO> createProduct(@ModelAttribute ProductDTO productDTO){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Product savedProduct = iProductService.save(productDTO, user.getId());
        return CommonResult.success(null, "Create product successfully");
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<ProductDTO> updateProduct(@PathVariable Long id, @ModelAttribute ProductDTO productDTO){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Product savedProduct = iProductService.updateProductById(id,productDTO, user.getId());
        return CommonResult.success(null,"Update product successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<String> deleteProduct(@PathVariable Long id){
        iProductService.deleteById(id);
        return CommonResult.success("delete product success");
    }

    @PostMapping(value = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResult<String> uploadImage(
            @RequestParam("files") MultipartFile file
    ) throws Exception {
        if (file == null || file.isEmpty()) {
            return CommonResult.failed("File không được rỗng");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            return CommonResult.failed("Bạn chỉ có thể tải lên tệp tối đa 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return CommonResult.failed("Bạn chỉ có thể tải lên tệp là hình ảnh");
        }

        // Tải lên hình ảnh
        CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadImage(file);

        return CommonResult.success(cloudinaryResponse.getUrl(), "Tải lên thành công");
    }


    @PostMapping("/generateFakeProducts")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public CommonResult<ProductDTO> createProduct(){
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Product savedProduct = iProductService.save(productDTO, user.getId());
        Faker faker = new Faker();
        for (long i = 0; i < 100000; i++) {
//            Thread.sleep(100);
            String title = faker.commerce().productName();
//            if (iProductService.existsByTitle(title)) {
//                continue;
//            }
            List<Long> categoryIds = new ArrayList<>();
            for (int j = 0; j < faker.number().numberBetween(1,5); j++) {
                categoryIds.add((long) faker.number().numberBetween(1, 29));
            }
            ProductDTO productDTO = ProductDTO
                    .builder()
                    .name(title)
//                    .price(faker.number().numberBetween(10, 90_000_000))
                    .description(faker.lorem().sentence())
//                    .discount(faker.number().numberBetween(0, 10))
//                    .averageRate(faker.number().numberBetween(0, 5))
                    .brandId((long)faker.number().numberBetween(1,5))
                    .categoryIds(categoryIds)
                    .images(List.of("https://res.cloudinary.com/dqgarzqlx/image/upload/v1724702644/615999978560552960.jpg"))
                    .status(1)
                    .totalSold(((long)faker.number().numberBetween(10, 90_000)))
                    .shopId((long)faker.number().numberBetween(1,5))
                    .minPrice(BigDecimal.valueOf(faker.number().numberBetween(10, 90_000)))
                    .build();
            Product product = iProductService.insertAProduct(productDTO);

        }
        return CommonResult.success(null,  "Create product successfully");
    }

}

