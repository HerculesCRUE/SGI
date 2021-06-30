package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class RolProyectoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private RolProyectoRepository repository;

  @Test
  public void findByAbreviaturaAndActivoIsTrue_ReturnsRolProyecto() throws Exception {
    // given: data RolProyecto with Abreviatura to find
    RolProyecto rolProyecto1 = generarMockRolProyecto("001", Boolean.TRUE);
    generarMockRolProyecto("002", Boolean.TRUE);
    generarMockRolProyecto("001", Boolean.FALSE);

    // when: find given Abreviatura
    String abreviaturaToFind = rolProyecto1.getAbreviatura();
    RolProyecto responseData = repository.findByAbreviaturaAndActivoIsTrue(abreviaturaToFind).get();

    // then: RolProyecto with given Abreviatura is found
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(rolProyecto1.getId());
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()").isEqualTo(abreviaturaToFind);
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(rolProyecto1.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()")
        .isEqualTo(rolProyecto1.getDescripcion());
    Assertions.assertThat(responseData.getRolPrincipal()).as("getRolPrincipal()")
        .isEqualTo(rolProyecto1.getRolPrincipal());
    Assertions.assertThat(responseData.getOrden()).as("getOrden()").isEqualTo(rolProyecto1.getOrden());
    Assertions.assertThat(responseData.getEquipo()).as("getEquipo()").isEqualTo(rolProyecto1.getEquipo());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Test
  public void findByAbreviaturaAndActivoIsTrue_ReturnsNull() throws Exception {
    // given: Abreviatura to find
    String abreviaturaToFind = "001";

    // when: find given Abreviatura
    Optional<RolProyecto> responseData = repository.findByAbreviaturaAndActivoIsTrue(abreviaturaToFind);

    // then: RolProyecto with given Abreviatura is not found
    Assertions.assertThat(responseData).isEqualTo(Optional.empty());
  }

  @Test
  public void findByNombreAndActivoIsTrue_ReturnsRolProyecto() throws Exception {
    // given: data RolProyecto with Nombre to find
    RolProyecto rolProyecto1 = generarMockRolProyecto("001", Boolean.TRUE);
    generarMockRolProyecto("002", Boolean.TRUE);
    generarMockRolProyecto("001", Boolean.FALSE);

    // when: find given Nombre
    String nombreToFind = rolProyecto1.getNombre();
    RolProyecto responseData = repository.findByNombreAndActivoIsTrue(nombreToFind).get();

    // then: RolProyecto with given Nombre is found
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(rolProyecto1.getId());
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()")
        .isEqualTo(rolProyecto1.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(nombreToFind);
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()")
        .isEqualTo(rolProyecto1.getDescripcion());
    Assertions.assertThat(responseData.getRolPrincipal()).as("getRolPrincipal()")
        .isEqualTo(rolProyecto1.getRolPrincipal());
    Assertions.assertThat(responseData.getOrden()).as("getOrden()").isEqualTo(rolProyecto1.getOrden());
    Assertions.assertThat(responseData.getEquipo()).as("getEquipo()").isEqualTo(rolProyecto1.getEquipo());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Test
  public void findByNombreAndActivoIsTrue_ReturnsNull() throws Exception {
    // given: Nombre to find
    String nombreToFind = "001";

    // when: find given Nombre
    Optional<RolProyecto> responseData = repository.findByNombreAndActivoIsTrue(nombreToFind);

    // then: RolProyecto with given Nombre is not found
    Assertions.assertThat(responseData).isEqualTo(Optional.empty());
  }

  /**
   * Función que genera RolProyecto
   * 
   * @param suffix
   * @return el objeto RolProyecto
   */
  private RolProyecto generarMockRolProyecto(String suffix, Boolean activo) {

    // @formatter:off
    RolProyecto rolProyecto = RolProyecto.builder()
        .abreviatura(suffix)
        .nombre("nombre-" + suffix)
        .descripcion("descripcion-" + suffix)
        .rolPrincipal(Boolean.FALSE)
        .orden(null)
        .equipo(RolProyecto.Equipo.INVESTIGACION)
        .activo(activo)
        .build();
    // @formatter:on
    return entityManager.persistAndFlush(rolProyecto);
  }
}
