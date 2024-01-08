package com.akshathsaipittala.streamspace.config;

import com.akshathsaipittala.streamspace.utils.RuntimeHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.content.fs.io.FileSystemResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@EnableFilesystemStores
@RequiredArgsConstructor
public class SpringContentStorageConfig {

    final RuntimeHelper runtimeHelper;

    @Bean
    File filesystemRoot() {
        return new File(runtimeHelper.USER_HOME);
    }

    @Bean
    FileSystemResourceLoader fsResourceLoader() {
        return new FileSystemResourceLoader(filesystemRoot().getAbsolutePath());
    }
}