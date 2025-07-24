package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConceptoGastoNombre;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoObservaciones;
import org.crue.hercules.sgi.csp.model.ConvocatoriaObjeto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaObservaciones;
import org.crue.hercules.sgi.csp.model.ConvocatoriaTitulo;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeograficoNombre;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidadNombre;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrenciaNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;

@DataJpaTest
class ConvocatoriaConceptoGastoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaConceptoGastoRepository repository;

  @Test
  void findAllByConvocatoriaIdAndConceptoGastoActivoTrueAndPermitidoTrue_ReturnsPageConvocatoriaConceptoGasto()
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
  void findAllByConvocatoriaIdAndConceptoGastoActivoTrueAndPermitidoTrue_ReturnsEmptyList() throws Exception {
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
    Assertions.assertThat(dataFound).size().isZero();
  }

  /**
   * Función que genera ConvocatoriaConceptoGasto
   * 
   * @param suffix
   * @return el objeto ConvocatoriaConceptoGasto
   */
  private ConvocatoriaConceptoGasto generarConvocatoriaConceptoGasto(String suffix, Boolean permitido) {
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombreModeloEjecucion" + suffix));

    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre(nombreModeloEjecucion)
        .activo(Boolean.TRUE)
        .externo(Boolean.FALSE)
        .contrato(Boolean.FALSE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    Set<TipoFinalidadNombre> nombreTipoFinalidad = new HashSet<>();
    nombreTipoFinalidad.add(new TipoFinalidadNombre(Language.ES, "nombreTipoFinalidad" + suffix));

    TipoFinalidad tipoFinalidad = TipoFinalidad.builder()
        .nombre(nombreTipoFinalidad)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(tipoFinalidad);

    ModeloTipoFinalidad modeloTipoFinalidad = ModeloTipoFinalidad.builder()
        .modeloEjecucion(modeloEjecucion)
        .tipoFinalidad(tipoFinalidad)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(modeloTipoFinalidad);

    Set<TipoRegimenConcurrenciaNombre> tipoRegimenConcurrenciaNombre = new HashSet<>();
    tipoRegimenConcurrenciaNombre
        .add(new TipoRegimenConcurrenciaNombre(Language.ES, "nombreTipoRegimenConcurrencia" + suffix));

    TipoRegimenConcurrencia tipoRegimenConcurrencia = TipoRegimenConcurrencia.builder()
        .nombre(tipoRegimenConcurrenciaNombre)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(tipoRegimenConcurrencia);

    Set<TipoAmbitoGeograficoNombre> tipoAmbitoGeograficoNombre = new HashSet<>();
    tipoAmbitoGeograficoNombre.add(new TipoAmbitoGeograficoNombre(Language.ES, "nombreTipoAmbitoGeografico" + suffix));

    TipoAmbitoGeografico tipoAmbitoGeografico = TipoAmbitoGeografico.builder()
        .nombre(tipoAmbitoGeograficoNombre)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(tipoAmbitoGeografico);

    Set<ConvocatoriaTitulo> convocatoriaTitulo = new HashSet<>();
    convocatoriaTitulo.add(new ConvocatoriaTitulo(Language.ES, "titulo" + suffix));

    Set<ConvocatoriaObjeto> convocatoriaObjeto = new HashSet<>();
    convocatoriaObjeto.add(new ConvocatoriaObjeto(Language.ES, "objeto-" + suffix));

    Set<ConvocatoriaObservaciones> convocatoriaObservaciones = new HashSet<>();
    convocatoriaObservaciones.add(new ConvocatoriaObservaciones(Language.ES, "observaciones-" + suffix));

    Convocatoria convocatoria = Convocatoria.builder()
        .unidadGestionRef("unidad" + suffix)
        .modeloEjecucion(modeloEjecucion)
        .codigo("codigo" + suffix)
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaProvisional(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaConcesion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo(convocatoriaTitulo)
        .objeto(convocatoriaObjeto)
        .observaciones(convocatoriaObservaciones)
        .finalidad(modeloTipoFinalidad.getTipoFinalidad())
        .regimenConcurrencia(tipoRegimenConcurrencia)
        .estado(Convocatoria.Estado.REGISTRADA)
        .duracion(12)
        .ambitoGeografico(tipoAmbitoGeografico)
        .clasificacionCVN(ClasificacionCVN.AYUDAS)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(convocatoria);

    Set<ConceptoGastoNombre> nombreConceptoGasto = new HashSet<>();
    nombreConceptoGasto.add(new ConceptoGastoNombre(Language.ES, "nombreConceptoGasto" + suffix));
    ConceptoGasto conceptoGasto = ConceptoGasto.builder()
        .nombre(nombreConceptoGasto)
        .activo(Boolean.TRUE)
        .costesIndirectos(true)
        .build();
    entityManager.persistAndFlush(conceptoGasto);

    Set<ConvocatoriaConceptoGastoObservaciones> observacionesConvocatoriaConceptoGasto = new HashSet<>();
    observacionesConvocatoriaConceptoGasto
        .add(new ConvocatoriaConceptoGastoObservaciones(Language.ES, "obs-1"));

    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = ConvocatoriaConceptoGasto.builder()
        .convocatoriaId(convocatoria.getId())
        .conceptoGasto(conceptoGasto)
        .observaciones(observacionesConvocatoriaConceptoGasto)
        .permitido(permitido).build();

    return entityManager.persistAndFlush(convocatoriaConceptoGasto);
  }
}
