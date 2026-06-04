package com.hospital.smartbedtracker.repository;

import com.hospital.smartbedtracker.entity.Bed;
import com.hospital.smartbedtracker.enums.BedStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {
    List<Bed> findByStatus(BedStatus status);
    List<Bed> findByWardId(Long wardId);
    List<Bed> findByWardIdAndStatus(Long wardId, BedStatus status);
    long countByStatus(BedStatus status);
    boolean existsByBedNumber(String bedNumber);

    @Query("SELECT b FROM Bed b LEFT JOIN FETCH b.ward ORDER BY b.bedNumber")
    List<Bed> findAllWithWard();
}
