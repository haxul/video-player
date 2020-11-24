package com.player.controllers;

import com.player.dto.FileDto;
import com.player.exceptions.UnsupportedFileException;
import com.player.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}
