package com.jh.dangerzone.config;

import com.jh.dangerzone.domain.Config;
import com.jh.dangerzone.domain.Frequency;
import com.jh.dangerzone.service.ServiceDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class DynamicSchedulingConfig implements SchedulingConfigurer {

    @Autowired
    private ServiceDispatcher serviceDispatcher;

    @Bean
    public Executor taskExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Value("${stationId}")
    private int stationId;

    @Value("${directoryLocation}")
    private String directoryLocation;

    @Value("${frequency}")
    private Frequency frequency;

    @PostConstruct
    public void init() throws IOException {

        File file = new File(directoryLocation);
        if(!file.exists() && !file.mkdir()){
            throw new IOException("Directory couldn't be found/created");
        }

    }


    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        Config config = new Config(stationId,directoryLocation,frequency);
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {

                        serviceDispatcher.handleRequest(config);
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext context) {
                        Optional<Date> lastCompletionTime =
                                Optional.ofNullable(context.lastCompletionTime());
                        Instant nextExecutionTime =
                                lastCompletionTime.orElseGet(Date::new).toInstant()
                                        .plusMillis(config.getFrequency().getMilis());
                        return Date.from(nextExecutionTime);
                    }
                }
        );
    }

}