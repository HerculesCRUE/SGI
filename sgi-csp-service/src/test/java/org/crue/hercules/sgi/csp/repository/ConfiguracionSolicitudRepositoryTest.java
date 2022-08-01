package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ConfiguracionSolicitudRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConfiguracionSolicitudRepository repository;

  @Test
  void findByConvocatoriaId_ReturnsConfiguracionSolicitud() throws Exception {
    // given: data ConfiguracionSolicitud to find by Convocatoria
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
    ConfiguracionSolicitud configuracionSolicitud1 = entityManager
        .persistAndFlush(ConfiguracionSolicitud.builder().convocatoriaId(convocatoria1.getId()).build());

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
    entityManager.persistAndFlush(ConfiguracionSolicitud.builder().convocatoriaId(convocatoria2.getId()).build());

    Long convocatoriaIdBuscado = configuracionSolicitud1.getConvocatoriaId();

    // when: find by by Convocatoria
    ConfiguracionSolicitud dataFound = repository.findByConvocatoriaId(convocatoriaIdBuscado).get();

    // then: ConfiguracionSolicitud is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(configuracionSolicitud1.getId());
    Assertions.assertThat(dataFound.getConvocatoriaId()).isEqualTo(configuracionSolicitud1.getConvocatoriaId());
  }

  @Test
  void findByModeloEjecucionId_ReturnsNull() throws Exception {
    // given: data ConfiguracionSolicitud to find by Convocatoria
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
    entityManager.persistAndFlush(ConfiguracionSolicitud.builder().convocatoriaId(convocatoria1.getId()).build());

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
    ConfiguracionSolicitud configuracionSolicitud2 = ConfiguracionSolicitud.builder()
        .convocatoriaId(convocatoria2.getId()).build();

    Long convocatoriaIdBuscado = configuracionSolicitud2.getConvocatoriaId();

    // when: find by by Convocatoria
    Optional<ConfiguracionSolicitud> dataFound = repository.findByConvocatoriaId(convocatoriaIdBuscado);

    // then: ConfiguracionSolicitud is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }
}
