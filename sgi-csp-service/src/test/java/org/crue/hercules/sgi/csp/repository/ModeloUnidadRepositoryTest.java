package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ModeloUnidadRepositoryTest
 */
@DataJpaTest
public class ModeloUnidadRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ModeloUnidadRepository repository;

  @Test
  public void findByModeloEjecucionIdAndUnidadGestionRef_ReturnsModeloUnidad() throws Exception {
    // given: 2 ModeloUnidad de los que 1 coincide con los id de
    // ModeloEjecucion y UnidadGestionRef
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(modeloEjecucion1);

    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(modeloEjecucion2);

    ModeloUnidad modeloUnidad1 = new ModeloUnidad(null, "unidad-1", modeloEjecucion1, true);
    entityManager.persistAndFlush(modeloUnidad1);

    ModeloUnidad modeloUnidad2 = new ModeloUnidad(null, "unidad-2", modeloEjecucion2, true);
    entityManager.persistAndFlush(modeloUnidad2);

    Long idModeloEjecucionBuscado = modeloEjecucion1.getId();
    String unidadGestionRefBuscado = "unidad-1";

    // when: se busca el ModeloTipoEnlace
    ModeloUnidad modeloTipoEnlaceEncontrado = repository
        .findByModeloEjecucionIdAndUnidadGestionRef(idModeloEjecucionBuscado, unidadGestionRefBuscado).get();

    // then: Se recupera el ModeloTipoEnlace buscado
    Assertions.assertThat(modeloTipoEnlaceEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceEncontrado.getModeloEjecucion().getNombre())
        .as("getModeloEjecucion().getNombre()").isEqualTo(modeloEjecucion1.getNombre());
    Assertions.assertThat(modeloTipoEnlaceEncontrado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(modeloUnidad1.getUnidadGestionRef());
    Assertions.assertThat(modeloTipoEnlaceEncontrado.getActivo()).as("getActivo()")
        .isEqualTo(modeloEjecucion1.getActivo());
  }

  @Test
  public void findByModeloEjecucionIdAndUnidadGestionRef_NoExiste_ReturnsNull() throws Exception {
    // given: 2 ModeloUnidad de los que ninguno coincide con los id de
    // ModeloEjecucion y UnidadGestionRef
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(modeloEjecucion1);

    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(modeloEjecucion2);

    ModeloUnidad modeloUnidad1 = new ModeloUnidad(null, "unidad-1", modeloEjecucion1, true);
    entityManager.persistAndFlush(modeloUnidad1);

    ModeloUnidad modeloUnidad2 = new ModeloUnidad(null, "unidad-2", modeloEjecucion2, true);
    entityManager.persistAndFlush(modeloUnidad2);

    Long idModeloEjecucionBuscado = modeloEjecucion1.getId();
    String unidadGestionRefBuscado = "unidad-2";

    // when: se busca el ModeloTipoEnlace
    Optional<ModeloUnidad> modeloTipoEnlaceEncontrado = repository
        .findByModeloEjecucionIdAndUnidadGestionRef(idModeloEjecucionBuscado, unidadGestionRefBuscado);

    // then: No se recupera el ModeloTipoEnlace buscado
    Assertions.assertThat(modeloTipoEnlaceEncontrado).isEqualTo(Optional.empty());
  }

}