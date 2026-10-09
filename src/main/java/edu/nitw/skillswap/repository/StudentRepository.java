package edu.nitw.skillswap.repository;

import edu.nitw.skillswap.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmailIgnoreCase(String email);
    Optional<Student> findByRollNumberIgnoreCase(String rollNumber);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByRollNumberIgnoreCase(String rollNumber);
}
