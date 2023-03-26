package com.zelusik.eatery.app.dto.curation.response;

import com.zelusik.eatery.app.dto.curation.CurationElemDto;
import com.zelusik.eatery.app.dto.place.response.PlaceCompactResponseWithoutIsMarked;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CurationElemResponse {

    @Schema(description = "PK", example = "1")
    private Long id;

    @Schema(description = "장소 정보")
    private PlaceCompactResponseWithoutIsMarked place;

    @Schema(description = "이미지 url", example = "https://eatery-s3-bucket...")
    private String image;

    public static CurationElemResponse of(Long id, PlaceCompactResponseWithoutIsMarked place, String image) {
        return new CurationElemResponse(id, place, image);
    }

    public static CurationElemResponse from(CurationElemDto dto) {
        return of(
                dto.id(),
                PlaceCompactResponseWithoutIsMarked.from(dto.placeDto()),
                dto.imageDto().thumbnailUrl()
        );
    }
}
