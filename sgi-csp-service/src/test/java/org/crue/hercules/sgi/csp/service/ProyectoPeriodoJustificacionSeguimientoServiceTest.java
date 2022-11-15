package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.SeguimientoJustificacionAnualidad;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoJustificacionSeguimientoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacionSeguimiento;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoJustificacionSeguimientoRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

/**
 * ProyectoPeriodoJustificacionSeguimientoServiceTest
 */
@Import({ ProyectoPeriodoJustificacionSeguimientoService.class, ApplicationContextSupport.class })
public class ProyectoPeriodoJustificacionSeguimientoServiceTest extends BaseServiceTest {

  @MockBean
  private ProyectoPeriodoJustificacionSeguimientoRepository proyectoPeriodoJustificacionSeguimientoRepository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @Autowired
  private ProyectoPeriodoJustificacionSeguimientoService proyectoPeriodoJustificacionSeguimientoService;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void findSeguimientosJustificacionAnualidadByProyectoSgeRef_ReturnsList() {
    // given: 20 SeguimientoJustificacionAnualidad
    String proyectoSgeRef = "1234";
    List<SeguimientoJustificacionAnualidad> seguimientosJustificacionAnualidadList = new ArrayList<>();
    for (long i = 1; i <= 20; i++) {
      seguimientosJustificacionAnualidadList
          .add(generarMockSeguimientoJustificacionAnualidad(i));
    }

    BDDMockito.given(
        proyectoPeriodoJustificacionSeguimientoRepository
            .findSeguimientosJustificacionAnualidadByProyectoSgeRef(ArgumentMatchers.anyString()))
        .willReturn(seguimientosJustificacionAnualidadList);

    // when: Get
    List<SeguimientoJustificacionAnualidad> list = proyectoPeriodoJustificacionSeguimientoService
        .findSeguimientosJustificacionAnualidadByProyectoSgeRef(proyectoSgeRef);

    // then: A List with 20 SeguimientoJustificacionAnualidad are returned
    // containing
    // justificanteReintegro='identificador-justificacion--001' to
    // 'identificador-justificacion-020'
    Assertions.assertThat(list).hasSize(20);
    for (int i = 0; i < 20; i++) {
      SeguimientoJustificacionAnualidad seguimientoJustificacionAnualidad = list.get(i);
      Assertions.assertThat(seguimientoJustificacionAnualidad.getIdentificadorJustificacion())
          .isEqualTo("identificador-justificacion-" + String.format("%03d", i + 1));
    }
  }

  @Test
  void findById_WithIdNull_ThrowsIllegalArgumentException() {
    // given: id null
    Long id = null;
    // when: Buscamos por id null
    // then: Lanza IllegalArgumentException porque el id no puede ser null
    Assertions.assertThatThrownBy(() -> proyectoPeriodoJustificacionSeguimientoService.findById(id))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void findById_WithNotExistingId_ThrowsProyectoPeriodoJustificacionSeguimientoNotFoundException() {
    // given: id no existente
    Long id = 1234L;
    // when: Buscamos por un id que no existe
    // then: Lanza ProyectoPeriodoJustificacionSeguimientoNotFoundException porque
    // no existe un
    // ProyectoPeriodoJustificacionSeguimiento con dicho id
    Assertions.assertThatThrownBy(() -> proyectoPeriodoJustificacionSeguimientoService.findById(id)).isInstanceOf(
        ProyectoPeriodoJustificacionSeguimientoNotFoundException.class);
  }

  @Test
  void findById_ReturnsProyectoPeriodoJustificacionSeguimiento() {
    // given: id existente
    Long id = 1234L;

    BDDMockito.given(proyectoPeriodoJustificacionSeguimientoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockProyectoPeriodoJustificacionSeguimiento(id)));
    // when: Buscamos por un id que existe
    ProyectoPeriodoJustificacionSeguimiento proyectoPeriodoJustificacionSeguimiento = proyectoPeriodoJustificacionSeguimientoService
        .findById(id);

    // then: Devuelve el RequerimientoJustificacion con dicho id
    Assertions.assertThat(proyectoPeriodoJustificacionSeguimiento).isNotNull();
    Assertions.assertThat(proyectoPeriodoJustificacionSeguimiento.getId()).as("getId()").isEqualTo(id);
  }

