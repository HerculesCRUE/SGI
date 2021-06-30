package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ConceptoGastoRepositoryTest
 */
@DataJpaTest
public class ConceptoGastoRespositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConceptoGastoRepository repository;

  @Test
  public void findByNombreAndActivoIsTrue_ReturnsConceptoGasto() throws Exception {
    // given: 2 ConceptoGasto de los que 1 coincide con el nombre buscado
    ConceptoGasto conceptoGasto1 = new ConceptoGasto(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(conceptoGasto1);

    ConceptoGasto conceptoGasto2 = new ConceptoGasto(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(conceptoGasto2);

    String nombreBuscado = "nombre-1";

    // when: se busca el ConceptoGasto nombre
    ConceptoGasto conceptoGastoEncontrado = repository.findByNombreAndActivoIsTrue(nombreBuscado).get();

    // then: Se recupera el ConceptoGasto con el nombre buscado
    Assertions.assertThat(conceptoGastoEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(conceptoGastoEncontrado.getNombre()).as("getNombre").isEqualTo(conceptoGasto1.getNombre());
    Assertions.assertThat(conceptoGastoEncontrado.getDescripcion()).as("getDescripcion")
        .isEqualTo(conceptoGasto1.getDescripcion());
    Assertions.assertThat(conceptoGastoEncontrado.getActivo()).as("getActivo").isEqualTo(conceptoGasto1.getActivo());
  }

  @Test
  public void findByNombreAndActivoIsTrue_NoExiste_ReturnsNull() throws Exception {
    // given: 2 ConceptoGasto que no coinciden con el nombre buscado
    ConceptoGasto conceptoGasto1 = new ConceptoGasto(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(conceptoGasto1);

    ConceptoGasto conceptoGasto2 = new ConceptoGasto(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(conceptoGasto2);

    String nombreBuscado = "nombre-noexiste";

    // when: se busca el ConceptoGasto por nombre
    Optional<ConceptoGasto> conceptoGastoEncontrado = repository.findByNombreAndActivoIsTrue(nombreBuscado);

    // then: No hay ningun ConceptoGasto con el nombre buscado
    Assertions.assertThat(conceptoGastoEncontrado).isEqualTo(Optional.empty());
  }

}
