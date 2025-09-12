package com.ai.repository;

import com.ai.entities.SessionTest;
import com.ai.entities.SessionTestQuestion;
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