  @Test
  void create_idNotNull_ThrowsIllegalArgumentException() {
    // given: Un ProyectoPeriodoJustificacionSeguimiento con un id no null
    ProyectoPeriodoJustificacionSeguimiento proyectoPeriodoJustificacionSeguimientoToCreate = generarMockProyectoPeriodoJustificacionSeguimiento(
        1L);

    // when: Creamos un ProyectoPeriodoJustificacionSeguimiento con id no null
    // then: Lanza IllegalArgumentException porque el id debe ser null
    Assertions
        .assertThatThrownBy(
            () -> proyectoPeriodoJustificacionSeguimientoService
                .create(proyectoPeriodoJustificacionSeguimientoToCreate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void create_ReturnsProyectoPeriodoJustificacionSeguimiento() {
    // given: Un ProyectoPeriodoJustificacionSeguimiento con un id null
    Long id = 1L;
    ProyectoPeriodoJustificacionSeguimiento proyectoPeriodoJustificacionSeguimientoToCreate = generarMockProyectoPeriodoJustificacionSeguimiento(
        null);
    ProyectoPeriodoJustificacionSeguimiento proyectoPeriodoJustificacionSeguimientoCreated = generarMockProyectoPeriodoJustificacionSeguimiento(
        id);

    // when: Creamos un ProyectoPeriodoJustificacionSeguimiento con id null
    BDDMockito
        .given(
            proyectoPeriodoJustificacionSeguimientoRepository
                .save(ArgumentMatchers.<ProyectoPeriodoJustificacionSeguimiento>any()))
        .willReturn(proyectoPeriodoJustificacionSeguimientoCreated);

    // then: ProyectoPeriodoJustificacionSeguimiento creado correctamente
    ProyectoPeriodoJustificacionSeguimiento proyectoSeguimientoJustificacion = proyectoPeriodoJustificacionSeguimientoService
        .create(proyectoPeriodoJustificacionSeguimientoToCreate);
    Assertions.assertThat(proyectoSeguimientoJustificacion).isNotNull();
    Assertions.assertThat(proyectoSeguimientoJustificacion.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(proyectoSeguimientoJustificacion.getJustificanteReintegro()).as("getJustificanteReintegro()")
        .isEqualTo(
            "ProyectoPeriodoJustificacionSeguimiento-" + String.format("%03d", id));
  }

  @Test
  void update_idIsNull_ThrowsIllegalArgumentException() {
    // given: Un ProyectoPeriodoJustificacionSeguimiento con un id null
    ProyectoPeriodoJustificacionSeguimiento proyectoPeriodoJustificacionSeguimientoToUpdate = generarMockProyectoPeriodoJustificacionSeguimiento(
        null);

    // when: Actualizamos un ProyectoPeriodoJustificacionSeguimiento con id null
    // then: Lanza IllegalArgumentException porque el id no debe ser null
    Assertions
        .assertThatThrownBy(
            () -> proyectoPeriodoJustificacionSeguimientoService
                .update(proyectoPeriodoJustificacionSeguimientoToUpdate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_idNotFound_ThrowsProyectoPeriodoJustificacionSeguimientoNotFoundException() {
    // given: Un ProyectoPeriodoJustificacionSeguimiento con un id not found
    ProyectoPeriodoJustificacionSeguimiento proyectoPeriodoJustificacionSeguimientoToUpdate = generarMockProyectoPeriodoJustificacionSeguimiento(
        999L);

    // when: Actualizamos un ProyectoPeriodoJustificacionSeguimiento con id not
    // found
    BDDMockito.given(proyectoPeriodoJustificacionSeguimientoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());
    // then: Lanza ProyectoPeriodoJustificacionSeguimientoNotFoundException porque
    // el id debe existir
    Assertions
        .assertThatThrownBy(
            () -> proyectoPeriodoJustificacionSeguimientoService
                .update(proyectoPeriodoJustificacionSeguimientoToUpdate))
        .isInstanceOf(ProyectoPeriodoJustificacionSeguimientoNotFoundException.class);
  }

  @Test
  void update_ReturnsProyectoPeriodoJustificacionSeguimiento() {
    // given: Un ProyectoPeriodoJustificacionSeguimiento con datos correctos
    Long id = 123L;
    String justificanteReintegroUpdated = "Updated";
    ProyectoPeriodoJustificacionSeguimiento proyectoPeriodoJustificacionSeguimientoToUpdate = generarMockProyectoPeriodoJustificacionSeguimiento(
        id, justificanteReintegroUpdated);
    ProyectoPeriodoJustificacionSeguimiento proyectoPeriodoJustificacionSeguimientoOnDB = generarMockProyectoPeriodoJustificacionSeguimiento(
        id);

    BDDMockito.given(proyectoPeriodoJustificacionSeguimientoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoPeriodoJustificacionSeguimientoOnDB));
    BDDMockito
        .given(
            proyectoPeriodoJustificacionSeguimientoRepository
                .save(ArgumentMatchers.<ProyectoPeriodoJustificacionSeguimiento>any()))
        .willReturn(proyectoPeriodoJustificacionSeguimientoToUpdate);

    // when: Actualizamos un ProyectoPeriodoJustificacionSeguimiento con datos
    // correctos
    // then: Se actualiza correctamente
    ProyectoPeriodoJustificacionSeguimiento proyectoSeguimientoJustificacion = proyectoPeriodoJustificacionSeguimientoService
        .update(proyectoPeriodoJustificacionSeguimientoToUpdate);
    Assertions.assertThat(proyectoSeguimientoJustificacion).isNotNull();
    Assertions.assertThat(proyectoSeguimientoJustificacion.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(proyectoSeguimientoJustificacion.getJustificanteReintegro()).as("getJustificanteReintegro()")
        .isEqualTo(justificanteReintegroUpdated);
  }

  private SeguimientoJustificacionAnualidad generarMockSeguimientoJustificacionAnualidad(Long index) {
    String justificanteReintegroSuffix = index != null ? String.format("%03d", index) : String.format("%03d", 1);
    return generarMockSeguimientoJustificacionAnualidad(1L, 1L, 1L,
        "identificador-justificacion-" + justificanteReintegroSuffix);
  }

  private SeguimientoJustificacionAnualidad generarMockSeguimientoJustificacionAnualidad(
      Long proyectoId, Long proyectoPeriodoJustificacionId, Long proyectoPeriodoJustificacionSeguimientoId,
      String identificadorJustificacion) {
    return SeguimientoJustificacionAnualidad.builder()
        .proyectoId(proyectoId)
        .proyectoPeriodoJustificacionId(proyectoPeriodoJustificacionId)
        .proyectoPeriodoJustificacionSeguimientoId(proyectoPeriodoJustificacionSeguimientoId)
        .identificadorJustificacion(identificadorJustificacion)
        .build();
  }

  private ProyectoPeriodoJustificacionSeguimiento generarMockProyectoPeriodoJustificacionSeguimiento(Long id) {
    String justificanteReintegroSuffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockProyectoPeriodoJustificacionSeguimiento(id,
        "ProyectoPeriodoJustificacionSeguimiento-" + justificanteReintegroSuffix, 1L, 1L);
  }

  private ProyectoPeriodoJustificacionSeguimiento generarMockProyectoPeriodoJustificacionSeguimiento(Long id,
      String justificanteReintegro) {
    return generarMockProyectoPeriodoJustificacionSeguimiento(
        id, justificanteReintegro, 1L, 1L);
  }

  private ProyectoPeriodoJustificacionSeguimiento generarMockProyectoPeriodoJustificacionSeguimiento(Long id,
      String justificanteReintegro, Long proyectoAnualidadId, Long proyectoPeriodoJustificacionId) {
    return ProyectoPeriodoJustificacionSeguimiento.builder()
        .id(id)
        .justificanteReintegro(justificanteReintegro)
        .proyectoAnualidadId(proyectoAnualidadId)
        .proyectoPeriodoJustificacionId(proyectoPeriodoJustificacionId)
        .build();
  }
}
