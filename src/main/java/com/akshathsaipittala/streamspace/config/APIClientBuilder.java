package com.akshathsaipittala.streamspace.config;

import com.akshathsaipittala.streamspace.web.api.APIBayClient;
import com.akshathsaipittala.streamspace.web.api.MicrosoftStoreAPI;
import com.akshathsaipittala.streamspace.web.api.YTSAPIClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class APIClientBuilder {

    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .executor(executorService)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }

    @Bean
    YTSAPIClient ytsapiClient(RestClient restClient) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(YTSAPIClient.class);
    }

    @Bean
    APIBayClient apiBayClient(RestClient restClient) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(APIBayClient.class);
    }

    @Bean
    MicrosoftStoreAPI microsoftStoreAPI(RestClient restClient) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(MicrosoftStoreAPI.class);
    }

}
