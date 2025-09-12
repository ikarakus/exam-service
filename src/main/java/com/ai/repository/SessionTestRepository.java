package com.ai.repository;

import com.ai.entities.SessionTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionTestRepository extends JpaRepository<SessionTest, Long> {

    @Query("SELECT st FROM SessionTest st LEFT JOIN FETCH st.questions WHERE st.id = ?1")
    SessionTest findByIdWithQuestions(Long id);

    @Query("SELECT st FROM SessionTest st WHERE st.user.id = ?1 AND st.courseLang = ?2 AND st.assessment = true")
    List<SessionTest> findAssessmentByUserAndCourseLang(Long userId, String courseLang);

    @Query("SELECT st FROM SessionTest st WHERE st.pass=true AND st.user.id = ?1 AND st.courseLang = ?2")
    List<SessionTest> findByUserIdAndCourseLang(Long userId, String courseLang);

    @Query(value = "SELECT l.id as lessonId, l.name as lessonName, l.description as lessonDescription, MAX(st.score) as maxScore " +
           "FROM lesson l " +
           "JOIN session_test st ON l.id = st.lesson_id " +
           "WHERE st.user_id = ?1 " +
           "AND st.course_lang = ?2 " +
           "AND st.level_id = ?3 " +
           "AND st.pass = true " +
           "GROUP BY l.id, l.name, l.description", nativeQuery = true)
    List<Object[]> findPassedLessonsByLevel(Long userId, String courseLang, Integer levelId);

    @Query("SELECT st FROM SessionTest st WHERE st.user.id = ?1 AND st.courseLang = ?2 AND st.assessment = true")
    Optional<SessionTest> findAssessmentByUserIdAndCourseLang(Long userId, String courseLang);

    @Query("SELECT st FROM SessionTest st WHERE st.user.id = ?1 AND st.levelId = ?2 AND st.lessonId = ?3 AND st.courseLang = ?4 AND st.assessment = false")
    List<SessionTest> findNonAssessmentByUserIdAndLevelIdAndLessonIdAndCourseLang(Long userId, Integer levelId, Integer lessonId, String courseLang);

    @Query("SELECT st FROM SessionTest st WHERE st.user.id = ?1 AND st.levelId = ?2 AND st.lessonId = ?3 AND st.courseLang = ?4")
    List<SessionTest> findAllByUserIdAndLevelIdAndLessonIdAndCourseLang(Long userId, Integer levelId, Integer lessonId, String courseLang);
} 
