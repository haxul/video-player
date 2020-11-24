package com.player.exceptions;

public class UnsupportedFileException extends Exception {
    public UnsupportedFileException() {
        super("Problems with the file");
    }

    public UnsupportedFileException(String message) {
        super(message);
    }
}
