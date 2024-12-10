package org.crue.hercules.sgi.csp.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.eti.ChecklistOutput;
import org.crue.hercules.sgi.csp.dto.eti.ChecklistOutput.Formly;
import org.crue.hercules.sgi.csp.dto.eti.PeticionEvaluacion;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.ColaborativoWithoutCoordinadorExternoException;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.EstadoSolicitudNotUpdatedException;
import org.crue.hercules.sgi.csp.exceptions.MissingInvestigadorPrincipalInSolicitudProyectoEquipoException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoWithoutSocioCoordinadorException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudWithoutRequeridedDocumentationException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToChangeEstadoSolicitudException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud.Estado;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto.TipoPresupuesto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.DocumentoRequeridoSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.EstadoSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.RolSocioRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudExternaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoPresupuestoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiEtiService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.csp.util.GrupoAuthorityHelper;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * SolicitudServiceTest
 */
class SolicitudServiceTest extends BaseServiceTest {

  @Mock
  private SolicitudRepository repository;

  @Mock
  private EstadoSolicitudRepository estadoSolicitudRepository;

  @Mock
  private ConfiguracionSolicitudRepository configuracionSolicitudRepository;

  @Mock
  private DocumentoRequeridoSolicitudRepository documentoRequeridoSolicitudRepository;

  @Mock
  private SolicitudDocumentoRepository solicitudDocumentoRepository;

  @Mock
  private SolicitudProyectoRepository solicitudProyectoRepository;

  @Mock
  private SolicitudProyectoEquipoRepository solicitudProyectoEquipoRepository;

  @Mock
  private SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;

  @Mock
  private SolicitudProyectoPresupuestoRepository solicitudProyectoPresupuestoRepository;

  @Mock
  private ProyectoRepository proyectoRepository;

  @Mock
  private ConvocatoriaRepository convocatoriaRepository;

  @Mock
  ConvocatoriaEntidadFinanciadoraRepository convocatoriaEntidadFinanciadoraRepository;

  @Mock
  ConvocatoriaEnlaceRepository convocatoriaEnlaceRepository;

  @Mock
  private SgiApiEtiService sgiApiEtiService;

  @Mock
  private ComunicadosService comunicadosService;

  @Mock
  private SgiApiSgpService personasService;

  @Mock
  private ProgramaRepository programaRepository;

  @Mock
  private SolicitudAuthorityHelper solicitudAuthorityHelper;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @Mock
  private GrupoAuthorityHelper grupoAuthorityHelper;

  @Mock
  private SolicitudRrhhComService solicitudRrhhComService;

  @Mock
  private SolicitudComService solicitudComService;

  @Mock
  private SolicitudExternaRepository solicitudExternaRepository;

  @Mock
  private RolSocioRepository rolSocioRepository;

  private SolicitudService service;

