package org.crue.hercules.sgi.csp.repository;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class AreaTematicaRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private AreaTematicaRepository repository;

  @Test
  public void findByPadreIdInAndActivoIsTrue_ReturnsAreaTematica() throws Exception {
    // given: 2 AreaTematica de los que 1 coincide con el id padre buscado
    AreaTematica areaTematica1 = new AreaTematica(null, "nombre-1", "descripcion-1", null, true);
    entityManager.persistAndFlush(areaTematica1);

    AreaTematica areaTematica2 = new AreaTematica(null, "nombre-2", "descripcion-2", areaTematica1, true);
    entityManager.persistAndFlush(areaTematica2);

    List<Long> idsPadreBuscados = Arrays.asList(areaTematica1.getId());

    // when: se busca el AreaTematica el id padre
    List<AreaTematica> areaTematicaEncontrados = repository.findByPadreIdInAndActivoIsTrue(idsPadreBuscados);

    // then: Se recupera el AreaTematica con el id padre buscado
    Assertions.assertThat(areaTematicaEncontrados.size()).as("size()").isEqualTo(1);
    AreaTematica areaTematicaEncontrado = areaTematicaEncontrados.get(0);
    Assertions.assertThat(areaTematicaEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(areaTematicaEncontrado.getNombre()).as("getNombre").isEqualTo(areaTematica2.getNombre());
    Assertions.assertThat(areaTematicaEncontrado.getDescripcion()).as("getDescripcion")
        .isEqualTo(areaTematica2.getDescripcion());
    Assertions.assertThat(areaTematicaEncontrado.getActivo()).as("getActivo").isEqualTo(areaTematica2.getActivo());
  }

  @Test
  public void findByPadreIdInAndActivoIsTrue_IdNoExiste_ReturnsEmptyList() throws Exception {
    // given: 2 AreaTematica que no coinciden con el id padre buscado
    AreaTematica areaTematica1 = new AreaTematica(null, "nombre-1", "descripcion-1", null, true);
    entityManager.persistAndFlush(areaTematica1);

    AreaTematica areaTematica2 = new AreaTematica(null, "nombre-2", "descripcion-2", areaTematica1, true);
    entityManager.persistAndFlush(areaTematica2);

    List<Long> idsPadreBuscados = Arrays.asList(areaTematica2.getId());

    // when: se busca el AreaTematica el id padre
    List<AreaTematica> areaTematicaEncontrados = repository.findByPadreIdInAndActivoIsTrue(idsPadreBuscados);

    // then: No hay ningun AreaTematica con el id padre buscado
    Assertions.assertThat(areaTematicaEncontrados.size()).as("size()").isEqualTo(0);

  }
}
