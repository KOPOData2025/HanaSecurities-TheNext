package com.hanati.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 처리 설정
 * 백그라운드 API 동기화 작업을 위한 스레드 풀 설정
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    @Bean(name = "apiSyncExecutor")
    public Executor apiSyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);                  // 기본 스레드 수
        executor.setMaxPoolSize(10);                  // 최대 스레드 수
        executor.setQueueCapacity(100);               // 큐 용량
        executor.setThreadNamePrefix("api-sync-");    // 스레드 이름 접두사
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();

        log.info("비동기 API 동기화 Executor 초기화 완료");
        return executor;
    }
}
