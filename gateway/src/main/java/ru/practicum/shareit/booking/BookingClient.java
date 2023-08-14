package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

//@Service
public class BookingClient extends BaseClient {

    public BookingClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> getBookingsTmpl(long userId, String state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> createBooking(Integer userId, BookingDto bookingDto) {
        return post("", userId, bookingDto);

    }

    public ResponseEntity<Object> updateBooking(Integer userId, Integer bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved={approved}", Long.valueOf(userId), parameters, null);
    }

    public ResponseEntity<Object> getBooking(Integer userId, Integer bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getUserBookings(Integer userId, String state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", Long.valueOf(userId), parameters);
    }

    public ResponseEntity<Object> getItemsBookings(Integer userId, String state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", Long.valueOf(userId), parameters);
    }

    public void deleteBooking(Integer userId, Integer bookingId) {
        delete("/" + bookingId, userId);
    }

}
