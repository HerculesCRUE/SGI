package org.crue.hercules.sgi.csp.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.model.TipoFinanciacionDescripcion;
import org.crue.hercules.sgi.csp.model.TipoFinanciacionNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * TipoFinanciacionRepositoryTest
 */
@DataJpaTest
class TipoFinanciacionRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private TipoFinanciacionRepository repository;

  @Test
  void findByNombreAndActivoIsTrue_ReturnsTipoFinanciacion() throws Exception {

    // given: 2 TipoFinanciacion de los que 1 coincide con el nombre buscado
    Set<TipoFinanciacionNombre> nombre1 = new HashSet<>();
    nombre1.add(new TipoFinanciacionNombre(Language.ES, "nombre-tipoFinanciacion1"));

    Set<TipoFinanciacionDescripcion> descripcion1 = new HashSet<>();
    descripcion1.add(new TipoFinanciacionDescripcion(Language.ES, "nombre-tipoFinanciacion1"));

    TipoFinanciacion tipoFinanciacion1 = new TipoFinanciacion(null,
        nombre1, descripcion1, true);
    entityManager.persistAndFlush(tipoFinanciacion1);

    Set<TipoFinanciacionNombre> nombre2 = new HashSet<>();
    nombre2.add(new TipoFinanciacionNombre(Language.ES, "nombre-tipoFinanciacion2"));

    Set<TipoFinanciacionDescripcion> descripcion2 = new HashSet<>();
    descripcion2.add(new TipoFinanciacionDescripcion(Language.ES, "descripcion-tipoFinanciacion2"));

    TipoFinanciacion tipoFinanciacion2 = new TipoFinanciacion(null, nombre2, descripcion2, true);
    entityManager.persistAndFlush(tipoFinanciacion2);

    String nombreBuscado = "nombre-tipoFinanciacion1";

    // when: se busca el TipoFinanciacionpor nombre
    TipoFinanciacion tipoFinanciacionEncontrado = repository
        .findByNombreLangAndNombreValueAndActivoIsTrue(Language.ES, nombreBuscado).get();

    // then: Se recupera el TipoFinanciacion con el nombre buscado
    Assertions.assertThat(tipoFinanciacionEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(tipoFinanciacionEncontrado.getNombre()).as("getNombre")
        .isEqualTo(tipoFinanciacion1.getNombre());
    Assertions.assertThat(tipoFinanciacionEncontrado.getDescripcion()).as("getDescripcion")
        .isEqualTo(tipoFinanciacion1.getDescripcion());
    Assertions.assertThat(tipoFinanciacionEncontrado.getActivo()).as("getActivo")
        .isEqualTo(tipoFinanciacion1.getActivo());
  }

  @Test
  void findByNombreAndActivoIsTrue_WithNombreNoExiste_ReturnsNull() throws Exception {

    // given: 2 TipoFinanciacion que no coinciden con el nombre buscado
    Set<TipoFinanciacionNombre> nombre1 = new HashSet<>();
    nombre1.add(new TipoFinanciacionNombre(Language.ES, "nombre-tipoFinanciacion1"));

    Set<TipoFinanciacionDescripcion> descripcion1 = new HashSet<>();
    descripcion1.add(new TipoFinanciacionDescripcion(Language.ES, "descripcion-tipoFinanciacion1"));

    TipoFinanciacion tipoFinanciacion1 = new TipoFinanciacion(null,
        nombre1, descripcion1, true);
    entityManager.persistAndFlush(tipoFinanciacion1);

    Set<TipoFinanciacionNombre> nombre2 = new HashSet<>();
    nombre2.add(new TipoFinanciacionNombre(Language.ES, "nombre-tipoFinanciacion"));

    Set<TipoFinanciacionDescripcion> descripcion2 = new HashSet<>();
    descripcion2.add(new TipoFinanciacionDescripcion(Language.ES, "descripcion-tipoFinanciacion"));

    TipoFinanciacion tipoFinanciacion2 = new TipoFinanciacion(null,
        nombre2, descripcion2, true);
    entityManager.persistAndFlush(tipoFinanciacion2);

    String nombreBuscado = "nombre-tipoFinanciacion-noexiste";

    // when: se busca elTipoFinanciacion por nombre
    Optional<TipoFinanciacion> tipoFinanciacionEncontrado = repository
        .findByNombreLangAndNombreValueAndActivoIsTrue(Language.ES, nombreBuscado);

    // then: Se recupera el TipoFinanciacion con el nombre buscado
    Assertions.assertThat(tipoFinanciacionEncontrado).isEqualTo(Optional.empty());
  }

}
