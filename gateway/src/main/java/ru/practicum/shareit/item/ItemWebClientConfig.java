package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class ItemWebClientConfig {

    @Value("${shareit-server.url}")
    private String serviceUrl;

//    @Value("${itemAppName}")
//    private String itemAppName;

    @Value("${itemApiPrefix}")
    private String itemApiPrefix;

//    private static final String API_PREFIX = "/items";

    @Bean
    public ItemClient itemClient(RestTemplateBuilder builder) {
        var restTemplate =
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl + itemApiPrefix))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build();

        ItemClient client = new ItemClient(restTemplate);
//        client.setAppName(itemAppName);
        return client;
    }

}
