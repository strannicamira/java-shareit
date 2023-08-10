package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private UserDto createdUserDto;
    private UserDto updatedUserDto;
    private UserDto userDtoToCreate;
    private UserDto userDtoToUpdate;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        userDtoToCreate = new UserDto();
        userDtoToCreate.setName("user");
        userDtoToCreate.setEmail("user@user.com");

        createdUserDto = new UserDto(
                1,
                "user",
                "user@user.com");

        userDtoToUpdate = new UserDto();
        userDtoToUpdate.setName("update");
        userDtoToUpdate.setEmail("update@user.com");

        updatedUserDto = new UserDto(
                1,
                "update",
                "uupdate@user.com");
    }

    @Order(1)
    @Test
    void create() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(createdUserDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdUserDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(createdUserDto.getName())))
                .andExpect(jsonPath("$.email", is(createdUserDto.getEmail())));
    }

    @Order(2)
    @Test
    void findAll() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(createdUserDto));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(createdUserDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(createdUserDto.getName())))
                .andExpect(jsonPath("$[0].email", is(createdUserDto.getEmail())))
                .andExpect(jsonPath("$[0].email", is(createdUserDto.getEmail())));
    }

    @Order(3)
    @Test
    public void update() throws Exception {
        when(userService.updateUser(anyInt(),any()))
                .thenReturn(updatedUserDto);

        mvc.perform(MockMvcRequestBuilders
                        .patch("/users/{id}", 1)
                        .content(mapper.writeValueAsString(userDtoToUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUserDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(updatedUserDto.getName())))
                .andExpect(jsonPath("$.email", is(updatedUserDto.getEmail())));
    }

    @Order(4)
    @Test
    void findById() throws Exception {
        when(userService.getUser(anyInt()))
                .thenReturn(createdUserDto);

        mvc.perform(MockMvcRequestBuilders
                        .get("/users/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdUserDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(createdUserDto.getName())))
                .andExpect(jsonPath("$.email", is(createdUserDto.getEmail())));
    }


    @Order(5)
    @Test
    public void deleteUserById() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", 1))
                .andExpect(status().isOk());
    }
}