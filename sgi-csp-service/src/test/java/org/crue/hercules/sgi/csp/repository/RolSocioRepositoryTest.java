package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class RolSocioRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private RolSocioRepository repository;

  @Test
  public void findByAbreviaturaAndActivoIsTrue_ReturnsRolSocio() throws Exception {
    // given: data RolSocio with Abreviatura to find
    RolSocio rolSocio1 = generarMockRolSocio("001", Boolean.TRUE);
    generarMockRolSocio("002", Boolean.TRUE);
    generarMockRolSocio("001", Boolean.FALSE);

    // when: find given Abreviatura
    String abreviaturaToFind = rolSocio1.getAbreviatura();
    RolSocio responseData = repository.findByAbreviaturaAndActivoIsTrue(abreviaturaToFind).get();

    // then: RolSocio with given Abreviatura is found
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(rolSocio1.getId());
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()").isEqualTo(abreviaturaToFind);
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(rolSocio1.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo(rolSocio1.getDescripcion());
    Assertions.assertThat(responseData.getCoordinador()).as("getCoordinador()").isEqualTo(rolSocio1.getCoordinador());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Test
  public void findByAbreviaturaAndActivoIsTrue_ReturnsNull() throws Exception {
    // given: Abreviatura to find
    String abreviaturaToFind = "001";

    // when: find given Abreviatura
    Optional<RolSocio> responseData = repository.findByAbreviaturaAndActivoIsTrue(abreviaturaToFind);

    // then: RolSocio with given Abreviatura is not found
    Assertions.assertThat(responseData).isEqualTo(Optional.empty());
  }

  @Test
  public void findByNombreAndActivoIsTrue_ReturnsRolSocio() throws Exception {
    // given: data RolSocio with Nombre to find
    RolSocio rolSocio1 = generarMockRolSocio("001", Boolean.TRUE);
    generarMockRolSocio("002", Boolean.TRUE);
    generarMockRolSocio("001", Boolean.FALSE);

    // when: find given Nombre
    String nombreToFind = rolSocio1.getNombre();
    RolSocio responseData = repository.findByNombreAndActivoIsTrue(nombreToFind).get();

    // then: RolSocio with given Nombre is found
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(rolSocio1.getId());
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()").isEqualTo(rolSocio1.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(nombreToFind);
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo(rolSocio1.getDescripcion());
    Assertions.assertThat(responseData.getCoordinador()).as("getCoordinador()").isEqualTo(rolSocio1.getCoordinador());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Test
  public void findByNombreAndActivoIsTrue_ReturnsNull() throws Exception {
    // given: Nombre to find
    String nombreToFind = "001";

    // when: find given Nombre
    Optional<RolSocio> responseData = repository.findByNombreAndActivoIsTrue(nombreToFind);

    // then: RolSocio with given Nombre is not found
    Assertions.assertThat(responseData).isEqualTo(Optional.empty());
  }

  /**
   * Función que genera RolSocio
   * 
   * @param suffix
   * @return el objeto RolSocio
   */
  private RolSocio generarMockRolSocio(String suffix, Boolean activo) {

    // @formatter:off
    RolSocio rolSocio = RolSocio.builder()
        .abreviatura(suffix)
        .nombre("nombre-" + suffix)
        .descripcion("descripcion-" + suffix)
        .coordinador(Boolean.FALSE)
        .activo(activo)
        .build();
    // @formatter:on
    return entityManager.persistAndFlush(rolSocio);
  }
}
