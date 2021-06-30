package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ContextoProyecto;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ContextoProyectoRepositoryTest
 */
@DataJpaTest
public class ContextoProyectoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ContextoProyectoRepository repository;

  @Test
  public void findByProyectoId_ReturnsContextoProyecto() throws Exception {

    // given: 2 ContextoProyecto de los que 1 coincide con el idProyecto buscado
    Proyecto proyecto1 = entityManager.persistAndFlush(generarMockProyecto(1L));
    ContextoProyecto contextoProyecto1 = generarMockContextoProyecto(1L, proyecto1.getId());
    entityManager.persistAndFlush(contextoProyecto1);

    Proyecto proyecto2 = entityManager.persistAndFlush(generarMockProyecto(2L));
    ContextoProyecto contextoProyecto2 = generarMockContextoProyecto(2L, proyecto2.getId());
    entityManager.persistAndFlush(contextoProyecto2);

    Long proyectoIdBuscado = proyecto1.getId();

    // when: se busca el ContextoProyecto por el idProyecto
    ContextoProyecto contextoProyectoEncontrado = repository.findByProyectoId(proyectoIdBuscado).get();

    // then: Se recupera el ContextoProyecto con el idProyecto buscado
    Assertions.assertThat(contextoProyectoEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(contextoProyectoEncontrado.getIntereses()).as("getIntereses")
        .isEqualTo(contextoProyecto1.getIntereses());
    Assertions.assertThat(contextoProyectoEncontrado.getObjetivos()).as("getObjetivos")
        .isEqualTo(contextoProyecto1.getObjetivos());
  }

  @Test
  public void findByProyectoNoExiste_ReturnsNull() throws Exception {

    // given: 2 ContextoProyecto que no coinciden con el idProyecto buscado
    Proyecto proyecto1 = entityManager.persistAndFlush(generarMockProyecto(1L));
    ContextoProyecto contextoProyecto1 = generarMockContextoProyecto(1L, proyecto1.getId());
    entityManager.persistAndFlush(contextoProyecto1);

    Long proyectoIdBuscado = 2L;

    // when: se busca el ContextoProyecto por idProyecto que no existe
    Optional<ContextoProyecto> contextoProyectoEncontrado = repository.findByProyectoId(proyectoIdBuscado);

    // then: No se recupera ningún ContextoProyecto
    Assertions.assertThat(contextoProyectoEncontrado).isEqualTo(Optional.empty());
  }

  private Proyecto generarMockProyecto(Long proyectoId) {
    // @formatter:off
    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre("nombreModeloEjecucion")
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    Proyecto proyecto = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo("PRO")
        .fechaInicio(Instant.now())
        .fechaFin(Instant.now()).activo(Boolean.TRUE)
        .fechaBase(Instant.now())
        .build();
    // @formatter:on
    return proyecto;
  }

  /**
   * Función que devuelve un objeto ContextoProyecto
   * 
   * @param id identificador
   * @return el objeto ContextoProyecto
   */
  private ContextoProyecto generarMockContextoProyecto(Long id, Long proyectoId) {
    ContextoProyecto contextoProyecto = new ContextoProyecto();
    contextoProyecto.setProyectoId(proyectoId);
    contextoProyecto.setIntereses("intereses");
    contextoProyecto.setObjetivos("objetivos");
    contextoProyecto.setPropiedadResultados(ContextoProyecto.PropiedadResultados.COMPARTIDA);
    return contextoProyecto;
  }

}
