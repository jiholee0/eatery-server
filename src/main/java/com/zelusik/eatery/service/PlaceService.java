package com.zelusik.eatery.service;

import com.zelusik.eatery.constant.place.DayOfWeek;
import com.zelusik.eatery.constant.place.FilteringType;
import com.zelusik.eatery.constant.place.PlaceSearchKeyword;
import com.zelusik.eatery.constant.review.ReviewKeywordValue;
import com.zelusik.eatery.domain.place.OpeningHours;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.dto.place.PlaceDtoWithMarkedStatus;
import com.zelusik.eatery.dto.place.PlaceDtoWithMarkedStatusAndImages;
import com.zelusik.eatery.dto.place.PlaceFilteringKeywordDto;
import com.zelusik.eatery.dto.place.PlaceScrapingResponse;
import com.zelusik.eatery.dto.place.request.PlaceCreateRequest;
import com.zelusik.eatery.dto.review.ReviewImageDto;
import com.zelusik.eatery.exception.place.PlaceNotFoundException;
import com.zelusik.eatery.exception.scraping.ScrapingServerInternalError;
import com.zelusik.eatery.repository.bookmark.BookmarkRepository;
import com.zelusik.eatery.repository.place.OpeningHoursRepository;
import com.zelusik.eatery.repository.place.PlaceRepository;
import com.zelusik.eatery.repository.review.ReviewKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PlaceService {

    private final WebScrapingService webScrapingService;
    private final ReviewImageService reviewImageService;
    private final PlaceRepository placeRepository;
    private final OpeningHoursRepository openingHoursRepository;
    private final BookmarkService bookmarkService;
    private final ReviewKeywordRepository reviewKeywordRepository;

    /**
     * 장소 정보를 받아 장소를 저장한다.
     *
     * @param placeCreateRequest 장소 정보가 담긴 dto.
     * @return 저장된 장소 entity.
     * @throws ScrapingServerInternalError Web scraping 서버에서 에러가 발생한 경우
     */
    @Transactional
    public PlaceDtoWithMarkedStatus create(Long memberId, PlaceCreateRequest placeCreateRequest) {
        PlaceScrapingResponse scrapingInfo = webScrapingService.getPlaceScrapingInfo(placeCreateRequest.getKakaoPid());

        Place place = placeRepository.save(placeCreateRequest
                .toDto(scrapingInfo.getHomepageUrl(), scrapingInfo.getClosingHours())
                .toEntity());

        if (scrapingInfo.getOpeningHours() != null) {
            List<OpeningHours> openingHoursList = scrapingInfo.getOpeningHours().stream()
                    .map(oh -> oh.toOpeningHoursEntity(place))
                    .toList();
            openingHoursRepository.saveAll(openingHoursList);
            place.getOpeningHoursList().addAll(openingHoursList);
        }

        return PlaceDtoWithMarkedStatus.from(place, bookmarkService.isMarkedPlace(memberId, place));
    }

    /**
     * placeId에 해당하는 장소를 조회한 후 반환한다.
     *
     * @param placeId 조회하고자 하는 장소의 PK
     * @return 조회한 장소 entity
     */
    public Place findById(Long placeId) {
        return placeRepository.findById(placeId).orElseThrow(PlaceNotFoundException::new);
    }

    /**
     * kakaoPid에 해당하는 장소를 조회한 후 반환한다.
     *
     * @param kakaoPid 조회하고자 하는 장소의 kakaoPid
     * @return 조회한 장소의 optional entity
     */
    public Optional<Place> findOptByKakaoPid(String kakaoPid) {
        return placeRepository.findByKakaoPid(kakaoPid);
    }

    /**
     * placeId에 해당하는 장소를 조회한 후 반환한다.
     *
     * @param placeId 조회하고자 하는 장소의 PK
     * @return 조회한 장소 dto
     */
    public PlaceDtoWithMarkedStatusAndImages findDtoWithMarkedStatusAndImages(Long memberId, Long placeId) {
        PlaceDtoWithMarkedStatus place = placeRepository.findDtoWithMarkedStatus(placeId, memberId).orElseThrow(PlaceNotFoundException::new);
        List<ReviewImageDto> images = reviewImageService.findLatest3ByPlace(place.getId());

        return PlaceDtoWithMarkedStatusAndImages.from(place, images);
    }

    /**
     * <p>중심 좌표 기준, 가까운 순으로 장소 목록을 검색한다.
     * <p>최대 50km 범위까지 조회한다.
     *
     * @param daysOfWeek 검색할 요일 목록
     * @param keyword    검색 키워드
     * @param lat        중심좌표의 위도
     * @param lng        중심좌표의 경도
     * @param pageable   paging 정보
     * @return 조회한 장소 목록
     */
    public Slice<PlaceDtoWithMarkedStatusAndImages> findDtosNearBy(Long memberId, List<DayOfWeek> daysOfWeek, PlaceSearchKeyword keyword, String lat, String lng, Pageable pageable) {
        return placeRepository.findDtosNearBy(memberId, daysOfWeek, keyword, lat, lng, 50, pageable);
    }

    /**
     * 북마크에 저장한 장소 목록(Slice)을 조회합니다.
     * 조회 시 장소와 관련된 이미지를 함께 조회합니다.
     * 이미지를 가져오는 기준은 "해당 장소에 작성된 리뷰에 담긴 사진 중 최신순으로 최대 3개"입니다.
     *
     * @param memberId 로그인 회원의 PK.
     * @param pageable paging 정보
     * @return 조회한 장소 목록과 사진 데이터
     */
    public Slice<PlaceDtoWithMarkedStatusAndImages> findMarkedDtos(Long memberId, FilteringType filteringType, String filteringKeyword, Pageable pageable) {
        return placeRepository.findMarkedPlaces(memberId, filteringType, filteringKeyword, pageable);
    }

    /**
     * {@code memberId}에 해당하는 회원의 저장한 장소들에 대해 filtering keyword 목록을 조회한다.
     *
     * @param memberId filtering keywords를 조회하고자 하는 회원의 PK
     * @return 조회된 filtering keywords
     */
    public List<PlaceFilteringKeywordDto> getFilteringKeywords(Long memberId) {
        return placeRepository.getFilteringKeywords(memberId);
    }

    /**
     * 장소의 top 3 keyword를 DB에서 조회 후 갱신한다.
     *
     * @param place top 3 keyword를 갱신할 장소
     */
    @Transactional
    public void renewTop3Keywords(Place place) {
        List<ReviewKeywordValue> placeTop3Keywords = reviewKeywordRepository.searchTop3Keywords(place.getId());
        place.setTop3Keywords(placeTop3Keywords);
    }
}
