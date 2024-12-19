package com.ptit.e_commerce_website_be.do_an_nhom.repositories;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Brand;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    @Query(value = "SELECT * FROM brand b " +
            "WHERE b.name LIKE CONCAT('%',?1,'%') " +
            "AND b.is_delete = 0", nativeQuery = true)
    Page<Brand> findAllBrand(String name, Pageable pageable);

    @Query("SELECT b.name, COUNT(p.id) " +
            "FROM Brand b LEFT JOIN Product p ON b.id = p.brandId " +
            "GROUP BY b.id, b.name")
    List<Object[]> findBrandNamesWithProductCount();
}
