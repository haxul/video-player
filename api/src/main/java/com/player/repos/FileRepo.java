package com.player.repos;

import com.player.entities.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepo extends JpaRepository<FileEntity, Long> {
    FileEntity findByFsPath(String path);

    @Query("select case when count(f)> 0 then true else false end" +
            " from FileEntity f where f.dir =:dir")
    boolean existsFileDir(@Param("dir") String dir);
}
