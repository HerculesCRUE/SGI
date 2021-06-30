package org.crue.hercules.sgi.usr.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.usr.model.Unidad;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * UnidadRepositoryTest
 */
@DataJpaTest
class UnidadRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private UnidadRepository repository;

  @Test
  void findByNombre_ReturnsUnidad() throws Exception {
    // given: 2 Unidad de los que 1 coincide con el nombre buscado
    Unidad unidad1 = new Unidad(null, "nombre-1", "acronimo-1", "descripcion-1", true);
    entityManager.persistAndFlush(unidad1);

    Unidad unidad2 = new Unidad(null, "nombre-2", "acronimo-2", "descripcion-2", true);
    entityManager.persistAndFlush(unidad2);

    String nombreBuscado = "nombre-1";

    // when: se busca el Unidad nombre
    Unidad unidadEncontrado = repository.findByNombre(nombreBuscado).get();

    // then: Se recupera el Unidad con el nombre buscado
    Assertions.assertThat(unidadEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(unidadEncontrado.getNombre()).as("getNombre").isEqualTo(unidad1.getNombre());
    Assertions.assertThat(unidadEncontrado.getAcronimo()).as("getAcronimo").isEqualTo(unidad1.getAcronimo());
    Assertions.assertThat(unidadEncontrado.getDescripcion()).as("getDescripcion").isEqualTo(unidad1.getDescripcion());
    Assertions.assertThat(unidadEncontrado.getActivo()).as("getActivo").isEqualTo(unidad1.getActivo());
  }

  @Test
  void findByNombreNoExiste_ReturnsNull() throws Exception {
    // given: 2 Unidad que no coinciden con el nombre buscado
    Unidad unidad1 = new Unidad(null, "nombre-1", "acronimo-1", "descripcion-1", true);
    entityManager.persistAndFlush(unidad1);

    Unidad unidad2 = new Unidad(null, "nombre-2", "acronimo-2", "descripcion-2", true);
    entityManager.persistAndFlush(unidad2);

    String nombreBuscado = "nombre-noexiste";

    // when: se busca el Unidad por nombre
    Optional<Unidad> unidadEncontrado = repository.findByNombre(nombreBuscado);

    // then: No hay ningun Unidad con el nombre buscado
    Assertions.assertThat(unidadEncontrado).isEmpty();
  }

  @Test
  void findByAcronimo_ReturnsUnidad() throws Exception {
    // given: 2 Unidad de los que 1 coincide con el acronimo buscado
    Unidad unidad1 = new Unidad(null, "nombre-1", "acronimo-1", "descripcion-1", true);
    entityManager.persistAndFlush(unidad1);

    Unidad unidad2 = new Unidad(null, "nombre-2", "acronimo-2", "descripcion-2", true);
    entityManager.persistAndFlush(unidad2);

    String acronimoBuscado = "acronimo-1";

    // when: se busca el Unidad nombre
    Unidad unidadEncontrado = repository.findByAcronimo(acronimoBuscado).get();

    // then: Se recupera el Unidad con el acronimo buscado
    Assertions.assertThat(unidadEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(unidadEncontrado.getNombre()).as("getNombre").isEqualTo(unidad1.getNombre());
    Assertions.assertThat(unidadEncontrado.getAcronimo()).as("getAcronimo").isEqualTo(unidad1.getAcronimo());
    Assertions.assertThat(unidadEncontrado.getDescripcion()).as("getDescripcion").isEqualTo(unidad1.getDescripcion());
    Assertions.assertThat(unidadEncontrado.getActivo()).as("getActivo").isEqualTo(unidad1.getActivo());
  }

  @Test
  void findByAcronimoNoExiste_ReturnsNull() throws Exception {
    // given: 2 Unidad que no coinciden con el acronimo buscado
    Unidad unidad1 = new Unidad(null, "nombre-1", "acronimo-1", "descripcion-1", true);
    entityManager.persistAndFlush(unidad1);

    Unidad unidad2 = new Unidad(null, "nombre-2", "acronimo-2", "descripcion-2", true);
    entityManager.persistAndFlush(unidad2);

    String acronimoBuscado = "acronimo-noexiste";

    // when: se busca el Unidad por acronimo
    Optional<Unidad> unidadEncontrado = repository.findByNombre(acronimoBuscado);

    // then: No hay ningun Unidad con el acronimo buscado
    Assertions.assertThat(unidadEncontrado).isEmpty();
  }
}
