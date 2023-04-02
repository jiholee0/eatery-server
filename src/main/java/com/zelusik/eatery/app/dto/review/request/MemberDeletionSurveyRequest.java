package com.zelusik.eatery.app.dto.review.request;

import com.zelusik.eatery.app.constant.review.MemberDeletionSurveyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MemberDeletionSurveyRequest {

    @Schema(description = "회원 탈퇴 설문 응답", example = "HARD_TO_WRITE")
    @NotNull
    private MemberDeletionSurveyType surveyType;

    public static MemberDeletionSurveyRequest of(MemberDeletionSurveyType surveyType) {
        return new MemberDeletionSurveyRequest(surveyType);
    }
}
