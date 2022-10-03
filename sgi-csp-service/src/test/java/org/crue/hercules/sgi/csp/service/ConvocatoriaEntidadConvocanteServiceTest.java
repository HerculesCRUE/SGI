package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEntidadConvocanteNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProgramaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadConvocanteRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaEntidadConvocanteServiceImpl;
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
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
 * ConvocatoriaEntidadConvocanteServiceTest
 */
class ConvocatoriaEntidadConvocanteServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaEntidadConvocanteRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ProgramaRepository programaRepository;
  @Mock
  private ConvocatoriaService convocatoriaService;
  @Mock
  private ConfiguracionSolicitudRepository configuracionSolicitudRepository;
  @Mock
  private SolicitudRepository solicitudRepository;

  private ConvocatoriaAuthorityHelper authorityHelper;
  private ConvocatoriaEntidadConvocanteService service;

  @BeforeEach
  void setUp() throws Exception {
    this.authorityHelper = new ConvocatoriaAuthorityHelper(convocatoriaRepository, configuracionSolicitudRepository);
    service = new ConvocatoriaEntidadConvocanteServiceImpl(repository, convocatoriaRepository, programaRepository,
        convocatoriaService, solicitudRepository, authorityHelper);
  }

  @Test
  void create_ReturnsConvocatoriaEntidadConvocante() {
    // given: Un nuevo ConvocatoriaEntidadConvocante
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(null,
        convocatoriaId);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(programaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadConvocante.getPrograma()));
    BDDMockito
        .given(repository.findByConvocatoriaIdAndEntidadRef(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.empty());

    BDDMockito.given(repository.save(convocatoriaEntidadConvocante)).will((InvocationOnMock invocation) -> {
      ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteCreado = invocation.getArgument(0);
      convocatoriaEntidadConvocanteCreado.setId(1L);
      return convocatoriaEntidadConvocanteCreado;
    });

    // when: Creamos el ConvocatoriaEntidadConvocante
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteCreado = service.create(convocatoriaEntidadConvocante);

    // then: El ConvocatoriaEntidadConvocante se crea correctamente
    Assertions.assertThat(convocatoriaEntidadConvocanteCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaEntidadConvocanteCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaEntidadConvocanteCreado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaEntidadConvocante.getConvocatoriaId());
    Assertions.assertThat(convocatoriaEntidadConvocanteCreado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(convocatoriaEntidadConvocante.getEntidadRef());
    Assertions.assertThat(convocatoriaEntidadConvocanteCreado.getPrograma().getId()).as("getPrograma().getId()")
        .isEqualTo(convocatoriaEntidadConvocante.getPrograma().getId());
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaEntidadConvocante que ya tiene id
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(1L,
        convocatoriaId);

    // when: Creamos el ConvocatoriaEntidadConvocante
    // then: Lanza una excepcion porque el ConvocatoriaEntidadConvocante ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaEntidadConvocante))
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "ConvocatoriaEntidadConvocante id tiene que ser null para crear un nuevo ConvocatoriaEntidadConvocante");
  }

  @Test
  void create_WithoutConvocatoriaId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadConvocante without ConvocatoriaId
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(null, null);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadGestora
        () -> service.create(convocatoriaEntidadConvocante))
        // then: throw exception as ConvocatoriaId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Convocatoria no puede ser null para crear ConvocatoriaEntidadGestora");
  }

  @Test
  void create_WithNoExistingConvocatoria_ThrowsConvocatoriaNotFoundException() {
    // given: a ConvocatoriaEntidadGestora with non existing Convocatoria
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(null,
        convocatoriaId);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadGestora
        () -> service.create(convocatoriaEntidadConvocante))
        // then: throw exception as Convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  void create_WithDuplicatedConvocatoriaIdAndEntidadRef_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadConvocante assigned with same Convocatoria And
    // EntidadRef
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaEntidadConvocante newConvocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(null,
        convocatoriaId);
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteExistente = generarMockConvocatoriaEntidadConvocante(1L,
        convocatoriaId);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito
        .given(repository.findByConvocatoriaIdAndEntidadRef(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.of(convocatoriaEntidadConvocanteExistente));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadConvocante
        () -> service.create(newConvocatoriaEntidadConvocante))
        // then: throw exception as assigned with same Convocatoria And EntidadRef
        // exists
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe una asociación activa para esa Convocatoria y Entidad");
  }

  @Test
  void create_WithNoExistingPrograma_404() {
    // given: a ConvocatoriaEntidadGestora with non existing Programa
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(null,
        convocatoriaId);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito
        .given(repository.findByConvocatoriaIdAndEntidadRef(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.empty());
    BDDMockito.given(programaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadGestora
        () -> service.create(convocatoriaEntidadConvocante))
        // then: throw exception as Programa is not found
        .isInstanceOf(ProgramaNotFoundException.class);
  }

  @Test
  void create_WithProgramaActivoFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadGestora with non existing Programa
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(null,
        convocatoriaId);
    convocatoriaEntidadConvocante.getPrograma().setActivo(false);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito
        .given(repository.findByConvocatoriaIdAndEntidadRef(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.empty());
    BDDMockito.given(programaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadConvocante.getPrograma()));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadGestora
        () -> service.create(convocatoriaEntidadConvocante))
        // then: throw exception as Programa is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El Programa debe estar Activo");
  }

  @Test
  void create_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadConvocante when modificable returns false
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaEntidadConvocante newConvocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(1L,
        convocatoriaId);
    newConvocatoriaEntidadConvocante.setId(null);
    convocatoria.setEstado(Convocatoria.Estado.REGISTRADA);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadConvocante
        () -> service.create(newConvocatoriaEntidadConvocante))
        // then: throw exception as modificable return false
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede crear ConvocatoriaEntidadConvocante. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
  }

  @Test
  void update_ReturnsConvocatoriaEntidadConvocante() {
    // given: Un nuevo ConvocatoriaEntidadConvocante con el nombre actualizado
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(1L,
        convocatoriaId);
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteProgramaActualizado = generarMockConvocatoriaEntidadConvocante(
        1L, convocatoriaId);
    convocatoriaEntidadConvocanteProgramaActualizado.setPrograma(new Programa(2L, null, null, null, null));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoriaEntidadConvocante));

    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito
        .given(repository.findByConvocatoriaIdAndEntidadRef(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.empty());

    BDDMockito.given(programaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadConvocante.getPrograma()));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaEntidadConvocante>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ConvocatoriaEntidadConvocante
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteActualizado = service
        .update(convocatoriaEntidadConvocanteProgramaActualizado);

    // then: El ConvocatoriaEntidadConvocante se actualiza correctamente.
    Assertions.assertThat(convocatoriaEntidadConvocanteActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaEntidadConvocanteActualizado.getId()).as("getId()")
        .isEqualTo(convocatoriaEntidadConvocante.getId());
    Assertions.assertThat(convocatoriaEntidadConvocanteActualizado.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(convocatoriaEntidadConvocante.getConvocatoriaId());
    Assertions.assertThat(convocatoriaEntidadConvocanteActualizado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(convocatoriaEntidadConvocante.getEntidadRef());
    Assertions.assertThat(convocatoriaEntidadConvocanteProgramaActualizado.getPrograma().getId())
        .as("getPrograma().getId()").isEqualTo(convocatoriaEntidadConvocante.getPrograma().getId());
  }

  @Test
  void update_WithIdNotExist_ThrowsConvocatoriaEntidadConvocanteNotFoundException() {
    // given: Un ConvocatoriaEntidadConvocante a actualizar con un id que no existe
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(1L,
        convocatoriaId);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el ConvocatoriaEntidadConvocante
    // then: Lanza una excepcion porque el ConvocatoriaEntidadConvocante no existe
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaEntidadConvocante))
        .isInstanceOf(ConvocatoriaEntidadConvocanteNotFoundException.class);
  }

  @Test
  void update_WithDuplicatedConvocatoriaIdAndEntidadRef_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadConvocante assigned with same Convocatoria And
    // EntidadRef
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(1L,
        convocatoriaId);
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteExistente = generarMockConvocatoriaEntidadConvocante(2L,
        convocatoriaId);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoriaEntidadConvocante));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito
        .given(repository.findByConvocatoriaIdAndEntidadRef(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.of(convocatoriaEntidadConvocanteExistente));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadConvocante
        () -> service.update(convocatoriaEntidadConvocante))
        // then: throw exception as assigned with same Convocatoria And EntidadRef
        // exists
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe una asociación activa para esa Convocatoria y Entidad");
  }

  @Test
  void update_WithNoExistingPrograma_404() {
    // given: a ConvocatoriaEntidadConvocante with non existing Programa
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(1L,
        convocatoriaId);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoriaEntidadConvocante));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito
        .given(repository.findByConvocatoriaIdAndEntidadRef(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.empty());
    BDDMockito.given(programaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadConvocante
        () -> service.update(convocatoriaEntidadConvocante))
        // then: throw exception as Programa is not found
        .isInstanceOf(ProgramaNotFoundException.class);
  }

  @Test
  void update_WithProgramaActivoFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadConvocante with non existing Programa
    Long convocatoriaId = 1L;
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(1L,
        convocatoriaId);

    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteActualizado = generarMockConvocatoriaEntidadConvocante(
        1L, convocatoriaId);
    convocatoriaEntidadConvocanteActualizado.getPrograma().setId(2L);
    convocatoriaEntidadConvocanteActualizado.getPrograma().setActivo(false);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoriaEntidadConvocante));

    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);

    BDDMockito
        .given(repository.findByConvocatoriaIdAndEntidadRef(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
        .willReturn(Optional.empty());

    BDDMockito.given(programaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadConvocanteActualizado.getPrograma()));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadConvocante
        () -> service.update(convocatoriaEntidadConvocanteActualizado))
        // then: throw exception as Programa is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El Programa debe estar Activo");
  }

  @Test
  void update_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadConvocante when modificable returns false
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = generarMockConvocatoriaEntidadConvocante(1L,
        convocatoriaId);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaEntidadConvocante));

    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadConvocante
        () -> service.update(convocatoriaEntidadConvocante))
        // then: throw exception as Convocatoria is registrada and has Solicitudes or
        // Proyectos
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede modificar ConvocatoriaEntidadConvocante. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
  }

  @Test
  void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing ConvocatoriaEntidadConvocante
    Long id = 1L;
    Long convocatoriaId = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoriaEntidadConvocante(id, convocatoriaId)));
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
        .isInstanceOf(ConvocatoriaEntidadConvocanteNotFoundException.class);
  }

  @Test
  void delete_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: existing ConvocatoriaEntidadConvocante when modificable returns false
    Long id = 1L;
    Long convocatoriaId = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockConvocatoriaEntidadConvocante(id, convocatoriaId)));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: throw exception as modificable returns false
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede eliminar ConvocatoriaEntidadConvocante. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void findAllByConvocatoria_ReturnsPage() {
    // given: Una lista con 37 ConvocatoriaEntidadGestora para la Convocatoria
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    List<ConvocatoriaEntidadConvocante> convocatoriasEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesConvocantes.add(generarMockConvocatoriaEntidadConvocante(i, convocatoriaId));
    }
    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaEntidadConvocante>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > convocatoriasEntidadesConvocantes.size() ? convocatoriasEntidadesConvocantes.size()
              : toIndex;
          List<ConvocatoriaEntidadConvocante> content = convocatoriasEntidadesConvocantes.subList(fromIndex, toIndex);
          Page<ConvocatoriaEntidadConvocante> pageResponse = new PageImpl<>(content, pageable,
              convocatoriasEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConvocatoriaEntidadConvocante> page = service.findAllByConvocatoria(convocatoriaId, null, paging);

    // then: Devuelve la pagina 3 con los ConvocatoriaEntidadConvocante del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(convocatoriaEntidadConvocante.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(convocatoriaEntidadConvocante.getEntidadRef()).isEqualTo("entidad-" + i);
    }
  }

  @Test
  void findById_ReturnsConvocatoriaEntidadConvocante() {
    // given: Un ConvocatoriaEntidadConvocante con el id buscado
    Long idBuscado = 1L;
    Long convocatoriaId = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockConvocatoriaEntidadConvocante(idBuscado, convocatoriaId)));

    // when: Buscamos el ConvocatoriaEntidadConvocante por su id
    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = service.findById(idBuscado);

    // then: el ConvocatoriaEntidadConvocante
    Assertions.assertThat(convocatoriaEntidadConvocante).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaEntidadConvocante.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  void findById_WithIdNotExist_ThrowsConvocatoriaEntidadConvocanteNotFoundException() throws Exception {
    // given: Ningun ConvocatoriaEntidadConvocante con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ConvocatoriaEntidadConvocante por su id
    // then: lanza un ConvocatoriaEntidadConvocanteNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ConvocatoriaEntidadConvocanteNotFoundException.class);
  }

  private Convocatoria generarMockConvocatoria(Long convocatoriaId) {
    return Convocatoria.builder()
        .id(convocatoriaId)
        .activo(true)
        .formularioSolicitud(FormularioSolicitud.PROYECTO)
        .build();
  }

  /**
   * Función que devuelve un objeto ConvocatoriaEntidadConvocante
   * 
   * @param id id del ConvocatoriaEntidadConvocante
   * @return el objeto ConvocatoriaEntidadConvocante
   */
  private ConvocatoriaEntidadConvocante generarMockConvocatoriaEntidadConvocante(Long id, Long convocatoriaId) {
    Programa programa = new Programa();
    programa.setId(id == null ? 1 : id);
    programa.setActivo(true);

    ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante = new ConvocatoriaEntidadConvocante();
    convocatoriaEntidadConvocante.setId(id);
    convocatoriaEntidadConvocante.setConvocatoriaId(convocatoriaId);
    convocatoriaEntidadConvocante.setEntidadRef("entidad-" + (id == null ? 1 : id));
    convocatoriaEntidadConvocante.setPrograma(programa);

    return convocatoriaEntidadConvocante;
  }

}
