package org.crue.hercules.sgi.eti.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class TipoEvaluacionRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private TipoEvaluacionRepository repository;

  @Test
  public void findByIdIn_ReturnsList() throws Exception {

    // given:
    TipoEvaluacion tipoEvaluacion1 = generarMockTipoEvaluacion(1L, "Retrospectiva");
    entityManager.persistFlushFind(tipoEvaluacion1);

    TipoEvaluacion tipoEvaluacion2 = generarMockTipoEvaluacion(2L, "Memoria");
    entityManager.persistFlushFind(tipoEvaluacion2);

    List<TipoEvaluacion> listaTipoEvaluacion = new ArrayList<TipoEvaluacion>();
    listaTipoEvaluacion.add(tipoEvaluacion1);
    listaTipoEvaluacion.add(tipoEvaluacion2);

    List<Long> ids = new ArrayList<Long>(Arrays.asList(1L, 2L));
    List<TipoEvaluacion> result = repository.findByActivoTrueAndIdIn(ids);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(listaTipoEvaluacion);
  }

  /**
   * Función que devuelve un objeto TipoEvaluacion
   * 
   * @param id     id del TipoEvaluacion
   * @param nombre nombre del TipoEvaluacion
   * @return el objeto TipoEvaluacion
   */

  public TipoEvaluacion generarMockTipoEvaluacion(Long id, String nombre) {

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(id);
    tipoEvaluacion.setNombre(nombre);
    tipoEvaluacion.setActivo(Boolean.TRUE);

    return tipoEvaluacion;
  }
}