package com.ptit.e_commerce_website_be.do_an_nhom.services.comment;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.CommentDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request.AddCommentRequestDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request.UpdateCommentRequestDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Comment;

import java.util.List;
import java.util.Optional;

public interface ICommentService {
    CommentDTO addComment(AddCommentRequestDTO requestDTO,  Long userID, String fullName);

    Optional<Comment> findById(Long id);

    void deleteComment(Long id);

    List<CommentDTO> getCommentsByProductIdAndSortByDate(Long productId);

    List<CommentDTO> getCommentsByProductIdAndSortByRating(Long productId);

    CommentDTO updateComment(Long id, UpdateCommentRequestDTO requestDTO) ;

    Optional<CommentDTO> getCommentById(Long id) ;
    List<CommentDTO> getCommentsByUserIdAndProductId(Long userId, Long productId);


}
