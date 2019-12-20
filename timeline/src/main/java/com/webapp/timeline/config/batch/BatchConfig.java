package com.webapp.timeline.config.batch;

import com.webapp.timeline.config.JpaConfig;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.util.List;


@Configuration
@EnableConfigurationProperties(QuartzProperties.class)
@EnableBatchProcessing
public class BatchConfig {
    private JpaConfig dataSourceConfiguration;

    @Autowired
    public void setDataSoure(JpaConfig dataSourceConfiguration) {
        this.dataSourceConfiguration = dataSourceConfiguration;
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);

        return jobRegistryBeanPostProcessor;
    }

    @Bean
    public JobFactory jobFactory(AutowireCapableBeanFactory autowireCapableBeanFactory) {
        return new SpringBeanJobFactory() {

            @Override
            protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
                Object job = super.createJobInstance(bundle);
                autowireCapableBeanFactory.autowireBean(job);
                return job;
            }
        };
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(QuartzProperties quartzProperties,
                                                     JobFactory jobFactory,
                                                     Trigger[] registryTrigger) throws Exception {

        DataSource dataSource = this.dataSourceConfiguration.datasourceForScheduling();
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();

        factoryBean.setSchedulerName("Timeline-Scheduler");
        factoryBean.setJobFactory(jobFactory);
        factoryBean.setWaitForJobsToCompleteOnShutdown(true);
        factoryBean.setOverwriteExistingJobs(true);
        factoryBean.setQuartzProperties(quartzProperties.toProperties());
        factoryBean.setDataSource(dataSource);
        factoryBean.setTriggers(registryTrigger);

        return factoryBean;
    }

    @Bean
    public Trigger[] registryTrigger(List<CronTriggerFactoryBean> cronTriggerFactoryBeanList) {
        return cronTriggerFactoryBeanList.stream()
                                        .map(CronTriggerFactoryBean::getObject)
                                        .toArray(Trigger[]::new);
    }

    @Bean
    public SmartLifecycle gracefulShutdownHook(SchedulerFactoryBean schedulerFactoryBean) {
        return new SmartLifecycle() {
            private boolean isRunning = false;
            private final Logger logger = LoggerFactory.getLogger(this.getClass());

            @Override
            public boolean isAutoStartup() {
                return true;
            }

            @Override
            public void start() {
                logger.info("Quartz customed-Graceful Shutdown Hook started.");
                isRunning = true;
            }

            @Override
            public void stop(Runnable callback) {
                stop();
                logger.info("Spring Application Context is shutting down.");
                callback.run();
            }

            @Override
            public void stop() {
                isRunning = false;

                try {
                    logger.info("Quartz customed=Graceful Shutdown Hook stopped..");
                    schedulerFactoryBean.destroy();
                }
                catch (SchedulerException e) {

                    try {
                        logger.info("Error while shutting down Quartz : " + e.getMessage(), e);

                        schedulerFactoryBean.getScheduler().shutdown(false);
                    }
                    catch(SchedulerException exception) {
                        logger.error("Unable to shutdown Quartz scheduler.", exception);
                    }
                }
            }

            @Override
            public boolean isRunning() {
                return false;
            }

            @Override
            public int getPhase() {
                return Integer.MAX_VALUE;
            }
        };
    }
}
