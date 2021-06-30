package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * TipoFaseRepositoryTest
 */
@DataJpaTest
public class TipoFaseRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private TipoFaseRepository repository;

  @Test
  public void findByNombreAndActivoIsTrue_ReturnsTipoFase() throws Exception {

    // given: 2 TipoFase de los que 1 coincide con el nombre buscado
    TipoFase tipoFase1 = new TipoFase(null, "nombre-tipoFase1", "descripcion-tipoFase1", true);
    entityManager.persistAndFlush(tipoFase1);

    TipoFase tipoFase2 = new TipoFase(null, "nombre-tipoFase2", "descripcion-tipoFase2", true);
    entityManager.persistAndFlush(tipoFase2);

    TipoFase tipoFase3 = new TipoFase(null, "nombre-tipoFase1", "descripcion-tipoFase1", false);
    entityManager.persistAndFlush(tipoFase3);

    String nombreBuscado = "nombre-tipoFase1";

    // when: se busca el TipoFasepor nombre
    TipoFase tipoFaseEncontrado = repository.findByNombreAndActivoIsTrue(nombreBuscado).get();

    // then: Se recupera el TipoFase con el nombre buscado
    Assertions.assertThat(tipoFaseEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(tipoFaseEncontrado.getNombre()).as("getNombre").isEqualTo(tipoFase1.getNombre());
    Assertions.assertThat(tipoFaseEncontrado.getDescripcion()).as("getDescripcion")
        .isEqualTo(tipoFase1.getDescripcion());
    Assertions.assertThat(tipoFaseEncontrado.getActivo()).as("getActivo").isEqualTo(Boolean.TRUE);
  }

  @Test
  public void findByNombreAndActivoIsTrueNoExiste_ReturnsNull() throws Exception {

    // given: 2 TipoFase que no coinciden con el nombre buscado
    TipoFase tipoFase1 = new TipoFase(null, "nombre-tipoFase1", "descripcion-tipoFase1", true);
    entityManager.persistAndFlush(tipoFase1);

    TipoFase tipoFase2 = new TipoFase(null, "nombre-tipoFase", "descripcion-tipoFase2", true);
    entityManager.persistAndFlush(tipoFase2);

    String nombreBuscado = "nombre-tipoFase-noexiste";

    // when: se busca el TipoFase por nombre
    Optional<TipoFase> tipoFaseEncontrado = repository.findByNombreAndActivoIsTrue(nombreBuscado);

    // then: Se recupera el TipoFase con el nombre buscado
    Assertions.assertThat(tipoFaseEncontrado).isEqualTo(Optional.empty());
  }

}
