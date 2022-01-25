package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProyectoSocioPeriodoJustificacionDocumentoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoSocioPeriodoJustificacionDocumentoRepository repository;

  @Test
  public void findAllByProyectoSocioPeriodoJustificacionId_ReturnsSocioPeriodoJustificacionDocuemnto()
      throws Exception {

    // given: 1 ProyectoSocioPeriodoJustificacionDocumento para el
    // ProyectoSocioPeriodoJustificacionId buscado
    ModeloEjecucion modeloEjecucion = entityManager
        .persistAndFlush(new ModeloEjecucion(null, "nombre-1", "descripcion-1", true, false, false));

    // @formatter:off
    RolSocio rolSocio = entityManager.persistAndFlush(RolSocio.builder()
        .abreviatura("001")
        .nombre("nombre-001")
        .descripcion("descripcion-001")
        .coordinador(Boolean.FALSE)
        .activo(Boolean.TRUE)
        .build());

    Proyecto proyecto = entityManager.persistAndFlush(Proyecto.builder()
        .titulo("proyecto")
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
    // @formatter:on

    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion1 = entityManager
        .persistAndFlush(new ProyectoSocioPeriodoJustificacion(null, proyectoSocio1.getId(), 1,
            Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"),
            Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"), "observaciones-1",
            Boolean.TRUE, Instant.parse("2020-11-20T00:00:00Z"), null));
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion2 = entityManager
        .persistAndFlush(new ProyectoSocioPeriodoJustificacion(null, proyectoSocio1.getId(), 1,
            Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"),
            Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"), "observaciones-2",
            Boolean.TRUE, Instant.parse("2020-11-20T00:00:00Z"), null));

    TipoDocumento tipoDocumento = entityManager
        .persistAndFlush(TipoDocumento.builder().nombre("tipo1").activo(Boolean.TRUE).build());

    ProyectoSocioPeriodoJustificacionDocumento proyectoSocioPeriodoJustificacionDocumento1 = entityManager
        .persistAndFlush(ProyectoSocioPeriodoJustificacionDocumento.builder().nombre("doc1").documentoRef("001")
            .proyectoSocioPeriodoJustificacionId(proyectoSocioPeriodoJustificacion1.getId())
            .tipoDocumento(tipoDocumento).visible(Boolean.TRUE).build());

    entityManager.persistAndFlush(ProyectoSocioPeriodoJustificacionDocumento.builder().nombre("doc2")
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
    Assertions.assertThat(dataFound.size()).isEqualTo(1);
    Assertions.assertThat(dataFound.get(0).getId()).isEqualTo(proyectoSocioPeriodoJustificacionDocumento1.getId());
    Assertions.assertThat(dataFound.get(0).getProyectoSocioPeriodoJustificacionId())
        .isEqualTo(proyectoSocioPeriodoJustificacionDocumento1.getProyectoSocioPeriodoJustificacionId());
    Assertions.assertThat(dataFound.get(0).getNombre())
        .isEqualTo(proyectoSocioPeriodoJustificacionDocumento1.getNombre());
  }

}
