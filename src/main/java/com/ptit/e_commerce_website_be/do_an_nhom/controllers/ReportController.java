package com.ptit.e_commerce_website_be.do_an_nhom.controllers;


import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
import com.ptit.e_commerce_website_be.do_an_nhom.services.inventory.InventoryService;
import com.ptit.e_commerce_website_be.do_an_nhom.services.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.*;


@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {
    private final InventoryService inventoryService;
    private final IProductService productService;

    @GetMapping("/export/excel/inventory")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public ResponseEntity<InputStreamResource>  exportInventoryDataToExcelFile( ) throws IOException{
        String fileName ="Inventory.xlsx";
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ByteArrayInputStream inputStream = inventoryService.getDataDownloaded(user.getId());
        InputStreamResource    response = new InputStreamResource(inputStream);
        ResponseEntity<InputStreamResource> responseEntity = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename="+fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(response);
        return responseEntity;
    }


    @GetMapping("/export/excel/import-inventory")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public ResponseEntity<InputStreamResource>  exportImportDataToExcelFile( ) throws IOException{
        String fileName ="History-import-product.xlsx";
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ByteArrayInputStream inputStream = inventoryService.getImportDataDownloaded(user.getId());
        InputStreamResource    response = new InputStreamResource(inputStream);
        ResponseEntity<InputStreamResource> responseEntity = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename="+fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(response);
        return responseEntity;
    }

    @GetMapping("/export/excel/product")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public ResponseEntity<InputStreamResource>  exportProductDataToExcelFile( ) throws IOException{
        String fileName ="List-product.xlsx";
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ByteArrayInputStream inputStream = productService.getProductDataDownloaded(user.getId());
        InputStreamResource    response = new InputStreamResource(inputStream);
        ResponseEntity<InputStreamResource> responseEntity = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename="+fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(response);
        return responseEntity;
    }

//    @GetMapping("/export/excel/import-inventory")
//    @PreAuthorize("hasRole('ROLE_SELLER')")
//    private void download() {
//        String fileName ="products.xlsx";
//        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        List<DetailInventoryDTO> result = inventoryService.getAllListImport(user.getId());
//        ByteArrayInputStream inputStream = inventoryService.getDataDownloaded(user.getId());
//        InputStreamResource    response = new InputStreamResource(inputStream);
//
//        ResponseEntity<InputStreamResource> responseEntity = ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename="+fileName)
//                .contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(response);
//        return responseEntity;
//    }

}
