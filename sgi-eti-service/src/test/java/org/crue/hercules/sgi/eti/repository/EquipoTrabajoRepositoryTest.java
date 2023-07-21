package org.crue.hercules.sgi.eti.repository;

import java.time.Instant;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.EquipoTrabajoWithIsEliminable;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class EquipoTrabajoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private EquipoTrabajoRepository repository;

  @Test
  public void findAllByPeticionEvaluacionId_WithPaging_ReturnsPage() throws Exception {

    // given: Hay 2 equipoTrabajo para la 1ª peticion evaluacion
    TipoActividad tipoActividad = generarMockTipoActividad();
    TipoActividad tipoActividadCreado = entityManager.persistFlushFind(tipoActividad);

    PeticionEvaluacion peticionEvaluacion1 = generarMockPeticionEvaluacion(tipoActividadCreado);
    PeticionEvaluacion peticionEvaluacion1Creada = entityManager.persistFlushFind(peticionEvaluacion1);

    PeticionEvaluacion peticionEvaluacion2 = generarMockPeticionEvaluacion(tipoActividadCreado);
    PeticionEvaluacion peticionEvaluacion2Creada = entityManager.persistFlushFind(peticionEvaluacion2);

    EquipoTrabajo equipoTrabajo1 = generarMockEquipoTrabajo(peticionEvaluacion1Creada);
    entityManager.persistFlushFind(equipoTrabajo1);

    EquipoTrabajo equipoTrabajo2 = generarMockEquipoTrabajo(peticionEvaluacion1Creada);
    entityManager.persistFlushFind(equipoTrabajo2);

    EquipoTrabajo equipoTrabajo3 = generarMockEquipoTrabajo(peticionEvaluacion2Creada);
    entityManager.persistFlushFind(equipoTrabajo3);

    // when: Se buscan los datos
    List<EquipoTrabajoWithIsEliminable> result = repository.findAllByPeticionEvaluacionId(peticionEvaluacion1.getId());

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isNotEmpty();
    Assertions.assertThat(result.size()).isEqualTo(2);
  }

  @Test
  public void findAllByPeticionEvaluacionId_WithPaging_ReturnsEmptyPage() throws Exception {

    // given: Hay 0 equipoTrabajo para la 2ª peticion evaluacion
    TipoActividad tipoActividad = generarMockTipoActividad();
    TipoActividad tipoActividadCreado = entityManager.persistFlushFind(tipoActividad);

    PeticionEvaluacion peticionEvaluacion1 = generarMockPeticionEvaluacion(tipoActividadCreado);
    PeticionEvaluacion peticionEvaluacion1Creada = entityManager.persistFlushFind(peticionEvaluacion1);

    PeticionEvaluacion peticionEvaluacion2 = generarMockPeticionEvaluacion(tipoActividadCreado);
    entityManager.persistFlushFind(peticionEvaluacion2);

    EquipoTrabajo equipoTrabajo1 = generarMockEquipoTrabajo(peticionEvaluacion1Creada);
    entityManager.persistFlushFind(equipoTrabajo1);

    EquipoTrabajo equipoTrabajo2 = generarMockEquipoTrabajo(peticionEvaluacion1Creada);
    entityManager.persistFlushFind(equipoTrabajo2);

    // when: Se buscan los datos
    List<EquipoTrabajoWithIsEliminable> result = repository.findAllByPeticionEvaluacionId(peticionEvaluacion2.getId());

    // then: Se recuperan los datos correctamente
    Assertions.assertThat(result).isEmpty();
  }

  /**
   * Función que devuelve un objeto TipoActividad
   * 
   * @return el objeto TipoActividad
   */
  public TipoActividad generarMockTipoActividad() {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    return tipoActividad;
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   *
   * @param tipoActividad el tipo de actividad de la PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */
  public PeticionEvaluacion generarMockPeticionEvaluacion(TipoActividad tipoActividad) {

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setCodigo("Codigo");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico");
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos("Objetivos");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("Titulo");
    peticionEvaluacion.setPersonaRef("user-00");
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
  }

  /**
   * Función que devuelve un objeto EquipoTrabajo
   * 
   * @param peticionEvaluacion la PeticionEvaluacion del EquipoTrabajo
   * @return el objeto EquipoTrabajo
   */
  public EquipoTrabajo generarMockEquipoTrabajo(PeticionEvaluacion peticionEvaluacion) {
    EquipoTrabajo equipoTrabajo = new EquipoTrabajo();
    equipoTrabajo.setPeticionEvaluacion(peticionEvaluacion);
    equipoTrabajo.setPersonaRef("user-00");

    return equipoTrabajo;
  }

}