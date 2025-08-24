package com.grantip.backend.domain.scholarship.service;

import com.grantip.backend.domain.region.domain.entity.Region;
import com.grantip.backend.domain.region.repository.RegionRepository;
import com.grantip.backend.domain.scholarship.domain.constant.QualificationCode;
import com.grantip.backend.domain.scholarship.domain.entity.UniversityCategory;
import com.grantip.backend.domain.scholarship.repository.UniversityCategoryRepository;
import com.grantip.backend.domain.user.domain.constant.Gender;
import com.grantip.backend.domain.user.domain.constant.Role;
import com.grantip.backend.domain.user.domain.constant.UnivYear;
import com.grantip.backend.domain.user.domain.entity.User;
import com.grantip.backend.domain.user.domain.entity.UserExtraInfo;
import com.grantip.backend.domain.user.repository.UserRepository;
import com.grantip.backend.global.code.ErrorCode;
import com.grantip.backend.global.exception.CustomException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Set;

@SpringBootTest
@ActiveProfiles("dev")
public class ScholarshipRecommendationServiceTest {
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RegionRepository regionRepository;

  @Autowired
  private UniversityCategoryRepository universityCategoryRepository;

  @Autowired
  private ScholarshipRecommendationService scholarshipRecommendationService;

  @Autowired
  private PlatformTransactionManager transactionManager;

  private TransactionTemplate transactionTemplate;

  private static final String EMAIL_WITH_EXTRA    = "perf_extra@naver.com";
  private static final String EMAIL_WITHOUT_EXTRA = "perf_noextra@naver.com";

  private static String cacheKey(String email) {
    return "recommendations::" + email;
  }

  @BeforeEach
  public void setup() {
    transactionTemplate = new TransactionTemplate(transactionManager);

    // 캐시와 락 제거
    redisTemplate.delete(cacheKey(EMAIL_WITH_EXTRA));
    redisTemplate.delete(cacheKey(EMAIL_WITHOUT_EXTRA));

    // TransactionTemplate을 사용하여 DB 작업을 별도 트랜잭션으로 커밋
    transactionTemplate.execute(status -> {
      userRepository.findByEmail(EMAIL_WITH_EXTRA).ifPresent(userRepository::delete);
      userRepository.findByEmail(EMAIL_WITHOUT_EXTRA).ifPresent(userRepository::delete);
      return null;
    });
  }

  @AfterEach
  void tearDown() {
    // 캐시/데이터 정리
    redisTemplate.delete(cacheKey(EMAIL_WITH_EXTRA));
    redisTemplate.delete(cacheKey(EMAIL_WITHOUT_EXTRA));
    transactionTemplate.execute(status -> {
      userRepository.findByEmail(EMAIL_WITH_EXTRA).ifPresent(userRepository::delete);
      userRepository.findByEmail(EMAIL_WITHOUT_EXTRA).ifPresent(userRepository::delete);
      return null;
    });
  }

  /**
   * 유틸: 테스트용 사용자 생성 (extraInfo 포함/미포함 선택)
   */
  private void createTestUser(String email, boolean withExtra) {
    Region region = regionRepository.findById(988L)
        .orElseThrow(() -> new CustomException(ErrorCode.REGION_NOT_FOUND));
    UniversityCategory univCat = universityCategoryRepository.findById(1L)
        .orElseThrow(() -> new CustomException(ErrorCode.UNIVERSITY_CATEGORY_NOT_FOUND));

    transactionTemplate.execute(s -> {
      UserExtraInfo extraInfo = null;
      if (withExtra) {
        extraInfo = UserExtraInfo.builder()
            .gpaScale(4.5)
            .overallGpa(3.8)
            .previousSemesterGpa(4.0)
            .previousSemesterCredits(18)
            .twoSemestersAgoGpa(4.2)
            .twoSemestersAgoCredits(21)
            .highSchoolGrade(2.0)
            .satAverageGrade(2.0)
            .scholarshipSupportInterval(3)
            .incomePercentileBand(3)
            .medianIncomeRatio(120)
            .qualificationCodes(Set.of(QualificationCode.LOW_INCOME, QualificationCode.MULTI_CHILD))
            .build();
      }

      User user = User.builder()
          .email(email)
          .username("perf-tester")
          .password("password")
          .currentSchool("세종대학교")
          .universityYear(UnivYear.EIGHTH_SEMESTER_OR_ABOVE)
          .phone("01012345678")
          .role(Role.USER)
          .active(true)
          .gender(Gender.FEMALE)
          .address(region)
          .residentAddress(region)
          .universityCategory(univCat)
          .extraInfo(extraInfo)
          .build();

      if (extraInfo != null) {
        extraInfo.setUser(user);
      }

      userRepository.saveAndFlush(user);
      return null;
    });
  }

  private void warmUp(){
    System.out.println("--- JVM 웜업을 시작합니다. ---");
    for(int i=0; i<10; i++){
      scholarshipRecommendationService.calculateRecommendations(EMAIL_WITH_EXTRA);
    }
    System.out.println("--- 웜업 완료. ---");

    redisTemplate.delete(cacheKey(EMAIL_WITH_EXTRA));
  }

  @Test
  void 추천_계산_로직_소요_시간_테스트(){
    // UserExtraInfo 있는 사용자
    createTestUser(EMAIL_WITH_EXTRA, true);

    // 웜업 실행
    warmUp();

    long startTime =  System.currentTimeMillis();
    var result = scholarshipRecommendationService.calculateRecommendations(EMAIL_WITH_EXTRA);
    long duration = System.currentTimeMillis() - startTime;

    System.out.printf("[추천 로직 테스트 1] calculateRecommendations 소요 시간: %d ms, 결과수=%d%n",
        duration, (result != null ? result.size() : -1));
  }

  @Test
  void 사용자정보_유무에_따른_실행_시간_비교(){
    // User만 존재하는 경우
    createTestUser(EMAIL_WITHOUT_EXTRA, false);
    // User + ExtraInfo 존재하는 경우
    createTestUser(EMAIL_WITH_EXTRA, true);

    // 웜업 실행
    warmUp();

    long startTime1 = System.currentTimeMillis();
    var result1 = scholarshipRecommendationService.calculateRecommendations(EMAIL_WITH_EXTRA);
    long duration1 = System.currentTimeMillis() - startTime1;

    long startTime2 = System.currentTimeMillis();
    var result2 = scholarshipRecommendationService.calculateRecommendations(EMAIL_WITHOUT_EXTRA);
    long duration2 = System.currentTimeMillis() - startTime2;

    long diff = duration2 - duration1;

    System.out.printf("[추천 로직 테스트 2] User+ExtraInfo 존재: %d ms (결과수=%d), User만 존재: %d ms (결과수=%d), 차이: %d ms%n",
        duration1, (result1 != null ? result1.size() : -1),
        duration2, (result2 != null ? result2.size() : -1),
        diff);

  }
}
