package com.akshathsaipittala.streamspace.services.resilience;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RetryService<T> {

    //@Value("${retryAttempts}")
    private int retryAttempts = 3;
    //@Value("${timeToWait}")
    private int timeToWait = 1000;

    public T retry(RetryExecutor<T> retryExecutor) {

        while (shouldRetry()) {
            try {
                //log.info("Retrying...");
                T result = retryExecutor.run();
                if (result!=null) {
                    return result;
                }
                //return; // if successful, exit method
            } catch (Exception e) {
                retryAttempts--;
                if (shouldRetry()) {
                    log.info("Waiting for next retry...");
                    waitBeforeNextRetry();
                } else {
                    //throw e; // if all retries failed, throw the exception
                    log.error("Exception occurred while executing the method {} ", e);
                }
            }
        }

        return null; // if all retries failed
    }

    private boolean shouldRetry() {
        return retryAttempts > 0;
    }

    private void waitBeforeNextRetry() {
        try {
            Thread.sleep(timeToWait);
        } catch (Exception e) {
            log.info("Exception while waiting for next retry {} ", e);
            Thread.currentThread().interrupt();
        }
    }

}
