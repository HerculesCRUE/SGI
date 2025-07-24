package org.crue.hercules.sgi.csp.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.model.TipoHitoDescripcion;
import org.crue.hercules.sgi.csp.model.TipoHitoNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * TipoHitoRepositoryTest
 */
@DataJpaTest
class TipoHitoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private TipoHitoRepository repository;

  @Test
  void findByNombreAndActivoIsTrue_ReturnsTipoHito() throws Exception {

    // given: 2 TipoHito de los que 1 coincide con el nombre buscado
    Set<TipoHitoNombre> nombreTipoHito1 = new HashSet<>();
    nombreTipoHito1.add(new TipoHitoNombre(Language.ES, "nombre-tipoHito1"));
    Set<TipoHitoDescripcion> descripcionTipoHito1 = new HashSet<>();
    descripcionTipoHito1.add(new TipoHitoDescripcion(Language.ES, "descripcion-tipoHito1"));
    TipoHito tipoHito1 = new TipoHito(null, nombreTipoHito1, descripcionTipoHito1, true);
    entityManager.persistAndFlush(tipoHito1);

    Set<TipoHitoNombre> nombreTipoHito2 = new HashSet<>();
    nombreTipoHito2.add(new TipoHitoNombre(Language.ES, "nombre-tipoHito2"));
    Set<TipoHitoDescripcion> descripcionTipoHito2 = new HashSet<>();
    descripcionTipoHito2.add(new TipoHitoDescripcion(Language.ES, "descripcion-tipoHito2"));
    TipoHito tipoHito2 = new TipoHito(null, nombreTipoHito2, descripcionTipoHito2, true);
    entityManager.persistAndFlush(tipoHito2);

    Set<TipoHitoNombre> nombreTipoHito3 = new HashSet<>();
    nombreTipoHito3.add(new TipoHitoNombre(Language.ES, "nombre-tipoHito1"));
    Set<TipoHitoDescripcion> descripcionTipoHito3 = new HashSet<>();
    descripcionTipoHito3.add(new TipoHitoDescripcion(Language.ES, "descripcion-tipoHito1"));
    TipoHito tipoHito3 = new TipoHito(null, nombreTipoHito3, descripcionTipoHito3, false);
    entityManager.persistAndFlush(tipoHito3);

    String nombreBuscado = "nombre-tipoHito1";

    // when: se busca el TipoHitopor nombre
    TipoHito tipoHitoEncontrado = repository.findByNombreLangAndNombreValueAndActivoIsTrue(Language.ES, nombreBuscado)
        .get();

    // then: Se recupera el TipoHito con el nombre buscado
    Assertions.assertThat(tipoHitoEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(tipoHitoEncontrado.getNombre()).as("getNombre").isEqualTo(tipoHito1.getNombre());
    Assertions.assertThat(tipoHitoEncontrado.getDescripcion()).as("getDescripcion")
        .isEqualTo(tipoHito1.getDescripcion());
    Assertions.assertThat(tipoHitoEncontrado.getActivo()).as("getActivo").isEqualTo(Boolean.TRUE);
  }

  @Test
  void findByNombreAndActivoIsTrueNoExiste_ReturnsNull() throws Exception {

    // given: 2 TipoHito que no coinciden con el nombre buscado
    Set<TipoHitoNombre> nombreTipoHito1 = new HashSet<>();
    nombreTipoHito1.add(new TipoHitoNombre(Language.ES, "nombre-tipoHito1"));
    Set<TipoHitoDescripcion> descripcionTipoHito1 = new HashSet<>();
    descripcionTipoHito1.add(new TipoHitoDescripcion(Language.ES, "descripcion-tipoHito1"));
    TipoHito tipoHito1 = new TipoHito(null, nombreTipoHito1, descripcionTipoHito1, true);
    entityManager.persistAndFlush(tipoHito1);

    Set<TipoHitoNombre> nombreTipoHito2 = new HashSet<>();
    nombreTipoHito2.add(new TipoHitoNombre(Language.ES, "nombre-tipoHito"));
    Set<TipoHitoDescripcion> descripcionTipoHito2 = new HashSet<>();
    descripcionTipoHito2.add(new TipoHitoDescripcion(Language.ES, "descripcion-tipoHito2"));
    TipoHito tipoHito2 = new TipoHito(null, nombreTipoHito2, descripcionTipoHito2, true);
    entityManager.persistAndFlush(tipoHito2);

    String nombreBuscado = "nombre-tipoHito-noexiste";

    // when: se busca el TipoHito por nombre
    Optional<TipoHito> tipoHitoEncontrado = repository.findByNombreLangAndNombreValueAndActivoIsTrue(Language.ES,
        nombreBuscado);

    // then: Se recupera el TipoHito con el nombre buscado
    Assertions.assertThat(tipoHitoEncontrado).isEqualTo(Optional.empty());
  }

}
