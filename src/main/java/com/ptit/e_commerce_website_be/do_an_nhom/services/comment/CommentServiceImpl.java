package com.ptit.e_commerce_website_be.do_an_nhom.services.comment;

import com.ptit.e_commerce_website_be.do_an_nhom.mapper.CommentMapper;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.CommentDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request.AddCommentRequestDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.dtos.request.UpdateCommentRequestDTO;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Comment;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.CommentRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.OrderItemRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.services.rate.RateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final RateService rateService;
    private final OrderItemRepository orderItemRepository;

    @Override
    public CommentDTO addComment(AddCommentRequestDTO requestDTO, Long userId, String fullName) {
        // Kiểm tra xem người dùng đã mua sản phẩm này chưa


        boolean hasPurchased = orderItemRepository.hasUserPurchasedProduct(userId, requestDTO.getProductId());

        if (!hasPurchased) {
            throw new IllegalArgumentException("You must purchase the product before commenting.");
        }
        // Tạo đối tượng Comment từ thông tin trong requestDTO
        Comment comment = Comment.builder()
                .content(requestDTO.getContent())
                .productId(requestDTO.getProductId())
                .fullName(fullName)
                .userId(userId)
                .rateStars(requestDTO.getRateStars())
                .replyTo(requestDTO.getReplyTo())
                .build();
        Comment savedComment = commentRepository.save(comment);
        // Cập nhật đánh giá của sản phẩm
        rateService.updateRate(requestDTO.getProductId());
        // Lưu comment vào cơ sở dữ liệu

        // Trả về DTO chứa thông tin comment đã lưu
        return commentMapper.toDto(savedComment);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Comment with ID " + id + " does not exist"));
        commentRepository.deleteById(id);
        rateService.updateRate(comment.getProductId());
    }

    @Override
    public List<CommentDTO> getCommentsByProductIdAndSortByDate(Long productId) {
        List<Comment> commentList = commentRepository.findCommentsByProductId(productId);

        return commentList.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDTO> getCommentsByProductIdAndSortByRating(Long productId) {
        List<Comment> commentList = commentRepository.findCommentsByProductId(productId);
        return commentList.stream()
                .sorted(Comparator.comparing(Comment::getRateStars).reversed())
                .map(commentMapper::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public CommentDTO updateComment(Long id, UpdateCommentRequestDTO requestDTO) {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Comment not found"));
        comment.setContent(requestDTO.getContent());
        comment.setRateStars(requestDTO.getRateStars());
        comment.setModifiedAt(LocalDateTime.now());
        commentRepository.save(comment);
        rateService.updateRate(comment.getProductId());
        return commentMapper.toDto(comment);

    }

    @Override
    public Optional<CommentDTO> getCommentById(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        return commentOptional.map(commentMapper::toDto); // Trả về Optional<CommentDTO>
    }

    @Override
    public List<CommentDTO> getCommentsByUserIdAndProductId(Long userId, Long productId) {
        try {
            List<Comment> commentList = commentRepository.findCommentsByUserIdAndProductId(userId, productId);
            return commentList.stream()
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve comments", e);
        }
    }


}