package com.iodigital.project.services;

import com.iodigital.project.dto.TalkDto;
import com.iodigital.project.models.Talk;
import com.iodigital.project.repositories.TalkRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:izebit@gmail.com">Artem Konovalov</a> <br/>
 * Date: 05.07.2022
 */
@Service
@AllArgsConstructor
public class TalkService {
    private final TalkRepository talkRepository;

    public Mono<Boolean> remove(final long id) {
        return talkRepository
                .existsById(id)
                .flatMap(result -> {
                    if (result)
                        return talkRepository
                                .deleteById(id)
                                .map(e -> true);
                    else
                        return Mono.just(false);

                });
    }

    public Mono<Long> create(final TalkDto talk) {
        return Mono.just(
                        Talk.builder()
                                .version(0L)
                                .author(talk.getAuthor())
                                .title(talk.getTitle())
                                .date(talk.getDate())
                                .link(talk.getLink())
                                .likes(talk.getLikes())
                                .views(talk.getViews())
                                .build())
                .flatMap(talkRepository::insert)
                .map(Talk::getId);
    }


    public Mono<Boolean> update(final TalkDto talk, final long id) {
        return talkRepository
                .findById(id)
                .map(entity -> {
                    if (StringUtils.hasText(talk.getAuthor()))
                        entity.setAuthor(entity.getAuthor());
                    if (StringUtils.hasText(talk.getTitle()))
                        entity.setTitle(talk.getTitle());
                    if (StringUtils.hasText(talk.getLink()))
                        entity.setLink(entity.getLink());
                    if (Objects.nonNull(talk.getDate()))
                        entity.setDate(talk.getDate());
                    if (Objects.nonNull(talk.getViews()))
                        entity.setViews(talk.getViews());
                    if (Objects.nonNull(talk.getLikes()))
                        entity.setLikes(talk.getLikes());
                    return entity;
                })
                .flatMap(talkRepository::save)
                .map(e -> true);

    }


    public Flux<TalkDto> search(final Map<String, String> searchParams) {
        var probe = new Talk();
        searchParams.forEach((key, value) -> {
                    switch (StringUtils.uncapitalize(key)) {
                        case "author" -> probe.setAuthor(value);
                        case "title" -> probe.setTitle(value);
                        case "views" -> probe.setViews(Long.parseLong(value));
                        case "likes" -> probe.setLikes(Long.parseLong(value));
                    }
                });

        return talkRepository
                .findAll(Example.of(probe))
                .map(e -> TalkDto.builder()
                        .author(e.getAuthor())
                        .title(e.getTitle())
                        .date(e.getDate())
                        .link(e.getLink())
                        .views(e.getViews())
                        .likes(e.getLikes())
                        .build());
    }
}
