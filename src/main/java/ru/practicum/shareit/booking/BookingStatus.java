package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum BookingStatus {
    WAITING(1,"Waiting"),
    APPROVED(2,"Approved"),
    REJECTED(3,"Rejected"),
    CANCELED(4,"Canceled");

    private final Integer id;
    private final String name;

    BookingStatus(Integer id, String name) {
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
    public static BookingStatus forValues(@JsonProperty("id") Integer id) {
        for (BookingStatus status : BookingStatus.values()) {
            if (status.id == id) {
                return status;
            }
        }
        return null;
    }
}
