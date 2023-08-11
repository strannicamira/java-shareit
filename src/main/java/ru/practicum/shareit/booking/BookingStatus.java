package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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

//    @JsonCreator
    public static BookingStatus forValues(@JsonProperty("id") Integer id) {
        for (BookingStatus status : BookingStatus.values()) {
            if (status.id.equals(id)) {
                return status;
            }
        }
        return null;
    }

//    @JsonCreator//(mode = JsonCreator.Mode.DEFAULT)
    public static BookingStatus forValues(@JsonProperty("name") String name) {
        for (BookingStatus status : BookingStatus.values()) {
            if (status.name.equals(name)) {
                return status;
            }
        }
        return null;
    }

    @Override
    @JsonCreator
    public String toString() {
        return getName();
    }
}
