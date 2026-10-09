package edu.nitw.skillswap.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private record OtpEntry(String code, Instant expiresAt, int attempts) {
        OtpEntry failedAttempt() { return new OtpEntry(code, expiresAt, attempts + 1); }
    }
    private final ConcurrentHashMap<String, OtpEntry> pending = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final MailService mailService;
    @Value("${skillswap.otp.expiry-minutes:5}") private long expiryMinutes;

    public OtpService(MailService mailService) { this.mailService = mailService; }

    public String issue(String email) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        pending.put(email, new OtpEntry(code, Instant.now().plusSeconds(expiryMinutes * 60), 0));
        String message = "Your SkillSwap verification code is " + code +
                ". It expires in " + expiryMinutes + " minutes. Do not share it.";
        if (mailService.isConfigured()) {
            mailService.send(email, "SkillSwap email verification", message);
        } else {
            System.out.println("\\n[LOCAL TEST OTP] " + email + " -> " + code + "\\n");
        }
        return code;
    }

    public boolean verify(String email, String code) {
        OtpEntry entry = pending.get(email);
        if (entry == null || Instant.now().isAfter(entry.expiresAt()) || entry.attempts() >= 5) {
            pending.remove(email); return false;
        }
        if (entry.code().equals(code)) { pending.remove(email); return true; }
        pending.put(email, entry.failedAttempt());
        return false;
    }

    public void remove(String email) { pending.remove(email); }
}
