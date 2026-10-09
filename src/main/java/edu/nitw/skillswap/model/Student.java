package edu.nitw.skillswap.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "students", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email"),
    @UniqueConstraint(columnNames = "roll_number")
})
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "roll_number", nullable = false, length = 9)
    private String rollNumber;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 40)
    private String branch;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false, length = 30)
    private String course;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Student() {}

    public Student(String name, String rollNumber, String email, String passwordHash,
                   String branch, Integer year, String course) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.email = email;
        this.passwordHash = passwordHash;
        this.branch = branch;
        this.year = year;
        this.course = course;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getRollNumber() { return rollNumber; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getBranch() { return branch; }
    public Integer getYear() { return year; }
    public String getCourse() { return course; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
