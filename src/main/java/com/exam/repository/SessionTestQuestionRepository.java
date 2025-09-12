package com.exam.repository;

import com.exam.entities.SessionTest;
import com.exam.entities.SessionTestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionTestQuestionRepository extends JpaRepository<SessionTestQuestion, Long> {
    @Query("SELECT q FROM SessionTestQuestion q WHERE q.sessionTest.id = ?1 AND (q.selectedLabel IS NULL OR q.selectedLabel = '') ORDER BY q.questionIndex ASC")
    List<SessionTestQuestion> findFirstUnansweredBySessionTestId(Long sessionTestId);

    @Query("SELECT q FROM SessionTestQuestion q WHERE q.sessionTest.id = ?1 AND (q.selectedLabel IS NULL OR q.selectedLabel = '') ORDER BY q.questionIndex ASC")
    List<SessionTestQuestion> findAllUnansweredBySessionTestId(Long sessionTestId);

    void deleteBySessionTest(SessionTest sessionTest);
} 
