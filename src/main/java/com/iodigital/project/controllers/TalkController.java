package com.iodigital.project.controllers;

import com.iodigital.project.dto.TalkDto;
import com.iodigital.project.services.TalkService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author <a href="mailto:izebit@gmail.com">Artem Konovalov</a> <br/>
 * Date: 05.07.2022
 */
@RestController
@RequestMapping("/api/v1/talks")
@AllArgsConstructor
public class TalkController {
    private final TalkService talkService;

    @PostMapping
    public Mono<ResponseEntity<String>> create(@RequestBody TalkDto talk) {
        return talkService
                .create(talk)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<TalkDto>> update(@RequestBody TalkDto talk, @PathVariable String id) {
        return talkService.update(talk, id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping
    public Flux<TalkDto> search(@RequestParam Map<String, String> searchParams) {
        return talkService.search(searchParams);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> remove(@PathVariable("id") String id) {
        return talkService.remove(id)
                .map(result -> {
                    if (result)
                        return ResponseEntity.ok().build();
                    else
                        return ResponseEntity.notFound().build();
                });
    }
}
