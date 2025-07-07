package com.grantip.backend.domain.user.service;

import com.grantip.backend.domain.user.dto.CustomUserDetails;
import com.grantip.backend.domain.user.entity.Role;
import com.grantip.backend.domain.user.entity.User;
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
    private final ApplicationEventPublisher eventPublisher;

    public User findByLoginId(String LoginId){
        return userRepository.findByLoginId(LoginId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public List<User> findAdmins(){
        return userRepository.findByRole(Role.ADMIN);
    }

    public boolean existsByLoginId(String LoginId){
        return userRepository.existsByLoginId(LoginId);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public void verifyCurrentPassword(String LoginId, String currentPassword){
        User user = findByLoginId(LoginId);
        if(!passwordEncoder.matches(currentPassword, user.getPassword())){
            throw new CustomException(ErrorCode.INCORRECT_CURRENT_PASSWORD);
        }
    }
    public CustomUserDetails loadUserDetailsByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
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
    @Transactional
    public void deleteUser(String identifier){
        User user = findByIdentifier(identifier);
        user.setActive(false);
        eventPublisher.publishEvent(new UserDeletedEvent(user));
    }
     */

}

