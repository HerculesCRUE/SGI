package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class TipoEnlaceRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private TipoEnlaceRepository repository;

  @Test
  public void findByNombreAndActivoIsTrue_ReturnsTipoEnlace() throws Exception {
    // given: data TipoEnlace with nombre to find
    TipoEnlace data = generarMockTipoEnlace(1L, Boolean.TRUE);
    entityManager.persistAndFlush(data);
    entityManager.persistAndFlush(generarMockTipoEnlace(2L, Boolean.TRUE));
    entityManager.persistAndFlush(generarMockTipoEnlace(1L, Boolean.FALSE));

    // when: find given nombre
    TipoEnlace dataFound = repository.findByNombreAndActivoIsTrue(data.getNombre()).get();

    // then: TipoEnlace with given name is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(data.getId());
    Assertions.assertThat(dataFound.getNombre()).isEqualTo(data.getNombre());
    Assertions.assertThat(dataFound.getDescripcion()).isEqualTo(data.getDescripcion());
    Assertions.assertThat(dataFound.getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Test
  public void findByNombreAndActivoIsTrue_ReturnsNull() throws Exception {
    // given: data TipoEnlace with nombre to find
    TipoEnlace data = generarMockTipoEnlace(1L, Boolean.TRUE);
    entityManager.persistAndFlush(generarMockTipoEnlace(2L, Boolean.TRUE));
    entityManager.persistAndFlush(generarMockTipoEnlace(1L, Boolean.FALSE));

    // when: find given nombre
    Optional<TipoEnlace> dataFound = repository.findByNombreAndActivoIsTrue(data.getNombre());

    // then: TipoEnlace with given name is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }

  /**
   * Función que devuelve un objeto TipoEnlace
   * 
   * @param id
   * @param activo
   * @return TipoEnlace
   */
  private TipoEnlace generarMockTipoEnlace(Long id, Boolean activo) {
    return TipoEnlace.builder().nombre("nombre-" + id).descripcion("descripcion-" + id).activo(activo).build();
  }
}
