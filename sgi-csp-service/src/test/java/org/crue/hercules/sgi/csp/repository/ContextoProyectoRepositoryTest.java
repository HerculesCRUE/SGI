package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ContextoProyecto;
import org.crue.hercules.sgi.csp.model.ContextoProyectoIntereses;
import org.crue.hercules.sgi.csp.model.ContextoProyectoObjetivos;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ContextoProyectoRepositoryTest
 */
@DataJpaTest
class ContextoProyectoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ContextoProyectoRepository repository;

  @Test
  void findByProyectoId_ReturnsContextoProyecto() {

    // given: 2 ContextoProyecto de los que 1 coincide con el idProyecto buscado
    Proyecto proyecto1 = entityManager.persistAndFlush(generarMockProyecto());
    ContextoProyecto contextoProyecto1 = generarMockContextoProyecto(proyecto1.getId());
    entityManager.persistAndFlush(contextoProyecto1);

    Proyecto proyecto2 = entityManager.persistAndFlush(generarMockProyecto());
    ContextoProyecto contextoProyecto2 = generarMockContextoProyecto(proyecto2.getId());
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
  void findByProyectoNoExiste_ReturnsNull() {

    // given: 2 ContextoProyecto que no coinciden con el idProyecto buscado
    Proyecto proyecto1 = entityManager.persistAndFlush(generarMockProyecto());
    ContextoProyecto contextoProyecto1 = generarMockContextoProyecto(proyecto1.getId());
    entityManager.persistAndFlush(contextoProyecto1);

    Long proyectoIdBuscado = 2000L;

    // when: se busca el ContextoProyecto por idProyecto que no existe
    Optional<ContextoProyecto> contextoProyectoEncontrado = repository.findByProyectoId(proyectoIdBuscado);

    // then: No se recupera ningún ContextoProyecto
    Assertions.assertThat(contextoProyectoEncontrado).isEmpty();
  }

  private Proyecto generarMockProyecto() {
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombreModeloEjecucion"));

    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre(nombreModeloEjecucion)
        .activo(Boolean.TRUE)
        .externo(Boolean.FALSE)
        .contrato(Boolean.FALSE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    Set<ProyectoTitulo> tituloProyecto = new HashSet<>();
    tituloProyecto.add(new ProyectoTitulo(Language.ES, "PRO"));

    return Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo(tituloProyecto)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.now()).activo(Boolean.TRUE)
        .build();
  }

  /**
   * Función que devuelve un objeto ContextoProyecto
   * 
   * @param id identificador
   * @return el objeto ContextoProyecto
   */
  private ContextoProyecto generarMockContextoProyecto(Long id) {
    Set<ContextoProyectoObjetivos> objetivosContextoProyecto = new HashSet<>();
    objetivosContextoProyecto.add(new ContextoProyectoObjetivos(Language.ES, "objetivos"));
    Set<ContextoProyectoIntereses> interesesContextoProyecto = new HashSet<>();
    interesesContextoProyecto.add(new ContextoProyectoIntereses(Language.ES, "intereses"));

    ContextoProyecto contextoProyecto = new ContextoProyecto();
    contextoProyecto.setProyectoId(id);
    contextoProyecto.setIntereses(interesesContextoProyecto);
    contextoProyecto.setObjetivos(objetivosContextoProyecto);
    contextoProyecto.setPropiedadResultados(ContextoProyecto.PropiedadResultados.COMPARTIDA);
    return contextoProyecto;
  }

}
