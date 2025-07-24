package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.AreaTematicaDescripcion;
import org.crue.hercules.sgi.csp.model.AreaTematicaNombre;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
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
class ConvocatoriaAreaTematicaRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaAreaTematicaRepository repository;

  @Test
  void findByConvocatoriaIdAndAreaTematicaId_ReturnsConvocatoriaAreaTematica() throws Exception {

    // given: data ConvocatoriaAreaTematica to find by Convocatoria and
    // AreaTematicaId
    ConvocatoriaAreaTematica convocatoriaAreaTematica1 = generarConvocatoriaAreaTematica("-001");
    generarConvocatoriaAreaTematica("-002");

    Long convocatoriaIdBuscado = convocatoriaAreaTematica1.getConvocatoriaId();
    Long areaTematicaIdBuscado = convocatoriaAreaTematica1.getAreaTematica().getId();

    // when: find by Convocatoria and AreaTematicaId
    ConvocatoriaAreaTematica dataFound = repository
        .findByConvocatoriaIdAndAreaTematicaId(convocatoriaIdBuscado, areaTematicaIdBuscado).get();

    // then: ConvocatoriaAreaTematica is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(convocatoriaAreaTematica1.getId());
    Assertions.assertThat(dataFound.getConvocatoriaId()).isEqualTo(convocatoriaAreaTematica1.getConvocatoriaId());
    Assertions.assertThat(dataFound.getAreaTematica().getId())
        .isEqualTo(convocatoriaAreaTematica1.getAreaTematica().getId());
  }

  @Test
  void findByConvocatoriaIdAndAreaTematicaId_ReturnsNull() throws Exception {
    // given: data ConvocatoriaAreaTematica to find by Convocatoria and
    // AreaTematicaId

    ConvocatoriaAreaTematica convocatoriaAreaTematica1 = generarConvocatoriaAreaTematica("-001");
    ConvocatoriaAreaTematica convocatoriaAreaTematica2 = generarConvocatoriaAreaTematica("-002");

    Long convocatoriaIdBuscado = convocatoriaAreaTematica1.getConvocatoriaId();
    Long areaTematicaIdBuscado = convocatoriaAreaTematica2.getAreaTematica().getId();

    // when: find by by Convocatoria and AreaTematicaId
    Optional<ConvocatoriaAreaTematica> dataFound = repository
        .findByConvocatoriaIdAndAreaTematicaId(convocatoriaIdBuscado, areaTematicaIdBuscado);

    // then: ConvocatoriaAreaTematica is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }

  /**
   * Función que genera ConvocatoriaEntidadGestora
   * 
   * @param suffix
   * @return el objeto ConvocatoriaEntidadGestora
   */
  private ConvocatoriaAreaTematica generarConvocatoriaAreaTematica(String suffix) {
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombreModeloEjecucion" + suffix));

    // @formatter:off
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
    tipoRegimenConcurrenciaNombre.add(new TipoRegimenConcurrenciaNombre(Language.ES, "nombreTipoRegimenConcurrencia" + suffix));


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

    Set<AreaTematicaNombre> nombreAreaTematicaPadre = new HashSet<>();
    nombreAreaTematicaPadre.add(new AreaTematicaNombre(Language.ES, "nombreAreaTematica" + suffix));

    Set<AreaTematicaDescripcion> descripcionAreaTematicaPadre = new HashSet<>();
    descripcionAreaTematicaPadre.add(new AreaTematicaDescripcion(Language.ES, "descripcionAreaTematica" + suffix));

    AreaTematica areaTematicaPadre = AreaTematica.builder()
        .nombre(nombreAreaTematicaPadre)
        .descripcion(descripcionAreaTematicaPadre)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(areaTematicaPadre);

    Set<AreaTematicaNombre> nombreAreaTematica = new HashSet<>();
    nombreAreaTematica.add(new AreaTematicaNombre(Language.ES, suffix));

    Set<AreaTematicaDescripcion> descripcionAreaTematica = new HashSet<>();
    descripcionAreaTematica.add(new AreaTematicaDescripcion(Language.ES, "areaTematica" + suffix));

    AreaTematica areaTematica = AreaTematica.builder()
        .nombre(nombreAreaTematica)
        .descripcion(descripcionAreaTematica)
        .padre(areaTematicaPadre)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(areaTematica);

    ConvocatoriaAreaTematica convocatoriaAreaTematica = ConvocatoriaAreaTematica.builder()
        .convocatoriaId(convocatoria.getId())
        .areaTematica(areaTematica)
        .observaciones("observaciones" + suffix)
        .build();
    // @formatter:on
    return entityManager.persistAndFlush(convocatoriaAreaTematica);
  }
}
