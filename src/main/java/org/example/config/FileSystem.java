package org.example.config;

import org.example.error.FileSystemException;
import org.example.model.FileDetails;
import org.example.model.Identifier;

import java.io.IOException;
import java.util.List;

public interface FileSystem {

    long addFile(String fileName, byte[] fileAsBytes) throws FileSystemException;

    byte[] getFile(Identifier identifier) throws FileSystemException;

    FileDetails viewFile(Identifier identifier) throws FileSystemException;

    List<FileDetails> listFiles(String query);

    void override(Identifier identifier, byte[] overrideData) throws FileSystemException;

    void update(Identifier identifier, String updateData) throws FileSystemException;

    void delete(Identifier identifier) throws FileSystemException;

    void closeFileSystem() throws IOException;
}
