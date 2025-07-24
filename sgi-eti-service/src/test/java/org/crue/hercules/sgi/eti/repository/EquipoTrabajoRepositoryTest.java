package org.crue.hercules.sgi.eti.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.EquipoTrabajoWithIsEliminable;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionDisMetodologico;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionObjetivos;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionResumen;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionTitulo;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.framework.i18n.Language;
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

    Set<PeticionEvaluacionTitulo> titulo = new HashSet<>();
    titulo.add(new PeticionEvaluacionTitulo(Language.ES, "Titulo"));
    Set<PeticionEvaluacionResumen> resumen = new HashSet<>();
    resumen.add(new PeticionEvaluacionResumen(Language.ES, "Resumen"));
    Set<PeticionEvaluacionObjetivos> objetivos = new HashSet<>();
    objetivos.add(new PeticionEvaluacionObjetivos(Language.ES, "Objetivos"));
    Set<PeticionEvaluacionDisMetodologico> disMetodologico = new HashSet<>();
    disMetodologico.add(new PeticionEvaluacionDisMetodologico(Language.ES, "DiseñoMetodologico"));
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setCodigo("Codigo");
    peticionEvaluacion.setDisMetodologico(disMetodologico);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos(objetivos);
    peticionEvaluacion.setResumen(resumen);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(titulo);
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
    equipoTrabajo.setPeticionEvaluacionId(peticionEvaluacion.getId());
    equipoTrabajo.setPersonaRef("user-00");

    return equipoTrabajo;
  }

}