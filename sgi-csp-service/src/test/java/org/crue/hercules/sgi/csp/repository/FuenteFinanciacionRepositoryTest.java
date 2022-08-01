package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * FuenteFinanciacionRepositoryTest
 */
@DataJpaTest
class FuenteFinanciacionRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private FuenteFinanciacionRepository repository;

  @Test
  void findByNombreAndActivoIsTrue_ReturnsFuenteFinanciacion() throws Exception {
    // given: 2 FuenteFinanciacion de los que 1 coincide con el nombre buscado
    TipoAmbitoGeografico tipoAmbitoGeografico = TipoAmbitoGeografico.builder().nombre("nombre-1").activo(true).build();
    entityManager.persistAndFlush(tipoAmbitoGeografico);

    TipoOrigenFuenteFinanciacion tipoOrigenFuenteFinanciacion = TipoOrigenFuenteFinanciacion.builder()
        .nombre("nombre-1").activo(true).build();
    entityManager.persistAndFlush(tipoOrigenFuenteFinanciacion);

    FuenteFinanciacion fuenteFinanciacion1 = FuenteFinanciacion.builder().nombre("nombre-1")
        .descripcion("descripcion-1").fondoEstructural(true).tipoAmbitoGeografico(tipoAmbitoGeografico)
        .tipoOrigenFuenteFinanciacion(tipoOrigenFuenteFinanciacion).activo(true).build();
    entityManager.persistAndFlush(fuenteFinanciacion1);

    FuenteFinanciacion fuenteFinanciacion2 = FuenteFinanciacion.builder().nombre("nombre-2")
        .descripcion("descripcion-2").fondoEstructural(true).tipoAmbitoGeografico(tipoAmbitoGeografico)
        .tipoOrigenFuenteFinanciacion(tipoOrigenFuenteFinanciacion).activo(true).build();
    entityManager.persistAndFlush(fuenteFinanciacion2);

    String nombreBuscado = "nombre-1";

    // when: se busca el FuenteFinanciacion nombre
    FuenteFinanciacion fuenteFinanciacionEncontrado = repository.findByNombreAndActivoIsTrue(nombreBuscado).get();

    // then: Se recupera el FuenteFinanciacion con el nombre buscado
    Assertions.assertThat(fuenteFinanciacionEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(fuenteFinanciacionEncontrado.getNombre()).as("getNombre")
        .isEqualTo(fuenteFinanciacion1.getNombre());
    Assertions.assertThat(fuenteFinanciacionEncontrado.getDescripcion()).as("getDescripcion")
        .isEqualTo(fuenteFinanciacion1.getDescripcion());
    Assertions.assertThat(fuenteFinanciacionEncontrado.getFondoEstructural()).as("getFondoEstructural")
        .isEqualTo(fuenteFinanciacion1.getFondoEstructural());
    Assertions.assertThat(fuenteFinanciacionEncontrado.getTipoAmbitoGeografico().getId())
        .as("getTipoAmbitoGeografico().getId()").isEqualTo(fuenteFinanciacion1.getTipoAmbitoGeografico().getId());
    Assertions.assertThat(fuenteFinanciacionEncontrado.getTipoOrigenFuenteFinanciacion().getId())
        .as("getTipoOrigenFuenteFinanciacion().getId()")
        .isEqualTo(fuenteFinanciacion1.getTipoOrigenFuenteFinanciacion().getId());
    Assertions.assertThat(fuenteFinanciacionEncontrado.getActivo()).as("getActivo")
        .isEqualTo(fuenteFinanciacion1.getActivo());
  }

  @Test
  void findByNombreAndActivoIsTrue_WithNombreNoExiste_ReturnsNull() throws Exception {
    // given: 2 FuenteFinanciacion que no coinciden con el nombre buscado
    TipoAmbitoGeografico tipoAmbitoGeografico = TipoAmbitoGeografico.builder().nombre("nombre-1").activo(true).build();
    entityManager.persistAndFlush(tipoAmbitoGeografico);

    TipoOrigenFuenteFinanciacion tipoOrigenFuenteFinanciacion = TipoOrigenFuenteFinanciacion.builder()
        .nombre("nombre-1").activo(true).build();
    entityManager.persistAndFlush(tipoOrigenFuenteFinanciacion);

    FuenteFinanciacion fuenteFinanciacion1 = FuenteFinanciacion.builder().nombre("nombre-1")
        .descripcion("descripcion-1").fondoEstructural(true).tipoAmbitoGeografico(tipoAmbitoGeografico)
        .tipoOrigenFuenteFinanciacion(tipoOrigenFuenteFinanciacion).activo(true).build();
    entityManager.persistAndFlush(fuenteFinanciacion1);

    FuenteFinanciacion fuenteFinanciacion2 = FuenteFinanciacion.builder().nombre("nombre-2")
        .descripcion("descripcion-2").fondoEstructural(true).tipoAmbitoGeografico(tipoAmbitoGeografico)
        .tipoOrigenFuenteFinanciacion(tipoOrigenFuenteFinanciacion).activo(true).build();
    entityManager.persistAndFlush(fuenteFinanciacion2);

    String nombreBuscado = "nombre-noexiste";

    // when: se busca el FuenteFinanciacion por nombre
    Optional<FuenteFinanciacion> fuenteFinanciacionEncontrado = repository.findByNombreAndActivoIsTrue(nombreBuscado);

    // then: No hay ningun FuenteFinanciacion con el nombre buscado
    Assertions.assertThat(fuenteFinanciacionEncontrado).isEqualTo(Optional.empty());
  }

}