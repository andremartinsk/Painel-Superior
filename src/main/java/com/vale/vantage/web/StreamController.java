
package com.vale.vantage.web;

import com.vale.vantage.auth.TokenService;
import com.vale.vantage.model.AlarmItem;
import com.vale.vantage.model.AlarmResponse;
import com.vale.vantage.service.AlarmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/stream")
public class StreamController {

    private static final Logger log = LoggerFactory.getLogger(StreamController.class);

    private final AlarmService alarmService;
    private final TokenService tokenService;

    public StreamController(AlarmService alarmService, TokenService tokenService) {
        this.alarmService = alarmService;
        this.tokenService = tokenService;
    }

    @GetMapping(value = "/alarms", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AlarmItem> alarms() {
        return Flux
                .interval(Duration.ofSeconds(10))
                .flatMap(tick -> {
                    // ✅ Se ainda não está autenticado, emite vazio (sem erro)
                    if (!tokenService.isAuthenticated()) {
                        return Mono.just(emptyResponse());
                    }
                    return alarmService.fetch(1, 100)
                            .timeout(Duration.ofSeconds(10))
                            .onErrorResume(e -> {
                                log.warn("Falha ao consultar Alarm API: {}", e.toString());
                                return Mono.just(emptyResponse());
                            });
                })
                .map(resp -> resp.getItems() == null ? Collections.<AlarmItem>emptyList() : resp.getItems())
                .flatMapIterable(list -> list)
                .doOnError(e -> log.error("Erro inesperado no stream de alarms", e))
                .retry(); // proteção adicional (deve quase nunca disparar)
    }

    private AlarmResponse emptyResponse() {
        AlarmResponse r = new AlarmResponse();
        r.setItems(List.of());
        r.setTotalItems(0);
        return r;
    }
}
