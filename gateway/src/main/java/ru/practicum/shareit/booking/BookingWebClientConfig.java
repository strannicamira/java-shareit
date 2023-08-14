package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class BookingWebClientConfig {

    @Value("${shareit-server.url}")
    private String serviceUrl;

//    @Value("${bookingAppName}")
//    private String bookingAppName;

    @Value("${bookingApiPrefix}")
    private String bookingApiPrefix;

//    private static final String API_PREFIX = "/bookings";

    @Bean
    public BookingClient bookingClient(RestTemplateBuilder builder) {
        var restTemplate =
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl + bookingApiPrefix))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build();

        BookingClient client = new BookingClient(restTemplate);
//        client.setAppName(bookingAppName);
        return client;
    }

}