  @BeforeEach
  void setUp() throws Exception {
    solicitudAuthorityHelper = new SolicitudAuthorityHelper(repository, solicitudExternaRepository);
    service = new SolicitudService(sgiConfigProperties,
        sgiApiEtiService, repository,
        estadoSolicitudRepository,
        configuracionSolicitudRepository,
        proyectoRepository,
        solicitudProyectoRepository,
        documentoRequeridoSolicitudRepository,
        solicitudDocumentoRepository,
        solicitudProyectoEquipoRepository,
        solicitudProyectoSocioRepository,
        solicitudProyectoPresupuestoRepository,
        convocatoriaRepository,
        convocatoriaEntidadFinanciadoraRepository,
        convocatoriaEnlaceRepository,
        programaRepository,
        solicitudAuthorityHelper,
        grupoAuthorityHelper,
        solicitudRrhhComService,
        solicitudComService,
        rolSocioRepository);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-C" })
  void create_WithConvocatoria_ReturnsSolicitud() {
    // given: Un nuevo Solicitud
    Solicitud solicitud = generarMockSolicitud(null, 1L, null);
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, solicitud.getUnidadGestionRef());

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConfiguracionSolicitud(1L, 1L)));
    BDDMockito.given(repository.save(ArgumentMatchers.<Solicitud>any())).will((InvocationOnMock invocation) -> {
      Solicitud solicitudCreado = invocation.getArgument(0);
      if (solicitudCreado.getId() == null) {
        solicitudCreado.setId(1L);
      }

      return solicitudCreado;
    });
    BDDMockito.given(estadoSolicitudRepository.save(ArgumentMatchers.<EstadoSolicitud>any()))
        .will((InvocationOnMock invocation) -> {
          EstadoSolicitud estadoSolicitudCreado = invocation.getArgument(0);
          estadoSolicitudCreado.setId(1L);
          return estadoSolicitudCreado;
        });

    // when: Creamos el Solicitud
    Solicitud solicitudCreado = service.create(solicitud);
    // then: El Solicitud se crea correctamente
    Assertions.assertThat(solicitudCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(solicitudCreado.getCodigoExterno()).as("getCodigoExterno()")
        .isEqualTo(solicitud.getCodigoExterno());
    Assertions.assertThat(solicitudCreado.getCodigoRegistroInterno()).as("getCodigoRegistroInterno()").isNotNull();
    Assertions.assertThat(solicitudCreado.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(solicitudCreado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(solicitud.getConvocatoriaId());
    Assertions.assertThat(solicitudCreado.getCreadorRef()).as("getCreadorRef()").isNotNull();
    Assertions.assertThat(solicitudCreado.getSolicitanteRef()).as("getSolicitanteRef()")
        .isEqualTo(solicitud.getSolicitanteRef());
    Assertions.assertThat(solicitudCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(solicitud.getObservaciones());
    Assertions.assertThat(solicitudCreado.getConvocatoriaExterna()).as("getConvocatoriaExterna()")
        .isEqualTo(solicitud.getConvocatoriaExterna());
    Assertions.assertThat(solicitudCreado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(solicitud.getUnidadGestionRef());
    Assertions.assertThat(solicitudCreado.getActivo()).as("getActivo").isEqualTo(solicitud.getActivo());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-C" })
  void create_WithConvocatoriaExterna_ReturnsSolicitud() {
    // given: Un nuevo Solicitud
    Solicitud solicitud = generarMockSolicitud(null, null, "externa");

    BDDMockito.given(repository.save(ArgumentMatchers.<Solicitud>any())).will((InvocationOnMock invocation) -> {
      Solicitud solicitudCreado = invocation.getArgument(0);
      if (solicitudCreado.getId() == null) {
        solicitudCreado.setId(1L);
      }

      return solicitudCreado;
    });

    BDDMockito.given(estadoSolicitudRepository.save(ArgumentMatchers.<EstadoSolicitud>any()))
        .will((InvocationOnMock invocation) -> {
          EstadoSolicitud estadoSolicitudCreado = invocation.getArgument(0);
          estadoSolicitudCreado.setId(1L);
          return estadoSolicitudCreado;
        });

    // when: Creamos el Solicitud
    Solicitud solicitudCreado = service.create(solicitud);

    // then: El Solicitud se crea correctamente
    Assertions.assertThat(solicitudCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(solicitudCreado.getCodigoExterno()).as("getCodigoExterno()")
        .isEqualTo(solicitud.getCodigoExterno());
    Assertions.assertThat(solicitudCreado.getCodigoRegistroInterno()).as("getCodigoRegistroInterno()").isNotNull();
    Assertions.assertThat(solicitudCreado.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(solicitudCreado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(solicitud.getConvocatoriaId());
    Assertions.assertThat(solicitudCreado.getCreadorRef()).as("getCreadorRef()").isNotNull();
    Assertions.assertThat(solicitudCreado.getSolicitanteRef()).as("getSolicitanteRef()")
        .isEqualTo(solicitud.getSolicitanteRef());
    Assertions.assertThat(solicitudCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(solicitud.getObservaciones());
    Assertions.assertThat(solicitudCreado.getConvocatoriaExterna()).as("getConvocatoriaExterna()")
        .isEqualTo(solicitud.getConvocatoriaExterna());
    Assertions.assertThat(solicitudCreado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(solicitud.getUnidadGestionRef());
    Assertions.assertThat(solicitudCreado.getActivo()).as("getActivo").isEqualTo(solicitud.getActivo());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-C" })
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo Solicitud que ya tiene id
    Solicitud solicitud = generarMockSolicitud(1L, 1L, null);

    // when: Creamos el Solicitud
    // then: Lanza una excepcion porque el Solicitud ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(solicitud)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Solicitud id tiene que ser null para crear una Solicitud");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-C" })
  void create_WithoutCreadorRef_ThrowsIllegalArgumentException() {
    // given: Un nuevo Solicitud que no tiene creadorRef
    Solicitud solicitud = generarMockSolicitud(null, 1L, null);
    solicitud.setCreadorRef(null);

    // when: Creamos el Solicitud
    // then: Lanza una excepcion porque no tiene creadorRef
    Assertions.assertThatThrownBy(() -> service.create(solicitud)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("CreadorRef no puede ser null para crear una Solicitud");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-C" })
  void create_WithoutConvocatoriaAndConvocatoriaExterna_ThrowsIllegalArgumentException() {
    // given: Un nuevo Solicitud que no tiene convocatoria ni convocatoria externa
    Solicitud solicitud = generarMockSolicitud(null, null, null);

    // when: Creamos el Solicitud
    // then: Lanza una excepcion porque no tiene convocatoria ni convocatoria
    // externa
    Assertions.assertThatThrownBy(() -> service.create(solicitud)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Convocatoria o Convocatoria externa tienen que ser distinto de null para crear una Solicitud");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-C" })
  void create_WithNoExistingConvocatoria_ThrowsProgramaNotFoundException() {
    // given: Un nuevo Solicitud que tiene una convocatoria que no existe
    Solicitud solicitud = generarMockSolicitud(null, 1L, null);

    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    // when: Creamos el Solicitud
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(solicitud))
        .isInstanceOf(ConfiguracionSolicitudNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-C_2" })
  void create_WithConvocatoriaWithNotAllowedUnidadGestion_ThrowsIllegalArgumentException() {
    // given: Un nuevo Solicitud que tiene convocatoria con una unidad de gestion no
    // permitida para el usuario
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, "1");
    Solicitud solicitud = generarMockSolicitud(null, 1L, null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(configuracionSolicitudRepository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConfiguracionSolicitud(1L, 1L)));

    // when: Creamos el Solicitud
    // then: Lanza una excepcion porque tiene convocatoria con una unidad de gestion
    // no
    // permitida para el usuario
    Assertions.assertThatThrownBy(() -> service.create(solicitud)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La Convocatoria pertenece a una Unidad de Gestión no gestionable por el usuario");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-C_2" })
  void create_WithConvocatoriaExternaAndNotAllowedUnidadGestion_ThrowsIllegalArgumentException() {
    // given: Un nuevo Solicitud que no tiene convocatoria ni convocatoria externa
    Solicitud solicitud = generarMockSolicitud(null, null, "externa");

    // when: Creamos el Solicitud
    // then: Lanza una excepcion porque no tiene convocatoria ni convocatoria
    // externa
    Assertions.assertThatThrownBy(() -> service.create(solicitud)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La Unidad de Gestión no es gestionable por el usuario");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_ReturnsSolicitud() {
    // given: Un nuevo Solicitud con las observaciones actualizadas
    Solicitud solicitud = generarMockSolicitud(1L, 1L, null);
    Solicitud solicitudoObservacionesActualizadas = generarMockSolicitud(1L, 1L, null);
    solicitudoObservacionesActualizadas.setObservaciones("observaciones actualizadas");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(solicitud));

    BDDMockito.given(repository.save(ArgumentMatchers.<Solicitud>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el Solicitud
    Solicitud solicitudActualizada = service.update(solicitudoObservacionesActualizadas);

    // then: El Solicitud se actualiza correctamente.
    Assertions.assertThat(solicitudActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudActualizada.getId()).as("getId()").isEqualTo(solicitud.getId());
    Assertions.assertThat(solicitudActualizada.getTitulo()).as("getTitulo()").isEqualTo(solicitud.getTitulo());
    Assertions.assertThat(solicitudActualizada.getCodigoExterno()).as("getCodigoExterno()")
        .isEqualTo(solicitud.getCodigoExterno());
    Assertions.assertThat(solicitudActualizada.getCodigoRegistroInterno()).as("getCodigoRegistroInterno()").isNotNull();
    Assertions.assertThat(solicitudActualizada.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(solicitudActualizada.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(solicitud.getConvocatoriaId());
    Assertions.assertThat(solicitudActualizada.getCreadorRef()).as("getCreadorRef()").isNotNull();
    Assertions.assertThat(solicitudActualizada.getSolicitanteRef()).as("getSolicitanteRef()")
        .isEqualTo(solicitud.getSolicitanteRef());
    Assertions.assertThat(solicitudActualizada.getObservaciones()).as("getObservaciones()")
        .isEqualTo(solicitud.getObservaciones());
    Assertions.assertThat(solicitudActualizada.getConvocatoriaExterna()).as("getConvocatoriaExterna()")
        .isEqualTo(solicitudoObservacionesActualizadas.getConvocatoriaExterna());
    Assertions.assertThat(solicitudActualizada.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(solicitud.getUnidadGestionRef());
    Assertions.assertThat(solicitudActualizada.getActivo()).as("getActivo").isEqualTo(solicitud.getActivo());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithFoundedSolicitudConvocatoriaIdNull_ReturnsSolicitud() {
    // given: Un nuevo Solicitud con las observaciones actualizadas
    Solicitud solicitud = generarMockSolicitud(1L, 1L, null);
    Solicitud solicitudoObservacionesActualizadas = generarMockSolicitud(1L, 1L, null);
    solicitudoObservacionesActualizadas.setObservaciones("observaciones actualizadas");

    solicitud.setConvocatoriaId(null);
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(solicitud));

    BDDMockito.given(repository.save(ArgumentMatchers.<Solicitud>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el Solicitud
    Solicitud solicitudActualizada = service.update(solicitudoObservacionesActualizadas);

    // then: El Solicitud se actualiza correctamente.
    Assertions.assertThat(solicitudActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudActualizada.getId()).as("getId()").isEqualTo(solicitud.getId());
    Assertions.assertThat(solicitudActualizada.getTitulo()).as("getTitulo()").isEqualTo(solicitud.getTitulo());
    Assertions.assertThat(solicitudActualizada.getCodigoExterno()).as("getCodigoExterno()")
        .isEqualTo(solicitud.getCodigoExterno());
    Assertions.assertThat(solicitudActualizada.getCodigoRegistroInterno()).as("getCodigoRegistroInterno()").isNotNull();
    Assertions.assertThat(solicitudActualizada.getEstado().getId()).as("getEstado().getId()").isNotNull();
    Assertions.assertThat(solicitudActualizada.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(solicitud.getConvocatoriaId());
    Assertions.assertThat(solicitudActualizada.getCreadorRef()).as("getCreadorRef()").isNotNull();
    Assertions.assertThat(solicitudActualizada.getSolicitanteRef()).as("getSolicitanteRef()")
        .isEqualTo(solicitud.getSolicitanteRef());
    Assertions.assertThat(solicitudActualizada.getObservaciones()).as("getObservaciones()")
        .isEqualTo(solicitud.getObservaciones());
    Assertions.assertThat(solicitudActualizada.getConvocatoriaExterna()).as("getConvocatoriaExterna()")
        .isEqualTo(solicitudoObservacionesActualizadas.getConvocatoriaExterna());
    Assertions.assertThat(solicitudActualizada.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(solicitud.getUnidadGestionRef());
    Assertions.assertThat(solicitudActualizada.getActivo()).as("getActivo").isEqualTo(solicitud.getActivo());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithIdNotExist_ThrowsSolicitudNotFoundException() {
    // given: Un Solicitud actualizado con un id que no existe
    Solicitud solicitud = generarMockSolicitud(1L, 1L, null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Actualizamos el Solicitud
    // then: Lanza una excepcion porque el Solicitud no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitud)).isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_ConvocatoriaNull_ThrowsIllegalArgumentException() {
    // given: Un nuevo Solicitud que no tiene creadorRef
    Solicitud solicitud = generarMockSolicitud(1L, 1L, null);
    solicitud.setConvocatoriaId(null);
    solicitud.setConvocatoriaExterna(null);

    // when: Creamos el Solicitud
    // then: Lanza una excepcion porque no tiene creadorRef
    Assertions.assertThatThrownBy(() -> service.update(solicitud)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Se debe seleccionar una convocatoria del SGI o convocatoria externa para actualizar Solicitud");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_SolicitanteRefNull_ThrowsIllegalArgumentException() {
    // given: Un nuevo Solicitud que no tiene creadorRef
    Solicitud solicitud = generarMockSolicitud(1L, 1L, null);
    solicitud.setFormularioSolicitud(FormularioSolicitud.PROYECTO);
    solicitud.setSolicitanteRef(null);

    // when: Creamos el Solicitud
    // then: Lanza una excepcion porque no tiene creadorRef
    Assertions.assertThatThrownBy(() -> service.update(solicitud)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El solicitante no puede ser null para actualizar Solicitud");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-R" })
  void enable_ReturnsSolicitud() {
    // given: Un nuevo Solicitud inactivo
    Solicitud solicitud = generarMockSolicitud(1L, 1L, null);
    solicitud.setActivo(false);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(solicitud));

    BDDMockito.given(repository.save(ArgumentMatchers.<Solicitud>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Activamos el Solicitud
    Solicitud programaActualizado = service.enable(solicitud.getId());

    // then: El Solicitud se activa correctamente.
    Assertions.assertThat(programaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(programaActualizado.getId()).as("getId()").isEqualTo(solicitud.getId());
    Assertions.assertThat(programaActualizado.getActivo()).as("getActivo()").isTrue();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-R" })
  void enable_WithIdNotExist_ThrowsSolicitudNotFoundException() {
    // given: Un id de un Solicitud que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el Solicitud
    // then: Lanza una excepcion porque el Solicitud no existe
    Assertions.assertThatThrownBy(() -> service.enable(idNoExiste)).isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-B" })
  void disable_ReturnsSolicitud() {
    // given: Un Solicitud activo
    Solicitud solicitud = generarMockSolicitud(1L, 1L, null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(solicitud));

    BDDMockito.given(repository.save(ArgumentMatchers.<Solicitud>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el Solicitud
    Solicitud solicitudActualizada = service.disable(solicitud.getId());

    // then: El Solicitud se desactivan correctamente
    Assertions.assertThat(solicitudActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudActualizada.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(solicitudActualizada.getActivo()).as("getActivo()").isFalse();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-INV-BR" })
  void disable_WithAuthorityInvAndSolicitudCreatorRefDistinctToCurrentUser_ThrowsIllegalArgumentException() {

    Solicitud solicitud = generarMockSolicitud(1L, 1L, null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(solicitud));

    Assertions.assertThatThrownBy(() -> service.disable(solicitud.getId())).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El usuario no es el creador de la Solicitud");

  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void disable_WithNotAuthorityInvAndNotAuthorityCSP_SOL_B_ThrowsIllegalArgumentException() {

    Solicitud solicitud = generarMockSolicitud(1L, 1L, null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(solicitud));

    Assertions.assertThatThrownBy(() -> service.disable(solicitud.getId())).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La Solicitud pertenece a una Unidad de Gestión no gestionable por el usuario");

  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-B" })
  void disable_WithIdNotExist_ThrowsSolicitudNotFoundException() {
    // given: Un id de un Solicitud que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el Solicitud
    // then: Lanza una excepcion porque el Solicitud no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste)).isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-V" })
  void findById_ReturnsSoliciud() {
    // given: Un Solicitud con el id buscado
    Long idBuscado = 1L;
    Solicitud solicitudBuscada = generarMockSolicitud(idBuscado, 1L, null);
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(solicitudBuscada));

    // when: Buscamos el Solicitud por su id
    Solicitud solicitud = service.findById(idBuscado);

    // then: el Solicitud
    Assertions.assertThat(solicitud).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitud.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(solicitud.getCodigoExterno()).as("getCodigoExterno()").isNull();
    Assertions.assertThat(solicitud.getCodigoRegistroInterno()).as("getCodigoRegistroInterno()")
        .isEqualTo("SGI_SLC1202011061027");
    Assertions.assertThat(solicitud.getEstado().getId()).as("getEstado().getId()").isEqualTo(1);
    Assertions.assertThat(solicitud.getConvocatoriaId()).as("getConvocatoriaId()").isEqualTo(1);
    Assertions.assertThat(solicitud.getCreadorRef()).as("getCreadorRef()").isEqualTo("usr-001");
    Assertions.assertThat(solicitud.getSolicitanteRef()).as("getSolicitanteRef()").isEqualTo("usr-002");
    Assertions.assertThat(solicitud.getObservaciones()).as("getObservaciones()").isEqualTo("observaciones-001");
    Assertions.assertThat(solicitud.getConvocatoriaExterna()).as("getConvocatoriaExterna()").isNull();
    Assertions.assertThat(solicitud.getUnidadGestionRef()).as("getUnidadGestionRef()").isEqualTo("1");
    Assertions.assertThat(solicitud.getActivo()).as("getActivo()").isTrue();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-V" })
  void findById_WithIdNotExist_ThrowsProgramaNotFoundException() throws Exception {
    // given: Ningun Solicitud con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el Solicitud por su id
    // then: lanza un SolicitudNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado)).isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-V" })
  void findAll_ReturnsPage() {
    // given: Una lista con 37 Solicitud
    List<Solicitud> solicitudes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudes.add(generarMockSolicitud(i, 1L, null));
    }

    BDDMockito
        .given(repository.findAllDistinct(ArgumentMatchers.<Specification<Solicitud>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Solicitud>>() {
          @Override
          public Page<Solicitud> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > solicitudes.size() ? solicitudes.size() : toIndex;
            List<Solicitud> content = solicitudes.subList(fromIndex, toIndex);
            Page<Solicitud> page = new PageImpl<>(content, pageable, solicitudes.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Solicitud> page = service.findAllRestringidos(null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Solicitud solicitud = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitud.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-V" })
  void findAllTodos_ReturnsPage() {
    // given: Una lista con 37 Solicitud
    List<Solicitud> solicitudes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudes.add(generarMockSolicitud(i, 1L, null));
    }

    BDDMockito
        .given(repository.findAllDistinct(ArgumentMatchers.<Specification<Solicitud>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Solicitud>>() {
          @Override
          public Page<Solicitud> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > solicitudes.size() ? solicitudes.size() : toIndex;
            List<Solicitud> content = solicitudes.subList(fromIndex, toIndex);
            Page<Solicitud> page = new PageImpl<>(content, pageable, solicitudes.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Solicitud> page = service.findAllTodosRestringidos(null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Solicitud solicitud = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitud.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-V_1" })
  void findAllTodos_WithUO1_ReturnsPage() {
    // given: Una lista con 37 Solicitud
    List<Solicitud> solicitudes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudes.add(generarMockSolicitud(i, 1L, null));
    }

    BDDMockito
        .given(repository.findAllDistinct(ArgumentMatchers.<Specification<Solicitud>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Solicitud>>() {
          @Override
          public Page<Solicitud> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > solicitudes.size() ? solicitudes.size() : toIndex;
            List<Solicitud> content = solicitudes.subList(fromIndex, toIndex);
            Page<Solicitud> page = new PageImpl<>(content, pageable, solicitudes.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Solicitud> page = service.findAllTodosRestringidos(null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Solicitud solicitud = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitud.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  @Test
  void cambiarEstado_WithSameEstado_ThrowsEstadoSolicitudNotUpdatedException() {
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    EstadoSolicitud newEstado = EstadoSolicitud.builder()
        .estado(Estado.BORRADOR)
        .build();

    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));

    Assertions.assertThatThrownBy(() -> this.service.cambiarEstado(solicitudId, newEstado))
        .isInstanceOf(EstadoSolicitudNotUpdatedException.class);
  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  @Test
  void cambiarEstado_WithNotEstadoDesistidaNeitherRenunciada_ReturnsSolicitud() {
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    EstadoSolicitud newEstado = EstadoSolicitud.builder()
        .estado(Estado.ADMITIDA_DEFINITIVA)
        .build();
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(solicitudId);
    PeticionEvaluacion peticionEvaluacion = buildMockPeticionEvaluacion(1L, 1L);
    List<SolicitudProyectoEquipo> equipos = Arrays.asList(buildMockSolicitudProyectoEquipo(1L, 1, 9));

    //@formatter:off
    ChecklistOutput responseChecklistOutput = ChecklistOutput.builder()
                                                                .fechaCreacion(Instant.now())
                                                                .id(1L)
                                                                .personaRef("user")
                                                                .formly(Formly.builder()
                                                                  .id(1L)
                                                                  .esquema("<div></div>")
                                                                  .build())
                                                                .respuesta("respuesta mocked from server true")
                                                                .build();
    //@formatter:on
    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(estadoSolicitudRepository.save(ArgumentMatchers.<EstadoSolicitud>any())).willReturn(newEstado);

    BDDMockito.given(solicitudProyectoRepository.findById(solicitud.getId()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito
        .given(solicitudProyectoEquipoRepository.findAllBySolicitudProyectoIdAndPersonaRef(anyLong(), anyString()))
        .willReturn(Arrays.asList(SolicitudProyectoEquipo.builder().build()));
    BDDMockito.given(solicitudProyectoRepository.save(ArgumentMatchers.<SolicitudProyecto>any()))
        .willReturn(solicitudProyecto);

    BDDMockito.given(sgiApiEtiService.getCheckList(anyString())).willReturn(responseChecklistOutput);

    BDDMockito.given(sgiApiEtiService.newPeticionEvaluacion(
        ArgumentMatchers.<PeticionEvaluacion>any())).willReturn(peticionEvaluacion);

    BDDMockito.given(repository.save(ArgumentMatchers.<Solicitud>any())).willReturn(solicitud);

    BDDMockito.given(solicitudProyectoEquipoRepository.findAllBySolicitudProyectoId(anyLong())).willReturn(equipos);

    Solicitud solicitudChanged = this.service.cambiarEstado(solicitudId, newEstado);

    Assertions.assertThat(solicitudChanged).isNotNull();
  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  @Test
  void cambiarEstado_WithChecklistNullAndEstadoDenegada_ReturnsSolicitud() {
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    EstadoSolicitud newEstado = EstadoSolicitud.builder()
        .estado(Estado.DENEGADA)
        .build();
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(solicitudId);
    solicitudProyecto.setPeticionEvaluacionRef("pet-eva-0001");
    PeticionEvaluacion peticionEvaluacion = buildMockPeticionEvaluacion(1L, 1L);

    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(estadoSolicitudRepository.save(ArgumentMatchers.<EstadoSolicitud>any())).willReturn(newEstado);

    BDDMockito.given(solicitudProyectoRepository.findById(solicitud.getId()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito
        .given(solicitudProyectoEquipoRepository.findAllBySolicitudProyectoIdAndPersonaRef(anyLong(), anyString()))
        .willReturn(Arrays.asList(SolicitudProyectoEquipo.builder().build()));
    BDDMockito.given(sgiApiEtiService.getPeticionEvaluacion(anyString())).willReturn(peticionEvaluacion);

    BDDMockito.given(repository.save(ArgumentMatchers.<Solicitud>any())).willReturn(solicitud);

    Solicitud solicitudChanged = this.service.cambiarEstado(solicitudId, newEstado);

    Assertions.assertThat(solicitudChanged).isNotNull();
  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  @Test
  void cambiarEstado_WithChecklistNullAndEstadoConcedidaAndDocumentosRequeridosSolicitudNotEmpty_ReturnsSolicitud() {
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    EstadoSolicitud newEstado = EstadoSolicitud.builder()
        .estado(Estado.CONCEDIDA)
        .build();
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(solicitudId);
    solicitudProyecto.setPeticionEvaluacionRef("pet-eva-0001");
    PeticionEvaluacion peticionEvaluacion = buildMockPeticionEvaluacion(1L, 1L);
    TipoDocumento tipoDocumento = buildMockTipoDocumento(1L);
    List<DocumentoRequeridoSolicitud> documentos = Arrays
        .asList(buildMockDocumentoRequeridoSolicitud(1L, tipoDocumento));
    List<SolicitudDocumento> solicitudDocumentos = Arrays.asList(SolicitudDocumento.builder().build());

    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(estadoSolicitudRepository.save(ArgumentMatchers.<EstadoSolicitud>any())).willReturn(newEstado);

    BDDMockito.given(solicitudProyectoRepository.findById(solicitud.getId()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito
        .given(solicitudProyectoEquipoRepository.findAllBySolicitudProyectoIdAndPersonaRef(anyLong(), anyString()))
        .willReturn(Arrays.asList(SolicitudProyectoEquipo.builder().build()));
    BDDMockito.given(sgiApiEtiService.getPeticionEvaluacion(anyString())).willReturn(peticionEvaluacion);

    BDDMockito.given(repository.save(ArgumentMatchers.<Solicitud>any())).willReturn(solicitud);

    BDDMockito.given(documentoRequeridoSolicitudRepository
        .findAll(ArgumentMatchers.<Specification<DocumentoRequeridoSolicitud>>any())).willReturn(documentos);

    BDDMockito.given(solicitudDocumentoRepository
        .findAllByTipoDocumentoIdInAndSolicitudId(ArgumentMatchers.<List<Long>>any(), anyLong()))
        .willReturn(solicitudDocumentos);

    Solicitud solicitudChanged = this.service.cambiarEstado(solicitudId, newEstado);

    Assertions.assertThat(solicitudChanged).isNotNull();
  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  @Test
  void cambiarEstado_WithChecklistNullAndEstadoConcedida_ReturnsSolicitud() {
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    EstadoSolicitud newEstado = EstadoSolicitud.builder()
        .estado(Estado.CONCEDIDA)
        .build();
    SolicitudProyecto solicitudProyecto = generarSolicitudProyecto(solicitudId);
    solicitudProyecto.setPeticionEvaluacionRef("pet-eva-0001");
    PeticionEvaluacion peticionEvaluacion = buildMockPeticionEvaluacion(1L, 1L);

    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));
    BDDMockito.given(estadoSolicitudRepository.save(ArgumentMatchers.<EstadoSolicitud>any())).willReturn(newEstado);

    BDDMockito.given(solicitudProyectoRepository.findById(solicitud.getId()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito
        .given(solicitudProyectoEquipoRepository.findAllBySolicitudProyectoIdAndPersonaRef(anyLong(), anyString()))
        .willReturn(Arrays.asList(SolicitudProyectoEquipo.builder().build()));
    BDDMockito.given(sgiApiEtiService.getPeticionEvaluacion(anyString())).willReturn(peticionEvaluacion);

    BDDMockito.given(repository.save(ArgumentMatchers.<Solicitud>any())).willReturn(solicitud);

    Solicitud solicitudChanged = this.service.cambiarEstado(solicitudId, newEstado);

    Assertions.assertThat(solicitudChanged).isNotNull();
  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  @Test
  void cambiarEstado_WithoutDocumentoRequerido_ThrowsSolicitudWithoutRequeridedDocumentationException() {
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    EstadoSolicitud newEstado = EstadoSolicitud.builder()
        .estado(Estado.CONCEDIDA)
        .build();
    TipoDocumento tipoDocumento = buildMockTipoDocumento(1L);
    List<DocumentoRequeridoSolicitud> documentos = Arrays
        .asList(buildMockDocumentoRequeridoSolicitud(1L, tipoDocumento));

    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));

    BDDMockito.given(documentoRequeridoSolicitudRepository
        .findAll(ArgumentMatchers.<Specification<DocumentoRequeridoSolicitud>>any())).willReturn(documentos);

    Assertions.assertThatThrownBy(() -> service.cambiarEstado(solicitudId, newEstado))
        .isInstanceOf(SolicitudWithoutRequeridedDocumentationException.class);
  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  @Test
  void cambiarEstado_WithDocumentoRequerido_ThrowsSolicitudProyectoNotFoundException() {
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    EstadoSolicitud newEstado = EstadoSolicitud.builder()
        .estado(Estado.CONCEDIDA)
        .build();
    solicitud.setFormularioSolicitud(FormularioSolicitud.PROYECTO);
    TipoDocumento tipoDocumento = buildMockTipoDocumento(1L);
    List<DocumentoRequeridoSolicitud> documentos = Arrays
        .asList(buildMockDocumentoRequeridoSolicitud(1L, tipoDocumento));
    List<SolicitudDocumento> solicitudDocumentos = new LinkedList<>();

    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));

    BDDMockito.given(documentoRequeridoSolicitudRepository
        .findAll(ArgumentMatchers.<Specification<DocumentoRequeridoSolicitud>>any())).willReturn(documentos);

    BDDMockito.given(solicitudDocumentoRepository
        .findAllByTipoDocumentoIdInAndSolicitudId(ArgumentMatchers.<List<Long>>any(), anyLong()))
        .willReturn(solicitudDocumentos);

    Assertions.assertThatThrownBy(() -> service.cambiarEstado(solicitudId, newEstado))
        .isInstanceOf(SolicitudWithoutRequeridedDocumentationException.class);
  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  @Test
  void cambiarEstado_WithDocumentoRequerido_ThrowsMissingInvestigadorPrincipalInSolicitudProyectoEquipoException() {
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    EstadoSolicitud newEstado = EstadoSolicitud.builder()
        .estado(Estado.CONCEDIDA)
        .build();
    solicitud.setFormularioSolicitud(FormularioSolicitud.PROYECTO);
    TipoDocumento tipoDocumento = buildMockTipoDocumento(1L);
    List<DocumentoRequeridoSolicitud> documentos = Arrays
        .asList(buildMockDocumentoRequeridoSolicitud(1L, tipoDocumento));
    List<SolicitudDocumento> solicitudDocumentos = Arrays.asList(SolicitudDocumento.builder().build());

    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));

    BDDMockito.given(documentoRequeridoSolicitudRepository
        .findAll(ArgumentMatchers.<Specification<DocumentoRequeridoSolicitud>>any())).willReturn(documentos);

    BDDMockito.given(solicitudDocumentoRepository
        .findAllByTipoDocumentoIdInAndSolicitudId(ArgumentMatchers.<List<Long>>any(), anyLong()))
        .willReturn(solicitudDocumentos);

    BDDMockito.given(solicitudProyectoRepository.findById(anyLong()))
        .willReturn(Optional.of(SolicitudProyecto.builder().id(1L).build()));

    BDDMockito
        .given(solicitudProyectoEquipoRepository.findAllBySolicitudProyectoIdAndPersonaRef(anyLong(), anyString()))
        .willReturn(new LinkedList<>());

    Assertions.assertThatThrownBy(() -> service.cambiarEstado(solicitudId, newEstado))
        .isInstanceOf(MissingInvestigadorPrincipalInSolicitudProyectoEquipoException.class);
  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  @Test
  void cambiarEstado_WithDocumentoRequerido_ThrowsColaborativoWithoutCoordinadorExternoException() {
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    EstadoSolicitud newEstado = EstadoSolicitud.builder()
        .estado(Estado.CONCEDIDA)
        .build();
    solicitud.setFormularioSolicitud(FormularioSolicitud.PROYECTO);
    TipoDocumento tipoDocumento = buildMockTipoDocumento(1L);
    List<DocumentoRequeridoSolicitud> documentos = Arrays
        .asList(buildMockDocumentoRequeridoSolicitud(1L, tipoDocumento));
    List<SolicitudDocumento> solicitudDocumentos = Arrays.asList(SolicitudDocumento.builder().build());

    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));

    BDDMockito.given(documentoRequeridoSolicitudRepository
        .findAll(ArgumentMatchers.<Specification<DocumentoRequeridoSolicitud>>any())).willReturn(documentos);

    BDDMockito.given(solicitudDocumentoRepository
        .findAllByTipoDocumentoIdInAndSolicitudId(ArgumentMatchers.<List<Long>>any(), anyLong()))
        .willReturn(solicitudDocumentos);
    // @formatter: off
    BDDMockito.given(solicitudProyectoRepository.findById(anyLong())).willReturn(Optional.of(SolicitudProyecto.builder()
        .id(1L)
        .colaborativo(Boolean.TRUE)
        .build()));
    // @formatter: on
    BDDMockito
        .given(solicitudProyectoEquipoRepository.findAllBySolicitudProyectoIdAndPersonaRef(anyLong(), anyString()))
        .willReturn(Arrays.asList(SolicitudProyectoEquipo.builder().build()));

    Assertions.assertThatThrownBy(() -> service.cambiarEstado(solicitudId, newEstado))
        .isInstanceOf(ColaborativoWithoutCoordinadorExternoException.class);
  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  @Test
  void cambiarEstado_WithDocumentoRequerido_ThrowsSolicitudProyectoWithoutSocioCoordinadorException() {
    Long solicitudId = 1L;
    Long rolUniversidadSocioId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    EstadoSolicitud newEstado = EstadoSolicitud.builder()
        .estado(Estado.CONCEDIDA)
        .build();
    solicitud.setFormularioSolicitud(FormularioSolicitud.PROYECTO);
    TipoDocumento tipoDocumento = buildMockTipoDocumento(1L);
    List<DocumentoRequeridoSolicitud> documentos = Arrays
        .asList(buildMockDocumentoRequeridoSolicitud(1L, tipoDocumento));
    List<SolicitudDocumento> solicitudDocumentos = Arrays.asList(SolicitudDocumento.builder().build());

    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));

    BDDMockito.given(documentoRequeridoSolicitudRepository
        .findAll(ArgumentMatchers.<Specification<DocumentoRequeridoSolicitud>>any())).willReturn(documentos);

    BDDMockito.given(solicitudDocumentoRepository
        .findAllByTipoDocumentoIdInAndSolicitudId(ArgumentMatchers.<List<Long>>any(), anyLong()))
        .willReturn(solicitudDocumentos);
    // @formatter: off
    BDDMockito.given(solicitudProyectoRepository.findById(anyLong())).willReturn(Optional.of(SolicitudProyecto.builder()
        .id(1L)
        .colaborativo(Boolean.TRUE)
        .rolUniversidadId(rolUniversidadSocioId)
        .build()));
    // @formatter: on
    BDDMockito
        .given(solicitudProyectoEquipoRepository.findAllBySolicitudProyectoIdAndPersonaRef(anyLong(), anyString()))
        .willReturn(Arrays.asList(SolicitudProyectoEquipo.builder().build()));
    BDDMockito.given(rolSocioRepository.findById(anyLong()))
        .willReturn(Optional.of(RolSocio.builder().coordinador(false).build()));

    Assertions.assertThatThrownBy(() -> service.cambiarEstado(solicitudId, newEstado))
        .isInstanceOf(SolicitudProyectoWithoutSocioCoordinadorException.class);
  }

  @WithMockUser(username = "usr-001", authorities = { "CSP-SOL-INV-ER" })
  @Test
  void cambiarEstado_WithEstadoSubsanacion_ThrowsUserNotAuthorizedToChangeEstadoSolicitudException() {
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    EstadoSolicitud newEstado = EstadoSolicitud.builder()
        .estado(Estado.CONCEDIDA)
        .build();
    solicitud.setEstado(EstadoSolicitud.builder()
        .estado(Estado.SUBSANACION).build());

    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));

    Assertions.assertThatThrownBy(() -> service.cambiarEstado(solicitudId, newEstado))
        .isInstanceOf(UserNotAuthorizedToChangeEstadoSolicitudException.class);
  }

  @WithMockUser(username = "usr-001", authorities = { "CSP-SOL-INV-ER" })
  @Test
  void cambiarEstado_WithExcluidaProvisional_ThrowsUserNotAuthorizedToChangeEstadoSolicitudException() {
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    EstadoSolicitud newEstado = EstadoSolicitud.builder()
        .estado(Estado.CONCEDIDA)
        .build();
    solicitud.setEstado(EstadoSolicitud.builder()
        .estado(Estado.EXCLUIDA_PROVISIONAL).build());

    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));

    Assertions.assertThatThrownBy(() -> service.cambiarEstado(solicitudId, newEstado))
        .isInstanceOf(UserNotAuthorizedToChangeEstadoSolicitudException.class);
  }

  @WithMockUser(username = "usr-001", authorities = { "CSP-SOL-INV-ER" })
  @Test
  void cambiarEstado_WithExcluidaDefinitiva_ThrowsUserNotAuthorizedToChangeEstadoSolicitudException() {
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    EstadoSolicitud newEstado = EstadoSolicitud.builder()
        .estado(Estado.CONCEDIDA)
        .build();
    solicitud.setEstado(EstadoSolicitud.builder()
        .estado(Estado.EXCLUIDA_DEFINITIVA).build());

    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));

    Assertions.assertThatThrownBy(() -> service.cambiarEstado(solicitudId, newEstado))
        .isInstanceOf(UserNotAuthorizedToChangeEstadoSolicitudException.class);
  }

  @WithMockUser(username = "usr-001", authorities = { "CSP-SOL-INV-ER" })
  @Test
  void cambiarEstado_WithDenegadaProvisional_ThrowsUserNotAuthorizedToChangeEstadoSolicitudException() {
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    EstadoSolicitud newEstado = EstadoSolicitud.builder()
        .estado(Estado.CONCEDIDA)
        .build();
    solicitud.setEstado(EstadoSolicitud.builder()
        .estado(Estado.DENEGADA_PROVISIONAL).build());

    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));

    Assertions.assertThatThrownBy(() -> service.cambiarEstado(solicitudId, newEstado))
        .isInstanceOf(UserNotAuthorizedToChangeEstadoSolicitudException.class);
  }

  @WithMockUser(username = "usr-001", authorities = { "CSP-SOL-INV-ER" })
  @Test
  void cambiarEstado_WithDenegada_ThrowsUserNotAuthorizedToChangeEstadoSolicitudException() {
    Long solicitudId = 1L;
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    EstadoSolicitud newEstado = EstadoSolicitud.builder()
        .estado(Estado.CONCEDIDA)
        .build();
    solicitud.setEstado(EstadoSolicitud.builder()
        .estado(Estado.DENEGADA).build());

    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));

    Assertions.assertThatThrownBy(() -> service.cambiarEstado(solicitudId, newEstado))
        .isInstanceOf(UserNotAuthorizedToChangeEstadoSolicitudException.class);
  }

  @Test
  void isPosibleCrearProyecto_WithEstadoSolicitudConcedidoAndSolicitudProyecctoExist_ReturnTrue() {
    Long solicitudId = 1L;
    EstadoSolicitud estado = EstadoSolicitud.builder()
        .estado(Estado.CONCEDIDA)
        .build();
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    solicitud.setFormularioSolicitud(FormularioSolicitud.PROYECTO);
    solicitud.setEstado(estado);

    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));

    BDDMockito.given(solicitudProyectoRepository.existsById(anyLong())).willReturn(Boolean.TRUE);

    boolean response = this.service.isPosibleCrearProyecto(solicitudId);

    Assertions.assertThat(response).isTrue();

  }

  @Test
  void isPosibleCrearProyecto_WithEstadoSolicitudConcedidoAndSolicitudProyecctoNotExist_ReturnTrue() {
    Long solicitudId = 1L;
    EstadoSolicitud estado = EstadoSolicitud.builder()
        .estado(Estado.CONCEDIDA)
        .build();
    Solicitud solicitud = generarMockSolicitud(solicitudId, 1L, null);
    solicitud.setFormularioSolicitud(FormularioSolicitud.PROYECTO);
    solicitud.setEstado(estado);

    BDDMockito.given(repository.findById(anyLong())).willReturn(Optional.of(solicitud));

    BDDMockito.given(solicitudProyectoRepository.existsById(anyLong())).willReturn(Boolean.FALSE);

    boolean response = this.service.isPosibleCrearProyecto(solicitudId);

    Assertions.assertThat(response).isFalse();

  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-ETI-V" })
  void getCodigoRegistroInterno_WithIdExist_ReturnsCodigoRegistroInterno() throws Exception {
    // given: Ningun Solicitud con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el Solicitud por su id
    // then: lanza un SolicitudNotFoundException
    Assertions.assertThatThrownBy(() -> service.getCodigoRegistroInterno(idBuscado))
        .isInstanceOf(SolicitudNotFoundException.class);
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

    Solicitud solicitud = new Solicitud();
    solicitud.setId(id);
    solicitud.setTitulo("titulo");
    solicitud.setCodigoExterno(null);
    solicitud.setConvocatoriaId(convocatoriaId);
    solicitud.setCreadorRef("usr-001");
    solicitud.setSolicitanteRef("usr-002");
    solicitud.setObservaciones("observaciones-" + String.format("%03d", id));
    solicitud.setConvocatoriaExterna(convocatoriaExterna);
    solicitud.setUnidadGestionRef("1");
    solicitud.setActivo(true);
    solicitud.setFormularioSolicitud(FormularioSolicitud.PROYECTO);

    if (id != null) {
      solicitud.setEstado(estadoSolicitud);
      solicitud.setCodigoRegistroInterno("SGI_SLC1202011061027");
      solicitud.setCreadorRef("usr-001");
    }

    return solicitud;
  }

  /**
   * Función que devuelve un objeto Convocatoria
   * 
   * @param convocatoriaId   id de la Convocatoria
   * @param unidadGestionRef UnidadGestionRef
   * @return la convocatoria
   */
  private Convocatoria generarMockConvocatoria(Long convocatoriaId, String unidadGestionRef) {
    Convocatoria convocatoria = new Convocatoria();
    convocatoria.setId(convocatoriaId);
    convocatoria.setUnidadGestionRef(unidadGestionRef);
    convocatoria.setActivo(true);

    return convocatoria;
  }

  /**
   * Genera un objeto ConfiguracionSolicitud
   * 
   * @param configuracionSolicitudId
   * @param convocatoriaId
   * @param convocatoriaFaseId
   * @return
   */
  private ConfiguracionSolicitud generarMockConfiguracionSolicitud(Long configuracionSolicitudId, Long convocatoriaId) {

    // @formatter:off
    ConfiguracionSolicitud configuracionSolicitud = ConfiguracionSolicitud.builder()
        .id(configuracionSolicitudId)
        .convocatoriaId(convocatoriaId)
        .tramitacionSGI(Boolean.TRUE)
        .importeMaximoSolicitud(BigDecimal.valueOf(12345))
        .build();
    // @formatter:on

    return configuracionSolicitud;
  }

  private SolicitudProyecto generarSolicitudProyecto(Long solicitudProyectoId) {

    return SolicitudProyecto.builder()
        .id(solicitudProyectoId)
        .acronimo("acronimo-" + solicitudProyectoId)
        .colaborativo(Boolean.FALSE)
        .tipoPresupuesto(TipoPresupuesto.GLOBAL)
        .checklistRef("checklist-001")
        .build();
  }

  private PeticionEvaluacion buildMockPeticionEvaluacion(Long id, Long checklistId) {
    return PeticionEvaluacion.builder()
        .id(id)
        .activo(Boolean.TRUE)
        .checklistId(checklistId)
        .build();
  }

  private SolicitudProyectoEquipo buildMockSolicitudProyectoEquipo(Long id, int mesInicio, int mesFin) {
    return SolicitudProyectoEquipo.builder()
        .id(id)
        .personaRef("usr-00" + 1)
        .mesFin(mesFin)
        .mesInicio(mesInicio)
        .build();
  }

  private DocumentoRequeridoSolicitud buildMockDocumentoRequeridoSolicitud(Long id, TipoDocumento tipoDocumento) {
    return DocumentoRequeridoSolicitud.builder()
        .id(id)
        .configuracionSolicitudId(1L)
        .observaciones("Testing")
        .tipoDocumento(tipoDocumento)
        .build();
  }

  private TipoDocumento buildMockTipoDocumento(Long id) {
    return TipoDocumento.builder()
        .activo(Boolean.TRUE)
        .id(id)
        .descripcion("testion")
        .build();
  }
}
