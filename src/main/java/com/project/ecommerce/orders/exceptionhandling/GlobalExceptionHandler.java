package com.project.ecommerce.orders.exceptionhandling;

import com.project.ecommerce.orders.exceptionhandling.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for API errors.
 * Catches specific and generic exceptions and returns structured error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles cases where a requested resource (e.g., Order, Customer) was not found.
     *
     * @param ex the exception thrown
     * @return a 404 NOT FOUND response with error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    /**
     * Handles cases where stock is insufficient to fulfill an order.
     *
     * @param ex the exception thrown
     * @return a 400 BAD REQUEST response with error details
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiError> handleInsufficientStock(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage()));
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

