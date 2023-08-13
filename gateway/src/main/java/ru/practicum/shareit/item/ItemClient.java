package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDtoToCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoToUpdate;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public ResponseEntity<Object> createItem(Integer userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Integer userId, Integer itemId, ItemDtoToUpdate itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItemWithBookingById(Integer userId, Integer itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItemsWithBookingByOwner(Integer userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllItemsByAnyUserByText(Integer userId, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", Long.valueOf(userId), parameters);
    }

    public void deleteItem(Integer userId, Integer itemId) {
        delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> createItemComment(Integer userId, Integer itemId, CommentDtoToCreate commentDtoToCreate) {
        return post("/" + itemId + "/comment", userId, commentDtoToCreate);
    }
}
