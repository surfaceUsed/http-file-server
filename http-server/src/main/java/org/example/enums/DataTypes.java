package org.example.enums;

public enum DataTypes {

    STRING,
    INTEGER,
    DOUBLE,
    LONG,
    BYTE,
    CHARACTER,
    SHORT,
    FLOAT,
    BOOLEAN,
    INVALID;

    public boolean isNumeric() {
        return this == INTEGER || this == DOUBLE || this == LONG || this == SHORT || this == FLOAT;
    }

    public boolean isText() {
        return this == STRING || this == CHARACTER;
    }
}
