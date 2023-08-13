package ru.practicum.shareit.item;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.LastBooking;
import ru.practicum.shareit.booking.NextBooking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentItemDto;
import ru.practicum.shareit.exception.ErrorHandler;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.TIME_PATTERN;

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController controller;

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private ItemDto createdItemDto;
    private ItemDto updatedItemDto;
    private ItemDto itemDtoToCreate;
    private ItemDtoForUpdate itemDtoToUpdate;
    private ItemWithBookingDto itemWithBookingDto;
    private Comment comment;
    private CommentItemDto createdComment;
    private ItemDto itemDtoToCreateWithoutAvailable;

    @BeforeEach
    void setUp() {

        objectMapper = Jackson2ObjectMapperBuilder.json().build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        SimpleDateFormat df = new SimpleDateFormat(TIME_PATTERN);
        objectMapper.setDateFormat(df);

        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .setControllerAdvice(ErrorHandler.class)
                .build();

        itemDtoToCreate = makeItemDto("Item", "Item...", Boolean.TRUE);
        createdItemDto = new ItemDto(1, "Item", "Item...", Boolean.TRUE, null);
        itemDtoToUpdate = new ItemDtoForUpdate(1, "UpdateItem", "UpdateItem...", Boolean.TRUE);
        updatedItemDto = new ItemDto(1, "UpdateItem", "UpdateItem...", Boolean.TRUE, null);
        itemWithBookingDto = makeItemWithBookingDto(createdItemDto);
        comment = makeComment("Comment");
        createdComment = makeCommentItemDto(1, comment, "User Name");
        itemDtoToCreateWithoutAvailable = makeItemDto("Item", "Item...", null);

    }


    @Order(1)
    @Test
    void createItem() throws Exception {
        when(itemService.createItem(anyInt(), any()))
                .thenReturn(createdItemDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDtoToCreate))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdItemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(createdItemDto.getName())))
                .andExpect(jsonPath("$.description", is(createdItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(createdItemDto.getAvailable())));
    }

    @Order(2)
    @Test
    void findAll() throws Exception {
        when(itemService.getUserItemsWithBooking(anyInt()))
                .thenReturn(List.of(itemWithBookingDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemWithBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemWithBookingDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemWithBookingDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemWithBookingDto.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemWithBookingDto.getLastBooking().getId())))
                .andExpect(jsonPath("$[0].lastBooking.bookerId", is(itemWithBookingDto.getLastBooking().getBookerId())))
                .andExpect(jsonPath("$[0].nextBooking.id", is(itemWithBookingDto.getNextBooking().getId())))
                .andExpect(jsonPath("$[0].nextBooking.bookerId", is(itemWithBookingDto.getNextBooking().getBookerId())));
    }

    @Order(3)
    @Test
    public void update() throws Exception {
        when(itemService.updateItem(anyInt(), any(), anyInt()))
                .thenReturn(updatedItemDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/items/{id}", 1)
                        .content(objectMapper.writeValueAsString(itemDtoToUpdate))
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(updatedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(updatedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(updatedItemDto.getAvailable())));
    }


    @Order(4)
    @Test
    void findById() throws Exception {
        when(itemService.getItemWithBooking(anyInt(), anyInt()))
                .thenReturn(itemWithBookingDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/items/{id}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemWithBookingDto.getName())))
                .andExpect(jsonPath("$.description", is(itemWithBookingDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemWithBookingDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemWithBookingDto.getLastBooking().getId())))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemWithBookingDto.getLastBooking().getBookerId())))
                .andExpect(jsonPath("$.nextBooking.id", is(itemWithBookingDto.getNextBooking().getId())))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(itemWithBookingDto.getNextBooking().getBookerId())));
    }

    @Order(5)
    @Test
    public void getUserItemsByText() throws Exception {
        when(itemService.getUserItems(anyInt(), anyString()))
                .thenReturn(List.of(createdItemDto));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "1")
                        .queryParam("text", "anytext"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(createdItemDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(createdItemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(createdItemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(createdItemDto.getAvailable())));


    }

    @Order(6)
    @Test
    public void createItemComment() throws Exception {

        when(itemService.createItemComment(anyInt(), anyInt(), any()))
                .thenReturn(createdComment);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/items/{itemId}/comment", 1)
                        .content(objectMapper.writeValueAsString(comment))
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdComment.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(createdComment.getText())))
                .andExpect(jsonPath("$.authorName", is(createdComment.getAuthorName())))
                .andExpect(jsonPath("$.created".toString(), is(createdComment.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).toString())));


    }

    @Order(7)
    @Test
    public void deleteItemById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }


    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }

    private ItemWithBookingDto makeItemWithBookingDto(ItemDto itemDto) {
        ItemWithBookingDto dto = new ItemWithBookingDto();
        dto.setId(itemDto.getId());
        dto.setName(itemDto.getName());
        dto.setDescription(itemDto.getDescription());
        dto.setAvailable(itemDto.getAvailable());
        dto.setLastBooking(new LastBooking(1, 1));
        dto.setNextBooking(new NextBooking(2, 2));
        return dto;
    }

    private Comment makeComment(String text) {
        Comment comment = new Comment();
        comment.setText(text);
        return comment;
    }

    private CommentItemDto makeCommentItemDto(Integer id, Comment comment, String authorName) {
        CommentItemDto dto = new CommentItemDto();
        dto.setId(id);
        dto.setText(comment.getText());
        dto.setAuthorName(authorName);
        dto.setCreated(LocalDateTime.now());
        return dto;
    }
}