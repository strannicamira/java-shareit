package ru.practicum.shareit.exception;

public class DuplicateEmailFoundException extends RuntimeException {
    public DuplicateEmailFoundException(String s) {
        super(s);
    }
}