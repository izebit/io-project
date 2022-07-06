package ru.izebit.project.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.izebit.project.dto.TalkDto;
import ru.izebit.project.models.Talk;
import ru.izebit.project.repositories.TalkRepository;

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

    public Mono<Boolean> remove(final String id) {
        return talkRepository
                .existsById(id)
                .flatMap(result -> {
                    if (result)
                        return talkRepository
                                .deleteById(id)
                                .thenReturn(true);
                    else
                        return Mono.just(false);

                });
    }

    public Mono<String> create(final TalkDto talk) {
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


    public Mono<TalkDto> update(final TalkDto updatedTalk, final String id) {
        return talkRepository
                .findById(id)
                .map(entity -> {
                    if (StringUtils.hasText(updatedTalk.getAuthor()))
                        entity.setAuthor(updatedTalk.getAuthor());
                    if (StringUtils.hasText(updatedTalk.getTitle()))
                        entity.setTitle(updatedTalk.getTitle());
                    if (StringUtils.hasText(updatedTalk.getLink()))
                        entity.setLink(updatedTalk.getLink());
                    if (Objects.nonNull(updatedTalk.getDate()))
                        entity.setDate(updatedTalk.getDate());
                    if (Objects.nonNull(updatedTalk.getViews()))
                        entity.setViews(updatedTalk.getViews());
                    if (Objects.nonNull(updatedTalk.getLikes()))
                        entity.setLikes(updatedTalk.getLikes());
                    return entity;
                })
                .flatMap(talkRepository::save)
                .map(TalkService::map);

    }


    public Flux<TalkDto> search(final Map<String, String> searchParams) {
        if (searchParams.isEmpty())
            return talkRepository
                    .findAll()
                    .map(TalkService::map);

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
                .findAll(Example.of(probe, ExampleMatcher
                        .matchingAll()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                        .withIgnoreCase()))
                .map(TalkService::map);
    }

    private static TalkDto map(Talk talk) {
        return TalkDto.builder()
                .author(talk.getAuthor())
                .title(talk.getTitle())
                .date(talk.getDate())
                .link(talk.getLink())
                .views(talk.getViews())
                .likes(talk.getLikes())
                .build();
    }
}
