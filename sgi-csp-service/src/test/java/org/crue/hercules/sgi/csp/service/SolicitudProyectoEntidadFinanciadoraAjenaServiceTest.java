package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.repository.FuenteFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEntidadFinanciadoraAjenaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.TipoFinanciacionRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudProyectoEntidadFinanciadoraAjenaServiceImpl;
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

/**
 * SolicitudProyectoEntidadFinanciadoraAjenaServiceTest
 */
public class SolicitudProyectoEntidadFinanciadoraAjenaServiceTest extends BaseServiceTest {

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

  private SolicitudProyectoEntidadFinanciadoraAjenaService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new SolicitudProyectoEntidadFinanciadoraAjenaServiceImpl(repository, fuenteFinanciacionRepository,
        tipoFinanciacionRepository, solicitudService, solicitudProyectoRepository);
  }

  @Test
  public void create_ReturnsSolicitudProyectoEntidadFinanciadoraAjena() {
    // given: Un nuevo SolicitudProyectoEntidadFinanciadoraAjena
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        null);

    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjena.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjena.getTipoFinanciacion()));

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
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoEntidadFinanciadoraAjena que ya tiene id
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        1L);

    // when: Creamos el SolicitudProyectoEntidadFinanciadoraAjena
    // then: Lanza una excepcion porque el SolicitudProyectoEntidadFinanciadoraAjena
    // ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoEntidadFinanciadoraAjena))
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "SolicitudProyectoEntidadFinanciadoraAjena id tiene que ser null para crear un nuevo SolicitudProyectoEntidadFinanciadoraAjena");
  }

  @Test
  public void create_WithoutSolicitudProyectoId_ThrowsIllegalArgumentException() {
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
        .hasMessage("Id SolicitudProyecto no puede ser null para crear SolicitudProyectoEntidadFinanciadoraAjena");
  }

  @Test
  public void create_WithFuenteFinanciacionActivoFalse_ThrowsIllegalArgumentException() {
    // given: a SolicitudProyectoEntidadFinanciadoraAjena with FuenteFinanciacion
    // activo=false
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        null);
    solicitudProyectoEntidadFinanciadoraAjena.getFuenteFinanciacion().setActivo(false);

    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjena.getFuenteFinanciacion()));

    Assertions.assertThatThrownBy(
        // when: create SolicitudProyectoEntidadFinanciadoraAjena
        () -> service.create(solicitudProyectoEntidadFinanciadoraAjena))
        // then: throw exception as FuenteFinanciacion is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("La FuenteFinanciacion debe estar Activo");
  }

  @Test
  public void create_WithTipoFinanciacionActivoFalse_ThrowsIllegalArgumentException() {
    // given: a SolicitudProyectoEntidadFinanciadoraAjena with TipoFinanciacion
    // activo=false
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        null);
    solicitudProyectoEntidadFinanciadoraAjena.getTipoFinanciacion().setActivo(false);

    BDDMockito.given(fuenteFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjena.getFuenteFinanciacion()));
    BDDMockito.given(tipoFinanciacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoEntidadFinanciadoraAjena.getTipoFinanciacion()));

    Assertions.assertThatThrownBy(
        // when: create SolicitudProyectoEntidadFinanciadoraAjena
        () -> service.create(solicitudProyectoEntidadFinanciadoraAjena))
        // then: throw exception as TipoFinanciacion is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El TipoFinanciacion debe estar Activo");
  }

  @Test
  public void update_ReturnsSolicitudProyectoEntidadFinanciadoraAjena() {
    // given: Un nuevo SolicitudProyectoEntidadFinanciadoraAjena con el nombre
    // actualizado
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId);
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        1L);
    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjenaPorcentajeActualizado = generarMockSolicitudProyectoEntidadFinanciadoraAjena(
        1L);
    solicitudProyectoEntidadFinanciadoraAjenaPorcentajeActualizado.setPorcentajeFinanciacion(1);

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
  public void update_WithIdNotExist_ThrowsSolicitudProyectoEntidadFinanciadoraAjenaNotFoundException() {
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

    // when: Actualizamos el SolicitudProyectoEntidadFinanciadoraAjena
    // then: Lanza una excepcion porque el SolicitudProyectoEntidadFinanciadoraAjena
    // no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoEntidadFinanciadoraAjena))
        .isInstanceOf(SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException.class);
  }

  @Test
  public void update_WithFuenteFinanciacionActivoFalse_ThrowsIllegalArgumentException() {
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

    Assertions.assertThatThrownBy(
        // when: update SolicitudProyectoEntidadFinanciadoraAjena
        () -> service.update(solicitudProyectoEntidadFinanciadoraAjenaActualizada))
        // then: throw exception as FuenteFinanciacion is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("La FuenteFinanciacion debe estar Activo");
  }

  @Test
  public void update_WithTipoFinanciacionActivoFalse_ThrowsIllegalArgumentException() {
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

    Assertions.assertThatThrownBy(
        // when: update SolicitudProyectoEntidadFinanciadoraAjena
        () -> service.update(solicitudProyectoEntidadFinanciadoraAjenaActualizada))
        // then: throw exception as TipoFinanciacion is not activo
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El TipoFinanciacion debe estar Activo");
  }

  @Test
  public void delete_WithExistingId_ReturnsSolicitudProyectoEntidadFinanciadoraAjena() {
    // given: existing SolicitudProyectoEntidadFinanciadoraAjena
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(true);
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

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(false);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException.class);
  }

  @Test
  public void findAllBySolicitud_ReturnsPage() {
    // given: Una lista con 37 SolicitudProyectoEntidadFinanciadoraAjena para la
    // Solicitud
    Long solicitudId = 1L;
    List<SolicitudProyectoEntidadFinanciadoraAjena> solicitudProyectoEntidadesFinanciadoraAjenas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoEntidadesFinanciadoraAjenas.add(generarMockSolicitudProyectoEntidadFinanciadoraAjena(i));
    }

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
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
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
  public void findById_ReturnsSolicitudProyectoEntidadFinanciadoraAjena() {
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
  public void findById_WithIdNotExist_ThrowsSolicitudProyectoEntidadFinanciadoraAjenaNotFoundException()
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
    // @formatter:off
    FuenteFinanciacion fuenteFinanciacion = FuenteFinanciacion.builder()
        .id(id == null ? 1 : id)
        .activo(true)
        .build();

    TipoFinanciacion tipoFinanciacion = TipoFinanciacion.builder()
        .id(id == null ? 1 : id)
        .activo(true)
        .build();

    SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena = SolicitudProyectoEntidadFinanciadoraAjena
        .builder()
        .id(id)
        .solicitudProyectoId(id == null ? 1 : id)
        .entidadRef("entidad-" + (id == null ? 0 : String.format("%03d", id)))
        .fuenteFinanciacion(fuenteFinanciacion)
        .tipoFinanciacion(tipoFinanciacion)
        .porcentajeFinanciacion(50)
        .build();
    // @formatter:on

    return solicitudProyectoEntidadFinanciadoraAjena;
  }

}
