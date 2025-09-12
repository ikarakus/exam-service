package com.ai.repository;

import com.ai.entities.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppConfigRepository extends JpaRepository<AppConfig, Long> {

    @Query("select app from AppConfig app where app.key = ?1 AND app.type=?2")
    AppConfig findByKey(String key,String type);

}

