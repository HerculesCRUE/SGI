package org.crue.hercules.sgi.tp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Scheduled task configuration class
 */
@Configuration
public class SchedulingConfig {
  private static final int POOL_SIZE = 4;
  private static final String THREAD_NAME_PREFIX = "TaskSchedulerThreadPool-";

  @Bean
  public TaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    // Number of core threads in thread pool for scheduled task execution
    taskScheduler.setPoolSize(POOL_SIZE);
    taskScheduler.setRemoveOnCancelPolicy(true);
    taskScheduler.setThreadNamePrefix(THREAD_NAME_PREFIX);
    return taskScheduler;
  }

}
