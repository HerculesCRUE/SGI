package org.crue.hercules.sgi.csp.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionDescripcion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ModeloEjecucionRepositoryTest
 */
@DataJpaTest
class ModeloEjecucionRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ModeloEjecucionRepository repository;

  @Test
  void findByNombreAndActivoIsTrue_ReturnsModeloEjecucion() throws Exception {

    // given: 2 ModeloEjecucion de los que 1 coincide con el nombre buscado
    Set<ModeloEjecucionNombre> nombreModeloEjecucion1 = new HashSet<>();
    nombreModeloEjecucion1.add(new ModeloEjecucionNombre(Language.ES, "nombre-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion1 = new HashSet<>();
    descripcionModeloEjecucion1.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-1"));
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, nombreModeloEjecucion1, descripcionModeloEjecucion1,
        true, false,
        false, false);
    entityManager.persistAndFlush(modeloEjecucion1);

    Set<ModeloEjecucionNombre> nombreModeloEjecucion2 = new HashSet<>();
    nombreModeloEjecucion2.add(new ModeloEjecucionNombre(Language.ES, "nombre-2"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion2 = new HashSet<>();
    descripcionModeloEjecucion2.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-2"));
    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, nombreModeloEjecucion2, descripcionModeloEjecucion2,
        true, false,
        false, false);
    entityManager.persistAndFlush(modeloEjecucion2);

    Set<ModeloEjecucionNombre> nombreModeloEjecucion3 = new HashSet<>();
    nombreModeloEjecucion3.add(new ModeloEjecucionNombre(Language.ES, "nombre-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion3 = new HashSet<>();
    descripcionModeloEjecucion3.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-1"));
    ModeloEjecucion modeloEjecucion3 = new ModeloEjecucion(null, nombreModeloEjecucion3, descripcionModeloEjecucion3,
        false, false,
        false, false);
    entityManager.persistAndFlush(modeloEjecucion3);

    String nombreBuscado = "nombre-1";

    // when: se busca el ModeloEjecucion por nombre
    ModeloEjecucion modeloEjecucionEncontrado = repository
        .findByNombreLangAndNombreValueAndActivoIsTrue(Language.ES, nombreBuscado).get();

    // then: Se recupera el ModeloEjecucion con el nombre buscado
    Assertions.assertThat(modeloEjecucionEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(modeloEjecucionEncontrado.getNombre()).as("getNombre")
        .isEqualTo(modeloEjecucion1.getNombre());
    Assertions.assertThat(modeloEjecucionEncontrado.getDescripcion()).as("getDescripcion")
        .isEqualTo(modeloEjecucion1.getDescripcion());
    Assertions.assertThat(modeloEjecucionEncontrado.getActivo()).as("getActivo").isEqualTo(Boolean.TRUE);
  }

  @Test
  void findByNombreAndActivoIsTrueNoExiste_ReturnsNull() throws Exception {

    // given: 2 ModeloEjecucion que no coinciden con el nombre buscado
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

    String nombreBuscado = "nombre-noexiste";

    // when: se busca el ModeloEjecucion por nombre
    Optional<ModeloEjecucion> modeloEjecucionEncontrado = repository
        .findByNombreLangAndNombreValueAndActivoIsTrue(Language.ES, nombreBuscado);

    // then: Se recupera el TipoDocumento con el nombre buscado
    Assertions.assertThat(modeloEjecucionEncontrado).isEqualTo(Optional.empty());
  }

}