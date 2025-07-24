package org.crue.hercules.sgi.csp.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionDescripcion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.model.TipoHitoDescripcion;
import org.crue.hercules.sgi.csp.model.TipoHitoNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ModeloTipoHitoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ModeloTipoHitoRepository repository;

  @Test
  void findByModeloEjecucionIdAndTipoHitoId_ReturnsModeloTipoHito() throws Exception {

    // given: data ModeloTipoHito to find by ModeloEjecucion and TipoHito
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombre-me-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion1 = new HashSet<>();
    descripcionModeloEjecucion1.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-me-1"));
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion(null, nombreModeloEjecucion, descripcionModeloEjecucion1,
        Boolean.TRUE,
        Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
    entityManager.persistAndFlush(modeloEjecucion);

    Set<TipoHitoNombre> nombreTipoHito = new HashSet<>();
    nombreTipoHito.add(new TipoHitoNombre(Language.ES, "nombre-tf-1"));

    Set<TipoHitoDescripcion> descripcionTipoHito = new HashSet<>();
    descripcionTipoHito.add(new TipoHitoDescripcion(Language.ES, "descripcion-tf-1"));

    TipoHito tipoHito = new TipoHito(null, nombreTipoHito, descripcionTipoHito, Boolean.TRUE);
    entityManager.persistAndFlush(tipoHito);
    ModeloTipoHito modeloTipoHito = new ModeloTipoHito(null, tipoHito, modeloEjecucion, Boolean.TRUE, Boolean.TRUE,
        Boolean.TRUE, Boolean.TRUE);
    entityManager.persistAndFlush(modeloTipoHito);

    // when: find by ModeloEjecucion and TipoHito
    ModeloTipoHito dataFound = repository
        .findByModeloEjecucionIdAndTipoHitoId(modeloEjecucion.getId(), tipoHito.getId()).get();

    // then: ModeloTipoHito is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(modeloTipoHito.getId());
    Assertions.assertThat(dataFound.getModeloEjecucion().getId()).isEqualTo(modeloEjecucion.getId());
    Assertions.assertThat(dataFound.getTipoHito().getId()).isEqualTo(tipoHito.getId());
    Assertions.assertThat(dataFound.getSolicitud()).isEqualTo(modeloTipoHito.getSolicitud());
    Assertions.assertThat(dataFound.getProyecto()).isEqualTo(modeloTipoHito.getProyecto());
    Assertions.assertThat(dataFound.getConvocatoria()).isEqualTo(modeloTipoHito.getConvocatoria());
    Assertions.assertThat(dataFound.getActivo()).isEqualTo(modeloTipoHito.getActivo());
  }

  @Test
  void findByModeloEjecucionIdAndTipoHitoId_ReturnsNull() throws Exception {
    // given: data ModeloTipoHito to find by ModeloEjecucion and TipoHito
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombre-me-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion1 = new HashSet<>();
    descripcionModeloEjecucion1.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-me-1"));
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion(null, nombreModeloEjecucion, descripcionModeloEjecucion1,
        Boolean.TRUE,
        Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
    entityManager.persistAndFlush(modeloEjecucion);

    Set<TipoHitoNombre> nombreTipoHito = new HashSet<>();
    nombreTipoHito.add(new TipoHitoNombre(Language.ES, "nombre-tf-1"));

    Set<TipoHitoDescripcion> descripcionTipoHito = new HashSet<>();
    descripcionTipoHito.add(new TipoHitoDescripcion(Language.ES, "descripcion-tf-1"));

    TipoHito tipoHito = new TipoHito(null, nombreTipoHito, descripcionTipoHito, Boolean.TRUE);
    entityManager.persistAndFlush(tipoHito);

    // when: find by ModeloEjecucion and TipoHito
    Optional<ModeloTipoHito> dataFound = repository.findByModeloEjecucionIdAndTipoHitoId(modeloEjecucion.getId(),
        tipoHito.getId());

    // then: ModeloTipoHito is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }
}
