package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlaceDescripcion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaObjeto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaObservaciones;
import org.crue.hercules.sgi.csp.model.ConvocatoriaTitulo;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeograficoNombre;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlaceNombre;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidadNombre;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrenciaNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ConvocatoriaEnlaceRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaEnlaceRepository repository;

  @Test
  void findByConvocatoriaIdAndUrl_ReturnsConvocatoriaEnlace() throws Exception {
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
  void findByConvocatoriaIdAndUrl_ReturnsNull() throws Exception {
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

    Set<TipoEnlaceNombre> tipoEnlaceNombre = new HashSet<>();
    tipoEnlaceNombre.add(new TipoEnlaceNombre(Language.ES, "nombreTipoEnlace" + suffix));

    TipoEnlace tipoEnlace = TipoEnlace.builder()
        .nombre(tipoEnlaceNombre)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(tipoEnlace);

    Set<ConvocatoriaEnlaceDescripcion> descripcionConvocatoriaEnlace = new HashSet<>();
    descripcionConvocatoriaEnlace.add(new ConvocatoriaEnlaceDescripcion(Language.ES, "descripcion-1"));

    ConvocatoriaEnlace convocatoriaEnlace = ConvocatoriaEnlace.builder()
        .convocatoriaId(convocatoria.getId())
        .tipoEnlace(tipoEnlace)
        .descripcion(descripcionConvocatoriaEnlace)
        .url("www.url1.com")
        .build();

    return entityManager.persistAndFlush(convocatoriaEnlace);
  }
}
