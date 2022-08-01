package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * RequisitoIPRepositoryTest
 */
@DataJpaTest
class RequisitoIPRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private RequisitoIPRepository repository;

  @Test
  void findByConvocatoriaId_ReturnsRequisitoIP() throws Exception {

    // given: 2 RequisitoIP de los que 1 coincide con el idConvocatoria buscado
    Convocatoria convocatoria = entityManager.persistAndFlush(generarMockConvocatoria(1L));
    RequisitoIP requisitoIP1 = generarMockRequisitoIP(1L, convocatoria.getId());
    entityManager.persistAndFlush(requisitoIP1);

    Convocatoria convocatoria2 = entityManager.persistAndFlush(generarMockConvocatoria(2L));
    RequisitoIP requisitoIP2 = generarMockRequisitoIP(2L, convocatoria2.getId());
    entityManager.persistAndFlush(requisitoIP2);

    Long convocatoriaIdBuscada = convocatoria.getId();

    // when: se busca el RequisitoIP por el idConvocatoria
    RequisitoIP requisitoIPEncontrado = repository.findById(convocatoriaIdBuscada).get();

    // then: Se recupera el RequisitoIP con el idConvocatoria buscado
    Assertions.assertThat(requisitoIPEncontrado.getId()).as("getId").isNotNull();
  }

  @Test
  void findByConvocatoriaNoExiste_ReturnsNull() throws Exception {

    // given: 2 RequisitoIP que no coinciden con el idConvocatoria buscado
    Convocatoria convocatoria = entityManager.persistAndFlush(generarMockConvocatoria(1L));
    RequisitoIP requisitoIP1 = generarMockRequisitoIP(1L, convocatoria.getId());
    entityManager.persistAndFlush(requisitoIP1);

    RequisitoIP requisitoIP2 = generarMockRequisitoIP(2L, 2L);

    Long convocatoriaIdBuscada = requisitoIP2.getId();

    // when: se busca el RequisitoIP por idConvocatoria que no existe
    Optional<RequisitoIP> requisitoIPEncontrado = repository.findById(convocatoriaIdBuscada);

    // then: Se recupera el RequisitoIP con el idConvocatoria buscado
    Assertions.assertThat(requisitoIPEncontrado).isEqualTo(Optional.empty());
  }

  private Convocatoria generarMockConvocatoria(Long index) {
    // @formatter:off
    Convocatoria convocatoria = Convocatoria.builder()
        .estado(Convocatoria.Estado.BORRADOR)
        .codigo("codigo-00" + index)
        .unidadGestionRef("2")
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo")
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on
    return convocatoria;
  }

  /**
   * Función que devuelve un objeto RequisitoIP
   * 
   * @param id identificador
   * @return el objeto RequisitoIP
   */
  private RequisitoIP generarMockRequisitoIP(Long id, Long convocatoriaId) {
    RequisitoIP requisitoIP = new RequisitoIP();
    requisitoIP.setId(convocatoriaId);
    requisitoIP.setSexoRef("Hombre");
    return requisitoIP;
  }

}
