package edu.nitw.skillswap.model;

import jakarta.persistence.*;

@Entity
@Table(name = "student_skills")
public class Skill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Student student;
    @Column(nullable = false, length = 100)
    private String skillName;
    @Column(nullable = false, length = 10)
    private String skillType;

    protected Skill() {}
    public Skill(Student student, String skillName, String skillType) {
        this.student = student; this.skillName = skillName; this.skillType = skillType;
    }
    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public String getSkillName() { return skillName; }
    public String getSkillType() { return skillType; }
}
