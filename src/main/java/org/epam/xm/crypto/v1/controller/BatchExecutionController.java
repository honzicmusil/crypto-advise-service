package org.epam.xm.crypto.v1.controller;

import org.epam.xm.crypto.service.BatchExecutionService;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;

/**
 * Controller that provides endpoints related to batch execution for the crypto module.
 * Exposes functionalities like manually triggering the batch and fetching the latest batch execution result.
 */
@RestController
@RequestMapping("/api/v1/crypto/batch")
public class BatchExecutionController {

    private final BatchExecutionService batchExecutionService;

    /**
     * Constructor that initializes the BatchExecutionController with its dependencies.
     *
     * @param batchExecutionService Service that provides batch execution-related operations.
     */
    @Autowired
    public BatchExecutionController(BatchExecutionService batchExecutionService) {
        this.batchExecutionService = batchExecutionService;
    }

    /**
     * Endpoint to manually trigger the CSV import batch job.
     *
     * @return {@link ResponseEntity} indicating that the request has been accepted.
     * @throws JobExecutionException If there's an issue while triggering the batch process.
     */
    @Operation(summary = "Endpoint which allows manual trigger of CSV import batch job.")
    @PostMapping("/trigger-batch")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RateLimiter(name = "restEndpointRateLimiter")
    public ResponseEntity<HttpStatus> triggerBatch() throws JobExecutionException {
        // Trigger the batch job using the service
        batchExecutionService.triggerReload();
        // Return response indicating the request has been accepted for processing
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     * Fetches the result of the most recent CSV import batch job execution.
     *
     * @return {@link ResponseEntity} with details of the latest batch execution.
     * @throws NoSuchJobInstanceException If there's no record of any job instance.
     */
    @Operation(summary = "Endpoint which returns result of latest CSV import job execution.")
    @GetMapping("/last-batch-info")
    @ResponseStatus(HttpStatus.OK)
    @RateLimiter(name = "restEndpointRateLimiter")
    public ResponseEntity<JobExecution> getLastBatchExecutionResult() throws NoSuchJobInstanceException {
        // Fetch the latest batch job execution details using the service
        JobExecution jobExecution = batchExecutionService.getLastBatchProcess();
        // Return response with the latest batch job execution details
        return new ResponseEntity<>(jobExecution, HttpStatus.OK);
    }
}

