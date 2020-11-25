package com.player.exceptions;

import com.player.dto.ErrorResponse;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import java.nio.file.FileAlreadyExistsException;

@ControllerAdvice
public class ExceptionController  {

    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleException(SizeLimitExceededException e) {
        var response = new ErrorResponse();
        response.setMessage(e.getMessage());
        response.setCause(SizeLimitExceededException.class.toString());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(UnsupportedFileException.class)
    public ResponseEntity<ErrorResponse> handleException(UnsupportedFileException e) {
        var response = new ErrorResponse();
        response.setCause(UnsupportedFileException.class.toString());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(FileOperationException.class)
    public ResponseEntity<ErrorResponse> handleException(FileOperationException e) {
        var response = new ErrorResponse();
        response.setCause(FileOperationException.class.toString());
        response.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleMultipartException(MultipartException ex) {
        var response = new ErrorResponse();
        response.setCause(MultipartException.class.toString());
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }
    @ExceptionHandler(FileAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleException(FileAlreadyExistsException ex) {
        var response = new ErrorResponse();
        response.setCause(FileAlreadyExistsException.class.toString());
        response.setMessage("File with name like this exists");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
