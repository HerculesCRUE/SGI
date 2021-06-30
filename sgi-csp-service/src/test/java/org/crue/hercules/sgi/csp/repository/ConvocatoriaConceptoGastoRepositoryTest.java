package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;

@DataJpaTest
public class ConvocatoriaConceptoGastoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaConceptoGastoRepository repository;

  @Test
  public void findAllByConvocatoriaIdAndConceptoGastoActivoTrueAndPermitidoTrue_ReturnsPageConvocatoriaConceptoGasto()
      throws Exception {
    // given: data ConvocatoriaConceptoGasto to find by Convocatoria and
    // ConceptoGasto and permitido
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto1 = generarConvocatoriaConceptoGasto("-001", true);

    entityManager.persistAndFlush(convocatoriaConceptoGasto1);
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto2 = generarConvocatoriaConceptoGasto("-002", true);
    entityManager.persistAndFlush(convocatoriaConceptoGasto2);

    Long convocatoriaIdBuscado = convocatoriaConceptoGasto1.getConvocatoriaId();

    // when: find by Convocatoria and ConceptoGasto and permitido
    Page<ConvocatoriaConceptoGasto> dataFound = repository
        .findAllByConvocatoriaIdAndConceptoGastoActivoTrueAndPermitidoTrue(convocatoriaIdBuscado, null);

    // then: ConvocatoriaConceptoGasto is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getContent().get(0).getId()).isEqualTo(convocatoriaConceptoGasto1.getId());
    Assertions.assertThat(dataFound.getContent().get(0).getConvocatoriaId())
        .isEqualTo(convocatoriaConceptoGasto1.getConvocatoriaId());
    Assertions.assertThat(dataFound.getContent().get(0).getConceptoGasto().getId())
        .isEqualTo(convocatoriaConceptoGasto1.getConceptoGasto().getId());
    Assertions.assertThat(dataFound.getContent().get(0).getPermitido())
        .isEqualTo(convocatoriaConceptoGasto1.getPermitido());
  }

  @Test
  public void findAllByConvocatoriaIdAndConceptoGastoActivoTrueAndPermitidoTrue_ReturnsEmptyList() throws Exception {
    // given: data ConvocatoriaConceptoGasto to find by Convocatoria and
    // ConceptoGasto and permitido
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto1 = generarConvocatoriaConceptoGasto("-001", false);

    entityManager.persistAndFlush(convocatoriaConceptoGasto1);
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto2 = generarConvocatoriaConceptoGasto("-002", false);
    entityManager.persistAndFlush(convocatoriaConceptoGasto2);

    Long convocatoriaIdBuscado = convocatoriaConceptoGasto1.getConvocatoriaId();

    // when: find by Convocatoria and ConceptoGasto and permitido
    Page<ConvocatoriaConceptoGasto> dataFound = repository
        .findAllByConvocatoriaIdAndConceptoGastoActivoTrueAndPermitidoTrue(convocatoriaIdBuscado, null);

    // then: ConvocatoriaConceptoGasto is not found
    Assertions.assertThat(dataFound).size().isEqualTo(0);
  }

  /**
   * Función que genera ConvocatoriaConceptoGasto
   * 
   * @param suffix
   * @return el objeto ConvocatoriaConceptoGasto
   */
  private ConvocatoriaConceptoGasto generarConvocatoriaConceptoGasto(String suffix, Boolean permitido) {

    // @formatter:off
    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre("nombreModeloEjecucion" + suffix)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    TipoFinalidad tipoFinalidad = TipoFinalidad.builder()
        .nombre("nombreTipoFinalidad" + suffix)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(tipoFinalidad);

    ModeloTipoFinalidad modeloTipoFinalidad = ModeloTipoFinalidad.builder()
        .modeloEjecucion(modeloEjecucion)
        .tipoFinalidad(tipoFinalidad)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(modeloTipoFinalidad);

    TipoRegimenConcurrencia tipoRegimenConcurrencia = TipoRegimenConcurrencia.builder()
        .nombre("nombreTipoRegimenConcurrencia" + suffix)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(tipoRegimenConcurrencia);

    TipoAmbitoGeografico tipoAmbitoGeografico = TipoAmbitoGeografico.builder()
        .nombre("nombreTipoAmbitoGeografico" + suffix)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(tipoAmbitoGeografico);

    Convocatoria convocatoria = Convocatoria.builder()
        .unidadGestionRef("unidad" + suffix)
        .modeloEjecucion(modeloEjecucion)
        .codigo("codigo" + suffix)
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaProvisional(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaConcesion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo" + suffix)
        .objeto("objeto" + suffix)
        .observaciones("observaciones" + suffix)
        .finalidad(modeloTipoFinalidad.getTipoFinalidad())
        .regimenConcurrencia(tipoRegimenConcurrencia)
        .colaborativos(Boolean.TRUE)
        .estado(Convocatoria.Estado.REGISTRADA)
        .duracion(12)
        .ambitoGeografico(tipoAmbitoGeografico)
        .clasificacionCVN(ClasificacionCVN.AYUDAS)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(convocatoria);

    ConceptoGasto conceptoGasto = ConceptoGasto.builder()
        .nombre("nombreConceptoGasto" + suffix)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(conceptoGasto);

    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = ConvocatoriaConceptoGasto.builder()
        .convocatoriaId(convocatoria.getId())
        .conceptoGasto(conceptoGasto)
        .observaciones("obs-1")
        .permitido(permitido).build();
    // @formatter:on
    return entityManager.persistAndFlush(convocatoriaConceptoGasto);
  }
}
