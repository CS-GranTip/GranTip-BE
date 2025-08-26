package com.grantip.backend.domain.user.service;

import com.grantip.backend.domain.region.service.RegionService;
import com.grantip.backend.domain.scholarship.event.RecommendationRefreshEvent;
import com.grantip.backend.domain.scholarship.service.UniversityCategoryService;
import com.grantip.backend.domain.user.domain.dto.CustomUserDetails;
import com.grantip.backend.domain.user.domain.dto.request.UpdateRequest;
import com.grantip.backend.domain.user.domain.constant.Role;
import com.grantip.backend.domain.user.domain.dto.response.MyPageResponse;
import com.grantip.backend.domain.user.domain.dto.response.UserResponse;
import com.grantip.backend.domain.user.domain.entity.User;
import com.grantip.backend.domain.user.domain.entity.UserExtraInfo;
import com.grantip.backend.domain.user.repository.UserRepository;
import com.grantip.backend.global.code.ErrorCode;
import com.grantip.backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UniversityCategoryService universityCategoryService;
    private final RegionService regionService;
    private final ApplicationEventPublisher applicationEventPublisher;


    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public List<User> findAdmins(){
        return userRepository.findByRole(Role.ADMIN);
    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public CustomUserDetails loadUserDetailsByEmail(String email) {
        return userRepository.findByEmail(email)
                .<CustomUserDetails>map(CustomUserDetails::new)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteUser(String identifier){

        User user = findByEmail(identifier);
        user.setActive(false);
        user.setExtraInfo(null);
        userRepository.delete(user);
        //eventPublisher.publishEvent(new UserDeletedEvent(user));
    }
    @Transactional
    public void updateInfo(String identifier, UpdateRequest req) {
        // 1) 기존 User 조회
        User user = findByEmail(identifier);

        // 2) User 필드 업데이트
        user.setPhone(req.getPhone());
        user.setUniversityCategory(universityCategoryService.findById(req.getUniversityCategoryId()));
        user.setCurrentSchool(req.getCurrentSchool());
        user.setHighSchool(req.getHighSchool());
        user.setUniversityYear(req.getUniversityYear());
        user.setGender(req.getGender());
        user.setAddress(regionService.findById(req.getAddressId()));
        user.setResidentAddress(regionService.findById(req.getResidentAddressId()));

        // 3) UserExtraInfo 준비
        UserExtraInfo extra = user.getExtraInfo();

        // 4) UserExtraInfo 필드 업데이트
        extra.setQualificationCodes(req.getQualificationCodes());
        extra.setHighSchoolGrade(req.getHighSchoolGrade());
        extra.setSatAverageGrade(req.getSatAverageGrade());
        extra.setGpaScale(req.getGpaScale());
        extra.setOverallGpa(req.getOverallGpa());
        extra.setPreviousSemesterCredits(req.getPreviousSemesterCredits());
        extra.setPreviousSemesterGpa(req.getPreviousSemesterGpa());
        extra.setTwoSemestersAgoCredits(req.getTwoSemestersAgoCredits());
        extra.setTwoSemestersAgoGpa(req.getTwoSemestersAgoGpa());
        extra.setScholarshipSupportInterval(req.getScholarshipSupportInterval());
        extra.setMedianIncomeRatio(req.getMedianIncomeRatio());
        extra.setIncomePercentileBand(req.getIncomePercentileBand());

        // 5) 저장은 트랜잭션 커밋 시점에 자동 반영 (cascade=ALL 이면 userRepository.save(user) 만으로 충분)
        userRepository.save(user);
        // 추천 데이터 갱신 비동기 실행
        applicationEventPublisher.publishEvent(new RecommendationRefreshEvent(identifier));
    }

    @Transactional
    public UserResponse getInfo(String identifier) {
        // 1) 기존 User 조회
        User user = findByEmail(identifier);
        UserResponse userResponse = new UserResponse();

        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setUniversityCategoryId(user.getUniversityCategory().getId());
        userResponse.setCurrentSchool(user.getCurrentSchool());
        userResponse.setHighSchool(user.getHighSchool());
        userResponse.setUniversityYear(user.getUniversityYear());
        userResponse.setGender(user.getGender());

        if (user.getAddress() != null) {
            Long addrId = user.getAddress().getId();
            String full = regionService.getFullRegionName(addrId);
            userResponse.setAddress(full);
        }
        userResponse.setAddressId(user.getAddress().getId());
        if (user.getResidentAddress() != null) {
            Long resId = user.getResidentAddress().getId();
            String full = regionService.getFullRegionName(resId);
            userResponse.setResidentAddress(full);
        }
        userResponse.setResidentAddressId(user.getResidentAddress().getId());


        // UserExtraInfo 준비 (없으면 새로 생성)
        UserExtraInfo extra = user.getExtraInfo();
        if (extra == null) {
            extra = UserExtraInfo.builder()
                    .user(user)         // 양방향 연관관계 세팅
                    .build();
            user.setExtraInfo(extra);
            return userResponse;
        }

        // UserExtraInfo 필드 업데이트
        userResponse.setQualificationCodes(extra.getQualificationCodes());
        userResponse.setHighSchoolGrade(extra.getHighSchoolGrade());
        userResponse.setSatAverageGrade(extra.getSatAverageGrade());
        userResponse.setGpaScale(extra.getGpaScale());
        userResponse.setOverallGpa(extra.getOverallGpa());
        userResponse.setPreviousSemesterCredits(extra.getPreviousSemesterCredits());
        userResponse.setPreviousSemesterGpa(extra.getPreviousSemesterGpa());
        userResponse.setTwoSemestersAgoCredits(extra.getTwoSemestersAgoCredits());
        userResponse.setTwoSemestersAgoGpa(extra.getTwoSemestersAgoGpa());
        userResponse.setScholarshipSupportInterval(extra.getScholarshipSupportInterval());
        userResponse.setMedianIncomeRatio(extra.getMedianIncomeRatio());
        userResponse.setIncomePercentileBand(extra.getIncomePercentileBand());

        // 5) 저장은 트랜잭션 커밋 시점에 자동 반영 (cascade=ALL 이면 userRepository.save(user) 만으로 충분)
        return userResponse;
    }

    @Transactional
    public MyPageResponse myPageInfo(String identifier) {
        User user = findByEmail(identifier);
        MyPageResponse myPageResponse = new MyPageResponse();

        myPageResponse.setUserId(user.getId());
        myPageResponse.setUsername(user.getUsername());
        myPageResponse.setUserUniversity(user.getCurrentSchool());

        return myPageResponse;
    }

    public void updatePassword(String identifier, String newPassword) {
        User user = findByEmail(identifier);
        user.setPassword(newPassword);
    }



}

