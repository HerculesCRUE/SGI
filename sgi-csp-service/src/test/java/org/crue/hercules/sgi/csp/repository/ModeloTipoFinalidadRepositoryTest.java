package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ModeloTipoFinalidadRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ModeloTipoFinalidadRepository repository;

  @Test
  public void findByModeloEjecucionIdAndTipoFinalidadId_ReturnsModeloTipoFinalidad() throws Exception {

    // given: data ModeloTipoFinalidad to find by ModeloEjecucion and TipoFinalidad
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion(null, "nombre-me-1", "descripcion-me-1", Boolean.TRUE);
    entityManager.persistAndFlush(modeloEjecucion);
    TipoFinalidad tipoFinalidad = new TipoFinalidad(null, "nombre-tf-1", "descripcion-tf-1", Boolean.TRUE);
    entityManager.persistAndFlush(tipoFinalidad);
    ModeloTipoFinalidad modeloTipoFinalidad = new ModeloTipoFinalidad(null, tipoFinalidad, modeloEjecucion,
        Boolean.TRUE);
    entityManager.persistAndFlush(modeloTipoFinalidad);

    // when: find by ModeloEjecucion and TipoFinalidad
    ModeloTipoFinalidad dataFound = repository
        .findByModeloEjecucionIdAndTipoFinalidadId(modeloEjecucion.getId(), tipoFinalidad.getId()).get();

    // then: ModeloTipoFinalidad is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(modeloTipoFinalidad.getId());
    Assertions.assertThat(dataFound.getModeloEjecucion().getId()).isEqualTo(modeloEjecucion.getId());
    Assertions.assertThat(dataFound.getTipoFinalidad().getId()).isEqualTo(tipoFinalidad.getId());
    Assertions.assertThat(dataFound.getActivo()).isEqualTo(modeloTipoFinalidad.getActivo());
  }

  @Test
  public void findByModeloEjecucionIdAndTipoFinalidadId_ReturnsNull() throws Exception {
    // given: data ModeloTipoFinalidad to find by ModeloEjecucion and TipoFinalidad
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion(null, "nombre-me-1", "descripcion-me-1", Boolean.TRUE);
    entityManager.persistAndFlush(modeloEjecucion);
    TipoFinalidad tipoFinalidad = new TipoFinalidad(null, "nombre-tf-1", "descripcion-tf-1", Boolean.TRUE);
    entityManager.persistAndFlush(tipoFinalidad);

    // when: find by ModeloEjecucion and TipoFinalidad
    Optional<ModeloTipoFinalidad> dataFound = repository
        .findByModeloEjecucionIdAndTipoFinalidadId(modeloEjecucion.getId(), tipoFinalidad.getId());

    // then: ModeloTipoFinalidad is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }
}
