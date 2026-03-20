package org.lapaloma.mapamundi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lapaloma.mapamundi.dao.IContinenteDAO;
import org.lapaloma.mapamundi.excepcion.ContinenteNoEncontradoException;
import org.lapaloma.mapamundi.vo.Continente;

class ContinenteServiceTest {

    private ContinenteService continenteService;
    private FakeContinenteDAO fakeDAO;

    @BeforeEach
    void setUp() {
        fakeDAO = new FakeContinenteDAO();
        continenteService = new ContinenteService(fakeDAO);
    }

    // =========================
    // obtenerContinentePorClave
    // =========================

    @Test
    void obtenerContinentePorClave_cuandoCodigoEsNull_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            continenteService.obtenerContinentePorClave(null);
        });
    }

    @Test
    void obtenerContinentePorClave_cuandoCodigoEstaVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            continenteService.obtenerContinentePorClave("");
        });
    }

    @Test
    void obtenerContinentePorClave_cuandoNoExiste_lanzaExcepcion() {
        assertThrows(ContinenteNoEncontradoException.class, () -> {
            continenteService.obtenerContinentePorClave("XX");
        });
    }

    @Test
    void obtenerContinentePorClave_cuandoExiste_retornaContinente() {
        fakeDAO.crearContinente(new Continente("EU", "Europa"));

        Continente resultado = continenteService.obtenerContinentePorClave("EU");

        assertNotNull(resultado);
        assertEquals("Europa", resultado.getNombre());
    }

    // =========================
    // obtenerListaContinentes
    // =========================

    @Test
    void obtenerListaContinentes_cuandoListaEstaVacia_lanzaExcepcion() {
        assertThrows(RuntimeException.class, () -> {
            continenteService.obtenerListaContinentes();
        });
    }

    @Test
    void obtenerListaContinentes_cuandoHayDatos_retornaLista() {
        fakeDAO.crearContinente(new Continente("EU", "Europa"));

        List<Continente> resultado = continenteService.obtenerListaContinentes();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    // =========================
    // obtenerContinentePorNombre
    // =========================

    @Test
    void obtenerContinentePorNombre_cuandoNombreEsNull_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            continenteService.obtenerContinentePorNombre(null);
        });
    }

    @Test
    void obtenerContinentePorNombre_cuandoNombreEstaVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            continenteService.obtenerContinentePorNombre("");
        });
    }

    @Test
    void obtenerContinentePorNombre_cuandoNoExiste_lanzaExcepcion() {
        assertThrows(ContinenteNoEncontradoException.class, () -> {
            continenteService.obtenerContinentePorNombre("Inexistente");
        });
    }

    @Test
    void obtenerContinentePorNombre_cuandoExiste_retornaLista() {
        fakeDAO.crearContinente(new Continente("EU", "Europa"));

        List<Continente> resultado = continenteService.obtenerContinentePorNombre("Europa");

        assertEquals(1, resultado.size());
    }

    // =========================
    // Fake DAO. Se crea el DAO dentro del test para no depender de la conexión a la base de datos, de si hay red, de si accede a un fichero...
    // En caso de usar el DOA real (ContinenteDaoJDBC) estaríamos hablando de prubeas de integración.
    // =========================

    static class FakeContinenteDAO implements IContinenteDAO {

        private List<Continente> data = new ArrayList<>();

        @Override
        public Continente obtenerContinentePorClave(String codigo) {
            return data.stream()
                    .filter(c -> c.getCodigo().equals(codigo))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<Continente> obtenerListaContinentes() {
            return new ArrayList<>(data);
        }

        @Override
        public List<Continente> obtenerContinentePorNombre(String nombre) {
            List<Continente> resultado = new ArrayList<>();

            for (Continente c : data) {
                if (c.getNombre().equals(nombre)) {
                    resultado.add(c);
                }
            }
            return resultado;
        }

		@Override
		public void actualizarContinente(Continente continente) {
		}

		@Override
		public void crearContinente(Continente continente) {
            data.add(continente);
		}

		@Override
		public void borrarContinente(Continente continente) {
			
		}
    }
}