package ru.practicum.shareit.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoToUpdate;

import javax.validation.Valid;

//@Service
public class UserClient extends BaseClient {
    public UserClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return post("", userDto);
    }

    public ResponseEntity<Object> updateUser(Integer id, @Valid UserDtoToUpdate userDto) {
        return patch("/" + id, userDto);
    }

    public ResponseEntity<Object> getAllUsersById(Integer id) {
        return get("/" + id);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("");
    }

    public void deleteUserById(Integer id) {
        delete("/" + id);
    }
}
