package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * RequisitoEquipoRepositoryTest
 */
@DataJpaTest
public class RequisitoEquipoRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private RequisitoEquipoRepository repository;

  @Test
  public void findByConvocatoriaId_ReturnsRequisitoEquipo() throws Exception {

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
    RequisitoEquipo requisitoEquipo1 = entityManager.persistAndFlush(new RequisitoEquipo(null, convocatoria1.getId(),
        "na-001", 4, 48, 6, false, "mc-001", 2, 10, 10, 15, 15, "otros"));

    // @formatter:on
    Convocatoria convocatoria2 = entityManager.persistAndFlush(
        Convocatoria.builder().estado(Convocatoria.Estado.BORRADOR).codigo("codigo-2").unidadGestionRef("2")
            .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z")).titulo("titulo").activo(Boolean.TRUE).build());
    // @formatter:on
    entityManager.persistAndFlush(new RequisitoEquipo(null, convocatoria2.getId(), "na-001", 4, 48, 6, false, "mc-001",
        2, 10, 10, 15, 15, "otros"));

    Long convocatoriaIdBuscada = convocatoria1.getId();

    // when: se busca el RequisitoEquipopor idConvocatoria
    RequisitoEquipo requisitoEquipoEncontrado = repository.findByConvocatoriaId(convocatoriaIdBuscada).get();

    // then: Se recupera el RequisitoEquipo con el idConvocatoria buscado
    Assertions.assertThat(requisitoEquipoEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(requisitoEquipoEncontrado.getRatioMujeres()).as("getRatioMujeres")
        .isEqualTo(requisitoEquipo1.getRatioMujeres());
    Assertions.assertThat(requisitoEquipoEncontrado.getEdadMaxima()).as("getEdadMaxima")
        .isEqualTo(requisitoEquipo1.getEdadMaxima());
    Assertions.assertThat(requisitoEquipoEncontrado.getModalidadContratoRef()).as("getModalidadContratoRef")
        .isEqualTo(requisitoEquipo1.getModalidadContratoRef());
  }

  @Test
  public void findByConvocatoriaNoExiste_ReturnsNull() throws Exception {

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

    entityManager.persistAndFlush(new RequisitoEquipo(null, convocatoria1.getId(), "na-001", 4, 48, 6, false, "mc-001",
        2, 10, 10, 15, 15, "otros"));

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
    entityManager.persistAndFlush(new RequisitoEquipo(null, convocatoria2.getId(), "na-001", 4, 48, 6, false, "mc-001",
        2, 10, 10, 15, 15, "otros"));

    Long convocatoriaIdBuscada = 99999L;

    // when: se busca el RequisitoEquipo por idConvocatoria
    Optional<RequisitoEquipo> requisitoEquipoEncontrado = repository.findByConvocatoriaId(convocatoriaIdBuscada);

    // then: Se recupera el RequisitoEquipo con el idConvocatoria buscado
    Assertions.assertThat(requisitoEquipoEncontrado).isEqualTo(Optional.empty());
  }

}