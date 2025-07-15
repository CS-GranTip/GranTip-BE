package com.grantip.backend.domain.email.service;

import com.grantip.backend.domain.email.repository.VerificationCodeRepository;
import com.grantip.backend.global.exception.EmailAuthException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final VerificationCodeRepository codeRepo;

    @Value("${spring.mail.username}")
    private String from;

    /*
    프로젝트의 흐름에 맞춰 “메일 보낸 뒤 생성된 코드를 돌려받아야 하나?”
    아니면 “그냥 보내기만 하면 되나?” 를 결정한 뒤, 인터페이스와 구현체 반환 타입을 일치시켜 주세요
    public String sendVerificationCode(String email) {
    String code = generateCode();
    // … 저장/전송 …
    return code;
    }
     */
    @Override
    public void sendVerificationCode(String email) {
        String code = generateCode();
        codeRepo.saveCode(email, code);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setFrom(from);
            helper.setSubject("GranTip 회원가입 인증 코드");

            String html = ""
                    + "<!DOCTYPE html>"
                    + "<html lang=\"ko\">"
                    + "<head>"
                    + "  <meta charset=\"UTF-8\" />"
                    + "  <title>인증 코드</title>"
                    + "</head>"
                    + "<body style=\"margin:0;padding:0;background:#f2f2f2;font-family:sans-serif;\">"
                    + "  <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#f2f2f2;\">"
                    + "    <tr>"
                    + "      <td align=\"center\">"
                    + "        <table width=\"500\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#ffffff;border-radius:8px;overflow:hidden;\">"
                    + "          <!-- HEADER -->"
                    + "          <tr>"
                    + "            <td style=\"background:#0068ff;padding:20px;text-align:center;\">"
                    + "              <h1 style=\"color:#ffffff;font-size:24px;margin:0;\">GranTip</h1>"
                    + "            </td>"
                    + "          </tr>"
                    + "          <!-- BODY -->"
                    + "          <tr>"
                    + "            <td style=\"padding:40px;text-align:center;\">"
                    + "              <p style=\"font-size:16px;margin:0 0 20px;color:#333;\">회원가입 인증 코드</p>"
                    + "              <p style=\"font-size:64px;font-weight:bold;color:#0068ff;margin:0 0 20px;\">"
                    +                code
                    + "              </p>"
                    + "              <p style=\"font-size:14px;color:#555;margin:0;\">해당 코드를 복사하여 인증 창에 붙여넣어 주세요.</p>"
                    + "            </td>"
                    + "          </tr>"
                    + "          <!-- FOOTER -->"
                    + "          <tr>"
                    + "            <td style=\"background:#f9f9f9;padding:20px;text-align:center;font-size:12px;color:#999;\">"
                    + "              &copy; 2025 GranTip. All rights reserved."
                    + "            </td>"
                    + "          </tr>"
                    + "        </table>"
                    + "      </td>"
                    + "    </tr>"
                    + "  </table>"
                    + "</body>"
                    + "</html>";

            helper.setText(html, true);

            // (필요시) 인라인 이미지도 붙일 수 있고요.
            // ClassPathResource logo = new ClassPathResource("static/images/GranTipLogo.png");
            // helper.addInline("logoImage", logo);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailAuthException("인증 메일 전송 중 오류가 발생했습니다.", e);
        }
    }




    @Override
    public void verifyCode(String email, String code) {
        String saved = codeRepo.getCode(email);
        if (saved == null || !saved.equals(code)) {
            throw new EmailAuthException("인증 코드가 없거나 잘못되었습니다.");
        }
        codeRepo.deleteCode(email);
    }

    private String generateCode() {
        Random rnd = new SecureRandom();
        int n = rnd.nextInt(900000) + 100000;  // 100000~999999
        return String.valueOf(n);
    }
}
