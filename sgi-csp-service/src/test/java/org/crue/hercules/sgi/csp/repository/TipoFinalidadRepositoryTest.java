package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class TipoFinalidadRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private TipoFinalidadRepository repository;

  @Test
  public void findByNombreAndActivoIsTrue_ReturnsTipoFinalidad() throws Exception {
    // given: data TipoFinalidad with nombre to find
    TipoFinalidad data = generarMockTipoFinalidad(1L, Boolean.TRUE);
    entityManager.persistAndFlush(data);
    entityManager.persistAndFlush(generarMockTipoFinalidad(2L, Boolean.TRUE));
    entityManager.persistAndFlush(generarMockTipoFinalidad(1L, Boolean.FALSE));

    // when: find given nombre
    TipoFinalidad dataFound = repository.findByNombreAndActivoIsTrue(data.getNombre()).get();

    // then: TipoFinalidad with given name is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(data.getId());
    Assertions.assertThat(dataFound.getNombre()).isEqualTo(data.getNombre());
    Assertions.assertThat(dataFound.getDescripcion()).isEqualTo(data.getDescripcion());
    Assertions.assertThat(dataFound.getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Test
  public void findByNombreAndActivoIsTrue_ReturnsNull() throws Exception {
    // given: data TipoFinalidad with nombre to find
    TipoFinalidad data = generarMockTipoFinalidad(1L, Boolean.TRUE);
    entityManager.persistAndFlush(generarMockTipoFinalidad(2L, Boolean.TRUE));
    entityManager.persistAndFlush(generarMockTipoFinalidad(3L, Boolean.TRUE));

    // when: find given nombre
    Optional<TipoFinalidad> dataFound = repository.findByNombreAndActivoIsTrue(data.getNombre());

    // then: TipoFinalidad with given name is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }

  /**
   * Función que devuelve un objeto TipoFinalidad
   * 
   * @param id
   * @param activo
   * @return TipoFinalidad
   */
  private TipoFinalidad generarMockTipoFinalidad(Long id, Boolean activo) {
    return TipoFinalidad.builder().nombre("nombre-" + id).descripcion("descripcion-" + id).activo(activo).build();
  }
}
