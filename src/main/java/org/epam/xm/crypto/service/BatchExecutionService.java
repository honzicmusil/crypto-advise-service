package org.epam.xm.crypto.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing and triggering the batch processes related to importing crypto CSV data.
 */
@Service
public class BatchExecutionService {

    // Dependencies for exploring job executions, launching new jobs, and the specific crypto CSV import job.
    private final JobExplorer jobExplorer;
    private final JobLauncher jobLauncher;
    private final Job cryptoCsvImportJob;

    /**
     * Constructor to inject the necessary dependencies.
     *
     * @param jobExplorer Tool to inspect executed batch jobs.
     * @param jobLauncher Component for running or restarting Spring Batch jobs.
     * @param cryptoCsvImportJob The Spring Batch job that manages crypto CSV imports.
     */
    @Autowired
    public BatchExecutionService(JobExplorer jobExplorer, JobLauncher jobLauncher, Job cryptoCsvImportJob) {
        this.jobExplorer = jobExplorer;
        this.jobLauncher = jobLauncher;
        this.cryptoCsvImportJob = cryptoCsvImportJob;
    }

    /**
     * Triggers the crypto CSV import job to reload data.
     *
     * @throws JobExecutionException if an error occurs during the job execution.
     */
    public void triggerReload() throws JobExecutionException {
        jobLauncher.run(cryptoCsvImportJob, new JobParametersBuilder()
                .addString("JobId", String.valueOf(System.currentTimeMillis()))
                .toJobParameters());
    }

    /**
     * Retrieves the last execution details of the crypto CSV import job.
     *
     * @return The details of the last executed batch process.
     * @throws NoSuchJobInstanceException if no instance of the specified job (cryptoCsvImportJob) is found.
     */
    public JobExecution getLastBatchProcess() throws NoSuchJobInstanceException {
        JobInstance lastJobInstance = jobExplorer.getLastJobInstance("cryptoCsvImportJob");
        if (lastJobInstance == null) {
            throw new NoSuchJobInstanceException("No such job instance (cryptoCsvImportJob) found.");
        }
        return jobExplorer.getLastJobExecution(lastJobInstance);
    }

    /**
     * Scheduled method to automatically trigger the CSV import job.
     * The job is scheduled to run at 1 AM every day based on the Asia/Nicosia timezone.
     *
     * @throws JobExecutionException If there's an issue while triggering the batch process.
     */
    @Scheduled(cron = "0 0 1 * * ?", zone = "Asia/Nicosia")
    public void scheduledTriggerReload() throws JobExecutionException {
        // Run the cryptoCsvImportJob
        jobLauncher.run(
                cryptoCsvImportJob,
                // Create a unique job parameter using the current timestamp to ensure the job instance is always unique
                new JobParametersBuilder()
                        .addString("JobId", String.valueOf(System.currentTimeMillis()))
                        .toJobParameters()
        );
    }
}
