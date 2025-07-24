package org.crue.hercules.sgi.csp.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.LineaInvestigacion;
import org.crue.hercules.sgi.csp.model.LineaInvestigacionNombre;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LineaInvestigacionRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private LineaInvestigacionRepository repository;

  @Test
  void findByNombreAndActivoIsTrue_ReturnsLineaInvestigacion() throws Exception {
    // given: data LineaInvestigacion with nombre to find
    LineaInvestigacion data = generarMockLineaInvestigacion(1L, Boolean.TRUE);
    entityManager.persistAndFlush(data);
    entityManager.persistAndFlush(generarMockLineaInvestigacion(2L, Boolean.TRUE));
    entityManager.persistAndFlush(generarMockLineaInvestigacion(1L, Boolean.FALSE));

    // when: find given nombre
    LineaInvestigacion dataFound = repository.findByNombreLangAndNombreValueAndActivoIsTrue(Language.ES,
        I18nHelper.getValueForLanguage(data.getNombre(), Language.ES)).get();

    // then: LineaInvestigacion with given name is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(data.getId());
    Assertions.assertThat(dataFound.getNombre()).isEqualTo(data.getNombre());
    Assertions.assertThat(dataFound.getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Test
  void findByNombreAndActivoIsTrue_ReturnsNull() throws Exception {
    // given: data LineaInvestigacion with nombre to find
    LineaInvestigacion data = generarMockLineaInvestigacion(1L, Boolean.TRUE);
    entityManager.persistAndFlush(generarMockLineaInvestigacion(2L, Boolean.TRUE));
    entityManager.persistAndFlush(generarMockLineaInvestigacion(1L, Boolean.FALSE));

    // when: find given nombre
    Optional<LineaInvestigacion> dataFound = repository.findByNombreLangAndNombreValueAndActivoIsTrue(Language.ES,
        I18nHelper.getValueForLanguage(data.getNombre(), Language.ES));

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
    Set<LineaInvestigacionNombre> nombreLineaInvestigacion = new HashSet<>();
    nombreLineaInvestigacion.add(new LineaInvestigacionNombre(Language.ES, "nombre-" + id));
    return LineaInvestigacion.builder().nombre(nombreLineaInvestigacion).activo(activo).build();
  }
}
