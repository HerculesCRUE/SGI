package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaTitulo;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.model.RequisitoEquipoCategoriaProfesional;
import org.crue.hercules.sgi.csp.repository.specification.RequisitoEquipoCategoriaProfesionalSpecifications;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

@DataJpaTest
class RequisitoEquipoCategoriaProfesionalRepositoryTest extends BaseRepositoryTest {
  @Autowired
  private RequisitoEquipoCategoriaProfesionalRepository repository;

  @Test
  void testDeleteInBulkByRequisitoEquipoId() {
    // given: 4 RequisitoEquipoCategoriaProfesional de un RequisitoEquipo y 2 de
    // otro
    Set<ConvocatoriaTitulo> convocatoriaTitulo = new HashSet<>();
    convocatoriaTitulo.add(new ConvocatoriaTitulo(Language.ES, "titulo"));

    Convocatoria convocatoria = entityManager.persistAndFlush(
        Convocatoria.builder()
            .estado(Convocatoria.Estado.BORRADOR)
            .codigo("codigo-000")
            .unidadGestionRef("2")
            .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
            .titulo(convocatoriaTitulo)
            .activo(Boolean.TRUE)
            .build());

    Set<ConvocatoriaTitulo> convocatoriaTitulo2 = new HashSet<>();
    convocatoriaTitulo2.add(new ConvocatoriaTitulo(Language.ES, "titulo"));

    Convocatoria convocatoria2 = entityManager.persistAndFlush(
        Convocatoria.builder()
            .estado(Convocatoria.Estado.BORRADOR)
            .codigo("codigo-000")
            .unidadGestionRef("2")
            .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
            .titulo(convocatoriaTitulo2)
            .activo(Boolean.TRUE)
            .build());

    RequisitoEquipo requisitoEquipo = entityManager.persistAndFlush(
        RequisitoEquipo.builder()
            .id(convocatoria.getId())
            .build());
    RequisitoEquipo requisitoEquipo2 = entityManager.persistAndFlush(
        RequisitoEquipo.builder()
            .id(convocatoria2.getId())
            .build());

    for (int i = 0; i < 4; i++) {
      // @formatter:off
      entityManager.persistAndFlush(RequisitoEquipoCategoriaProfesional.builder()
          .requisitoEquipoId(requisitoEquipo.getId())
          .categoriaProfesionalRef("categoriaProfesionalRef")
          .build());
      // @formatter:on
    }
    for (int i = 0; i < 2; i++) {
      // @formatter:off
      entityManager.persistAndFlush(RequisitoEquipoCategoriaProfesional.builder()
          .requisitoEquipoId(requisitoEquipo2.getId())
          .categoriaProfesionalRef("categoriaProfesionalRef")
          .build());
      // @formatter:on
    }
    // when: se borran los RequisitoEquipoCategoriaProfesional del primer
    // RequisitoEquipo
    long eliminados = repository.deleteInBulkByRequisitoEquipoId(requisitoEquipo.getId());

    // then: se eliminan 4 registros
    Assertions.assertThat(eliminados).isEqualTo(4);
  }

  @Test
  void testByRequisitoEquipoId() {
    // given: 4 RequisitoEquipoCategoriaProfesional de un RequisitoEquipo y 2 de
    // otro

    Set<ConvocatoriaTitulo> convocatoriaTitulo = new HashSet<>();
    convocatoriaTitulo
        .add(new ConvocatoriaTitulo(Language.ES, "titulo"));

    Convocatoria convocatoria = entityManager.persistAndFlush(
        Convocatoria.builder()
            .estado(Convocatoria.Estado.BORRADOR)
            .codigo("codigo-000")
            .unidadGestionRef("2")
            .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
            .titulo(convocatoriaTitulo)
            .activo(Boolean.TRUE)
            .build());

    Set<ConvocatoriaTitulo> convocatoriaTitulo2 = new HashSet<>();
    convocatoriaTitulo2.add(new ConvocatoriaTitulo(Language.ES, "titulo"));

    Convocatoria convocatoria2 = entityManager.persistAndFlush(
        Convocatoria.builder()
            .estado(Convocatoria.Estado.BORRADOR)
            .codigo("codigo-000")
            .unidadGestionRef("2")
            .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
            .titulo(convocatoriaTitulo2)
            .activo(Boolean.TRUE)
            .build());

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
      entityManager.persistAndFlush(RequisitoEquipoCategoriaProfesional.builder()
          .requisitoEquipoId(requisitoEquipo.getId())
          .categoriaProfesionalRef("categoriaProfesionalRef")
          .build());
      // @formatter:on
    }
    for (int i = 0; i < 2; i++) {
      // @formatter:off
      entityManager.persistAndFlush(RequisitoEquipoCategoriaProfesional.builder()
          .requisitoEquipoId(requisitoEquipo2.getId())
          .categoriaProfesionalRef("categoriaProfesionalRef")
          .build());
      // @formatter:on
    }
    // when: buscamos los RequisitoEquipoCategoriaProfesional del primer
    // RequisitoEquipo
    Specification<RequisitoEquipoCategoriaProfesional> specs = RequisitoEquipoCategoriaProfesionalSpecifications
        .byRequisitoEquipoId(requisitoEquipo.getId());

    List<RequisitoEquipoCategoriaProfesional> returnValue = repository.findAll(specs);

    // then: se recuperan 4 registros
    Assertions.assertThat(returnValue).isNotNull();
    Assertions.assertThat(returnValue).hasSize(4);
  }

}
