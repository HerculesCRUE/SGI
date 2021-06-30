package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoEnlaceRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaEnlaceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;

/**
 * ConvocatoriaEnlaceServiceTest
 */
public class ConvocatoriaEnlaceServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaEnlaceRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ModeloTipoEnlaceRepository modeloTipoEnlaceRepository;

  private ConvocatoriaEnlaceService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaEnlaceServiceImpl(repository, convocatoriaRepository, modeloTipoEnlaceRepository);
  }

  @Test
  public void create_ReturnsConvocatoriaEnlace() {
    // given: Un nuevo ConvocatoriaEnlace
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(null, convocatoriaId);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.findByConvocatoriaIdAndUrl(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.empty());

    BDDMockito
        .given(modeloTipoEnlaceRepository.findByModeloEjecucionIdAndTipoEnlaceId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoEnlace(1L, convocatoriaEnlace, Boolean.TRUE, convocatoria)));

    BDDMockito.given(repository.save(convocatoriaEnlace)).will((InvocationOnMock invocation) -> {
      ConvocatoriaEnlace convocatoriaEnlaceCreado = invocation.getArgument(0);
      convocatoriaEnlaceCreado.setId(1L);
      return convocatoriaEnlaceCreado;
    });

    // when: Creamos el ConvocatoriaEnlace
    ConvocatoriaEnlace convocatoriaEnlaceCreado = service.create(convocatoriaEnlace);

    // then: El ConvocatoriaEnlace se crea correctamente
    Assertions.assertThat(convocatoriaEnlaceCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaEnlaceCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaEnlaceCreado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(convocatoriaEnlace.getDescripcion());
    Assertions.assertThat(convocatoriaEnlaceCreado.getUrl()).as("getUrl()").isEqualTo(convocatoriaEnlace.getUrl());
    Assertions.assertThat(convocatoriaEnlace.getTipoEnlace()).as("getTipoEnlace()")
        .isEqualTo(convocatoriaEnlace.getTipoEnlace());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaEnlace que ya tiene id
    Long convocatoriaId = 1L;
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L, convocatoriaId);

    // when: Creamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque el ConvocatoriaEnlace ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaEnlace)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConvocatoriaEnlace id tiene que ser null para crear un nuevo ConvocatoriaEnlace");
  }

  @Test
  public void create_WithoutUrl_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaEnlace sin url
    Long convocatoriaId = 1L;
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(null, convocatoriaId);
    convocatoriaEnlace.setUrl(null);

    // when: Creamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque la url es null
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaEnlace)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConvocatoriaEnlace url no puede ser null para crear una nueva ConvocatoriaEnlace");
  }

  @Test
  public void create_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaEnlace sin convocatoria
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(null, null);

    // when: Creamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque la convocatoria es null
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaEnlace)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Convocatoria no puede ser null para crear ConvocatoriaEnlace");
  }

  @Test
  public void create_WithUrlDuplicada_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaEnlace con el enlace inactivo
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaEnlace convocatoriaEnlace1 = generarMockConvocatoriaEnlace(3L, convocatoriaId);
    convocatoriaEnlace1.setUrl("www.duplicada.com");
    ConvocatoriaEnlace convocatoriaEnlace2 = generarMockConvocatoriaEnlace(null, convocatoriaId);
    convocatoriaEnlace1.setUrl("www.duplicada.com");

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.findByConvocatoriaIdAndUrl(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.of(convocatoriaEnlace1));

    // when: Creamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque la url es duplicada
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaEnlace2))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe esa url para esta Convocatoria");
  }

  @Test
  public void create_WithoutModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaEnlace con Convocatoria sin Modelo de Ejecucion
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(null, convocatoriaId);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setModeloEjecucion(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEnlace
        () -> service.create(convocatoriaEnlace))
        // then: throw exception as ModeloEjecucion not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoEnlace '%s' no disponible para el ModeloEjecucion '%s'",
            convocatoriaEnlace.getTipoEnlace().getNombre(), "Convocatoria sin modelo asignado");
  }

  @Test
  public void create_WithoutModeloTipoEnlace_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaEnlace con TipoEnlace no asignado al Modelo de Ejecucion
    // de la
    // convocatoria
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(null, convocatoriaId);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(modeloTipoEnlaceRepository.findByModeloEjecucionIdAndTipoEnlaceId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEnlace
        () -> service.create(convocatoriaEnlace))
        // then: throw exception as ModeloTipoEnlace not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoEnlace '%s' no disponible para el ModeloEjecucion '%s'",
            convocatoriaEnlace.getTipoEnlace().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void create_WithDisabledModeloTipoEnlace_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaEnlace con la asignación de TipoEnlace al Modelo de
    // Ejecucion
    // de la convocatoria inactiva
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(null, convocatoriaId);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito
        .given(modeloTipoEnlaceRepository.findByModeloEjecucionIdAndTipoEnlaceId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoEnlace(1L, convocatoriaEnlace, Boolean.FALSE, convocatoria)));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEnlace
        () -> service.create(convocatoriaEnlace))
        // then: throw exception as ModeloTipoEnlace is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoEnlace '%s' no está activo para el ModeloEjecucion '%s'",
            convocatoriaEnlace.getTipoEnlace().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void create_WithDisabledTipoEnlace_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaEnlace TipoEnlace disabled
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(null, convocatoriaId);
    convocatoriaEnlace.getTipoEnlace().setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito
        .given(modeloTipoEnlaceRepository.findByModeloEjecucionIdAndTipoEnlaceId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoEnlace(1L, convocatoriaEnlace, Boolean.TRUE, convocatoria)));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEnlace
        () -> service.create(convocatoriaEnlace))
        // then: throw exception as TipoEnlace is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoEnlace '%s' no está activo", convocatoriaEnlace.getTipoEnlace().getNombre());
  }

  @Test
  public void update_ReturnsConvocatoriaEnlace() {
    // given: Un nuevo ConvocatoriaEnlace con el nombre actualizado
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    ConvocatoriaEnlace convocatoriaEnlaceDescripcionActualizada = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    convocatoriaEnlaceDescripcionActualizada.setDescripcion("nuevaDescripcion");

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaEnlace));
    BDDMockito.given(repository.findByConvocatoriaIdAndUrl(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.empty());
    BDDMockito
        .given(modeloTipoEnlaceRepository.findByModeloEjecucionIdAndTipoEnlaceId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional
            .of(generarMockModeloTipoEnlace(1L, convocatoriaEnlaceDescripcionActualizada, Boolean.TRUE, convocatoria)));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaEnlace>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ConvocatoriaEnlace
    ConvocatoriaEnlace convocatoriaEnlaceActualizado = service.update(convocatoriaEnlaceDescripcionActualizada);

    // then: El ConvocatoriaEnlace se actualiza correctamente.
    Assertions.assertThat(convocatoriaEnlaceActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaEnlaceActualizado.getId()).as("getId()").isEqualTo(convocatoriaEnlace.getId());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(convocatoriaEnlaceDescripcionActualizada.getDescripcion());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getTipoEnlace()).as("getTipoEnlace()")
        .isEqualTo(convocatoriaEnlace.getTipoEnlace());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getUrl()).as("getUrl()").isEqualTo(convocatoriaEnlace.getUrl());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaEnlace.getConvocatoriaId());
  }

  @Test
  public void update_WithIdNotExist_ThrowsConvocatoriaEnlaceNotFoundException() {
    // given: Un ConvocatoriaEnlace actualizado con un id que no existe
    Long convocatoriaId = 1L;
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L, convocatoriaId);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque el ConvocatoriaEnlace no existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaEnlace))
        .isInstanceOf(ConvocatoriaEnlaceNotFoundException.class);
  }

  @Test
  public void update_WithoutUrl_ThrowsIllegalArgumentException() {
    // given: Un ConvocatoriaEnlace actualizado con sin url
    Long convocatoriaId = 1L;
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    convocatoriaEnlace.setUrl(null);

    // when: Actualizamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque el ConvocatoriaEnlace no existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaEnlace)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConvocatoriaEnlace url no puede ser null para actualizar un nuevo ConvocatoriaEnlace");
  }

  @Test
  public void update_WithoutTipoEnlace_ReturnsConvocatoriaEnlace() {
    // given: Un nuevo ConvocatoriaEnlace con el nombre actualizado
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    ConvocatoriaEnlace convocatoriaEnlaceDescripcionActualizada = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    convocatoriaEnlaceDescripcionActualizada.getTipoEnlace().setId(null);
    convocatoriaEnlaceDescripcionActualizada.setDescripcion("nuevaDescripcion");

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaEnlace));
    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaEnlace>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ConvocatoriaEnlace
    ConvocatoriaEnlace convocatoriaEnlaceActualizado = service.update(convocatoriaEnlaceDescripcionActualizada);

    // then: El ConvocatoriaEnlace se actualiza correctamente.
    Assertions.assertThat(convocatoriaEnlaceActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaEnlaceActualizado.getId()).as("getId()").isEqualTo(convocatoriaEnlace.getId());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(convocatoriaEnlaceDescripcionActualizada.getDescripcion());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getTipoEnlace()).as("getTipoEnlace()").isNull();
    Assertions.assertThat(convocatoriaEnlaceActualizado.getUrl()).as("getUrl()").isEqualTo(convocatoriaEnlace.getUrl());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaEnlace.getConvocatoriaId());
  }

  @Test
  public void update_WithDuplicatedUrl_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEnlace con la url duplicada
    Long convocatoriaId = 1L;
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    ConvocatoriaEnlace convocatoriaEnlaceExistente = generarMockConvocatoriaEnlace(2L, convocatoriaId);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaEnlace));
    BDDMockito.given(repository.findByConvocatoriaIdAndUrl(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.of(convocatoriaEnlaceExistente));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEnlace
        () -> service.update(convocatoriaEnlace))
        // then: Lanza una excepcion porque la url es duplicada
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe esa url para esta Convocatoria");
  }

  @Test
  public void update_WithoutModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaEnlace con Convocatoria sin Modelo de Ejecucion
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    ConvocatoriaEnlace convocatoriaEnlaceActualizado = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    convocatoriaEnlaceActualizado.setTipoEnlace(generarMockTipoEnlace(2L, Boolean.TRUE));
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    convocatoria.setModeloEjecucion(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaEnlace));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEnlace
        () -> service.update(convocatoriaEnlaceActualizado))
        // then: throw exception as ModeloTipoEnlace not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoEnlace '%s' no disponible para el ModeloEjecucion '%s'",
            convocatoriaEnlaceActualizado.getTipoEnlace().getNombre(), "Convocatoria sin modelo asignado");
  }

  @Test
  public void update_WithoutModeloTipoEnlace_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaEnlace con TipoEnlace no asignado al Modelo de Ejecucion
    // de la
    // convocatoria
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    ConvocatoriaEnlace convocatoriaEnlaceActualizado = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    convocatoriaEnlaceActualizado.setTipoEnlace(generarMockTipoEnlace(2L, Boolean.TRUE));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaEnlace));
    BDDMockito.given(modeloTipoEnlaceRepository.findByModeloEjecucionIdAndTipoEnlaceId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEnlace
        () -> service.update(convocatoriaEnlaceActualizado))
        // then: throw exception as ModeloTipoEnlace not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoEnlace '%s' no disponible para el ModeloEjecucion '%s'",
            convocatoriaEnlaceActualizado.getTipoEnlace().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void update_WithDisabledModeloTipoEnlace_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaEnlace con la asignación de TipoEnlace al Modelo de
    // Ejecucion
    // de la convocatoria inactiva
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    ConvocatoriaEnlace convocatoriaEnlaceActualizado = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    convocatoriaEnlaceActualizado.setTipoEnlace(generarMockTipoEnlace(2L, Boolean.FALSE));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaEnlace));
    BDDMockito
        .given(modeloTipoEnlaceRepository.findByModeloEjecucionIdAndTipoEnlaceId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(
            Optional.of(generarMockModeloTipoEnlace(2L, convocatoriaEnlaceActualizado, Boolean.FALSE, convocatoria)));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEnlace
        () -> service.update(convocatoriaEnlaceActualizado))
        // then: throw exception as ModeloTipoEnlace is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoEnlace '%s' no está activo para el ModeloEjecucion '%s'",
            convocatoriaEnlaceActualizado.getTipoEnlace().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void update_WithDisabledTipoEnlace_ThrowsIllegalArgumentException() {
    // given: ConvocatoriaEnlace TipoEnlace disabled
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    ConvocatoriaEnlace convocatoriaEnlaceActualizado = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    convocatoriaEnlaceActualizado.setTipoEnlace(generarMockTipoEnlace(2L, Boolean.FALSE));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaEnlace));
    BDDMockito
        .given(modeloTipoEnlaceRepository.findByModeloEjecucionIdAndTipoEnlaceId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(
            Optional.of(generarMockModeloTipoEnlace(2L, convocatoriaEnlaceActualizado, Boolean.TRUE, convocatoria)));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEnlace
        () -> service.update(convocatoriaEnlaceActualizado))
        // then: throw exception as TipoEnlace is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoEnlace '%s' no está activo", convocatoriaEnlaceActualizado.getTipoEnlace().getNombre());
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing convocatoriaEnlace
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() throws Exception {
    // given: no id
    Long id = null;

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: IllegalArgumentException is thrown
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaEnlaceNotFoundException.class);
  }

  @Test
  public void findById_ReturnsConvocatoriaEnlace() {
    // given: Un ConvocatoriaEnlace con el id buscado
    Long idBuscado = 1L;
    Long convocatoriaId = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockConvocatoriaEnlace(idBuscado, convocatoriaId)));

    // when: Buscamos el ConvocatoriaEnlace por su id
    ConvocatoriaEnlace convocatoriaEnlace = service.findById(idBuscado);

    // then: el ConvocatoriaEnlace
    Assertions.assertThat(convocatoriaEnlace).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaEnlace.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(convocatoriaEnlace.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-1");
    Assertions.assertThat(convocatoriaEnlace.getConvocatoriaId()).as("getConvocatoriaId()").isEqualTo(convocatoriaId);

  }

  @Test
  public void findById_WithIdNotExist_ThrowsConvocatoriaEnlaceNotFoundException() throws Exception {
    // given: Ningun ConvocatoriaEnlace con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ConvocatoriaEnlace por su id
    // then: lanza un ConvocatoriaEnlaceNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ConvocatoriaEnlaceNotFoundException.class);
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
        .colaborativos(Boolean.TRUE)
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
   * Función que devuelve un objeto TipoEnlace
   * 
   * @param id     id del TipoEnlace
   * @param activo
   * @return el objeto TipoEnlace
   */
  private TipoEnlace generarMockTipoEnlace(Long id, Boolean activo) {

    TipoEnlace tipoEnlace = new TipoEnlace();
    tipoEnlace.setId(id);
    tipoEnlace.setNombre("nombre-" + id);
    tipoEnlace.setDescripcion("descripcion-" + id);
    tipoEnlace.setActivo(activo);

    return tipoEnlace;
  }

  /**
   * Función que genera ModeloTipoEnlace a partir de un objeto ConvocatoriaEnlace
   * 
   * @param id
   * @param convocatoriaEnlace
   * @param activo
   * @return
   */
  private ModeloTipoEnlace generarMockModeloTipoEnlace(Long id, ConvocatoriaEnlace convocatoriaEnlace, Boolean activo,
      Convocatoria convocatoria) {

    // @formatter:off
    return ModeloTipoEnlace.builder()
        .id(id)
        .modeloEjecucion(convocatoria.getModeloEjecucion())
        .tipoEnlace(convocatoriaEnlace.getTipoEnlace())
        .activo(activo)
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto ConvocatoriaEnlace
   * 
   * @param id id del ConvocatoriaEnlace
   * @return el objeto ConvocatoriaEnlace
   */
  private ConvocatoriaEnlace generarMockConvocatoriaEnlace(Long id, Long convocatoriaId) {

    // @formatter:off
    return ConvocatoriaEnlace.builder()
        .id(id)
        .convocatoriaId(convocatoriaId)
        .descripcion("descripcion-" + id)
        .url("www.url" + id + ".es")
        .tipoEnlace(generarMockTipoEnlace(1L, Boolean.TRUE))
        .build();
    // @formatter:on
  }

}
