package com.webapp.timeline.config.batch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Properties;

@Component
@ConfigurationProperties(prefix = "org.quartz")
@Setter
@Getter
public class QuartzProperties {
    private static final String PREFIX = "org.quartz";

    private Scheduler scheduler;
    private JobStore jobStore;
    private ThreadPool threadPool;

    @Setter
    @Getter
    public static class Scheduler {
        private String instanceId;
        private String instanceName;
        private String makeSchedulerThreadDaemon;
        private String interruptJobsOnShutdown;
    }

    @Setter
    @Getter
    public static class JobStore {
        private String clusterCheckinInterval;
        private String driverDelegateClass;
        private String isClustered;
        private String misfireThreshold;
        private String tablePrefix;
        private String useProperties;
    }

    @Setter
    @Getter
    public static class ThreadPool {
        private String threadCount;
        private String makeThreadsDaemons;
    }

    public Properties toProperties() throws IllegalAccessException {
        Properties properties = new Properties();
        getProperties(PREFIX, this, properties);
        return properties;
    }

    private void getProperties(String prefix, Object object, Properties properties) {
        Arrays.stream(object.getClass().getDeclaredFields()).filter(field -> !Modifier.isStatic(field.getModifiers()))
                .forEach(field -> {
                    field.setAccessible(true);

                    try {
                        makeStringProperties(prefix, object, properties, field);
                    }
                    catch(IllegalAccessException exception) {
                        exception.printStackTrace();
                    }
                });
    }

    private void makeStringProperties(String prefix, Object object, Properties properties, Field field) throws IllegalAccessException {
        Object value = field.get(object);
        if(value == null) {
            return;
        }

        if(String.class == field.getType()) {
            properties.put(prefix + "." + field.getName(), value);
            return;
        }

        getProperties(prefix + "." + field.getName(), value, properties);
    }
}
