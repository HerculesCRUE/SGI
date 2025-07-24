package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacionNombre;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.Solicitud.OrigenSolicitud;
import org.crue.hercules.sgi.csp.model.SolicitudObservaciones;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.model.SolicitudTitulo;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.model.TipoFinanciacionNombre;
import org.crue.hercules.sgi.csp.repository.FuenteFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudExternaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEntidadFinanciadoraAjenaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEntidadRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.TipoFinanciacionRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudProyectoEntidadFinanciadoraAjenaServiceImpl;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
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
 * SolicitudProyectoEntidadFinanciadoraAjenaServiceTest
 */
class SolicitudProyectoEntidadFinanciadoraAjenaServiceTest extends BaseServiceTest {

  @Mock
  private SolicitudProyectoEntidadFinanciadoraAjenaRepository repository;

  @Mock
  private FuenteFinanciacionRepository fuenteFinanciacionRepository;

  @Mock
  private TipoFinanciacionRepository tipoFinanciacionRepository;

  @Mock
  private SolicitudService solicitudService;

  @Mock
  private SolicitudProyectoRepository solicitudProyectoRepository;

  @Mock
  private SolicitudProyectoEntidadRepository solicitudProyectoEntidadRepository;

  @Mock
  private SolicitudRepository solicitudRepository;

  @Mock
  private SolicitudExternaRepository solicitudExternaRepository;

  private SolicitudProyectoEntidadFinanciadoraAjenaService service;

  private SolicitudAuthorityHelper solicitudAuthorityHelper;

