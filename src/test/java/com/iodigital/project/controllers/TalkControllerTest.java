package com.iodigital.project.controllers;

import com.iodigital.project.ApplicationLauncher;
import com.iodigital.project.dto.TalkDto;
import com.iodigital.project.repositories.TalkRepository;
import com.iodigital.project.services.TalkService;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author <a href="mailto:izebit@gmail.com">Artem Konovalov</a> <br/>
 * Date: 06.07.2022
 */
@ExtendWith({SpringExtension.class})
@SpringBootTest(
        properties = "spring.main.web-application-type=reactive",
        classes = {TalkControllerTest.TestConfiguration.class, ApplicationLauncher.class})
@ActiveProfiles("test")
public class TalkControllerTest {
    @Autowired
    private WebTestClient client;
    @Autowired
    private TalkRepository repository;
    @Autowired
    private TalkService service;


    @ParameterizedTest
    @MethodSource("getTalk")
    void createTalkTest(TalkDto expectedTalk) {
        var id = client.post()
                .uri("/api/v1/talks")
                .bodyValue(expectedTalk)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().returnResult().getResponseBody();

        var actualTalk = repository.findById(new String(id)).block();
        assertThat(actualTalk, is(notNullValue()));
        assertThat(actualTalk.getAuthor(), is(expectedTalk.getAuthor()));
        assertThat(actualTalk.getTitle(), is(expectedTalk.getTitle()));
        assertThat(actualTalk.getLink(), is(expectedTalk.getLink()));
        assertThat(actualTalk.getLikes(), is(expectedTalk.getLikes()));
        assertThat(actualTalk.getViews(), is(expectedTalk.getViews()));
        assertThat(actualTalk.getDate(), is(expectedTalk.getDate()));
    }

    @ParameterizedTest
    @MethodSource("getTalk")
    void updateTest_when_talk_exists(TalkDto expectedTalk) {
        var id = service.create(expectedTalk).block();

        var updatedTalk = expectedTalk
                .withAuthor("Brad Pitt")
                .withViews(10000L);

        client.put()
                .uri("/api/v1/talks/" + id)
                .bodyValue(updatedTalk)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.author").isEqualTo(updatedTalk.getAuthor())
                .jsonPath("$.title").isEqualTo(updatedTalk.getTitle())
                .jsonPath("$.link").isEqualTo(updatedTalk.getLink())
                .jsonPath("$.views").isEqualTo(updatedTalk.getViews())
                .jsonPath("$.likes").isEqualTo(updatedTalk.getLikes())
                .jsonPath("$.date").isEqualTo(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(updatedTalk.getDate()));
    }

    @ParameterizedTest
    @MethodSource("getTalk")
    void updateTest_when_talk_does_not_exist(TalkDto expectedTalk) {
        client.put()
                .uri("/api/v1/talks/" + UUID.randomUUID())
                .bodyValue(expectedTalk)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @ParameterizedTest
    @MethodSource("getTalks")
    void searchTest_all_match(List<TalkDto> talks) {
        talks.forEach(talk -> service.create(talk).block());

        client.get()
                .uri("/api/v1/talks")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TalkDto.class)
                .hasSize(4)
                .contains(talks.toArray(new TalkDto[0]));
    }

