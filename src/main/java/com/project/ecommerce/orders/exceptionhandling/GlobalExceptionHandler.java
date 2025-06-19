package com.project.ecommerce.orders.exceptionhandling;

import com.project.ecommerce.orders.exceptionhandling.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler for API errors.
 * Catches specific and generic exceptions and returns structured error responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles cases where a requested resource (e.g., Order, Customer) was not found.
     *
     * @param ex the exception thrown
     * @return a 404 NOT FOUND response with error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    /**
     * Handles cases where stock is insufficient to fulfill an order.
     *
     * @param ex the exception thrown
     * @return a 400 BAD REQUEST response with error details
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiError> handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    /**
     * Handles cases where an order is not found.
     *
     * @param ex the exception thrown
     * @param request the HTTP request to get path info
     * @return a 404 NOT FOUND response with structured error
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiError> handleOrderNotFound(OrderNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    /**
     * Handles cases where an order status update is invalid.
     *
     * @param ex the exception thrown
     * @param request the HTTP request to get path info
     * @return a 400 BAD REQUEST response with structured error
     */
    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ApiError> handleInvalidStatus(InvalidOrderStatusException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    /**
     * Handles validation errors when @Valid fails on incoming request DTOs.
     * This method is triggered when a MethodArgumentNotValidException is thrown,
     * which occurs if request body fields violate validation constraints (e.g., @NotNull, @Min).
     *
     * @param ex the MethodArgumentNotValidException containing validation error details
     * @param request the HttpServletRequest to get request URI for error path
     * @return ResponseEntity containing ApiError with BAD_REQUEST status and detailed field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        // Extract all field errors, format as "fieldName: errorMessage", and join with semicolons
        String combinedMessages = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        // Build and return a structured ApiError response with HTTP 400 status
        return buildErrorResponse(combinedMessages, HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildErrorResponse(
                "Malformed JSON request: " + ex.getMostSpecificCause().getMessage(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI());
    }

    /**
     * Handles any unhandled exceptions (generic errors).
     * Skips handling for API docs and actuator paths so that Spring default handlers can take over.
     *
     * @param exception the exception thrown
     * @param request the HTTP request to get path info
     * @return a 500 INTERNAL SERVER ERROR response with structured error
     * @throws Exception rethrows if path should be handled by Spring itself
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception exception, HttpServletRequest request) throws Exception {
        String path = request.getRequestURI();
        log.error("Caught in generic handler: {}", exception.getClass().getName(), exception);
        // Skip handling for API docs / actuator endpoints so Spring can handle them
        if (path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/actuator")) {
            throw exception;  // Let Spring handle these internally
        }

        return buildErrorResponse(
                "Internal server error: " + exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                path
        );
    }

    /**
     * Utility method to build structured API error responses.
     *
     * @param message the error message
     * @param status the HTTP status to return
     * @param path the request URI where the error occurred
     * @return ResponseEntity with ApiError body
     */
    private ResponseEntity<ApiError> buildErrorResponse(String message, HttpStatus status, String path) {
        ApiError error = ApiError.builder()
                .status(status)
                .message(message)
                .timestamp(java.time.LocalDateTime.now())
                .path(path)
                .build();
        return new ResponseEntity<>(error, status);
    }
}

