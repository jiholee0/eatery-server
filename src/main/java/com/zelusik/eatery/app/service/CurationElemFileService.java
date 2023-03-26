package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.curation.CurationElemFile;
import com.zelusik.eatery.app.repository.curation.CurationElemFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CurationElemFileService {

    private final FileService fileService;
    private final CurationElemFileRepository curationElemFileRepository;

    private static final String DIR_PATH = "curation/";

    /**
     * CurationElem 이미지를 S3 bucket에 업로드하고,
     * CurationElemFile entity를 생성 및 저장한다.
     *
     * @param multipartFile 업로드할 이미지
     * @return 저장된 CurationElemFile entity
     */
    @Transactional
    public CurationElemFile upload(MultipartFile multipartFile) {
        S3ImageDto s3ImageDto = fileService.uploadImage(multipartFile, DIR_PATH);
        return curationElemFileRepository.save(CurationElemFile.of(
                s3ImageDto.originalName(),
                s3ImageDto.storedName(),
                s3ImageDto.url(),
                s3ImageDto.thumbnailStoredName(),
                s3ImageDto.thumbnailUrl()
        ));
    }
}
