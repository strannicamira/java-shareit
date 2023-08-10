package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

@UtilityClass
public class Constants {
    public static final String TIME_PATTERN = "YYYY-MM-DDTHH:mm:ss";
    public static final Sort SORT_BY_ID_ASC = Sort.by(Sort.Direction.ASC, "id");
    public static final Sort SORT_BY_ID_DESC = Sort.by(Sort.Direction.DESC, "id");//from new to old,newer first? for ex. 6, 5, 4
    public static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    public static final Sort SORT_BY_REQUEST_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");


}