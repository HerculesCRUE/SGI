package org.crue.hercules.sgi.csp.repository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.ProgramaDescripcion;
import org.crue.hercules.sgi.csp.model.ProgramaNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ProgramaRepositoryTest
 */
@DataJpaTest
class ProgramaRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProgramaRepository repository;

  @Test
  void findByPadreIdInAndActivoIsTrue_ReturnsPrograma() throws Exception {
    // given: 2 Programa de los que 1 coincide con el id padre buscado
    Set<ProgramaNombre> nombrePrograma1 = new HashSet<>();
    nombrePrograma1.add(new ProgramaNombre(Language.ES, "nombre-1"));

    Set<ProgramaDescripcion> descripcionPrograma1 = new HashSet<>();
    descripcionPrograma1.add(new ProgramaDescripcion(Language.ES, "descripcion-1"));

    Programa programa1 = new Programa(null, nombrePrograma1, descripcionPrograma1, null, true);
    entityManager.persistAndFlush(programa1);

    Set<ProgramaNombre> nombrePrograma2 = new HashSet<>();
    nombrePrograma2.add(new ProgramaNombre(Language.ES, "nombre-2"));

    Set<ProgramaDescripcion> descripcionPrograma2 = new HashSet<>();
    descripcionPrograma2.add(new ProgramaDescripcion(Language.ES, "descripcion-2"));

    Programa programa2 = new Programa(null, nombrePrograma2, descripcionPrograma2, programa1, true);
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
  void findByPadreIdInAndActivoIsTrue_IdNoExiste_ReturnsEmptyList() throws Exception {
    // given: 2 Programa que no coinciden con el id padre buscado
    Set<ProgramaNombre> nombrePrograma1 = new HashSet<>();
    nombrePrograma1.add(new ProgramaNombre(Language.ES, "nombre-1"));

    Set<ProgramaDescripcion> descripcionPrograma1 = new HashSet<>();
    descripcionPrograma1.add(new ProgramaDescripcion(Language.ES, "descripcion-1"));

    Programa programa1 = new Programa(null, nombrePrograma1, descripcionPrograma1, null, true);
    entityManager.persistAndFlush(programa1);

    Set<ProgramaNombre> nombrePrograma2 = new HashSet<>();
    nombrePrograma2.add(new ProgramaNombre(Language.ES, "nombre-2"));

    Set<ProgramaDescripcion> descripcionPrograma2 = new HashSet<>();
    descripcionPrograma2.add(new ProgramaDescripcion(Language.ES, "descripcion-2"));

    Programa programa2 = new Programa(null, nombrePrograma2, descripcionPrograma2, programa1, true);
    entityManager.persistAndFlush(programa2);

    List<Long> idsPadreBuscados = Arrays.asList(programa2.getId());

    // when: se busca el Programa el id padre
    List<Programa> programaEncontrados = repository.findByPadreIdInAndActivoIsTrue(idsPadreBuscados);

    // then: No hay ningun Programa con el id padre buscado
    Assertions.assertThat(programaEncontrados.size()).as("size()").isZero();

  }

}