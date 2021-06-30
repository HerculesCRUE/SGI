package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ConvocatoriaEntidadGestoraRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaEntidadGestoraRepository repository;

  @Test
  public void findByConvocatoriaIdIdAndEntidadRef_ReturnsConvocatoriaEntidadGestora() throws Exception {
    // given: data ConvocatoriaEntidadGestora to find by Convocatoria and
    // EntidadRef
    ConvocatoriaEntidadGestora convocatoriaEntidadGestora1 = generarConvocatoriaEntidadGestora("-001");
    generarConvocatoriaEntidadGestora("-002");

    Long convocatoriaIdBuscado = convocatoriaEntidadGestora1.getConvocatoriaId();
    String entidadRefBuscado = convocatoriaEntidadGestora1.getEntidadRef();

    // when: find by by Convocatoria and EntidadRef
    ConvocatoriaEntidadGestora dataFound = repository
        .findByConvocatoriaIdAndEntidadRef(convocatoriaIdBuscado, entidadRefBuscado).get();

    // then: ConvocatoriaEntidadGestora is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(convocatoriaEntidadGestora1.getId());
    Assertions.assertThat(dataFound.getConvocatoriaId()).isEqualTo(convocatoriaEntidadGestora1.getConvocatoriaId());
    Assertions.assertThat(dataFound.getEntidadRef()).isEqualTo(convocatoriaEntidadGestora1.getEntidadRef());
  }

  @Test
  public void findByModeloEjecucionIdAndTipoFinalidadId_ReturnsNull() throws Exception {
    // given: data ConvocatoriaEntidadGestora to find by Convocatoria and EntidadRef
    ConvocatoriaEntidadGestora convocatoriaEntidadGestora1 = generarConvocatoriaEntidadGestora("-001");
    ConvocatoriaEntidadGestora convocatoriaEntidadGestora2 = generarConvocatoriaEntidadGestora("-002");

    Long convocatoriaIdBuscado = convocatoriaEntidadGestora1.getConvocatoriaId();
    String entidadRefBuscado = convocatoriaEntidadGestora2.getEntidadRef();

    // when: find by by Convocatoria and EntidadRef
    Optional<ConvocatoriaEntidadGestora> dataFound = repository.findByConvocatoriaIdAndEntidadRef(convocatoriaIdBuscado,
        entidadRefBuscado);

    // then: ConvocatoriaEntidadGestora is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }

  /**
   * Función que genera ConvocatoriaEntidadGestora
   * 
   * @param suffix
   * @return el objeto ConvocatoriaEntidadGestora
   */
  private ConvocatoriaEntidadGestora generarConvocatoriaEntidadGestora(String suffix) {

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

    ConvocatoriaEntidadGestora convocatoriaEntidadGestora = ConvocatoriaEntidadGestora.builder()
        .convocatoriaId(convocatoria.getId())
        .entidadRef("entidad" + suffix)
        .build();
    // @formatter:on
    return entityManager.persistAndFlush(convocatoriaEntidadGestora);
  }
}
