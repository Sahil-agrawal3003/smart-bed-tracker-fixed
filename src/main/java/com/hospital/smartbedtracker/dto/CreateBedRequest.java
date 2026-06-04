package com.hospital.smartbedtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBedRequest {

    @NotBlank(message = "Bed number is required")
    private String bedNumber;

    @NotNull(message = "Ward ID is required")
    private Long wardId;
}
