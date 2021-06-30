package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ModeloEjecucionRepositoryTest
 */
@DataJpaTest
public class ModeloEjecucionRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ModeloEjecucionRepository repository;

  @Test
  public void findByNombreAndActivoIsTrue_ReturnsModeloEjecucion() throws Exception {

    // given: 2 ModeloEjecucion de los que 1 coincide con el nombre buscado
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(modeloEjecucion1);

    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(modeloEjecucion2);

    ModeloEjecucion modeloEjecucion3 = new ModeloEjecucion(null, "nombre-1", "descripcion-1", false);
    entityManager.persistAndFlush(modeloEjecucion3);

    String nombreBuscado = "nombre-1";

    // when: se busca el ModeloEjecucion por nombre
    ModeloEjecucion modeloEjecucionEncontrado = repository.findByNombreAndActivoIsTrue(nombreBuscado).get();

    // then: Se recupera el ModeloEjecucion con el nombre buscado
    Assertions.assertThat(modeloEjecucionEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(modeloEjecucionEncontrado.getNombre()).as("getNombre")
        .isEqualTo(modeloEjecucion1.getNombre());
    Assertions.assertThat(modeloEjecucionEncontrado.getDescripcion()).as("getDescripcion")
        .isEqualTo(modeloEjecucion1.getDescripcion());
    Assertions.assertThat(modeloEjecucionEncontrado.getActivo()).as("getActivo").isEqualTo(Boolean.TRUE);
  }

  @Test
  public void findByNombreAndActivoIsTrueNoExiste_ReturnsNull() throws Exception {

    // given: 2 ModeloEjecucion que no coinciden con el nombre buscado
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(modeloEjecucion1);

    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(modeloEjecucion2);

    String nombreBuscado = "nombre-noexiste";

    // when: se busca el ModeloEjecucion por nombre
    Optional<ModeloEjecucion> modeloEjecucionEncontrado = repository.findByNombreAndActivoIsTrue(nombreBuscado);

    // then: Se recupera el TipoDocumento con el nombre buscado
    Assertions.assertThat(modeloEjecucionEncontrado).isEqualTo(Optional.empty());
  }

}