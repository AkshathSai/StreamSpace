package com.akshathsaipittala.streamspace.config;

import com.akshathsaipittala.streamspace.web.api.APIBayClient;
import com.akshathsaipittala.streamspace.web.api.YTSAPIClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class APIClientConfig {

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder
                .requestFactory(new JdkClientHttpRequestFactory())
                .build();
    }

    @Bean
    YTSAPIClient ytsapiClient(RestClient restClient) {
        return HttpServiceProxyFactory
                .builder()
                .exchangeAdapter(RestClientAdapter.create(restClient))
                .build()
                .createClient(YTSAPIClient.class);
    }

    @Bean
    APIBayClient apiBayClient(RestClient restClient) {
        return HttpServiceProxyFactory
                .builder()
                .exchangeAdapter(RestClientAdapter.create(restClient))
                .build()
                .createClient(APIBayClient.class);
    }

}
