package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ConvocatoriaPeriodoSeguimientoCientificoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaPeriodoSeguimientoCientificoRepository repository;

  @Test
  public void findAllByConvocatoriaIdOrderByMesInicial_ReturnsConvocatoriaPeriodoSeguimientoCientificoList()
      throws Exception {

    // given: 10 ConvocatoriaPeriodoSeguimientoCientifico with same ConvocatoriId
    // @formatter:off
    Convocatoria convocatoria1 = Convocatoria.builder()
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-1")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(convocatoria1);

    Convocatoria convocatoria2 = Convocatoria.builder()
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-2")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(convocatoria2);

    for (int i = 11; i > 1; i--) {
      ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
          .builder()
          .convocatoriaId((i % 2 == 0) ? convocatoria2.getId() : convocatoria1.getId())
          .numPeriodo(i / 2)
          .tipoSeguimiento(TipoSeguimiento.FINAL)
          .mesInicial(i - 1)
          .mesFinal(i)
          .build();
      // @formatter:on

      entityManager.persistAndFlush(convocatoriaPeriodoSeguimientoCientifico);
    }
    Long convocatoriaIdBuscado = convocatoria1.getId();

    // when: se busca ConvocatoriaPeriodoSeguimientoCientifico por ConvocatoriaId
    // ordenadas por Mes Inicial
    List<ConvocatoriaPeriodoSeguimientoCientifico> dataFound = repository
        .findAllByConvocatoriaIdOrderByMesInicial(convocatoriaIdBuscado);

    // then: Se recupera ConvocatoriaPeriodoSeguimientoCientifico con el
    // ConvocatoriaId ordenados por Mes Inicial
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound).size().isEqualTo(5);

    for (int i = 0; i < dataFound.size(); i++) {
      int numPeriodo = dataFound.get(i).getNumPeriodo();
      Assertions.assertThat(dataFound.get(i).getNumPeriodo()).as("getNumPeriodo()").isEqualTo(i + 1);
      Assertions.assertThat(dataFound.get(i).getMesInicial()).as("getMesInicial()").isEqualTo(numPeriodo * 2);
      Assertions.assertThat(dataFound.get(i).getMesFinal()).as("getMesFinal()").isEqualTo((numPeriodo * 2) + 1);
    }
  }

  @Test
  public void findAllByConvocatoriaIdOrderByMesInicial_ReturnsNull() throws Exception {
    // given: 10 ConvocatoriaPeriodoSeguimientoCientifico
    // @formatter:off
    Convocatoria convocatoria1 = Convocatoria.builder()
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-1")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(convocatoria1);

    Convocatoria convocatoria2 = Convocatoria.builder()
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-2")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(convocatoria2);
    // @formatter:on

    for (int i = 11; i > 1; i--) {
      // @formatter:off
      ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico = ConvocatoriaPeriodoSeguimientoCientifico
          .builder()
          .convocatoriaId((i % 2 == 0) ? convocatoria2.getId() : convocatoria1.getId())
          .numPeriodo(i / 2)
          .tipoSeguimiento(TipoSeguimiento.FINAL)
          .mesInicial(i - 1)
          .mesFinal(i)
          .build();
      // @formatter:on

      if (i % 2 == 0) {
        entityManager.persistAndFlush(convocatoriaPeriodoSeguimientoCientifico);
      }
    }

    Long convocatoriaIdBuscado = convocatoria1.getId();

    // when: se busca ConvocatoriaPeriodoSeguimientoCientifico para ConvocatoriaId
    List<ConvocatoriaPeriodoSeguimientoCientifico> dataFound = repository
        .findAllByConvocatoriaIdOrderByMesInicial(convocatoriaIdBuscado);

    // then: No encuentra ConvocatoriaPeriodoSeguimientoCientifico para
    // ConvocatoriaId
    Assertions.assertThat(dataFound).isEmpty();
  }

}
