package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProyectoEntidadConvocanteRepositoryTest extends BaseRepositoryTest {
  @Autowired
  private ProyectoEntidadConvocanteRepository repository;

  @Test
  void existsByProyectoIdAndEntidadRef_returnsTrue() throws Exception {
    // given: 2 ProyectoEntidadConvocante (only 1 matches find criteria)
    String matchingEntidadRef = "Entidad1";
    String notMatchingEntidadRef = "Entidad2";

    // @formatter:off
    ModeloEjecucion modeloEjecucion = entityManager.persistAndFlush(
      ModeloEjecucion.builder()
        .nombre("nombre-1")
        .descripcion("descripcion-1")
        .activo(true)
        .contrato(Boolean.FALSE)
        .externo(Boolean.FALSE)
      .build()
    );
    Proyecto proyecto = entityManager.persistAndFlush(
      Proyecto.builder()
        .titulo("titulo")
        .unidadGestionRef("2")
        .modeloEjecucion(modeloEjecucion)
        .fechaInicio(Instant.parse("2020-01-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-12-31T23:59:59Z"))
        .activo(true)
      .build()
    );
    entityManager.persistAndFlush(
      ProyectoEntidadConvocante.builder()
        .proyectoId(proyecto.getId())
        .entidadRef(matchingEntidadRef)
      .build()
    );
    entityManager.persistAndFlush(
      ProyectoEntidadConvocante.builder()
        .proyectoId(proyecto.getId())
        .entidadRef(notMatchingEntidadRef)
      .build()
    );
    // @formatter:on

    // when: check existence by existing ProyectoId and EntidadRef
    Boolean response = repository.existsByProyectoIdAndEntidadRef(proyecto.getId(), matchingEntidadRef);

    // then: the matching ProyectoEntidadConvocante is returned
    Assertions.assertThat(response).isTrue();
  }

  @Test
  void existsProyectoIdAndEntidadRef_NotExists_returnsFalse() throws Exception {
    // given: 2 ProyectoEntidadConvocante (no one matches find criteria)
    String entidadRef = "Entidad";
    String notMatchingEntidadRef1 = "Entidad1";
    String notMatchingEntidadRef2 = "Entidad2";

    // @formatter:off
    ModeloEjecucion modeloEjecucion = entityManager.persistAndFlush(
      ModeloEjecucion.builder()
        .nombre("nombre-1")
        .descripcion("descripcion-1")
        .activo(true)
        .contrato(Boolean.FALSE)
        .externo(Boolean.FALSE)
      .build()
    );
    Proyecto proyecto = entityManager.persistAndFlush(
      Proyecto.builder()
        .titulo("titulo")
        .unidadGestionRef("2")
        .modeloEjecucion(modeloEjecucion)
        .fechaInicio(Instant.parse("2020-01-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-12-31T23:59:59Z"))
        .activo(true)
      .build()
    );
    entityManager.persistAndFlush(
      ProyectoEntidadConvocante.builder()
        .proyectoId(proyecto.getId())
        .entidadRef(notMatchingEntidadRef1)
      .build()
    );
    entityManager.persistAndFlush(
      ProyectoEntidadConvocante.builder()
        .proyectoId(proyecto.getId())
        .entidadRef(notMatchingEntidadRef2)
      .build()
    );
    // formatter:on

    // when: check existence by no existing ProyectoId and EntidadRef
    boolean response = repository.existsByProyectoIdAndEntidadRef(proyecto.getId(), entidadRef);

    // then: the no ProyectoEntidadConvocante is returned
    Assertions.assertThat(response).isFalse();
  }
}
