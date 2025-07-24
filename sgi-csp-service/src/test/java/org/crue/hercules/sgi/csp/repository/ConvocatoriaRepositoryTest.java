package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.model.Convocatoria;
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
class ConvocatoriaRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaRepository repository;

  @Test
  void findByCodigo_ReturnsConvocatoria() {
    // given: data Convocatoria with codigo to find
    Convocatoria convocatoria = generarMockConvocatoria("-001");

    // when: find given codigo
    Convocatoria dataFound = repository.findByCodigo(convocatoria.getCodigo()).get();

    // then: Convocatoria with given codigo is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(convocatoria.getId());
    Assertions.assertThat(dataFound.getUnidadGestionRef()).isEqualTo(convocatoria.getUnidadGestionRef());
    Assertions.assertThat(dataFound.getModeloEjecucion().getId()).isEqualTo(convocatoria.getModeloEjecucion().getId());
    Assertions.assertThat(dataFound.getCodigo()).isEqualTo(convocatoria.getCodigo());
    Assertions.assertThat(dataFound.getFechaPublicacion()).isEqualTo(convocatoria.getFechaPublicacion());
    Assertions.assertThat(dataFound.getFechaProvisional()).isEqualTo(convocatoria.getFechaProvisional());
    Assertions.assertThat(dataFound.getFechaConcesion()).isEqualTo(convocatoria.getFechaConcesion());
    Assertions.assertThat(dataFound.getTitulo()).isEqualTo(convocatoria.getTitulo());
    Assertions.assertThat(dataFound.getObjeto()).isEqualTo(convocatoria.getObjeto());
    Assertions.assertThat(dataFound.getObservaciones()).isEqualTo(convocatoria.getObservaciones());
    Assertions.assertThat(dataFound.getFinalidad().getId()).isEqualTo(convocatoria.getFinalidad().getId());
    Assertions.assertThat(dataFound.getRegimenConcurrencia().getId())
        .isEqualTo(convocatoria.getRegimenConcurrencia().getId());
    Assertions.assertThat(dataFound.getEstado()).isEqualTo(convocatoria.getEstado());
    Assertions.assertThat(dataFound.getDuracion()).isEqualTo(convocatoria.getDuracion());
    Assertions.assertThat(dataFound.getAmbitoGeografico().getId())
        .isEqualTo(convocatoria.getAmbitoGeografico().getId());
    Assertions.assertThat(dataFound.getClasificacionCVN()).isEqualTo(convocatoria.getClasificacionCVN());
    Assertions.assertThat(dataFound.getActivo()).isEqualTo(convocatoria.getActivo());
  }

  @Test
  void findByCodigo_ReturnsNull() {
    // given: codigo to find
    String codigo = "codigo-001";

    // when: find given codigo
    Optional<Convocatoria> dataFound = repository.findByCodigo(codigo);

    // then: Convocatoria with given codigo is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }

  /**
   * Función que genera Convocatoria
   * 
   * @param suffix
   * @return el objeto Convocatoria
   */
  private Convocatoria generarMockConvocatoria(String suffix) {
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

    Set<TipoRegimenConcurrenciaNombre> nombre = new HashSet<>();
    nombre.add(new TipoRegimenConcurrenciaNombre(Language.ES, "nombreTipoRegimenConcurrencia" + suffix));

    TipoRegimenConcurrencia tipoRegimenConcurrencia = TipoRegimenConcurrencia.builder()
        .nombre(nombre)
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
    convocatoriaTitulo.add(new ConvocatoriaTitulo(Language.ES, "titulo-" + suffix));

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
    return entityManager.persistAndFlush(convocatoria);
  }
}
