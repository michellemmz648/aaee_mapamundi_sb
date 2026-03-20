/**
 * 
 */
package org.lapaloma.mapamundi.controller;

import java.util.List;

import org.lapaloma.mapamundi.service.ContinenteService;
import org.lapaloma.mapamundi.vo.Continente;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Isidoro Nevares Martín - Virgen de la Paloma Fecha creación: 13 mar 2026
 */
@RestController
@RequestMapping("/api/continentes")
public class ContinenteController {

    private final ContinenteService continenteService;

    // Spring inyecta automáticamente el service con su DAO
    public ContinenteController(ContinenteService continenteService) {
        this.continenteService = continenteService;
    }
    
    // GET /api/continentes - listar todos los continentes
    @GetMapping
    public List<Continente> getAll() {
        List<Continente> listaContinentes = continenteService.obtenerListaContinentes();
        return listaContinentes;
    }

    // GET /api/continentes/codigo/{codigo} - Obtener un Continente por su código
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Continente> getByCodigo(@PathVariable("codigo") String codigo) {
        Continente continente = continenteService.obtenerContinentePorClave(codigo);

        return continente != null ? ResponseEntity.ok(continente) : ResponseEntity.notFound().build();
    }

    // GET /api/continentes/nombre/{nombre} - Obtener un Continente por su nombre
    @GetMapping("/nombre/{nombre}")
    public List<Continente> getByNombre(@PathVariable("nombre") String nombre) {
        List<Continente> listaContinentes = continenteService.obtenerContinentePorNombre(nombre);

        return listaContinentes;
    }

}
