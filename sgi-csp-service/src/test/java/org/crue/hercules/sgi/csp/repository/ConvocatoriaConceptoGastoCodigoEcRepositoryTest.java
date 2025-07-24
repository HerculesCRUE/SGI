package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConceptoGastoNombre;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
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

@DataJpaTest
class ConvocatoriaConceptoGastoCodigoEcRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaConceptoGastoCodigoEcRepository repository;

  @Test
  void findAllByConvocatoriaConceptoGastoId_ReturnsListConvocatoriaConceptoGastoCodigoEc() throws Exception {
    // given: data ConvocatoriaConceptoGastoCodigoEc to find by
    // ConvocatoriaConceptoGasto and permitido
    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc1 = generarConvocatoriaConceptoGastoCodigoEc(
        "-001", true);

    generarConvocatoriaConceptoGastoCodigoEc("-002", true);

    // when: find by ConvocatoriaConceptoGasto and permitido
    List<ConvocatoriaConceptoGastoCodigoEc> dataFound = repository
        .findAllByConvocatoriaConceptoGastoId(convocatoriaConceptoGastoCodigoEc1.getConvocatoriaConceptoGastoId());

    // then: ConvocatoriaConceptoGasto is found
    Assertions.assertThat(dataFound.get(0)).isNotNull();
    Assertions.assertThat(dataFound.get(0).getId()).isEqualTo(convocatoriaConceptoGastoCodigoEc1.getId());
    Assertions.assertThat(dataFound.get(0).getConvocatoriaConceptoGastoId())
        .isEqualTo(convocatoriaConceptoGastoCodigoEc1.getConvocatoriaConceptoGastoId());
  }

  @Test
  void findAllByConvocatoriaConceptoGastoId_ReturnsEmptyListConvocatoriaConceptoGastoCodigoEc()
      throws Exception {
    // given: data ConvocatoriaConceptoGastoCodigoEc to find by
    // ConvocatoriaConceptoGasto and permitido
    generarConvocatoriaConceptoGastoCodigoEc("-001", true);
    ConvocatoriaConceptoGastoCodigoEc lastConvocatoriaConceptoGastoCodigoEc = generarConvocatoriaConceptoGastoCodigoEc(
        "-002", true);

    // when: find by ConvocatoriaConceptoGasto and permitido
    List<ConvocatoriaConceptoGastoCodigoEc> dataFound = repository.findAllByConvocatoriaConceptoGastoId(
        lastConvocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGastoId() + 1);

    // then: ConvocatoriaConceptoGastoCodigoEc is not found
    Assertions.assertThat(dataFound).size().isZero();
  }

  /**
   * Función que genera ConvocatoriaConceptoGastoCodigoEc
   * 
   * @param suffix
   * @return el objeto ConvocatoriaConceptoGastoCodigoEc
   */
  private ConvocatoriaConceptoGastoCodigoEc generarConvocatoriaConceptoGastoCodigoEc(String suffix, Boolean permitido) {
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
    entityManager.persistAndFlush(convocatoriaConceptoGasto);

    ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc = ConvocatoriaConceptoGastoCodigoEc.builder()
        .convocatoriaConceptoGastoId(convocatoriaConceptoGasto.getId())
        .codigoEconomicoRef("cod" + suffix)
        .build();

    return entityManager.persistAndFlush(convocatoriaConceptoGastoCodigoEc);
  }
}
