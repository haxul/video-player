package com.player.entities;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Data
@Table(name = "files")
public class FileEntity {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "files_id_seq"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private Long id;

    @Column(name = "path", nullable = false, unique = true, length = 150)
    private String fsPath;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "dir", length = 100)
    private String dir;

    @Column(name = "extension", nullable = false, length = 10)
    private String extension;
}
