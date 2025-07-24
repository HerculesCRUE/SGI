package org.crue.hercules.sgi.csp.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionDescripcion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidadDescripcion;
import org.crue.hercules.sgi.csp.model.TipoFinalidadNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ModeloTipoFinalidadRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ModeloTipoFinalidadRepository repository;

  @Test
  void findByModeloEjecucionIdAndTipoFinalidadId_ReturnsModeloTipoFinalidad() throws Exception {

    // given: data ModeloTipoFinalidad to find by ModeloEjecucion and TipoFinalidad
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombre-me-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion1 = new HashSet<>();
    descripcionModeloEjecucion1.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-me-1"));
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion(null, nombreModeloEjecucion, descripcionModeloEjecucion1,
        Boolean.TRUE,
        Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
    entityManager.persistAndFlush(modeloEjecucion);

    Set<TipoFinalidadNombre> nombreTipoFinalidad = new HashSet<>();
    nombreTipoFinalidad.add(new TipoFinalidadNombre(Language.ES, "nombre-tf-1"));

    Set<TipoFinalidadDescripcion> descripcionTipoFinalidad = new HashSet<>();
    descripcionTipoFinalidad.add(new TipoFinalidadDescripcion(Language.ES, "descripcion-tf-1"));

    TipoFinalidad tipoFinalidad = new TipoFinalidad(null, nombreTipoFinalidad, descripcionTipoFinalidad, Boolean.TRUE);
    entityManager.persistAndFlush(tipoFinalidad);
    ModeloTipoFinalidad modeloTipoFinalidad = new ModeloTipoFinalidad(null, tipoFinalidad, modeloEjecucion,
        Boolean.TRUE);
    entityManager.persistAndFlush(modeloTipoFinalidad);

    // when: find by ModeloEjecucion and TipoFinalidad
    ModeloTipoFinalidad dataFound = repository
        .findByModeloEjecucionIdAndTipoFinalidadId(modeloEjecucion.getId(), tipoFinalidad.getId()).get();

    // then: ModeloTipoFinalidad is found
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound.getId()).isEqualTo(modeloTipoFinalidad.getId());
    Assertions.assertThat(dataFound.getModeloEjecucion().getId()).isEqualTo(modeloEjecucion.getId());
    Assertions.assertThat(dataFound.getTipoFinalidad().getId()).isEqualTo(tipoFinalidad.getId());
    Assertions.assertThat(dataFound.getActivo()).isEqualTo(modeloTipoFinalidad.getActivo());
  }

  @Test
  void findByModeloEjecucionIdAndTipoFinalidadId_ReturnsNull() throws Exception {
    // given: data ModeloTipoFinalidad to find by ModeloEjecucion and TipoFinalidad
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombre-me-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion1 = new HashSet<>();
    descripcionModeloEjecucion1.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-me-1"));
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion(null, nombreModeloEjecucion, descripcionModeloEjecucion1,
        Boolean.TRUE,
        Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
    entityManager.persistAndFlush(modeloEjecucion);

    Set<TipoFinalidadNombre> nombreTipoFinalidad = new HashSet<>();
    nombreTipoFinalidad.add(new TipoFinalidadNombre(Language.ES, "nombre-tf-1"));

    Set<TipoFinalidadDescripcion> descripcionTipoFinalidad = new HashSet<>();
    descripcionTipoFinalidad.add(new TipoFinalidadDescripcion(Language.ES, "descripcion-tf-1"));

    TipoFinalidad tipoFinalidad = new TipoFinalidad(null, nombreTipoFinalidad, descripcionTipoFinalidad, Boolean.TRUE);
    entityManager.persistAndFlush(tipoFinalidad);

    // when: find by ModeloEjecucion and TipoFinalidad
    Optional<ModeloTipoFinalidad> dataFound = repository
        .findByModeloEjecucionIdAndTipoFinalidadId(modeloEjecucion.getId(), tipoFinalidad.getId());

    // then: ModeloTipoFinalidad is not found
    Assertions.assertThat(dataFound).isEqualTo(Optional.empty());
  }
}
