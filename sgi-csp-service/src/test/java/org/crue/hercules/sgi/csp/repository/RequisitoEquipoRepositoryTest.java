package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * RequisitoEquipoRepositoryTest
 */
@DataJpaTest
class RequisitoEquipoRepositoryTest extends BaseRepositoryTest {
  @Autowired
  private RequisitoEquipoRepository repository;

  @Test
  void findByConvocatoriaId_ReturnsRequisitoEquipo() throws Exception {

    // given: 2 RequisitoEquipo de los que 1 coincide con el idConvocatoria buscado
    // @formatter:off
    Convocatoria convocatoria1 = entityManager.persistAndFlush(Convocatoria.builder()
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-1")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .activo(Boolean.TRUE)
        .build());
    // @formatter:on

    // @formatter:off
    RequisitoEquipo requisitoEquipo1 = entityManager.persistAndFlush(RequisitoEquipo.builder()
        .id(convocatoria1.getId())
        .fechaMinimaNivelAcademico(null)
        .fechaMaximaNivelAcademico(null)
        .edadMaxima(48)
        .ratioSexo(6)
        .sexoRef("sexo-ref")
        .vinculacionUniversidad(false)
        .fechaMinimaCategoriaProfesional(null)
        .fechaMaximaCategoriaProfesional(null)
        .numMinimoCompetitivos(10)
        .numMinimoNoCompetitivos(10)
        .numMaximoCompetitivosActivos(15)
        .numMaximoNoCompetitivosActivos(15)
        .otrosRequisitos("otros")
        .build());
    // @formatter:on

    // @formatter:on
    Convocatoria convocatoria2 = entityManager.persistAndFlush(
        Convocatoria.builder().estado(Convocatoria.Estado.BORRADOR).codigo("codigo-2").unidadGestionRef("2")
            .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z")).titulo("titulo").activo(Boolean.TRUE).build());
    // @formatter:on

    // @formatter:off
    entityManager.persistAndFlush(RequisitoEquipo.builder()
        .id(convocatoria2.getId())
        .fechaMinimaNivelAcademico(null)
        .fechaMaximaNivelAcademico(null)
        .edadMaxima(48)
        .ratioSexo(6)
        .sexoRef("sexo-ref")
        .vinculacionUniversidad(false)
        .fechaMinimaCategoriaProfesional(null)
        .fechaMaximaCategoriaProfesional(null)
        .numMinimoCompetitivos(10)
        .numMinimoNoCompetitivos(10)
        .numMaximoCompetitivosActivos(15)
        .numMaximoNoCompetitivosActivos(15)
        .otrosRequisitos("otros")
        .build());
    // @formatter:on

    Long convocatoriaIdBuscada = convocatoria1.getId();

    // when: se busca el RequisitoEquipopor idConvocatoria
    RequisitoEquipo requisitoEquipoEncontrado = repository.findById(convocatoriaIdBuscada).get();

    // then: Se recupera el RequisitoEquipo con el idConvocatoria buscado
    Assertions.assertThat(requisitoEquipoEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(requisitoEquipoEncontrado.getRatioSexo()).as("getRatioSexo")
        .isEqualTo(requisitoEquipo1.getRatioSexo());
    Assertions.assertThat(requisitoEquipoEncontrado.getEdadMaxima()).as("getEdadMaxima")
        .isEqualTo(requisitoEquipo1.getEdadMaxima());
  }

  @Test
  void findByConvocatoriaNoExiste_ReturnsNull() throws Exception {

    // given: 2 RequisitoEquipo que no coinciden con el idConvocatoria buscado
    // @formatter:off
    Convocatoria convocatoria1 = entityManager.persistAndFlush(Convocatoria.builder()
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-1")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .activo(Boolean.TRUE)
        .build());
    // @formatter:on

    // @formatter:off
    entityManager.persistAndFlush(RequisitoEquipo.builder()
        .id(convocatoria1.getId())
        .fechaMinimaNivelAcademico(null)
        .fechaMaximaNivelAcademico(null)
        .edadMaxima(48)
        .ratioSexo(6)
        .sexoRef("sexo-ref")
        .vinculacionUniversidad(false)
        .fechaMinimaCategoriaProfesional(null)
        .fechaMaximaCategoriaProfesional(null)
        .numMinimoCompetitivos(10)
        .numMinimoNoCompetitivos(10)
        .numMaximoCompetitivosActivos(15)
        .numMaximoNoCompetitivosActivos(15)
        .otrosRequisitos("otros")
        .build());
    // @formatter:on

    // @formatter:off
    Convocatoria convocatoria2 = entityManager.persistAndFlush(Convocatoria.builder()
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-2")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .activo(Boolean.TRUE)
        .build());
    // @formatter:on
    // @formatter:off
    entityManager.persistAndFlush(RequisitoEquipo.builder()
        .id(convocatoria2.getId())
        .fechaMinimaNivelAcademico(null)
        .fechaMaximaNivelAcademico(null)
        .edadMaxima(48)
        .ratioSexo(6)
        .sexoRef("sexo-ref")
        .vinculacionUniversidad(false)
        .fechaMinimaCategoriaProfesional(null)
        .fechaMaximaCategoriaProfesional(null)
        .numMinimoCompetitivos(10)
        .numMinimoNoCompetitivos(10)
        .numMaximoCompetitivosActivos(15)
        .numMaximoNoCompetitivosActivos(15)
        .otrosRequisitos("otros")
        .build());
    // @formatter:on

    Long convocatoriaIdBuscada = 99999L;

    // when: se busca el RequisitoEquipo por idConvocatoria
    Optional<RequisitoEquipo> requisitoEquipoEncontrado = repository.findById(convocatoriaIdBuscada);

    // then: Se recupera el RequisitoEquipo con el idConvocatoria buscado
    Assertions.assertThat(requisitoEquipoEncontrado).isEqualTo(Optional.empty());
  }

}