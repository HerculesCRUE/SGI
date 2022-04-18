package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.LineaInvestigacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class LineaInvestigacionRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private LineaInvestigacionRepository repository;

  @Test
  public void findByNombreAndActivoIsTrue_ReturnsLineaInvestigacion() throws Exception {
    // given: data LineaInvestigacion with nombre to find
    LineaInvestigacion data = generarMockLineaInvestigacion(1L, Boolean.TRUE);
    entityManager.persistAndFlush(data);
    entityManager.persistAndFlush(generarMockLineaInvestigacion(2L, Boolean.TRUE));
    entityManager.persistAndFlush(generarMockLineaInvestigacion(1L, Boolean.FALSE));

    // when: find given nombre
    LineaInvestigacion dataFound = repository.findByNombreAndActivoIsTrue(data.getNombre()).get();

    // then: LineaInvestigacion with given name is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(data.getId());
    Assertions.assertThat(dataFound.getNombre()).isEqualTo(data.getNombre());
    Assertions.assertThat(dataFound.getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Test
  public void findByNombreAndActivoIsTrue_ReturnsNull() throws Exception {
    // given: data LineaInvestigacion with nombre to find
    LineaInvestigacion data = generarMockLineaInvestigacion(1L, Boolean.TRUE);
    entityManager.persistAndFlush(generarMockLineaInvestigacion(2L, Boolean.TRUE));
    entityManager.persistAndFlush(generarMockLineaInvestigacion(1L, Boolean.FALSE));

    // when: find given nombre
    Optional<LineaInvestigacion> dataFound = repository.findByNombreAndActivoIsTrue(data.getNombre());

    // then: LineaInvestigacion with given name is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }

  /**
   * Función que devuelve un objeto LineaInvestigacion
   * 
   * @param id
   * @param activo
   * @return LineaInvestigacion
   */
  private LineaInvestigacion generarMockLineaInvestigacion(Long id, Boolean activo) {
    return LineaInvestigacion.builder().nombre("nombre-" + id).activo(activo).build();
  }
}
