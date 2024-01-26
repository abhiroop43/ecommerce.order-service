package dev.abhiroopsantra.orderservice.handler;

import dev.abhiroopsantra.orderservice.dto.ApiResponse;
import dev.abhiroopsantra.orderservice.exception.BadRequestException;
import dev.abhiroopsantra.orderservice.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // handle internal server errors
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse> handleException(Exception e) {
        log.error("Internal Server Error: ", e);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.errCode = "500";
        apiResponse.errMessage = e.getMessage();
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // handle bad request errors
    @ExceptionHandler(BadRequestException.class)
    ResponseEntity<ApiResponse> handleBadRequestException(BadRequestException e) {
        log.error("Bad Request: ", e);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.errCode = "400";
        apiResponse.errMessage = e.getMessage();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    // handle not found errors
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiResponse> handleNotFoundException(NotFoundException e) {
        log.error("Not Found: ", e);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.errCode = "404";
        apiResponse.errMessage = e.getMessage();
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }
}
