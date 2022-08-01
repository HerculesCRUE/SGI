package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPaqueteTrabajoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.repository.ProyectoPaqueteTrabajoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoPaqueteTrabajoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ProyectoPaqueteTrabajoServiceTest
 */

class ProyectoPaqueteTrabajoServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoPaqueteTrabajoRepository repository;

  @Mock
  private ProyectoRepository proyectoRepository;

  private ProyectoPaqueteTrabajoService service;

  @BeforeEach
  void setUp() throws Exception {
    service = new ProyectoPaqueteTrabajoServiceImpl(repository, proyectoRepository);
  }

  @Test
  void create_ReturnsProyectoPaqueteTrabajo() {
    // given: Un nuevo ProyectoPaqueteTrabajo
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setId(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getPermitePaquetesTrabajo(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(Boolean.TRUE));
    BDDMockito.given(repository.existsProyectoPaqueteTrabajoByProyectoIdAndNombre(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any())).willReturn(Boolean.FALSE);
    BDDMockito
        .given(proyectoRepository.existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Instant>any(), ArgumentMatchers.<Instant>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(repository.save(proyectoPaqueteTrabajo)).will((InvocationOnMock invocation) -> {
      ProyectoPaqueteTrabajo proyectoPaqueteTrabajoCreado = invocation.getArgument(0);
      proyectoPaqueteTrabajoCreado.setId(1L);
      return proyectoPaqueteTrabajoCreado;
    });

    // when: Creamos el ProyectoPaqueteTrabajo
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajoCreado = service.create(proyectoPaqueteTrabajo);

    // then: El ProyectoPaqueteTrabajo se crea correctamente
    Assertions.assertThat(proyectoPaqueteTrabajoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoPaqueteTrabajoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoPaqueteTrabajoCreado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoPaqueteTrabajo.getProyectoId());
    Assertions.assertThat(proyectoPaqueteTrabajoCreado.getNombre()).as("getNombre()")
        .isEqualTo(proyectoPaqueteTrabajo.getNombre());
    Assertions.assertThat(proyectoPaqueteTrabajoCreado.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(proyectoPaqueteTrabajo.getFechaInicio());
    Assertions.assertThat(proyectoPaqueteTrabajoCreado.getFechaFin()).as("getFechaFin()")
        .isEqualTo(proyectoPaqueteTrabajo.getFechaFin());
    Assertions.assertThat(proyectoPaqueteTrabajoCreado.getPersonaMes()).as("getPersonaMes()")
        .isEqualTo(proyectoPaqueteTrabajo.getPersonaMes());
    Assertions.assertThat(proyectoPaqueteTrabajoCreado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(proyectoPaqueteTrabajo.getDescripcion());
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ProyectoPaqueteTrabajo que ya tiene id
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    // when: Creamos el ProyectoPaqueteTrabajo
    // then: Lanza una excepcion porque el ProyectoPaqueteTrabajo ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(proyectoPaqueteTrabajo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ProyectoPaqueteTrabajo id tiene que ser null para crear un nuevo ProyectoPaqueteTrabajo");
  }

  @Test
  void create_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoPaqueteTrabajo without ProyectoId
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setId(null);
    proyectoPaqueteTrabajo.setProyectoId(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoPaqueteTrabajo
        () -> service.create(proyectoPaqueteTrabajo))
        // then: throw exception as ProyectoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Proyecto no puede ser null para realizar la acción sobre ProyectoPaqueteTrabajo");
  }

  @Test
  void create_WithoutTipoPaqueteTrabajoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoPaqueteTrabajo without Nombre
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setId(null);
    proyectoPaqueteTrabajo.setNombre(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoPaqueteTrabajo
        () -> service.create(proyectoPaqueteTrabajo))
        // then: throw exception as Nombre is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Nombre PaqueteTrabajo no puede ser null para realizar la acción sobre ProyectoPaqueteTrabajo");
  }

  @Test
  void create_WithoutFechaInicio_ThrowsIllegalArgumentException() {
    // given: a ProyectoPaqueteTrabajo without FechaInicio
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setId(null);
    proyectoPaqueteTrabajo.setFechaInicio(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoPaqueteTrabajo
        () -> service.create(proyectoPaqueteTrabajo))
        // then: throw exception as FechaInicio is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Fecha inicio no puede ser null para realizar la acción sobre ProyectoPaqueteTrabajo");
  }

  @Test
  void create_WithoutFechaFin_ThrowsIllegalArgumentException() {
    // given: a ProyectoPaqueteTrabajo without FechaFin
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setId(null);
    proyectoPaqueteTrabajo.setFechaFin(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoPaqueteTrabajo
        () -> service.create(proyectoPaqueteTrabajo))
        // then: throw exception as FechaFin is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Fecha fin no puede ser null para realizar la acción sobre ProyectoPaqueteTrabajo");
  }

  @Test
  void create_WithoutPersonaMes_ThrowsIllegalArgumentException() {
    // given: a ProyectoPaqueteTrabajo without PersonaMes
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setId(null);
    proyectoPaqueteTrabajo.setPersonaMes(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoPaqueteTrabajo
        () -> service.create(proyectoPaqueteTrabajo))
        // then: throw exception as PersonaMes is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Persona/Mes debe tener un valor para realizar la acción sobre ProyectoPaqueteTrabajo");
  }

  @Test
  void create_WithFechaInicioGreaterThanFechaFin_ThrowsIllegalArgumentException() {
    // given: Fecha Inicio > fechaFin
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setId(null);
    proyectoPaqueteTrabajo.setFechaInicio(proyectoPaqueteTrabajo.getFechaFin().plus(Period.ofDays(1)));

    // when: create ProyectoPaqueteTrabajo
    Assertions.assertThatThrownBy(() -> service.create(proyectoPaqueteTrabajo))
        // then: throw exception as Inicio > fechaFin
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin debe ser posterior a la fecha de inicio");
  }

  @Test
  void create_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoPaqueteTrabajo with non existing Proyecto
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setId(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: create ProyectoPaqueteTrabajo
        () -> service.create(proyectoPaqueteTrabajo))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  void create_WithProyectoWithPermitePaquetesTrabajoFalse_ThrowsIllegalArgumentException() throws Exception {
    // given: Proyecto with field PermitePaquetesTrabajo = FALSE or NULL
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setId(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getPermitePaquetesTrabajo(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(Boolean.FALSE));

    Assertions.assertThatThrownBy(
        // when: create ProyectoPaqueteTrabajo
        () -> service.create(proyectoPaqueteTrabajo))
        // then: IllegalArgumentException is thrown
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El proyecto no está configurado para utilizar paquetes de trabajo");
  }

  @Test
  void create_WithDuplicatedNombreInProyecto_ThrowsIllegalArgumentException() {
    // given: a ProyectoPaqueteTrabajo with duplicated nombre in proyecto
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setId(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getPermitePaquetesTrabajo(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(Boolean.TRUE));
    BDDMockito.given(repository.existsProyectoPaqueteTrabajoByProyectoIdAndNombre(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: create ProyectoPaqueteTrabajo
        () -> service.create(proyectoPaqueteTrabajo))
        // then: throw exception as nombre is duplicated
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un ProyectoPaqueteTrabajo en el proyecto con el nombre '%s'",
            proyectoPaqueteTrabajo.getNombre());
  }

  @Test
  void create_WithDatesOutsideProyectoRange_ThrowsIllegalArgumentException() {
    // given: a ProyectoPaqueteTrabajo with dates aoutside Proyecto Range
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setId(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getPermitePaquetesTrabajo(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(Boolean.TRUE));
    BDDMockito.given(repository.existsProyectoPaqueteTrabajoByProyectoIdAndNombre(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any())).willReturn(Boolean.FALSE);
    BDDMockito
        .given(proyectoRepository.existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Instant>any(), ArgumentMatchers.<Instant>any()))
        .willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: create ProyectoPaqueteTrabajo
        () -> service.create(proyectoPaqueteTrabajo))
        // then: throw exception as nombre is duplicated
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "El periodo del ProyectoPaqueteTrabajo no se encuentra dentro del rango de fechas definido para el proyecto");
  }

  @Test
  void update_ReturnsProyectoPaqueteTrabajo() {
    // given: Un nuevo ProyectoPaqueteTrabajo con el tipoPaqueteTrabajo actualizado
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajoActualizado = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajoActualizado.setDescripcion("descripcion-modificada");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoPaqueteTrabajo));

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getPermitePaquetesTrabajo(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(Boolean.TRUE));
    BDDMockito.given(repository.existsProyectoPaqueteTrabajoByIdNotAndProyectoIdAndNombre(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any())).willReturn(Boolean.FALSE);
    BDDMockito
        .given(proyectoRepository.existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Instant>any(), ArgumentMatchers.<Instant>any()))
        .willReturn(Boolean.TRUE);

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoPaqueteTrabajo>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ProyectoPaqueteTrabajo
    ProyectoPaqueteTrabajo updated = service.update(proyectoPaqueteTrabajoActualizado);

    // then: El ProyectoPaqueteTrabajo se actualiza correctamente.
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(proyectoPaqueteTrabajo.getId());
    Assertions.assertThat(updated.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoPaqueteTrabajo.getProyectoId());
    Assertions.assertThat(updated.getNombre()).as("getNombre()").isEqualTo(proyectoPaqueteTrabajo.getNombre());
    Assertions.assertThat(updated.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(proyectoPaqueteTrabajo.getFechaInicio());
    Assertions.assertThat(updated.getFechaFin()).as("getFechaFin()").isEqualTo(proyectoPaqueteTrabajo.getFechaFin());
    Assertions.assertThat(updated.getPersonaMes()).as("getPersonaMes()")
        .isEqualTo(proyectoPaqueteTrabajo.getPersonaMes());
    Assertions.assertThat(updated.getDescripcion()).as("getDescripcion()")
        .isEqualTo(proyectoPaqueteTrabajoActualizado.getDescripcion());
  }

  @Test
  void update_WithIdNotExist_ThrowsProyectoPaqueteTrabajoNotFoundException() {
    // given: Un ProyectoPaqueteTrabajo a actualizar con un id que no existe
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el ProyectoPaqueteTrabajo
    // then: Lanza una excepcion porque el ProyectoPaqueteTrabajo no existe
    Assertions.assertThatThrownBy(() -> service.update(proyectoPaqueteTrabajo))
        .isInstanceOf(ProyectoPaqueteTrabajoNotFoundException.class);
  }

  @Test
  void update_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoPaqueteTrabajo without ProyectoId
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setDescripcion("descripcion-modificada");
    proyectoPaqueteTrabajo.setProyectoId(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoPaqueteTrabajo
        () -> service.update(proyectoPaqueteTrabajo))
        // then: throw exception as ProyectoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Proyecto no puede ser null para realizar la acción sobre ProyectoPaqueteTrabajo");
  }

  @Test
  void update_WithoutNombre_ThrowsIllegalArgumentException() {
    // given: a ProyectoPaqueteTrabajo without Nombre
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setDescripcion("descripcion-modificada");
    proyectoPaqueteTrabajo.setNombre(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoPaqueteTrabajo
        () -> service.update(proyectoPaqueteTrabajo))
        // then: throw exception as Nombre is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Nombre PaqueteTrabajo no puede ser null para realizar la acción sobre ProyectoPaqueteTrabajo");
  }

  @Test
  void update_WithoutFechaInicio_ThrowsIllegalArgumentException() {
    // given: a ProyectoPaqueteTrabajo without FechaInicio
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setDescripcion("descripcion-modificada");
    proyectoPaqueteTrabajo.setFechaInicio(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoPaqueteTrabajo
        () -> service.update(proyectoPaqueteTrabajo))
        // then: throw exception as FechaInicio is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Fecha inicio no puede ser null para realizar la acción sobre ProyectoPaqueteTrabajo");
  }

  @Test
  void update_WithoutFechaFin_ThrowsIllegalArgumentException() {
    // given: a ProyectoPaqueteTrabajo without FechaFin
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setDescripcion("descripcion-modificada");
    proyectoPaqueteTrabajo.setFechaFin(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoPaqueteTrabajo
        () -> service.update(proyectoPaqueteTrabajo))
        // then: throw exception as FechaFin is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Fecha fin no puede ser null para realizar la acción sobre ProyectoPaqueteTrabajo");
  }

  @Test
  void update_WithoutPersonaMes_ThrowsIllegalArgumentException() {
    // given: a ProyectoPaqueteTrabajo without FechaFin
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setDescripcion("descripcion-modificada");
    proyectoPaqueteTrabajo.setPersonaMes(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoPaqueteTrabajo
        () -> service.update(proyectoPaqueteTrabajo))
        // then: throw exception as PersonaMes is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Persona/Mes debe tener un valor para realizar la acción sobre ProyectoPaqueteTrabajo");
  }

  @Test
  void update_WithFechaInicioGreaterThanFechaFin_ThrowsIllegalArgumentException() {
    // given: Fecha Inicio > fechaFin
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajoOriginal = generarMockProyectoPaqueteTrabajo(1L, 1L);
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setDescripcion("descripcion-modificada");
    proyectoPaqueteTrabajo.setFechaInicio(proyectoPaqueteTrabajo.getFechaFin().plus(Period.ofDays(1)));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoPaqueteTrabajoOriginal));

    Assertions.assertThatThrownBy(
        // when: update ProyectoPaqueteTrabajo
        () -> service.update(proyectoPaqueteTrabajo))
        // then: throw exception as Inicio > fechaFin
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin debe ser posterior a la fecha de inicio");
  }

  @Test
  void update_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoPaqueteTrabajo with non existing Proyecto
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajoOriginal = generarMockProyectoPaqueteTrabajo(1L, 1L);
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setDescripcion("descripcion-modificada");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoPaqueteTrabajoOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: update ProyectoPaqueteTrabajo
        () -> service.update(proyectoPaqueteTrabajo))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  void update_WithProyectoWithPermitePaquetesTrabajoFalse_ThrowsIllegalArgumentException() throws Exception {
    // given: Proyecto with field PermitePaquetesTrabajo = FALSE or NULL
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajoOriginal = generarMockProyectoPaqueteTrabajo(1L, 1L);
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setDescripcion("descripcion-modificada");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoPaqueteTrabajoOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getPermitePaquetesTrabajo(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(Boolean.FALSE));

    Assertions.assertThatThrownBy(
        // when: update ProyectoPaqueteTrabajo
        () -> service.update(proyectoPaqueteTrabajo))
        // then: IllegalArgumentException is thrown
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El proyecto no está configurado para utilizar paquetes de trabajo");
  }

  @Test
  void update_WithDuplicatedNombreInProyecto_ThrowsIllegalArgumentException() {
    // given: a ProyectoPaqueteTrabajo with duplicated nombre in proyecto
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajoOriginal = generarMockProyectoPaqueteTrabajo(1L, 1L);
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setDescripcion("descripcion-modificada");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoPaqueteTrabajoOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getPermitePaquetesTrabajo(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(Boolean.TRUE));
    BDDMockito.given(repository.existsProyectoPaqueteTrabajoByIdNotAndProyectoIdAndNombre(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: update ProyectoPaqueteTrabajo
        () -> service.update(proyectoPaqueteTrabajo))
        // then: throw exception as nombre is duplicated
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un ProyectoPaqueteTrabajo en el proyecto con el nombre '%s'",
            proyectoPaqueteTrabajo.getNombre());
  }

  @Test
  void update_WithDatesOutsideProyectoRange_ThrowsIllegalArgumentException() {
    // given: a ProyectoPaqueteTrabajo with dates aoutside Proyecto Range
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajoOriginal = generarMockProyectoPaqueteTrabajo(1L, 1L);
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = generarMockProyectoPaqueteTrabajo(1L, 1L);
    proyectoPaqueteTrabajo.setDescripcion("descripcion-modificada");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoPaqueteTrabajoOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getPermitePaquetesTrabajo(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(Boolean.TRUE));
    BDDMockito.given(repository.existsProyectoPaqueteTrabajoByIdNotAndProyectoIdAndNombre(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any())).willReturn(Boolean.FALSE);
    BDDMockito
        .given(proyectoRepository.existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Instant>any(), ArgumentMatchers.<Instant>any()))
        .willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: update ProyectoPaqueteTrabajo
        () -> service.update(proyectoPaqueteTrabajo))
        // then: throw exception as nombre is duplicated
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "El periodo del ProyectoPaqueteTrabajo no se encuentra dentro del rango de fechas definido para el proyecto");
  }

  @Test
  void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing ProyectoPaqueteTrabajo
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(repository.getPermitePaquetesTrabajo(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(Boolean.TRUE));
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.<Long>any());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoPaqueteTrabajoNotFoundException.class);
  }

  @Test
  void delete_WithProyectoWithPermitePaquetesTrabajoFalse_ThrowsIllegalArgumentException() throws Exception {
    // given: Proyecto with field PermitePaquetesTrabajo = FALSE or NULL
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(repository.getPermitePaquetesTrabajo(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(Boolean.FALSE));

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: IllegalArgumentException is thrown
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El proyecto no está configurado para utilizar paquetes de trabajo");
  }

  @Test
  void findById_ReturnsProyectoPaqueteTrabajo() {
    // given: Un ProyectoPaqueteTrabajo con el id buscado
    Long idBuscado = 1L;
    Long idProyecto = 1L;

    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockProyectoPaqueteTrabajo(idBuscado, idProyecto)));

    // when: Buscamos el ProyectoPaqueteTrabajo por su id
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = service.findById(idBuscado);

    // then: el ProyectoPaqueteTrabajo
    Assertions.assertThat(proyectoPaqueteTrabajo).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoPaqueteTrabajo.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  void findById_WithIdNotExist_ThrowsProyectoPaqueteTrabajoNotFoundException() throws Exception {
    // given: Ningun ProyectoPaqueteTrabajo con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ProyectoPaqueteTrabajo por su id
    // then: lanza un ProyectoPaqueteTrabajoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ProyectoPaqueteTrabajoNotFoundException.class);
  }

  @Test
  void findAllByProyecto_ReturnsPage() {
    // given: Una lista con 37 ProyectoPaqueteTrabajo para la Proyecto
    Long proyectoId = 1L;
    List<ProyectoPaqueteTrabajo> proyectosEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectosEntidadesConvocantes.add(generarMockProyectoPaqueteTrabajo(Long.valueOf(i), proyectoId));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoPaqueteTrabajo>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectosEntidadesConvocantes.size() ? proyectosEntidadesConvocantes.size() : toIndex;
          List<ProyectoPaqueteTrabajo> content = proyectosEntidadesConvocantes.subList(fromIndex, toIndex);
          Page<ProyectoPaqueteTrabajo> pageResponse = new PageImpl<>(content, pageable,
              proyectosEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoPaqueteTrabajo> page = service.findAllByProyecto(proyectoId, null, paging);

    // then: Devuelve la pagina 3 con los ProyectoPaqueteTrabajo del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyectoPaqueteTrabajo.getId()).isEqualTo(Long.valueOf(i));
    }
  }

  /**
   * Función que devuelve un objeto ProyectoPaqueteTrabajo
   * 
   * @param id         id del ProyectoPaqueteTrabajo
   * @param proyectoId id del Proyecto
   * @return el objeto ProyectoPaqueteTrabajo
   */
  private ProyectoPaqueteTrabajo generarMockProyectoPaqueteTrabajo(Long id, Long proyectoId) {

    // @formatter:off
    return ProyectoPaqueteTrabajo.builder()
        .id(id)
        .proyectoId(proyectoId)
        .nombre("proyecto-paquete-trabajo-" + (id == null ? "" : String.format("%03d", id)))
        .fechaInicio(Instant.parse("2020-01-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-01-15T23:59:59Z"))
        .personaMes(1D)
        .descripcion("descripcion-proyecto-paquete-trabajo-" + (id == null ? "" : String.format("%03d", id)))
        .build();
    // @formatter:on
  }
}
