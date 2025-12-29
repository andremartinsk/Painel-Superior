
package com.vale.vantage.service;

import com.vale.vantage.api.AlarmClient;
import com.vale.vantage.model.AlarmResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AlarmService {
    private final AlarmClient alarmClient;

    public AlarmService(AlarmClient alarmClient) {
        this.alarmClient = alarmClient;
    }

    /**
     * Busca de alarmes sem parâmetros de sort, evitando HTTP 422 ("Invalid value for param [sort]").
     * Se quiser filtrar apenas alertas, chame com search=null e use type="alert" no AlarmClient (parâmetro opcional).
     */
    public Mono<AlarmResponse> fetch(int page, int pageSize) {
        return alarmClient.listAlarms(
                page,
                pageSize,
                null,    // sort (removido)
                null,    // sortDirection (removido)
                null,    // search
                "0"      // parentData: "0" ou "1"
        );
    }
}
