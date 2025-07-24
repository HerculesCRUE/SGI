package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoJustificacion;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacionObservaciones;
import org.crue.hercules.sgi.csp.model.ConvocatoriaTitulo;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ConvocatoriaPeriodoJustificacionRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaPeriodoJustificacionRepository repository;

  @Test
  void findAllByConvocatoriaId_ReturnsConvocatoriaPeriodoJustificacion() {

    // given: 2 ConvocatoriaPeriodoJustificacion para el ConvocatoriaId buscado
    Set<ConvocatoriaTitulo> convocatoriaTitulo1 = new HashSet<>();
    convocatoriaTitulo1.add(new ConvocatoriaTitulo(Language.ES, "titulo"));

    Convocatoria convocatoria1 = Convocatoria.builder()
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-1")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo(convocatoriaTitulo1)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(convocatoria1);

    Set<ConvocatoriaTitulo> convocatoriaTitulo2 = new HashSet<>();
    convocatoriaTitulo2.add(new ConvocatoriaTitulo(Language.ES, "titulo"));

    Convocatoria convocatoria2 = Convocatoria.builder()
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-2")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo(convocatoriaTitulo2)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(convocatoria2);
    // @formatter:on
    Set<ConvocatoriaPeriodoJustificacionObservaciones> obsConvocatoriaPeriodoJustificacion1 = new HashSet<>();
    obsConvocatoriaPeriodoJustificacion1
        .add(new ConvocatoriaPeriodoJustificacionObservaciones(Language.ES, "observaciones-1"));

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion1 = new ConvocatoriaPeriodoJustificacion(null,
        convocatoria1.getId(), 1, 1, 2, Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"),
        obsConvocatoriaPeriodoJustificacion1, TipoJustificacion.FINAL);
    entityManager.persistAndFlush(convocatoriaPeriodoJustificacion1);

    Set<ConvocatoriaPeriodoJustificacionObservaciones> obsConvocatoriaPeriodoJustificacion2 = new HashSet<>();
    obsConvocatoriaPeriodoJustificacion2
        .add(new ConvocatoriaPeriodoJustificacionObservaciones(Language.ES, "observaciones-2"));

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion2 = new ConvocatoriaPeriodoJustificacion(null,
        convocatoria1.getId(), 2, 3, 5, Instant.parse("2020-12-10T00:00:00Z"), Instant.parse("2021-11-20T23:59:59Z"),
        obsConvocatoriaPeriodoJustificacion2, TipoJustificacion.PERIODICO);
    entityManager.persistAndFlush(convocatoriaPeriodoJustificacion2);

    Long convocatoriaIdBuscado = convocatoria1.getId();

    // when: se buscan los ConvocatoriaPeriodoJustificacion por ConvocatoriaId
    List<ConvocatoriaPeriodoJustificacion> dataFound = repository.findAllByConvocatoriaId(convocatoriaIdBuscado);

    // then: Se recuperan los ConvocatoriaPeriodoJustificacion con el ConvocatoriaId
    // buscado
    Assertions.assertThat(dataFound).hasSize(2);
    Assertions.assertThat(dataFound.get(0).getId()).isEqualTo(convocatoriaPeriodoJustificacion1.getId());
    Assertions.assertThat(dataFound.get(0).getConvocatoriaId())
        .isEqualTo(convocatoriaPeriodoJustificacion1.getConvocatoriaId());
    Assertions.assertThat(dataFound.get(0).getObservaciones())
        .isEqualTo(convocatoriaPeriodoJustificacion1.getObservaciones());
  }

  @Test
  void findFirstByConvocatoriaIdOrderByNumPeriodoDesc_ReturnsConvocatoriaPeriodoJustificacion() {

    // given: 2 ConvocatoriaPeriodoJustificacion de una Convocatoria
    // @formatter:off
    Convocatoria convocatoria1 = Convocatoria.builder()
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-1")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .activo(Boolean.TRUE)
        .build();
        Set<ConvocatoriaTitulo> convocatoriaTitulo = new HashSet<>();
        convocatoriaTitulo.add(new ConvocatoriaTitulo(Language.ES, "titulo-" + String.format("%03d", convocatoria1.getId())));
        convocatoria1.setTitulo(convocatoriaTitulo);
    entityManager.persistAndFlush(convocatoria1);
    // @formatter:on

    Set<ConvocatoriaPeriodoJustificacionObservaciones> obsConvocatoriaPeriodoJustificacion1 = new HashSet<>();
    obsConvocatoriaPeriodoJustificacion1
        .add(new ConvocatoriaPeriodoJustificacionObservaciones(Language.ES, "observaciones-1"));

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion1 = new ConvocatoriaPeriodoJustificacion(null,
        convocatoria1.getId(), 2, 7, 9, Instant.parse("2020-12-10T00:00:00Z"), Instant.parse("2021-11-20T23:59:59Z"),
        obsConvocatoriaPeriodoJustificacion1, TipoJustificacion.PERIODICO);
    entityManager.persistAndFlush(convocatoriaPeriodoJustificacion1);

    Set<ConvocatoriaPeriodoJustificacionObservaciones> obsConvocatoriaPeriodoJustificacion2 = new HashSet<>();
    obsConvocatoriaPeriodoJustificacion2
        .add(new ConvocatoriaPeriodoJustificacionObservaciones(Language.ES, "observaciones-2"));

    ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion2 = new ConvocatoriaPeriodoJustificacion(null,
        convocatoria1.getId(), 1, 3, 5, Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"),
        obsConvocatoriaPeriodoJustificacion2, TipoJustificacion.PERIODICO);
    entityManager.persistAndFlush(convocatoriaPeriodoJustificacion2);

    Long convocatoriaIdBuscado = convocatoria1.getId();

    // when: se busca el ConvocatoriaPeriodoJustificacion con el ultimo numero
    // periodo
    ConvocatoriaPeriodoJustificacion dataFound = repository
        .findFirstByConvocatoriaIdOrderByNumPeriodoDesc(convocatoriaIdBuscado).get();

    // then: Se recupera el ConvocatoriaPeriodoJustificacion con el ultimo numero
    // periodo
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).as("getId()").isEqualTo(convocatoriaPeriodoJustificacion1.getId());
    Assertions.assertThat(dataFound.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaPeriodoJustificacion1.getConvocatoriaId());
    Assertions.assertThat(dataFound.getMesFinal()).as("getObservaciones()")
        .isEqualTo(convocatoriaPeriodoJustificacion1.getMesFinal());
    Assertions.assertThat(dataFound.getObservaciones()).as("getObservaciones()")
        .isEqualTo(convocatoriaPeriodoJustificacion1.getObservaciones());
  }

}
