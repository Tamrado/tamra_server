package com.webapp.timeline.config.batch;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@EnableBatchProcessing
public class BatchJobRunner implements Job {
    private static final Logger logger = LoggerFactory.getLogger(BatchJobRunner.class);
    private JobLocator jobLocator;
    private JobLauncher jobLauncher;

    @Autowired
    public BatchJobRunner(JobLocator jobLocator, JobLauncher jobLauncher) {
        this.jobLocator = jobLocator;
        this.jobLauncher = jobLauncher;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            String jobName = BatchConnector.getJobName(context.getMergedJobDataMap());
            logger.info("Batch Job Started -- : " + jobName);

            JobParameters jobParameters = BatchConnector.getMergedJobParameters(context);
            this.jobLauncher.run(this.jobLocator.getJob(jobName), jobParameters);
            logger.info("Batch Job Completed -- : " + jobName);
        }
        catch(NoSuchJobException
            | JobExecutionAlreadyRunningException
            | JobRestartException
            | JobParametersInvalidException
            | JobInstanceAlreadyCompleteException
            | SchedulerException e) {

            logger.error("Exception while Job runs! : " + e.getCause());
            throw new JobExecutionException();
        }
    }
}
