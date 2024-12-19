package com.ptit.e_commerce_website_be.do_an_nhom.mapper;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.CommentDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentDTO toDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setFullName(comment.getFullName());
        commentDTO.setProductId(comment.getProductId());
        commentDTO.setUserId(comment.getUserId());
        commentDTO.setRateStars(comment.getRateStars());
        commentDTO.setReplyTo(comment.getReplyTo());
        commentDTO.setCreatedAt(comment.getCreatedAt());
        commentDTO.setModifiedAt(comment.getModifiedAt());
        return commentDTO;
    }

    public Comment toEntity(CommentDTO commentDTO) {
        if (commentDTO == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setId(commentDTO.getId());
        comment.setContent(commentDTO.getContent());
        comment.setProductId(commentDTO.getProductId());
        comment.setUserId(commentDTO.getUserId());
        comment.setRateStars(commentDTO.getRateStars());
        comment.setReplyTo(commentDTO.getReplyTo());
        comment.setModifiedAt(commentDTO.getModifiedAt());
        comment.setCreatedAt(commentDTO.getCreatedAt());
        comment.setFullName(comment.getFullName());
        return comment;
    }
}
