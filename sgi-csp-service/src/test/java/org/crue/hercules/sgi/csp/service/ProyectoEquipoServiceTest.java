package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoResponsableEconomicoRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoEquipoServiceImpl;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;

public class ProyectoEquipoServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoEquipoRepository repository;
  @Mock
  private ProyectoRepository proyectoRepository;
  @Mock
  private ProyectoResponsableEconomicoRepository proyectoResponsableEconomicoRepository;
  @Mock
  private ProyectoHelper proyectoHelper;

  private ProyectoEquipoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ProyectoEquipoServiceImpl(repository, proyectoRepository,
        proyectoHelper);
  }

  @Test
  public void update_ReturnsProyectoEquipoList() {
    // given: una lista con uno de los ProyectoEquipo
    // actualizado, otro nuevo y sin el otros existente
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);

    List<ProyectoEquipo> proyectoEquipoExistentes = new ArrayList<>();
    proyectoEquipoExistentes.add(generarMockProyectoEquipo(3L, Instant.parse("2020-02-15T00:00:00Z"),
        Instant.parse("2020-03-15T23:59:59Z"), proyectoId));
    proyectoEquipoExistentes.add(generarMockProyectoEquipo(4L, Instant.parse("2020-03-16T00:00:00Z"),
        Instant.parse("2020-04-15T23:59:59Z"), proyectoId));
    proyectoEquipoExistentes.add(generarMockProyectoEquipo(5L, Instant.parse("2020-04-16T00:00:00Z"),
        Instant.parse("2020-05-15T23:59:59Z"), proyectoId));

    ProyectoEquipo newProyectoEquipo = generarMockProyectoEquipo(null, Instant.parse("2020-06-16T00:00:00Z"),
        Instant.parse("2020-07-15T23:59:59Z"), proyectoId);
    ProyectoEquipo updatedProyectoEquipo = generarMockProyectoEquipo(4L, Instant.parse("2020-03-16T00:00:00Z"),
        Instant.parse("2020-04-14T23:59:59Z"), proyectoId);

    List<ProyectoEquipo> proyectoEquipoActualizar = new ArrayList<>();
    proyectoEquipoActualizar.add(newProyectoEquipo);
    proyectoEquipoActualizar.add(updatedProyectoEquipo);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));

    BDDMockito.given(repository.findAllByProyectoId(ArgumentMatchers.anyLong())).willReturn(proyectoEquipoExistentes);

    BDDMockito.doNothing().when(repository).deleteAll(ArgumentMatchers.<ProyectoEquipo>anyList());

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ProyectoEquipo>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<ProyectoEquipo> proyectoEquipos = invocation.getArgument(0);
          return proyectoEquipos.stream().map(proyectoEquipo -> {
            if (proyectoEquipo.getId() == null) {
              proyectoEquipo.setId(6L);
            }
            proyectoEquipo.setProyectoId(proyectoId);
            return proyectoEquipo;
          }).collect(Collectors.toList());
        });

    // when: updateProyectoEquipos
    List<ProyectoEquipo> proyectoEquipoActualizados = service.update(proyectoId, proyectoEquipoActualizar);

    // then: Se crea el nuevo ProyectoEquipo, se actualiza
    // el existe y se elimina el otro
    Assertions.assertThat(proyectoEquipoActualizados.get(0).getId()).as("get(0).getId()")
        .isEqualTo(updatedProyectoEquipo.getId());
    Assertions.assertThat(proyectoEquipoActualizados.get(0).getProyectoId()).as("get(0).getProyectoId()")
        .isEqualTo(proyectoId);
    Assertions.assertThat(proyectoEquipoActualizados.get(0).getFechaInicio()).as("get(0).getFechaInicio()")
        .isEqualTo(updatedProyectoEquipo.getFechaInicio());
    Assertions.assertThat(proyectoEquipoActualizados.get(0).getFechaFin()).as("get(0).getFechaFin()")
        .isEqualTo(updatedProyectoEquipo.getFechaFin());
    Assertions.assertThat(proyectoEquipoActualizados.get(0).getHorasDedicacion()).as("get(0).getHorasDedicacion()")
        .isEqualTo(updatedProyectoEquipo.getHorasDedicacion());
    Assertions.assertThat(proyectoEquipoActualizados.get(0).getPersonaRef()).as("get(0).getPersonaRef()")
        .isEqualTo(updatedProyectoEquipo.getPersonaRef());
    Assertions.assertThat(proyectoEquipoActualizados.get(0).getRolProyecto().getId())
        .as("get(0).getRolProyecto().getId()").isEqualTo(updatedProyectoEquipo.getRolProyecto().getId());

    Assertions.assertThat(proyectoEquipoActualizados.get(1).getId()).as("get(1).getId()").isEqualTo(6L);
    Assertions.assertThat(proyectoEquipoActualizados.get(1).getProyectoId()).as("get(1).getProyectoId()")
        .isEqualTo(proyectoId);
    Assertions.assertThat(proyectoEquipoActualizados.get(1).getFechaInicio()).as("get(1).getFechaInicio()")
        .isEqualTo(newProyectoEquipo.getFechaInicio());
    Assertions.assertThat(proyectoEquipoActualizados.get(1).getFechaFin()).as("get(1).getFechaFin()")
        .isEqualTo(newProyectoEquipo.getFechaFin());
    Assertions.assertThat(proyectoEquipoActualizados.get(1).getHorasDedicacion()).as("get(1).getHorasDedicacion()")
        .isEqualTo(newProyectoEquipo.getHorasDedicacion());
    Assertions.assertThat(proyectoEquipoActualizados.get(1).getPersonaRef()).as("get(1).getPersonaRef()")
        .isEqualTo(newProyectoEquipo.getPersonaRef());
    Assertions.assertThat(proyectoEquipoActualizados.get(1).getRolProyecto().getId())
        .as("get(1).getRolProyecto().getId()").isEqualTo(newProyectoEquipo.getRolProyecto().getId());

    Mockito.verify(repository, Mockito.times(1)).deleteAll(ArgumentMatchers.<ProyectoEquipo>anyList());
    Mockito.verify(repository, Mockito.times(1)).saveAll(ArgumentMatchers.<ProyectoEquipo>anyList());
  }

  @Test
  public void update_WithNoExistingConvocatoria_ThrowsProyectoNotFoundException() {
    // given: a ProyectoEquipo with non existing Proyecto
    Long proyectoId = 1L;
    ProyectoEquipo proyectoEquipo = generarMockProyectoEquipo(3L, Instant.parse("2020-02-15T00:00:00Z"),
        Instant.parse("2020-05-15T23:59:59Z"), proyectoId);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ProyectoEquipo
        () -> service.update(proyectoId, Arrays.asList(proyectoEquipo)))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  public void update_WithIdNotExist_ThrowsProyectoEquipoNotFoundException() {
    // given: Un ProyectoEquipo a actualizar con un id que
    // no existe
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoEquipo proyectoEquipo = generarMockProyectoEquipo(3L, Instant.parse("2020-02-15T00:00:00Z"),
        Instant.parse("2020-05-15T23:59:59Z"), proyectoId);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));

    BDDMockito.given(repository.findAllByProyectoId(ArgumentMatchers.anyLong())).willReturn(new ArrayList<>());

    // when:updateProyectoEquiposConvocatoria
    // then: Lanza una excepcion porque el ProyectoEquipo
    // no existe
    Assertions.assertThatThrownBy(() -> service.update(proyectoId, Arrays.asList(proyectoEquipo)))
        .isInstanceOf(ProyectoEquipoNotFoundException.class);
  }

  @Test
  public void update_WithFechaInicioAfterFechaFin_ThrowsIllegalArgumentException() {
    // given: a ProyectoEquipo with fecha inicio after fecha fin
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoEquipo proyectoEquipo = generarMockProyectoEquipo(3L, Instant.parse("2020-05-15T00:00:00Z"),
        Instant.parse("2020-03-15T23:59:59Z"), proyectoId);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));

    BDDMockito.given(repository.findAllByProyectoId(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(proyectoEquipo));

    Assertions.assertThatThrownBy(
        // when: updateProyectoEquipos
        () -> service.update(proyectoId, Arrays.asList(proyectoEquipo)))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de inicio no puede ser superior a la fecha de fin");
  }

  @Test
  public void update_WithFechaFinGreaterThanDuracionProyecto_ThrowsIllegalArgumentException() {
    // given: a ProyectoEquipo with fechaFin greater than
    // duracion proyecto
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoEquipo proyectoEquipo = generarMockProyectoEquipo(3L, Instant.parse("2020-05-15T00:00:00Z"),
        Instant.parse("2022-03-15T00:00:00Z"), proyectoId);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));

    BDDMockito.given(repository.findAllByProyectoId(ArgumentMatchers.anyLong()))
        .willReturn(Arrays.asList(proyectoEquipo));

    Assertions.assertThatThrownBy(
        // when: updateProyectoEquipos
        () -> service.update(proyectoId, Arrays.asList(proyectoEquipo)))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Las fechas de proyecto equipo deben de estar dentro de la duración del proyecto");
  }

  @Test
  public void update_WithFechasSolapadas_ThrowsIllegalArgumentException() {
    // given: a ProyectoEquipo with fechas solapadas
    Long proyectoId = 1L;
    Proyecto proyecto = generarMockProyecto(proyectoId);
    ProyectoEquipo proyectoEquipo = generarMockProyectoEquipo(null, Instant.parse("2020-05-15T00:00:00Z"),
        Instant.parse("2021-03-15T23:59:59Z"), proyectoId);
    ProyectoEquipo proyectoEquipo2 = generarMockProyectoEquipo(null, Instant.parse("2020-05-15T00:00:00Z"),
        Instant.parse("2021-03-15T23:59:59Z"), proyectoId);

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyecto));

    BDDMockito.given(repository.findAllByProyectoId(ArgumentMatchers.anyLong())).willReturn(new ArrayList<>());

    Assertions.assertThatThrownBy(
        // when: updateProyectoEquiposConvocatoria
        () -> service.update(proyectoId, Arrays.asList(proyectoEquipo, proyectoEquipo2)))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El proyecto equipo se solapa con otro existente");
  }

  @Test
  public void findById_WithExistingId_ReturnsProyectoEquipo() throws Exception {
    // given: existing ProyectoEquipo
    ProyectoEquipo proyectoEquipo = generarMockProyectoEquipo(1L, Instant.parse("2020-05-15T00:00:00Z"),
        Instant.parse("2021-03-15T23:59:59Z"), 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(proyectoEquipo));

    // when: find by id ProyectoEquipo
    ProyectoEquipo data = service.findById(proyectoEquipo.getId());

    // then: returns ProyectoEquipo
    Assertions.assertThat(data).isNotNull();
    Assertions.assertThat(data.getId()).isNotNull();
    Assertions.assertThat(data.getId()).isEqualTo(proyectoEquipo.getId());
    Assertions.assertThat(data.getProyectoId()).isEqualTo(proyectoEquipo.getProyectoId());
    Assertions.assertThat(data.getRolProyecto().getId()).isEqualTo(proyectoEquipo.getRolProyecto().getId());
    Assertions.assertThat(data.getFechaInicio()).isEqualTo(proyectoEquipo.getFechaInicio());
    Assertions.assertThat(data.getFechaFin()).isEqualTo(proyectoEquipo.getFechaFin());
    Assertions.assertThat(data.getPersonaRef()).isEqualTo(proyectoEquipo.getPersonaRef());
    Assertions.assertThat(data.getHorasDedicacion()).isEqualTo(proyectoEquipo.getHorasDedicacion());

  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoEquipoNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  public void findAllByProyecto_WithPaging_ReturnsPage() {
    // given: One hundred ProyectoEquipo
    Long proyectoId = 1L;
    List<ProyectoEquipo> listaProyectoEquipo = new LinkedList<ProyectoEquipo>();
    for (int i = 1; i <= 100; i++) {
      listaProyectoEquipo.add(generarMockProyectoEquipo(Long.valueOf(i), Instant.parse("2020-05-15T00:00:00Z"),
          Instant.parse("2021-03-15T23:59:59Z"), 1L));
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoEquipo>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoEquipo>>() {
          @Override
          public Page<ProyectoEquipo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<ProyectoEquipo> content = listaProyectoEquipo.subList(fromIndex, toIndex);
            Page<ProyectoEquipo> page = new PageImpl<>(content, pageable, listaProyectoEquipo.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoEquipo> page = service.findAllByProyecto(proyectoId, null, paging);

    // then: A Page with ten ProyectoEquipo are returned
    // containing
    // obsrvaciones='observaciones-31' to
    // 'observaciones-40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 31; i < 10; i++) {
      ProyectoEquipo item = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(item.getId()).isEqualTo(Long.valueOf(i));
    }
  }

  private Proyecto generarMockProyecto(Long proyectoId) {
    // @formatter:off
    Proyecto proyecto = Proyecto.builder()
      .id(proyectoId)
      .titulo("proyecto 2")
      .acronimo("PR2")
      .fechaInicio(Instant.parse("2020-01-20T00:00:00Z"))
      .fechaFin(Instant.parse("2021-11-20T23:59:59Z"))
      .unidadGestionRef("2")
      .activo(Boolean.TRUE)
      .build();
    // @formatter:on
    return proyecto;
  }

  /**
   * Función que devuelve un objeto ProyectoEquipo
   * 
   * @param id          id del ProyectoEquipo
   * @param fechaInicio Fecha inicio
   * @param fechaFin    Fecha fin
   * @param proyectoId  Id Proyecto
   * @return el objeto ProyectoEquipo
   */
  private ProyectoEquipo generarMockProyectoEquipo(Long id, Instant fechaInicio, Instant fechaFin, Long proyectoId) {

    // @formatter:off
    RolProyecto rolProyecto = RolProyecto.builder().id(id)
        .abreviatura("001")
        .nombre("nombre-001")
        .descripcion("descripcion-001")
        .rolPrincipal(Boolean.FALSE)
        .equipo(RolProyecto.Equipo.INVESTIGACION).activo(Boolean.TRUE)
        .build();
    // @formatter:on

    ProyectoEquipo proyectoEquipo = ProyectoEquipo.builder().id(id).proyectoId(proyectoId).rolProyecto(rolProyecto)
        .fechaInicio(fechaInicio).fechaFin(fechaFin).personaRef("001").horasDedicacion(new Double(2)).build();

    return proyectoEquipo;
  }

}
