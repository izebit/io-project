package ru.izebit.project.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.NonNull;

import java.time.LocalDate;

/**
 * @author <a href="mailto:izebit@gmail.com">Artem Konovalov</a> <br/>
 * Date: 05.07.2022
 */
@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@Document(collection = "talks")
public class Talk {
    @Id
    private String id;
    @Version
    private Long version;
    @Field("title")
    @NonNull
    private String title;
    @Field("author")
    @NonNull
    private String author;
    @Field("date")
    @NonNull
    private LocalDate date;
    @Field("views")
    private Long views;
    @Field("likes")
    private Long likes;
    @Field("link")
    @NonNull
    private String link;
}
