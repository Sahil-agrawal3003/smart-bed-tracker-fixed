package com.hospital.smartbedtracker.service;

import com.hospital.smartbedtracker.entity.Ward;
import com.hospital.smartbedtracker.exception.ResourceNotFoundException;
import com.hospital.smartbedtracker.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WardService {

    private final WardRepository wardRepository;

    public List<Ward> getAllWards() {
        return wardRepository.findAll();
    }

    public Ward getWardById(Long id) {
        return wardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ward not found with id: " + id));
    }

    @Transactional
    public Ward createWard(Ward ward) {
        if (wardRepository.existsByName(ward.getName())) {
            throw new IllegalStateException("Ward with name '" + ward.getName() + "' already exists.");
        }
        return wardRepository.save(ward);
    }

    @Transactional
    public Ward updateWard(Long id, Ward updated) {
        Ward ward = getWardById(id);
        ward.setName(updated.getName());
        ward.setTotalBeds(updated.getTotalBeds());
        ward.setFloor(updated.getFloor());
        ward.setDescription(updated.getDescription());
        return wardRepository.save(ward);
    }

    @Transactional
    public void deleteWard(Long id) {
        Ward ward = getWardById(id);
        wardRepository.delete(ward);
    }
}
