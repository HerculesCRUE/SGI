package org.crue.hercules.sgi.eti.repository;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class DictamenRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private DictamenRepository repository;

  @Test
  public void findByIdIn_ReturnsListDictamen() throws Exception {

    // Datos existentes
    TipoEvaluacion tipoEvaluacion = entityManager.persistFlushFind(generarMockTipoEvaluacion());

    List<Dictamen> response = new LinkedList<Dictamen>();
    response.add(entityManager.persist(generarMockDictamen(1L, tipoEvaluacion)));
    response.add(entityManager.persist(generarMockDictamen(2L, tipoEvaluacion)));

    List<Long> ids = Lists.newArrayList(1L, 2L);

    // when: Se buscan los datos
    List<Dictamen> result = repository.findByIdIn(ids);

    // then: Se comprueba que se recuperan los registros 1 y 2
    Assertions.assertThat(result.size()).isEqualTo(response.size());
    Assertions.assertThat(result.equals(response));

  }

  @Test
  public void findByTipoEvaluacionId_ReturnsListDictamen() throws Exception {

    // Datos existentes
    TipoEvaluacion tipoEvaluacion = entityManager.persistFlushFind(generarMockTipoEvaluacion());

    List<Dictamen> response = new LinkedList<Dictamen>();
    response.add(entityManager.persist(generarMockDictamen(1L, tipoEvaluacion)));
    response.add(entityManager.persist(generarMockDictamen(2L, tipoEvaluacion)));

    // when: Se buscan los datos
    List<Dictamen> result = repository.findByTipoEvaluacionId(tipoEvaluacion.getId());

    // then: Se comprueba que se recuperan los registros de "TipoEvaluacion"
    Assertions.assertThat(result.size()).isEqualTo(response.size());
    Assertions.assertThat(result.equals(response));

  }

  /**
   * Función que devuelve un objeto Dictamen
   * 
   * @param id
   * @param tipoEvaluacion
   * @return Dictamen
   */
  public Dictamen generarMockDictamen(Long id, TipoEvaluacion tipoEvaluacion) {
    return new Dictamen(id, "Dictamen", tipoEvaluacion, Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoEvaluacion
   * 
   * @return el objeto TipoEvaluacion
   */
  public TipoEvaluacion generarMockTipoEvaluacion() {
    return new TipoEvaluacion(1L, "TipoEvaluacion", Boolean.TRUE);
  }
}