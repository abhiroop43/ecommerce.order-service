package dev.abhiroopsantra.orderservice.exception;

import lombok.experimental.StandardException;

import java.io.Serial;

@StandardException
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
