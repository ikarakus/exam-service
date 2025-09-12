package com.ai.repository;

import com.ai.entities.AvatarProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AvatarProfileRepository extends JpaRepository<AvatarProfile, Long> {


    @Query("select a from AvatarProfile a WHERE a.active= true and a.userLang=?1 and a.kids=?2 order by a.orderNo")
    List<AvatarProfile> getAvatars(String lang,Boolean kids);

    @Query("SELECT COALESCE(MAX(a.orderNo), 0) FROM AvatarProfile a")
    Integer findMaxOrderNo();
    
    @Query("SELECT a FROM AvatarProfile a WHERE a.tutorId = ?1")
    Optional<AvatarProfile> findByTutorId(Long tutorId);
}