    @ParameterizedTest
    @MethodSource("getTalks")
    void searchTest_none_match(List<TalkDto> talks) {
        talks.forEach(talk -> service.create(talk).block());

        client.get()
                .uri("/api/v1/talks?views=101001")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TalkDto.class)
                .hasSize(0);
    }

    @ParameterizedTest
    @MethodSource("getTalks")
    void searchTest_several_fields_match(List<TalkDto> talks) {
        talks.forEach(talk -> service.create(talk).block());

        var expectedTalks = talks.stream()
                .filter(e -> e.getTitle().toLowerCase().contains("rosa"))
                .filter(e -> e.getAuthor().toLowerCase().contains("pitt"))
                .toArray(TalkDto[]::new);

        client.get()
                .uri("/api/v1/talks?title=Rosa&author=Pitt")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TalkDto.class)
                .hasSize(1)
                .contains(expectedTalks);
    }

    @ParameterizedTest
    @MethodSource("getTalks")
    void searchTest_title_match(List<TalkDto> talks) {
        talks.forEach(talk -> service.create(talk).block());

        var expectedTalks = talks.stream()
                .filter(e -> e.getTitle().toLowerCase().contains("rosa"))
                .toArray(TalkDto[]::new);

        client.get()
                .uri("/api/v1/talks?title=Rosa")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TalkDto.class)
                .hasSize(3)
                .contains(expectedTalks);
    }

    @ParameterizedTest
    @MethodSource("getTalks")
    void searchTest_author_match(List<TalkDto> talks) {
        talks.forEach(talk -> service.create(talk).block());

        var expectedTalks = talks.stream()
                .filter(e -> e.getAuthor().toLowerCase().contains("pitt"))
                .toArray(TalkDto[]::new);

        client.get()
                .uri("/api/v1/talks?author=pitt")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TalkDto.class)
                .hasSize(1)
                .contains(expectedTalks);
    }

    @ParameterizedTest
    @MethodSource("getTalks")
    void searchTest_views_match(List<TalkDto> talks) {
        talks.forEach(talk -> service.create(talk).block());

        var expectedTalks = talks.stream()
                .filter(e -> e.getViews() == 100L)
                .toArray(TalkDto[]::new);

        client.get()
                .uri("/api/v1/talks?views=100")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TalkDto.class)
                .hasSize(3)
                .contains(expectedTalks);
    }

    @ParameterizedTest
    @MethodSource("getTalks")
    void searchTest_likes_match(List<TalkDto> talks) {
        talks.forEach(talk -> service.create(talk).block());

        var expectedTalks = talks.stream()
                .filter(e -> e.getLikes() == 42L)
                .toArray(TalkDto[]::new);

        client.get()
                .uri("/api/v1/talks?likes=42")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TalkDto.class)
                .hasSize(4)
                .contains(expectedTalks);
    }


    @ParameterizedTest
    @MethodSource("getTalk")
    void removeTest_when_talk_exists(TalkDto expectedTalk) {
        var id = service.create(expectedTalk).block();


        client.delete()
                .uri("/api/v1/talks/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        assertThat(repository.existsById(id).block(), is(Boolean.FALSE));
    }


    @Test
    void removeTest_when_talk_does_not_exist() {
        client.delete()
                .uri("/api/v1/talks/" + UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }


    private static Stream<Arguments> getTalk() {
        return Stream.of(Arguments.of(getDto()));
    }

    private static Stream<Arguments> getTalks() {
        return Stream.of(
                Arguments.of(
                        Arrays.asList(
                                getDto(),
                                getDto().withAuthor("Brad Pitt"),
                                getDto().withViews(101L),
                                getDto().withTitle("Usando la fotografía para celebrar los diferentes tonos y colores de la humanidad")
                        )
                ));
    }

    private static TalkDto getDto() {
        return TalkDto.builder()
                .author("David Ikard")
                .views(100L)
                .likes(42L)
                .date(LocalDate.now())
                .title("The real story of Rosa Parks — and why we need to confront myths about Black history")
                .link("https://ted.com/talks/david_ikard_the_real_story_of_rosa_parks_and_why_we_need_to_confront_myths_about_black_history")
                .build();
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll().block();
    }

    @Configuration
    public static class TestConfiguration {

        @Bean(destroyMethod = "shutdown")
        public MongoServer mongoServer(@Value("${spring.data.mongodb.host}") String host,
                                       @Value("${spring.data.mongodb.port}") int port) {
            MongoServer mongoServer = new MongoServer(new MemoryBackend());
            mongoServer.bind(host, port);
            return mongoServer;
        }

        @Bean
        public WebTestClient webTestClient(TalkController controller) {
            return WebTestClient.bindToController(controller).build();
        }
    }
}