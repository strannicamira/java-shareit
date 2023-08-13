package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonCreator;

//@ToString
//@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BookingStatus {
    WAITING(0, "WAITING"),
    APPROVED(1, "APPROVED"),
    REJECTED(2, "REJECTED"),
    CANCELED(3, "CANCELED");

    private final Integer id;
    private final String name;

    BookingStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonCreator
    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    @Override
    @JsonCreator
    public String toString() {
        return getName();
    }
}
