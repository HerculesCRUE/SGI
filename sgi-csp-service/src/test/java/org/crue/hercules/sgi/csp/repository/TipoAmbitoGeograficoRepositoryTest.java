package org.crue.hercules.sgi.csp.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeograficoNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * TipoAmbitoGeograficoRepositoryTest
 */
@DataJpaTest
class TipoAmbitoGeograficoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private TipoAmbitoGeograficoRepository repository;

  @Test
  void findByNombre_ReturnsTipoAmbitoGeografico() throws Exception {
    Set<TipoAmbitoGeograficoNombre> nombre1 = new HashSet<>();
    nombre1.add(new TipoAmbitoGeograficoNombre(Language.ES, "nombre-1"));

    // given: 2 TipoAmbitoGeografico de los que 1 coincide con el nombre buscado
    TipoAmbitoGeografico tipoAmbitoGeografico1 = TipoAmbitoGeografico.builder().nombre(nombre1).activo(true).build();
    entityManager.persistAndFlush(tipoAmbitoGeografico1);
    
    Set<TipoAmbitoGeograficoNombre> nombre2 = new HashSet<>();
    nombre2.add(new TipoAmbitoGeograficoNombre(Language.ES, "nombre-2"));

    TipoAmbitoGeografico tipoAmbitoGeografico2 = TipoAmbitoGeografico.builder().nombre(nombre2).activo(true).build();
    entityManager.persistAndFlush(tipoAmbitoGeografico2);

    String nombreBuscado = "nombre-1";

    // when: se busca el TipoAmbitoGeografico nombre
    TipoAmbitoGeografico tipoAmbitoGeograficoEncontrado = repository.findByNombreValue(nombreBuscado).get();

    // then: Se recupera el TipoAmbitoGeografico con el nombre buscado
    Assertions.assertThat(tipoAmbitoGeograficoEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(tipoAmbitoGeograficoEncontrado.getNombre()).as("getNombre")
        .isEqualTo(tipoAmbitoGeografico1.getNombre());
    Assertions.assertThat(tipoAmbitoGeograficoEncontrado.getActivo()).as("getActivo")
        .isEqualTo(tipoAmbitoGeografico1.getActivo());
  }

  @Test
  void findByNombreNoExiste_ReturnsNull() throws Exception {
    Set<TipoAmbitoGeograficoNombre> nombre1 = new HashSet<>();
    nombre1.add(new TipoAmbitoGeograficoNombre(Language.ES, "nombre-1"));
    
    // given: 2 TipoAmbitoGeografico que no coinciden con el nombre buscado
    TipoAmbitoGeografico tipoAmbitoGeografico1 = TipoAmbitoGeografico.builder().nombre(nombre1).activo(true).build();
    entityManager.persistAndFlush(tipoAmbitoGeografico1);
    
    Set<TipoAmbitoGeograficoNombre> nombre2 = new HashSet<>();
    nombre2.add(new TipoAmbitoGeograficoNombre(Language.ES, "nombre-2"));

    TipoAmbitoGeografico tipoAmbitoGeografico2 = TipoAmbitoGeografico.builder().nombre(nombre2).activo(true).build();
    entityManager.persistAndFlush(tipoAmbitoGeografico2);

    String nombreBuscado = "nombre-noexiste";

    // when: se busca el TipoAmbitoGeografico por nombre
    Optional<TipoAmbitoGeografico> tipoAmbitoGeograficoEncontrado = repository.findByNombreValue(nombreBuscado);

    // then: No hay ningun TipoAmbitoGeografico con el nombre buscado
    Assertions.assertThat(tipoAmbitoGeograficoEncontrado).isEqualTo(Optional.empty());
  }

}
