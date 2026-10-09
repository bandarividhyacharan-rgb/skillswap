
package edu.nitw.skillswap.repository;

import edu.nitw.skillswap.model.SkillRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SkillRequestRepository extends JpaRepository<SkillRequest, Long> {

    @Query("SELECT r FROM SkillRequest r JOIN FETCH r.sender JOIN FETCH r.receiver " +
           "WHERE r.receiver.id = :receiverId ORDER BY r.createdAt DESC")
    List<SkillRequest> findByReceiverIdOrderByCreatedAtDesc(
            @Param("receiverId") Long receiverId);

    @Query("SELECT r FROM SkillRequest r JOIN FETCH r.sender JOIN FETCH r.receiver " +
           "WHERE r.sender.id = :senderId ORDER BY r.createdAt DESC")
    List<SkillRequest> findBySenderIdOrderByCreatedAtDesc(
            @Param("senderId") Long senderId);

    @Query("SELECT r FROM SkillRequest r JOIN FETCH r.sender JOIN FETCH r.receiver " +
           "WHERE r.id = :id AND r.receiver.id = :receiverId")
    Optional<SkillRequest> findByIdAndReceiverId(
            @Param("id") Long id,
            @Param("receiverId") Long receiverId);
}

