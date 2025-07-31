package com.grantip.backend.domain.user.service;

import com.grantip.backend.domain.region.service.RegionService;
import com.grantip.backend.domain.scholarship.service.UniversityCategoryService;
import com.grantip.backend.domain.user.domain.dto.CustomUserDetails;
import com.grantip.backend.domain.user.domain.dto.request.UpdateRequest;
import com.grantip.backend.domain.user.domain.dto.request.VerifyPassword;
import com.grantip.backend.domain.user.domain.constant.Role;
import com.grantip.backend.domain.user.domain.entity.User;
import com.grantip.backend.domain.user.domain.entity.UserExtraInfo;
import com.grantip.backend.domain.user.repository.UserRepository;
import com.grantip.backend.global.code.ErrorCode;
import com.grantip.backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
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

    public void verifyCurrentPassword(String email, String currentPassword){
        User user = findByEmail(email);
        if(!passwordEncoder.matches(currentPassword, user.getPassword())){
            throw new CustomException(ErrorCode.INCORRECT_CURRENT_PASSWORD);
        }
    }
    public CustomUserDetails loadUserDetailsByEmail(String email) {
        return userRepository.findByEmail(email)
                .<CustomUserDetails>map(CustomUserDetails::new)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    }
    /*
    @Transactional
    public void updatePassword(String LoginId, PasswordUpdate passwordUpdate){
        User user = findByLoginId(LoginId);
        verifyCurrentPassword(LoginId, passwordUpdate.getCurrentPassword());
        if(passwordEncoder.matches(passwordUpdate.getNewPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.SAME_AS_OLD_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(passwordUpdate.getNewPassword()));
    }
    */
    public void verifyPassword(VerifyPassword verifyPassword) {
        String p1 = verifyPassword.getPassword1();
        String p2 = verifyPassword.getPassword2();

        if(!p1.equals(p2)) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }
    }
    @Transactional
    public void deleteUser(String identifier){

        User user = findByEmail(identifier);
        user.setActive(false);
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

        // 3) UserExtraInfo 준비 (없으면 새로 생성)
        UserExtraInfo extra = user.getExtraInfo();
        if (extra == null) {
            extra = UserExtraInfo.builder()
                    .user(user)         // 양방향 연관관계 세팅
                    .build();
            user.setExtraInfo(extra);
        }

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
    }



}

