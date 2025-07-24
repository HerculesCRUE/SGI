package org.crue.hercules.sgi.csp.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionDescripcion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumentoDescripcion;
import org.crue.hercules.sgi.csp.model.TipoDocumentoNombre;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFaseDescripcion;
import org.crue.hercules.sgi.csp.model.TipoFaseNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ModeloTipoDocumentoRepositoryTest
 */
@DataJpaTest
class ModeloTipoDocumentoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ModeloTipoDocumentoRepository repository;

  @Test
  void findByModeloEjecucionIdAndTipoDocumentoId_ReturnsModeloTipoDocumentoList() throws Exception {
    // given: 2 ModeloTipoDocumento de los que 1 coincide con los ids de
    // ModeloEjecucion y TipoDocumento
    Set<ModeloEjecucionNombre> nombreModeloEjecucion1 = new HashSet<>();
    nombreModeloEjecucion1.add(new ModeloEjecucionNombre(Language.ES, "nombre-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion1 = new HashSet<>();
    descripcionModeloEjecucion1.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-1"));
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, nombreModeloEjecucion1, descripcionModeloEjecucion1,
        true, false,
        false,
        false);
    entityManager.persistAndFlush(modeloEjecucion1);

    Set<ModeloEjecucionNombre> nombreModeloEjecucion2 = new HashSet<>();
    nombreModeloEjecucion2.add(new ModeloEjecucionNombre(Language.ES, "nombre-2"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion2 = new HashSet<>();
    descripcionModeloEjecucion2.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-2"));
    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, nombreModeloEjecucion2, descripcionModeloEjecucion2,
        true, false,
        false,
        false);
    entityManager.persistAndFlush(modeloEjecucion2);

    Set<TipoDocumentoNombre> nombreTipoDocumento1 = new HashSet<>();
    nombreTipoDocumento1.add(new TipoDocumentoNombre(Language.ES, "nombre-1"));
    Set<TipoDocumentoDescripcion> descripcionTipoDocumento1 = new HashSet<>();
    descripcionTipoDocumento1.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-1"));
    TipoDocumento tipoDocumento1 = new TipoDocumento(null, nombreTipoDocumento1, descripcionTipoDocumento1, true);
    entityManager.persistAndFlush(tipoDocumento1);

    Set<TipoDocumentoNombre> nombreTipoDocumento2 = new HashSet<>();
    nombreTipoDocumento2.add(new TipoDocumentoNombre(Language.ES, "nombre-2"));
    Set<TipoDocumentoDescripcion> descripcionTipoDocumento2 = new HashSet<>();
    descripcionTipoDocumento2.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-2"));
    TipoDocumento tipoDocumento2 = new TipoDocumento(null, nombreTipoDocumento2, descripcionTipoDocumento2, true);
    entityManager.persistAndFlush(tipoDocumento2);

    ModeloTipoDocumento modeloTipoDocumento1 = new ModeloTipoDocumento(null, tipoDocumento1, modeloEjecucion1, null,
        true);
    entityManager.persistAndFlush(modeloTipoDocumento1);

    ModeloTipoDocumento modeloTipoDocumento2 = new ModeloTipoDocumento(null, tipoDocumento2, modeloEjecucion2, null,
        true);
    entityManager.persistAndFlush(modeloTipoDocumento2);

    Long idModeloEjecucionBuscado = modeloEjecucion1.getId();
    Long idTipoDocumentoBuscado = tipoDocumento1.getId();

    // when: se busca el ModeloTipoDocumento
    List<ModeloTipoDocumento> modeloTipoDocumentoEncontrados = repository
        .findByModeloEjecucionIdAndTipoDocumentoId(idModeloEjecucionBuscado, idTipoDocumentoBuscado);

    // then: Se recupera el ModeloTipoDocumento buscado
    Assertions.assertThat(modeloTipoDocumentoEncontrados.size()).as("size()").isEqualTo(1);
    Assertions.assertThat(modeloTipoDocumentoEncontrados.get(0).getId()).as("getId(").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoEncontrados.get(0).getModeloEjecucion().getId())
        .as("getModeloEjecucion().getId()").isEqualTo(modeloTipoDocumento1.getModeloEjecucion().getId());
    Assertions.assertThat(modeloTipoDocumentoEncontrados.get(0).getTipoDocumento().getId())
        .as("getTipoDocumento().getId()").isEqualTo(modeloTipoDocumento1.getTipoDocumento().getId());
    Assertions.assertThat(modeloTipoDocumentoEncontrados.get(0).getActivo()).as("getActivo()")
        .isEqualTo(modeloEjecucion1.getActivo());
  }

  @Test
  void findByModeloEjecucionIdAndTipoDocumentoId_NoExiste_ReturnsEmptyList() throws Exception {
    // given: 2 ModeloTipoDocumento que no coinciden con los ids de
    // ModeloEjecucion y TipoDocumento
    Set<ModeloEjecucionNombre> nombreModeloEjecucion1 = new HashSet<>();
    nombreModeloEjecucion1.add(new ModeloEjecucionNombre(Language.ES, "nombre-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion1 = new HashSet<>();
    descripcionModeloEjecucion1.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-1"));
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, nombreModeloEjecucion1, descripcionModeloEjecucion1,
        true, false,
        false,
        false);
    entityManager.persistAndFlush(modeloEjecucion1);

    Set<ModeloEjecucionNombre> nombreModeloEjecucion2 = new HashSet<>();
    nombreModeloEjecucion2.add(new ModeloEjecucionNombre(Language.ES, "nombre-2"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion2 = new HashSet<>();
    descripcionModeloEjecucion2.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-2"));
    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, nombreModeloEjecucion2, descripcionModeloEjecucion2,
        true, false,
        false,
        false);
    entityManager.persistAndFlush(modeloEjecucion2);

    Set<TipoDocumentoNombre> nombreTipoDocumento1 = new HashSet<>();
    nombreTipoDocumento1.add(new TipoDocumentoNombre(Language.ES, "nombre-1"));
    Set<TipoDocumentoDescripcion> descripcionTipoDocumento1 = new HashSet<>();
    descripcionTipoDocumento1.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-1"));
    TipoDocumento tipoDocumento1 = new TipoDocumento(null, nombreTipoDocumento1, descripcionTipoDocumento1, true);
    entityManager.persistAndFlush(tipoDocumento1);

    Set<TipoDocumentoNombre> nombreTipoDocumento2 = new HashSet<>();
    nombreTipoDocumento2.add(new TipoDocumentoNombre(Language.ES, "nombre-2"));
    Set<TipoDocumentoDescripcion> descripcionTipoDocumento2 = new HashSet<>();
    descripcionTipoDocumento2.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-2"));
    TipoDocumento tipoDocumento2 = new TipoDocumento(null, nombreTipoDocumento2, descripcionTipoDocumento2, true);
    entityManager.persistAndFlush(tipoDocumento2);

    ModeloTipoDocumento modeloTipoDocumento1 = new ModeloTipoDocumento(null, tipoDocumento1, modeloEjecucion1, null,
        true);
    entityManager.persistAndFlush(modeloTipoDocumento1);

    ModeloTipoDocumento modeloTipoDocumento2 = new ModeloTipoDocumento(null, tipoDocumento2, modeloEjecucion2, null,
        true);
    entityManager.persistAndFlush(modeloTipoDocumento2);

    Long idModeloEjecucionBuscado = modeloEjecucion2.getId();
    Long idTipoDocumentoBuscado = tipoDocumento1.getId();

    // when: se busca el ModeloTipoDocumento
    List<ModeloTipoDocumento> modeloTipoEnlaceEncontrados = repository
        .findByModeloEjecucionIdAndTipoDocumentoId(idModeloEjecucionBuscado, idTipoDocumentoBuscado);

    // then: No se recupera el ModeloTipoDocumento buscado
    Assertions.assertThat(modeloTipoEnlaceEncontrados).hasSize(0);
  }

  @Test
  void findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId_ReturnsModeloTipoDocumento()
      throws Exception {
    // given: 2 ModeloTipoDocumento de los que 1 coincide con los ids de
    // ModeloEjecucion, ModeloTipoFase y TipoDocumento
    Set<ModeloEjecucionNombre> nombreModeloEjecucion1 = new HashSet<>();
    nombreModeloEjecucion1.add(new ModeloEjecucionNombre(Language.ES, "nombre-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion1 = new HashSet<>();
    descripcionModeloEjecucion1.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-1"));
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, nombreModeloEjecucion1, descripcionModeloEjecucion1,
        true, false,
        false,
        false);
    entityManager.persistAndFlush(modeloEjecucion1);

    Set<ModeloEjecucionNombre> nombreModeloEjecucion2 = new HashSet<>();
    nombreModeloEjecucion2.add(new ModeloEjecucionNombre(Language.ES, "nombre-2"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion2 = new HashSet<>();
    descripcionModeloEjecucion2.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-2"));
    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, nombreModeloEjecucion2, descripcionModeloEjecucion2,
        true, false,
        false,
        false);
    entityManager.persistAndFlush(modeloEjecucion2);

    Set<TipoFaseNombre> nombreTipoFase1 = new HashSet<>();
    nombreTipoFase1.add(new TipoFaseNombre(Language.ES, "nombre-1"));

    Set<TipoFaseDescripcion> descripcionTipoFase1 = new HashSet<>();
    descripcionTipoFase1.add(new TipoFaseDescripcion(Language.ES, "descripcion-1"));

    TipoFase tipoFase1 = new TipoFase(null, nombreTipoFase1, descripcionTipoFase1, true);
    entityManager.persistAndFlush(tipoFase1);

    Set<TipoFaseNombre> nombreTipoFase2 = new HashSet<>();
    nombreTipoFase2.add(new TipoFaseNombre(Language.ES, "nombre-2"));

    Set<TipoFaseDescripcion> descripcionTipoFase2 = new HashSet<>();
    descripcionTipoFase2.add(new TipoFaseDescripcion(Language.ES, "descripcion-2"));

    TipoFase tipoFase2 = new TipoFase(null, nombreTipoFase2, descripcionTipoFase2, true);
    entityManager.persistAndFlush(tipoFase2);

    ModeloTipoFase modeloTipoFase1 = new ModeloTipoFase(null, tipoFase1, modeloEjecucion1, true, true, true, true);
    entityManager.persistAndFlush(modeloTipoFase1);

    ModeloTipoFase modeloTipoFase2 = new ModeloTipoFase(null, tipoFase2, modeloEjecucion2, true, true, true, true);
    entityManager.persistAndFlush(modeloTipoFase2);

    Set<TipoDocumentoNombre> nombreTipoDocumento1 = new HashSet<>();
    nombreTipoDocumento1.add(new TipoDocumentoNombre(Language.ES, "nombre-1"));
    Set<TipoDocumentoDescripcion> descripcionTipoDocumento1 = new HashSet<>();
    descripcionTipoDocumento1.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-1"));
    TipoDocumento tipoDocumento1 = new TipoDocumento(null, nombreTipoDocumento1, descripcionTipoDocumento1, true);
    entityManager.persistAndFlush(tipoDocumento1);

    Set<TipoDocumentoNombre> nombreTipoDocumento2 = new HashSet<>();
    nombreTipoDocumento2.add(new TipoDocumentoNombre(Language.ES, "nombre-2"));
    Set<TipoDocumentoDescripcion> descripcionTipoDocumento2 = new HashSet<>();
    descripcionTipoDocumento2.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-2"));
    TipoDocumento tipoDocumento2 = new TipoDocumento(null, nombreTipoDocumento2, descripcionTipoDocumento2, true);
    entityManager.persistAndFlush(tipoDocumento2);

    ModeloTipoDocumento modeloTipoDocumento1 = new ModeloTipoDocumento(null, tipoDocumento1, modeloEjecucion1,
        modeloTipoFase1, true);
    entityManager.persistAndFlush(modeloTipoDocumento1);

    ModeloTipoDocumento modeloTipoDocumento2 = new ModeloTipoDocumento(null, tipoDocumento2, modeloEjecucion2,
        modeloTipoFase2, true);
    entityManager.persistAndFlush(modeloTipoDocumento2);

    Long idModeloEjecucionBuscado = modeloEjecucion1.getId();
    Long idModeloTipoFaseBuscado = modeloTipoFase1.getId();
    Long idTipoDocumentoBuscado = tipoDocumento1.getId();

    // when: se busca el ModeloTipoDocumento
    ModeloTipoDocumento modeloTipoDocumentoEncontrado = repository
        .findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(idModeloEjecucionBuscado, idModeloTipoFaseBuscado,
            idTipoDocumentoBuscado)
        .get();

    // then: Se recupera el ModeloTipoDocumento buscado
    Assertions.assertThat(modeloTipoDocumentoEncontrado.getId()).as("getId(").isNotNull();
    Assertions.assertThat(modeloTipoDocumentoEncontrado.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(modeloTipoDocumento1.getModeloEjecucion().getId());
    Assertions.assertThat(modeloTipoDocumentoEncontrado.getModeloTipoFase().getId()).as("getModeloTipoFase().getId()")
        .isEqualTo(modeloTipoDocumento1.getModeloTipoFase().getId());
    Assertions.assertThat(modeloTipoDocumentoEncontrado.getTipoDocumento().getId()).as("getTipoDocumento().getId()")
        .isEqualTo(modeloTipoDocumento1.getTipoDocumento().getId());
    Assertions.assertThat(modeloTipoDocumentoEncontrado.getActivo()).as("getActivo()")
        .isEqualTo(modeloTipoDocumento1.getActivo());
  }

  @Test
  void findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId_NoExiste_ReturnsEmptyList()
      throws Exception {
    // given: 2 ModeloTipoDocumento que no coinciden con los ids de
    // ModeloEjecucion, ModeloTipoFase y TipoDocumento
    Set<ModeloEjecucionNombre> nombreModeloEjecucion1 = new HashSet<>();
    nombreModeloEjecucion1.add(new ModeloEjecucionNombre(Language.ES, "nombre-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion1 = new HashSet<>();
    descripcionModeloEjecucion1.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-1"));
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, nombreModeloEjecucion1, descripcionModeloEjecucion1,
        true, false,
        false,
        false);
    entityManager.persistAndFlush(modeloEjecucion1);

    Set<ModeloEjecucionNombre> nombreModeloEjecucion2 = new HashSet<>();
    nombreModeloEjecucion2.add(new ModeloEjecucionNombre(Language.ES, "nombre-2"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion2 = new HashSet<>();
    descripcionModeloEjecucion2.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-2"));
    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, nombreModeloEjecucion2, descripcionModeloEjecucion2,
        true, false,
        false,
        false);
    entityManager.persistAndFlush(modeloEjecucion2);

    Set<TipoFaseNombre> nombreTipoFase1 = new HashSet<>();
    nombreTipoFase1.add(new TipoFaseNombre(Language.ES, "nombre-1"));

    Set<TipoFaseDescripcion> descripcionTipoFase1 = new HashSet<>();
    descripcionTipoFase1.add(new TipoFaseDescripcion(Language.ES, "descripcion-1"));

    TipoFase tipoFase1 = new TipoFase(null, nombreTipoFase1, descripcionTipoFase1, true);
    entityManager.persistAndFlush(tipoFase1);

    Set<TipoFaseNombre> nombreTipoFase2 = new HashSet<>();
    nombreTipoFase2.add(new TipoFaseNombre(Language.ES, "nombre-2"));

    Set<TipoFaseDescripcion> descripcionTipoFase2 = new HashSet<>();
    descripcionTipoFase2.add(new TipoFaseDescripcion(Language.ES, "descripcion-2"));

    TipoFase tipoFase2 = new TipoFase(null, nombreTipoFase2, descripcionTipoFase2, true);
    entityManager.persistAndFlush(tipoFase2);

    ModeloTipoFase modeloTipoFase1 = new ModeloTipoFase(null, tipoFase1, modeloEjecucion1, true, true, true, true);
    entityManager.persistAndFlush(modeloTipoFase1);

    ModeloTipoFase modeloTipoFase2 = new ModeloTipoFase(null, tipoFase2, modeloEjecucion2, true, true, true, true);
    entityManager.persistAndFlush(modeloTipoFase2);

    Set<TipoDocumentoNombre> nombreTipoDocumento1 = new HashSet<>();
    nombreTipoDocumento1.add(new TipoDocumentoNombre(Language.ES, "nombre-1"));
    Set<TipoDocumentoDescripcion> descripcionTipoDocumento1 = new HashSet<>();
    descripcionTipoDocumento1.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-1"));
    TipoDocumento tipoDocumento1 = new TipoDocumento(null, nombreTipoDocumento1, descripcionTipoDocumento1, true);
    entityManager.persistAndFlush(tipoDocumento1);

    Set<TipoDocumentoNombre> nombreTipoDocumento2 = new HashSet<>();
    nombreTipoDocumento2.add(new TipoDocumentoNombre(Language.ES, "nombre-2"));
    Set<TipoDocumentoDescripcion> descripcionTipoDocumento2 = new HashSet<>();
    descripcionTipoDocumento2.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-2"));
    TipoDocumento tipoDocumento2 = new TipoDocumento(null, nombreTipoDocumento2, descripcionTipoDocumento2, true);
    entityManager.persistAndFlush(tipoDocumento2);

    ModeloTipoDocumento modeloTipoDocumento1 = new ModeloTipoDocumento(null, tipoDocumento1, modeloEjecucion1,
        modeloTipoFase1, true);
    entityManager.persistAndFlush(modeloTipoDocumento1);

    ModeloTipoDocumento modeloTipoDocumento2 = new ModeloTipoDocumento(null, tipoDocumento2, modeloEjecucion2,
        modeloTipoFase2, true);
    entityManager.persistAndFlush(modeloTipoDocumento2);

    Long idModeloEjecucionBuscado = modeloEjecucion2.getId();
    Long idModeloTipoFaseBuscado = modeloTipoFase1.getId();
    Long idTipoDocumentoBuscado = tipoDocumento2.getId();

    // when: se busca el ModeloTipoDocumento
    Optional<ModeloTipoDocumento> modeloTipoEnlaceEncontrados = repository
        .findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(idModeloEjecucionBuscado, idModeloTipoFaseBuscado,
            idTipoDocumentoBuscado);

    // then: No se recupera el ModeloTipoDocumento buscado
    Assertions.assertThat(modeloTipoEnlaceEncontrados).isEqualTo(Optional.empty());
  }
}
