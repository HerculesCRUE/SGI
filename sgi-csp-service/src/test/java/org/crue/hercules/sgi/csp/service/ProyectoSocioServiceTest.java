package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoResponsableEconomicoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoPagoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionDocumentoRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoSocioServiceImpl;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoSocioServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoSocioRepository repository;
  @Mock
  private ProyectoSocioEquipoRepository equipoRepository;
  @Mock
  private ProyectoSocioPeriodoPagoRepository periodoPagoRepository;
  @Mock
  private ProyectoSocioPeriodoJustificacionDocumentoRepository documentoRepository;
  @Mock
  private ProyectoSocioPeriodoJustificacionRepository periodoJustificacionRepository;
  @Mock
  private ProyectoRepository proyectoRepository;
  @Mock
  private ProyectoEquipoRepository proyectoEquipoRepository;
  @Mock
  private ProyectoResponsableEconomicoRepository proyectoResponsableEconomicoRepository;

  private ProyectoHelper proyectoHelper;
  private ProyectoSocioService service;

  @BeforeEach
  public void setUp() throws Exception {
    proyectoHelper = new ProyectoHelper(proyectoEquipoRepository, proyectoResponsableEconomicoRepository);
    service = new ProyectoSocioServiceImpl(repository, equipoRepository, periodoPagoRepository, documentoRepository,
        periodoJustificacionRepository, proyectoRepository, this.proyectoHelper);
  }

  @Test
  public void create_ReturnsProyectoSocio() {
    // given: new ProyectoSocio
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(1L);
    proyectoSocio.setId(null);

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoSocio>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ProyectoSocio givenData = invocation.getArgument(0, ProyectoSocio.class);
          ProyectoSocio newData = new ProyectoSocio();
          BeanUtils.copyProperties(givenData, newData);
          newData.setId(1L);
          return newData;
        });

    // when: create ProyectoSocio
    ProyectoSocio responseData = service.create(proyectoSocio);

    // then: new ProyectoSocio is created
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyectoId()).as("getProyectoId()").isEqualTo(proyectoSocio.getProyectoId());
    Assertions.assertThat(responseData.getEmpresaRef()).as("getEmpresaRef()").isEqualTo(proyectoSocio.getEmpresaRef());
    Assertions.assertThat(responseData.getRolSocio()).as("getRolSocio()").isNotNull();
    Assertions.assertThat(responseData.getRolSocio().getId()).as("getRolSocio().getId()")
        .isEqualTo(proyectoSocio.getRolSocio().getId());
    Assertions.assertThat(responseData.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(proyectoSocio.getFechaInicio());
    Assertions.assertThat(responseData.getFechaFin()).as("getFechaFin()").isEqualTo(proyectoSocio.getFechaFin());
    Assertions.assertThat(responseData.getNumInvestigadores()).as("getNumInvestigadores()")
        .isEqualTo(proyectoSocio.getNumInvestigadores());
    Assertions.assertThat(responseData.getImporteConcedido()).as("getImporteConcedido()")
        .isEqualTo(proyectoSocio.getImporteConcedido());

  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocio with id filled
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(1L);

    Assertions.assertThatThrownBy(
        // when: create ProyectoSocio
        () -> service.create(proyectoSocio))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Id tiene que ser null para crear el ProyectoSocio");
  }

  @Test
  public void update_WithExistingId_ReturnsProyectoSocio() {
    // given: existing ProyectoSocio
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(1L);
    ProyectoSocio proyectoSocioActualizado = generarMockProyectoSocio(1L);
    proyectoSocioActualizado.setNumInvestigadores(10);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyectoSocio));

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoSocio>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          ProyectoSocio givenData = invocation.getArgument(0, ProyectoSocio.class);
          return givenData;
        });

    // when: update ProyectoSocio
    ProyectoSocio responseData = service.update(proyectoSocioActualizado);

    // then: ProyectoSocio is updated
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyectoId()).as("getProyectoId()").isEqualTo(proyectoSocio.getProyectoId());
    Assertions.assertThat(responseData.getEmpresaRef()).as("getEmpresaRef()").isEqualTo(proyectoSocio.getEmpresaRef());
    Assertions.assertThat(responseData.getRolSocio()).as("getRolSocio()").isNotNull();
    Assertions.assertThat(responseData.getRolSocio().getId()).as("getRolSocio().getId()")
        .isEqualTo(proyectoSocio.getRolSocio().getId());
    Assertions.assertThat(responseData.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(proyectoSocio.getFechaInicio());
    Assertions.assertThat(responseData.getFechaFin()).as("getFechaFin()").isEqualTo(proyectoSocio.getFechaFin());
    Assertions.assertThat(responseData.getNumInvestigadores()).as("getNumInvestigadores()")
        .isEqualTo(proyectoSocioActualizado.getNumInvestigadores());
    Assertions.assertThat(responseData.getImporteConcedido()).as("getImporteConcedido()")
        .isEqualTo(proyectoSocio.getImporteConcedido());

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocio without id filled
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(1L);
    proyectoSocio.setId(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoSocio
        () -> service.update(proyectoSocio))
        // then: throw exception as id must be provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Id no puede ser null para actualizar ProyectoSocio");
  }

  @Test
  public void update_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(1L);
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing ProyectoSocio
        () -> service.update(proyectoSocio))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoSocioNotFoundException.class);
  }

  @Test
  public void update_RolSocioNoCoordinador_WithProyectoAbiertoCoordinadorExterno_ThrowsIllegalArgumentException()
      throws Exception {
    // given: a ProyectoSocio with RolSocio with coordinador false
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoSocio proyectoSocioExistente = generarMockProyectoSocio(1L);
    proyecto.getEstado().setEstado(EstadoProyecto.Estado.CONCEDIDO);
    proyecto.setColaborativo(true);
    proyecto.setCoordinadorExterno(true);

    ProyectoSocio proyectoSocio = generarMockProyectoSocio(1L);
    proyectoSocio.setRolSocio(RolSocio.builder().id(2L).coordinador(false).build());

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyectoSocioExistente));
    BDDMockito.given(repository.count(ArgumentMatchers.<Specification<ProyectoSocio>>any())).willReturn(0L);

    Assertions.assertThatThrownBy(
        // when: update
        () -> service.update(proyectoSocio))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Debe existir al menos un socio con TipoRolSocio que tenga el campo coordinador a true");
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing ProyectoSocio
    Long id = 1L;

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockProyecto(id)));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockProyectoSocio(id)));
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: unique ProyectoSocio with RolSocio with coordinador true
    Long id = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: throw exception
        .isInstanceOf(ProyectoSocioNotFoundException.class);
  }

  @Test
  public void delete_LastRolSocioCoordinador_WithProyectoAbiertoCoordinadorExterno__ThrowsIllegalArgumentException()
      throws Exception {
    // given: no existing id
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    Long id = 1L;
    ProyectoSocio proyectoSocio = generarMockProyectoSocio(1L);
    proyecto.getEstado().setEstado(EstadoProyecto.Estado.CONCEDIDO);
    proyecto.setColaborativo(true);
    proyecto.setCoordinadorExterno(true);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyectoSocio));
    BDDMockito.given(repository.count(ArgumentMatchers.<Specification<ProyectoSocio>>any())).willReturn(0L);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Debe existir al menos un socio con TipoRolSocio que tenga el campo coordinador a true");
  }

  @Test
  public void existsById_WithExistingId_ReturnsTRUE() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: exists by id
    boolean responseData = service.existsById(id);

    // then: returns TRUE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isTrue();
  }

  @Test
  public void existsById_WithNoExistingId_ReturnsFALSE() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: exists by id
    boolean responseData = service.existsById(id);

    // then: returns TRUE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  public void findById_WithExistingId_ReturnsProyectoSocio() throws Exception {
    // given: existing ProyectoSocio
    ProyectoSocio proyectoSocioExistente = generarMockProyectoSocio(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyectoSocioExistente));

    // when: find by id ProyectoSocio
    ProyectoSocio responseData = service.findById(proyectoSocioExistente.getId());

    // then: returns ProyectoSocio
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoSocioExistente.getProyectoId());
    Assertions.assertThat(responseData.getEmpresaRef()).as("getEmpresaRef()")
        .isEqualTo(proyectoSocioExistente.getEmpresaRef());
    Assertions.assertThat(responseData.getRolSocio()).as("getRolSocio()").isNotNull();
    Assertions.assertThat(responseData.getRolSocio().getId()).as("getRolSocio().getId()")
        .isEqualTo(proyectoSocioExistente.getRolSocio().getId());
    Assertions.assertThat(responseData.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(proyectoSocioExistente.getFechaInicio());
    Assertions.assertThat(responseData.getFechaFin()).as("getFechaFin()")
        .isEqualTo(proyectoSocioExistente.getFechaFin());
    Assertions.assertThat(responseData.getNumInvestigadores()).as("getNumInvestigadores()")
        .isEqualTo(proyectoSocioExistente.getNumInvestigadores());
    Assertions.assertThat(responseData.getImporteConcedido()).as("getImporteConcedido()")
        .isEqualTo(proyectoSocioExistente.getImporteConcedido());

  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoSocioNotFoundException.class);
  }

  @Test
  public void findAllByProyecto_WithPaging_ReturnsPage() {
    // given: 37 ProyectoSocio
    Long proyectoId = 1L;
    List<ProyectoSocio> proyectoSocios = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoSocios.add(generarMockProyectoSocio(i));
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoSocio>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectoSocios.size() ? proyectoSocios.size() : toIndex;
          List<ProyectoSocio> content = proyectoSocios.subList(fromIndex, toIndex);
          Page<ProyectoSocio> pageResponse = new PageImpl<>(content, pageable, proyectoSocios.size());
          return pageResponse;
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoSocio> page = service.findAllByProyecto(proyectoId, null, paging);

    // then: A Page with ten ProyectoSocio are returned
    // containing id='31' to '37'
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoSocio item = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(item.getId()).isEqualTo(Long.valueOf(i));
    }

  }

  @Test
  public void existsProyectoSocioCoordinador_Exist_ReturnsTRUE() throws Exception {
    // given: existing proyecto socio coordinador
    Long proyectoId = 1L;
    BDDMockito.given(repository.count(ArgumentMatchers.<Specification<ProyectoSocio>>any())).willReturn(1L);

    // when: existsProyectoSocioCoordinador
    boolean responseData = service.existsProyectoSocioCoordinador(proyectoId);

    // then: returns TRUE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isTrue();
  }

  @Test
  public void existsProyectoSocioCoordinador_NoExist_ReturnsFALSE() throws Exception {
    // given: no existing id
    Long proyectoId = 1L;
    BDDMockito.given(repository.count(ArgumentMatchers.<Specification<ProyectoSocio>>any())).willReturn(0L);

    // when: exists by id
    boolean responseData = service.existsProyectoSocioCoordinador(proyectoId);

    // then: returns TRUE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  private Proyecto generarMockProyecto(Long proyectoId) {
    // @formatter:off
    return Proyecto.builder()
            .id(proyectoId)
            .estado(
                EstadoProyecto.builder()
                    .id(1L)
                    .estado(EstadoProyecto.Estado.BORRADOR)
                    .build())
            .build();
    // @formatter:on
  }

  /**
   * Función que genera un ProyectoSocio
   * 
   * @param proyectoSocioId Identificador del {@link ProyectoSocio}
   * @return el ProyectoSocio
   */
  private ProyectoSocio generarMockProyectoSocio(Long proyectoSocioId) {

    String suffix = String.format("%03d", proyectoSocioId);

    // @formatter:off
    ProyectoSocio proyectoSocio = ProyectoSocio.builder()
        .id(proyectoSocioId)
        .proyectoId(1L)
        .empresaRef("empresa-" + suffix)
        .rolSocio(RolSocio.builder().id(1L).coordinador(true).build())
        .fechaInicio(Instant.parse("2021-01-11T00:00:00Z"))
        .fechaFin(Instant.parse("2022-01-11T23:59:59Z"))
        .numInvestigadores(5)
        .importeConcedido(BigDecimal.valueOf(1000))
        .build();
    // @formatter:on

    return proyectoSocio;
  }

}
