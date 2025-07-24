package org.crue.hercules.sgi.csp.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionDescripcion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlaceDescripcion;
import org.crue.hercules.sgi.csp.model.TipoEnlaceNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ModeloTipoEnlaceRepositoryTest
 */
@DataJpaTest
class ModeloTipoEnlaceRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ModeloTipoEnlaceRepository repository;

  @Test
  void findByModeloEjecucionIdAndTipoEnlaceId_ReturnsModeloTipoEnlace() throws Exception {

    // given: 2 ModeloTipoEnlace de los que 1 coincide con los ids de
    // ModeloEjecucion y TipoEnlace
    Set<ModeloEjecucionNombre> nombreModeloEjecucion1 = new HashSet<>();
    nombreModeloEjecucion1.add(new ModeloEjecucionNombre(Language.ES, "nombre-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion1 = new HashSet<>();
    descripcionModeloEjecucion1.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-1"));
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, nombreModeloEjecucion1, descripcionModeloEjecucion1,
        true, false,
        false,
        false);
    entityManager.persistAndFlush(modeloEjecucion1);

    Set<ModeloEjecucionNombre> nombreModeloEjecucion2 = new HashSet<>();
    nombreModeloEjecucion2.add(new ModeloEjecucionNombre(Language.ES, "nombre-2"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion2 = new HashSet<>();
    descripcionModeloEjecucion2.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-2"));
    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, nombreModeloEjecucion2, descripcionModeloEjecucion2,
        true, false,
        false,
        false);
    entityManager.persistAndFlush(modeloEjecucion2);

    Set<TipoEnlaceNombre> nombre1 = new HashSet<>();
    nombre1.add(new TipoEnlaceNombre(Language.ES, "nombre-1"));

    Set<TipoEnlaceDescripcion> descripcion1 = new HashSet<>();
    descripcion1.add(new TipoEnlaceDescripcion(Language.ES, "descripcion-1"));

    TipoEnlace tipoEnlace1 = new TipoEnlace(null, nombre1, descripcion1, true);
    entityManager.persistAndFlush(tipoEnlace1);

    Set<TipoEnlaceNombre> nombre2 = new HashSet<>();
    nombre2.add(new TipoEnlaceNombre(Language.ES, "nombre-2"));

    Set<TipoEnlaceDescripcion> descripcion2 = new HashSet<>();
    descripcion2.add(new TipoEnlaceDescripcion(Language.ES, "descripcion-2"));

    TipoEnlace tipoEnlace2 = new TipoEnlace(null, nombre2, descripcion2, true);
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
  void findByModeloEjecucionIdAndTipoEnlaceId_NoExiste_ReturnsNull() throws Exception {

    // given: 2 ModeloEjecucion que no coinciden con los ids de
    // ModeloEjecucion y TipoEnlace
    Set<ModeloEjecucionNombre> nombreModeloEjecucion1 = new HashSet<>();
    nombreModeloEjecucion1.add(new ModeloEjecucionNombre(Language.ES, "nombre-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion1 = new HashSet<>();
    descripcionModeloEjecucion1.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-1"));
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, nombreModeloEjecucion1, descripcionModeloEjecucion1,
        true, false,
        false,
        false);
    entityManager.persistAndFlush(modeloEjecucion1);

    Set<ModeloEjecucionNombre> nombreModeloEjecucion2 = new HashSet<>();
    nombreModeloEjecucion2.add(new ModeloEjecucionNombre(Language.ES, "nombre-2"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion2 = new HashSet<>();
    descripcionModeloEjecucion2.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-2"));
    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, nombreModeloEjecucion2, descripcionModeloEjecucion2,
        true, false,
        false,
        false);
    entityManager.persistAndFlush(modeloEjecucion2);

    Set<TipoEnlaceNombre> nombre1 = new HashSet<>();
    nombre1.add(new TipoEnlaceNombre(Language.ES, "nombre-1"));

    Set<TipoEnlaceDescripcion> descripcion1 = new HashSet<>();
    descripcion1.add(new TipoEnlaceDescripcion(Language.ES, "descripcion-1"));

    TipoEnlace tipoEnlace1 = new TipoEnlace(null, nombre1, descripcion1, true);
    entityManager.persistAndFlush(tipoEnlace1);

    Set<TipoEnlaceNombre> nombre2 = new HashSet<>();
    nombre2.add(new TipoEnlaceNombre(Language.ES, "nombre-2"));

    Set<TipoEnlaceDescripcion> descripcion2 = new HashSet<>();
    descripcion2.add(new TipoEnlaceDescripcion(Language.ES, "descripcion-2"));

    TipoEnlace tipoEnlace2 = new TipoEnlace(null, nombre2, descripcion2, true);
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