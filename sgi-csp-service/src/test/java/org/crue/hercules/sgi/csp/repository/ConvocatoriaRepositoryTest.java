package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ConvocatoriaRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaRepository repository;

  @Test
  public void findByCodigo_ReturnsConvocatoria() throws Exception {
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
    Assertions.assertThat(dataFound.getColaborativos()).isEqualTo(convocatoria.getColaborativos());
    Assertions.assertThat(dataFound.getEstado()).isEqualTo(convocatoria.getEstado());
    Assertions.assertThat(dataFound.getDuracion()).isEqualTo(convocatoria.getDuracion());
    Assertions.assertThat(dataFound.getAmbitoGeografico().getId())
        .isEqualTo(convocatoria.getAmbitoGeografico().getId());
    Assertions.assertThat(dataFound.getClasificacionCVN()).isEqualTo(convocatoria.getClasificacionCVN());
    Assertions.assertThat(dataFound.getActivo()).isEqualTo(convocatoria.getActivo());
  }

  @Test
  public void findByCodigo_ReturnsNull() throws Exception {
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
    // @formatter:on
    return entityManager.persistAndFlush(convocatoria);
  }
}
