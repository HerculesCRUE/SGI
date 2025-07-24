package org.crue.hercules.sgi.csp.repository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.AreaTematicaDescripcion;
import org.crue.hercules.sgi.csp.model.AreaTematicaNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AreaTematicaRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private AreaTematicaRepository repository;

  @Test
  void findByPadreIdInAndActivoIsTrue_ReturnsAreaTematica() throws Exception {
    // given: 2 AreaTematica de los que 1 coincide con el id padre buscado
    Set<AreaTematicaNombre> nombre1 = new HashSet<>();
    nombre1.add(new AreaTematicaNombre(Language.ES, "nombre-1"));

    Set<AreaTematicaDescripcion> descripcion1 = new HashSet<>();
    descripcion1.add(new AreaTematicaDescripcion(Language.ES, "descripcion-1"));

    AreaTematica areaTematica1 = new AreaTematica(null, nombre1, descripcion1, null, true);
    entityManager.persistAndFlush(areaTematica1);

    Set<AreaTematicaNombre> nombre2 = new HashSet<>();
    nombre2.add(new AreaTematicaNombre(Language.ES, "nombre-2"));

    Set<AreaTematicaDescripcion> descripcion2 = new HashSet<>();
    descripcion2.add(new AreaTematicaDescripcion(Language.ES, "descripcion-2"));

    AreaTematica areaTematica2 = new AreaTematica(null, nombre2, descripcion2, areaTematica1, true);
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
  void findByPadreIdInAndActivoIsTrue_IdNoExiste_ReturnsEmptyList() throws Exception {
    // given: 2 AreaTematica que no coinciden con el id padre buscado
    Set<AreaTematicaNombre> nombre1 = new HashSet<>();
    nombre1.add(new AreaTematicaNombre(Language.ES, "nombre-1"));

    Set<AreaTematicaDescripcion> decripcion1 = new HashSet<>();
    decripcion1.add(new AreaTematicaDescripcion(Language.ES, "descripcion-1"));

    AreaTematica areaTematica1 = new AreaTematica(null, nombre1, decripcion1, null, true);
    entityManager.persistAndFlush(areaTematica1);

    Set<AreaTematicaNombre> nombre2 = new HashSet<>();
    nombre2.add(new AreaTematicaNombre(Language.ES, "nombre-2"));

    Set<AreaTematicaDescripcion> decripcion2 = new HashSet<>();
    decripcion2.add(new AreaTematicaDescripcion(Language.ES, "descripcion-2"));

    AreaTematica areaTematica2 = new AreaTematica(null, nombre2, decripcion2, areaTematica1, true);
    entityManager.persistAndFlush(areaTematica2);

    List<Long> idsPadreBuscados = Arrays.asList(areaTematica2.getId());

    // when: se busca el AreaTematica el id padre
    List<AreaTematica> areaTematicaEncontrados = repository.findByPadreIdInAndActivoIsTrue(idsPadreBuscados);

    // then: No hay ningun AreaTematica con el id padre buscado
    Assertions.assertThat(areaTematicaEncontrados.size()).as("size()").isZero();

  }
}
