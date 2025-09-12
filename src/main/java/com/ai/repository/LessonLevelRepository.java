package com.ai.repository;

import com.ai.entities.LessonLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LessonLevelRepository extends JpaRepository<LessonLevel, Integer> {
    @Query("SELECT l FROM LessonLevel l WHERE l.courseLang =?1 AND l.code not in ('CEFR','Conv') AND l.active = true ORDER BY l.orderNo")
    List<LessonLevel> findByLangExcludingId5(String lang);
} 
