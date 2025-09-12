package com.exam.repository;

import com.exam.entities.QuestionBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {

    @Query(value ="SELECT * FROM question_bank WHERE course_lang = :courseLang AND assessment = true ORDER BY RANDOM() LIMIT 100", nativeQuery = true)
    List<QuestionBank> findByCourseLangAndAssessment(@Param("courseLang") String courseLang);
    
    @Query(value ="SELECT * FROM question_bank WHERE level_id = :levelId AND lesson_id = :lessonId AND course_lang = :courseLang AND assessment = false ORDER BY RANDOM() LIMIT 100", nativeQuery = true)
    List<QuestionBank> findByLevelIdAndLessonIdAndCourseLang(@Param("levelId") Integer levelId, @Param("lessonId") Integer lessonId, @Param("courseLang") String courseLang);
} 
