package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ConvocatoriaEnlaceRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaEnlaceRepository repository;

  @Test
  public void findByConvocatoriaIdAndUrl_ReturnsConvocatoriaEnlace() throws Exception {
    // given: data ConvocatoriaEnlace to find by Convocatoria and Url
    ConvocatoriaEnlace convocatoriaEnlace1 = generarConvocatoriaEnlace("-001");

    entityManager.persistAndFlush(convocatoriaEnlace1);
    ConvocatoriaEnlace convocatoriaEnlace2 = generarConvocatoriaEnlace("-002");
    entityManager.persistAndFlush(convocatoriaEnlace2);

    Long convocatoriaIdBuscado = convocatoriaEnlace1.getConvocatoriaId();
    String urlBuscada = "www.url1.com";

    // when: find by by Convocatoria and url
    ConvocatoriaEnlace dataFound = repository.findByConvocatoriaIdAndUrl(convocatoriaIdBuscado, urlBuscada).get();

    // then: ConvocatoriaEnlace is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(convocatoriaEnlace1.getId());
    Assertions.assertThat(dataFound.getConvocatoriaId()).isEqualTo(convocatoriaEnlace1.getConvocatoriaId());
    Assertions.assertThat(dataFound.getUrl()).isEqualTo(convocatoriaEnlace1.getUrl());
  }

  @Test
  public void findByConvocatoriaIdAndUrl_ReturnsNull() throws Exception {
    // given: data ConvocatoriaEnlace to find by Convocatoria and Url

    ConvocatoriaEnlace convocatoriaEnlace1 = generarConvocatoriaEnlace("-001");

    entityManager.persistAndFlush(convocatoriaEnlace1);
    ConvocatoriaEnlace convocatoriaEnlace2 = generarConvocatoriaEnlace("-002");
    entityManager.persistAndFlush(convocatoriaEnlace2);

    Long convocatoriaIdBuscado = convocatoriaEnlace1.getConvocatoriaId();
    String urlBuscada = "www.url3.com";

    // when: find by by Convocatoria and url
    Optional<ConvocatoriaEnlace> dataFound = repository.findByConvocatoriaIdAndUrl(convocatoriaIdBuscado, urlBuscada);

    // then: ConvocatoriaEnlace is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }

  /**
   * Función que genera ConvocatoriaEnlace
   * 
   * @param suffix
   * @return el objeto ConvocatoriaEnlace
   */
  private ConvocatoriaEnlace generarConvocatoriaEnlace(String suffix) {

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

    TipoEnlace tipoEnlace = TipoEnlace.builder()
        .nombre("nombreTipoEnlace" + suffix)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(tipoEnlace);

    ConvocatoriaEnlace convocatoriaEnlace = ConvocatoriaEnlace.builder()
        .convocatoriaId(convocatoria.getId())
        .tipoEnlace(tipoEnlace)
        .descripcion("descripcion-1")
        .url("www.url1.com")
        .build();
    // @formatter:on
    return entityManager.persistAndFlush(convocatoriaEnlace);
  }
}
