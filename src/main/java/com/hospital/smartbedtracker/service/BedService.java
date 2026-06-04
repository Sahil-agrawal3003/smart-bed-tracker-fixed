package com.hospital.smartbedtracker.service;

import com.hospital.smartbedtracker.dto.BedStatsResponse;
import com.hospital.smartbedtracker.dto.CreateBedRequest;
import com.hospital.smartbedtracker.entity.Bed;
import com.hospital.smartbedtracker.entity.Ward;
import com.hospital.smartbedtracker.enums.BedStatus;
import com.hospital.smartbedtracker.exception.BedNotAvailableException;
import com.hospital.smartbedtracker.exception.ResourceNotFoundException;
import com.hospital.smartbedtracker.repository.BedRepository;
import com.hospital.smartbedtracker.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BedService {

    private final BedRepository bedRepository;
    private final WardRepository wardRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public List<Bed> getAllBeds() {
        return bedRepository.findAllWithWard();
    }

    public List<Bed> getAvailableBeds() {
        return bedRepository.findByStatus(BedStatus.AVAILABLE);
    }

    public List<Bed> getBedsByWard(Long wardId) {
        return bedRepository.findByWardId(wardId);
    }

    public Bed getBedById(Long id) {
        return bedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bed not found with id: " + id));
    }

    @Transactional
    public Bed createBed(CreateBedRequest request) {
        if (bedRepository.existsByBedNumber(request.getBedNumber())) {
            throw new IllegalStateException("Bed number already exists: " + request.getBedNumber());
        }
        Ward ward = wardRepository.findById(request.getWardId())
                .orElseThrow(() -> new ResourceNotFoundException("Ward not found with id: " + request.getWardId()));

        Bed bed = new Bed();
        bed.setBedNumber(request.getBedNumber());
        bed.setWard(ward);
        bed.setStatus(BedStatus.AVAILABLE);
        bed.setLastUpdated(LocalDateTime.now());
        Bed saved = bedRepository.save(bed);
        broadcastBedUpdate(saved);
        log.info("Created bed: {}", saved.getBedNumber());
        return saved;
    }

    @Transactional
    public Bed assignBed(Long bedId) {
        Bed bed = getBedById(bedId);
        if (bed.getStatus() != BedStatus.AVAILABLE) {
            throw new BedNotAvailableException(
                    "Bed " + bed.getBedNumber() + " is not available. Current status: " + bed.getStatus());
        }
        bed.setStatus(BedStatus.OCCUPIED);
        bed.setLastUpdated(LocalDateTime.now());
        Bed saved = bedRepository.save(bed);
        broadcastBedUpdate(saved);
        return saved;
    }

    @Transactional
    public Bed releaseBed(Long bedId) {
        Bed bed = getBedById(bedId);
        bed.setStatus(BedStatus.AVAILABLE);
        bed.setLastUpdated(LocalDateTime.now());
        Bed saved = bedRepository.save(bed);
        broadcastBedUpdate(saved);
        return saved;
    }

    @Transactional
    public Bed setMaintenance(Long bedId) {
        Bed bed = getBedById(bedId);
        bed.setStatus(BedStatus.MAINTENANCE);
        bed.setLastUpdated(LocalDateTime.now());
        Bed saved = bedRepository.save(bed);
        broadcastBedUpdate(saved);
        return saved;
    }

    @Transactional
    public Bed setReserved(Long bedId) {
        Bed bed = getBedById(bedId);
        if (bed.getStatus() != BedStatus.AVAILABLE) {
            throw new BedNotAvailableException("Bed must be AVAILABLE to reserve it.");
        }
        bed.setStatus(BedStatus.RESERVED);
        bed.setLastUpdated(LocalDateTime.now());
        Bed saved = bedRepository.save(bed);
        broadcastBedUpdate(saved);
        return saved;
    }

    @Transactional
    public void deleteBed(Long bedId) {
        Bed bed = getBedById(bedId);
        if (bed.getStatus() == BedStatus.OCCUPIED) {
            throw new IllegalStateException("Cannot delete an occupied bed.");
        }
        bedRepository.delete(bed);
        log.info("Deleted bed id={}", bedId);
    }

    public BedStatsResponse getBedStats() {
        long total     = bedRepository.count();
        long available = bedRepository.countByStatus(BedStatus.AVAILABLE);
        long occupied  = bedRepository.countByStatus(BedStatus.OCCUPIED);
        long maint     = bedRepository.countByStatus(BedStatus.MAINTENANCE);
        long reserved  = bedRepository.countByStatus(BedStatus.RESERVED);
        double rate    = total == 0 ? 0.0 : Math.round((occupied * 100.0 / total) * 10.0) / 10.0;
        return new BedStatsResponse(total, available, occupied, maint, reserved, rate);
    }

    private void broadcastBedUpdate(Bed bed) {
        messagingTemplate.convertAndSend("/topic/beds", bed);
    }
}
