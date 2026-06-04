package com.hospital.smartbedtracker.init;

import com.hospital.smartbedtracker.entity.Bed;
import com.hospital.smartbedtracker.entity.Ward;
import com.hospital.smartbedtracker.enums.BedStatus;
import com.hospital.smartbedtracker.repository.BedRepository;
import com.hospital.smartbedtracker.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final WardRepository wardRepository;
    private final BedRepository bedRepository;

    @Value("${app.data.init:false}")
    private boolean initData;

    @Override
    public void run(String... args) {
        if (!initData || wardRepository.count() > 0) {
            log.info("Skipping data initialization (already seeded or disabled).");
            return;
        }

        log.info("Seeding sample hospital data...");

        // ── Wards ──────────────────────────────────────────────────────────
        Ward icu  = createWard("ICU",             10, "1st Floor", "Intensive Care Unit");
        Ward gen  = createWard("General Ward",    20, "2nd Floor", "General medical ward");
        Ward peds = createWard("Pediatrics",      15, "3rd Floor", "Children's ward");
        Ward ortho = createWard("Orthopedics",    12, "4th Floor", "Bone and joint care");

        wardRepository.saveAll(List.of(icu, gen, peds, ortho));

        // ── Beds ───────────────────────────────────────────────────────────
        createBedsForWard(icu,   "ICU",   10);
        createBedsForWard(gen,   "GEN",   20);
        createBedsForWard(peds,  "PED",   15);
        createBedsForWard(ortho, "ORT",   12);

        log.info("✅ Seeded {} wards and {} beds.", wardRepository.count(), bedRepository.count());
    }

    private Ward createWard(String name, int totalBeds, String floor, String desc) {
        Ward w = new Ward();
        w.setName(name);
        w.setTotalBeds(totalBeds);
        w.setFloor(floor);
        w.setDescription(desc);
        return w;
    }

    private void createBedsForWard(Ward ward, String prefix, int count) {
        for (int i = 1; i <= count; i++) {
            Bed bed = new Bed();
            bed.setBedNumber(prefix + String.format("%02d", i));
            bed.setWard(ward);
            // Sprinkle some non-available statuses for realism
            if (i % 5 == 0) {
                bed.setStatus(BedStatus.MAINTENANCE);
            } else if (i % 7 == 0) {
                bed.setStatus(BedStatus.OCCUPIED);
            } else {
                bed.setStatus(BedStatus.AVAILABLE);
            }
            bed.setLastUpdated(LocalDateTime.now());
            bedRepository.save(bed);
        }
    }
}
