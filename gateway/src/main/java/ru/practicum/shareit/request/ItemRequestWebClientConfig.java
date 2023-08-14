package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class ItemRequestWebClientConfig {

    @Value("${shareit-server.url}")
    private String serviceUrl;

//    @Value("${itemRequestAppName}")
//    private String itemRequestAppName;

    @Value("${itemRequestApiPrefix}")
    private String itemRequestApiPrefix;

//    private static final String API_PREFIX = "/requests";

    @Bean
    public ItemRequestClient itemRequestClient(RestTemplateBuilder builder) {
        var restTemplate =
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl + itemRequestApiPrefix))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build();

        ItemRequestClient client = new ItemRequestClient(restTemplate);
//        client.setAppName(itemRequestAppName);
        return client;
    }

}
