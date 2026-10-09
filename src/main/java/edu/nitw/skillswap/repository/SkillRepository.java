package edu.nitw.skillswap.repository;

import edu.nitw.skillswap.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByStudentId(Long studentId);
    List<Skill> findBySkillTypeIgnoreCase(String skillType);
}
