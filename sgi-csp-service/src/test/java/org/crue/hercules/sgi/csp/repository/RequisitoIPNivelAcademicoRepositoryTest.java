package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;
import org.crue.hercules.sgi.csp.repository.specification.RequisitoIPNivelAcademicoSpecifications;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;

@DataJpaTest
public class RequisitoIPNivelAcademicoRepositoryTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private RequisitoIPNivelAcademicoRepository repository;

  @Test
  void testDeleteInBulkByRequisitoIPId() {
    // given: 4 RequisitoIPNivelAcademico de un RequisitoIP y 2 de otro
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
    RequisitoIP requisitoIP = entityManager.persistAndFlush(
      RequisitoIP.builder()
        .id(convocatoria.getId())
      .sexoRef("Hombre")
      .build());
    RequisitoIP requisitoIP2 = entityManager.persistAndFlush(
      RequisitoIP.builder()
        .id(convocatoria2.getId())
      .sexoRef("Hombre")
      .build());
    // @formatter:on

    for (int i = 0; i < 4; i++) {
      // @formatter:off
      entityManager.persistAndFlush(RequisitoIPNivelAcademico.builder()
          .requisitoIPId(requisitoIP.getId())
          .nivelAcademicoRef("nivelAcademicoRef")
          .build());
      // @formatter:on
    }
    for (int i = 0; i < 2; i++) {
      // @formatter:off
      entityManager.persistAndFlush(RequisitoIPNivelAcademico.builder()
          .requisitoIPId(requisitoIP2.getId())
          .nivelAcademicoRef("nivelAcademicoRef")
          .build());
      // @formatter:on
    }
    // when: se borran los RequisitoIPNivelAcademico del primer RequisitoIP
    long eliminados = repository.deleteInBulkByRequisitoIPId(requisitoIP.getId());

    // then: se eliminan 4 registros
    Assertions.assertThat(eliminados).isEqualTo(4);
  }

  @Test
  void testByRequisitoIPId() {
    // given: 4 RequisitoIPNivelAcademico de un RequisitoIP y 2 de otro
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
    RequisitoIP requisitoIP = entityManager.persistAndFlush(
      RequisitoIP.builder()
        .id(convocatoria.getId())
      .sexoRef("Hombre")
      .build());
    RequisitoIP requisitoIP2 = entityManager.persistAndFlush(
      RequisitoIP.builder()
        .id(convocatoria2.getId())
      .sexoRef("Hombre")
      .build());
    // @formatter:on

    for (int i = 0; i < 4; i++) {
      // @formatter:off
      entityManager.persistAndFlush(RequisitoIPNivelAcademico.builder()
          .requisitoIPId(requisitoIP.getId())
          .nivelAcademicoRef("nivelAcademicoRef")
          .build());
      // @formatter:on
    }
    for (int i = 0; i < 2; i++) {
      // @formatter:off
      entityManager.persistAndFlush(RequisitoIPNivelAcademico.builder()
          .requisitoIPId(requisitoIP2.getId())
          .nivelAcademicoRef("nivelAcademicoRef")
          .build());
      // @formatter:on
    }
    // when: buscamos los RequisitoIPNivelAcademico del primer RequisitoIP
    Specification<RequisitoIPNivelAcademico> specs = RequisitoIPNivelAcademicoSpecifications
        .byRequisitoIPId(requisitoIP.getId());

    List<RequisitoIPNivelAcademico> returnValue = repository.findAll(specs);

    // then: se recuperan 4 registros
    Assertions.assertThat(returnValue).isNotNull();
    Assertions.assertThat(returnValue.size()).isEqualTo(4);
  }

}
