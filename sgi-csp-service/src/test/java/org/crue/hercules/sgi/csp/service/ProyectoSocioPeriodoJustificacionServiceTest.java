package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionDocumentoRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoSocioPeriodoJustificacionServiceImpl;
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
 * ProyectoSocioPeriodoJustificacionServiceTest
 */
public class ProyectoSocioPeriodoJustificacionServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoSocioPeriodoJustificacionRepository repository;
  @Mock
  private ProyectoSocioRepository proyectoSocioRepository;
  @Mock
  private ProyectoRepository proyectoRepository;

  @Mock
  private ProyectoSocioPeriodoJustificacionDocumentoRepository proyectoSocioPeriodoJustificacionDocumentoRepository;

  private ProyectoSocioPeriodoJustificacionService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ProyectoSocioPeriodoJustificacionServiceImpl(repository, proyectoSocioRepository,
        proyectoSocioPeriodoJustificacionDocumentoRepository, proyectoRepository);
  }

  @Test
  public void update_ReturnsProyectoSocioPeriodoJustificacion() {
    // given: un ProyectoSocioPeriodoJustificacion actualizado,
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoSocioPeriodoJustificacion updateProyectoSocioPeriodoJustificacion1 = generarMockProyectoSocioPeriodoJustificacion(
        4L);
    updateProyectoSocioPeriodoJustificacion1.setFechaInicio(Instant.parse("2021-01-19T00:00:00Z"));
    updateProyectoSocioPeriodoJustificacion1.setFechaFin(Instant.parse("2021-01-26T23:59:59Z"));

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockProyectoSocioPeriodoJustificacion(4L)));

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ProyectoSocioPeriodoJustificacion>anyList()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update
    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockProyectoSocio(4L, proyectoId)));
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacionActualizado = service
        .update(updateProyectoSocioPeriodoJustificacion1, 4L);

    // then: Se actualiza el ProyectoSocioPeriodoJustificacion
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getId()).as(".getId()").isEqualTo(4L);
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getProyectoSocioId()).as(".getProyectoSocioId()")
        .isEqualTo(4L);
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getFechaInicio()).as(".getFechaInicio()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getFechaInicio());
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getFechaFin()).as("getFechaFin()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getFechaFin());
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getFechaInicioPresentacion())
        .as("get(0).getFechaInicioPresentacion()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getFechaInicioPresentacion());
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getFechaFinPresentacion())
        .as("get(0).getFechaFinPresentacion()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getFechaFinPresentacion());
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getNumPeriodo()).as("getNumPeriodo()")
        .isEqualTo(1);
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getObservaciones());

  }

  @Test
  public void update_WithNoExistingProyectoSocio_ThrowsProyectoSocioNotFoundException() {
    // given: a ProyectoSocioEntidadGestora with non existing ProyectoSocio
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        proyectoSocioPeriodoJustificacionId);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocioPeriodoJustificacion));

    Assertions.assertThatThrownBy(
        // when: update ProyectoSocioEntidadGestora
        () -> service.update(proyectoSocioPeriodoJustificacion, proyectoSocioPeriodoJustificacionId))
        // then: throw exception as ProyectoSocio is not found
        .isInstanceOf(ProyectoSocioNotFoundException.class);
  }

  @Test
  public void update_WithIdNotExist_ThrowsProyectoSocioPeriodoJustificacionNotFoundException() {
    // given: Un ProyectoSocioPeriodoJustificacion a actualizar con un id que no
    // existe
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        proyectoSocioPeriodoJustificacionId);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when:update
    // then: Lanza una excepcion porque el ProyectoSocioPeriodoJustificacion no
    // existe
    Assertions
        .assertThatThrownBy(
            () -> service.update(proyectoSocioPeriodoJustificacion, proyectoSocioPeriodoJustificacionId))
        .isInstanceOf(ProyectoSocioPeriodoJustificacionNotFoundException.class);
  }

  @Test
  public void update_WithProyectoSocioChange_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion with proyecto socio modificado
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        proyectoSocioPeriodoJustificacionId);

    proyectoSocioPeriodoJustificacion.setProyectoSocioId(3L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockProyectoSocioPeriodoJustificacion(proyectoSocioPeriodoJustificacionId)));

    Assertions.assertThatThrownBy(
        // when: update
        () -> service.update(proyectoSocioPeriodoJustificacion, proyectoSocioPeriodoJustificacionId))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("No se puede modificar el proyecto socio del ProyectoSocioPeriodoJustificacion");
  }

  @Test
  public void update_WithFechaFinBeforeThanFechaInicio_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion with fecha fin before
    // fecha inicio
    Long proyectoId = 1L;
    Long proyectoSocioId = 1L;
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(proyectoSocioId, proyectoId);
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        proyectoSocioPeriodoJustificacionId);

    proyectoSocioPeriodoJustificacion
        .setFechaInicio(proyectoSocioPeriodoJustificacion.getFechaFin().plus(Period.ofDays(1)));

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocio));

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocioPeriodoJustificacion));

    Assertions.assertThatThrownBy(
        // when: update
        () -> service.update(proyectoSocioPeriodoJustificacion, proyectoSocioPeriodoJustificacionId))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha final tiene que ser posterior a la fecha inicial");
  }

  @Test
  public void update_WithOutFechasPresentacionAndEstadoProyectoAbierto_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion without fechas presentacion and
    // estado proyecto abierto
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    Long proyectoSocioId = 1L;
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(proyectoSocioId, proyectoId);

    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        proyectoSocioPeriodoJustificacionId);

    proyectoSocioPeriodoJustificacion.setFechaInicioPresentacion(null);
    proyectoSocioPeriodoJustificacion.setFechaFinPresentacion(null);

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocio));
    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocioPeriodoJustificacion));

    Assertions.assertThatThrownBy(
        // when: update
        () -> service.update(proyectoSocioPeriodoJustificacion, proyectoSocioPeriodoJustificacionId))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Las fechas de presentación no pueden ser null cuando el estado del proyecto es Abierto");
  }

  @Test
  public void update_WithFechaFinPresentacionBeforeFechaInicioPresentacion_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion with FechaFinPresentacion before
    // FechaInicioPresentacion
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    Long proyectoSocioId = 1L;
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(proyectoSocioId, proyectoId);
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        proyectoSocioPeriodoJustificacionId);
    proyectoSocioPeriodoJustificacion
        .setFechaInicioPresentacion(proyectoSocioPeriodoJustificacion.getFechaFinPresentacion().plus(Period.ofDays(1)));

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocio));

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocioPeriodoJustificacion));

    Assertions.assertThatThrownBy(
        // when: update
        () -> service.update(proyectoSocioPeriodoJustificacion, proyectoSocioPeriodoJustificacionId))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin de presentación tiene que ser posterior a la fecha de inicio de presentación");
  }

  @Test
  public void update_WithFechaFinAfterProyectoFechaFin_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion with Fecha fin after fecha fin
    // proyecto
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    Long proyectoSocioId = 1L;
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(proyectoSocioId, proyectoId);
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        proyectoSocioPeriodoJustificacionId);
    proyectoSocioPeriodoJustificacion.setFechaFin(proyectoSocio.getFechaFin().plus(Period.ofDays(1)));

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocio));

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocioPeriodoJustificacion));

    Assertions.assertThatThrownBy(
        // when: update
        () -> service.update(proyectoSocioPeriodoJustificacion, proyectoSocioPeriodoJustificacionId))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha fin no puede ser superior a la fecha fin indicada en Proyecto socio");
  }

  @Test
  public void create_ReturnsProyectoSocioPeriodoJustificacion() {
    // given: un nuevo ProyectoSocioPeriodoJustificacion ,
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    Long proyectoSocioId = 1L;
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(proyectoSocioId, proyectoId);

    ProyectoSocioPeriodoJustificacion updateProyectoSocioPeriodoJustificacion1 = generarMockProyectoSocioPeriodoJustificacion(
        null);
    updateProyectoSocioPeriodoJustificacion1.setFechaInicio(Instant.parse("2021-01-19T00:00:00Z"));
    updateProyectoSocioPeriodoJustificacion1.setFechaFin(Instant.parse("2021-01-26T23:59:59Z"));

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocio));

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ProyectoSocioPeriodoJustificacion>anyList()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacionActualizado = service
        .create(updateProyectoSocioPeriodoJustificacion1);

    // then: Se crea el nuevo ProyectoSocioPeriodoJustificacion,
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getProyectoSocioId()).as(".getProyectoSocioId()")
        .isEqualTo(1L);
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getFechaInicio()).as(".getFechaInicio()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getFechaInicio());
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getFechaFin()).as("getFechaFin()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getFechaFin());
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getFechaInicioPresentacion())
        .as("get(0).getFechaInicioPresentacion()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getFechaInicioPresentacion());
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getFechaFinPresentacion())
        .as("get(0).getFechaFinPresentacion()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getFechaFinPresentacion());
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getNumPeriodo()).as("getNumPeriodo()")
        .isEqualTo(1);
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getObservaciones());

  }

  @Test
  public void create_WithNoExistingProyectoSocio_ThrowsProyectoSocioNotFoundException() {
    // given: a ProyectoSocioEntidadGestora with non existing ProyectoSocio
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoSocioEntidadGestora
        () -> service.create(proyectoSocioPeriodoJustificacion))
        // then: throw exception as ProyectoSocio is not found
        .isInstanceOf(ProyectoSocioNotFoundException.class);
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un ProyectoSocioPeriodoJustificacion a actualizar con un id que no
    // existe
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        3L);

    // when:create
    // then: Lanza una excepcion porque el ProyectoSocioPeriodoJustificacion no
    // puede tener id
    Assertions.assertThatThrownBy(
        // when: create
        () -> service.create(proyectoSocioPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El id de proyecto socio periodo justificación debe ser null");
  }

  @Test
  public void create_WithFechaFinBeforeThanFechaInicio_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion with fecha fin before
    // fecha inicio
    Long proyectoId = 1L;
    Long proyectoSocioId = 1L;
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(proyectoSocioId, proyectoId);
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        null);

    proyectoSocioPeriodoJustificacion
        .setFechaInicio(proyectoSocioPeriodoJustificacion.getFechaFin().plus(Period.ofDays(1)));

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocio));

    Assertions.assertThatThrownBy(
        // when: create
        () -> service.create(proyectoSocioPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha final tiene que ser posterior a la fecha inicial");
  }

  @Test
  public void create_WithOutFechasPresentacionAndEstadoProyectoAbierto_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion without fechas presentacion and
    // estado proyecto abierto
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    Long proyectoSocioId = 1L;
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(proyectoSocioId, proyectoId);
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        null);

    proyectoSocioPeriodoJustificacion.setFechaInicioPresentacion(null);
    proyectoSocioPeriodoJustificacion.setFechaFinPresentacion(null);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocio));

    Assertions.assertThatThrownBy(
        // when: create
        () -> service.create(proyectoSocioPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Las fechas de presentación no pueden ser null cuando el estado del proyecto es Abierto");
  }

  @Test
  public void create_WithFechaFinPresentacionBeforeFechaInicioPresentacion_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion with FechaFinPresentacion before
    // FechaInicioPresentacion
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    Long proyectoSocioId = 1L;
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(proyectoSocioId, proyectoId);
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        null);
    proyectoSocioPeriodoJustificacion
        .setFechaInicioPresentacion(proyectoSocioPeriodoJustificacion.getFechaFinPresentacion().plus(Period.ofDays(1)));

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocio));

    Assertions.assertThatThrownBy(
        // when: create
        () -> service.create(proyectoSocioPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin de presentación tiene que ser posterior a la fecha de inicio de presentación");
  }

  @Test
  public void create_WithFechaFinAfterProyectoFechaFin_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion with Fecha fin after fecha fin
    // proyecto
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    Long proyectoSocioId = 1L;
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(proyectoSocioId, proyectoId);
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        null);
    proyectoSocioPeriodoJustificacion.setFechaFin(proyectoSocio.getFechaFin().plus(Period.ofDays(1)));

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocio));

    Assertions.assertThatThrownBy(
        // when: create
        () -> service.create(proyectoSocioPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha fin no puede ser superior a la fecha fin indicada en Proyecto socio");
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing SolicitudProyectoSocio
    Long id = 1L;
    Long proyectoId = 1L;
    Long proyectoSocioId = 1L;
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(proyectoSocioId, proyectoId);

    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        4L);
    List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificacionEliminar = new ArrayList<>();
    proyectoSocioPeriodoJustificacionEliminar.add(proyectoSocioPeriodoJustificacion);

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocio));

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id, proyectoSocioPeriodoJustificacionEliminar))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void delete_WithNoExistingProyectoSocio_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        4L);
    List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificacionEliminar = new ArrayList<>();
    proyectoSocioPeriodoJustificacionEliminar.add(proyectoSocioPeriodoJustificacion);

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id, proyectoSocioPeriodoJustificacionEliminar))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoSocioNotFoundException.class);
  }

  @Test
  public void findAllByProyectoSocio_ReturnsPage() {
    // given: Una lista con 37 ProyectoSocioEntidadGestora para la ProyectoSocio
    Long proyectoSocioId = 1L;
    List<ProyectoSocioPeriodoJustificacion> proyectoSociosEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoSociosEntidadesConvocantes.add(generarMockProyectoSocioPeriodoJustificacion(i));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoSocioPeriodoJustificacion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectoSociosEntidadesConvocantes.size() ? proyectoSociosEntidadesConvocantes.size()
              : toIndex;
          List<ProyectoSocioPeriodoJustificacion> content = proyectoSociosEntidadesConvocantes.subList(fromIndex,
              toIndex);
          Page<ProyectoSocioPeriodoJustificacion> pageResponse = new PageImpl<>(content, pageable,
              proyectoSociosEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoSocioPeriodoJustificacion> page = service.findAllByProyectoSocio(proyectoSocioId, null, paging);

    // then: Devuelve la pagina 3 con los ProyectoSocioPeriodoJustificacion del 31
    // al
    // 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyectoSocioPeriodoJustificacion.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(proyectoSocioPeriodoJustificacion.getObservaciones()).isEqualTo("observaciones-" + i);
    }
  }

  @Test
  public void findById_ReturnsProyectoSocioPeriodoJustificacion() {
    // given: Un ProyectoSocioPeriodoJustificacion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockProyectoSocioPeriodoJustificacion(idBuscado)));

    // when: Buscamos el ProyectoSocioPeriodoJustificacion por su id
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = service.findById(idBuscado);

    // then: el ProyectoSocioPeriodoJustificacion
    Assertions.assertThat(proyectoSocioPeriodoJustificacion).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(Instant.parse("2020-09-11T00:00:00Z"));
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getFechaFin()).as("getFechaFin()")
        .isEqualTo(Instant.parse("2020-12-20T23:59:59Z"));
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getFechaInicioPresentacion())
        .as("getFechaInicioPresentacion()").isEqualTo(Instant.parse("2020-10-10T00:00:00Z"));
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getFechaFinPresentacion()).as("getFechaFinPresentacion()")
        .isEqualTo(Instant.parse("2020-11-20T23:59:59Z"));
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getNumPeriodo()).as("getNumPeriodo()").isEqualTo(1);
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getObservaciones()).as("getObservaciones()")
        .isEqualTo("observaciones-1");
  }

  @Test
  public void findById_WithIdNotExist_ThrowsProyectoSocioPeriodoJustificacionNotFoundException() throws Exception {
    // given: Ningun ProyectoSocioPeriodoJustificacion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ProyectoSocioPeriodoJustificacion por su id
    // then: lanza un ProyectoSocioPeriodoJustificacionNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ProyectoSocioPeriodoJustificacionNotFoundException.class);
  }

  private Proyecto generarMockProyecto(Long id) {
    Proyecto proyecto = new Proyecto();
    proyecto.setEstado(new EstadoProyecto());
    proyecto.getEstado().setEstado(EstadoProyecto.Estado.CONCEDIDO);
    return proyecto;
  }

  private ProyectoSocio generarMockProyectoSocio(Long id, Long proyectoId) {
    ProyectoSocio proyectoSocio = new ProyectoSocio();
    proyectoSocio.setId(id == null ? 1 : id);
    proyectoSocio.setProyectoId(proyectoId);
    proyectoSocio.setFechaFin(Instant.parse("2022-12-23T23:59:59Z"));

    return proyectoSocio;
  }

  /**
   * Función que devuelve un objeto ProyectoSocioPeriodoJustificacion.
   * 
   * @param id identificador
   * @return el objeto ProyectoSocioPeriodoJustificacion
   */
  private ProyectoSocioPeriodoJustificacion generarMockProyectoSocioPeriodoJustificacion(Long id) {
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = new ProyectoSocioPeriodoJustificacion();
    proyectoSocioPeriodoJustificacion.setId(id);
    proyectoSocioPeriodoJustificacion.setProyectoSocioId(id == null ? 1 : id);
    proyectoSocioPeriodoJustificacion.setNumPeriodo(1);
    proyectoSocioPeriodoJustificacion.setFechaInicio(Instant.parse("2020-09-11T00:00:00Z"));
    proyectoSocioPeriodoJustificacion.setFechaFin(Instant.parse("2020-12-20T23:59:59Z"));
    proyectoSocioPeriodoJustificacion.setFechaInicioPresentacion(Instant.parse("2020-10-10T00:00:00Z"));
    proyectoSocioPeriodoJustificacion.setFechaFinPresentacion(Instant.parse("2020-11-20T23:59:59Z"));
    proyectoSocioPeriodoJustificacion.setObservaciones("observaciones-" + id);

    return proyectoSocioPeriodoJustificacion;
  }

}
