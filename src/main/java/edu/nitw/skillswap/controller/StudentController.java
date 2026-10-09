package edu.nitw.skillswap.controller;

import edu.nitw.skillswap.model.Skill;
import edu.nitw.skillswap.model.SkillRequest;
import edu.nitw.skillswap.model.Student;
import edu.nitw.skillswap.repository.SkillRepository;
import edu.nitw.skillswap.repository.SkillRequestRepository;
import edu.nitw.skillswap.repository.StudentRepository;
import edu.nitw.skillswap.service.MailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class StudentController {
    private final StudentRepository students;
    private final SkillRepository skills;
    private final SkillRequestRepository requests;
    private final MailService mailService;
    @Value("${skillswap.admin.email}") private String adminEmail;

    public StudentController(StudentRepository students, SkillRepository skills,
                             SkillRequestRepository requests, MailService mailService) {
        this.students = students; this.skills = skills; this.requests = requests; this.mailService = mailService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Student me = current(session);
        if (me == null) return "redirect:/login";
        model.addAttribute("me", me);
        model.addAttribute("mySkills", skills.findByStudentId(me.getId()));
        model.addAttribute("students", students.findAll().stream().filter(s -> !s.getId().equals(me.getId())).toList());
        model.addAttribute("incoming", requests.findByReceiverIdOrderByCreatedAtDesc(me.getId()));
        model.addAttribute("outgoing", requests.findBySenderIdOrderByCreatedAtDesc(me.getId()));
        model.addAttribute("adminEmail", adminEmail);
        return "dashboard";
    }

    @PostMapping("/skills/add")
    public String addSkill(@RequestParam String skillName, @RequestParam String skillType,
                           HttpSession session, RedirectAttributes redirect) {
        Student me = current(session);
        if (me == null) return "redirect:/login";
        String cleanName = skillName == null ? "" : skillName.trim();
        if (cleanName.isBlank() || cleanName.length() > 100 ||
            !(skillType.equals("TEACH") || skillType.equals("LEARN"))) {
            redirect.addFlashAttribute("error", "Enter a skill and choose Teach or Learn."); return "redirect:/dashboard";
        }
        skills.save(new Skill(me, cleanName, skillType));
        redirect.addFlashAttribute("success", "Skill added.");
        return "redirect:/dashboard";
    }

    @PostMapping("/requests/send")
    public String sendRequest(@RequestParam Long receiverId, @RequestParam String skillName,
                              HttpSession session, RedirectAttributes redirect) {
        Student me = current(session);
        if (me == null) return "redirect:/login";
        Student receiver = students.findById(receiverId).orElse(null);
        String skill = skillName == null ? "" : skillName.trim();
        if (receiver == null || receiver.getId().equals(me.getId()) || skill.isBlank() || skill.length() > 100) {
            redirect.addFlashAttribute("error", "Could not send that request."); return "redirect:/dashboard";
        }
        requests.save(new SkillRequest(me, receiver, skill));
        if (mailService.isConfigured()) {
            try { mailService.send(receiver.getEmail(), "New SkillSwap help request",
                    me.getName() + " (" + me.getEmail() + ") sent you a request for help with: " + skill +
                    "\\nLog in to SkillSwap to accept or reject it."); } catch (Exception ignored) {}
        }
        redirect.addFlashAttribute("success", "Skill request sent.");
        return "redirect:/dashboard";
    }

    @PostMapping("/requests/{id}/status")
    public String updateRequest(@PathVariable Long id, @RequestParam String status,
                                HttpSession session, RedirectAttributes redirect) {
        Student me = current(session);
        if (me == null) return "redirect:/login";
        if (!(status.equals("ACCEPTED") || status.equals("REJECTED"))) {
            redirect.addFlashAttribute("error", "Invalid request status."); return "redirect:/dashboard";
        }
        SkillRequest request = requests.findByIdAndReceiverId(id, me.getId()).orElse(null);
        if (request == null || !request.getStatus().equals("PENDING")) {
            redirect.addFlashAttribute("error", "Request not found or already processed."); return "redirect:/dashboard";
        }
        request.setStatus(status);
        requests.save(request);
        if (mailService.isConfigured()) {
            try { mailService.send(request.getSender().getEmail(), "SkillSwap request " + status.toLowerCase(),
                    me.getName() + " " + status.toLowerCase() + " your request for " + request.getSkillName() + "."); }
            catch (Exception ignored) {}
        }
        redirect.addFlashAttribute("success", "Request " + status.toLowerCase() + ".");
        return "redirect:/dashboard";
    }

    private Student current(HttpSession session) {
        Object id = session.getAttribute("studentId");
        if (!(id instanceof Long)) return null;
        return students.findById((Long) id).orElse(null);
    }
}
