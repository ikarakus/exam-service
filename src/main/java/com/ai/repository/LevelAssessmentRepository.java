package com.ai.repository;

import com.ai.entities.LevelAssessmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelAssessmentRepository extends JpaRepository<LevelAssessmentHistory, Integer> {

} 
