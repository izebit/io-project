package ru.izebit.project.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.izebit.project.dto.TalkDto;
import ru.izebit.project.services.TalkService;

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
    @ApiOperation(value = "create a new talk", response = String.class,
            notes = "takes a new talk and return a created one's ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 500, message = "internal error")
    })
    public Mono<ResponseEntity<String>> create(@RequestBody TalkDto talk) {
        return talkService
                .create(talk)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "update a talk", notes = "returns updated one")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 404, message = "entity has not been found"),
            @ApiResponse(code = 500, message = "internal error")
    })
    public Mono<ResponseEntity<TalkDto>> update(@RequestBody TalkDto talk, @PathVariable String id) {
        return talkService.update(talk, id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping
    @ApiOperation(value = "search talks with a particular fields",
            notes = "returns list of talks. fields can be 'author', 'title', 'likes', 'views'")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 500, message = "internal error")
    })
    public Flux<TalkDto> search(@RequestParam Map<String, String> searchParams) {
        return talkService.search(searchParams);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "remove a talk with id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 404, message = "entity has not been found"),
            @ApiResponse(code = 500, message = "internal error")
    })
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
