package com.hospital.smartbedtracker.controller;

import com.hospital.smartbedtracker.entity.Ward;
import com.hospital.smartbedtracker.service.WardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wards")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WardController {

    private final WardService wardService;

    @GetMapping
    public ResponseEntity<List<Ward>> getAllWards() {
        return ResponseEntity.ok(wardService.getAllWards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ward> getWardById(@PathVariable Long id) {
        return ResponseEntity.ok(wardService.getWardById(id));
    }

    @PostMapping
    public ResponseEntity<Ward> createWard(@Valid @RequestBody Ward ward) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wardService.createWard(ward));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ward> updateWard(@PathVariable Long id, @Valid @RequestBody Ward ward) {
        return ResponseEntity.ok(wardService.updateWard(id, ward));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWard(@PathVariable Long id) {
        wardService.deleteWard(id);
        return ResponseEntity.noContent().build();
    }
}
