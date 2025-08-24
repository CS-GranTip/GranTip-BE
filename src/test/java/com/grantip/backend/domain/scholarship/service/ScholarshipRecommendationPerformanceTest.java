package com.grantip.backend.domain.scholarship.service;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.grantip.backend.domain.region.domain.entity.Region;
import com.grantip.backend.domain.region.repository.RegionRepository;
import com.grantip.backend.domain.scholarship.domain.constant.QualificationCode;
import com.grantip.backend.domain.scholarship.domain.entity.UniversityCategory;
import com.grantip.backend.domain.scholarship.event.RecommendationCalculateEvent;
import com.grantip.backend.domain.scholarship.event.RecommendationRefreshEvent;
import com.grantip.backend.domain.scholarship.repository.UniversityCategoryRepository;
import com.grantip.backend.domain.user.domain.constant.Gender;
import com.grantip.backend.domain.user.domain.constant.Role;
import com.grantip.backend.domain.user.domain.constant.UnivYear;
import com.grantip.backend.domain.user.domain.entity.User;
import com.grantip.backend.domain.user.domain.entity.UserExtraInfo;
import com.grantip.backend.domain.user.repository.UserRepository;
import com.grantip.backend.global.code.ErrorCode;
import com.grantip.backend.global.exception.CustomException;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class ScholarshipRecommendationPerformanceTest {
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RegionRepository regionRepository;

  @Autowired
  private UniversityCategoryRepository universityCategoryRepository;

  @Autowired
  private ApplicationEventPublisher eventPublisher;

  @Autowired
  private ScholarshipRecommendationService scholarshipRecommendationService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PlatformTransactionManager transactionManager;

  private TransactionTemplate transactionTemplate;

  private static final String TEST_USER_IDENTIFIER = "testUser@naver.com";
  private static final String CACHE_KEY = "recommendations::" + TEST_USER_IDENTIFIER;
  private static final String LOCK_KEY = "recommendations:lock:" + TEST_USER_IDENTIFIER;

  @BeforeEach
  public void setup() {
    transactionTemplate = new TransactionTemplate(transactionManager);

    // 관련 데이터 제거
    transactionTemplate.execute(status -> {
      userRepository.findByEmail(TEST_USER_IDENTIFIER).ifPresent(userRepository::delete);
      return null;
    });
    // 캐시와 락 제거
    redisTemplate.delete(List.of(CACHE_KEY, LOCK_KEY));

    // 테스트 유저 생성
    createTestUser();
  }

  @AfterEach
  void tearDown() {
    // 테스트 후 생성된 데이터 정리
    transactionTemplate.execute(status -> {
      userRepository.findByEmail(TEST_USER_IDENTIFIER).ifPresent(userRepository::delete);
      return null;
    });

    // 캐시와 락 제거
    redisTemplate.delete(List.of(CACHE_KEY, LOCK_KEY));
  }

  private void createTestUser() {
    // TransactionTemplate을 사용하여 DB 작업을 별도 트랜잭션으로 커밋
    transactionTemplate.execute(status -> {
      Region testRegion = regionRepository.findById(988L)
          .orElseThrow(() -> new CustomException(ErrorCode.REGION_NOT_FOUND));

      UniversityCategory testUnivCategory = universityCategoryRepository.findById(1L)
          .orElseThrow(() -> new CustomException(ErrorCode.UNIVERSITY_CATEGORY_NOT_FOUND));


      // 테스트용 User 및 UserExtraInfo 생성
      UserExtraInfo extraInfo = UserExtraInfo.builder()
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

      User testUser = User.builder()
          .email(TEST_USER_IDENTIFIER)
          .username("performance-tester")
          .password("password")
          .currentSchool("세종대학교")
          .universityYear(UnivYear.EIGHTH_SEMESTER_OR_ABOVE)
          .phone("01012345678")
          .role(Role.USER)
          .active(true)
          .gender(Gender.FEMALE)
          .address(testRegion)
          .residentAddress(testRegion)
          .universityCategory(testUnivCategory)
          .extraInfo(extraInfo)
          .build();

      extraInfo.setUser(testUser);
      userRepository.saveAndFlush(testUser);
      return null;
    });
  }

  private void warmUp(){
    System.out.println("--- JVM 웜업을 시작합니다. ---");
    for(int i=0; i<10; i++){
      scholarshipRecommendationService.calculateRecommendations(TEST_USER_IDENTIFIER);
    }
    System.out.println("--- 웜업 완료. ---");

    redisTemplate.delete(CACHE_KEY);
  }

  @Test
  void 신규_사용자_로그인_시_백그라운드_계산_성능_테스트(){
    // 웜업 실행
    warmUp();

    // 로그인 시 추천 계산 이벤트 발행
    long startTime = System.currentTimeMillis();
    eventPublisher.publishEvent(new RecommendationCalculateEvent(TEST_USER_IDENTIFIER));

    // 일정 시간 내 캐시 생성 확인
    Awaitility.await()
        .atMost(Duration.ofSeconds(10))
        .pollInterval(Duration.ofMillis(200))
        .until(() -> redisTemplate.opsForValue().get(CACHE_KEY) != null);

    long duration = System.currentTimeMillis() - startTime;
    System.out.printf("[성능 테스트 1] 백그라운드 계산 소요 시간: %d ms%n", duration);
  }

  @Test
  void API_캐시_조회_시_사용자_체감_응답_속도_테스트() throws Exception {
    // 웜업 실행
    warmUp();

    // 캐시 생성
    scholarshipRecommendationService.calculateRecommendations(TEST_USER_IDENTIFIER);

    // 추천 API 호출
    long startTime = System.currentTimeMillis();
    mockMvc.perform(get("/api/scholarships/recommendation")
        .with(user(TEST_USER_IDENTIFIER)))
        .andExpect(status().isOk());

    long duration = System.currentTimeMillis() - startTime;
    System.out.printf("[성능 테스트 2] API 캐시 조회 응답 시간: %d ms%n", duration);
  }

  @Test
  void 사용자_정보_업데이트_시_캐시_갱신_성능_테스트() {
    // 웜업 실행
    warmUp();

    // 이전 캐시 더미 데이터 저장
    List<String> DUMMY_VALUE = List.of("DUMMY");
    redisTemplate.opsForValue().set(CACHE_KEY, DUMMY_VALUE);


    long startTime = System.currentTimeMillis();
    eventPublisher.publishEvent(new RecommendationRefreshEvent(TEST_USER_IDENTIFIER));

    Awaitility.await()
        .atMost(Duration.ofSeconds(10))
        .pollInterval(Duration.ofMillis(200))
        .until(() -> {
          Object newCacheValue = redisTemplate.opsForValue().get(CACHE_KEY);
          return newCacheValue != null && !newCacheValue.equals(DUMMY_VALUE);
        });

    long duration = System.currentTimeMillis() - startTime;
    System.out.printf("[성능 테스트 3] 캐시 갱신 소요 시간: %d ms%n", duration);
  }

  @Test
  void 기존_사용자_로그인_시_불필요한_계산_방지_테스트() throws Exception {
    // 캐시 생성
    scholarshipRecommendationService.calculateRecommendations(TEST_USER_IDENTIFIER);

    // 기존 사용자가 다시 로그인하여 계산 이벤트 발생
    eventPublisher.publishEvent(new RecommendationCalculateEvent(TEST_USER_IDENTIFIER));

    // Lock Key 생성 여부를 통해 실제 계산 로직 실행되지 않는지 검증
    Awaitility.await()
        .during(Duration.ofSeconds(2)) // 2초 동안
        .atMost(Duration.ofSeconds(3)) // 최대 3초까지 지켜보면서
        .with()
        .pollInterval(Duration.ofMillis(100))
        .until(() -> !redisTemplate.hasKey(LOCK_KEY)); // Lock Key가 없는 상태가 유지되어야 함

    System.out.println("[성능 테스트 4] 캐시 존재 시, 계산 Lock이 생성되지 않음을 확인했습니다.");
  }
}
