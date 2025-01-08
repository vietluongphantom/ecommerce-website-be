package com.ptit.e_commerce_website_be.do_an_nhom.services.inventory;


import com.ptit.e_commerce_website_be.do_an_nhom.exceptions.DataNotFoundException;
import com.ptit.e_commerce_website_be.do_an_nhom.mapper.InventoryMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.DetailInventoryDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.InventoryDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Inventory;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.ProductItem;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Supply;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.InventoryRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.ProductItemRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.SellerRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.ptit.e_commerce_website_be.do_an_nhom.configs.Constant.*;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{
    private final InventoryRepository inventoryRepository;
    private final SellerRepository sellerRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductItemRepository productItemRepository;
    private final SupplyRepository supplyRepository;


    @Override
    public InventoryDTO getInventoryById(Long id){
        Inventory inventory =  inventoryRepository.findById(id)
                .orElseThrow(()->new DataNotFoundException("Cannot find inventory by id"));
        return inventoryMapper.toDTO(inventory);
    }

    @Override
    public Page<DetailInventoryDTO> getAllInventory(String warehouse, String skuCode, String name, Long userId, Pageable pageable){
        Long shopId = sellerRepository.findShopIdByUserId(userId);
        if (shopId == null){
            throw  new DataNotFoundException("Cannot find shopId by userId");
        }
        List<DetailInventoryDTO> detailInventoryDTOList = inventoryRepository.getAllInventory(warehouse, skuCode, name, shopId, pageable.getPageSize(), pageable.getOffset());
        int totalItems = inventoryRepository.countAllInventory(warehouse, skuCode, name, shopId);
        return new PageImpl<>(detailInventoryDTOList, pageable, totalItems);
    }

    @Override
    @Transactional
    public DetailInventoryDTO importWarehouse(DetailInventoryDTO detailInventoryDTO, Long userId){
        ProductItem productItem = productItemRepository.findBySkuCode(detailInventoryDTO.getSkuCode(), detailInventoryDTO.getProductId());
        if(productItem == null){
            throw new DataNotFoundException("Cannot found productItem");
        }
        int quantity  = productItem.getQuantity()==null?0: productItem.getQuantity();
        productItem.setQuantity(quantity + detailInventoryDTO.getQuantity());
        productItemRepository.save(productItem);
        Supply supply = Supply.builder()
                .quantity(detailInventoryDTO.getQuantity())
                .supplierId(detailInventoryDTO.getSupplierId())
                .productItemId(productItem.getId())
//                .supplier(detailInventoryDTO.())
                .location(detailInventoryDTO.getLocation())
                .status(Boolean.TRUE)
                .build();
        supplyRepository.save(supply);

        Inventory inventory = inventoryRepository.findByProductItemIdAndWarehouseId(productItem.getId(), detailInventoryDTO.getSupplierId());
        if(inventory == null){
            Inventory newInventory = Inventory.builder()
                    .supplierId(detailInventoryDTO.getSupplierId())
                    .quantity(detailInventoryDTO.getQuantity())
                    .productItemId(productItem.getId())
                    .build();
            inventoryRepository.save(newInventory);

        }else {
            inventory.setQuantity(detailInventoryDTO.getQuantity() + inventory.getQuantity());
            inventoryRepository.save(inventory);
        }

        return detailInventoryDTO;
    }

    @Override
    public Page<DetailInventoryDTO> getListImport(String supplier,String location,String skuCode,String name ,String createdAt, Long userId, Pageable pageable) {
        Long shopId = sellerRepository.findShopIdByUserId(userId);
        List<DetailInventoryDTO> detailInventoryDTOList = supplyRepository.getAllImport(supplier,location,skuCode, name , createdAt,shopId, pageable.getPageSize(), pageable.getOffset());
        int totalItems = supplyRepository.countAllImport(supplier,location,skuCode, name , createdAt,shopId);
        return new PageImpl<>(detailInventoryDTOList, pageable, totalItems);
    }

    @Override
    public Page<DetailInventoryDTO> getListExport( String  supplier,String location,String skuCode,String  name ,String createdAt, Long userId, Pageable pageable){
        Long shopId = sellerRepository.findShopIdByUserId(userId);
        List<DetailInventoryDTO> detailInventoryDTOList = supplyRepository.getAllExport(supplier,location,skuCode, name , createdAt,shopId, pageable.getPageSize(), pageable.getOffset());
        int totalItems = supplyRepository.countAllExport(supplier,location,skuCode, name , createdAt,shopId);
        return new PageImpl<>(detailInventoryDTOList, pageable, totalItems);
    }


    @Override
    public ByteArrayInputStream getDataDownloaded(Long userId) throws IOException {
        Long shopId = sellerRepository.findShopIdByUserId(userId);
        List<DetailInventoryDTO> detailInventoryDTOList = inventoryRepository.getAllListInventoryData(shopId);
        ByteArrayInputStream data = dataInventoryToExcel(detailInventoryDTOList);
        return data;
    }

    @Override
    public ByteArrayInputStream getImportDataDownloaded(Long userId) throws IOException {
        Long shopId = sellerRepository.findShopIdByUserId(userId);
        List<DetailInventoryDTO> detailInventoryDTOList = supplyRepository.getAllImportData(shopId);
        ByteArrayInputStream data = dataImportToExcel(detailInventoryDTOList);
        return data;
    }

    @Override
    public List<DetailInventoryDTO> getAllListImport(Long userId) {
        Long shopId = sellerRepository.findShopIdByUserId(userId);
        List<DetailInventoryDTO> detailInventoryDTOList = supplyRepository.getAllListImport(shopId);
        return detailInventoryDTOList;
    }


    public static ByteArrayInputStream dataInventoryToExcel(List<DetailInventoryDTO> allListImportInventory) throws IOException {
        Workbook workbook  = new XSSFWorkbook();

        ByteArrayOutputStream byteArrayOutputStream  = new ByteArrayOutputStream();
        try {
            Sheet sheet = workbook.createSheet(SHEET_NAME);
            Row row = sheet.createRow(0);

            for (int i  =0; i< HEADER_ALL_LIST_INVENTORY.length;i++){

                Cell cell = row.createCell(i);
                cell.setCellValue(HEADER_ALL_LIST_INVENTORY[i]);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            int rowIndex = 1;
            for (DetailInventoryDTO p :allListImportInventory){
                Row row1 = sheet.createRow(rowIndex);
                rowIndex++;
                row1.createCell(0).setCellValue(p.getId());
                row1.createCell(1).setCellValue(p.getProductId());
                row1.createCell(2).setCellValue(p.getName());
                row1.createCell(3).setCellValue(p.getSkuCode());
                row1.createCell(4).setCellValue(p.getPrice().doubleValue());
                row1.createCell(5).setCellValue(p.getImportPrice().doubleValue());
                row1.createCell(6).setCellValue(p.getQuantity());
//                row1.createCell(6).setCellValue(p.getWarehouse());
                row1.createCell(7).setCellValue(p.getCreateAt().format(formatter));
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

    public static ByteArrayInputStream dataImportToExcel(List<DetailInventoryDTO> allListImportInventory) throws IOException {
        Workbook workbook  = new XSSFWorkbook();

        ByteArrayOutputStream byteArrayOutputStream  = new ByteArrayOutputStream();
        try {
            Sheet sheet = workbook.createSheet(SHEET_NAME);
            Row row = sheet.createRow(0);

            for (int i  =0; i< HEADER_ALL_LIST_IMPORT.length;i++){

                Cell cell = row.createCell(i);
                cell.setCellValue(HEADER_ALL_LIST_IMPORT[i]);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            int rowIndex = 1;
            for (DetailInventoryDTO p :allListImportInventory){
                Row row1 = sheet.createRow(rowIndex);
                rowIndex++;
                row1.createCell(0).setCellValue(p.getId());
                row1.createCell(1).setCellValue(p.getProductId());
                row1.createCell(2).setCellValue(p.getName());
                row1.createCell(3).setCellValue(p.getSkuCode());
                row1.createCell(4).setCellValue(p.getPrice().doubleValue());
                row1.createCell(5).setCellValue(p.getImportPrice().doubleValue());
                row1.createCell(6).setCellValue(p.getQuantity());
//                row1.createCell(7).setCellValue(p.getWarehouse());
//                row1.createCell(8).setCellValue(p.getSupplier());
                row1.createCell(9).setCellValue(p.getLocation());
                row1.createCell(10).setCellValue(p.getCreateAt().format(formatter));
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

