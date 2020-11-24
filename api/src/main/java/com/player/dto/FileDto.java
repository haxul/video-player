package com.player.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {

    private Long id;
    private String path;
    private String extension;
    private String name;
    private String dir;
}
