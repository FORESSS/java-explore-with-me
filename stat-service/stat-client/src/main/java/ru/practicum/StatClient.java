package ru.practicum;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.utils.DateTimeException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ru.practicum.utils.Constants.FORMATTER;

@Component
@Slf4j
public class StatClient {

    private final RestClient restClient;

    public StatClient(@Value("${stat-server.url}") String serverUrl) {
        this.restClient = RestClient.create(serverUrl);
        log.info("Server stat run URL: {}", serverUrl);
    }

    @SneakyThrows
    public void saveHit(String app, HttpServletRequest request) {
        log.info("Saving hit for {}", app);
        EndpointHitDto endpointHitDto = toDto(app, request);
        ResponseEntity<Void> response = restClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHitDto)
                .retrieve()
                .toBodilessEntity();
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Posted hit with code {}", response.getStatusCode());
        } else {
            log.error("Posted hit with error code {}", response.getStatusCode());
        }
        Thread.sleep(500);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                       List<String> uris, boolean unique) {
        log.info("Getting stats for {}", uris);
        if (start == null || end == null) {
            throw new DateTimeException("88888888888888888888");
        }
        try {
            return restClient.get()
                    .uri(uriBuilder ->
                            uriBuilder.path("/stats")
                                    .queryParam("start", start.format(FORMATTER))
                                    .queryParam("end", end.format(FORMATTER))
                                    .queryParam("uris", uris)
                                    .queryParam("unique", unique)
                                    .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            (request, response) ->
                                    log.error("Getting stats for {} with error code {}", uris, response.getStatusCode()))
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            log.error("Getting stats for {} failed", uris, e);
            return Collections.emptyList();
        }
    }

    private EndpointHitDto toDto(String app, HttpServletRequest request) {
        return EndpointHitDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
    }
}