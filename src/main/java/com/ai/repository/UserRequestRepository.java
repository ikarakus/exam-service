package com.ai.repository;


import com.ai.entities.User;
import com.ai.entities.UserRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UserRequestRepository extends JpaRepository<UserRequest, Long> {

    @Query("select req from UserRequest req where req.duration > 0 and req.status <> 3 and req.updatedDateTime is not null and req.user=?1 and req.courseLang=?2 and req.createdDateTime BETWEEN ?3 AND ?4")
    List<UserRequest> getRequestByUser(User user, String courseLang, Date startDate, Date endDate);

    @Query("SELECT ur FROM UserRequest ur WHERE ur.user.id = ?1 ORDER BY ur.createdDateTime DESC")
    List<UserRequest> findLatestByUserId(Long userId);

    @Query("SELECT ur.selectedLevel, SUM(ur.duration) FROM UserRequest ur WHERE ur.user.id = ?1 AND ur.courseLang = ?2 AND ur.selectedLevel IS NOT NULL GROUP BY ur.selectedLevel")
    List<Object[]> findSumDurationByUserIdAndLangGroupedByLevel(Long userId, String language);

    List<UserRequest> findByUserIdAndSelectedLevel(Long userId, String selectedLevel);
    void deleteByUserIdAndSelectedLevel(Long userId, String selectedLevel);

}
