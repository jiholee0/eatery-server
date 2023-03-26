package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.member.Member;
import com.zelusik.eatery.app.domain.member.ProfileImage;
import com.zelusik.eatery.app.repository.member.ProfileImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileImageService {

    private final FileService fileService;
    private final ProfileImageRepository profileImageRepository;

    private static final String DIR_PATH = "member/";

    @Transactional
    public ProfileImage upload(Member member, MultipartFile multipartFile) {
        S3ImageDto s3ImageDto = fileService.uploadImage(multipartFile, DIR_PATH);
        return profileImageRepository.save(ProfileImage.of(
                member,
                s3ImageDto.originalName(),
                s3ImageDto.storedName(),
                s3ImageDto.url(),
                s3ImageDto.thumbnailStoredName(),
                s3ImageDto.thumbnailUrl()
        ));
    }
}
