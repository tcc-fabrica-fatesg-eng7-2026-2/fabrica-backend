package com.fabrica.equipment.controller;

import com.fabrica.equipment.domain.Instrumento;
import com.fabrica.equipment.service.InstrumentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/instrumentos")
public class InstrumentoController {

    private final InstrumentoService service;

    public InstrumentoController(InstrumentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Instrumento> create(@RequestBody Instrumento instrumento) {
        Instrumento created = service.createInstrumento(instrumento);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
