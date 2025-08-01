package com.grantip.backend.domain.favorite.service;

import com.grantip.backend.domain.favorite.domain.entity.FavoriteScholar;
import com.grantip.backend.domain.favorite.repository.FavoriteRepository;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipSummaryResponse;
import com.grantip.backend.domain.scholarship.domain.entity.Scholarship;
import com.grantip.backend.domain.scholarship.mapper.ScholarshipMapper;
import com.grantip.backend.domain.user.domain.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {
  private final FavoriteRepository favoriteRepository;
  private final ScholarshipMapper scholarshipMapper;

  /**
   * 장학금 좋아요 등록 또는 취소
   *
   * @param scholarship 장학금
   * @param user  사용자
   * @return 좋아요 등록/취소 후의 상태 (등록: true, 취소: false)
   */
  public boolean toggleFavorite(Scholarship scholarship, User user){
    Optional<FavoriteScholar> existingFavorite = favoriteRepository.findByScholarshipAndUser(scholarship, user);

    if(existingFavorite.isPresent()){
      favoriteRepository.delete(existingFavorite.get());
      return false;
    }
    else{
      favoriteRepository.save(FavoriteScholar.builder()
          .scholarship(scholarship)
          .user(user)
          .build()
      );
      return true;
    }
  }

  /**
   * 장학금 좋아요 목록 조회
   *
   * @param user  사용자
   * @param pageable pageable
   * @return 좋아요한 리스트 페이지
   */
  public Page<ScholarshipSummaryResponse> findFavoritedScholarships(User user, Pageable pageable){
    return favoriteRepository.findByUser(user, pageable)
        .map(FavoriteScholar::getScholarship)
        .map(scholarshipMapper::toSummaryResponse);
  }
}
