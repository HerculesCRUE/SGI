package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.csp.exceptions.PeriodoLongerThanSolicitudProyectoException;
import org.crue.hercules.sgi.csp.exceptions.PeriodoWrongOrderException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudProyectoSocioPeriodoJustificacionServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * SolicitudProyectoSocioPeriodoJustificacionServiceTest
 */
@Import({ SolicitudProyectoSocioPeriodoJustificacionServiceImpl.class })
class SolicitudProyectoSocioPeriodoJustificacionServiceTest extends BaseServiceTest {

  @MockBean
  private SolicitudProyectoSocioPeriodoJustificacionRepository repository;
  @MockBean
  private SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;
  @MockBean
  private SolicitudService solicitudService;
  @MockBean
  private SolicitudProyectoRepository solicitudProyectoRepository;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private SolicitudProyectoSocioPeriodoJustificacionService service;

  @Test
  void update_ReturnsSolicitudProyectoSocioPeriodoJustificacion() {
    // given: una lista con uno de los SolicitudProyectoSocioPeriodoJustificacion
    // actualizado,
    // otro nuevo y sin el otros existente
    Long solicitudId = 1L;
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);

    List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificacionExistentes = new ArrayList<>();
    solicitudProyectoSocioPeriodoJustificacionExistentes.add(generarSolicitudProyectoSocioPeriodoJustificacion(2L, 1L));
    solicitudProyectoSocioPeriodoJustificacionExistentes.add(generarSolicitudProyectoSocioPeriodoJustificacion(4L, 1L));
    solicitudProyectoSocioPeriodoJustificacionExistentes.add(generarSolicitudProyectoSocioPeriodoJustificacion(5L, 1L));

    SolicitudProyectoSocioPeriodoJustificacion newSolicitudProyectoSocioPeriodoJustificacion = generarSolicitudProyectoSocioPeriodoJustificacion(
        null, 1L);
    newSolicitudProyectoSocioPeriodoJustificacion.setMesInicial(1);
    newSolicitudProyectoSocioPeriodoJustificacion.setMesFinal(3);
    SolicitudProyectoSocioPeriodoJustificacion updatedSolicitudProyectoSocioPeriodoJustificacion = generarSolicitudProyectoSocioPeriodoJustificacion(
        4L, 1L);
    updatedSolicitudProyectoSocioPeriodoJustificacion.setMesInicial(4);
    updatedSolicitudProyectoSocioPeriodoJustificacion.setMesFinal(10);

