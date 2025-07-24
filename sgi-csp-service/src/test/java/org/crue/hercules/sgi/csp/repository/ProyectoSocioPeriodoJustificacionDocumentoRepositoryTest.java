package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionDescripcion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumentoNombre;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionObservaciones;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.RolSocioAbreviatura;
import org.crue.hercules.sgi.csp.model.RolSocioDescripcion;
import org.crue.hercules.sgi.csp.model.RolSocioNombre;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumentoNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProyectoSocioPeriodoJustificacionDocumentoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoSocioPeriodoJustificacionDocumentoRepository repository;

  @Test
  void findAllByProyectoSocioPeriodoJustificacionId_ReturnsSocioPeriodoJustificacionDocuemnto() {

    // given: 1 ProyectoSocioPeriodoJustificacionDocumento para el
    // ProyectoSocioPeriodoJustificacionId buscado
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombre-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion1 = new HashSet<>();
    descripcionModeloEjecucion1.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-1"));
    ModeloEjecucion modeloEjecucion = entityManager
        .persistAndFlush(
            new ModeloEjecucion(null, nombreModeloEjecucion, descripcionModeloEjecucion1, true, false, false, false));

    Set<RolSocioAbreviatura> rolSocioAbreviatura = new HashSet<>();
    rolSocioAbreviatura.add(new RolSocioAbreviatura(Language.ES, "001"));

    Set<RolSocioNombre> rolSocioNombre = new HashSet<>();
    rolSocioNombre.add(new RolSocioNombre(Language.ES, "nombre-001"));

    Set<RolSocioDescripcion> rolSocioDescripcion = new HashSet<>();
    rolSocioDescripcion.add(new RolSocioDescripcion(Language.ES, "descripcion-001"));

    Set<TipoDocumentoNombre> nombreTipoDocumento = new HashSet<>();
    nombreTipoDocumento.add(new TipoDocumentoNombre(Language.ES, "tipo1"));

    RolSocio rolSocio = entityManager.persistAndFlush(RolSocio.builder()
        .abreviatura(rolSocioAbreviatura)
        .nombre(rolSocioNombre)
        .descripcion(rolSocioDescripcion)
        .coordinador(Boolean.FALSE)
        .activo(Boolean.TRUE)
        .build());

    Set<ProyectoTitulo> tituloProyecto = new HashSet<>();
    tituloProyecto.add(new ProyectoTitulo(Language.ES, "proyecto"));

    Proyecto proyecto = entityManager.persistAndFlush(Proyecto.builder()
        .titulo(tituloProyecto)
        .fechaInicio(Instant.parse("2020-09-18T00:00:00Z"))
        .fechaFin(Instant.parse("2022-10-11T23:59:59Z"))
        .unidadGestionRef("2")
        .modeloEjecucion(modeloEjecucion)
        .activo(Boolean.TRUE)
        .build());

    ProyectoSocio proyectoSocio1 = entityManager.persistAndFlush(ProyectoSocio.builder()
        .proyectoId(proyecto.getId())
        .empresaRef("codigo-1")
        .rolSocio(rolSocio)
        .build());
    Set<ProyectoSocioPeriodoJustificacionObservaciones> observaciones1 = new HashSet<>();
    observaciones1.add(new ProyectoSocioPeriodoJustificacionObservaciones(Language.ES, "observaciones1"));
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion1 = entityManager
        .persistAndFlush(new ProyectoSocioPeriodoJustificacion(null, proyectoSocio1.getId(), 1,
            Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"),
            Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"),
            observaciones1, Boolean.TRUE, Instant.parse("2020-11-20T00:00:00Z"), null));
    Set<ProyectoSocioPeriodoJustificacionObservaciones> observaciones2 = new HashSet<>();
    observaciones2.add(new ProyectoSocioPeriodoJustificacionObservaciones(Language.ES, "observaciones2"));
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion2 = entityManager
        .persistAndFlush(new ProyectoSocioPeriodoJustificacion(null, proyectoSocio1.getId(), 1,
            Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"),
            Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"),
            observaciones2, Boolean.TRUE, Instant.parse("2020-11-20T00:00:00Z"), null));

    TipoDocumento tipoDocumento = entityManager
        .persistAndFlush(TipoDocumento.builder().nombre(nombreTipoDocumento).activo(Boolean.TRUE).build());

    Set<ProyectoSocioPeriodoJustificacionDocumentoNombre> nombreDocumento1 = new HashSet<>();
    nombreDocumento1.add(new ProyectoSocioPeriodoJustificacionDocumentoNombre(Language.ES, "doc1"));

    ProyectoSocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacionDocumento1 = entityManager
        .persistAndFlush(
            ProyectoSocioPeriodoJustificacionDocumento.builder().nombre(nombreDocumento1).documentoRef("001")
                .proyectoSocioPeriodoJustificacionId(proyectoSocioPeriodoJustificacion1.getId())
                .tipoDocumento(tipoDocumento).visible(Boolean.TRUE).build());

    Set<ProyectoSocioPeriodoJustificacionDocumentoNombre> nombreDocumento2 = new HashSet<>();
    nombreDocumento2.add(new ProyectoSocioPeriodoJustificacionDocumentoNombre(Language.ES, "doc2"));

    entityManager.persistAndFlush(ProyectoSocioPeriodoJustificacionDocumento.builder().nombre(nombreDocumento2)
        .documentoRef("002").proyectoSocioPeriodoJustificacionId(proyectoSocioPeriodoJustificacion2.getId())
        .tipoDocumento(tipoDocumento).visible(Boolean.TRUE).build());

    Long proyectoSocioPeriodoJustificacionIdBuscado = proyectoSocioPeriodoJustificacion1.getId();

    // when: se buscan los ProyectoSocioPeriodoJustificacionDocumento por
    // ProyectoSocioPeriodoJustificacionId
    List<ProyectoSocioPeriodoJustificacionDocumento> dataFound = repository
        .findAllByProyectoSocioPeriodoJustificacionId(proyectoSocioPeriodoJustificacionIdBuscado);

    // then: Se recuperan los ProyectoSocioPeriodoJustificacion con el
    // ProyectoSocioId
    // buscado
    Assertions.assertThat(dataFound).hasSize(1);
    Assertions.assertThat(dataFound.get(0).getId()).isEqualTo(proyectoSocioPeriodoJustificacionDocumento1.getId());
    Assertions.assertThat(dataFound.get(0).getProyectoSocioPeriodoJustificacionId())
        .isEqualTo(proyectoSocioPeriodoJustificacionDocumento1.getProyectoSocioPeriodoJustificacionId());
    Assertions.assertThat(dataFound.get(0).getNombre())
        .isEqualTo(proyectoSocioPeriodoJustificacionDocumento1.getNombre());
  }

}
