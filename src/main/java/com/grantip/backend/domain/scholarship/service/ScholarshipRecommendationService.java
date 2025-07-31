package com.grantip.backend.domain.scholarship.service;

import com.grantip.backend.domain.scholarship.domain.constant.GradeCriterionType;
import com.grantip.backend.domain.scholarship.domain.constant.QualificationCode;
import com.grantip.backend.domain.scholarship.domain.dto.response.RecommendedScholarshipResponse;
import com.grantip.backend.domain.scholarship.domain.entity.GeneralCriterion;
import com.grantip.backend.domain.scholarship.domain.entity.GradeCriterion;
import com.grantip.backend.domain.scholarship.domain.entity.IncomeCriterion;
import com.grantip.backend.domain.scholarship.domain.entity.Scholarship;
import com.grantip.backend.domain.scholarship.mapper.ScholarshipMapper;
import com.grantip.backend.domain.scholarship.repository.ScholarshipRepositoryCustom;
import com.grantip.backend.domain.user.domain.constant.UnivYear;
import com.grantip.backend.domain.user.domain.entity.User;
import com.grantip.backend.domain.user.domain.entity.UserExtraInfo;
import com.grantip.backend.domain.user.service.UserService;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScholarshipRecommendationService {
  private final UserService userService;
  private final ScholarshipRepositoryCustom scholarshipRepositoryCustom;
  private final ScholarshipMapper scholarshipMapper;
  private static final double MINIMUM_FILTER_SCORE = 3000.0;

  /**
   * 실제 추천 로직을 수행하고 결과를 캐싱하는 메서드
   */
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "recommendations", key = "#identifier")
  public List<RecommendedScholarshipResponse> calculateRecommendations(String identifier){
    log.info("사용자 추천 계산 시작: {}", identifier);
    User user = userService.findByEmail(identifier);
    UserExtraInfo userInfo = user.getExtraInfo();

    // 1차 필터링
    List<Scholarship> candidates = scholarshipRepositoryCustom.findFilteredScholarships(user, userInfo);
    log.info("사용자: {}, 1차 필터링(DB) 후보군: {}건", identifier, candidates.size());

    // 2차 필터링 - 복잡한 규칙 메모리에서 처리
    List<RecommendedScholarshipResponse> result = candidates.stream()
        .map(scholarship -> {
          double filterScore = calculateFilterScore(scholarship, user, userInfo);
          double preferenceScore = calculatePreferenceScore(scholarship, user, userInfo);

          // 최소 필터링 점수를 넘긴 장학금만 최종 후보로 선택
          if(filterScore >= MINIMUM_FILTER_SCORE){
            double finalScore = filterScore + preferenceScore;
            log.debug("최종 점수 계산 >> 장학금 ID: {}, 필터 점수: {}, 가산점: {}, 최종 점수: {}",
                scholarship.getId(), filterScore, preferenceScore, finalScore);
            return mapToRecommendedResponse(scholarship, finalScore);
          }
          return null;
        })
        .filter(Objects::nonNull) // 탈락한 null 객체 제거
        .sorted(Comparator.comparingDouble(RecommendedScholarshipResponse::getScore).reversed())
        .toList();

    log.info("사용자 추천 계산 완료: {}. 최종 추천 수: {}건", identifier, result.size());
    return result;
  }

  /**
   * Scholarship 객체를 RecommendedScholarshipResponse DTO로 변환, 점수를 계산하여 포함
   */
  private RecommendedScholarshipResponse mapToRecommendedResponse(Scholarship scholarship, double score){
    return scholarshipMapper.toRecommendedResponse(scholarship, score);
  }

  /**
   * 2차 필터링: 각 항목을 점수로 변환하여 총점을 계산
   * @return 필수 조건들의 만족도를 합산한 점수
   */
  private double calculateFilterScore(Scholarship scholarship, User user, UserExtraInfo userInfo){
    double totalFilterScore = 0;

    totalFilterScore += getSchoolRequirementScore(scholarship, user);
    totalFilterScore += getGradeRequirementScore(scholarship, user, userInfo);
    totalFilterScore += getIncomeRequirementScore(scholarship, userInfo);
    totalFilterScore += getGeneralRequirementScore(scholarship, userInfo);

    return totalFilterScore;
  }

  /**
   * 학교명 검사를 위한 관련 상세내용 텍스트를 결합하는 헬퍼 메서드
   */
  private String getFullTextForSchoolCheck(Scholarship scholarship){
    return String.join(" ",
        scholarship.getSpecificQualificationDetail() != null ? scholarship.getSpecificQualificationDetail() : "",
        scholarship.getQualificationRestrictionDetail() != null ? scholarship.getQualificationRestrictionDetail() : "",
        scholarship.getGradeCriteriaDetail() != null ? scholarship.getGradeCriteriaDetail() : ""
    );
  }

  /**
   * 단일 성적 기준(GradeCriterion)을 사용자가 만족하는지 확인하는 헬퍼 메서드
   */
  private boolean userMeetGradeCriterion(GradeCriterion criterion, User user, UserExtraInfo userInfo){
    // 사용자의 학적 상태 확인 (FRESHMAN 또는 그 외)
    boolean isFreshman = user.getUniversityYear() == UnivYear.FRESHMAN;

    // 장학금 조건의 대상 그룹과 사용자 그룹이 맞지 않으면 통과
    if(("신입생".equals(criterion.getGroup()) && !isFreshman) ||
        ("재학생".equals(criterion.getGroup()) && isFreshman)){
      return true;
    }

    // 조건 유형에 따라 분기
    switch(criterion.getType()){
      case GPA:
        return checkGpaCriterion(criterion, userInfo);
      case CREDITS:
        return checkCreditsCriterion(criterion, userInfo);
      case RANK:
        return checkRankCriterion(criterion, userInfo);
      case ETC:
        return checkEtcCriterion(criterion, userInfo);
      default:
        return true;
    }
  }

  /**
   * GPA(평균 평점) 조건 검사
   */
  private boolean checkGpaCriterion(GradeCriterion criterion, UserExtraInfo userInfo){
    if(userInfo.getGpaScale() == null) return false; // 사용자가 성적 기준을 입력 안 했으면 false

    Double userGpa = null;
    // 기준 학기(semester)에 따라 사용자 성적 선택
    switch(criterion.getSemester()){
      case LAST:
        userGpa = userInfo.getPreviousSemesterGpa();
        break;
      case LAST2:
        if (userInfo.getPreviousSemesterGpa() != null && userInfo.getTwoSemestersAgoGpa() != null) {
          userGpa = (userInfo.getPreviousSemesterGpa() + userInfo.getTwoSemestersAgoGpa()) / 2.0;
        }
        break;
      case AVG:
        userGpa = userInfo.getOverallGpa();
        break;
    }

    if(userGpa == null) return false; // 사용자가 해당 학기 성적을 입력 안 했으면 false

    // 사용자의 성적 기준에 맞는 장학금 조건 점수 선택
    Double requiredGpa = (userInfo.getGpaScale() == 4.5) ? criterion.getScore5() : criterion.getScore3();
    if(requiredGpa == null || requiredGpa == 0.0) return true; // 장학금에 해당 기준 점수 조건이 없으면 통과

    return userGpa >= requiredGpa;
  }

  /**
   * 이수 학점 조건 검사
   */
  private boolean checkCreditsCriterion(GradeCriterion criterion, UserExtraInfo userInfo){
    Integer userCredits = null;
    Integer requiredCredits = criterion.getCredits();

    if(requiredCredits == null || requiredCredits == 0) return true; // 조건이 없으면 통과

    // 기준 학기에 따라 사용자 이수 학점 선택
    switch(criterion.getSemester()){
      case LAST:
        userCredits = userInfo.getPreviousSemesterCredits();
        break;
      case LAST2:
        if (userInfo.getPreviousSemesterCredits() != null && userInfo.getTwoSemestersAgoCredits() != null) {
          userCredits = userInfo.getPreviousSemesterCredits() + userInfo.getTwoSemestersAgoCredits();
        }
        break;
    }

    if(userCredits == null) return false; // 사용자가 이수 학점을 입력 안 했으면 false

    return userCredits >= requiredCredits;
  }

  /**
   * 등급/석차 조건 검사 (주로 신입생의 내신, 수능)
   */
  private boolean checkRankCriterion(GradeCriterion criterion, UserExtraInfo userInfo){
    Double userGrade = null;
    Double requiredRank = criterion.getRank();
    String keyword = criterion.getKeyword();

    if(requiredRank == null) return true; // 조건이 없으면 통과

    if ("내신".equals(keyword)) {
      userGrade = userInfo.getHighSchoolGrade();
    } else if ("수능".equals(keyword)) {
      userGrade = userInfo.getSatAverageGrade();
    }

    if(userGrade == null) return false; // 사용자가 성적을 입력 안 했으면 false

    return userGrade <= requiredRank;
    // TODO: keyword에 "등급"으로 저장되는 데이터들 python에서 처리 & 과목별 등급 처리 고려
  }

  /**
   * 기타("성적 우수자" 등) 조건 검사
   */
  private boolean checkEtcCriterion(GradeCriterion criterion, UserExtraInfo userInfo){
    /*
    String keyword = criterion.getKeyword();
    if(keyword != null && keyword.contains("우수")){
      // '성적 우수' - 전체 평점 4.0/4.5 (3.8/4.3) 이상인 경우를 '우수'로 판단
      if(userInfo.getOverallGpa() == null || userInfo.getGpaScale() == null) return false;

      if(userInfo.getGpaScale() == 4.5 && userInfo.getOverallGpa() >= 4.0) return true;
      if(userInfo.getGpaScale() == 4.3 && userInfo.getOverallGpa() >= 3.8) return true;

      return false;
    }
    */
    return true; // '우수' 키워드가 없는 기타 조건은 일단 통과
  }

  /**
   * 단일 소득 기준(IncomeCriterion)을 사용자가 만족하는지 확인하는 헬퍼 메서드
   */
  private boolean userMeetIncomeCriterion(IncomeCriterion criterion, UserExtraInfo userInfo){
    // 필수 자격 조건 검사
    List<QualificationCode> requiredQualifications = criterion.getRequiredQualifications();
    if(requiredQualifications != null && !requiredQualifications.isEmpty()){
      // 사용자가 필수 자격 코드를 모두 가지고 있는지 확인
      if(!userInfo.getQualificationCodes().containsAll(requiredQualifications)){
        return false;
      }
    }

    // 소득 기준 면제 여부 확인
    if(criterion.isIgnoreIncomeAndAssets()){
      return true; // 소득 무관 조건이므로 통과
    }

    // 학자금 지원 구간
    if(criterion.getScholarshipSupportInterval() != null){
      if (userInfo.getScholarshipSupportInterval() == null ||
          userInfo.getScholarshipSupportInterval() > criterion.getScholarshipSupportInterval()) {
        return false;
      }
    }

    // 소득 분위
    if(criterion.getIncomePercentileBand() != null){
      if(userInfo.getIncomePercentileBand() == null ||
          userInfo.getIncomePercentileBand() > criterion.getIncomePercentileBand()){
        return false;
      }
    }

    // 기준 중위소득 비율
    if(criterion.getMedianIncomeRatio() != null){
      if(userInfo.getMedianIncomeRatio() == null ||
          userInfo.getMedianIncomeRatio() > criterion.getMedianIncomeRatio()){
        return false;
      }
    }

    return true;
  }

  /**
   * 사용자가 장학금의 학교명 필수 조건을 만족하는지 검사
   * @return 필수 조건이지만 사용자의 학교가 아닌 경우 1000점
   */
  private double getSchoolRequirementScore(Scholarship scholarship, User user){
    String fullText = getFullTextForSchoolCheck(scholarship);
    String userSchool = user.getCurrentSchool();

    if(StringUtils.hasText(fullText) && fullText.contains("대학교") && !fullText.contains(userSchool)){
      if(fullText.contains("에 한함") || fullText.contains("입학생")){
        return 0; // 필수 조건 불일치 시 0점
      }
    }
    return 1000; // 학교 조건 통과 시 1000점
  }

  /**
   * 사용자가 장학금의 성적 조건을 만족하는지 검사
   * @return 모든 성적 조건 통과 시 1000점
   */
  private double getGradeRequirementScore(Scholarship scholarship, User user, UserExtraInfo userInfo){
    Set<GradeCriterion> criteria = scholarship.getGradeCriteria();
    if(criteria == null || criteria.isEmpty()) return 1000;

    for(GradeCriterion criterion : criteria){
      if(!userMeetGradeCriterion(criterion, user, userInfo)){
        return 0; // 완전히 미달이면 0점
      }
    }
    return 1000; // 모든 성적 조건 통과 시 1000점
  }

  /**
   * 사용자가 장학금의 소득 조건을 만족하는지 검사
   * @return 모든 소득 조건 통과 시 1000점
   */
  private double getIncomeRequirementScore(Scholarship scholarship, UserExtraInfo userInfo){
    Set<IncomeCriterion> criteria = scholarship.getIncomeCriteria();
    if(criteria == null || criteria.isEmpty()) return 1000;

    for(IncomeCriterion criterion : criteria){
      if(userMeetIncomeCriterion(criterion, userInfo)){
        return 1000; // 하나라도 만족하면 1000점
      }
    }
    return 0; // 모두 만족 못하면 0점
  }

  /**
   * 사용자가 장학금의 일반 자격 조건을 만족하는지 검사
   * @return 모든 필수 자격 조건 통과 시 1000점
   */
  private double getGeneralRequirementScore(Scholarship scholarship, UserExtraInfo userInfo){
    Set<GeneralCriterion> criteria = scholarship.getGeneralCriteria();
    if(criteria == null || criteria.isEmpty()) return 1000;

    for(GeneralCriterion criterion : criteria){
      List<QualificationCode> required = criterion.getRequiredQualifications();
      if(required != null && !required.isEmpty()){
        if(userInfo.getQualificationCodes() == null || !userInfo.getQualificationCodes().containsAll(required)){
          return 0; // 필수 자격 없으면 0점
        }
      }
    }
    return 1000; // 모든 필수 자격 통과 시 1000점
  }

  /**
   * 스코어링: 필터링된 장학금에 대해 '우대/선호도' 점수 계산
   * @return 우대 조건들을 만족할수록 높은 가산점
   */
  private double calculatePreferenceScore(Scholarship scholarship, User user, UserExtraInfo userInfo){
    double score = 0.0;
    score += addSchoolAffinityScore(scholarship, user);
    score += addPreferenceQualificationScore(scholarship, userInfo);
    score += addGpaMarginScore(scholarship, userInfo);
    score += addIncomeLevelScore(scholarship, userInfo);
    score += addGradeEtcScore(scholarship, userInfo);
    return score;
  }

  /**
   * 스코어링: 학교명 언급 및 우대 여부에 따라 점수를 부여
   */
  private double addSchoolAffinityScore(Scholarship scholarship, User user){
    double schoolScore = 0.0;
    String userSchool = user.getCurrentSchool();
    if(!StringUtils.hasText(userSchool)){
      return 0.0;
    }

    String fullText = getFullTextForSchoolCheck(scholarship);

    if(StringUtils.hasText(fullText) && fullText.contains(userSchool)){
      schoolScore += 100; // 특정 학교 언급 시 높은 가산점
      if(fullText.contains("우대") || fullText.contains("우선")){
        schoolScore += 50; // '우대' 키워드 있으면 추가 가산점
      }
    }
    return schoolScore;
  }

  /**
   * 스코어링: 우대 자격 조건 만족 개수에 따라 점수를 부여
   */
  private double addPreferenceQualificationScore(Scholarship scholarship, UserExtraInfo userInfo){
    double qualificationScore = 0.0;
    Set<QualificationCode> userQualifications = userInfo.getQualificationCodes();
    if(userQualifications == null || userQualifications.isEmpty()){
      return 0.0;
    }

    for(GeneralCriterion criterion : scholarship.getGeneralCriteria()){
      List<QualificationCode> preferences = criterion.getPreferenceQualifications();
      if(preferences != null){
        for(QualificationCode pref : preferences){
          if(userQualifications.contains(pref)){
            qualificationScore += 50; // 우대 자격 1개당 50점
          }
        }
      }
    }
    return qualificationScore;
  }

  /**
   * 스코어링: 사용자 성적이 최소 요구 성적보다 높은 만큼 점수를 부여
   */
  private double addGpaMarginScore(Scholarship scholarship, UserExtraInfo userInfo){
    double gpaScore = 0.0;
    // GPA 조건 찾기
    GradeCriterion gpaCriterion = scholarship.getGradeCriteria().stream()
        .filter(c -> c.getType() == GradeCriterionType.GPA && c.getSemester() != null)
        .findFirst().orElse(null);

    if(gpaCriterion == null || userInfo.getGpaScale() == null){
      return 0.0;
    }

    Double userGpa = switch (gpaCriterion.getSemester()){
      case LAST -> userInfo.getPreviousSemesterGpa();
      case LAST2 -> (userInfo.getPreviousSemesterGpa() != null && userInfo.getTwoSemestersAgoGpa() != null)
          ? (userInfo.getPreviousSemesterGpa() + userInfo.getTwoSemestersAgoGpa()) / 2.0 : null;
      case AVG -> userInfo.getOverallGpa();
      default -> null;
    };

    if(userGpa == null) return 0.0;

    Double requiredGpa = (userInfo.getGpaScale() == 4.5) ? gpaCriterion.getScore5() : gpaCriterion.getScore3();
    if(requiredGpa != null && requiredGpa > 0 && userGpa > requiredGpa){
      gpaScore += (userGpa - requiredGpa) * 50; // 최소 기준 초과 점수 1점당 50점
    }

    return gpaScore;
  }

  /**
   * 스코어링: 사용자의 소득분위가 기준보다 낮을수록 높은 점수를 부여
   * 여러 소득 기준 중 사용자에게 가장 유리한 점수를 찾아 반환
   */
  private double addIncomeLevelScore(Scholarship scholarship, UserExtraInfo userInfo){
    double maxIncomeScore = 0.0;

    for(IncomeCriterion criterion : scholarship.getIncomeCriteria()){
      double currentCriterionScore = 0.0;

      // 학자금 지원 구간 점수
      if (criterion.getScholarshipSupportInterval() != null && userInfo.getScholarshipSupportInterval() != null) {
        int userInterval = userInfo.getScholarshipSupportInterval();
        int maxInterval = criterion.getScholarshipSupportInterval();
        if(userInterval <= maxInterval){
          currentCriterionScore += (maxInterval - userInterval) * 20.0; // 1구간 차이마다 20점
        }
      }

      // 소득 분위 점수
      if(criterion.getIncomePercentileBand() != null && userInfo.getIncomePercentileBand() != null){
        int userBand = userInfo.getIncomePercentileBand();
        int maxBand = criterion.getIncomePercentileBand();
        if(userBand <= maxBand){
          currentCriterionScore += (maxBand - userBand) * 20.0; // 1분위 차이마다 20점
        }
      }

      // 기준 중위소득 비율 점수
      if(criterion.getMedianIncomeRatio() != null && userInfo.getMedianIncomeRatio() != null){
        int userRatio = userInfo.getMedianIncomeRatio();
        int maxRatio = criterion.getMedianIncomeRatio();
        if(userRatio <= maxRatio){
          currentCriterionScore += (maxRatio - userRatio) * 1.0; // 1% 차이마다 1점
        }
      }

      // 지금까지의 최고점보다 높으면 갱신
      if(currentCriterionScore > maxIncomeScore){
        maxIncomeScore = currentCriterionScore;
      }
    }

    return maxIncomeScore;
  }

  /**
   * 스코어링: 성적 조건 중 ETC (예: '성적 우수자') 타입에 대해 성적이 높을 수록 높은 점수를 부여
   */
  private double addGradeEtcScore(Scholarship scholarship, UserExtraInfo userInfo){
    double etcScore = 0.0;
    for(GradeCriterion criterion : scholarship.getGradeCriteria()){
      if(criterion.getType() == GradeCriterionType.ETC && criterion.getKeyword() != null){
        if(criterion.getKeyword().contains("우수")){
          // 사용자의 전체 평점이 최상위권일 경우 높은 점수 부여
          if(isGpaExcellent(userInfo)){
            etcScore += 150;
          }
        }
      }
    }

    return etcScore;
  }

  /**
   * 최상위 성적임을 판별하는 헬퍼 메서드
   */
  private boolean isGpaExcellent(UserExtraInfo userInfo) {
    if(userInfo.getOverallGpa() == null || userInfo.getGpaScale() == null) return false;
    return (userInfo.getGpaScale() == 4.5 && userInfo.getOverallGpa() >= 4.0) ||
           (userInfo.getGpaScale() == 4.3 && userInfo.getOverallGpa() >= 3.8);
  }
}
