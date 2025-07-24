package org.crue.hercules.sgi.csp.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlaceDescripcion;
import org.crue.hercules.sgi.csp.model.TipoEnlaceNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class TipoEnlaceRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private TipoEnlaceRepository repository;

  @Test
  void findByNombreAndActivoIsTrue_ReturnsTipoEnlace() throws Exception {
    // given: data TipoEnlace with nombre to find
    TipoEnlace data = generarMockTipoEnlace(1L, Boolean.TRUE);
    entityManager.persistAndFlush(data);
    entityManager.persistAndFlush(generarMockTipoEnlace(2L, Boolean.TRUE));
    entityManager.persistAndFlush(generarMockTipoEnlace(1L, Boolean.FALSE));

    // when: find given nombre
    TipoEnlaceNombre nombre = data.getNombre().iterator().next();
    TipoEnlace dataFound = repository.findByNombreLangAndNombreValueAndActivoIsTrue(nombre.getLang(), nombre.getValue())
        .get();

    // then: TipoEnlace with given name is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(data.getId());
    Assertions.assertThat(dataFound.getNombre()).isEqualTo(data.getNombre());
    Assertions.assertThat(dataFound.getDescripcion()).isEqualTo(data.getDescripcion());
    Assertions.assertThat(dataFound.getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Test
  void findByNombreAndActivoIsTrue_ReturnsNull() throws Exception {
    // given: data TipoEnlace with nombre to find
    TipoEnlace data = generarMockTipoEnlace(1L, Boolean.TRUE);
    entityManager.persistAndFlush(generarMockTipoEnlace(2L, Boolean.TRUE));
    entityManager.persistAndFlush(generarMockTipoEnlace(1L, Boolean.FALSE));

    // when: find given nombre
    TipoEnlaceNombre nombre = data.getNombre().iterator().next();
    Optional<TipoEnlace> dataFound = repository
        .findByNombreLangAndNombreValueAndActivoIsTrue(nombre.getLang(), nombre.getValue());

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
    Set<TipoEnlaceNombre> nombre = new HashSet<>();
    nombre.add(new TipoEnlaceNombre(Language.ES, "nombre-" + id));

    Set<TipoEnlaceDescripcion> descripcion = new HashSet<>();
    descripcion.add(new TipoEnlaceDescripcion(Language.ES, "descripcion-" + id));

    return TipoEnlace.builder().nombre(nombre).descripcion(descripcion).activo(activo).build();
  }
}
