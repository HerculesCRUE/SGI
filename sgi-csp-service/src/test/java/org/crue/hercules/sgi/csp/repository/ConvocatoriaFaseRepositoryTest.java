package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFaseObservaciones;
import org.crue.hercules.sgi.csp.model.ConvocatoriaObjeto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaObservaciones;
import org.crue.hercules.sgi.csp.model.ConvocatoriaTitulo;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeograficoNombre;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFaseNombre;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidadNombre;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrenciaNombre;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaFaseSpecifications;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@DataJpaTest
class ConvocatoriaFaseRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaFaseRepository repository;

  @Test
  void existsConvocatoriaFaseConFechasSolapadas_WithLasDosFechasSolapadas_ReturnsConvocatoriaFase()
      throws Exception {

    // given: 2 ConvocatoriaFase de los que coinciden en el
    // tiempo con la misma ConvocatoriaId
    ConvocatoriaFase convocatoriaFase1 = generarConvocatoriaFase("-001");

    Long idConvocatoriaFase = 999999L;
    Long convocatoriaIdBuscado = convocatoriaFase1.getConvocatoriaId();
    Instant fechaInicioBuscada = Instant.parse("2020-10-11T00:00:00Z");
    Instant fechaFinBuscada = Instant.parse("2020-10-20T00:00:00Z");
    Long tipoFaseIdBuscado = convocatoriaFase1.getTipoFase().getId();

    // when: se busca si existen fechas solapadas
    Pageable pageable = PageRequest.of(0, 10);
    Specification<ConvocatoriaFase> specByConvocatoria = ConvocatoriaFaseSpecifications
        .byConvocatoriaId(convocatoriaIdBuscado);
    Specification<ConvocatoriaFase> specByTipoFase = ConvocatoriaFaseSpecifications.byTipoFaseId(tipoFaseIdBuscado);
    Specification<ConvocatoriaFase> specByIdNotEqual = ConvocatoriaFaseSpecifications.byIdNotEqual(idConvocatoriaFase);
    Specification<ConvocatoriaFase> spec = ConvocatoriaFaseSpecifications.byRangoFechaSolapados(fechaInicioBuscada,
        fechaFinBuscada);
    Specification<ConvocatoriaFase> specs = Specification.where(specByConvocatoria).and(spec).and(specByTipoFase)
        .and(specByIdNotEqual);

    List<ConvocatoriaFase> fases = repository.findAll(specs, pageable).getContent();

    // then: devuelve 1

    Assertions.assertThat(fases.get(0)).isEqualTo(convocatoriaFase1);

  }

  @Test
  void existsConvocatoriaFaseConFechasSolapadas_WithNingunaFechaSolapada_NoReturnsConvocatoriaFase()
      throws Exception {

    // given: 2 ConvocatoriaFase de los que coinciden en el
    // tiempo con la misma ConvocatoriaId
    ConvocatoriaFase convocatoriaFase1 = generarConvocatoriaFase("-001");

    Long idConvocatoriaFase = 988888888L;
    Long convocatoriaIdBuscado = convocatoriaFase1.getConvocatoriaId();
    Instant fechaInicioBuscada = Instant.parse("2020-11-11T00:00:00Z");
    Instant fechaFinBuscada = Instant.parse("2020-11-20T00:00:00Z");
    Long tipoFaseIdBuscado = convocatoriaFase1.getTipoFase().getId();

    // when: se busca si existen fechas solapadas
    Pageable pageable = PageRequest.of(0, 10);
    Specification<ConvocatoriaFase> specByConvocatoria = ConvocatoriaFaseSpecifications
        .byConvocatoriaId(convocatoriaIdBuscado);
    Specification<ConvocatoriaFase> specByTipoFase = ConvocatoriaFaseSpecifications.byTipoFaseId(tipoFaseIdBuscado);
    Specification<ConvocatoriaFase> specByIdNotEqual = ConvocatoriaFaseSpecifications.byIdNotEqual(idConvocatoriaFase);
    Specification<ConvocatoriaFase> spec = ConvocatoriaFaseSpecifications.byRangoFechaSolapados(fechaInicioBuscada,
        fechaFinBuscada);
    Specification<ConvocatoriaFase> specs = Specification.where(specByConvocatoria).and(spec).and(specByTipoFase)
        .and(specByIdNotEqual);

    List<ConvocatoriaFase> fases = repository.findAll(specs, pageable).getContent();

    // then: devuelve 1

    Assertions.assertThat(fases.isEmpty());

  }

  @Test
  void existsConvocatoriaFaseConFechasSolapadas_WithFechaInicioSolapada_ReturnsConvocatoriaFase()
      throws Exception {

    // given: 2 ConvocatoriaFase de los que coinciden en el
    // tiempo con la misma ConvocatoriaId
    ConvocatoriaFase convocatoriaFase1 = generarConvocatoriaFase("-001");

    Long idConvocatoriaFase = 87999999999L;
    Long convocatoriaIdBuscado = convocatoriaFase1.getConvocatoriaId();
    Instant fechaInicioBuscada = Instant.parse("2020-10-11T00:00:00Z");
    Instant fechaFinBuscada = Instant.parse("2020-11-20T00:00:00Z");
    Long tipoFaseIdBuscado = convocatoriaFase1.getTipoFase().getId();

    // when: se busca si existen fechas solapadas
    // EntidadRef
    Pageable pageable = PageRequest.of(0, 10);
    Specification<ConvocatoriaFase> specByConvocatoria = ConvocatoriaFaseSpecifications
        .byConvocatoriaId(convocatoriaIdBuscado);
    Specification<ConvocatoriaFase> specByTipoFase = ConvocatoriaFaseSpecifications.byTipoFaseId(tipoFaseIdBuscado);
    Specification<ConvocatoriaFase> specByIdNotEqual = ConvocatoriaFaseSpecifications.byIdNotEqual(idConvocatoriaFase);
    Specification<ConvocatoriaFase> spec = ConvocatoriaFaseSpecifications.byRangoFechaSolapados(fechaInicioBuscada,
        fechaFinBuscada);
    Specification<ConvocatoriaFase> specs = Specification.where(specByConvocatoria).and(spec).and(specByTipoFase)
        .and(specByIdNotEqual);

    List<ConvocatoriaFase> fases = repository.findAll(specs, pageable).getContent();

    // then: devuelve 1

    Assertions.assertThat(fases.get(0)).isEqualTo(convocatoriaFase1);
  }

  @Test
  void existsConvocatoriaFaseConFechasSolapadas_WithFechaFinSolapada_ReturnsConvocatoriaFase() throws Exception {

    // given: 2 ConvocatoriaFase de los que coinciden en el
    // tiempo con la misma ConvocatoriaId

    ConvocatoriaFase convocatoriaFase1 = generarConvocatoriaFase("-001");

    Long idConvocatoriaFase = 200000000L;
    Long convocatoriaIdBuscado = convocatoriaFase1.getConvocatoriaId();
    Instant fechaInicioBuscada = Instant.parse("2020-09-11T00:00:00Z");
    Instant fechaFinBuscada = Instant.parse("2020-10-20T00:00:00Z");
    Long tipoFaseIdBuscado = convocatoriaFase1.getTipoFase().getId();

    // when: se busca si existen fechas solapadas
    // EntidadRef
    Pageable pageable = PageRequest.of(0, 10);
    Specification<ConvocatoriaFase> specByConvocatoria = ConvocatoriaFaseSpecifications
        .byConvocatoriaId(convocatoriaIdBuscado);
    Specification<ConvocatoriaFase> specByTipoFase = ConvocatoriaFaseSpecifications.byTipoFaseId(tipoFaseIdBuscado);
    Specification<ConvocatoriaFase> specByIdNotEqual = ConvocatoriaFaseSpecifications.byIdNotEqual(idConvocatoriaFase);
    Specification<ConvocatoriaFase> spec = ConvocatoriaFaseSpecifications.byRangoFechaSolapados(fechaInicioBuscada,
        fechaFinBuscada);
    Specification<ConvocatoriaFase> specs = Specification.where(specByConvocatoria).and(spec).and(specByTipoFase)
        .and(specByIdNotEqual);

    List<ConvocatoriaFase> fases = repository.findAll(specs, pageable).getContent();

    // then: devuelve 1

    Assertions.assertThat(fases.get(0)).isEqualTo(convocatoriaFase1);

  }

  @Test
  void existsConvocatoriaFaseConFechasSolapadas_WithFechasInicioYFinSolapada_ReturnsConvocatoriaFase()
      throws Exception {

    // given: 2 ConvocatoriaFase de los que coinciden en el
    // tiempo con la misma ConvocatoriaId

    ConvocatoriaFase convocatoriaFase1 = generarConvocatoriaFase("-001");

    Long idConvocatoriaFase = null;
    Long convocatoriaIdBuscado = convocatoriaFase1.getConvocatoriaId();
    Instant fechaInicioBuscada = Instant.parse("2020-09-11T00:00:00Z");
    Instant fechaFinBuscada = Instant.parse("2020-11-20T00:00:00Z");
    Long tipoFaseIdBuscado = convocatoriaFase1.getTipoFase().getId();

    // when: se busca si existen fechas solapadas
    // EntidadRef
    Pageable pageable = PageRequest.of(0, 10);
    Specification<ConvocatoriaFase> specByConvocatoria = ConvocatoriaFaseSpecifications
        .byConvocatoriaId(convocatoriaIdBuscado);
    Specification<ConvocatoriaFase> specByTipoFase = ConvocatoriaFaseSpecifications.byTipoFaseId(tipoFaseIdBuscado);
    Specification<ConvocatoriaFase> specByIdNotEqual = ConvocatoriaFaseSpecifications.byIdNotEqual(idConvocatoriaFase);
    Specification<ConvocatoriaFase> spec = ConvocatoriaFaseSpecifications.byRangoFechaSolapados(fechaInicioBuscada,
        fechaFinBuscada);
    Specification<ConvocatoriaFase> specs = Specification.where(specByConvocatoria).and(spec).and(specByTipoFase)
        .and(specByIdNotEqual);

    List<ConvocatoriaFase> fases = repository.findAll(specs, pageable).getContent();

    // then: devuelve 1

    Assertions.assertThat(fases.get(0)).isEqualTo(convocatoriaFase1);

  }

  /**
   * Función que genera ConvocatoriaFase
   * 
   * @param suffix
   * @return el objeto ConvocatoriaFase
   */
  private ConvocatoriaFase generarConvocatoriaFase(String suffix) {
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
    entityManager.persistAndFlush(convocatoria);

    Set<TipoFaseNombre> nombreTipoFase = new HashSet<>();
    nombreTipoFase.add(new TipoFaseNombre(Language.ES, "nombreTipoEnlace" + suffix));

    TipoFase tipoFase = TipoFase.builder()
        .nombre(nombreTipoFase)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(tipoFase);

    Set<ConvocatoriaFaseObservaciones> obsConvocatoriaFase = new HashSet<>();
    obsConvocatoriaFase.add(new ConvocatoriaFaseObservaciones(Language.ES, "observaciones" + suffix));

    ConvocatoriaFase convocatoriaFase = ConvocatoriaFase.builder()
        .convocatoriaId(convocatoria.getId())
        .tipoFase(tipoFase)
        .fechaInicio(Instant.parse("2020-10-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-10-31T00:00:00Z"))
        .observaciones(obsConvocatoriaFase)
        .build();
    return entityManager.persistAndFlush(convocatoriaFase);
  }

}
