package by.zuevvlad.wialontransport.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class SchedulingConfiguration {

    @Bean(name = "taskScheduler")
    public TaskScheduler createTaskScheduler(@Value("${scheduler.thread-pool-task-scheduler.size}") final int sizeThreadPool,
                                             @Value("${scheduler.thread-pool-task-scheduler.thread-prefix-name}") final String threadNamePrefix) {
        final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(sizeThreadPool);
        threadPoolTaskScheduler.setThreadNamePrefix(threadNamePrefix);
        return threadPoolTaskScheduler;
    }
}