  @BeforeEach
  void setUp() throws Exception {
    solicitudAuthorityHelper = new SolicitudAuthorityHelper(solicitudRepository, solicitudExternaRepository);
    service = new SolicitudProyectoEntidadFinanciadoraAjenaServiceImpl(repository, fuenteFinanciacionRepository,
        tipoFinanciacionRepository, solicitudService, solicitudProyectoRepository, solicitudProyectoEntidadRepository,
        solicitudAuthorityHelper);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create_ReturnsSolicitudProyectoEntidadFinanciadoraAjena() {
    // given: Un nuevo SolicitudProyectoEntidadFinanciadoraAjena
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        null);

    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjena.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjena.getTipoFinanciacion()));
    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));

    BDDMockito.given(repository.save(solicitudProyectoEntidadFinanciadoraAjena)).will((InvocationOnMock invocation) -> {
      SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjenaCreado = invocation
          .getArgument(0);
      solicitudProyectoEntidadFinanciadoraAjenaCreado.setId(1L);
      return solicitudProyectoEntidadFinanciadoraAjenaCreado;
    });

    // when: Creamos el SolicitudProyectoEntidadFinanciadoraAjena
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjenaCreado = service
        .create(solicitudProyectoEntidadFinanciadoraAjena);

    // then: El SolicitudProyectoEntidadFinanciadoraAjena se crea correctamente
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaCreado.getSolicitudProyectoId())
        .as("getSolicitudProyectoId()").isEqualTo(solicitudProyectoEntidadFinanciadoraAjena.getSolicitudProyectoId());
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaCreado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(solicitudProyectoEntidadFinanciadoraAjena.getEntidadRef());
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaCreado.getFuenteFinanciacion().getId())
        .as("getFuenteFinanciacion().getId()")
        .isEqualTo(solicitudProyectoEntidadFinanciadoraAjena.getFuenteFinanciacion().getId());
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaCreado.getTipoFinanciacion().getId())
        .as("getTipoFinanciacion().getId()")
        .isEqualTo(solicitudProyectoEntidadFinanciadoraAjena.getTipoFinanciacion().getId());
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaCreado.getPorcentajeFinanciacion())
        .as("getPorcentajeFinanciacion())")
        .isEqualTo(solicitudProyectoEntidadFinanciadoraAjena.getPorcentajeFinanciacion());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoEntidadFinanciadoraAjena que ya tiene id
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        1L);

    // when: Creamos el SolicitudProyectoEntidadFinanciadoraAjena
    // then: Lanza una excepcion porque el SolicitudProyectoEntidadFinanciadoraAjena
    // ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoEntidadFinanciadoraAjena))
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "Identificador de Solicitud Proyecto Entidad Financiadora Ajena debe ser nulo");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create_WithoutSolicitudProyectoId_ThrowsIllegalArgumentException() {
    // given: a SolicitudProyectoEntidadFinanciadoraAjena without
    // SolicitudProyectoId
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        null);
    solicitudProyectoEntidadFinanciadoraAjena.setSolicitudProyectoId(null);

    Assertions.assertThatThrownBy(
        // when: create SolicitudProyectoEntidadFinanciadoraAjena
        () -> service.create(solicitudProyectoEntidadFinanciadoraAjena))
        // then: throw exception as SolicitudProyectoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Datos de Proyecto no puede ser nulo");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create_WithFuenteFinanciacionActivoFalse_ThrowsIllegalArgumentException() {
    // given: a SolicitudProyectoEntidadFinanciadoraAjena with FuenteFinanciacion
    // activo=false
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        null);
    solicitudProyectoEntidadFinanciadoraAjena.getFuenteFinanciacion().setActivo(false);

    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjena.getFuenteFinanciacion()));
    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));

    Assertions.assertThatThrownBy(
        // when: create SolicitudProyectoEntidadFinanciadoraAjena
        () -> service.create(solicitudProyectoEntidadFinanciadoraAjena))
        // then: throw exception as FuenteFinanciacion is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("%s de Fuente de Financiación no está activo",
            solicitudProyectoEntidadFinanciadoraAjena.getFuenteFinanciacion().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create_WithTipoFinanciacionActivoFalse_ThrowsIllegalArgumentException() {
    // given: a SolicitudProyectoEntidadFinanciadoraAjena with TipoFinanciacion
    // activo=false
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        null);
    solicitudProyectoEntidadFinanciadoraAjena.getTipoFinanciacion().setActivo(false);

    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjena.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjena.getTipoFinanciacion()));
    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));

    Assertions.assertThatThrownBy(
        // when: create SolicitudProyectoEntidadFinanciadoraAjena
        () -> service.create(solicitudProyectoEntidadFinanciadoraAjena))
        // then: throw exception as TipoFinanciacion is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El TipoFinanciacion debe estar Activo");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_ReturnsSolicitudProyectoEntidadFinanciadoraAjena() {
    // given: Un nuevo SolicitudProyectoEntidadFinanciadoraAjena con el nombre
    // actualizado
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId);
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        1L);
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjenaPorcentajeActualizado = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        1L);
    solicitudProyectoEntidadFinanciadoraAjenaPorcentajeActualizado.setPorcentajeFinanciacion(BigDecimal.ONE);

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjena.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjena.getTipoFinanciacion()));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjena));
    BDDMockito.given(repository.save(ArgumentMatchers.<SolicitudProyectoEntidadFinanciadoraAjena>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));

    // when: Actualizamos el SolicitudProyectoEntidadFinanciadoraAjena
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjenaActualizado = service
        .update(solicitudProyectoEntidadFinanciadoraAjenaPorcentajeActualizado);

    // then: El SolicitudProyectoEntidadFinanciadoraAjena se actualiza
    // correctamente.
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaActualizado.getId()).as("getId()")
        .isEqualTo(solicitudProyectoEntidadFinanciadoraAjena.getId());
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaActualizado.getSolicitudProyectoId())
        .as("getSolicitudProyectoId()").isEqualTo(solicitudProyectoEntidadFinanciadoraAjena.getSolicitudProyectoId());
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaActualizado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(solicitudProyectoEntidadFinanciadoraAjena.getEntidadRef());
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaActualizado.getFuenteFinanciacion().getId())
        .as("getFuenteFinanciacion().getId()")
        .isEqualTo(solicitudProyectoEntidadFinanciadoraAjena.getFuenteFinanciacion().getId());
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaActualizado.getTipoFinanciacion().getId())
        .as("getTipoFinanciacion().getId()")
        .isEqualTo(solicitudProyectoEntidadFinanciadoraAjena.getTipoFinanciacion().getId());
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjenaActualizado.getPorcentajeFinanciacion())
        .as("getPorcentajeFinanciacion())")
        .isEqualTo(solicitudProyectoEntidadFinanciadoraAjenaPorcentajeActualizado.getPorcentajeFinanciacion());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithIdNotExist_ThrowsSolicitudProyectoEntidadFinanciadoraAjenaNotFoundException() {
    // given: Un SolicitudProyectoEntidadFinanciadoraAjena a actualizar con un id
    // que no existe
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId);
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        1L);

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));

    // when: Actualizamos el SolicitudProyectoEntidadFinanciadoraAjena
    // then: Lanza una excepcion porque el SolicitudProyectoEntidadFinanciadoraAjena
    // no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoEntidadFinanciadoraAjena))
        .isInstanceOf(SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithFuenteFinanciacionActivoFalse_ThrowsIllegalArgumentException() {
    // given: a SolicitudProyectoEntidadFinanciadoraAjena with FuenteFinanciacion
    // activo=false
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId);
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        1L);
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjenaActualizada = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        1L);
    solicitudProyectoEntidadFinanciadoraAjenaActualizada.getFuenteFinanciacion().setId(2L);
    solicitudProyectoEntidadFinanciadoraAjenaActualizada.getFuenteFinanciacion().setActivo(false);

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjenaActualizada.getFuenteFinanciacion()));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjena));
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));

    Assertions.assertThatThrownBy(
        // when: update SolicitudProyectoEntidadFinanciadoraAjena
        () -> service.update(solicitudProyectoEntidadFinanciadoraAjenaActualizada))
        // then: throw exception as FuenteFinanciacion is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("%s de Fuente de Financiación no está activo",
            solicitudProyectoEntidadFinanciadoraAjenaActualizada.getFuenteFinanciacion().getNombre());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithTipoFinanciacionActivoFalse_ThrowsIllegalArgumentException() {
    // given: a SolicitudProyectoEntidadFinanciadoraAjena with TipoFinanciacion
    // activo=false
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId);
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        1L);
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjenaActualizada = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        1L);
    solicitudProyectoEntidadFinanciadoraAjenaActualizada.getTipoFinanciacion().setId(2L);
    solicitudProyectoEntidadFinanciadoraAjenaActualizada.getTipoFinanciacion().setActivo(false);

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjenaActualizada.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjenaActualizada.getTipoFinanciacion()));
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjena));
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));

    Assertions.assertThatThrownBy(
        // when: update SolicitudProyectoEntidadFinanciadoraAjena
        () -> service.update(solicitudProyectoEntidadFinanciadoraAjenaActualizada))
        // then: throw exception as TipoFinanciacion is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El TipoFinanciacion debe estar Activo");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void delete_WithExistingId_ReturnsSolicitudProyectoEntidadFinanciadoraAjena() {
    // given: existing SolicitudProyectoEntidadFinanciadoraAjena
    Long id = 1L;
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        id);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjena));
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void findAllBySolicitud_ReturnsPage() {
    // given: Una lista con 37 SolicitudProyectoEntidadFinanciadoraAjena para la
    // Solicitud
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    List<SolicitudProyectoEntidadFinanciadoraAjena> solicitudProyectoEntidadesFinanciadoraAjenas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoEntidadesFinanciadoraAjenas.add(generarMockSolicitudProyectoEntidadFinanciadoraAjena(i));
    }

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(solicitud));
    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoEntidadFinanciadoraAjena>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > solicitudProyectoEntidadesFinanciadoraAjenas.size()
              ? solicitudProyectoEntidadesFinanciadoraAjenas.size()
              : toIndex;
          List<SolicitudProyectoEntidadFinanciadoraAjena> content = solicitudProyectoEntidadesFinanciadoraAjenas
              .subList(fromIndex, toIndex);
          Page<SolicitudProyectoEntidadFinanciadoraAjena> pageResponse = new PageImpl<>(content, pageable,
              solicitudProyectoEntidadesFinanciadoraAjenas.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<SolicitudProyectoEntidadFinanciadoraAjena> page = service.findAllBySolicitud(solicitudId, null, paging);

    // then: Devuelve la pagina 3 con los SolicitudProyectoEntidadFinanciadoraAjena
    // del 31 al
    // 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjena.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjena.getEntidadRef())
          .isEqualTo("entidad-" + String.format("%03d", i));
    }
  }

  @Test
  void findById_ReturnsSolicitudProyectoEntidadFinanciadoraAjena() {
    // given: Un SolicitudProyectoEntidadFinanciadoraAjena con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockSolicitudProyectoEntidadFinanciadoraAjena(idBuscado)));

    // when: Buscamos el SolicitudProyectoEntidadFinanciadoraAjena por su id
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = service.findById(idBuscado);

    // then: el SolicitudProyectoEntidadFinanciadoraAjena
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjena).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoEntidadFinanciadoraAjena.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  void findById_WithIdNotExist_ThrowsSolicitudProyectoEntidadFinanciadoraAjenaNotFoundException()
      throws Exception {
    // given: Ningun SolicitudProyectoEntidadFinanciadoraAjena con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SolicitudProyectoEntidadFinanciadoraAjena por su id
    // then: lanza un SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException.class);
  }

  private SolicitudProyecto generarMockSolicitudProyecto(Long solicitudProyectoId) {
    return SolicitudProyecto.builder().id(solicitudProyectoId).build();
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoEntidadFinanciadoraAjena
   * 
   * @param id id del SolicitudProyectoEntidadFinanciadoraAjena
   * @return el objeto SolicitudProyectoEntidadFinanciadoraAjena
   */
  private SolicitudProyectoEntidadFinanciadoraAjena generarMockSolicitudProyectoEntidadFinanciadoraAjena(Long id) {

    Set<FuenteFinanciacionNombre> tipoFinanciacionNombre = new HashSet<>();
    tipoFinanciacionNombre.add(new FuenteFinanciacionNombre(Language.ES, "nombreFuenteFinanciacion"));

    // @formatter:off
    FuenteFinanciacion fuenteFinanciacion = FuenteFinanciacion.builder()
        .id(id == null ? 1 : id)
        .nombre(tipoFinanciacionNombre)
        .activo(true)
        .build();

    Set<TipoFinanciacionNombre> nombre = new HashSet<>();
    nombre.add(new TipoFinanciacionNombre(Language.ES, "nombreTipoFinanciacion"));

    TipoFinanciacion tipoFinanciacion = TipoFinanciacion.builder()
        .id(id == null ? 1 : id)
        .nombre(nombre)
        .activo(true)
        .build();

    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = SolicitudProyectoEntidadFinanciadoraAjena
        .builder()
        .id(id)
        .solicitudProyectoId(id == null ? 1 : id)
        .entidadRef("entidad-" + (id == null ? 0 : String.format("%03d", id)))
        .fuenteFinanciacion(fuenteFinanciacion)
        .tipoFinanciacion(tipoFinanciacion)
        .porcentajeFinanciacion(BigDecimal.valueOf(50))
        .build();
    // @formatter:on

    return solicitudProyectoEntidadFinanciadoraAjena;
  }

  /**
   * Función que devuelve un objeto Solicitud
   * 
   * @param id                  id del Solicitud
   * @param convocatoriaId      id de la Convocatoria
   * @param convocatoriaExterna convocatoria externa
   * @return el objeto Solicitud
   */
  private Solicitud generarMockSolicitud(Long id, Long convocatoriaId, String convocatoriaExterna) {
    EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
    estadoSolicitud.setId(1L);
    estadoSolicitud.setEstado(EstadoSolicitud.Estado.BORRADOR);

    Programa programa = new Programa();
    programa.setId(1L);

    Set<SolicitudTitulo> solicitudTitulo = new HashSet<>();
    solicitudTitulo.add(new SolicitudTitulo(Language.ES, "titulo"));

    Set<SolicitudObservaciones> solicitudObservaciones = new HashSet<>();
    solicitudObservaciones.add(new SolicitudObservaciones(Language.ES, "observaciones-" + String.format("%03d", id)));

    Solicitud solicitud = new Solicitud();
    solicitud.setId(id);
    solicitud.setTitulo(solicitudTitulo);
    solicitud.setCodigoExterno(null);
    solicitud.setConvocatoriaId(convocatoriaId);
    solicitud.setCreadorRef("usr-001");
    solicitud.setSolicitanteRef("usr-002");
    solicitud.setObservaciones(solicitudObservaciones);
    solicitud.setConvocatoriaExterna(convocatoriaExterna);
    solicitud.setUnidadGestionRef("1");
    solicitud.setActivo(true);
    solicitud.setFormularioSolicitud(FormularioSolicitud.PROYECTO);
    solicitud.setOrigenSolicitud(
        convocatoriaId != null ? OrigenSolicitud.CONVOCATORIA_SGI : OrigenSolicitud.CONVOCATORIA_NO_SGI);

    if (id != null) {
      solicitud.setEstado(estadoSolicitud);
      solicitud.setCodigoRegistroInterno("SGI_SLC1202011061027");
      solicitud.setCreadorRef("usr-001");
    }

    return solicitud;
  }

}
