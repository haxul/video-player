package com.player.services;

import com.player.dto.FileDto;
import com.player.entities.FileEntity;
import com.player.exceptions.FileOperationException;
import com.player.exceptions.UnsupportedFileException;
import com.player.repos.FileRepo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class FileService {

    @Autowired
    private FileRepo fileRepo;


    private final String FILES_PATH;

    public FileService(@Value("${files-path}") String files_path) {
        FILES_PATH = files_path;
    }

    @SneakyThrows
    public FileDto save(MultipartFile inputFile, String dir) {
        /*
          define filename and extension and set codedName
         */

        FileDto fileDto = buildFileDto(Objects.requireNonNull(inputFile.getOriginalFilename()));
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
    public List<String> checkFileNaming(LinkedList<String> fileNamePaths) {
        if (fileNamePaths.isEmpty()) return Collections.emptyList();
        var head = fileNamePaths.getFirst();
        if (!head.matches("^[a-zA-Z0-9]{1,40}$")) return List.of(head);
        fileNamePaths.removeFirst();
        return checkFileNaming(fileNamePaths);
    }

    @SneakyThrows
    private FileDto buildFileDto(String filename) {
        var splitted = new LinkedList<>(Arrays.asList((filename.split("\\."))));
        if (splitted.size() != 2) throw new UnsupportedFileException("extension is not found");
        List<String> wrongFileNames = checkFileNaming(new LinkedList<>(splitted));
        if (!wrongFileNames.isEmpty())
            throw new UnsupportedFileException("incorrect naming in file: " + wrongFileNames.get(0));
        var file = new FileDto();
        file.setName(splitted.getFirst());
        file.setExtension(splitted.getLast());
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

    @SneakyThrows
    public void removeAll()  {
        CompletableFuture<Boolean> cleanDirectoryFuture = cleanDirectory(new File(FILES_PATH));
        fileRepo.deleteAll();
        Boolean isRemoved = cleanDirectoryFuture.get();
        if (!isRemoved) throw new FileOperationException();
        log.info("all files are removed");
    }

    private CompletableFuture<Boolean> cleanDirectory(File dir) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FileUtils.cleanDirectory(dir);
                return true;
            } catch (IOException e) {
                log.error("Cannot remove files: " + e);
                return false;
            }
        });
    }
}
