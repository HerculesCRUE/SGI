package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ModeloTipoEnlaceRepositoryTest
 */
@DataJpaTest
public class ModeloTipoEnlaceRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ModeloTipoEnlaceRepository repository;

  @Test
  public void findByModeloEjecucionIdAndTipoEnlaceId_ReturnsModeloTipoEnlace() throws Exception {

    // given: 2 ModeloTipoEnlace de los que 1 coincide con los ids de
    // ModeloEjecucion y TipoEnlace
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(modeloEjecucion1);

    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(modeloEjecucion2);

    TipoEnlace tipoEnlace1 = new TipoEnlace(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(tipoEnlace1);

    TipoEnlace tipoEnlace2 = new TipoEnlace(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(tipoEnlace2);

    ModeloTipoEnlace modeloTipoEnlace1 = new ModeloTipoEnlace(null, tipoEnlace1, modeloEjecucion1, true);
    entityManager.persistAndFlush(modeloTipoEnlace1);

    ModeloTipoEnlace modeloTipoEnlace2 = new ModeloTipoEnlace(null, tipoEnlace2, modeloEjecucion2, true);
    entityManager.persistAndFlush(modeloTipoEnlace2);

    Long idModeloEjecucionBuscado = modeloEjecucion1.getId();
    Long idTipoEnlaceBuscado = tipoEnlace1.getId();

    // when: se busca el ModeloTipoEnlace
    ModeloTipoEnlace modeloTipoEnlaceEncontrado = repository
        .findByModeloEjecucionIdAndTipoEnlaceId(idModeloEjecucionBuscado, idTipoEnlaceBuscado).get();

    // then: Se recupera el ModeloTipoEnlace buscado
    Assertions.assertThat(modeloTipoEnlaceEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceEncontrado.getModeloEjecucion().getNombre())
        .as("getModeloEjecucion().getNombre()").isEqualTo(modeloEjecucion1.getNombre());
    Assertions.assertThat(modeloTipoEnlaceEncontrado.getTipoEnlace().getNombre()).as("getTipoEnlace().getNombre()")
        .isEqualTo(tipoEnlace1.getNombre());
    Assertions.assertThat(modeloTipoEnlaceEncontrado.getActivo()).as("getActivo()")
        .isEqualTo(modeloEjecucion1.getActivo());
  }

  @Test
  public void findByModeloEjecucionIdAndTipoEnlaceId_NoExiste_ReturnsNull() throws Exception {

    // given: 2 ModeloEjecucion que no coinciden con los ids de
    // ModeloEjecucion y TipoEnlace
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(modeloEjecucion1);

    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(modeloEjecucion2);

    TipoEnlace tipoEnlace1 = new TipoEnlace(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(tipoEnlace1);

    TipoEnlace tipoEnlace2 = new TipoEnlace(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(tipoEnlace2);

    ModeloTipoEnlace modeloTipoEnlace1 = new ModeloTipoEnlace(null, tipoEnlace1, modeloEjecucion1, true);
    entityManager.persistAndFlush(modeloTipoEnlace1);

    ModeloTipoEnlace modeloTipoEnlace2 = new ModeloTipoEnlace(null, tipoEnlace2, modeloEjecucion2, true);
    entityManager.persistAndFlush(modeloTipoEnlace2);

    Long idModeloEjecucionBuscado = modeloEjecucion2.getId();
    Long idTipoEnlaceBuscado = tipoEnlace1.getId();

    // when: se busca el ModeloTipoEnlace
    Optional<ModeloTipoEnlace> modeloTipoEnlaceEncontrado = repository
        .findByModeloEjecucionIdAndTipoEnlaceId(idModeloEjecucionBuscado, idTipoEnlaceBuscado);

    // then: No se recupera el ModeloTipoEnlace buscado
    Assertions.assertThat(modeloTipoEnlaceEncontrado).isEqualTo(Optional.empty());
  }

}