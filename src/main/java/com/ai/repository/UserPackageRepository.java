package com.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPackageRepository extends JpaRepository<UserPackage, Long> {

    @Query("select u from UserPackage u where u.id = (select max(ui.id) from UserPackage ui WHERE ui.originalTransactionId=?1)")
    Optional<UserPackage> findByOriginalTransactionId(String originalTransactionId);
}
