package com.fabrica.equipment.service;

import com.fabrica.equipment.domain.Instrumento;
import com.fabrica.equipment.repository.InstrumentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class InstrumentoServiceKafkaTest {

    @Mock
    private InstrumentoRepository repository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private InstrumentoService service;

    @Test
    public void deveDispararKafkaSeAgendamentoAutomatico() {
        Instrumento inst = new Instrumento();
        inst.setIdInstrumento(UUID.randomUUID());
        inst.setAgendamentoAutomatico(true);

        when(repository.save(any())).thenReturn(inst);

        service.createInstrumento(inst);

        verify(kafkaTemplate).send(eq("instrument-created-topic"), eq(inst.getIdInstrumento().toString()), anyMap());
    }

    @Test
    public void naoDeveDispararKafkaSeManual() {
        Instrumento inst = new Instrumento();
        inst.setIdInstrumento(UUID.randomUUID());
        inst.setAgendamentoAutomatico(false); // Falso

        when(repository.save(any())).thenReturn(inst);

        service.createInstrumento(inst);

        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyMap());
    }
}
