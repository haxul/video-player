package com.player.services;

import com.player.dto.FileDto;
import com.player.entities.FileEntity;
import com.player.exceptions.UnsupportedFileException;
import com.player.repos.FileRepo;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.util.Objects;

@Service
@Slf4j
public class FileService {

    @Autowired
    private FileRepo fileRepo;

    @Value("${files-path}")
    private String FILES_PATH;

    @SneakyThrows
    public FileDto save(MultipartFile inputFile, String dir) {
        // TODO check name of input file and dir
        /*
          define filename and extension and set codedName
         */

        FileDto fileDto = splitFileByDot(Objects.requireNonNull(inputFile.getOriginalFilename()));
        String relativePath = dir == null ? "/" + inputFile.getOriginalFilename() : "/" + dir + "/" + inputFile.getOriginalFilename();
        fileDto.setPath(relativePath);
        fileDto.setDir(dir);

        /*
           define place for file
         */

        String path = FILES_PATH + File.separator + (dir == null ? inputFile.getOriginalFilename() : dir + File.separator + inputFile.getOriginalFilename());

         /*
          check if file already exists
         */
        FileEntity existedFile = fileRepo.findByFsPath(path);
        if (existedFile != null) throw new FileAlreadyExistsException(fileDto.getPath());

        /*
         try to store file on disc. If it was failed throw UnsupportedFileException
         */
        if (!fileRepo.existsFileDir(dir)) {
            File newDirectory = new File(FILES_PATH + "/" + dir);
            boolean done = newDirectory.mkdirs();
            if (!done) throw new FileSystemException("cannot create dir " + dir);
        }
        File file = new File(path);
        tryStoreOnDisk(inputFile, file);

        /*
         store file in db
         */

        FileEntity fileEntity = new FileEntity();
        fileEntity.setExtension(fileDto.getExtension());
        fileEntity.setFsPath(path);
        fileEntity.setDir(dir);
        fileEntity.setName(fileDto.getName());

        FileEntity stored = fileRepo.save(fileEntity);

        /*
         add required fields for dto
         */

        fileDto.setId(stored.getId());
        log.info("File " + fileDto.getName() + "." + fileDto.getExtension() + " is saved");
        return fileDto;
    }

    @SneakyThrows
    private FileDto splitFileByDot(String filename) {
        String[] splitted = filename.split("\\.");
        if (splitted.length != 2) throw new UnsupportedFileException("extension is not found");
        var file = new FileDto();
        file.setName(splitted[0]);
        file.setExtension(splitted[1]);
        return file;
    }


    @SneakyThrows
    private void tryStoreOnDisk(MultipartFile file, File disk) {
        try {
            file.transferTo(disk);
        } catch (IOException e) {
            throw new UnsupportedFileException(e.getMessage());
        }
    }
}
