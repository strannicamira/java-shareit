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
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.ItemBookingDto;
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
    private BookingService bookingService;
    @InjectMocks
    private BookingController controller;
    private ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private BookingOutDto bookingOutDto;
    private BookingDto bookingDto;
    private BookingOutDto updatedBookingOutDto;

    @BeforeEach
    void setUp() {

        objectMapper = Jackson2ObjectMapperBuilder.json().build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        SimpleDateFormat df = new SimpleDateFormat(TIME_PATTERN);
        objectMapper.setDateFormat(df);

        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(ErrorHandler.class)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        LocalDateTime now = LocalDateTime.now();
        ItemBookingDto itemBookingDto = new ItemBookingDto(2, "Item for Booking");
        UserBookingDto userBookingDto = new UserBookingDto(2);
        bookingOutDto = new BookingOutDto(1, now.plusDays(1), now.plusDays(2), itemBookingDto, userBookingDto, BookingStatus.WAITING);
        bookingDto = new BookingDto(1, now.plusDays(1), now.plusDays(2), itemBookingDto.getId(), userBookingDto.getId(), BookingStatus.WAITING);
        updatedBookingOutDto = new BookingOutDto(1, now.plusDays(1), now.plusDays(2), itemBookingDto, userBookingDto, BookingStatus.APPROVED);
    }


    @Order(10)
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
                .andExpect(jsonPath("$.status", is(bookingOutDto.getStatus().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingOutDto.getItem().getName())))
        ;
    }

    @Order(11)
    @Test
    void createItem_thenThrowNotOwnerException() throws Exception {
        when(bookingService.createBooking(anyInt(), any()))
                .thenThrow(NotOwnerException.class);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(12)
    @Test
    void createItem_thenThrowNotAvailableException() throws Exception {
        when(bookingService.createBooking(anyInt(), any()))
                .thenThrow(NotAvailableException.class);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Order(13)
    @Test
    void createItem_thenThrowIllegalStateException() throws Exception {
        when(bookingService.createBooking(anyInt(), any()))
                .thenThrow(IllegalStateException.class);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
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
                .andExpect(jsonPath("$[0].status", is(bookingOutDto.getStatus().getName())))
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
                .andExpect(jsonPath("$.status", is(updatedBookingOutDto.getStatus().getName())))
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
                .andExpect(jsonPath("$.status", is(updatedBookingOutDto.getStatus().getName())))
                .andExpect(jsonPath("$.booker.id", is(updatedBookingOutDto.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(updatedBookingOutDto.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(updatedBookingOutDto.getItem().getName())));
    }

    @Order(5)
    @Test
    public void getUserItemsBookings() throws Exception {
        when(bookingService.getItemsBookings(anyInt(), anyString(), anyInt(), anyInt()))
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
                .andExpect(jsonPath("$[0].status", is(updatedBookingOutDto.getStatus().getName())))
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
}