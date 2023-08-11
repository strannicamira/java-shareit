package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentItemDto;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.UserBookingDto;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.TIME_PATTERN;

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingControllerTest {
    @Mock
    private ItemService itemService;

    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController controller;

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private ItemDto createdItemDto;
    private ItemDto updatedItemDto;
    private ItemDto itemDtoToCreate;
    private ItemDtoForUpdate itemDtoToUpdate;
    private ItemWithBookingDto itemWithBookingDto;
    private Comment comment;
    private CommentItemDto createdComment;
    private BookingOutDto bookingOutDto;
    private BookingDto bookingDto;
    private BookingOutDto updatedBookingOutDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();


//        ObjectMapper
        objectMapper = Jackson2ObjectMapperBuilder.json().build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

//        ObjectMapper objectMapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat(TIME_PATTERN);
        objectMapper.setDateFormat(df);

//        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

//        this.boardProcessorController = new MyController();
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        itemDtoToCreate = makeItemDto("Item", "Item...");
        createdItemDto = new ItemDto(1, "Item", "Item...", Boolean.TRUE, null);
        itemDtoToUpdate = new ItemDtoForUpdate(1, "UpdateItem", "UpdateItem...", Boolean.TRUE);
        updatedItemDto = new ItemDto(1, "UpdateItem", "UpdateItem...", Boolean.TRUE, null);
        itemWithBookingDto = makeItemWithBookingDto(createdItemDto);
        comment = makeComment("Comment");
        createdComment = makeCommentItemDto(1, comment, "User Name");
        LocalDateTime now = LocalDateTime.now();
        ItemBookingDto itemBookingDto = new ItemBookingDto(2, "Item for Booking");
        UserBookingDto userBookingDto = new UserBookingDto(2);
        bookingOutDto = new BookingOutDto(1, now.plusDays(1), now.plusDays(2), itemBookingDto, userBookingDto, BookingStatus.WAITING);
        bookingDto = new BookingDto(1, now.plusDays(1), now.plusDays(2), itemBookingDto.getId(), userBookingDto.getId(), BookingStatus.WAITING);
        updatedBookingOutDto = new BookingOutDto(1, now.plusDays(1), now.plusDays(2), itemBookingDto, userBookingDto, BookingStatus.APPROVED);
//        updatedbookingOutDto = new BookingOutDto(1, now.plusDays(1), now.plusDays(2), itemBookingDto, userBookingDto, BookingStatus.WAITING);


    }


    @Order(1)
    @Test
    void createItem() throws Exception {
        when(bookingService.createBooking(anyInt(), any()))
                .thenReturn(bookingOutDto);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(bookingOutDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingOutDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status.name", is(bookingOutDto.getStatus().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingOutDto.getItem().getName())))
        ;
    }

    @Order(2)
    @Test
    void findAll() throws Exception {
        when(bookingService.getUserBookings(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "20")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingOutDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].start", is(bookingOutDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", is(bookingOutDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].status.name", is(bookingOutDto.getStatus().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingOutDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].item.id", is(bookingOutDto.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", is(bookingOutDto.getItem().getName())));
    }

    @Order(3)
    @Test
    public void update() throws Exception {
        when(bookingService.updateBooking(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(updatedBookingOutDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .queryParam("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedBookingOutDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(updatedBookingOutDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(updatedBookingOutDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status.name", is(updatedBookingOutDto.getStatus().getName())))
                .andExpect(jsonPath("$.booker.id", is(updatedBookingOutDto.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(updatedBookingOutDto.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(updatedBookingOutDto.getItem().getName())));
    }


    @Order(4)
    @Test
    void findById() throws Exception {
        when(bookingService.getBooking(anyInt(), anyInt()))
                .thenReturn(updatedBookingOutDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedBookingOutDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(updatedBookingOutDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(updatedBookingOutDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status.name", is(updatedBookingOutDto.getStatus().getName())))
                .andExpect(jsonPath("$.booker.id", is(updatedBookingOutDto.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(updatedBookingOutDto.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(updatedBookingOutDto.getItem().getName())));
    }

    @Order(5)
    @Test
    public void getUserItemsBookings() throws Exception {
        when(bookingService.getUserItemsBookings(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(updatedBookingOutDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .header("X-Sharer-User-Id", "1")
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(updatedBookingOutDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].start", is(updatedBookingOutDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].end", is(updatedBookingOutDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$[0].status.name", is(updatedBookingOutDto.getStatus().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(updatedBookingOutDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].item.id", is(updatedBookingOutDto.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", is(updatedBookingOutDto.getItem().getName())));
    }


      @Order(7)
      @Test
      public void deleteItemById() throws Exception {
          mockMvc.perform(delete("/bookings/{bookingId}", 1)
                          .header("X-Sharer-User-Id", "1"))
                  .andExpect(status().isOk());
      }


    private ItemDto makeItemDto(String name, String description) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(Boolean.TRUE);
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
//        dto.setCreated(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.NANOS));

//        log.info("getCreated = " + dto.getCreated().toString());
        return dto;
    }


}