package com.player.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private String cause;
}
