package com.hospital.smartbedtracker.exception;

public class BedNotAvailableException extends RuntimeException {
    public BedNotAvailableException(String message) {
        super(message);
    }
}
