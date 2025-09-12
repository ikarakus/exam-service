package com.ai.repository;

import com.ai.entities.PackageMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface PackageMapRepository extends JpaRepository<PackageMap, Long> {

    @Query("select u from PackageMap u where u.courseLang=?1 AND u.app = true")
    List<PackageMap> getActivePackages(String courseLang);

    @Query("select u from PackageMap u where u.courseLang=?1 AND u.packageId=?2")
    Optional<PackageMap> findByPackageId(String courseLang,Integer packageId);

}
