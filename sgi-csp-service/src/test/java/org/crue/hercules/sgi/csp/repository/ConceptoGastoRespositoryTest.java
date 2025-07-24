package org.crue.hercules.sgi.csp.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConceptoGastoDescripcion;
import org.crue.hercules.sgi.csp.model.ConceptoGastoNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ConceptoGastoRepositoryTest
 */
@DataJpaTest
class ConceptoGastoRespositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConceptoGastoRepository repository;

  @Test
  void findByNombreAndActivoIsTrue_ReturnsConceptoGasto() throws Exception {
    // given: 2 ConceptoGasto de los que 1 coincide con el nombre buscado
    Set<ConceptoGastoNombre> nombreConceptoGasto1 = new HashSet<>();
    nombreConceptoGasto1.add(new ConceptoGastoNombre(Language.ES, "nombre-1"));
    Set<ConceptoGastoDescripcion> descripcionConceptoGasto1 = new HashSet<>();
    descripcionConceptoGasto1.add(new ConceptoGastoDescripcion(Language.ES, "descripcion-1"));
    ConceptoGasto conceptoGasto1 = new ConceptoGasto(null, nombreConceptoGasto1, descripcionConceptoGasto1, true, true);
    entityManager.persistAndFlush(conceptoGasto1);

    Set<ConceptoGastoNombre> nombreConceptoGasto2 = new HashSet<>();
    nombreConceptoGasto2.add(new ConceptoGastoNombre(Language.ES, "nombre-2"));
    Set<ConceptoGastoDescripcion> descripcionConceptoGasto2 = new HashSet<>();
    descripcionConceptoGasto2.add(new ConceptoGastoDescripcion(Language.ES, "descripcion-2"));
    ConceptoGasto conceptoGasto2 = new ConceptoGasto(null, nombreConceptoGasto2, descripcionConceptoGasto2, true,
        false);
    entityManager.persistAndFlush(conceptoGasto2);

    String nombreBuscado = "nombre-1";

    // when: se busca el ConceptoGasto nombre
    ConceptoGasto conceptoGastoEncontrado = repository
        .findByNombreLangAndNombreValueAndActivoIsTrue(Language.ES, nombreBuscado).get();

    // then: Se recupera el ConceptoGasto con el nombre buscado
    Assertions.assertThat(conceptoGastoEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(conceptoGastoEncontrado.getNombre()).as("getNombre").isEqualTo(conceptoGasto1.getNombre());
    Assertions.assertThat(conceptoGastoEncontrado.getDescripcion()).as("getDescripcion")
        .isEqualTo(conceptoGasto1.getDescripcion());
    Assertions.assertThat(conceptoGastoEncontrado.getActivo()).as("getActivo").isEqualTo(conceptoGasto1.getActivo());
  }

  @Test
  void findByNombreAndActivoIsTrue_NoExiste_ReturnsNull() throws Exception {
    // given: 2 ConceptoGasto que no coinciden con el nombre buscado
    Set<ConceptoGastoNombre> nombreConceptoGasto1 = new HashSet<>();
    nombreConceptoGasto1.add(new ConceptoGastoNombre(Language.ES, "nombre-1"));
    Set<ConceptoGastoDescripcion> descripcionConceptoGasto1 = new HashSet<>();
    descripcionConceptoGasto1.add(new ConceptoGastoDescripcion(Language.ES, "descripcion-1"));
    ConceptoGasto conceptoGasto1 = new ConceptoGasto(null, nombreConceptoGasto1, descripcionConceptoGasto1, true, true);
    entityManager.persistAndFlush(conceptoGasto1);

    Set<ConceptoGastoNombre> nombreConceptoGasto2 = new HashSet<>();
    nombreConceptoGasto2.add(new ConceptoGastoNombre(Language.ES, "nombre-2"));
    Set<ConceptoGastoDescripcion> descripcionConceptoGasto2 = new HashSet<>();
    descripcionConceptoGasto2.add(new ConceptoGastoDescripcion(Language.ES, "descripcion-2"));
    ConceptoGasto conceptoGasto2 = new ConceptoGasto(null, nombreConceptoGasto2, descripcionConceptoGasto2, true,
        false);
    entityManager.persistAndFlush(conceptoGasto2);

    String nombreBuscado = "nombre-noexiste";

    // when: se busca el ConceptoGasto por nombre
    Optional<ConceptoGasto> conceptoGastoEncontrado = repository
        .findByNombreLangAndNombreValueAndActivoIsTrue(Language.ES, nombreBuscado);

    // then: No hay ningun ConceptoGasto con el nombre buscado
    Assertions.assertThat(conceptoGastoEncontrado).isEqualTo(Optional.empty());
  }

}
