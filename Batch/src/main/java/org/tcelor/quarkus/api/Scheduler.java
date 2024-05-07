package org.tcelor.quarkus.api;

import java.util.Properties;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jberet.schedule.JobScheduleConfig;
import org.jberet.schedule.JobScheduleConfigBuilder;
import org.jberet.schedule.JobScheduler;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.batch.operations.JobOperator;
import jakarta.ejb.ScheduleExpression;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class Scheduler {
    @Inject
    public JobScheduler jobScheduler;

    @Inject
    public JobOperator jobOperator;

    @ConfigProperty(name = "batch.scheduled", defaultValue = "true")
    public Boolean isScheduled;

    void onStart(@Observes StartupEvent startupEvent) {
        if (isScheduled) {
            Log.info("Launching by scheduler ...");
            ScheduleExpression schedule = new ScheduleExpression()
                    .second("*"); // Run every 10 seconds
            final JobScheduleConfig scheduleConfig = JobScheduleConfigBuilder.newInstance()
                    .jobName("job")
                    .initialDelay(0)
                    .scheduleExpression(schedule)
                    .persistent(true)
                    .build();
            jobScheduler.schedule(scheduleConfig);
        } else {
            Log.info("Launching by JobOperator ...");
            jobOperator.start("job", new Properties());        }
    }
}