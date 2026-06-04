package com.hospital.smartbedtracker.service;

import com.hospital.smartbedtracker.dto.AdmitPatientRequest;
import com.hospital.smartbedtracker.entity.Bed;
import com.hospital.smartbedtracker.entity.Patient;
import com.hospital.smartbedtracker.enums.BedStatus;
import com.hospital.smartbedtracker.enums.PatientStatus;
import com.hospital.smartbedtracker.exception.BedNotAvailableException;
import com.hospital.smartbedtracker.exception.ResourceNotFoundException;
import com.hospital.smartbedtracker.repository.BedRepository;
import com.hospital.smartbedtracker.repository.PatientRepository;
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
public class PatientService {

    private final PatientRepository patientRepository;
    private final BedRepository bedRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public List<Patient> getAdmittedPatients() {
        return patientRepository.findAdmittedWithBed();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    @Transactional
    public Patient admitPatient(AdmitPatientRequest request, Long bedId) {
        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new ResourceNotFoundException("Bed not found with id: " + bedId));

        if (bed.getStatus() != BedStatus.AVAILABLE && bed.getStatus() != BedStatus.RESERVED) {
            throw new BedNotAvailableException(
                    "Bed " + bed.getBedNumber() + " is not available. Current status: " + bed.getStatus());
        }

        Patient patient = new Patient();
        patient.setName(request.getName());
        patient.setAge(request.getAge());
        patient.setDisease(request.getDisease());
        patient.setDoctorAssigned(request.getDoctorAssigned());
        patient.setContactNumber(request.getContactNumber());
        patient.setEmergencyContact(request.getEmergencyContact());
        patient.setBed(bed);
        patient.setAdmissionDate(LocalDateTime.now());
        patient.setStatus(PatientStatus.ADMITTED);

        bed.setStatus(BedStatus.OCCUPIED);
        bed.setLastUpdated(LocalDateTime.now());
        bedRepository.save(bed);

        Patient saved = patientRepository.save(patient);
        messagingTemplate.convertAndSend("/topic/patients", saved);
        log.info("Admitted patient {} to bed {}", saved.getName(), bed.getBedNumber());
        return saved;
    }

    @Transactional
    public Patient dischargePatient(Long patientId) {
        Patient patient = getPatientById(patientId);

        if (patient.getStatus() == PatientStatus.DISCHARGED) {
            throw new IllegalStateException("Patient is already discharged.");
        }

        if (patient.getBed() != null) {
            Bed bed = patient.getBed();
            bed.setStatus(BedStatus.AVAILABLE);
            bed.setLastUpdated(LocalDateTime.now());
            bedRepository.save(bed);
            messagingTemplate.convertAndSend("/topic/beds", bed);
        }

        patient.setDischargeDate(LocalDateTime.now());
        patient.setStatus(PatientStatus.DISCHARGED);
        patient.setBed(null);

        Patient saved = patientRepository.save(patient);
        messagingTemplate.convertAndSend("/topic/patients", saved);
        log.info("Discharged patient id={}", patientId);
        return saved;
    }

    @Transactional
    public Patient transferPatient(Long patientId, Long newBedId) {
        Patient patient = getPatientById(patientId);

        if (patient.getStatus() != PatientStatus.ADMITTED) {
            throw new IllegalStateException("Only admitted patients can be transferred.");
        }

        Bed newBed = bedRepository.findById(newBedId)
                .orElseThrow(() -> new ResourceNotFoundException("Target bed not found with id: " + newBedId));

        if (newBed.getStatus() != BedStatus.AVAILABLE) {
            throw new BedNotAvailableException("Target bed is not available.");
        }

        // Release old bed
        if (patient.getBed() != null) {
            Bed oldBed = patient.getBed();
            oldBed.setStatus(BedStatus.AVAILABLE);
            oldBed.setLastUpdated(LocalDateTime.now());
            bedRepository.save(oldBed);
        }

        // Assign new bed
        newBed.setStatus(BedStatus.OCCUPIED);
        newBed.setLastUpdated(LocalDateTime.now());
        bedRepository.save(newBed);

        patient.setBed(newBed);
        patient.setStatus(PatientStatus.TRANSFERRED);

        Patient saved = patientRepository.save(patient);
        log.info("Transferred patient id={} to bed {}", patientId, newBed.getBedNumber());
        return saved;
    }
}
