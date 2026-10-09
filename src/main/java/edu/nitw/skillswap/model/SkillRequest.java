package edu.nitw.skillswap.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "skill_requests")
public class SkillRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Student sender;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Student receiver;
    @Column(nullable = false, length = 100)
    private String skillName;
    @Column(nullable = false, length = 20)
    private String status = "PENDING";
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected SkillRequest() {}
    public SkillRequest(Student sender, Student receiver, String skillName) {
        this.sender = sender; this.receiver = receiver; this.skillName = skillName;
    }
    public Long getId() { return id; }
    public Student getSender() { return sender; }
    public Student getReceiver() { return receiver; }
    public String getSkillName() { return skillName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
