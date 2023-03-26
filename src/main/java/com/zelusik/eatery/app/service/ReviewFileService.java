package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.domain.review.Review;
import com.zelusik.eatery.app.domain.review.ReviewFile;
import com.zelusik.eatery.app.dto.file.S3FileDto;
import com.zelusik.eatery.app.dto.review.ReviewFileDto;
import com.zelusik.eatery.app.repository.review.ReviewFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewFileService {

    private final FileService fileService;
    private final ReviewFileRepository reviewFileRepository;

    private static final String DIR_PATH = "review/";

    /**
     * Review 첨부 파일을 S3 bucket에 업로드한다.
     *
     * @param review         파일이 첨부될 리뷰
     * @param multipartFiles 업로드할 파일들
     */
    @Transactional
    public void upload(Review review, List<MultipartFile> multipartFiles) {
        multipartFiles.forEach(multipartFile -> {
            S3ImageDto s3ImageDto = fileService.uploadImage(multipartFile, DIR_PATH);
            review.getReviewFiles().add(ReviewFile.of(
                    review,
                    s3ImageDto.originalName(),
                    s3ImageDto.storedName(),
                    s3ImageDto.url(),
                    s3ImageDto.thumbnailStoredName(),
                    s3ImageDto.thumbnailUrl()
            ));
        });
        reviewFileRepository.saveAll(review.getReviewFiles());
    }

    public List<ReviewFileDto> findLatest3ByPlace(Place place) {
        return reviewFileRepository.findLatest3ByPlace(place).stream()
                .map(ReviewFileDto::from)
                .toList();
    }

    /**
     * ReviewFile들을 삭제한다.
     *
     * @param reviewFiles 삭제할 ReviewFile 목록
     */
    @Transactional
    public void deleteAll(List<ReviewFile> reviewFiles) {
        reviewFileRepository.deleteAll(reviewFiles);
    }
}
