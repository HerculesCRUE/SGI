package org.crue.hercules.sgi.csp.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumentoDescripcion;
import org.crue.hercules.sgi.csp.model.TipoDocumentoNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * TipoDocumentoRepositoryTest
 */
@DataJpaTest
class TipoDocumentoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private TipoDocumentoRepository repository;

  @Test
  void findByNombreAndActivoIsTrue_ReturnsTipoDocumento() throws Exception {

    // given: 2 TipoDocumento de los que 1 coincide con el nombre buscado
    Set<TipoDocumentoNombre> nombreTipoDocumento1 = new HashSet<>();
    nombreTipoDocumento1.add(new TipoDocumentoNombre(Language.ES, "nombre-tipoDocumento1"));
    Set<TipoDocumentoDescripcion> descripcionTipoDocumento1 = new HashSet<>();
    descripcionTipoDocumento1.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-tipoDocumento1"));
    TipoDocumento tipoDocumento1 = new TipoDocumento(null, nombreTipoDocumento1, descripcionTipoDocumento1, true);
    entityManager.persistAndFlush(tipoDocumento1);

    Set<TipoDocumentoNombre> nombreTipoDocumento2 = new HashSet<>();
    nombreTipoDocumento2.add(new TipoDocumentoNombre(Language.ES, "nombre-tipoDocumento2"));
    Set<TipoDocumentoDescripcion> descripcionTipoDocumento2 = new HashSet<>();
    descripcionTipoDocumento2.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-tipoDocumento2"));
    TipoDocumento tipoDocumento2 = new TipoDocumento(null, nombreTipoDocumento2, descripcionTipoDocumento2, true);
    entityManager.persistAndFlush(tipoDocumento2);

    Set<TipoDocumentoNombre> nombreTipoDocumento3 = new HashSet<>();
    nombreTipoDocumento3.add(new TipoDocumentoNombre(Language.ES, "nombre-tipoDocumento1"));
    Set<TipoDocumentoDescripcion> descripcionTipoDocumento3 = new HashSet<>();
    descripcionTipoDocumento3.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-tipoDocumento1"));
    TipoDocumento tipoDocumento3 = new TipoDocumento(null, nombreTipoDocumento3, descripcionTipoDocumento3,
        false);
    entityManager.persistAndFlush(tipoDocumento3);

    String nombreBuscado = "nombre-tipoDocumento1";

    // when: se busca el TipoDocumentopor nombre
    TipoDocumento tipoDocumentoEncontrado = repository
        .findByNombreLangAndNombreValueAndActivoIsTrue(Language.ES, nombreBuscado).get();

    // then: Se recupera el TipoDocumento con el nombre buscado
    Assertions.assertThat(tipoDocumentoEncontrado.getId()).as("getId").isNotNull();
    Assertions.assertThat(tipoDocumentoEncontrado.getNombre()).as("getNombre").isEqualTo(tipoDocumento1.getNombre());
    Assertions.assertThat(tipoDocumentoEncontrado.getDescripcion()).as("getDescripcion")
        .isEqualTo(tipoDocumento1.getDescripcion());
    Assertions.assertThat(tipoDocumentoEncontrado.getActivo()).as("getActivo").isEqualTo(Boolean.TRUE);
  }

  @Test
  void findByNombreAndActivoIsTrueNoExiste_ReturnsNull() throws Exception {

    // given: 2 TipoDocumento que no coinciden con el nombre buscado
    Set<TipoDocumentoNombre> nombreTipoDocumento1 = new HashSet<>();
    nombreTipoDocumento1.add(new TipoDocumentoNombre(Language.ES, "nombre-tipoDocumento1"));
    Set<TipoDocumentoDescripcion> descripcionTipoDocumento1 = new HashSet<>();
    descripcionTipoDocumento1.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-tipoDocumento1"));
    TipoDocumento tipoDocumento1 = new TipoDocumento(null, nombreTipoDocumento1, descripcionTipoDocumento1, true);
    entityManager.persistAndFlush(tipoDocumento1);

    Set<TipoDocumentoNombre> nombreTipoDocumento2 = new HashSet<>();
    nombreTipoDocumento2.add(new TipoDocumentoNombre(Language.ES, "nombre-tipoDocumento"));
    Set<TipoDocumentoDescripcion> descripcionTipoDocumento2 = new HashSet<>();
    descripcionTipoDocumento2.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-tipoDocumento2"));
    TipoDocumento tipoDocumento2 = new TipoDocumento(null, nombreTipoDocumento2, descripcionTipoDocumento2, true);
    entityManager.persistAndFlush(tipoDocumento2);

    String nombreBuscado = "nombre-tipoDocumento-noexiste";

    // when: se busca el TipoDocumento por nombre
    Optional<TipoDocumento> tipoDocumentoEncontrado = repository
        .findByNombreLangAndNombreValueAndActivoIsTrue(Language.ES, nombreBuscado);

    // then: Se recupera el TipoDocumento con el nombre buscado
    Assertions.assertThat(tipoDocumentoEncontrado).isEqualTo(Optional.empty());
  }

}