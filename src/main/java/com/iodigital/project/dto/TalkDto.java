package com.iodigital.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.NonNull;

import java.time.LocalDate;

/**
 * @author <a href="mailto:izebit@gmail.com">Artem Konovalov</a> <br/>
 * Date: 05.07.2022
 */
@Data
@Builder
public class TalkDto {
    private String title;
    private String author;
    private String link;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Builder.Default
    private LocalDate date = LocalDate.now();
    @Builder.Default
    private Long views = 0L;
    @Builder.Default
    private Long likes = 0L;
}
