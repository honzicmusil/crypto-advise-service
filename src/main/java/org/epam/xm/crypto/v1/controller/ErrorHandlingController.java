package org.epam.xm.crypto.v1.controller;

import org.springdoc.api.ErrorMessage;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * A centralized error handling controller using {@link ControllerAdvice} to handle exceptions across
 * the whole application. It captures specific exceptions and translates them into HTTP responses.
 */
@ControllerAdvice
public class ErrorHandlingController {

    /**
     * Handles instances where a requested job does not exist.
     *
     * @param e The exception instance of {@link NoSuchJobInstanceException}.
     * @return A {@link ResponseEntity} containing an error message and an HTTP NOT FOUND status.
     */
    @ExceptionHandler(NoSuchJobInstanceException.class)
    public ResponseEntity<ErrorMessage> handleNoSuchJobInstanceException(NoSuchJobInstanceException e) {
        // Create error message from the exception
        ErrorMessage message = new ErrorMessage(e.getMessage());
        // Return response entity with the error message and a 404 Not Found status
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles instances where there is an error executing a job.
     *
     * @param e The exception instance of {@link JobExecutionException}.
     * @return A {@link ResponseEntity} containing an error message and an HTTP INTERNAL SERVER ERROR status.
     */
    @ExceptionHandler(JobExecutionException.class)
    public ResponseEntity<ErrorMessage> handleJobExecutionException(JobExecutionException e) {
        // Create error message from the exception
        ErrorMessage message = new ErrorMessage(e.getMessage());
        // Return response entity with the error message and a 500 Internal Server Error status
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles instances where an illegal argument is provided.
     *
     * @param e The exception instance of {@link IllegalArgumentException}.
     * @return A {@link ResponseEntity} containing an error message and an HTTP BAD REQUEST status.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleJobExecutionException(IllegalArgumentException e) {
        // Create error message from the exception
        ErrorMessage message = new ErrorMessage(e.getMessage());
        // Return response entity with the error message and a 400 Bad Request status
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
