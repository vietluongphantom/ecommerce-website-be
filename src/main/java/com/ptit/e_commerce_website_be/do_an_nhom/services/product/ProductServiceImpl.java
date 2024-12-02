package com.ptit.e_commerce_website_be.do_an_nhom.services.product;


import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.ProductMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailInventoryDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.ProductDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.*;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.ProductResponse;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.*;
import com.ptit.e_commerce_website_be.do_an_nhom.services.CloudinaryService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.images.ImagesService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.productitem.ProductItemServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.ptit.e_commerce_website_be.do_an_nhom.configs.Constant.*;

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

                            .brandId(brand.get().getId())
                            .brandName(brand.get().getName())

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

                            .brandId(brand.get().getId())
                            .brandName(brand.get().getName())

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
    public ByteArrayInputStream getProductDataDownloaded(Long userId) throws IOException {
        Shop shop = shopRepository.findByUserId(userId);
        List<ProductResponse> productResponseList = new ArrayList<>();
        productsRepository.getProductDataForExcel(shop.getId()).stream()
                .forEach(product -> {
                    List<String> categoryNames = product.getCategoryList().stream()
                            .map(Category::getName)
                            .collect(Collectors.toList());

                    // Xử lý Optional Brand để tránh NoSuchElementException
                    Optional<Brand> brand = brandRepository.findById(product.getBrandId());
                    List<String> imageList = imagesRepository.findLinkByProductId(product.getId());

                    ProductResponse productResponse = ProductResponse.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .description(product.getDescription())
                            .minPrice(product.getMinPrice())
                            .totalSold(product.getTotalSold())
                            .thumbnail(product.getThumbnail())

                            .brandId(brand.get().getId())
                            .brandName(brand.get().getName())

                            .images(imageList)
                            .categoryNames(categoryNames)
                            .categories(product.getCategoryList())
                            .createdAt(product.getCreatedAt())
                            .modifiedAt(product.getModifiedAt())
                            .build();
                    productResponseList.add(productResponse);
                });
        ByteArrayInputStream data = dataProductToExcel(productResponseList);
        return data;
    }


    @Override
    public ProductResponse getProductById(Long id){
        Product product = productsRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("product not found"));

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

                .brandId(brand.get().getId())
                .images(imageList)
                .brandName(brand.get().getName())

                .categories(product.getCategoryList())
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .build();
    }


    public static ByteArrayInputStream dataProductToExcel(List<ProductResponse> productResponseList) throws IOException {
        Workbook workbook  = new XSSFWorkbook();

        ByteArrayOutputStream byteArrayOutputStream  = new ByteArrayOutputStream();
        try {
            Sheet sheet = workbook.createSheet(SHEET_NAME);
            Row row = sheet.createRow(0);

            for (int i  =0; i< HEADER_ALL_LIST_PRODUCT.length;i++){

                Cell cell = row.createCell(i);
                cell.setCellValue(HEADER_ALL_LIST_PRODUCT[i]);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            int rowIndex = 1;
            for (ProductResponse p :productResponseList){
                Row row1 = sheet.createRow(rowIndex);
                rowIndex++;
                row1.createCell(0).setCellValue(p.getId());
                row1.createCell(1).setCellValue(p.getName());
                row1.createCell(2).setCellValue(p.getMinPrice().doubleValue());
                row1.createCell(3).setCellValue(p.getTotalSold()==null?0:p.getTotalSold());
                row1.createCell(4).setCellValue(p.getBrandName());
//                row1.createCell(5).setCellValue(p.getCategoryNames());
                row1.createCell(5).setCellValue(String.join(", ", p.getCategoryNames()));
                row1.createCell(6).setCellValue(String.join(", ", p.getImages()));
                row1.createCell(7).setCellValue(p.getAverageRate()==null?0:p.getAverageRate().doubleValue());
                row1.createCell(8).setCellValue(p.getQuantity()==null?0:p.getQuantity());
                row1.createCell(9).setCellValue(p.getDescription());
                row1.createCell(10).setCellValue(p.getCreatedAt().format(formatter));
            }

            workbook.write(byteArrayOutputStream);
            return  new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            workbook.close();
            byteArrayOutputStream.close();
        }
    }

}

