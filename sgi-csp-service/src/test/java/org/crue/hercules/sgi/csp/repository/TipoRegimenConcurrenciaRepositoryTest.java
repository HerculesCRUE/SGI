package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class TipoRegimenConcurrenciaRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private TipoRegimenConcurrenciaRepository repository;

  @Test
  public void findByNombre_ReturnsTipoRegimenConcurrencia() throws Exception {
    // given: data TipoRegimenConcurrencia with nombre to find
    TipoRegimenConcurrencia data = generarMockTipoRegimenConcurrencia(1L, Boolean.TRUE);
    entityManager.persistAndFlush(data);
    entityManager.persistAndFlush(generarMockTipoRegimenConcurrencia(2L, Boolean.TRUE));
    entityManager.persistAndFlush(generarMockTipoRegimenConcurrencia(3L, Boolean.TRUE));

    // when: find given nombre
    TipoRegimenConcurrencia dataFound = repository.findByNombre(data.getNombre()).get();

    // then: TipoRegimenConcurrencia with given name is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(data.getId());
    Assertions.assertThat(dataFound.getNombre()).isEqualTo(data.getNombre());
    Assertions.assertThat(dataFound.getActivo()).isEqualTo(data.getActivo());
  }

  @Test
  public void findByNombre_ReturnsNull() throws Exception {
    // given: data TipoRegimenConcurrencia with nombre to find
    TipoRegimenConcurrencia data = generarMockTipoRegimenConcurrencia(1L, Boolean.TRUE);
    entityManager.persistAndFlush(generarMockTipoRegimenConcurrencia(2L, Boolean.TRUE));
    entityManager.persistAndFlush(generarMockTipoRegimenConcurrencia(3L, Boolean.TRUE));

    // when: find given nombre
    Optional<TipoRegimenConcurrencia> dataFound = repository.findByNombre(data.getNombre());

    // then: TipoRegimenConcurrencia with given name is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }

  /**
   * Función que devuelve un objeto TipoRegimenConcurrencia
   * 
   * @param id
   * @param activo
   * @return TipoRegimenConcurrencia
   */
  private TipoRegimenConcurrencia generarMockTipoRegimenConcurrencia(Long id, Boolean activo) {
    return TipoRegimenConcurrencia.builder().nombre("nombre-" + id).activo(activo).build();
  }
}
