package com.ptit.e_commerce_website_be.do_an_nhom.controllers;

import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.CommentDTO;
//import com.ptit.e_commerce_website_be.do_an_nhom.dtos.request.AddCommentRequestDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request.AddCommentRequestDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request.UpdateCommentRequestDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.User;
//import com.ptit.e_commerce_website_be.do_an_nhom.models.responses.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.models.response.CommonResult;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.OrderItemRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.services.comment.ICommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final ICommentService commentService;



    private final OrderItemRepository orderItemRepository;

    @PostMapping("/add_comment")
    public CommonResult<CommentDTO> addComment(@Valid @RequestBody AddCommentRequestDTO requestDTO) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            CommentDTO commentDTO = commentService.addComment(requestDTO, user.getId(), user.getFullName());
            return CommonResult.success(commentDTO, "Comment added successfully");
        } catch (Exception e) {
            return CommonResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }


    @DeleteMapping("/delete_comment/{id}")
    public CommonResult<String> deleteComment(@PathVariable Long id) {
        return commentService.findById(id)
                .map(comment -> {
                    commentService.deleteComment(id);
                    return CommonResult.success("Comment with ID " + id + " has been deleted.");
                }).orElse(CommonResult.error(404, "Comment not found"));
    }


    @GetMapping("/product/{productId}/sort-by-date")
    public CommonResult<List<CommentDTO>> getCommentsByProductIdSortedByDate(@PathVariable("productId") Long productId) {
        try {
            List<CommentDTO> comments = commentService.getCommentsByProductIdAndSortByDate(productId);
            return CommonResult.success(comments, "Comments sorted by date retrieved successfully");
        } catch (Exception e) {
            return CommonResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while retrieving comments");
        }
    }

    @GetMapping("/product/{productId}/sort-by-rating")
    public CommonResult<List<CommentDTO>> getCommentsByProductIdSortedByRating(@PathVariable("productId") Long productId) {
        try {
            List<CommentDTO> comments = commentService.getCommentsByProductIdAndSortByRating(productId);
            return CommonResult.success(comments, "Comments sorted by rating retrieved successfully");
        } catch (Exception e) {
            return CommonResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while retrieving comments");
        }
    }

    @PutMapping("/update_comment/{id}")
    public CommonResult<CommentDTO> updateComment(@PathVariable("id") Long id, @Valid @RequestBody UpdateCommentRequestDTO requestDTO) {
        try {
            CommentDTO commentDTO = commentService.updateComment(id, requestDTO);
            return CommonResult.success(commentDTO, "Comment updated successfully");
        } catch (IllegalArgumentException e) {
            return CommonResult.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            return CommonResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while updating the comment");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResult<CommentDTO>> getCommentById(@PathVariable("id") Long id) {
        Optional<CommentDTO> commentDTO = commentService.getCommentById(id);

        // Nếu không tìm thấy comment, trả về status 204
        if (commentDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(CommonResult.success(null, "No comment found"));
        }
        // Nếu tìm thấy comment, trả về status 200 với thông tin comment
        return ResponseEntity.ok(CommonResult.success(commentDTO.get(), "Comment fetched successfully"));
    }

    @GetMapping("/user/product/{productId}")
    public CommonResult<?> getCommentsByUserAndProductId(@PathVariable("productId") Long productId) {
        try {
            // Lấy thông tin người dùng từ SecurityContextHolder
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = user.getId();
            // Kiểm tra xem người dùng đã mua sản phẩm này chưa
            boolean hasPurchased = orderItemRepository.hasUserPurchasedProduct(userId, productId);
            if (!hasPurchased) {
                return CommonResult.error(HttpStatus.BAD_REQUEST.value(), "User has not purchased this product.");
            }
            // Lấy danh sách comment của người dùng theo productId
            List<CommentDTO> comments = commentService.getCommentsByUserIdAndProductId(userId, productId);

            // Nếu người dùng đã mua hàng nhưng chưa comment
            if (comments.isEmpty()) {
                return CommonResult.failed( "User has purchased the product but has not commented yet.");
            }

            // Nếu người dùng đã comment
            return CommonResult.success(comments, "Comments retrieved successfully");

        } catch (Exception e) {
            return CommonResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while retrieving comments");
        }
    }


}

