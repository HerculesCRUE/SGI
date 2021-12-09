package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaFaseNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaFaseRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaFaseServiceImpl;
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
import org.springframework.security.test.context.support.WithMockUser;

/**
 * ConvocatoriaFaseServiceTest
 */

public class ConvocatoriaFaseServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaFaseRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ConfiguracionSolicitudRepository configuracionSolicitudRepository;
  @Mock
  private ModeloTipoFaseRepository modeloTipoFaseRepository;
  @Mock
  private ConvocatoriaService convocatoriaService;

  private ConvocatoriaFaseService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaFaseServiceImpl(repository, convocatoriaRepository, configuracionSolicitudRepository,
        modeloTipoFaseRepository, convocatoriaService);
  }

  @Test
  public void create_ReturnsConvocatoriaFase() {
    // given: Un nuevo ConvocatoriaFase
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConvocatoriaFase> page = new PageImpl<>(new ArrayList<ConvocatoriaFase>(), pageable, 0);
          return page;

        });
    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, convocatoria, convocatoriaFase, Boolean.TRUE)));

    BDDMockito.given(repository.save(convocatoriaFase)).will((InvocationOnMock invocation) -> {
      ConvocatoriaFase convocatoriaFaseCreado = invocation.getArgument(0);
      convocatoriaFaseCreado.setId(1L);
      return convocatoriaFaseCreado;
    });

    // when: Creamos el ConvocatoriaFase
    ConvocatoriaFase convocatoriaFaseCreado = service.create(convocatoriaFase);

    // then: El ConvocatoriaFase se crea correctamente
    Assertions.assertThat(convocatoriaFaseCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaFaseCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaFaseCreado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaFase.getConvocatoriaId());
    Assertions.assertThat(convocatoriaFaseCreado.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(convocatoriaFase.getFechaInicio());
    Assertions.assertThat(convocatoriaFaseCreado.getFechaFin()).as("getFechaFin()")
        .isEqualTo(convocatoriaFase.getFechaFin());
    Assertions.assertThat(convocatoriaFaseCreado.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(convocatoriaFase.getTipoFase().getId());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaFase que ya tiene id
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);

    // when: Creamos el ConvocatoriaFase
    // then: Lanza una excepcion porque el ConvocatoriaFase ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaFase)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear ConvocatoriaFase");
  }

  @Test
  public void create_WithoutConvocatoriaId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaFase without ConvocatoriaId
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);
    convocatoriaFase.setConvocatoriaId(null);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as ConvocatoriaId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Convocatoria no puede ser null para crear ConvocatoriaFase");
  }

  @Test
  public void create_WithNoExistingConvocatoria_ThrowsConvocatoriaNotFoundException() {
    // given: a ConvocatoriaFase with non existing Convocatoria
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as Convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  public void create_WithoutTipoFaseId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaFase without tipoFaseId
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);
    convocatoriaFase.getTipoFase().setId(null);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as tipoFaseId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Fase no puede ser null para crear ConvocatoriaFase");
  }

  @Test
  public void create_WithoutModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaFase con Convocatoria sin Modelo de Ejecucion
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setModeloEjecucion(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as ModeloEjecucion not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no disponible para el ModeloEjecucion '%s'",
            convocatoriaFase.getTipoFase().getNombre(), "Convocatoria sin modelo asignado");
  }

  @Test
  public void create_WithoutModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaFase con TipoFase no asignado al Modelo de Ejecucion de la
    // convocatoria
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as ModeloTipoFase not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no disponible para el ModeloEjecucion '%s'",
            convocatoriaFase.getTipoFase().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void create_WithDisabledModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaFase con la asignación de TipoFase al Modelo de Ejecucion
    // de la convocatoria inactiva
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, convocatoria, convocatoriaFase, Boolean.FALSE)));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as ModeloTipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoFase '%s' no está activo para el ModeloEjecucion '%s'",
            convocatoriaFase.getTipoFase().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void create_WithDisabledTipoFase_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaFase TipoFase disabled
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);
    convocatoriaFase.getTipoFase().setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, convocatoria, convocatoriaFase, Boolean.TRUE)));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as TipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no está activo", convocatoriaFase.getTipoFase().getNombre());
  }

  @Test
  public void create_WithRangoFechasSopalado_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaFase with fechas solapadas con una convocatoria
    // existente
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, convocatoria, convocatoriaFase, Boolean.TRUE)));

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConvocatoriaFase> page = new PageImpl<>(Arrays.asList(generarMockConvocatoriaFase(null)), pageable, 0);
          return page;

        });

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(convocatoriaFase))
        // then: throw exception as TipoFase is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe una convocatoria en ese rango de fechas");
  }

  @Test
  public void update_ReturnsConvocatoriaFase() {
    // given: Un nuevo ConvocatoriaFase con el nombre actualizado
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);
    ConvocatoriaFase convocatoriaFaseActualizado = generarMockConvocatoriaFase(1L);
    convocatoriaFaseActualizado.setTipoFase(generarMockTipoFase(2L, Boolean.TRUE));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaFase));
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(
            Optional.of(generarMockModeloTipoFase(1L, convocatoria, convocatoriaFaseActualizado, Boolean.TRUE)));
    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaFase>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));
    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConvocatoriaFase> page = new PageImpl<>(new ArrayList<ConvocatoriaFase>(), pageable, 0);
          return page;

        });

    // when: Actualizamos el ConvocatoriaFase
    ConvocatoriaFase updated = service.update(convocatoriaFaseActualizado);

    // then: El ConvocatoriaFase se actualiza correctamente.
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(convocatoriaFase.getId());
    Assertions.assertThat(updated.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaFase.getConvocatoriaId());
    Assertions.assertThat(updated.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(convocatoriaFaseActualizado.getFechaInicio());
    Assertions.assertThat(updated.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(convocatoriaFaseActualizado.getTipoFase().getId());
  }

  @Test
  public void update_WithIdNotExist_ThrowsConvocatoriaFaseNotFoundException() {
    // given: Un ConvocatoriaFase a actualizar con un id que no existe
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el ConvocatoriaFase
    // then: Lanza una excepcion porque el ConvocatoriaFase no existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaFase))
        .isInstanceOf(ConvocatoriaFaseNotFoundException.class);
  }

  @Test
  public void update_WithoutModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaFase con Convocatoria sin Modelo de Ejecucion
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);
    ConvocatoriaFase convocatoriaFaseActualizado = generarMockConvocatoriaFase(1L);
    convocatoriaFaseActualizado.setTipoFase(generarMockTipoFase(2L, Boolean.TRUE));
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setModeloEjecucion(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaFase));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaFase
        () -> service.update(convocatoriaFaseActualizado))
        // then: throw exception as ModeloTipoFase not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no disponible para el ModeloEjecucion '%s'",
            convocatoriaFaseActualizado.getTipoFase().getNombre(), "Convocatoria sin modelo asignado");
  }

  @Test
  public void update_WithoutModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaFase con TipoFase no asignado al Modelo de Ejecucion de la
    // convocatoria
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);
    ConvocatoriaFase convocatoriaFaseActualizado = generarMockConvocatoriaFase(1L);
    convocatoriaFaseActualizado.setTipoFase(generarMockTipoFase(2L, Boolean.TRUE));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaFase));
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaFase
        () -> service.update(convocatoriaFaseActualizado))
        // then: throw exception as ModeloTipoFase not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no disponible para el ModeloEjecucion '%s'",
            convocatoriaFaseActualizado.getTipoFase().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void update_WithDisabledModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaFase con la asignación de TipoFase al Modelo de Ejecucion
    // de la convocatoria inactiva
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);
    ConvocatoriaFase convocatoriaFaseActualizado = generarMockConvocatoriaFase(1L);
    convocatoriaFaseActualizado.setTipoFase(generarMockTipoFase(2L, Boolean.TRUE));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaFase));
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(
            Optional.of(generarMockModeloTipoFase(2L, convocatoria, convocatoriaFaseActualizado, Boolean.FALSE)));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaFase
        () -> service.update(convocatoriaFaseActualizado))
        // then: throw exception as ModeloTipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoFase '%s' no está activo para el ModeloEjecucion '%s'",
            convocatoriaFaseActualizado.getTipoFase().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void update_WithDisabledTipoFase_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaFase TipoFase disabled
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);
    ConvocatoriaFase convocatoriaFaseActualizado = generarMockConvocatoriaFase(1L);
    convocatoriaFaseActualizado.setTipoFase(generarMockTipoFase(2L, Boolean.FALSE));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaFase));
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(
            Optional.of(generarMockModeloTipoFase(2L, convocatoria, convocatoriaFaseActualizado, Boolean.TRUE)));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaFase
        () -> service.update(convocatoriaFaseActualizado))
        // then: throw exception as TipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no está activo", convocatoriaFaseActualizado.getTipoFase().getNombre());
  }

  @Test
  public void update_WithRangoFechasSopalado_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaFase with fechas solapadas con una convocatoria
    // existente
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);
    ConvocatoriaFase convocatoriaFaseActualizado = generarMockConvocatoriaFase(1L);
    convocatoriaFaseActualizado.setTipoFase(generarMockTipoFase(2L, Boolean.TRUE));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaFase));
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(
            Optional.of(generarMockModeloTipoFase(2L, convocatoria, convocatoriaFaseActualizado, Boolean.TRUE)));
    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ConvocatoriaFase> page = new PageImpl<>(Arrays.asList(generarMockConvocatoriaFase(1L)), pageable, 0);
          return page;

        });

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaFase
        () -> service.update(convocatoriaFaseActualizado))
        // then: throw exception as Programa is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe una convocatoria en ese rango de fechas");
  }

  @Test
  public void update_SelectedInConfiguracionSolicitudWhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaFase selected in ConfiguracionSolicitud when
    // modificable return false
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(1L);
    ConfiguracionSolicitud configuracionSolicitud = ConfiguracionSolicitud.builder()
        .fasePresentacionSolicitudes(convocatoriaFase).convocatoriaId(convocatoriaFase.getConvocatoriaId()).build();

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaFase));
    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaFase
        () -> service.update(convocatoriaFase))
        // then: throw exception as Convocatoria is not modificable
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede modificar ConvocatoriaFase. No tiene los permisos necesarios o se encuentra asignada a la ConfiguracionSolicitud de una convocatoria que está registrada y cuenta con solicitudes o proyectos asociados");
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing ConvocatoriaFase
    Long id = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoriaFase(1L)));

    BDDMockito.given(configuracionSolicitudRepository.findByFasePresentacionSolicitudesId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(Collections.emptyList()));

    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaFaseNotFoundException.class);
  }

  @Test
  public void delete_SelectedInConfiguracionSolicitudWhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: existing ConvocatoriaFase when modificable returns false
    Long id = 1L;
    ConvocatoriaFase convocatoriaFase = generarMockConvocatoriaFase(id);
    ConfiguracionSolicitud configuracionSolicitud = ConfiguracionSolicitud.builder()
        .fasePresentacionSolicitudes(convocatoriaFase).convocatoriaId(convocatoriaFase.getConvocatoriaId()).build();

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoriaFase(id)));
    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: throw exception as Convocatoria is not modificable
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede eliminar ConvocatoriaFase. No tiene los permisos necesarios o se encuentra asignada a la ConfiguracionSolicitud de una convocatoria que está registrada y cuenta con solicitudes o proyectos asociados");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  public void findAllByConvocatoria_ReturnsPage() {
    // given: Una lista con 37 ConvocatoriaFase para la Convocatoria
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    List<ConvocatoriaFase> convocatoriasEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesConvocantes.add(generarMockConvocatoriaFase(Long.valueOf(i)));
    }
    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));
    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > convocatoriasEntidadesConvocantes.size() ? convocatoriasEntidadesConvocantes.size()
              : toIndex;
          List<ConvocatoriaFase> content = convocatoriasEntidadesConvocantes.subList(fromIndex, toIndex);
          Page<ConvocatoriaFase> pageResponse = new PageImpl<>(content, pageable,
              convocatoriasEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConvocatoriaFase> page = service.findAllByConvocatoria(convocatoriaId, null, paging);

    // then: Devuelve la pagina 3 con los ConvocatoriaFase del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConvocatoriaFase convocatoriaFase = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(convocatoriaFase.getId()).isEqualTo(Long.valueOf(i));
    }
  }

  @Test
  public void findById_ReturnsConvocatoriaFase() {
    // given: Un ConvocatoriaFase con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockConvocatoriaFase(idBuscado)));

    // when: Buscamos el ConvocatoriaFase por su id
    ConvocatoriaFase convocatoriaFase = service.findById(idBuscado);

    // then: el ConvocatoriaFase
    Assertions.assertThat(convocatoriaFase).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaFase.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsConvocatoriaFaseNotFoundException() throws Exception {
    // given: Ningun ConvocatoriaFase con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ConvocatoriaFase por su id
    // then: lanza un ConvocatoriaFaseNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ConvocatoriaFaseNotFoundException.class);
  }

  /**
   * Función que genera Convocatoria
   * 
   * @param convocatoriaId
   * @param unidadGestionId
   * @param modeloEjecucionId
   * @param modeloTipoFinalidadId
   * @param tipoRegimenConcurrenciaId
   * @param tipoAmbitoGeogragicoId
   * @param activo
   * @return la convocatoria
   */
  private Convocatoria generarMockConvocatoria(Long convocatoriaId, Long unidadGestionId, Long modeloEjecucionId,
      Long modeloTipoFinalidadId, Long tipoRegimenConcurrenciaId, Long tipoAmbitoGeogragicoId, Boolean activo) {

    // @formatter:off
    ModeloEjecucion modeloEjecucion = (modeloEjecucionId == null) ? null
        : ModeloEjecucion.builder()
            .id(modeloEjecucionId)
            .nombre("nombreModeloEjecucion-" + String.format("%03d", modeloEjecucionId))
            .activo(Boolean.TRUE)
            .build();

    TipoFinalidad tipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : TipoFinalidad.builder()
            .id(modeloTipoFinalidadId)
            .nombre("nombreTipoFinalidad-" + String.format("%03d", modeloTipoFinalidadId))
            .activo(Boolean.TRUE)
            .build();

    ModeloTipoFinalidad modeloTipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : ModeloTipoFinalidad.builder()
            .id(modeloTipoFinalidadId)
            .modeloEjecucion(modeloEjecucion)
            .tipoFinalidad(tipoFinalidad)
            .activo(Boolean.TRUE)
            .build();

    TipoRegimenConcurrencia tipoRegimenConcurrencia = (tipoRegimenConcurrenciaId == null) ? null
        : TipoRegimenConcurrencia.builder()
            .id(tipoRegimenConcurrenciaId)
            .nombre("nombreTipoRegimenConcurrencia-" + String.format("%03d", tipoRegimenConcurrenciaId))
            .activo(Boolean.TRUE)
            .build();

    TipoAmbitoGeografico tipoAmbitoGeografico = (tipoAmbitoGeogragicoId == null) ? null
        : TipoAmbitoGeografico.builder()
            .id(tipoAmbitoGeogragicoId)
            .nombre("nombreTipoAmbitoGeografico-" + String.format("%03d", tipoAmbitoGeogragicoId))
            .activo(Boolean.TRUE)
            .build();

    Convocatoria convocatoria = Convocatoria.builder()
        .id(convocatoriaId)
        .unidadGestionRef((unidadGestionId == null) ? null : "unidad-" + String.format("%03d", unidadGestionId))
        .modeloEjecucion(modeloEjecucion)
        .codigo("codigo-" + String.format("%03d", convocatoriaId))
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaProvisional(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaConcesion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo-" + String.format("%03d", convocatoriaId))
        .objeto("objeto-" + String.format("%03d", convocatoriaId))
        .observaciones("observaciones-" + String.format("%03d", convocatoriaId))
        .finalidad((modeloTipoFinalidad == null) ? null : modeloTipoFinalidad.getTipoFinalidad())
        .regimenConcurrencia(tipoRegimenConcurrencia)
        .estado(Convocatoria.Estado.REGISTRADA)
        .duracion(12)
        .ambitoGeografico(tipoAmbitoGeografico)
        .clasificacionCVN(ClasificacionCVN.AYUDAS)
        .activo(activo)
        .build();
    // @formatter:on

    return convocatoria;
  }

  /**
   * Función que devuelve un objeto TipoFase
   * 
   * @param id     id del TipoFase
   * @param activo
   * @return el objeto TipoFase
   */
  private TipoFase generarMockTipoFase(Long id, Boolean activo) {

    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id);
    tipoFase.setNombre("nombre-" + id);
    tipoFase.setDescripcion("descripcion-" + id);
    tipoFase.setActivo(activo);

    return tipoFase;
  }

  /**
   * Función que genera ModeloTipoFase a partir de un objeto ConvocatoriaFase
   * 
   * @param id
   * @param convocatoriaFase
   * @param activo
   * @return
   */
  private ModeloTipoFase generarMockModeloTipoFase(Long id, Convocatoria convocatoria,
      ConvocatoriaFase convocatoriaFase, Boolean activo) {

    // @formatter:off
    return ModeloTipoFase.builder()
        .id(id)
        .modeloEjecucion(convocatoria.getModeloEjecucion())
        .tipoFase(convocatoriaFase.getTipoFase())
        .activo(activo)
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto ConvocatoriaFase
   * 
   * @param id id del ConvocatoriaFase
   * @return el objeto ConvocatoriaFase
   */
  private ConvocatoriaFase generarMockConvocatoriaFase(Long id) {

    // @formatter:off
    return ConvocatoriaFase.builder()
        .id(id)
        .convocatoriaId(1L)
        .fechaInicio(Instant.parse("2020-10-19T00:00:00Z"))
        .fechaFin(Instant.parse("2020-10-28T00:00:00Z"))
        .tipoFase(generarMockTipoFase(1L, Boolean.TRUE))
        .observaciones("observaciones" + id)
        .build();
    // @formatter:on
  }

  private Convocatoria generarMockConvocatoria(Long convocatoriaId) {
    return Convocatoria.builder().id(convocatoriaId).build();
  }

}
