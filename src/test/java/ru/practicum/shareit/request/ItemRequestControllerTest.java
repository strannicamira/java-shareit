package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.LastBooking;
import ru.practicum.shareit.booking.NextBooking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentItemDto;
import ru.practicum.shareit.item.*;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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
import static ru.practicum.shareit.util.Constants.TIME_PATTERN;

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController controller;
    private ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;


    private ItemRequest itemRequestToCreate;
    private ItemRequestDto createdItemRequestDto;
    private ItemRequestDto gotItemRequestDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat(TIME_PATTERN);
        mapper.setDateFormat(df);

        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        ItemDto itemDto = new ItemDto(1,"Something", "Some thing", Boolean.TRUE, 1);
        List<ItemDto> itemDtos = Arrays.asList(itemDto);
        LocalDateTime now = LocalDateTime.now();
        itemRequestToCreate = makeItemRequest();
        createdItemRequestDto = makeItemRequestDto(1, "My first request", now);
        gotItemRequestDto = makeItemRequestDto(1, "My first request", now, itemDtos);

    }

    private static ItemRequestDto makeItemRequestDto(Integer id, String description, LocalDateTime created) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(id);
        dto.setDescription(description);
        dto.setCreated(created);
        return dto;
    }

    private static ItemRequest makeItemRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("My first request");
        return itemRequest;
    }

    private ItemDto makeItemDto(String name, String description, Boolean available, Integer requestId) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setRequestId(requestId);
        return dto;
    }

    private static ItemRequestDto makeItemRequestDto(Integer id, String description, LocalDateTime created, List<ItemDto> itemDtos) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(id);
        dto.setDescription(description);
        dto.setCreated(created);
        dto.setItems(itemDtos);
        return dto;
    }

    @Order(1)
    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.create(anyInt(), any()))
                .thenReturn(createdItemRequestDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestToCreate))
                        .header("X-Sharer-User-Id", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdItemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.created", is(createdItemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.description", is(createdItemRequestDto.getDescription())));
    }


    @Order(2)
    @Test
    void getListItemRequestsByUserId() throws Exception {
        when(itemRequestService.get(anyInt()))
                .thenReturn(List.of(gotItemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(gotItemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].created", is(gotItemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].description", is(gotItemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].id", is(gotItemRequestDto.getItems().get(0).getId())))
                .andExpect(jsonPath("$[0].items[0].name", is(gotItemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(gotItemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(gotItemRequestDto.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(gotItemRequestDto.getItems().get(0).getRequestId())));
    }

    @Order(3)
    @Test
    void getItemRequestsByUserId() throws Exception {
        when(itemRequestService.get(anyInt(), anyInt()))
                .thenReturn(gotItemRequestDto);

        mockMvc.perform(get("/requests/{requestId}",1)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(gotItemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.created", is(gotItemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.description", is(gotItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.items[0].id", is(gotItemRequestDto.getItems().get(0).getId())))
                .andExpect(jsonPath("$.items[0].name", is(gotItemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].description", is(gotItemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(gotItemRequestDto.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$.items[0].requestId", is(gotItemRequestDto.getItems().get(0).getRequestId())));
    }

    @Order(4)
    @Test
    void getListAllItemRequests() throws Exception {
        when(itemRequestService.get(anyInt(),anyInt(),anyInt()))
                .thenReturn(List.of(gotItemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .queryParam("from","0")
                        .queryParam("size","20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(gotItemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].created", is(gotItemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].description", is(gotItemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].id", is(gotItemRequestDto.getItems().get(0).getId())))
                .andExpect(jsonPath("$[0].items[0].name", is(gotItemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(gotItemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(gotItemRequestDto.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(gotItemRequestDto.getItems().get(0).getRequestId())));
    }

}