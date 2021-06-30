package org.crue.hercules.sgi.csp.repository;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Programa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ProgramaRepositoryTest
 */
@DataJpaTest
public class ProgramaRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProgramaRepository repository;

  @Test
  public void findByPadreIdInAndActivoIsTrue_ReturnsPrograma() throws Exception {
    // given: 2 Programa de los que 1 coincide con el id padre buscado
    Programa programa1 = new Programa(null, "nombre-1", "descripcion-1", null, true);
    entityManager.persistAndFlush(programa1);

    Programa programa2 = new Programa(null, "nombre-2", "descripcion-2", programa1, true);
    entityManager.persistAndFlush(programa2);

    List<Long> idsPadreBuscados = Arrays.asList(programa1.getId());

    // when: se busca el Programa el id padre
    List<Programa> programaEncontrados = repository.findByPadreIdInAndActivoIsTrue(idsPadreBuscados);

    // then: Se recupera el Programa con el id padre buscado
    Assertions.assertThat(programaEncontrados.size()).as("size()").isEqualTo(1);
    Programa programaEncontrado = programaEncontrados.get(0);
    Assertions.assertThat(programaEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(programaEncontrado.getNombre()).as("getNombre").isEqualTo(programa2.getNombre());
    Assertions.assertThat(programaEncontrado.getDescripcion()).as("getDescripcion")
        .isEqualTo(programa2.getDescripcion());
    Assertions.assertThat(programaEncontrado.getActivo()).as("getActivo").isEqualTo(programa2.getActivo());
  }

  @Test
  public void findByPadreIdInAndActivoIsTrue_IdNoExiste_ReturnsEmptyList() throws Exception {
    // given: 2 Programa que no coinciden con el id padre buscado
    Programa programa1 = new Programa(null, "nombre-1", "descripcion-1", null, true);
    entityManager.persistAndFlush(programa1);

    Programa programa2 = new Programa(null, "nombre-2", "descripcion-2", programa1, true);
    entityManager.persistAndFlush(programa2);

    List<Long> idsPadreBuscados = Arrays.asList(programa2.getId());

    // when: se busca el Programa el id padre
    List<Programa> programaEncontrados = repository.findByPadreIdInAndActivoIsTrue(idsPadreBuscados);

    // then: No hay ningun Programa con el id padre buscado
    Assertions.assertThat(programaEncontrados.size()).as("size()").isEqualTo(0);

  }

}