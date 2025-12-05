package com.api.veiculos.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiError {
    private int status;
    private String error;
    private String message;
    private String path;
}
