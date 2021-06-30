package org.crue.hercules.sgi.eti.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ComiteRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ComiteRepository repository;

  @Test
  public void findByIdAndActivoTrue_ReturnsData() throws Exception {

    // given: Datos existentes para el comité activo

    Formulario formulario = entityManager.persistFlushFind(generarMockFormulario());
    Comite comite = entityManager.persistFlushFind(generarMockComite(formulario));

    // when: Se buscan los datos
    Optional<Comite> result = repository.findByIdAndActivoTrue(comite.getId());

    // then: Se recuperan los datos correctamente
    Assertions.assertThat(result.get()).isNotNull();

  }

  /**
   * Función que devuelve un objeto Formulario
   * 
   * @return el objeto Formulario
   */
  public Formulario generarMockFormulario() {
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    return formulario;
  }

  /**
   * Función que devuelve un objeto Comite
   * 
   * @param formulario el formulario
   * @return el objeto Comite
   */
  public Comite generarMockComite(Formulario formulario) {
    return new Comite(null, "Comite1", formulario, Boolean.TRUE);
  }

}