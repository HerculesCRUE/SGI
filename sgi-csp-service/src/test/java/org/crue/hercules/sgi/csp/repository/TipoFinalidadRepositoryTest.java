package org.crue.hercules.sgi.csp.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidadDescripcion;
import org.crue.hercules.sgi.csp.model.TipoFinalidadNombre;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class TipoFinalidadRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private TipoFinalidadRepository repository;

  @Test
  void findByNombreAndActivoIsTrue_ReturnsTipoFinalidad() throws Exception {
    // given: data TipoFinalidad with nombre to find
    TipoFinalidad data = generarMockTipoFinalidad(1L, Boolean.TRUE);
    entityManager.persistAndFlush(data);
    entityManager.persistAndFlush(generarMockTipoFinalidad(2L, Boolean.TRUE));
    entityManager.persistAndFlush(generarMockTipoFinalidad(1L, Boolean.FALSE));

    // when: find given nombre
    TipoFinalidad dataFound = repository.findByNombreLangAndNombreValueAndActivoIsTrue(
        Language.ES,
        I18nHelper.getValueForLanguage(data.getNombre(), Language.ES))
        .get();

    // then: TipoFinalidad with given name is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(data.getId());
    Assertions.assertThat(dataFound.getNombre()).isEqualTo(data.getNombre());
    Assertions.assertThat(dataFound.getDescripcion()).isEqualTo(data.getDescripcion());
    Assertions.assertThat(dataFound.getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Test
  void findByNombreAndActivoIsTrue_ReturnsNull() throws Exception {
    // given: data TipoFinalidad with nombre to find
    TipoFinalidad data = generarMockTipoFinalidad(1L, Boolean.TRUE);
    entityManager.persistAndFlush(generarMockTipoFinalidad(2L, Boolean.TRUE));
    entityManager.persistAndFlush(generarMockTipoFinalidad(3L, Boolean.TRUE));

    // when: find given nombre
    Optional<TipoFinalidad> dataFound = repository.findByNombreLangAndNombreValueAndActivoIsTrue(
        Language.ES,
        I18nHelper.getValueForLanguage(data.getNombre(), Language.ES));
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
    Set<TipoFinalidadNombre> nombreTipoFinalidad = new HashSet<>();
    nombreTipoFinalidad.add(new TipoFinalidadNombre(Language.ES, "nombre-" + id));

    Set<TipoFinalidadDescripcion> descripcionTipoFinalidad = new HashSet<>();
    descripcionTipoFinalidad.add(new TipoFinalidadDescripcion(Language.ES, "descripcion-" + id));

    return TipoFinalidad.builder()
        .nombre(nombreTipoFinalidad)
        .descripcion(descripcionTipoFinalidad)
        .activo(activo)
        .build();
  }
}
