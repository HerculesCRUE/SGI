package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoNivelAcademico;
import org.crue.hercules.sgi.csp.repository.specification.RequisitoEquipoNivelAcademicoSpecifications;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

@DataJpaTest
public class RequisitoEquipoNivelAcademicoRepositoryTest extends BaseRepositoryTest {
  @Autowired
  private RequisitoEquipoNivelAcademicoRepository repository;

  @Test
  void testDeleteInBulkByRequisitoEquipoId() {
    // given: 4 RequisitoEquipoNivelAcademico de un RequisitoEquipo y 2 de otro
    // @formatter:off
    Convocatoria convocatoria = entityManager.persistAndFlush( 
      Convocatoria.builder()
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-000")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .activo(Boolean.TRUE)
        .build());
    Convocatoria convocatoria2 = entityManager.persistAndFlush( 
      Convocatoria.builder()
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-000")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .activo(Boolean.TRUE)
        .build());
    // @formatter:on

    // @formatter:off
    RequisitoEquipo requisitoEquipo = entityManager.persistAndFlush(
      RequisitoEquipo.builder()
        .id(convocatoria.getId())
      .build());
    RequisitoEquipo requisitoEquipo2 = entityManager.persistAndFlush(
      RequisitoEquipo.builder()
        .id(convocatoria2.getId())
      .build());
    // @formatter:on

    for (int i = 0; i < 4; i++) {
      // @formatter:off
      entityManager.persistAndFlush(RequisitoEquipoNivelAcademico.builder()
          .requisitoEquipoId(requisitoEquipo.getId())
          .nivelAcademicoRef("nivelAcademicoRef")
          .build());
      // @formatter:on
    }
    for (int i = 0; i < 2; i++) {
      // @formatter:off
      entityManager.persistAndFlush(RequisitoEquipoNivelAcademico.builder()
          .requisitoEquipoId(requisitoEquipo2.getId())
          .nivelAcademicoRef("nivelAcademicoRef")
          .build());
      // @formatter:on
    }
    // when: se borran los RequisitoEquipoNivelAcademico del primer RequisitoEquipo
    long eliminados = repository.deleteInBulkByRequisitoEquipoId(requisitoEquipo.getId());

    // then: se eliminan 4 registros
    Assertions.assertThat(eliminados).isEqualTo(4);
  }

  @Test
  void testByRequisitoEquipoId() {
    // given: 4 RequisitoEquipoNivelAcademico de un RequisitoEquipo y 2 de otro
    // @formatter:off
    Convocatoria convocatoria = entityManager.persistAndFlush( 
      Convocatoria.builder()
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-000")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .activo(Boolean.TRUE)
        .build());
    Convocatoria convocatoria2 = entityManager.persistAndFlush( 
      Convocatoria.builder()
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-000")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .activo(Boolean.TRUE)
        .build());
    // @formatter:on

    // @formatter:off
    RequisitoEquipo requisitoEquipo = entityManager.persistAndFlush(
      RequisitoEquipo.builder()
        .id(convocatoria.getId())
      .build());
    RequisitoEquipo requisitoEquipo2 = entityManager.persistAndFlush(
      RequisitoEquipo.builder()
        .id(convocatoria2.getId())
      .build());
    // @formatter:on

    for (int i = 0; i < 4; i++) {
      // @formatter:off
      entityManager.persistAndFlush(RequisitoEquipoNivelAcademico.builder()
          .requisitoEquipoId(requisitoEquipo.getId())
          .nivelAcademicoRef("nivelAcademicoRef")
          .build());
      // @formatter:on
    }
    for (int i = 0; i < 2; i++) {
      // @formatter:off
      entityManager.persistAndFlush(RequisitoEquipoNivelAcademico.builder()
          .requisitoEquipoId(requisitoEquipo2.getId())
          .nivelAcademicoRef("nivelAcademicoRef")
          .build());
      // @formatter:on
    }
    // when: buscamos los RequisitoEquipoNivelAcademico del primer RequisitoEquipo
    Specification<RequisitoEquipoNivelAcademico> specs = RequisitoEquipoNivelAcademicoSpecifications
        .byRequisitoEquipoId(requisitoEquipo.getId());

    List<RequisitoEquipoNivelAcademico> returnValue = repository.findAll(specs);

    // then: se recuperan 4 registros
    Assertions.assertThat(returnValue).isNotNull();
    Assertions.assertThat(returnValue.size()).isEqualTo(4);
  }

}
