package com.hospital.smartbedtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BedStatsResponse {
    private long total;
    private long available;
    private long occupied;
    private long maintenance;
    private long reserved;
    private double occupancyRate;
}
