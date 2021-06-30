package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.DocumentoRequeridoSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.EstadoSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoPresupuestoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.client.RestTemplate;

/**
 * SolicitudServiceTest
 */
public class SolicitudServiceTest extends BaseServiceTest {

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
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  private SolicitudService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new SolicitudService(restApiProperties, restTemplate, repository, estadoSolicitudRepository,
        configuracionSolicitudRepository, proyectoRepository, solicitudProyectoRepository,
        documentoRequeridoSolicitudRepository, solicitudDocumentoRepository, solicitudProyectoEquipoRepository,
        solicitudProyectoSocioRepository, solicitudProyectoPresupuestoRepository, convocatoriaRepository,
        convocatoriaEntidadFinanciadoraRepository);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-C" })
  public void create_WithConvocatoria_ReturnsSolicitud() {
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
  public void create_WithConvocatoriaExterna_ReturnsSolicitud() {
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
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo Solicitud que ya tiene id
    Solicitud solicitud = generarMockSolicitud(1L, 1L, null);

    // when: Creamos el Solicitud
    // then: Lanza una excepcion porque el Solicitud ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(solicitud)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Solicitud id tiene que ser null para crear una Solicitud");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-C" })
  public void create_WithoutCreadorRef_ThrowsIllegalArgumentException() {
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
  public void create_WithoutConvocatoriaAndConvocatoriaExterna_ThrowsIllegalArgumentException() {
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
  public void create_WithNoExistingConvocatoria_ThrowsProgramaNotFoundException() {
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
  public void create_WithConvocatoriaWithNotAllowedUnidadGestion_ThrowsIllegalArgumentException() {
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
  public void create_WithConvocatoriaExternaAndNotAllowedUnidadGestion_ThrowsIllegalArgumentException() {
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
  public void update_ReturnsSolicitud() {
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
  public void update_WithIdNotExist_ThrowsSolicitudNotFoundException() {
    // given: Un Solicitud actualizado con un id que no existe
    Solicitud solicitud = generarMockSolicitud(1L, 1L, null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Actualizamos el Solicitud
    // then: Lanza una excepcion porque el Solicitud no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitud)).isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  public void update_ConvocatoriaNull_ThrowsIllegalArgumentException() {
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
  public void update_SolicitanteRefNull_ThrowsIllegalArgumentException() {
    // given: Un nuevo Solicitud que no tiene creadorRef
    Solicitud solicitud = generarMockSolicitud(1L, 1L, null);
    solicitud.setSolicitanteRef(null);

    // when: Creamos el Solicitud
    // then: Lanza una excepcion porque no tiene creadorRef
    Assertions.assertThatThrownBy(() -> service.update(solicitud)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El solicitante no puede ser null para actualizar Solicitud");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-R" })
  public void enable_ReturnsSolicitud() {
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
    Assertions.assertThat(programaActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-R" })
  public void enable_WithIdNotExist_ThrowsSolicitudNotFoundException() {
    // given: Un id de un Solicitud que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el Solicitud
    // then: Lanza una excepcion porque el Solicitud no existe
    Assertions.assertThatThrownBy(() -> service.enable(idNoExiste)).isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-B" })
  public void disable_ReturnsSolicitud() {
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
    Assertions.assertThat(solicitudActualizada.getActivo()).as("getActivo()").isEqualTo(false);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-B" })
  public void disable_WithIdNotExist_ThrowsSolicitudNotFoundException() {
    // given: Un id de un Solicitud que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el Solicitud
    // then: Lanza una excepcion porque el Solicitud no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste)).isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-V" })
  public void findById_ReturnsSoliciud() {
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
    Assertions.assertThat(solicitud.getConvocatoriaExterna()).as("getConvocatoriaExterna()").isEqualTo(null);
    Assertions.assertThat(solicitud.getUnidadGestionRef()).as("getUnidadGestionRef()").isEqualTo("1");
    Assertions.assertThat(solicitud.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-V" })
  public void findById_WithIdNotExist_ThrowsProgramaNotFoundException() throws Exception {
    // given: Ningun Solicitud con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el Solicitud por su id
    // then: lanza un SolicitudNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado)).isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-V" })
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 Solicitud
    List<Solicitud> solicitudes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudes.add(generarMockSolicitud(i, 1L, null));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Solicitud>>any(), ArgumentMatchers.<Pageable>any()))
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
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
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
  public void findAllTodos_ReturnsPage() {
    // given: Una lista con 37 Solicitud
    List<Solicitud> solicitudes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudes.add(generarMockSolicitud(i, 1L, null));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Solicitud>>any(), ArgumentMatchers.<Pageable>any()))
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
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Solicitud solicitud = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitud.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
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
    solicitud.setCodigoExterno(null);
    solicitud.setConvocatoriaId(convocatoriaId);
    solicitud.setCreadorRef("usr-001");
    solicitud.setSolicitanteRef("usr-002");
    solicitud.setObservaciones("observaciones-" + String.format("%03d", id));
    solicitud.setConvocatoriaExterna(convocatoriaExterna);
    solicitud.setUnidadGestionRef("1");
    solicitud.setActivo(true);

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
        .formularioSolicitud(FormularioSolicitud.ESTANDAR)
        .build();
    // @formatter:on

    return configuracionSolicitud;
  }

}
