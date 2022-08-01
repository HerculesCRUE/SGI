package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEntidadFinanciadoraNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.FuenteFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.FuenteFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.TipoFinanciacionRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaEntidadFinanciadoraServiceImpl;
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
 * ConvocatoriaEntidadFinanciadoraServiceTest
 */
class ConvocatoriaEntidadFinanciadoraServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaEntidadFinanciadoraRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private FuenteFinanciacionRepository fuenteFinanciacionRepository;
  @Mock
  private TipoFinanciacionRepository tipoFinanciacionRepository;
  @Mock
  private ConvocatoriaService convocatoriaService;
  @Mock
  ConfiguracionSolicitudRepository configuracionSolicitudRepository;

  private ConvocatoriaEntidadFinanciadoraService service;

  @BeforeEach
  void setUp() throws Exception {
    service = new ConvocatoriaEntidadFinanciadoraServiceImpl(repository, convocatoriaRepository,
        fuenteFinanciacionRepository, tipoFinanciacionRepository, convocatoriaService,
        configuracionSolicitudRepository);
  }

  @Test
  void create_ReturnsConvocatoriaEntidadFinanciadora() {
    // given: Un nuevo ConvocatoriaEntidadFinanciadora
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(null,
        convocatoriaId);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora.getTipoFinanciacion()));

    BDDMockito.given(repository.save(convocatoriaEntidadFinanciadora)).will((InvocationOnMock invocation) -> {
      ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadoraCreado = invocation.getArgument(0);
      convocatoriaEntidadFinanciadoraCreado.setId(1L);
      return convocatoriaEntidadFinanciadoraCreado;
    });

    // when: Creamos el ConvocatoriaEntidadFinanciadora
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadoraCreado = service
        .create(convocatoriaEntidadFinanciadora);

    // then: El ConvocatoriaEntidadFinanciadora se crea correctamente
    Assertions.assertThat(convocatoriaEntidadFinanciadoraCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaEntidadFinanciadoraCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaEntidadFinanciadoraCreado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaEntidadFinanciadora.getConvocatoriaId());
    Assertions.assertThat(convocatoriaEntidadFinanciadoraCreado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(convocatoriaEntidadFinanciadora.getEntidadRef());
    Assertions.assertThat(convocatoriaEntidadFinanciadoraCreado.getFuenteFinanciacion().getId())
        .as("getFuenteFinanciacion().getId()")
        .isEqualTo(convocatoriaEntidadFinanciadora.getFuenteFinanciacion().getId());
    Assertions.assertThat(convocatoriaEntidadFinanciadoraCreado.getTipoFinanciacion().getId())
        .as("getTipoFinanciacion().getId()").isEqualTo(convocatoriaEntidadFinanciadora.getTipoFinanciacion().getId());
    Assertions.assertThat(convocatoriaEntidadFinanciadoraCreado.getPorcentajeFinanciacion())
        .as("getPorcentajeFinanciacion())").isEqualTo(convocatoriaEntidadFinanciadora.getPorcentajeFinanciacion());
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaEntidadFinanciadora que ya tiene id
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(1L,
        convocatoriaId);

    // when: Creamos el ConvocatoriaEntidadFinanciadora
    // then: Lanza una excepcion porque el ConvocatoriaEntidadFinanciadora ya tiene
    // id
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaEntidadFinanciadora))
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "ConvocatoriaEntidadFinanciadora id tiene que ser null para crear un nuevo ConvocatoriaEntidadFinanciadora");
  }

  @Test
  void create_WithNegativePorcentajeFinanciacion_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaEntidadFinanciadora con porcentaje negativo
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(null,
        convocatoriaId);
    convocatoriaEntidadFinanciadora.setPorcentajeFinanciacion(BigDecimal.valueOf(-10));

    // when: Creamos el ConvocatoriaEntidadFinanciadora
    // then: Lanza una excepcion porque el PorcentajeFinanciacion es negativo
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaEntidadFinanciadora))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("PorcentajeFinanciacion no puede ser negativo");
  }

  @Test
  void create_WithoutConvocatoriaId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadFinanciadora without ConvocatoriaId
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(null,
        null);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadFinanciadora
        () -> service.create(convocatoriaEntidadFinanciadora))
        // then: throw exception as ConvocatoriaId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Convocatoria no puede ser null para crear ConvocatoriaEntidadFinanciadora");
  }

  @Test
  void create_WithNoExistingConvocatoria_ThrowsConvocatoriaNotFoundException() {
    // given: a ConvocatoriaEntidadFinanciadora with non existing Convocatoria
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(null,
        convocatoriaId);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadFinanciadora
        () -> service.create(convocatoriaEntidadFinanciadora))
        // then: throw exception as Convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  void create_WithNoExistingFuenteFinanciacion_ThrowsFuenteFinanciacionNotFoundException() {
    // given: a ConvocatoriaEntidadFinanciadora with non existing FuenteFinanciacion
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(null,
        convocatoriaId);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadFinanciadora
        () -> service.create(convocatoriaEntidadFinanciadora))
        // then: throw exception as FuenteFinanciacion is not found
        .isInstanceOf(FuenteFinanciacionNotFoundException.class);
  }

  @Test
  void create_WithFuenteFinanciacionActivoFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadFinanciadora with FuenteFinanciacion activo=false
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(null,
        convocatoriaId);
    convocatoriaEntidadFinanciadora.getFuenteFinanciacion().setActivo(false);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora.getFuenteFinanciacion()));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadFinanciadora
        () -> service.create(convocatoriaEntidadFinanciadora))
        // then: throw exception as FuenteFinanciacion is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("La FuenteFinanciacion debe estar Activo");
  }

  @Test
  void create_WithNoExistingTipoFinanciacion_ThrowsFuenteFinanciacionNotFoundException() {
    // given: a ConvocatoriaEntidadFinanciadora with non existing TipoFinanciacion
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(null,
        convocatoriaId);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadFinanciadora
        () -> service.create(convocatoriaEntidadFinanciadora))
        // then: throw exception as TipoFinanciacion is not found
        .isInstanceOf(TipoFinanciacionNotFoundException.class);
  }

  @Test
  void create_WithTipoFinanciacionActivoFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadFinanciadora with TipoFinanciacion activo=false
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(null,
        convocatoriaId);
    convocatoriaEntidadFinanciadora.getTipoFinanciacion().setActivo(false);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora.getTipoFinanciacion()));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadFinanciadora
        () -> service.create(convocatoriaEntidadFinanciadora))
        // then: throw exception as TipoFinanciacion is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El TipoFinanciacion debe estar Activo");
  }

  @Test
  void create_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadFinanciadora when modificable returns False
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaEntidadFinanciadora newConvocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(1L,
        convocatoriaId);
    newConvocatoriaEntidadFinanciadora.setId(null);
    convocatoria.setEstado(Convocatoria.Estado.REGISTRADA);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadFinanciadora
        () -> service.create(newConvocatoriaEntidadFinanciadora))
        // then: throw exception as Convocatoria is not modificable
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede crear ConvocatoriaEntidadFinanciadora. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
  }

  @Test
  void update_ReturnsConvocatoriaEntidadFinanciadora() {
    // given: Un nuevo ConvocatoriaEntidadFinanciadora con el nombre actualizado
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(1L,
        convocatoriaId);
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadoraPorcentajeActualizado = generarMockConvocatoriaEntidadFinanciadora(
        1L, convocatoriaId);
    convocatoriaEntidadFinanciadoraPorcentajeActualizado.setPorcentajeFinanciacion(BigDecimal.valueOf(1));

    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora.getTipoFinanciacion()));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaEntidadFinanciadora>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ConvocatoriaEntidadFinanciadora
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadoraActualizado = service
        .update(convocatoriaEntidadFinanciadoraPorcentajeActualizado);

    // then: El ConvocatoriaEntidadFinanciadora se actualiza correctamente.
    Assertions.assertThat(convocatoriaEntidadFinanciadoraActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaEntidadFinanciadoraActualizado.getId()).as("getId()")
        .isEqualTo(convocatoriaEntidadFinanciadora.getId());
    Assertions.assertThat(convocatoriaEntidadFinanciadoraActualizado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaEntidadFinanciadora.getConvocatoriaId());
    Assertions.assertThat(convocatoriaEntidadFinanciadoraActualizado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(convocatoriaEntidadFinanciadora.getEntidadRef());
    Assertions.assertThat(convocatoriaEntidadFinanciadoraActualizado.getFuenteFinanciacion().getId())
        .as("getFuenteFinanciacion().getId()")
        .isEqualTo(convocatoriaEntidadFinanciadora.getFuenteFinanciacion().getId());
    Assertions.assertThat(convocatoriaEntidadFinanciadoraActualizado.getTipoFinanciacion().getId())
        .as("getTipoFinanciacion().getId()").isEqualTo(convocatoriaEntidadFinanciadora.getTipoFinanciacion().getId());
    Assertions.assertThat(convocatoriaEntidadFinanciadoraActualizado.getPorcentajeFinanciacion())
        .as("getPorcentajeFinanciacion())")
        .isEqualTo(convocatoriaEntidadFinanciadoraPorcentajeActualizado.getPorcentajeFinanciacion());
  }

  @Test
  void update_WithNegativePorcentajeFinanciacion_ThrowsIllegalArgumentException() {
    // given: Un ConvocatoriaEntidadFinanciadora con porcentaje negativo
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(1L,
        convocatoriaId);
    convocatoriaEntidadFinanciadora.setPorcentajeFinanciacion(BigDecimal.valueOf(-10));

    // when: Actualizamos el ConvocatoriaEntidadFinanciadora
    // then: Lanza una excepcion porque el PorcentajeFinanciacion es negativo
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaEntidadFinanciadora))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("PorcentajeFinanciacion no puede ser negativo");
  }

  @Test
  void update_WithIdNotExist_ThrowsConvocatoriaEntidadFinanciadoraNotFoundException() {
    // given: Un ConvocatoriaEntidadFinanciadora a actualizar con un id que no
    // existe
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(1L,
        convocatoriaId);

    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora.getTipoFinanciacion()));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el ConvocatoriaEntidadFinanciadora
    // then: Lanza una excepcion porque el ConvocatoriaEntidadFinanciadora no existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaEntidadFinanciadora))
        .isInstanceOf(ConvocatoriaEntidadFinanciadoraNotFoundException.class);
  }

  @Test
  void update_WithNoExistingFuenteFinanciacion_ThrowsFuenteFinanciacionNotFoundException() {
    // given: a ConvocatoriaEntidadFinanciadora with non existing FuenteFinanciacion
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(1L,
        convocatoriaId);

    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadFinanciadora
        () -> service.update(convocatoriaEntidadFinanciadora))
        // then: throw exception as FuenteFinanciacion is not found
        .isInstanceOf(FuenteFinanciacionNotFoundException.class);
  }

  @Test
  void update_WithNoExistingTipoFinanciacion_ThrowsTipoFinanciacionNotFoundException() {
    // given: a ConvocatoriaEntidadFinanciadora with non existing TipoFinanciacion
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(1L,
        convocatoriaId);

    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadFinanciadora
        () -> service.update(convocatoriaEntidadFinanciadora))
        // then: throw exception as TipoFinanciacion is not found
        .isInstanceOf(TipoFinanciacionNotFoundException.class);
  }

  @Test
  void update_WithFuenteFinanciacionActivoFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadFinanciadora with FuenteFinanciacion activo=false
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(1L,
        convocatoriaId);
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadoraActualizada = generarMockConvocatoriaEntidadFinanciadora(
        1L, convocatoriaId);
    convocatoriaEntidadFinanciadoraActualizada.getFuenteFinanciacion().setId(2L);
    convocatoriaEntidadFinanciadoraActualizada.getFuenteFinanciacion().setActivo(false);

    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadoraActualizada.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadoraActualizada.getTipoFinanciacion()));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadFinanciadora
        () -> service.update(convocatoriaEntidadFinanciadoraActualizada))
        // then: throw exception as FuenteFinanciacion is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("La FuenteFinanciacion debe estar Activo");
  }

  @Test
  void update_WithTipoFinanciacionActivoFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadFinanciadora with TipoFinanciacion activo=false
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(1L,
        convocatoriaId);
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadoraActualizada = generarMockConvocatoriaEntidadFinanciadora(
        1L, convocatoriaId);
    convocatoriaEntidadFinanciadoraActualizada.getTipoFinanciacion().setId(2L);
    convocatoriaEntidadFinanciadoraActualizada.getTipoFinanciacion().setActivo(false);

    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadoraActualizada.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadoraActualizada.getTipoFinanciacion()));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadFinanciadora
        () -> service.update(convocatoriaEntidadFinanciadoraActualizada))
        // then: throw exception as TipoFinanciacion is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El TipoFinanciacion debe estar Activo");
  }

  @Test
  void update_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadFinanciadora when modificable return false
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockConvocatoriaEntidadFinanciadora(1L,
        convocatoriaId);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora));
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora.getTipoFinanciacion()));

    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadFinanciadora
        () -> service.update(convocatoriaEntidadFinanciadora))
        // then: throw exception as Convocatoria is not modificable
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede modificar ConvocatoriaEntidadFinanciadora. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
  }

  @Test
  void delete_WithExistingId_ReturnsConvocatoriaEntidadFinanciadora() {
    // given: existing ConvocatoriaEntidadFinanciadora
    Long id = 1L;
    Long convocatoriaId = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoriaEntidadFinanciadora(id, convocatoriaId)));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.anyLong());

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

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaEntidadFinanciadoraNotFoundException.class);
  }

  @Test
  void delete_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: existing ConvocatoriaEntidadFinanciadora when modificable returns
    // false
    Long id = 1L;
    Long convocatoriaId = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoriaEntidadFinanciadora(1L, convocatoriaId)));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: throw exception as Convocatoria is not modificable
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede eliminar ConvocatoriaEntidadFinanciadora. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void findAllByConvocatoria_ReturnsPage() {
    // given: Una lista con 37 ConvocatoriaEntidadFinanciadora para la Convocatoria
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);

    List<ConvocatoriaEntidadFinanciadora> convocatoriasEntidadesFinanciadoras = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesFinanciadoras.add(generarMockConvocatoriaEntidadFinanciadora(i, convocatoriaId));
    }
    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaEntidadFinanciadora>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > convocatoriasEntidadesFinanciadoras.size() ? convocatoriasEntidadesFinanciadoras.size()
              : toIndex;
          List<ConvocatoriaEntidadFinanciadora> content = convocatoriasEntidadesFinanciadoras.subList(fromIndex,
              toIndex);
          Page<ConvocatoriaEntidadFinanciadora> pageResponse = new PageImpl<>(content, pageable,
              convocatoriasEntidadesFinanciadoras.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConvocatoriaEntidadFinanciadora> page = service.findAllByConvocatoria(convocatoriaId, null, paging);

    // then: Devuelve la pagina 3 con los ConvocatoriaEntidadFinanciadora del 31 al
    // 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(convocatoriaEntidadFinanciadora.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(convocatoriaEntidadFinanciadora.getEntidadRef()).isEqualTo("entidad-" + i);
    }
  }

  @Test
  void findById_ReturnsConvocatoriaEntidadFinanciadora() {
    // given: Un ConvocatoriaEntidadFinanciadora con el id buscado
    Long idBuscado = 1L;
    Long convocatoriaId = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockConvocatoriaEntidadFinanciadora(idBuscado, convocatoriaId)));

    // when: Buscamos el ConvocatoriaEntidadFinanciadora por su id
    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = service.findById(idBuscado);

    // then: el ConvocatoriaEntidadFinanciadora
    Assertions.assertThat(convocatoriaEntidadFinanciadora).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaEntidadFinanciadora.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  void findById_WithIdNotExist_ThrowsConvocatoriaEntidadFinanciadoraNotFoundException() throws Exception {
    // given: Ningun ConvocatoriaEntidadFinanciadora con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ConvocatoriaEntidadFinanciadora por su id
    // then: lanza un ConvocatoriaEntidadFinanciadoraNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ConvocatoriaEntidadFinanciadoraNotFoundException.class);
  }

  private Convocatoria generarMockConvocatoria(Long convocatoriaId) {
    return Convocatoria.builder().id(convocatoriaId).build();
  }

  /**
   * Función que devuelve un objeto ConvocatoriaEntidadFinanciadora
   * 
   * @param id id del ConvocatoriaEntidadFinanciadora
   * @return el objeto ConvocatoriaEntidadFinanciadora
   */
  private ConvocatoriaEntidadFinanciadora generarMockConvocatoriaEntidadFinanciadora(Long id, Long convocatoriaId) {
    FuenteFinanciacion fuenteFinanciacion = new FuenteFinanciacion();
    fuenteFinanciacion.setId(id == null ? 1 : id);
    fuenteFinanciacion.setActivo(true);

    TipoFinanciacion tipoFinanciacion = new TipoFinanciacion();
    tipoFinanciacion.setId(id == null ? 1 : id);
    tipoFinanciacion.setActivo(true);

    Programa programa = new Programa();
    programa.setId(id);

    ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora = new ConvocatoriaEntidadFinanciadora();
    convocatoriaEntidadFinanciadora.setId(id);
    convocatoriaEntidadFinanciadora.setConvocatoriaId(convocatoriaId);
    convocatoriaEntidadFinanciadora.setEntidadRef("entidad-" + (id == null ? 0 : id));
    convocatoriaEntidadFinanciadora.setFuenteFinanciacion(fuenteFinanciacion);
    convocatoriaEntidadFinanciadora.setTipoFinanciacion(tipoFinanciacion);
    convocatoriaEntidadFinanciadora.setPorcentajeFinanciacion(BigDecimal.valueOf(50));

    return convocatoriaEntidadFinanciadora;
  }

}
