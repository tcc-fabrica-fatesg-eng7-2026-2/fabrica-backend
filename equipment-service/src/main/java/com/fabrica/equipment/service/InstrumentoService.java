package com.fabrica.equipment.service;

import com.fabrica.equipment.domain.Instrumento;
import com.fabrica.equipment.repository.InstrumentoRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InstrumentoService {

    private final InstrumentoRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private static final String TOPIC = "instrument-created-topic";

    public InstrumentoService(InstrumentoRepository repository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Instrumento createInstrumento(Instrumento instrumento) {
        Instrumento saved = repository.save(instrumento);

        // Se o instrumento estiver configurado para agendamento automático, publica evento no Kafka
        if (saved.isAgendamentoAutomatico()) {
            Map<String, Object> eventPayload = new HashMap<>();
            eventPayload.put("idInstrumento", saved.getIdInstrumento().toString());
            eventPayload.put("tag", saved.getTag());
            eventPayload.put("idFilial", saved.getIdFilial());
            eventPayload.put("rangeInicial", saved.getRangeInicial());
            eventPayload.put("rangeFinal", saved.getRangeFinal());
            eventPayload.put("unidadeMedida", saved.getUnidadeMedida());

            kafkaTemplate.send(TOPIC, saved.getIdInstrumento().toString(), eventPayload);
        }

        return saved;
    }
}
