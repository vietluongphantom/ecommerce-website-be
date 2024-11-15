package com.ptit.e_commerce_website_be.do_an_nhom.services.productitem;


import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailProductItemDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ImagesRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ProductItemRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ProductRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductItemServiceImpl implements ProductItemService
{
    private final ProductItemRepository productItemRepository;

    private  final CartItemRepository cartItemRepository;

    private final OrderItemRepository orderItemRepository ;

    private final ProductItemAttributesRepository productItemAttributesRepository;

    private final ProductRepository productRepository;

    private final ImagesRepository imagesRepository;

    private final AttributeValuesRepository attributeValuesRepository;

    private final ProductAttributesRepository productAttributesRepository;

    private final ShopRepository shopRepository;

    @Transactional
    public void deleteProductItemById(Long id) {
        // Xóa các bản ghi trong cart_item tham chiếu đến product_item
        cartItemRepository.deleteByProductItemId(id);
        // Xóa bản ghi trong product_item
        productItemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public DetailProductItemDTO createProductItem(DetailProductItemDTO detailProductItemDTO, Long userId){
        int valuesCount = detailProductItemDTO.getProductItemAtrAttributesDTOS().size();
        List<ProductAttributes> productAttributesList = productAttributesRepository.findAllByProductId(detailProductItemDTO.getProductId());

        List<Long> valuesIds = detailProductItemDTO.getProductItemAtrAttributesDTOS().stream()
                .map(ProductItemAttributesDTO::getAttributeValueId)
                .collect(Collectors.toList());
        if(productAttributesList.size() == valuesCount ) {
            List<ProductItem> productItemList = productItemRepository.findProductItemByAttributesValues(detailProductItemDTO.getProductId(), valuesIds, valuesCount);
            if(productItemList.size() > 0 ){
                throw new AlreadyExistedException("product item already exists");
            }
        }
        if(productAttributesList.size() > valuesCount ) {
            throw new QuantityExceededException("number of product item attribute list isn't enough ");
        }
//        else {
//            throw new Exception("number of product item attribute list isn't enough ");
//        }

        Product product = productRepository.findById(detailProductItemDTO.getProductId())
                .orElseThrow(()-> new DataNotFoundException("Cannot not found product"));

        if (product.getMinPrice() == null || product.getMinPrice().compareTo(detailProductItemDTO.getPrice()) > 0) {
            product.setMinPrice(detailProductItemDTO.getPrice());
            productRepository.save(product);
        }

        ProductItem checkProductItem = productItemRepository.findBySkuCode(detailProductItemDTO.getSkuCode(), detailProductItemDTO.getProductId());
        if(checkProductItem != null){
            throw new AlreadyExistedException("sku code has been used");
        }
        ProductItem productItem = ProductItem.builder()
                .importPrice(detailProductItemDTO.getImportPrice())
                .productId(detailProductItemDTO.getProductId())
                .price(detailProductItemDTO.getPrice())
                .isDelete(Boolean.FALSE)
                .quantity(0)
                .skuCode(detailProductItemDTO.getSkuCode())
                .build();
        productItemRepository.save(productItem);

        for(ProductItemAttributesDTO productItemAttributesDTO : detailProductItemDTO.getProductItemAtrAttributesDTOS()){
            AttributeValues attributeValues = attributeValuesRepository.findById(productItemAttributesDTO.getAttributeValueId())
                    .orElseThrow(()-> new DataNotFoundException("Cannot found attribute values by id"));

            Long attributeId = attributeValuesRepository.findAttributeIdByAttributeValueId(attributeValues.getId());
            ProductItemAttributes productItemAttributes = ProductItemAttributes.builder()
                    .productItemId(productItem.getId())
                    .value(attributeValues.getValue())
                    .productAttributesId(attributeId)
                    .attributeValueId(productItemAttributesDTO.getAttributeValueId())
                    .build();
            productItemAttributesRepository.save(productItemAttributes);
        }
        return detailProductItemDTO;
    }

    @Override
    public Page<Object> getAllProductItem(Long productId, Long userId, Pageable pageable){
        int limit = pageable.getPageSize();
        int offset = pageable.getPageNumber() * limit;
        List<ProductItem> productItems =  productItemRepository.findAllByProductId(productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new DataNotFoundException("Cannot find product"));

        List<Object> productItemValues =  new ArrayList<>();

        for (ProductItem productItem :productItems) {
            List<ProductItemAttributes> attributeValues = productItemAttributesRepository.findByProductItemId(productItem.getId());
            List<ProductItemAttributesDTO> attributeDTOs = attributeValues.stream()
                    .map(attr -> new ProductItemAttributesDTO(attr.getValue(),attr.getAttributeValueId(), attr.getId(), attr.getProductAttributesId()))
                    .collect(Collectors.toList());

            DetailProductItemDTO detailProductItemDTO = DetailProductItemDTO.builder()
                    .id(productItem.getId())
                    .quantity(productItem.getQuantity())
                    .name(product.getName())
                    .skuCode(productItem.getSkuCode())
                    .price(productItem.getPrice())
                    .importPrice(productItem.getImportPrice())
                    .productId(productItem.getProductId())
                    .productItemAtrAttributesDTOS(attributeDTOs)
                    .totalSold(productItem.getTotalSold())
                    .build();
            productItemValues.add(detailProductItemDTO);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), productItemValues.size());
        List<Object> pagedList = productItemValues.subList(start, end);
        return new PageImpl<>(pagedList, pageable, productItemValues.size());
    }



    @Override
    @Transactional
    public DetailProductItemDTO updateProductItem(DetailProductItemDTO detailProductItemDTO, Long userId){
        Product product = productRepository.findById(detailProductItemDTO.getProductId())
                .orElseThrow(()-> new DataNotFoundException("Cannot not found product"));
        int valuesCount = detailProductItemDTO.getProductItemAtrAttributesDTOS().size();
        List<ProductAttributes> productAttributesList = productAttributesRepository.findAllByProductId(detailProductItemDTO.getProductId());

        List<Long> valuesIds = detailProductItemDTO.getProductItemAtrAttributesDTOS().stream()
                .map(ProductItemAttributesDTO::getAttributeValueId)
                .collect(Collectors.toList());
        if(productAttributesList.size() == valuesCount ) {
            List<ProductItem> productItemList = productItemRepository.findProductItemByAttributesValues(detailProductItemDTO.getProductId(), valuesIds, valuesCount);
            if(productItemList.size() > 0 ){
                throw new AlreadyExistedException("product item already exists");
            }
        }
        if (product.getMinPrice() == null || product.getMinPrice().compareTo(detailProductItemDTO.getPrice()) > 0) {
            product.setMinPrice(detailProductItemDTO.getPrice());
            productRepository.save(product);
        }

        ProductItem productItem = productItemRepository.findBySkuCode(detailProductItemDTO.getSkuCode(), detailProductItemDTO.getProductId());
        if (productItem == null){
            throw new DataNotFoundException("Cannot find product item");
        }


        ProductItem newProductItem = ProductItem.builder()
                .skuCode(detailProductItemDTO.getSkuCode())
                .id(productItem.getId())
                .quantity(productItem.getQuantity())
                .isDelete(Boolean.FALSE)
                .importPrice(detailProductItemDTO.getImportPrice())
                .productId(detailProductItemDTO.getProductId())
                .price(detailProductItemDTO.getPrice())
                .build();
        productItemRepository.save(newProductItem);

        productItemAttributesRepository.deleteProductItemAttributesValue(productItem.getId());
        for(ProductItemAttributesDTO productItemAttributesDTO : detailProductItemDTO.getProductItemAtrAttributesDTOS()) {
            AttributeValues attributeValues = attributeValuesRepository.findById(productItemAttributesDTO.getAttributeValueId())
                    .orElseThrow(()-> new DataNotFoundException("Cannot found attribute values by id"));

//            ProductItemAttributes productItemAttributes = productItemAttributesRepository.findById(productItemAttributesDTO.getId()).orElseThrow(()-> new DataNotFoundException("cannot find product item attribute by id"))
            ProductItemAttributes productItemAttributes = ProductItemAttributes.builder()
                    .value(attributeValues.getValue())
                    .id(productItemAttributesDTO.getId())
                    .productAttributesId((productItemAttributesDTO.getProductAttributeId()))
                    .attributeValueId(productItemAttributesDTO.getAttributeValueId())
                    .productItemId(productItem.getId())
                    .build();
            productItemAttributesRepository.save(productItemAttributes);
        }
        return detailProductItemDTO;
    }


    @Override
    @Transactional
    public void deleteProductItem(Long id, Long userId){
        ProductItem productItem = productItemRepository.findById(id)
                .orElseThrow(()-> new DataNotFoundException("product item not exist"));
        productItem.setIsDelete(Boolean.TRUE);
        productItemRepository.save(productItem);
    }


    @Override
    public DetailProductItemDTO getProductItemById(Long id, Long userId){
        ProductItem productItem = productItemRepository.findById(id)
                .orElseThrow(()-> new DataNotFoundException("Cannot find product item by id"));
        Product product = productRepository.findById(productItem.getProductId())
                .orElseThrow(()-> new DataNotFoundException("Cannot find product by id"));

        List<String> images = imagesRepository.findLinkByProductId(product.getId());
        List<ProductItemAttributes> attributeValues = productItemAttributesRepository.findByProductItemId(productItem.getId());
        List<ProductItemAttributesDTO> attributeDTOs = attributeValues.stream()
                .map(attr -> new ProductItemAttributesDTO(attr.getValue(),attr.getAttributeValueId(),attr.getProductAttributesId(), attr.getId()))
                .collect(Collectors.toList());
        DetailProductItemDTO detailProductItemDTO = DetailProductItemDTO.builder()
                .id(productItem.getId())
                .quantity(productItem.getQuantity())
                .name(product.getName())
                .importPrice(productItem.getImportPrice())
                .skuCode(productItem.getSkuCode())
                .price(productItem.getPrice())
                .image(images.get(0))
                .productId(productItem.getProductId())
                .productItemAtrAttributesDTOS(attributeDTOs)
                .totalSold(productItem.getTotalSold())
                .build();
        return detailProductItemDTO;
    }

    @Override
    public Map<String, Object> getProductItemByAttributesValues(Long id, List<Long> valuesIds){
        int valuesCount = valuesIds.size();
        Long sumQuantity = productItemRepository.sumQuantity(id, valuesIds, valuesCount);
        int attributeCount = productAttributesRepository.findAllByProductId(id).size();
        List<ProductItem> productItemList = new ArrayList<>();
        if(attributeCount ==valuesCount  ) {
            productItemList = productItemRepository.findProductItemByAttributesValues(id, valuesIds, valuesCount);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("product_item",productItemList);
        result.put("quantity", sumQuantity);
        return result;
    }

    @Override
    public List<ProductItem> getListProductItemByProductId(Long productId, Long userId){
        Shop shop = shopRepository.findByUserId(userId);
        if(shop == null){
            throw  new DataNotFoundException("cannot find shopId by userId");
        }
        List<ProductItem> productItems = productItemRepository.getListProductItemByProductId(productId, shop.getId());
        return productItems;
    }
}

