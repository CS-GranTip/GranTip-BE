package com.grantip.backend.domain.favorite.controller;

import com.grantip.backend.domain.favorite.domain.dto.request.FavoriteScholarshipRequest;
import com.grantip.backend.domain.favorite.service.FavoriteService;
import com.grantip.backend.domain.scholarship.domain.dto.response.ScholarshipSummaryResponse;
import com.grantip.backend.domain.scholarship.service.ScholarshipService;
import com.grantip.backend.domain.user.service.UserService;
import com.grantip.backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {
  private final FavoriteService favoriteService;
  private final ScholarshipService scholarshipService;
  private final UserService userService;

  // 좋아요 등록 or 취소
  @PostMapping("/{scholarshipId}")
  public ResponseEntity<ApiResponse<Boolean>> toggleFavorite(
      @AuthenticationPrincipal UserDetails userDetails,
      @PathVariable Long scholarshipId){

    boolean isFavorited = favoriteService.toggleFavorite(
        scholarshipService.findById(scholarshipId),
        userService.findByEmail(userDetails.getUsername())
    );

    String message = isFavorited ? "좋아요 등록에 성공했습니다." : "좋아요 취소에 성공했습니다.";

    return ResponseEntity.ok(
        ApiResponse.
            <Boolean>builder()
            .success(true)
            .code(200)
            .result(isFavorited)
            .message(message)
            .build()
      );
    }

  // 사용자의 좋아요 목록 조회
  @GetMapping
  public ResponseEntity<ApiResponse<Page<ScholarshipSummaryResponse>>> getFavoritedScholarships(
      @AuthenticationPrincipal UserDetails userDetails,
      @ParameterObject @Valid FavoriteScholarshipRequest request){
    return ResponseEntity.ok(
      ApiResponse
          .<Page<ScholarshipSummaryResponse>>builder()
          .success(true)
          .code(200)
          .result(favoriteService.findFavoritedScholarships(userService.findByEmail(userDetails.getUsername()), request.toPageable()))
          .message("좋아요한 장학금 목록 조회에 성공했습니다.")
          .build()
    );
  }
}
