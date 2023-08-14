package ru.practicum.shareit.request;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;

//@Service
public class ItemRequestClient extends BaseClient {
    public ItemRequestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(Integer userId, ItemRequestDto itemRequest) {
        return post("", userId, itemRequest);
    }

    public ResponseEntity<Object> get(Integer userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> get(Integer userId, Integer requestId) {
        return get("/" + requestId, userId);
    }


    public ResponseEntity<Object> get(Integer userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", Long.valueOf(userId), parameters);
    }
}
