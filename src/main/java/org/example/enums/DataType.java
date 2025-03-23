package org.example.enums;

public enum DataType {

    STRING,
    INTEGER,
    DOUBLE,
    LONG,
    CHARACTER,
    SHORT,
    FLOAT;

    public boolean isNumeric() {
        return this == INTEGER || this == DOUBLE || this == LONG || this == SHORT || this == FLOAT;
    }

    public boolean isText() {
        return this == STRING || this == CHARACTER;
    }
}
