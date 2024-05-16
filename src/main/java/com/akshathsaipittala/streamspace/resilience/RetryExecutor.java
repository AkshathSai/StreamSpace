package com.akshathsaipittala.streamspace.resilience;

@FunctionalInterface
public interface RetryExecutor<T> {

    T run() throws Exception;

}