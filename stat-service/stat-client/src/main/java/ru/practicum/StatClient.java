package ru.practicum;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.util.Validator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ru.practicum.util.Constants.FORMATTER;

@Component
@Slf4j
public class StatClient {
    private final RestClient restClient;
    @Autowired
    private final Validator validator;

    public StatClient(@Value("${stat-server.url}") String serverUrl, Validator validator) {
        this.restClient = RestClient.create(serverUrl);
        this.validator = validator;
        log.info("Server stat run URL: {}", serverUrl);
    }

    public void saveHit(String app, HttpServletRequest request) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        ResponseEntity<Void> response = restClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(endpointHitDto)
                .retrieve()
                .toBodilessEntity();

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Сохранение информации о запросе");
        } else {
            log.error("Ошибка при сохранении информации, код ошибки: {}", response.getStatusCode());
        }
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                       List<String> uris, boolean unique) {
        log.info("Getting stats for {}", uris);
        validator.checkDateTime(start, end);
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
}