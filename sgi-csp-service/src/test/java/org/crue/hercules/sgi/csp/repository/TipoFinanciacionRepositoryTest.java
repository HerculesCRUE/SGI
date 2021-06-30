package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * TipoFinanciacionRepositoryTest
 */
@DataJpaTest
public class TipoFinanciacionRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private TipoFinanciacionRepository repository;

  @Test
  public void findByNombreAndActivoIsTrue_ReturnsTipoFinanciacion() throws Exception {

    // given: 2 TipoFinanciacion de los que 1 coincide con el nombre buscado
    TipoFinanciacion tipoFinanciacion1 = new TipoFinanciacion(null, "nombre-tipoFinanciacion1",
        "descripcion-tipoFinanciacion1", true);
    entityManager.persistAndFlush(tipoFinanciacion1);

    TipoFinanciacion tipoFinanciacion2 = new TipoFinanciacion(null, "nombre-tipoFinanciacion2",
        "descripcion-tipoFinanciacion2", true);
    entityManager.persistAndFlush(tipoFinanciacion2);

    String nombreBuscado = "nombre-tipoFinanciacion1";

    // when: se busca el TipoFinanciacionpor nombre
    TipoFinanciacion tipoFinanciacionEncontrado = repository.findByNombreAndActivoIsTrue(nombreBuscado).get();

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
  public void findByNombreAndActivoIsTrue_WithNombreNoExiste_ReturnsNull() throws Exception {

    // given: 2 TipoFinanciacion que no coinciden con el nombre buscado
    TipoFinanciacion tipoFinanciacion1 = new TipoFinanciacion(null, "nombre-tipoFinanciacion1",
        "descripcion-tipoFinanciacion1", true);
    entityManager.persistAndFlush(tipoFinanciacion1);

    TipoFinanciacion tipoFinanciacion2 = new TipoFinanciacion(null, "nombre-tipoFinanciacion",
        "descripcion-tipoFinanciacion2", true);
    entityManager.persistAndFlush(tipoFinanciacion2);

    String nombreBuscado = "nombre-tipoFinanciacion-noexiste";

    // when: se busca elTipoFinanciacion por nombre
    Optional<TipoFinanciacion> tipoFinanciacionEncontrado = repository.findByNombreAndActivoIsTrue(nombreBuscado);

    // then: Se recupera el TipoFinanciacion con el nombre buscado
    Assertions.assertThat(tipoFinanciacionEncontrado).isEqualTo(Optional.empty());
  }

}
