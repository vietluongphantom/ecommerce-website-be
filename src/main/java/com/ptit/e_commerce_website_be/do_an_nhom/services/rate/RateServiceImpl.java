package com.ptit.e_commerce_website_be.do_an_nhom.services.rate;

import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Comment;
import com.ptit.e_commerce_website_be.do_an_nhom.models.entities.Rate;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.CommentRepository;
import com.ptit.e_commerce_website_be.do_an_nhom.repositories.RateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RateServiceImpl implements RateService {

    private final RateRepository rateRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public void updateRate(Long productId) {
        // Lấy danh sách bình luận dựa trên productId
        List<Comment> commentList = commentRepository.findCommentsByProductId(productId);
        long newQuantity = commentList.size();

        // Kiểm tra nếu không có bình luận nào
        if (newQuantity == 0) {
            // Nếu không có bình luận, đặt giá trị trung bình đánh giá về 0
            Rate rate = rateRepository.findByProductId(productId);
            if (rate == null) {
                rate = new Rate();
                rate.setProductId(productId);
                rate.setAverageStars(BigDecimal.ZERO);
                rate.setQuantity(0L);
                rateRepository.save(rate);
                return;
            }
            rate.setAverageStars(BigDecimal.ZERO);
            rate.setQuantity(0L);
            rateRepository.save(rate);
            return;
        }

        // Tính tổng số sao từ các bình luận
        BigDecimal totalStars = commentList.stream()
                .map(Comment::getRateStars)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tính trung bình sao
        BigDecimal newAverageStars = totalStars.divide(BigDecimal.valueOf(newQuantity), 2, RoundingMode.HALF_UP);

        // Cập nhật thông tin đánh giá trong cơ sở dữ liệu
        Rate rate = rateRepository.findByProductId(productId);
        if (rate == null) {
            rate = new Rate();
            rate.setProductId(productId);
        }
        rate.setAverageStars(newAverageStars);
        rate.setQuantity(newQuantity);

        rateRepository.save(rate);
    }

    @Override
    public BigDecimal getAverageStarsByProductId(Long productId) {
        // Tìm Rate theo productId
        Rate rate = rateRepository.findByProductId(productId);
        if (rate != null) {
            // Nếu tìm thấy, trả về giá trị averageStars
            return rate.getAverageStars();
        }
        // Nếu không tìm thấy, trả về giá trị mặc định (ví dụ: BigDecimal.ZERO)
        return BigDecimal.ZERO;
    }
}