    List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificacionActualizar = new ArrayList<>();
    solicitudProyectoSocioPeriodoJustificacionActualizar.add(newSolicitudProyectoSocioPeriodoJustificacion);
    solicitudProyectoSocioPeriodoJustificacionActualizar.add(updatedSolicitudProyectoSocioPeriodoJustificacion);

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoSocioPeriodoJustificacionExistentes);
    BDDMockito.doNothing().when(repository)
        .deleteAll(ArgumentMatchers.<SolicitudProyectoSocioPeriodoJustificacion>anyList());
    BDDMockito.given(repository.saveAll(ArgumentMatchers.<SolicitudProyectoSocioPeriodoJustificacion>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<SolicitudProyectoSocioPeriodoJustificacion> periodosPagos = invocation.getArgument(0);
          return periodosPagos.stream().map(periodoPago -> {
            if (periodoPago.getId() == null) {
              periodoPago.setId(6L);
            }
            periodoPago.setSolicitudProyectoSocioId(solicitudProyectoSocioId);
            return periodoPago;
          }).collect(Collectors.toList());
        });
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: se actualiza solicitudProyectoSocioPeriodoJustificacionActualizar
    List<SolicitudProyectoSocioPeriodoJustificacion> periodosPagoActualizados = service.update(solicitudProyectoSocioId,
        solicitudProyectoSocioPeriodoJustificacionActualizar);

    // then: El SolicitudProyectoSocioPeriodoJustificacion se actualiza
    // correctamente.

    // then: Se crea el nuevo ConvocatoriaPeriodoJustificacion, se actualiza el
    // existe y se elimina el otro
    Assertions.assertThat(periodosPagoActualizados.get(0).getId()).as("get(0).getId()").isEqualTo(6L);
    Assertions.assertThat(periodosPagoActualizados.get(0).getSolicitudProyectoSocioId())
        .as("get(0).getSolicitudProyectoSocioId()").isEqualTo(solicitudProyectoSocioId);
    Assertions.assertThat(periodosPagoActualizados.get(0).getMesInicial()).as("get(0).getMesInicial()")
        .isEqualTo(newSolicitudProyectoSocioPeriodoJustificacion.getMesInicial());
    Assertions.assertThat(periodosPagoActualizados.get(0).getMesFinal()).as("get(0).getMesFinal()")
        .isEqualTo(newSolicitudProyectoSocioPeriodoJustificacion.getMesFinal());
    Assertions.assertThat(periodosPagoActualizados.get(0).getFechaInicio()).as("get(0).getFechaInicio()")
        .isEqualTo(newSolicitudProyectoSocioPeriodoJustificacion.getFechaInicio());
    Assertions.assertThat(periodosPagoActualizados.get(0).getFechaFin()).as("get(0).getFechaFin()")
        .isEqualTo(newSolicitudProyectoSocioPeriodoJustificacion.getFechaFin());
    Assertions.assertThat(periodosPagoActualizados.get(0).getNumPeriodo()).as("get(0).getNumPeriodo()")
        .isEqualTo(newSolicitudProyectoSocioPeriodoJustificacion.getNumPeriodo());
    Assertions.assertThat(periodosPagoActualizados.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo(newSolicitudProyectoSocioPeriodoJustificacion.getObservaciones());

    Assertions.assertThat(periodosPagoActualizados.get(1).getId()).as("get(1).getId()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoJustificacion.getId());
    Assertions.assertThat(periodosPagoActualizados.get(1).getSolicitudProyectoSocioId())
        .as("get(1).getSolicitudProyectoSocioId()").isEqualTo(solicitudProyectoSocioId);
    Assertions.assertThat(periodosPagoActualizados.get(1).getMesInicial()).as("get(1).getMesInicial()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoJustificacion.getMesInicial());
    Assertions.assertThat(periodosPagoActualizados.get(1).getMesFinal()).as("get(1).getMesFinal()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoJustificacion.getMesFinal());
    Assertions.assertThat(periodosPagoActualizados.get(1).getFechaInicio()).as("get(1).getFechaInicio()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoJustificacion.getFechaInicio());
    Assertions.assertThat(periodosPagoActualizados.get(1).getFechaFin()).as("get(1).getFechaFin()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoJustificacion.getFechaFin());
    Assertions.assertThat(periodosPagoActualizados.get(1).getNumPeriodo()).as("get(1).getNumPeriodo()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoJustificacion.getNumPeriodo());
    Assertions.assertThat(periodosPagoActualizados.get(1).getObservaciones()).as("get(1).getObservaciones()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoJustificacion.getObservaciones());

    Mockito.verify(repository, Mockito.times(1))
        .deleteAll(ArgumentMatchers.<SolicitudProyectoSocioPeriodoJustificacion>anyList());
    Mockito.verify(repository, Mockito.times(1))
        .saveAll(ArgumentMatchers.<SolicitudProyectoSocioPeriodoJustificacion>anyList());

  }

  @Test
  void update_WithSolicitudProyectoSocioNotExist_ThrowsSolicitudProyectoSocioNotFoundException() {
    // given: a SolicitudProyectoSocioPeriodoJustificacion with non existing
    // SolicitudProyectoSocio
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion = generarSolicitudProyectoSocioPeriodoJustificacion(
        1L, 1L);

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadGestora
        () -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoSocioPeriodoJustificacion)))
        // then: throw exception as SolicitudProyectoSocio is not found
        .isInstanceOf(SolicitudProyectoSocioNotFoundException.class);
  }

  @Test
  void update_WithIdNotExist_ThrowsSolicitudProyectoSocioPeriodoJustificacionNotFoundException() {
    // given: Un SolicitudProyectoSocioPeriodoJustificacion actualizado con un id
    // que no
    // existe
    Long solicitudId = 1L;
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);
    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion = generarSolicitudProyectoSocioPeriodoJustificacion(
        1L, solicitudProyectoSocioId);

    List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificacionExistentes = new ArrayList<>();
    solicitudProyectoSocioPeriodoJustificacionExistentes.add(generarSolicitudProyectoSocioPeriodoJustificacion(3L, 1L));

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoSocioPeriodoJustificacionExistentes);
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocioPeriodoJustificacion
    // then: Lanza una excepcion porque el
    // SolicitudProyectoSocioPeriodoJustificacion no
    // existe
    Assertions
        .assertThatThrownBy(
            () -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoSocioPeriodoJustificacion)))
        .isInstanceOf(SolicitudProyectoSocioPeriodoJustificacionNotFoundException.class);
  }

  @Test
  void update_WithSolicitudProyectoSocioChange_ThrowsNoRelatedEntitiesException() {
    // given:Se actualiza SolicitudProyectoSocio
    Long solicitudId = 1L;
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);
    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion = generarSolicitudProyectoSocioPeriodoJustificacion(
        1L, 1L);

    solicitudProyectoSocioPeriodoJustificacion.setSolicitudProyectoSocioId(2L);

    List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificacionExistentes = new ArrayList<>();
    solicitudProyectoSocioPeriodoJustificacionExistentes.add(generarSolicitudProyectoSocioPeriodoJustificacion(1L, 1L));

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoSocioPeriodoJustificacionExistentes);
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocio del
    // SolicitudProyectoSocioPeriodoJustificacion
    // then: Lanza una excepcion porque no se puede modificar el campo
    // SolicitudProyectoSocio
    Assertions
        .assertThatThrownBy(
            () -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoSocioPeriodoJustificacion)))
        .isInstanceOf(NoRelatedEntitiesException.class)
        .hasMessage("Not all provided Justification Period are related with Partner");
  }

  @Test
  void update_WithMesSolapamiento_ThrowsPeriodoWrongOrderException() {
    // given: Se actualiza SolicitudProyectoSocioPeriodoJustificacion cuyo mes es
    // superior a la duración de solicitud de proyecto
    Long solicitudId = 1L;
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);
    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion1 = generarSolicitudProyectoSocioPeriodoJustificacion(
        1L, solicitudProyectoSocioId);
    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion2 = generarSolicitudProyectoSocioPeriodoJustificacion(
        2L, solicitudProyectoSocioId);

    List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificacionExistentes = new ArrayList<>();
    solicitudProyectoSocioPeriodoJustificacionExistentes.add(generarSolicitudProyectoSocioPeriodoJustificacion(1L, 1L));
    solicitudProyectoSocioPeriodoJustificacionExistentes.add(generarSolicitudProyectoSocioPeriodoJustificacion(2L, 1L));

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoSocioPeriodoJustificacionExistentes);
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocioPeriodoJustificacion
    // then: Lanza una excepcion porque el mes es superior a la duración de
    // la solicitud de proyecto
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId,
            Arrays.asList(solicitudProyectoSocioPeriodoJustificacion1, solicitudProyectoSocioPeriodoJustificacion2)))
        .isInstanceOf(PeriodoWrongOrderException.class).hasMessageContaining(
            "The first Period must start in month 1 and all Periods must be consecutive, with no gaps");
  }

  @Test
  void update_WithMesInicialPosteriorMesFinal_ThrowsConstraintViolationException() {
    // given: Se actualiza SolicitudProyectoSocioPeriodoJustificacion cuyo mes
    // inicial es
    // posterior al mes final
    Long solicitudId = 1L;
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);
    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion1 = generarSolicitudProyectoSocioPeriodoJustificacion(
        1L, solicitudProyectoSocioId);
    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion2 = generarSolicitudProyectoSocioPeriodoJustificacion(
        2L, solicitudProyectoSocioId);

    solicitudProyectoSocioPeriodoJustificacion1.setMesInicial(6);

    solicitudProyectoSocioPeriodoJustificacion1.setMesFinal(5);

    List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificacionExistentes = new ArrayList<>();
    solicitudProyectoSocioPeriodoJustificacionExistentes.add(generarSolicitudProyectoSocioPeriodoJustificacion(1L, 1L));
    solicitudProyectoSocioPeriodoJustificacionExistentes.add(generarSolicitudProyectoSocioPeriodoJustificacion(2L, 1L));

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoSocioPeriodoJustificacionExistentes);
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocioPeriodoJustificacion
    // then: Lanza una excepcion porque mes inicial es
    // posterior al mes final
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId,
            Arrays.asList(solicitudProyectoSocioPeriodoJustificacion1, solicitudProyectoSocioPeriodoJustificacion2)))
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("End month must be bigger or equal than initial month");
  }

  @Test
  void update_WithFechaInicioPosteriorFechaFin_ThrowsConstraintViolationException() {
    // given: Se actualiza SolicitudProyectoSocioPeriodoJustificacion con fecha
    // inicio
    // superior a fecha fin
    Long solicitudId = 1L;
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);
    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion1 = generarSolicitudProyectoSocioPeriodoJustificacion(
        1L, 1L);

    solicitudProyectoSocioPeriodoJustificacion1.setFechaInicio(Instant.parse("2021-12-19T00:00:00Z"));

    List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificacionExistentes = new ArrayList<>();
    solicitudProyectoSocioPeriodoJustificacionExistentes.add(generarSolicitudProyectoSocioPeriodoJustificacion(1L, 1L));

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoSocioPeriodoJustificacionExistentes);
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocioPeriodoJustificacion
    // then: Lanza una excepcion porque fecha inicio superior a fecha fin
    Assertions
        .assertThatThrownBy(
            () -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoSocioPeriodoJustificacion1)))
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("End date must be bigger or equal than initial date");
  }

  @Test
  void update_WithMesFinalSuperiorDuracion_ThrowsPeriodoLongerThanSolicitudProyectoException() {
    // given: Se actualiza SolicitudProyectoSocioPeriodoJustificacion con mes final
    // superior a la duración
    Long solicitudId = 1L;
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);
    solicitudProyecto.setDuracion(15);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);
    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion1 = generarSolicitudProyectoSocioPeriodoJustificacion(
        1L, 1L);

    solicitudProyectoSocioPeriodoJustificacion1.setMesFinal(20);

    List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificacionExistentes = new ArrayList<>();
    solicitudProyectoSocioPeriodoJustificacionExistentes.add(generarSolicitudProyectoSocioPeriodoJustificacion(1L, 1L));

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoSocioPeriodoJustificacionExistentes);
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocioPeriodoJustificacion
    // then: Lanza una excepcion porque mes final superior a la duración
    Assertions
        .assertThatThrownBy(
            () -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoSocioPeriodoJustificacion1)))
        .isInstanceOf(PeriodoLongerThanSolicitudProyectoException.class)
        .hasMessage("The Period goes beyond the duration of the Project Data");
  }

  @Test
  void update_WithSolapamientoPeriodo_ThrowsPeriodoWrongOrderException() {
    // given: Se actualiza SolicitudProyectoSocioPeriodoJustificacion con fecha
    // inicio
    // superior a fecha fin
    Long solicitudId = 1L;
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);
    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion1 = generarSolicitudProyectoSocioPeriodoJustificacion(
        1L, 1L);
    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion2 = generarSolicitudProyectoSocioPeriodoJustificacion(
        2L, 1L);

    List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificacionExistentes = new ArrayList<>();
    solicitudProyectoSocioPeriodoJustificacionExistentes.add(generarSolicitudProyectoSocioPeriodoJustificacion(1L, 1L));
    solicitudProyectoSocioPeriodoJustificacionExistentes.add(generarSolicitudProyectoSocioPeriodoJustificacion(2L, 1L));

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoSocioPeriodoJustificacionExistentes);
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocioPeriodoJustificacion
    // then: Lanza una excepcion porque fecha inicio superior a fecha fin
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId,
            Arrays.asList(solicitudProyectoSocioPeriodoJustificacion1, solicitudProyectoSocioPeriodoJustificacion2)))
        .isInstanceOf(PeriodoWrongOrderException.class).hasMessageContaining(
            "The first Period must start in month 1 and all Periods must be consecutive, with no gaps");
  }

  @Test
  void findById_ReturnsSolicitudProyectoSocioPeriodoJustificacion() {
    // given: Un SolicitudProyectoSocioPeriodoJustificacion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarSolicitudProyectoSocioPeriodoJustificacion(idBuscado, 1L)));

    // when: Buscamos el SolicitudProyectoSocioPeriodoJustificacion por su id
    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion = service.findById(idBuscado);

    // then: el SolicitudProyectoSocioPeriodoJustificacion
    Assertions.assertThat(solicitudProyectoSocioPeriodoJustificacion).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoSocioPeriodoJustificacion.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  void findById_WithIdNotExist_ThrowsSolicitudProyectoSocioPeriodoJustificacionNotFoundException()
      throws Exception {
    // given: Ningun SolicitudProyectoSocioPeriodoJustificacion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SolicitudProyectoSocioPeriodoJustificacion por su id
    // then: lanza un SolicitudProyectoSocioPeriodoJustificacionNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(SolicitudProyectoSocioPeriodoJustificacionNotFoundException.class);
  }

  @Test
  void findAllBySolicitud_ReturnsPage() {
    // given: Una lista con 37 SolicitudProyectoSocioPeriodoJustificacion
    Long solicitudProyectoSocioId = 1L;
    List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificacion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoSocioPeriodoJustificacion.add(generarSolicitudProyectoSocioPeriodoJustificacion(i, i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoSocioPeriodoJustificacion>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<SolicitudProyectoSocioPeriodoJustificacion>>() {
          @Override
          public Page<SolicitudProyectoSocioPeriodoJustificacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > solicitudProyectoSocioPeriodoJustificacion.size()
                ? solicitudProyectoSocioPeriodoJustificacion.size()
                : toIndex;
            List<SolicitudProyectoSocioPeriodoJustificacion> content = solicitudProyectoSocioPeriodoJustificacion
                .subList(fromIndex, toIndex);
            Page<SolicitudProyectoSocioPeriodoJustificacion> page = new PageImpl<>(content, pageable,
                solicitudProyectoSocioPeriodoJustificacion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<SolicitudProyectoSocioPeriodoJustificacion> page = service
        .findAllBySolicitudProyectoSocio(solicitudProyectoSocioId, null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacionRecuperado = page
          .getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitudProyectoSocioPeriodoJustificacionRecuperado.getId()).isEqualTo(i);
    }
  }

  private SolicitudProyecto generarMockSolicitudProyecto(Long solicitudProyectoId, Long solicitudId) {
    return SolicitudProyecto.builder().id(solicitudProyectoId).build();
  }

  private SolicitudProyectoSocio generarMockSolicitudProyectoSocio(Long solicitudProyectoSocioId,
      Long solicitudProyectoId) {
    return SolicitudProyectoSocio.builder().id(solicitudProyectoSocioId).solicitudProyectoId(solicitudProyectoId)
        .build();
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoSocioPeriodoJustificacion
   * 
   * @param solicitudProyectoSocioPeriodoJustificacionId
   * @param solicitudProyectoSocioId
   * @return el objeto SolicitudProyectoSocioPeriodoJustificacion
   */
  private SolicitudProyectoSocioPeriodoJustificacion generarSolicitudProyectoSocioPeriodoJustificacion(
      Long solicitudProyectoSocioPeriodoJustificacionId, Long solicitudProyectoSocioId) {

    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion = SolicitudProyectoSocioPeriodoJustificacion
        .builder().id(solicitudProyectoSocioPeriodoJustificacionId).solicitudProyectoSocioId(solicitudProyectoSocioId)
        .numPeriodo(2).mesInicial(1).mesFinal(3).fechaInicio(Instant.parse("2020-12-19T00:00:00Z"))
        .fechaFin(Instant.parse("2021-02-09T00:00:00Z")).observaciones("Periodo 1").build();

    return solicitudProyectoSocioPeriodoJustificacion;
  }

}
