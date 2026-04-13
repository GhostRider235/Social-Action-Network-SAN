package com.proyect.Social_action_networkks.controllers;

import com.proyect.Social_action_networkks.modelo.Fundacion;
import com.proyect.Social_action_networkks.servicio.FundacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fundaciones")
public class FundacionRestController {

    @Autowired
    private FundacionService fundacionService;

    @GetMapping
    public List<Fundacion> obtenerTodas() {
        return fundacionService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fundacion> obtenerPorId(@PathVariable String id) {
        return fundacionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Fundacion guardar(@RequestBody Fundacion fundacion) {
        return fundacionService.guardar(fundacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fundacion> actualizar(@PathVariable String id, @RequestBody Fundacion fundacion) {
        return fundacionService.obtenerPorId(id)
                .map(f -> {
                    fundacion.setId(id);
                    return ResponseEntity.ok(fundacionService.guardar(fundacion));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        if (fundacionService.obtenerPorId(id).isPresent()) {
            fundacionService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
