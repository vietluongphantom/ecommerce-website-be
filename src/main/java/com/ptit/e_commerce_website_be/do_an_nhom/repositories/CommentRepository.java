package com.ptit.e_commerce_website_be.do_an_nhom.repositories;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Các phương thức truy vấn tùy chỉnh nếu cần

    @Query("SELECT c FROM Comment c WHERE c.productId = :productId")
    List<Comment> findCommentsByProductId(@Param("productId") Long productId);

    List<Comment> findCommentsByUserIdAndProductId(Long userId, Long productId);
}
