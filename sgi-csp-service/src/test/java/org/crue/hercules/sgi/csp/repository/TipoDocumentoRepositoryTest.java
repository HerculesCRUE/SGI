package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * TipoDocumentoRepositoryTest
 */
@DataJpaTest
public class TipoDocumentoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private TipoDocumentoRepository repository;

  @Test
  public void findByNombreAndActivoIsTrue_ReturnsTipoDocumento() throws Exception {

    // given: 2 TipoDocumento de los que 1 coincide con el nombre buscado
    TipoDocumento tipoDocumento1 = new TipoDocumento(null, "nombre-tipoDocumento1", "descripcion-tipoDocumento1", true);
    entityManager.persistAndFlush(tipoDocumento1);

    TipoDocumento tipoDocumento2 = new TipoDocumento(null, "nombre-tipoDocumento2", "descripcion-tipoDocumento2", true);
    entityManager.persistAndFlush(tipoDocumento2);

    TipoDocumento tipoDocumento3 = new TipoDocumento(null, "nombre-tipoDocumento1", "descripcion-tipoDocumento1",
        false);
    entityManager.persistAndFlush(tipoDocumento3);

    String nombreBuscado = "nombre-tipoDocumento1";

    // when: se busca el TipoDocumentopor nombre
    TipoDocumento tipoDocumentoEncontrado = repository.findByNombreAndActivoIsTrue(nombreBuscado).get();

    // then: Se recupera el TipoDocumento con el nombre buscado
    Assertions.assertThat(tipoDocumentoEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(tipoDocumentoEncontrado.getNombre()).as("getNombre").isEqualTo(tipoDocumento1.getNombre());
    Assertions.assertThat(tipoDocumentoEncontrado.getDescripcion()).as("getDescripcion")
        .isEqualTo(tipoDocumento1.getDescripcion());
    Assertions.assertThat(tipoDocumentoEncontrado.getActivo()).as("getActivo").isEqualTo(Boolean.TRUE);
  }

  @Test
  public void findByNombreAndActivoIsTrueNoExiste_ReturnsNull() throws Exception {

    // given: 2 TipoDocumento que no coinciden con el nombre buscado
    TipoDocumento tipoDocumento1 = new TipoDocumento(null, "nombre-tipoDocumento1", "descripcion-tipoDocumento1", true);
    entityManager.persistAndFlush(tipoDocumento1);

    TipoDocumento tipoDocumento2 = new TipoDocumento(null, "nombre-tipoDocumento", "descripcion-tipoDocumento2", true);
    entityManager.persistAndFlush(tipoDocumento2);

    String nombreBuscado = "nombre-tipoDocumento-noexiste";

    // when: se busca el TipoDocumento por nombre
    Optional<TipoDocumento> tipoDocumentoEncontrado = repository.findByNombreAndActivoIsTrue(nombreBuscado);

    // then: Se recupera el TipoDocumento con el nombre buscado
    Assertions.assertThat(tipoDocumentoEncontrado).isEqualTo(Optional.empty());
  }

}