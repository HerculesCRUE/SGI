package org.crue.hercules.sgi.csp.repository;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ModeloTipoDocumentoRepositoryTest
 */
@DataJpaTest
public class ModeloTipoDocumentoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ModeloTipoDocumentoRepository repository;

  @Test
  public void findByModeloEjecucionIdAndTipoDocumentoId_ReturnsModeloTipoDocumentoList() throws Exception {
    // given: 2 ModeloTipoDocumento de los que 1 coincide con los ids de
    // ModeloEjecucion y TipoDocumento
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(modeloEjecucion1);

    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(modeloEjecucion2);

    TipoDocumento tipoDocumento1 = new TipoDocumento(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(tipoDocumento1);

    TipoDocumento tipoDocumento2 = new TipoDocumento(null, "nombre-2", "descripcion-2", true);
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
  public void findByModeloEjecucionIdAndTipoDocumentoId_NoExiste_ReturnsEmptyList() throws Exception {
    // given: 2 ModeloTipoDocumento que no coinciden con los ids de
    // ModeloEjecucion y TipoDocumento
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(modeloEjecucion1);

    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(modeloEjecucion2);

    TipoDocumento tipoDocumento1 = new TipoDocumento(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(tipoDocumento1);

    TipoDocumento tipoDocumento2 = new TipoDocumento(null, "nombre-2", "descripcion-2", true);
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
    Assertions.assertThat(modeloTipoEnlaceEncontrados.size()).isEqualTo(0);
  }

  @Test
  public void findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId_ReturnsModeloTipoDocumento()
      throws Exception {
    // given: 2 ModeloTipoDocumento de los que 1 coincide con los ids de
    // ModeloEjecucion, ModeloTipoFase y TipoDocumento
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(modeloEjecucion1);

    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(modeloEjecucion2);

    TipoFase tipoFase1 = new TipoFase(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(tipoFase1);

    TipoFase tipoFase2 = new TipoFase(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(tipoFase2);

    ModeloTipoFase modeloTipoFase1 = new ModeloTipoFase(null, tipoFase1, modeloEjecucion1, true, true, true, true);
    entityManager.persistAndFlush(modeloTipoFase1);

    ModeloTipoFase modeloTipoFase2 = new ModeloTipoFase(null, tipoFase2, modeloEjecucion2, true, true, true, true);
    entityManager.persistAndFlush(modeloTipoFase2);

    TipoDocumento tipoDocumento1 = new TipoDocumento(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(tipoDocumento1);

    TipoDocumento tipoDocumento2 = new TipoDocumento(null, "nombre-2", "descripcion-2", true);
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
  public void findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId_NoExiste_ReturnsEmptyList()
      throws Exception {
    // given: 2 ModeloTipoDocumento que no coinciden con los ids de
    // ModeloEjecucion, ModeloTipoFase y TipoDocumento
    ModeloEjecucion modeloEjecucion1 = new ModeloEjecucion(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(modeloEjecucion1);

    ModeloEjecucion modeloEjecucion2 = new ModeloEjecucion(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(modeloEjecucion2);

    TipoFase tipoFase1 = new TipoFase(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(tipoFase1);

    TipoFase tipoFase2 = new TipoFase(null, "nombre-2", "descripcion-2", true);
    entityManager.persistAndFlush(tipoFase2);

    ModeloTipoFase modeloTipoFase1 = new ModeloTipoFase(null, tipoFase1, modeloEjecucion1, true, true, true, true);
    entityManager.persistAndFlush(modeloTipoFase1);

    ModeloTipoFase modeloTipoFase2 = new ModeloTipoFase(null, tipoFase2, modeloEjecucion2, true, true, true, true);
    entityManager.persistAndFlush(modeloTipoFase2);

    TipoDocumento tipoDocumento1 = new TipoDocumento(null, "nombre-1", "descripcion-1", true);
    entityManager.persistAndFlush(tipoDocumento1);

    TipoDocumento tipoDocumento2 = new TipoDocumento(null, "nombre-2", "descripcion-2", true);
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
