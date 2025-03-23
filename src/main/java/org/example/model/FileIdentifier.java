package org.example.model;

import org.example.enums.DataType;

/**
 * Represents a unique identifier for a file, which can be either a numeric ID or a file name. This class supports two
 * types of identifiers:
 *
 *     1. A numeric identifier (Long) representing the file ID.
 *     2. A string identifier representing the file name.
 *
 * The type of identifier is tracked using the DataTypes enum. This class follows the Builder pattern to enforce
 * controlled object creation.
 */
public class FileIdentifier implements Identifier {

    private String name;
    private Long id;
    private DataType dataTypes;

    private FileIdentifier() {}

    /**
     * Returns the data type of the identifier (either STRING or LONG).
     *
     * @return the identifier's data type.
     */
    @Override
    public DataType getIdentifierDataType() {
        return this.dataTypes;
    }

    /**
     * Returns the numeric identifier of the file.
     *
     * @return the file ID if available, otherwise null.
     */
    @Override
    public Long getNumericIdentifier() {
        return this.id;
    }

    /**
     * Returns the string identifier of the file.
     *
     * @return the file name if available, otherwise null.
     */
    @Override
    public String getStringIdentifier() {
        return this.name;
    }

    /**
     * Creates a new builder instance for constructing FileIdentifier objects.
     *
     * @return a new Builder instance.
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Builder class for constructing instances of FileIdentifier. This ensures that an identifier is set before the
     * object is created.
     */
    public static class Builder {

        private final FileIdentifier identifier = new FileIdentifier();

        private Builder() {}

        /**
         * Sets a numeric file identifier.
         *
         * @param id the file ID.
         * @return the builder instance.
         */
        public Builder setFileId(Long id) {
            identifier.id = id;
            identifier.dataTypes = DataType.LONG;
            return this;
        }

        /**
         * Sets a string file identifier.
         *
         * @param name the file name.
         * @return the builder instance.
         */
        public Builder setFileName(String name) {
            identifier.name = name;
            identifier.dataTypes = DataType.STRING;
            return this;
        }

        /**
         * Builds and returns a {@link FileIdentifier} instance.
         *
         * @return the constructed {@link FileIdentifier}.
         */
        public FileIdentifier build() {
            return identifier;
        }
    }
}
