package org.example.zaivki.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate

@Configuration
@EnableRetry
class RetryConfig {

    @Bean
    fun retryTemplate(): RetryTemplate {
        val retryTemplate = RetryTemplate()

        val retryPolicy = SimpleRetryPolicy(3)
        retryTemplate.setRetryPolicy(retryPolicy)

        val backOffPolicy = FixedBackOffPolicy()
        backOffPolicy.backOffPeriod = 2000L
        retryTemplate.setBackOffPolicy(backOffPolicy)

        return retryTemplate
    }
}