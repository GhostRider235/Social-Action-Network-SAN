package com.proyect.Social_action_networkks.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyect.Social_action_networkks.modelo.Fundacion;
import com.proyect.Social_action_networkks.servicio.FundacionService;

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
public ResponseEntity<Fundacion> obtenerPorId(@PathVariable("id") String id)  {
        return fundacionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Fundacion guardar(@RequestBody Fundacion fundacion) {
        return fundacionService.guardar(fundacion);
    }

    @PutMapping("/{id}")
public ResponseEntity<Fundacion> actualizar(
        @PathVariable("id") String id,
        @RequestBody Fundacion fundacion) {
        return fundacionService.obtenerPorId(id)
                .map(f -> {
                    fundacion.setId(id);
                    return ResponseEntity.ok(fundacionService.guardar(fundacion));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
public ResponseEntity<Void> eliminar(@PathVariable("id") String id) {
        if (fundacionService.obtenerPorId(id).isPresent()) {
            fundacionService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
