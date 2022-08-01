package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.FuenteFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoEntidadFinanciadoraNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.repository.FuenteFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.TipoFinanciacionRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoEntidadFinanciadoraServiceImpl;
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
class ProyectoEntidadFinanciadoraServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoEntidadFinanciadoraRepository repository;
  @Mock
  private ProyectoRepository proyectoRepository;
  @Mock
  private FuenteFinanciacionRepository fuenteFinanciacionRepository;
  @Mock
  private TipoFinanciacionRepository tipoFinanciacionRepository;
  @Mock
  private ConvocatoriaService convocatoriaService;

  private ProyectoEntidadFinanciadoraService service;

  @BeforeEach
  void setUp() throws Exception {
    service = new ProyectoEntidadFinanciadoraServiceImpl(repository, proyectoRepository, fuenteFinanciacionRepository,
        tipoFinanciacionRepository);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_3" })
  void create_ReturnsProyectoEntidadFinanciadora() {
    // given: Un nuevo ProyectoEntidadFinanciadora
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(null);
    Proyecto proyecto = buildProyecto(1L, "3", EstadoProyecto.Estado.CONCEDIDO);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoEntidadFinanciadora.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoEntidadFinanciadora.getTipoFinanciacion()));

    BDDMockito.given(repository.save(proyectoEntidadFinanciadora)).will((InvocationOnMock invocation) -> {
      ProyectoEntidadFinanciadora proyectoEntidadFinanciadoraCreado = invocation.getArgument(0);
      proyectoEntidadFinanciadoraCreado.setId(1L);
      return proyectoEntidadFinanciadoraCreado;
    });

    // when: Creamos el ProyectoEntidadFinanciadora
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadoraCreado = service.create(proyectoEntidadFinanciadora);

    // then: El ProyectoEntidadFinanciadora se crea correctamente
    Assertions.assertThat(proyectoEntidadFinanciadoraCreado).as("isNotNull()").isNotNull();

    Assertions.assertThat(proyectoEntidadFinanciadoraCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoEntidadFinanciadoraCreado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyecto.getId());
    Assertions.assertThat(proyectoEntidadFinanciadoraCreado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(proyectoEntidadFinanciadora.getEntidadRef());
    Assertions.assertThat(proyectoEntidadFinanciadoraCreado.getFuenteFinanciacion().getId())
        .as("getFuenteFinanciacion().getId()").isEqualTo(proyectoEntidadFinanciadora.getFuenteFinanciacion().getId());
    Assertions.assertThat(proyectoEntidadFinanciadoraCreado.getTipoFinanciacion().getId())
        .as("getTipoFinanciacion().getId()").isEqualTo(proyectoEntidadFinanciadora.getTipoFinanciacion().getId());
    Assertions.assertThat(proyectoEntidadFinanciadoraCreado.getPorcentajeFinanciacion())
        .as("getPorcentajeFinanciacion())").isEqualTo(proyectoEntidadFinanciadora.getPorcentajeFinanciacion());
    Assertions.assertThat(proyectoEntidadFinanciadoraCreado.getAjena()).as("getAjena())")
        .isEqualTo(proyectoEntidadFinanciadora.getAjena());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_3" })
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ProyectoEntidadFinanciadora que ya tiene id
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(1L);

    // when: Creamos el ProyectoEntidadFinanciadora
    // then: Lanza una excepcion porque el ProyectoEntidadFinanciadora ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(proyectoEntidadFinanciadora))
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "ProyectoEntidadFinanciadora id tiene que ser null para crear un nuevo ProyectoEntidadFinanciadora");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_3" })
  void create_WithNegativePorcentajeFinanciacion_ThrowsIllegalArgumentException() {
    // given: Un nuevo ProyectoEntidadFinanciadora con porcentaje negativo
    ProyectoEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(null);
    convocatoriaEntidadFinanciadora.setPorcentajeFinanciacion(BigDecimal.valueOf(-10));

    Proyecto proyecto = buildProyecto(1L, "3", EstadoProyecto.Estado.CONCEDIDO);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));

    // when: Creamos el ProyectoEntidadFinanciadora
    // then: Lanza una excepcion porque el PorcentajeFinanciacion es negativo
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaEntidadFinanciadora))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("PorcentajeFinanciacion no puede ser negativo");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C" })
  void create_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoEntidadFinanciadora without proyectoId
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(null);
    proyectoEntidadFinanciadora.setProyectoId(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoEntidadFinanciadora
        () -> service.create(proyectoEntidadFinanciadora))
        // then: throw exception as proyectoId is null
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El id de proyecto no puede ser nulo");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C" })
  void create_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoEntidadFinanciadora with non existing Proyecto
    ProyectoEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(null);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ProyectoEntidadFinanciadora
        () -> service.create(convocatoriaEntidadFinanciadora))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_3" })
  void create_WithNoExistingFuenteFinanciacion_ThrowsFuenteFinanciacionNotFoundException() {
    // given: a ProyectoEntidadFinanciadora with non existing FuenteFinanciacion
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(null);
    Proyecto proyecto = buildProyecto(1L, "3", EstadoProyecto.Estado.CONCEDIDO);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));

    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ProyectoEntidadFinanciadora
        () -> service.create(proyectoEntidadFinanciadora))
        // then: throw exception as FuenteFinanciacion is not found
        .isInstanceOf(FuenteFinanciacionNotFoundException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_3" })
  void create_WithFuenteFinanciacionActivoFalse_ThrowsIllegalArgumentException() {
    // given: a ProyectoEntidadFinanciadora with FuenteFinanciacion activo=false
    ProyectoEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(null);
    Proyecto proyecto = buildProyecto(1L, "3", EstadoProyecto.Estado.CONCEDIDO);
    convocatoriaEntidadFinanciadora.getFuenteFinanciacion().setActivo(false);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora.getFuenteFinanciacion()));

    Assertions.assertThatThrownBy(
        // when: create ProyectoEntidadFinanciadora
        () -> service.create(convocatoriaEntidadFinanciadora))
        // then: throw exception as FuenteFinanciacion is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("La FuenteFinanciacion debe estar Activo");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_3" })
  void create_WithNoExistingTipoFinanciacion_ThrowsFuenteFinanciacionNotFoundException() {
    // given: a ProyectoEntidadFinanciadora with non existing TipoFinanciacion
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(null);
    Proyecto proyecto = buildProyecto(1L, "3", EstadoProyecto.Estado.CONCEDIDO);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoEntidadFinanciadora.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ProyectoEntidadFinanciadora
        () -> service.create(proyectoEntidadFinanciadora))
        // then: throw exception as TipoFinanciacion is not found
        .isInstanceOf(TipoFinanciacionNotFoundException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_3" })
  void create_WithTipoFinanciacionActivoFalse_ThrowsIllegalArgumentException() {
    // given: a ProyectoEntidadFinanciadora with TipoFinanciacion activo=false
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(null);
    proyectoEntidadFinanciadora.getTipoFinanciacion().setActivo(false);
    Proyecto proyecto = buildProyecto(1L, "3", EstadoProyecto.Estado.CONCEDIDO);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoEntidadFinanciadora.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoEntidadFinanciadora.getTipoFinanciacion()));

    Assertions.assertThatThrownBy(
        // when: create ProyectoEntidadFinanciadora
        () -> service.create(proyectoEntidadFinanciadora))
        // then: throw exception as TipoFinanciacion is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El TipoFinanciacion debe estar Activo");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_3" })
  void update_ReturnsProyectoEntidadFinanciadora() {
    // given: Un nuevo ProyectoEntidadFinanciadora con el porcentajeFinanciacion
    // actualizado
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(1L);
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadoraPorcentajeActualizado = generarMockProyectoEntidadFinanciadora(
        1L);
    proyectoEntidadFinanciadoraPorcentajeActualizado.setPorcentajeFinanciacion(BigDecimal.ONE);
    Proyecto proyecto = buildProyecto(1L, "3", EstadoProyecto.Estado.CONCEDIDO);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoEntidadFinanciadora.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoEntidadFinanciadora.getTipoFinanciacion()));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoEntidadFinanciadora));

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoEntidadFinanciadora>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ProyectoEntidadFinanciadora
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadoraActualizado = service
        .update(proyectoEntidadFinanciadoraPorcentajeActualizado);

    // then: El ProyectoEntidadFinanciadora se actualiza correctamente.
    Assertions.assertThat(proyectoEntidadFinanciadoraActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoEntidadFinanciadoraActualizado.getId()).as("getId()")
        .isEqualTo(proyectoEntidadFinanciadora.getId());
    Assertions.assertThat(proyectoEntidadFinanciadoraActualizado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoEntidadFinanciadora.getProyectoId());
    Assertions.assertThat(proyectoEntidadFinanciadoraActualizado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(proyectoEntidadFinanciadora.getEntidadRef());
    Assertions.assertThat(proyectoEntidadFinanciadoraActualizado.getFuenteFinanciacion().getId())
        .as("getFuenteFinanciacion().getId()").isEqualTo(proyectoEntidadFinanciadora.getFuenteFinanciacion().getId());
    Assertions.assertThat(proyectoEntidadFinanciadoraActualizado.getTipoFinanciacion().getId())
        .as("getTipoFinanciacion().getId()").isEqualTo(proyectoEntidadFinanciadora.getTipoFinanciacion().getId());
    Assertions.assertThat(proyectoEntidadFinanciadoraActualizado.getPorcentajeFinanciacion())
        .as("getPorcentajeFinanciacion())")
        .isEqualTo(proyectoEntidadFinanciadoraPorcentajeActualizado.getPorcentajeFinanciacion());
    Assertions.assertThat(proyectoEntidadFinanciadoraPorcentajeActualizado.getAjena()).as("getAjena())")
        .isEqualTo(proyectoEntidadFinanciadora.getAjena());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_3" })
  void update_WithNegativePorcentajeFinanciacion_ThrowsIllegalArgumentException() {
    // given: Un ProyectoEntidadFinanciadora con porcentaje negativo
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadoraOld = generarMockProyectoEntidadFinanciadora(1L);
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(1L);
    proyectoEntidadFinanciadora.setPorcentajeFinanciacion(BigDecimal.valueOf(-10));
    Proyecto proyecto = buildProyecto(1L, "3", EstadoProyecto.Estado.CONCEDIDO);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoEntidadFinanciadoraOld));

    // when: Actualizamos el ProyectoEntidadFinanciadora
    // then: Lanza una excepcion porque el PorcentajeFinanciacion es negativo
    Assertions.assertThatThrownBy(() -> service.update(proyectoEntidadFinanciadora))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("PorcentajeFinanciacion no puede ser negativo");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C" })
  void update_WithIdNotExist_ThrowsProyectoEntidadFinanciadoraNotFoundException() {
    // given: Un ProyectoEntidadFinanciadora a actualizar con un id que no existe
    ProyectoEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el ProyectoEntidadFinanciadora
    // then: Lanza una excepcion porque el ProyectoEntidadFinanciadora no existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaEntidadFinanciadora))
        .isInstanceOf(ProyectoEntidadFinanciadoraNotFoundException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_3" })
  void update_WithNoExistingFuenteFinanciacion_ThrowsFuenteFinanciacionNotFoundException() {
    // given: a ProyectoEntidadFinanciadora with non existing FuenteFinanciacion
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(1L);
    Proyecto proyecto = buildProyecto(1L, "3", EstadoProyecto.Estado.CONCEDIDO);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoEntidadFinanciadora));
    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ProyectoEntidadFinanciadora
        () -> service.update(proyectoEntidadFinanciadora))
        // then: throw exception as FuenteFinanciacion is not found
        .isInstanceOf(FuenteFinanciacionNotFoundException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_3" })
  void update_WithNoExistingTipoFinanciacion_ThrowsTipoFinanciacionNotFoundException() {
    // given: a ProyectoEntidadFinanciadora with non existing TipoFinanciacion
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(1L);
    Proyecto proyecto = buildProyecto(1L, "3", EstadoProyecto.Estado.CONCEDIDO);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoEntidadFinanciadora));
    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoEntidadFinanciadora.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ProyectoEntidadFinanciadora
        () -> service.update(proyectoEntidadFinanciadora))
        // then: throw exception as TipoFinanciacion is not found
        .isInstanceOf(TipoFinanciacionNotFoundException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_3" })
  void update_WithFuenteFinanciacionActivoFalse_ThrowsIllegalArgumentException() {
    // given: a ProyectoEntidadFinanciadora with FuenteFinanciacion activo=false
    ProyectoEntidadFinanciadora convocatoriaEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(1L);
    ProyectoEntidadFinanciadora convocatoriaEntidadFinanciadoraActualizada = generarMockProyectoEntidadFinanciadora(1L);
    convocatoriaEntidadFinanciadoraActualizada.getFuenteFinanciacion().setId(2L);
    convocatoriaEntidadFinanciadoraActualizada.getFuenteFinanciacion().setActivo(false);
    Proyecto proyecto = buildProyecto(1L, "3", EstadoProyecto.Estado.CONCEDIDO);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadoraActualizada.getFuenteFinanciacion()));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoriaEntidadFinanciadora));

    Assertions.assertThatThrownBy(
        // when: update ProyectoEntidadFinanciadora
        () -> service.update(convocatoriaEntidadFinanciadoraActualizada))
        // then: throw exception as FuenteFinanciacion is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("La FuenteFinanciacion debe estar Activo");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_3" })
  void update_WithTipoFinanciacionActivoFalse_ThrowsIllegalArgumentException() {
    // given: a ProyectoEntidadFinanciadora with TipoFinanciacion activo=false
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = generarMockProyectoEntidadFinanciadora(1L);
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadoraActualizada = generarMockProyectoEntidadFinanciadora(1L);
    proyectoEntidadFinanciadoraActualizada.getTipoFinanciacion().setId(2L);
    proyectoEntidadFinanciadoraActualizada.getTipoFinanciacion().setActivo(false);
    Proyecto proyecto = buildProyecto(1L, "3", EstadoProyecto.Estado.CONCEDIDO);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoEntidadFinanciadoraActualizada.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoEntidadFinanciadoraActualizada.getTipoFinanciacion()));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoEntidadFinanciadora));

    Assertions.assertThatThrownBy(
        // when: update ProyectoEntidadFinanciadora
        () -> service.update(proyectoEntidadFinanciadoraActualizada))
        // then: throw exception as TipoFinanciacion is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El TipoFinanciacion debe estar Activo");
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_3" })
  void delete_WithExistingId_ReturnsProyectoEntidadFinanciadora() {
    // given: existing ProyectoEntidadFinanciadora
    Long id = 1L;
    Proyecto proyecto = buildProyecto(1L, "3", EstadoProyecto.Estado.CONCEDIDO);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockProyectoEntidadFinanciadora(id)));
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C" })
  void delete_WithNoExistingId_ThrowsNotFoundException() {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoEntidadFinanciadoraNotFoundException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_3" })
  void findAllByProyecto_ReturnsPage() {
    // given: Una lista con 37 ProyectoEntidadFinanciadora para el Proyecto
    Long convocatoriaId = 1L;
    List<ProyectoEntidadFinanciadora> proyectosEntidadesFinanciadoras = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectosEntidadesFinanciadoras.add(generarMockProyectoEntidadFinanciadora(i));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoEntidadFinanciadora>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectosEntidadesFinanciadoras.size() ? proyectosEntidadesFinanciadoras.size() : toIndex;
          List<ProyectoEntidadFinanciadora> content = proyectosEntidadesFinanciadoras.subList(fromIndex, toIndex);
          Page<ProyectoEntidadFinanciadora> pageResponse = new PageImpl<>(content, pageable,
              proyectosEntidadesFinanciadoras.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoEntidadFinanciadora> page = service.findAllByProyecto(convocatoriaId, null, paging);

    // then: Devuelve la pagina 3 con los ProyectoEntidadFinanciadora del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyectoEntidadFinanciadora.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(proyectoEntidadFinanciadora.getEntidadRef()).isEqualTo("entidad-" + i);
    }
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C_3" })
  void findById_ReturnsProyectoEntidadFinanciadora() {
    // given: Un ProyectoEntidadFinanciadora con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockProyectoEntidadFinanciadora(idBuscado)));

    // when: Buscamos el ProyectoEntidadFinanciadora por su id
    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = service.findById(idBuscado);

    // then: el ProyectoEntidadFinanciadora
    Assertions.assertThat(proyectoEntidadFinanciadora).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoEntidadFinanciadora.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-C" })
  void findById_WithIdNotExist_ThrowsProyectoEntidadFinanciadoraNotFoundException() throws Exception {
    // given: Ningun ProyectoEntidadFinanciadora con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ProyectoEntidadFinanciadora por su id
    // then: lanza un ProyectoEntidadFinanciadoraNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ProyectoEntidadFinanciadoraNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaEntidadFinanciadora
   * 
   * @param id id del ConvocatoriaEntidadFinanciadora
   * @return el objeto ConvocatoriaEntidadFinanciadora
   */
  private ProyectoEntidadFinanciadora generarMockProyectoEntidadFinanciadora(Long id) {
    FuenteFinanciacion fuenteFinanciacion = FuenteFinanciacion.builder()
    // @formatter:off
      .id(id == null ? 1 : id)
      .activo(true)
      .build();
    // @formatter:on

    TipoFinanciacion tipoFinanciacion = TipoFinanciacion.builder()
    // @formatter:off
      .id(id == null ? 1 : id)
      .activo(true)
      .build();
    // @formatter:on

    ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = ProyectoEntidadFinanciadora.builder()
    //@formatter:off
      .id(id)
      .proyectoId(id == null ? 1 : id)
      .entidadRef("entidad-" + (id == null ? 0 : id))
      .fuenteFinanciacion(fuenteFinanciacion)
      .tipoFinanciacion(tipoFinanciacion)
      .porcentajeFinanciacion(BigDecimal.valueOf(50))
      .ajena(false)
      .build();
    //@formatter:on

    return proyectoEntidadFinanciadora;
  }

  private Proyecto buildProyecto(Long id, String unidadGestionRef, EstadoProyecto.Estado estado) {
    EstadoProyecto estadoProyecto = EstadoProyecto.builder()
    //@formatter:off
      .estado(estado)
      .build();
    //@formatter:on

    Proyecto proyecto = Proyecto.builder()
    //@formatter:off
      .id(id)
      .unidadGestionRef(unidadGestionRef)
      .estado(estadoProyecto)
      .build();
    //@formatter:on

    return proyecto;
  }
}
