package com.zelusik.eatery.app.dto.member.response;

import com.zelusik.eatery.app.constant.FoodCategory;
import com.zelusik.eatery.app.constant.member.Gender;
import com.zelusik.eatery.app.dto.member.MemberDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberResponse {

    @Schema(description = "회원 id(PK)", example = "1")
    private Long id;

    @Schema(description = "이메일", example = "eatery@gmail.com")
    private String email;

    @Schema(description = "닉네임", example = "우기")
    private String nickname;

    @Schema(description = "성별", example = "남성")
    private String gender;

    @Schema(description = "선호 음식 카테고리 목록")
    private List<String> favoriteFoodCategories;

    public static MemberResponse of(Long id, String email, String nickname, Gender gender, List<FoodCategory> favoriteFoodCategories) {
        return new MemberResponse(
                id,
                email,
                nickname,
                gender.getDescription(),
                favoriteFoodCategories.stream()
                        .map(FoodCategory::getDescription)
                        .toList()
        );
    }

    public static MemberResponse from(MemberDto dto) {
        return of(
                dto.id(),
                dto.email(),
                dto.nickname(),
                dto.gender(),
                dto.favoriteFoodCategories()
        );
    }
}
