package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ConvocatoriaAreaTematicaRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaAreaTematicaRepository repository;

  @Test
  public void findByConvocatoriaIdAndAreaTematicaId_ReturnsConvocatoriaAreaTematica() throws Exception {

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
  public void findByConvocatoriaIdAndAreaTematicaId_ReturnsNull() throws Exception {
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

  @Test
  public void findByConvocatoriaId_ReturnsConvocatoriaAreaTematica() throws Exception {

    // given: data ConvocatoriaAreaTematica to find by Convocatoria and
    // AreaTematicaId
    ConvocatoriaAreaTematica convocatoriaAreaTematica1 = generarConvocatoriaAreaTematica("-001");
    generarConvocatoriaAreaTematica("-002");

    Long convocatoriaIdBuscado = convocatoriaAreaTematica1.getConvocatoriaId();

    // when: find by Convocatoria and AreaTematicaId
    ConvocatoriaAreaTematica dataFound = repository.findByConvocatoriaId(convocatoriaIdBuscado).get();

    // then: ConvocatoriaAreaTematica is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(convocatoriaAreaTematica1.getId());
    Assertions.assertThat(dataFound.getConvocatoriaId()).isEqualTo(convocatoriaAreaTematica1.getConvocatoriaId());
    Assertions.assertThat(dataFound.getAreaTematica().getId())
        .isEqualTo(convocatoriaAreaTematica1.getAreaTematica().getId());
  }

  @Test
  public void findByConvocatoriaId_ReturnsNull() throws Exception {
    // given: data ConvocatoriaAreaTematica to find by Convocatoria and
    // AreaTematicaId

    // when: find by by Convocatoria and AreaTematicaId
    Optional<ConvocatoriaAreaTematica> dataFound = repository.findByConvocatoriaId(5L);

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

    AreaTematica areaTematicaPadre = AreaTematica.builder()
        .nombre("nombreAreaTematica" + suffix)
        .descripcion("descripcionAreaTematica" + suffix)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(areaTematicaPadre);

    AreaTematica areaTematica = AreaTematica.builder()
        .nombre(suffix)
        .descripcion("areaTematica" + suffix)
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
