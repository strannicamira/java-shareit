package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public enum BookingState {
    CURRENT(1, "CURRENT"),
    PAST(2, "PAST"),
    FUTURE(3, "FUTURE"),
    WAITING(4, "WAITING"),
    REJECTED(5, "REJECTED"),
    ALL(6, "ALL");

    private final Integer id;
    private final String name;

    BookingState(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    @JsonCreator
    public static BookingState forValues(@JsonProperty("id") Integer id) {
        for (BookingState state : BookingState.values()) {
            if (state.id.equals(id)) {
                return state;
            }
        }
        return null;
    }

    @JsonCreator
    public static BookingState forValues(@JsonProperty("name") String name) {
        for (BookingState state : BookingState.values()) {
            if (state.name.equals(name)) {
                return state;
            }
        }
        return null;
    }

    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
