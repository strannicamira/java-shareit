package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class UserWebClientConfig {

    @Value("${shareit-server.url}")
    private String serviceUrl;

//    @Value("${userAppName}")
//    private String userAppName;

    @Value("${userApiPrefix}")
    private String userApiPrefix;

//    private static final String API_PREFIX = "/users";

    @Bean
    public UserClient userClient(RestTemplateBuilder builder) {
        var restTemplate =
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl + userApiPrefix))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build();

        UserClient client = new UserClient(restTemplate);
//        client.setAppName(userAppName);
        return client;
    }

}
