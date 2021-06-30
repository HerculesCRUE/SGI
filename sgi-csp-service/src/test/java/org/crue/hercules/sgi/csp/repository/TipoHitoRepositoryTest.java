package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * TipoHitoRepositoryTest
 */
@DataJpaTest
public class TipoHitoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private TipoHitoRepository repository;

  @Test
  public void findByNombreAndActivoIsTrue_ReturnsTipoHito() throws Exception {

    // given: 2 TipoHito de los que 1 coincide con el nombre buscado
    TipoHito tipoHito1 = new TipoHito(null, "nombre-tipoHito1", "descripcion-tipoHito1", true);
    entityManager.persistAndFlush(tipoHito1);

    TipoHito tipoHito2 = new TipoHito(null, "nombre-tipoHito2", "descripcion-tipoHito2", true);
    entityManager.persistAndFlush(tipoHito2);

    TipoHito tipoHito3 = new TipoHito(null, "nombre-tipoHito1", "descripcion-tipoHito1", false);
    entityManager.persistAndFlush(tipoHito3);

    String nombreBuscado = "nombre-tipoHito1";

    // when: se busca el TipoHitopor nombre
    TipoHito tipoHitoEncontrado = repository.findByNombreAndActivoIsTrue(nombreBuscado).get();

    // then: Se recupera el TipoHito con el nombre buscado
    Assertions.assertThat(tipoHitoEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(tipoHitoEncontrado.getNombre()).as("getNombre").isEqualTo(tipoHito1.getNombre());
    Assertions.assertThat(tipoHitoEncontrado.getDescripcion()).as("getDescripcion")
        .isEqualTo(tipoHito1.getDescripcion());
    Assertions.assertThat(tipoHitoEncontrado.getActivo()).as("getActivo").isEqualTo(Boolean.TRUE);
  }

  @Test
  public void findByNombreAndActivoIsTrueNoExiste_ReturnsNull() throws Exception {

    // given: 2 TipoHito que no coinciden con el nombre buscado
    TipoHito tipoHito1 = new TipoHito(null, "nombre-tipoHito1", "descripcion-tipoHito1", true);
    entityManager.persistAndFlush(tipoHito1);

    TipoHito tipoHito2 = new TipoHito(null, "nombre-tipoHito", "descripcion-tipoHito2", true);
    entityManager.persistAndFlush(tipoHito2);

    String nombreBuscado = "nombre-tipoHito-noexiste";

    // when: se busca el TipoHito por nombre
    Optional<TipoHito> tipoHitoEncontrado = repository.findByNombreAndActivoIsTrue(nombreBuscado);

    // then: Se recupera el TipoHito con el nombre buscado
    Assertions.assertThat(tipoHitoEncontrado).isEqualTo(Optional.empty());
  }

}
