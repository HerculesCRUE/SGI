package org.crue.hercules.sgi.csp.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFaseDescripcion;
import org.crue.hercules.sgi.csp.model.TipoFaseNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * TipoFaseRepositoryTest
 */
@DataJpaTest
class TipoFaseRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private TipoFaseRepository repository;

  @Test
  void findByNombreLangAndNombreValueAndActivoIsTrue_ReturnsTipoFase() {

    // given: 3 TipoFase de los que 1 coincide con el nombre buscado
    Set<TipoFaseNombre> nombreTipoFase1 = new HashSet<>();
    nombreTipoFase1.add(new TipoFaseNombre(Language.ES, "nombre-tipoFase1"));

    Set<TipoFaseDescripcion> descripcionTipoFase1 = new HashSet<>();
    descripcionTipoFase1.add(new TipoFaseDescripcion(Language.ES, "descripcion-tipoFase1"));

    TipoFase tipoFase1 = new TipoFase(null, nombreTipoFase1, descripcionTipoFase1, true);
    entityManager.persistAndFlush(tipoFase1);

    Set<TipoFaseNombre> nombreTipoFase2 = new HashSet<>();
    nombreTipoFase2.add(new TipoFaseNombre(Language.ES, "nombre-tipoFase2"));

    Set<TipoFaseDescripcion> descripcionTipoFase2 = new HashSet<>();
    descripcionTipoFase2.add(new TipoFaseDescripcion(Language.ES, "descripcion-tipoFase2"));

    TipoFase tipoFase2 = new TipoFase(null, nombreTipoFase2, descripcionTipoFase2, true);
    entityManager.persistAndFlush(tipoFase2);

    Set<TipoFaseNombre> nombreTipoFase3 = new HashSet<>();
    nombreTipoFase3.add(new TipoFaseNombre(Language.ES, "nombre-tipoFase3"));

    Set<TipoFaseDescripcion> descripcionTipoFase3 = new HashSet<>();
    descripcionTipoFase3.add(new TipoFaseDescripcion(Language.ES, "descripcion-tipoFase3"));

    TipoFase tipoFase3 = new TipoFase(null, nombreTipoFase3, descripcionTipoFase3, false);
    entityManager.persistAndFlush(tipoFase3);

    String nombreBuscado = "nombre-tipoFase1";

    // when: se busca el TipoFasepor nombre
    TipoFase tipoFaseEncontrado = repository.findByNombreLangAndNombreValueAndActivoIsTrue(Language.ES, nombreBuscado)
        .get();

    // then: Se recupera el TipoFase con el nombre buscado
    Assertions.assertThat(tipoFaseEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(tipoFaseEncontrado.getNombre()).as("getNombre").isEqualTo(tipoFase1.getNombre());
    Assertions.assertThat(tipoFaseEncontrado.getDescripcion()).as("getDescripcion")
        .isEqualTo(tipoFase1.getDescripcion());
    Assertions.assertThat(tipoFaseEncontrado.getActivo()).as("getActivo").isEqualTo(Boolean.TRUE);
  }

  @Test
  void findByNombreLangAndNombreValueAndActivoIsTrueNoExiste_ReturnsNull() {

    // given: 2 TipoFase que no coinciden con el nombre buscado
    Set<TipoFaseNombre> nombreTipoFase1 = new HashSet<>();
    nombreTipoFase1.add(new TipoFaseNombre(Language.ES, "nombre-tipoFase1"));

    Set<TipoFaseDescripcion> descripcionTipoFase1 = new HashSet<>();
    descripcionTipoFase1.add(new TipoFaseDescripcion(Language.ES, "descripcion-tipoFase1"));

    TipoFase tipoFase1 = new TipoFase(null, nombreTipoFase1, descripcionTipoFase1, true);
    entityManager.persistAndFlush(tipoFase1);

    Set<TipoFaseNombre> nombreTipoFase2 = new HashSet<>();
    nombreTipoFase2.add(new TipoFaseNombre(Language.ES, "nombre-tipoFase2"));

    Set<TipoFaseDescripcion> descripcionTipoFase2 = new HashSet<>();
    descripcionTipoFase2.add(new TipoFaseDescripcion(Language.ES, "descripcion-tipoFase2"));

    TipoFase tipoFase2 = new TipoFase(null, nombreTipoFase2, descripcionTipoFase2, true);
    entityManager.persistAndFlush(tipoFase2);

    String nombreBuscado = "nombre-tipoFase-noexiste";

    // when: se busca el TipoFase por nombre
    Optional<TipoFase> tipoFaseEncontrado = repository.findByNombreLangAndNombreValueAndActivoIsTrue(Language.ES,
        nombreBuscado);

    // then: Se recupera el TipoFase con el nombre buscado
    Assertions.assertThat(tipoFaseEncontrado).isEqualTo(Optional.empty());
  }

}
