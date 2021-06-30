package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ModeloTipoHitoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ModeloTipoHitoRepository repository;

  @Test
  public void findByModeloEjecucionIdAndTipoHitoId_ReturnsModeloTipoHito() throws Exception {

    // given: data ModeloTipoHito to find by ModeloEjecucion and TipoHito
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion(null, "nombre-me-1", "descripcion-me-1", Boolean.TRUE);
    entityManager.persistAndFlush(modeloEjecucion);
    TipoHito tipoHito = new TipoHito(null, "nombre-tf-1", "descripcion-tf-1", Boolean.TRUE);
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
  public void findByModeloEjecucionIdAndTipoHitoId_ReturnsNull() throws Exception {
    // given: data ModeloTipoHito to find by ModeloEjecucion and TipoHito
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion(null, "nombre-me-1", "descripcion-me-1", Boolean.TRUE);
    entityManager.persistAndFlush(modeloEjecucion);
    TipoHito tipoHito = new TipoHito(null, "nombre-tf-1", "descripcion-tf-1", Boolean.TRUE);
    entityManager.persistAndFlush(tipoHito);

    // when: find by ModeloEjecucion and TipoHito
    Optional<ModeloTipoHito> dataFound = repository.findByModeloEjecucionIdAndTipoHitoId(modeloEjecucion.getId(),
        tipoHito.getId());

    // then: ModeloTipoHito is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }
}
