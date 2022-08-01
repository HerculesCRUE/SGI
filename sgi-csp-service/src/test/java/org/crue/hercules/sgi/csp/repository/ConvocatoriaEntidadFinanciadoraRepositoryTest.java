package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ConvocatoriaEntidadFinanciadoraRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ConvocatoriaEntidadFinanciadoraRepository repository;

  @Test
  void findAllByConvocatoriaId_ReturnsListConvocatoriaEntidadFinanciadora() throws Exception {

    // given: 2 ConvocatoriaEntidadFinanciadora de los que 1 coincide con el
    // ConvocatoriaId a buscar
    Convocatoria convocatoria1 = Convocatoria.builder()
    // @formatter:off
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-1")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on
    entityManager.persistAndFlush(convocatoria1);
    Convocatoria convocatoria2 = Convocatoria.builder()
    // @formatter:off
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-2")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on
    entityManager.persistAndFlush(convocatoria2);

    List<ConvocatoriaEntidadFinanciadora> persistList = new ArrayList<>();
    // 5 elements to convocatoria 1
    for (int i = 1; i <= 5; i++) {
      ConvocatoriaEntidadFinanciadora entidad = new ConvocatoriaEntidadFinanciadora(null, convocatoria1.getId(),
          "entidadRef-" + i, null, null, null, null);
      persistList.add(entityManager.persistAndFlush(entidad));
    }
    // 5 elements to convocatoria 2
    for (int i = 1; i <= 5; i++) {
      ConvocatoriaEntidadFinanciadora entidad = new ConvocatoriaEntidadFinanciadora(null, convocatoria2.getId(),
          "entidadRef-" + i, null, null, null, null);
      persistList.add(entityManager.persistAndFlush(entidad));
    }

    Long convocatoriaIdBuscado = convocatoria1.getId();

    // when: se busca el ConvocatoriaEntidadFinanciadora por ConvocatoriaId
    List<ConvocatoriaEntidadFinanciadora> dataFound = repository.findByConvocatoriaId(convocatoriaIdBuscado);

    // then: Se comprueba que los elementos retornados pertenecen al convocatoriaId
    // solicitado
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound).hasSize(5);
    dataFound.stream().forEach((entidad) -> {
      Assertions.assertThat(entidad.getConvocatoriaId()).isEqualTo(convocatoriaIdBuscado);
    });
  }

  @Test
  void findAllByConvocatoriaId_ReturnsEmptyList() throws Exception {
    // given: 2 ConvocatoriaEntidadFinanciadora de los que 1 coincide con el
    // ConvocatoriaId a buscar
    Convocatoria convocatoria1 = Convocatoria.builder()
    // @formatter:off
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-1")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on
    entityManager.persistAndFlush(convocatoria1);
    Convocatoria convocatoria2 = Convocatoria.builder()
    // @formatter:off
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-2")
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on
    entityManager.persistAndFlush(convocatoria2);

    List<ConvocatoriaEntidadFinanciadora> persistList = new ArrayList<>();
    // 5 elements to convocatoria 1
    for (int i = 1; i <= 5; i++) {
      ConvocatoriaEntidadFinanciadora entidad = new ConvocatoriaEntidadFinanciadora(null, convocatoria1.getId(),
          "entidadRef-" + i, null, null, null, null);
      persistList.add(entityManager.persistAndFlush(entidad));
    }
    // 5 elements to convocatoria 2
    for (int i = 1; i <= 5; i++) {
      ConvocatoriaEntidadFinanciadora entidad = new ConvocatoriaEntidadFinanciadora(null, convocatoria2.getId(),
          "entidadRef-" + i, null, null, null, null);
      persistList.add(entityManager.persistAndFlush(entidad));
    }

    Long convocatoriaIdBuscado = 3000L;

    // when: se busca el ConvocatoriaEntidadFinanciadora por ConvocatoriaId
    List<ConvocatoriaEntidadFinanciadora> dataFound = repository.findByConvocatoriaId(convocatoriaIdBuscado);

    // then: Se comprueba que no se obtiene ningún valor y la lista está vacia
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.size()).isZero();
  }
}
