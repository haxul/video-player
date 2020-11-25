package com.player.controllers;

import com.player.dto.FileDto;
import com.player.exceptions.FileOperationException;
import com.player.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping
    public FileDto save(@RequestParam("file") MultipartFile inputFile, @RequestParam(name = "dir", required = false) String dir)  {
        return fileService.save(inputFile, dir);
    }

    @DeleteMapping
    public ResponseEntity<String> removeAll() {
        fileService.removeAll();
        final String body = "{\"status\": \"Success\"}";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
