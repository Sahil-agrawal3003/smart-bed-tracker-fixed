package com.hospital.smartbedtracker.controller;

import com.hospital.smartbedtracker.dto.BedStatsResponse;
import com.hospital.smartbedtracker.dto.CreateBedRequest;
import com.hospital.smartbedtracker.entity.Bed;
import com.hospital.smartbedtracker.service.BedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beds")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BedController {

    private final BedService bedService;

    /** GET /api/beds — list all beds */
    @GetMapping
    public ResponseEntity<List<Bed>> getAllBeds() {
        return ResponseEntity.ok(bedService.getAllBeds());
    }

    /** GET /api/beds/{id} — single bed */
    @GetMapping("/{id}")
    public ResponseEntity<Bed> getBedById(@PathVariable Long id) {
        return ResponseEntity.ok(bedService.getBedById(id));
    }

    /** GET /api/beds/available — available beds only */
    @GetMapping("/available")
    public ResponseEntity<List<Bed>> getAvailableBeds() {
        return ResponseEntity.ok(bedService.getAvailableBeds());
    }

    /** GET /api/beds/ward/{wardId} — beds in a ward */
    @GetMapping("/ward/{wardId}")
    public ResponseEntity<List<Bed>> getBedsByWard(@PathVariable Long wardId) {
        return ResponseEntity.ok(bedService.getBedsByWard(wardId));
    }

    /** GET /api/beds/stats — occupancy stats (public) */
    @GetMapping("/stats")
    public ResponseEntity<BedStatsResponse> getStats() {
        return ResponseEntity.ok(bedService.getBedStats());
    }

    /** POST /api/beds — create a new bed (ADMIN) */
    @PostMapping
    public ResponseEntity<Bed> createBed(@Valid @RequestBody CreateBedRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bedService.createBed(request));
    }

    /** PUT /api/beds/{id}/assign — mark as occupied */
    @PutMapping("/{id}/assign")
    public ResponseEntity<Bed> assignBed(@PathVariable Long id) {
        return ResponseEntity.ok(bedService.assignBed(id));
    }

    /** PUT /api/beds/{id}/release — mark as available */
    @PutMapping("/{id}/release")
    public ResponseEntity<Bed> releaseBed(@PathVariable Long id) {
        return ResponseEntity.ok(bedService.releaseBed(id));
    }

    /** PUT /api/beds/{id}/maintenance — mark for maintenance */
    @PutMapping("/{id}/maintenance")
    public ResponseEntity<Bed> setMaintenance(@PathVariable Long id) {
        return ResponseEntity.ok(bedService.setMaintenance(id));
    }

    /** PUT /api/beds/{id}/reserve — reserve a bed */
    @PutMapping("/{id}/reserve")
    public ResponseEntity<Bed> reserveBed(@PathVariable Long id) {
        return ResponseEntity.ok(bedService.setReserved(id));
    }

    /** DELETE /api/beds/{id} — delete a bed (ADMIN) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBed(@PathVariable Long id) {
        bedService.deleteBed(id);
        return ResponseEntity.noContent().build();
    }
}
