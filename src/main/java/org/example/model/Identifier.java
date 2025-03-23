package org.example.model;

import org.example.enums.DataType;

/**
 * Represents a generic identifier that can be either a numeric ID or a string. Implementing classes should define how
 * they store and retrieve these identifiers.
 */
public interface Identifier {

    /**
     * Returns the data type of the identifier (either STRING or LONG).
     *
     * @return the identifier's data type.
     */
    DataType getIdentifierDataType();

    /**
     * Returns the numeric identifier if applicable.
     *
     * @return the numeric identifier, or null if the identifier is not a number.
     */
    Long getNumericIdentifier();

    /**
     * Returns the string identifier if applicable.
     *
     * @return the string identifier, or null if the identifier is not a string.
     */
    String getStringIdentifier();

}
