package com.akshathsaipittala.streamspace.services.resilience;

@FunctionalInterface
public interface RetryExecutor<T> {

    T run() throws Exception;

}