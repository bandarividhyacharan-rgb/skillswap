package edu.nitw.skillswap.controller;

import edu.nitw.skillswap.model.Student;
import edu.nitw.skillswap.repository.StudentRepository;
import edu.nitw.skillswap.service.MailService;
import edu.nitw.skillswap.service.OtpService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.Set;

@Controller
public class AuthController {
    private static final BCryptPasswordEncoder PASSWORDS = new BCryptPasswordEncoder();
    private static final Set<String> BRANCHES = Set.of("CSE","ECE","EEE","Mechanical","Civil","Chemical","Biotechnology","Metallurgical and Materials","Mathematics and Computing");
    private static final Set<String> COURSES = Set.of("B.Tech","M.Tech","M.Sc","MBA","Ph.D.");
    private final StudentRepository students;
    private final OtpService otpService;
    private final MailService mailService;

    public AuthController(StudentRepository students, OtpService otpService, MailService mailService) {
        this.students = students; this.otpService = otpService; this.mailService = mailService;
    }

    @GetMapping({"/", "/login"})
    public String loginPage(@RequestParam(required = false) String registered, Model model) {
        if (registered != null) model.addAttribute("success", "Account verified. Please log in.");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() { return "register"; }

    @PostMapping("/register/send-otp")
    public String sendOtp(@RequestParam String name, @RequestParam String rollNumber,
                          @RequestParam String email, @RequestParam String branch,
                          @RequestParam Integer year, @RequestParam String course,
                          @RequestParam String password, HttpSession session, Model model) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        String normalizedRoll = rollNumber.trim().toUpperCase(Locale.ROOT);
        String error = validate(name, normalizedRoll, normalizedEmail, branch, year, course, password);
        if (error != null) { model.addAttribute("error", error); return "register"; }
        if (students.existsByEmailIgnoreCase(normalizedEmail)) {
            model.addAttribute("error", "This email already has an account. Please log in."); return "register";
        }
        if (students.existsByRollNumberIgnoreCase(normalizedRoll)) {
            model.addAttribute("error", "This roll number is already registered."); return "register";
        }
        try {
            otpService.issue(normalizedEmail);
        } catch (Exception ex) {
            model.addAttribute("error", "Could not send email. Check mail settings and try again."); return "register";
        }
        session.setAttribute("pendingName", name.trim());
        session.setAttribute("pendingRoll", normalizedRoll);
        session.setAttribute("pendingEmail", normalizedEmail);
        session.setAttribute("pendingBranch", branch);
        session.setAttribute("pendingYear", year);
        session.setAttribute("pendingCourse", course);
        session.setAttribute("pendingPasswordHash", PASSWORDS.encode(password));
        model.addAttribute("email", normalizedEmail);
        model.addAttribute("message", mailService.isConfigured()
                ? "A verification code was sent to your email."
                : "Local test mode: check the application terminal for your OTP.");
        return "verify-otp";
    }

    @PostMapping("/register/verify-otp")
    public String verifyOtp(@RequestParam String otp, HttpSession session, Model model) {
        String email = (String) session.getAttribute("pendingEmail");
        if (email == null) return "redirect:/register";
        if (!otpService.verify(email, otp.trim())) {
            model.addAttribute("email", email);
            model.addAttribute("error", "Invalid or expired OTP. Check the code and try again.");
            return "verify-otp";
        }
        Student student = new Student(
                (String) session.getAttribute("pendingName"),
                (String) session.getAttribute("pendingRoll"),
                email,
                (String) session.getAttribute("pendingPasswordHash"),
                (String) session.getAttribute("pendingBranch"),
                (Integer) session.getAttribute("pendingYear"),
                (String) session.getAttribute("pendingCourse"));
        student.setVerified(true);
        students.save(student);
        clearPending(session);
        return "redirect:/login?registered=1";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
                        HttpSession session, Model model) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        Student student = students.findByEmailIgnoreCase(normalized).orElse(null);
        if (student == null || !student.isVerified() || !PASSWORDS.matches(password, student.getPasswordHash())) {
            model.addAttribute("error", "Incorrect email or password.");
            return "login";
        }
        session.setAttribute("studentId", student.getId());
        session.setAttribute("studentName", student.getName());
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) { session.invalidate(); return "redirect:/login"; }

    private String validate(String name, String roll, String email, String branch,
                            Integer year, String course, String password) {
        if (name == null || name.trim().length() < 2 || name.trim().length() > 100) return "Enter your full name.";
        if (!roll.matches("[A-Z0-9]{9}")) return "B.Tech roll number must contain exactly 9 letters/numbers.";
        if (!email.matches("^[a-zA-Z0-9._%+-]+@student\\.nitw\\.ac\\.in$")) return "Use your official @student.nitw.ac.in email address.";
        if (!BRANCHES.contains(branch)) return "Select a branch from the list.";
        if (year == null || year < 1 || year > 6) return "Select a valid year.";
        if (!COURSES.contains(course)) return "Select a course from the list.";
        if (password == null || password.length() < 8) return "Password must be at least 8 characters.";
        return null;
    }

    private void clearPending(HttpSession session) {
        for (String key : new String[]{"pendingName","pendingRoll","pendingEmail","pendingBranch","pendingYear","pendingCourse","pendingPasswordHash"}) {
            session.removeAttribute(key);
        }
    }
}
