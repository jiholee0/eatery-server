package com.zelusik.eatery.app.dto.review;

import com.zelusik.eatery.app.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.app.domain.review.Review;
import com.zelusik.eatery.app.domain.review.ReviewKeyword;
import com.zelusik.eatery.app.dto.member.MemberDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReviewDtoWithMember {

    private Long id;
    private MemberDto writerDto;
    private List<ReviewKeywordValue> keywords;
    private String autoCreatedContent;
    private String content;
    private List<ReviewFileDto> reviewFileDtos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static ReviewDtoWithMember of(List<ReviewKeywordValue> keywords, String autoCreatedContent, String content) {
        return of(null, null, keywords, autoCreatedContent, content, null, null, null, null);
    }

    public static ReviewDtoWithMember of(Long id, MemberDto writerDto, List<ReviewKeywordValue> keywords, String autoCreatedContent, String content, List<ReviewFileDto> reviewFileDtos, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new ReviewDtoWithMember(id, writerDto, keywords, autoCreatedContent, content, reviewFileDtos, createdAt, updatedAt, deletedAt);
    }

    public static ReviewDtoWithMember from(Review entity) {
        return of(
                entity.getId(),
                MemberDto.from(entity.getWriter()),
                entity.getKeywords().stream()
                        .map(ReviewKeyword::getKeyword)
                        .toList(),
                entity.getAutoCreatedContent(),
                entity.getContent(),
                entity.getReviewFiles().stream()
                        .map(ReviewFileDto::from)
                        .toList(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
