package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlaceDescripcion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaObjeto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaObservaciones;
import org.crue.hercules.sgi.csp.model.ConvocatoriaTitulo;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeograficoNombre;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlaceDescripcion;
import org.crue.hercules.sgi.csp.model.TipoEnlaceNombre;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidadNombre;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrenciaNombre;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoEnlaceRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaEnlaceServiceImpl;
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;

/**
 * ConvocatoriaEnlaceServiceTest
 */
class ConvocatoriaEnlaceServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaEnlaceRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ModeloTipoEnlaceRepository modeloTipoEnlaceRepository;
  @Mock
  private ConvocatoriaAuthorityHelper authorityHelper;

  private ConvocatoriaEnlaceService service;

  @BeforeEach
  void setUp() {
    service = new ConvocatoriaEnlaceServiceImpl(repository, convocatoriaRepository, modeloTipoEnlaceRepository,
        authorityHelper);
  }

  @Test
  void create_ReturnsConvocatoriaEnlace() {
    // given: Un nuevo ConvocatoriaEnlace
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(null, convocatoriaId);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

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
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaEnlace que ya tiene id
    Long convocatoriaId = 1L;
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L, convocatoriaId);

    // when: Creamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque el ConvocatoriaEnlace ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaEnlace)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Convocatoria Enlace debe ser nulo");
  }

  @Test
  void create_WithoutUrl_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaEnlace sin url
    Long convocatoriaId = 1L;
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(null, convocatoriaId);
    convocatoriaEnlace.setUrl(null);

    // when: Creamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque la url es null
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaEnlace)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("URL de Convocatoria Enlace no puede ser nulo");
  }

  @Test
  void create_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaEnlace sin convocatoria
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(null, null);

    // when: Creamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque la convocatoria es null
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaEnlace)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Convocatoria no puede ser nulo");
  }

  @Test
  void create_WithoutModeloEjecucion_ThrowsIllegalArgumentException() {
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
        .hasMessage("Tipo Enlace no disponible para el Modelo Ejecución Convocatoria sin modelo asignado");
  }

  @Test
  void create_WithoutModeloTipoEnlace_ThrowsIllegalArgumentException() {
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
        .hasMessage("Tipo Enlace no disponible para el Modelo Ejecución %s",
            convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  void create_WithDisabledModeloTipoEnlace_ThrowsIllegalArgumentException() {
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
        .hasMessage("%s de Modelo Tipo Enlace no está activo",
            convocatoriaEnlace.getTipoEnlace().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  void create_WithDisabledTipoEnlace_ThrowsIllegalArgumentException() {
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
        .hasMessage("%s de Tipo Enlace no está activo", convocatoriaEnlace.getTipoEnlace().getNombre());
  }

  @Test
  void update_ReturnsConvocatoriaEnlace() {
    // given: Un nuevo ConvocatoriaEnlace con el nombre actualizado
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    ConvocatoriaEnlace convocatoriaEnlaceDescripcionActualizada = generarMockConvocatoriaEnlace(1L, convocatoriaId);

    Set<ConvocatoriaEnlaceDescripcion> descripcionConvocatoriaEnlace = new HashSet<>();
    descripcionConvocatoriaEnlace.add(new ConvocatoriaEnlaceDescripcion(Language.ES, "nuevaDescripcion"));
    convocatoriaEnlaceDescripcionActualizada.setDescripcion(descripcionConvocatoriaEnlace);
    convocatoriaEnlaceDescripcionActualizada.setDescripcion(descripcionConvocatoriaEnlace);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(convocatoriaEnlace));
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
    Assertions.assertThat(I18nHelper.getValueForLanguage(convocatoriaEnlaceActualizado.getDescripcion(), Language.ES))
        .as("getDescripcion()")
        .isEqualTo(
            I18nHelper.getValueForLanguage(convocatoriaEnlaceDescripcionActualizada.getDescripcion(), Language.ES));
    Assertions.assertThat(convocatoriaEnlaceActualizado.getTipoEnlace()).as("getTipoEnlace()")
        .isEqualTo(convocatoriaEnlace.getTipoEnlace());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getUrl()).as("getUrl()").isEqualTo(convocatoriaEnlace.getUrl());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaEnlace.getConvocatoriaId());
  }

  @Test
  void update_WithIdNotExist_ThrowsConvocatoriaEnlaceNotFoundException() {
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
  void update_WithoutUrl_ThrowsIllegalArgumentException() {
    // given: Un ConvocatoriaEnlace actualizado con sin url
    Long convocatoriaId = 1L;
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    convocatoriaEnlace.setUrl(null);

    // when: Actualizamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque el ConvocatoriaEnlace no existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaEnlace)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("URL de Convocatoria Enlace no puede ser nulo");
  }

  @Test
  void update_WithoutTipoEnlace_ReturnsConvocatoriaEnlace() {
    // given: Un nuevo ConvocatoriaEnlace con el nombre actualizado
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaEnlace convocatoriaEnlace = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    ConvocatoriaEnlace convocatoriaEnlaceDescripcionActualizada = generarMockConvocatoriaEnlace(1L, convocatoriaId);
    convocatoriaEnlaceDescripcionActualizada.getTipoEnlace().setId(null);

    Set<ConvocatoriaEnlaceDescripcion> descripcionConvocatoriaEnlace = new HashSet<>();
    descripcionConvocatoriaEnlace.add(new ConvocatoriaEnlaceDescripcion(Language.ES, "nuevaDescripcion"));
    convocatoriaEnlaceDescripcionActualizada.setDescripcion(descripcionConvocatoriaEnlace);

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
    Assertions.assertThat(I18nHelper.getValueForLanguage(convocatoriaEnlaceActualizado.getDescripcion(), Language.ES))
        .as("getDescripcion()")
        .isEqualTo(
            I18nHelper.getValueForLanguage(convocatoriaEnlaceDescripcionActualizada.getDescripcion(), Language.ES));
    Assertions.assertThat(convocatoriaEnlaceActualizado.getTipoEnlace()).as("getTipoEnlace()").isNull();
    Assertions.assertThat(convocatoriaEnlaceActualizado.getUrl()).as("getUrl()").isEqualTo(convocatoriaEnlace.getUrl());
    Assertions.assertThat(convocatoriaEnlaceActualizado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaEnlace.getConvocatoriaId());
  }

  @Test
  void update_WithoutModeloEjecucion_ThrowsIllegalArgumentException() {
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
        .hasMessage("Tipo Enlace no disponible para el Modelo Ejecución Convocatoria sin modelo asignado");
  }

  @Test
  void update_WithoutModeloTipoEnlace_ThrowsIllegalArgumentException() {
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
        .hasMessage("Tipo Enlace no disponible para el Modelo Ejecución %s",
            convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  void update_WithDisabledModeloTipoEnlace_ThrowsIllegalArgumentException() {
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
        .hasMessage("%s de Modelo Tipo Enlace no está activo para el modelo ejecución %s",
            convocatoriaEnlaceActualizado.getTipoEnlace().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  void update_WithDisabledTipoEnlace_ThrowsIllegalArgumentException() {
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
        .hasMessage("%s de Tipo Enlace no está activo", convocatoriaEnlaceActualizado.getTipoEnlace().getNombre());
  }

  @Test
  void delete_WithExistingId_NoReturnsAnyException() {
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
  void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: no id
    Long id = null;

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: IllegalArgumentException is thrown
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void delete_WithNoExistingId_ThrowsNotFoundException() {
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
  void findById_ReturnsConvocatoriaEnlace() {
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
    Assertions.assertThat(I18nHelper.getValueForLanguage(convocatoriaEnlace.getDescripcion(), Language.ES))
        .as("getDescripcion()").isEqualTo("descripcion-1");
    Assertions.assertThat(convocatoriaEnlace.getConvocatoriaId()).as("getConvocatoriaId()").isEqualTo(convocatoriaId);

  }

  @Test
  void findById_WithIdNotExist_ThrowsConvocatoriaEnlaceNotFoundException() {
    // given: Ningun ConvocatoriaEnlace con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ConvocatoriaEnlace por su id
    // then: lanza un ConvocatoriaEnlaceNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ConvocatoriaEnlaceNotFoundException.class);
  }

  @Test
  void update_RepeatedUrl_ThrowsIllegalArgumentException() {
    // given: Crear dos ConvocatoriaEnlace con misma url
    Long convocatoriaId = 1L;
    String url = "www.ejemplo.es";
    ConvocatoriaEnlace enlace1 = generarMockConvocatoriaEnlace(null, null);
    enlace1.setUrl(url);
    ConvocatoriaEnlace enlace2 = generarMockConvocatoriaEnlace(null, null);
    enlace2.setUrl(url);
    List<ConvocatoriaEnlace> convocatoriaEnlacesToCreate = Arrays.asList(enlace1, enlace2);

    // when: Cuando creamos estos dos elementos
    // then: Lanza una IllegalArgumentException
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaId, convocatoriaEnlacesToCreate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_EmptyArray_ReturnsEmptyList() {
    // given: Una lista vacia de ConvocatoriaEnlace
    Long convocatoriaId = 1L;
    List<ConvocatoriaEnlace> convocatoriaEnlacesToCreate = new ArrayList<>();

    // when: Cuando le pasamos una lista vacia
    // then: Devuelve una lista vacia
    List<ConvocatoriaEnlace> response = service.update(convocatoriaId, convocatoriaEnlacesToCreate);

    Assertions.assertThat(response).isEmpty();
  }

  @Test
  void update__ReturnsConvocatoriaEnlaceList() {
    // given: Una lista con una ConvocatoriaEnlace
    Long convocatoriaId = 1L;
    String url = "www.ejemplo.es";
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConvocatoriaEnlace enlace1 = generarMockConvocatoriaEnlace(null, null);
    enlace1.setUrl(url);
    List<ConvocatoriaEnlace> convocatoriaEnlacesToCreate = Arrays.asList(enlace1);

    // when: Cuando le pasamos una lista no vacia
    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito
        .given(modeloTipoEnlaceRepository.findByModeloEjecucionIdAndTipoEnlaceId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockModeloTipoEnlace(1L, enlace1, Boolean.TRUE, convocatoria)));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaEnlace>any()))
        .will((InvocationOnMock invocation) -> {
          ConvocatoriaEnlace convocatoriaEnlaceCreado = invocation.getArgument(0);
          convocatoriaEnlaceCreado.setId(1L);
          return convocatoriaEnlaceCreado;
        });
    // then: Devuelve una lista con el elemento creado
    List<ConvocatoriaEnlace> response = service.update(convocatoriaId, convocatoriaEnlacesToCreate);

    Assertions.assertThat(response).hasSize(1);

    Assertions.assertThat(response.get(0).getConvocatoriaId()).as("response.get(0).getConvocatoriaId()")
        .isEqualTo(convocatoriaId);
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
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(
        new ModeloEjecucionNombre(Language.ES, "nombreModeloEjecucion-" + String.format("%03d", modeloEjecucionId)));

    ModeloEjecucion modeloEjecucion = (modeloEjecucionId == null) ? null
        : ModeloEjecucion.builder()
            .id(modeloEjecucionId)
            .nombre(nombreModeloEjecucion)
            .activo(Boolean.TRUE)
            .build();

    Set<TipoFinalidadNombre> nombreTipoFinalidad = new HashSet<>();
    nombreTipoFinalidad.add(
        new TipoFinalidadNombre(Language.ES, "nombreTipoFinalidad-" + String.format("%03d", modeloTipoFinalidadId)));

    TipoFinalidad tipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : TipoFinalidad.builder()
            .id(modeloTipoFinalidadId)
            .nombre(nombreTipoFinalidad)
            .activo(Boolean.TRUE)
            .build();

    ModeloTipoFinalidad modeloTipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : ModeloTipoFinalidad.builder()
            .id(modeloTipoFinalidadId)
            .modeloEjecucion(modeloEjecucion)
            .tipoFinalidad(tipoFinalidad)
            .activo(Boolean.TRUE)
            .build();

    Set<TipoRegimenConcurrenciaNombre> tipoRegimenConcurrenciaNombre = new HashSet<>();
    tipoRegimenConcurrenciaNombre.add(new TipoRegimenConcurrenciaNombre(Language.ES,
        "nombreTipoRegimenConcurrencia-" + String.format("%03d", tipoRegimenConcurrenciaId)));

    TipoRegimenConcurrencia tipoRegimenConcurrencia = (tipoRegimenConcurrenciaId == null) ? null
        : TipoRegimenConcurrencia.builder()
            .id(tipoRegimenConcurrenciaId)
            .nombre(tipoRegimenConcurrenciaNombre)
            .activo(Boolean.TRUE)
            .build();

    Set<TipoAmbitoGeograficoNombre> nombre = new HashSet<>();
    nombre.add(new TipoAmbitoGeograficoNombre(Language.ES,
        "nombreTipoAmbitoGeografico-" + String.format("%03d", tipoAmbitoGeogragicoId)));

    TipoAmbitoGeografico tipoAmbitoGeografico = (tipoAmbitoGeogragicoId == null) ? null
        : TipoAmbitoGeografico.builder()
            .id(tipoAmbitoGeogragicoId)
            .nombre(nombre)
            .activo(Boolean.TRUE)
            .build();

    Set<ConvocatoriaTitulo> convocatoriaTitulo = new HashSet<>();
    convocatoriaTitulo.add(new ConvocatoriaTitulo(Language.ES, "titulo-" + String.format("%03d", convocatoriaId)));

    Set<ConvocatoriaObjeto> convocatoriaObjeto = new HashSet<>();
    convocatoriaObjeto.add(new ConvocatoriaObjeto(Language.ES, "objeto-" + String.format("%03d", convocatoriaId)));

    Set<ConvocatoriaObservaciones> convocatoriaObservaciones = new HashSet<>();
    convocatoriaObservaciones
        .add(new ConvocatoriaObservaciones(Language.ES, "observaciones-" + String.format("%03d", convocatoriaId)));

    return Convocatoria.builder()
        .id(convocatoriaId)
        .unidadGestionRef((unidadGestionId == null) ? null : "unidad-" + String.format("%03d", unidadGestionId))
        .modeloEjecucion(modeloEjecucion)
        .codigo("codigo-" + String.format("%03d", convocatoriaId))
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaProvisional(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaConcesion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo(convocatoriaTitulo)
        .objeto(convocatoriaObjeto)
        .observaciones(convocatoriaObservaciones)
        .finalidad((modeloTipoFinalidad == null) ? null : modeloTipoFinalidad.getTipoFinalidad())
        .regimenConcurrencia(tipoRegimenConcurrencia)
        .estado(Convocatoria.Estado.REGISTRADA)
        .duracion(12)
        .ambitoGeografico(tipoAmbitoGeografico)
        .clasificacionCVN(ClasificacionCVN.AYUDAS)
        .activo(activo)
        .build();
  }

  /**
   * Función que devuelve un objeto TipoEnlace
   * 
   * @param id     id del TipoEnlace
   * @param activo
   * @return el objeto TipoEnlace
   */
  private TipoEnlace generarMockTipoEnlace(Long id, Boolean activo) {
    Set<TipoEnlaceNombre> nombre = new HashSet<>();
    nombre.add(new TipoEnlaceNombre(Language.ES, "nombre-" + id));

    Set<TipoEnlaceDescripcion> descripcion = new HashSet<>();
    descripcion.add(new TipoEnlaceDescripcion(Language.ES, "descripcion-" + id));

    TipoEnlace tipoEnlace = new TipoEnlace();
    tipoEnlace.setId(id);
    tipoEnlace.setNombre(nombre);
    tipoEnlace.setDescripcion(descripcion);
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
    Set<ConvocatoriaEnlaceDescripcion> descripcionConvocatoriaEnlace = new HashSet<>();
    descripcionConvocatoriaEnlace.add(new ConvocatoriaEnlaceDescripcion(Language.ES, "descripcion-" + id));

    // @formatter:off
    return ConvocatoriaEnlace.builder()
        .id(id)
        .convocatoriaId(convocatoriaId)
        .descripcion(descripcionConvocatoriaEnlace)
        .url("www.url" + id + ".es")
        .tipoEnlace(generarMockTipoEnlace(1L, Boolean.TRUE))
        .build();
    // @formatter:on
  }

}
