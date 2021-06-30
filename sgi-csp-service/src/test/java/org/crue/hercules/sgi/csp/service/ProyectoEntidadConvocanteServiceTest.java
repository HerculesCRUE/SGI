package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoEntidadConvocanteRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoEntidadConvocanteServiceImpl;
import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;

public class ProyectoEntidadConvocanteServiceTest extends BaseServiceTest {
  @Mock
  private ProyectoEntidadConvocanteRepository repository;
  @Mock
  private ProyectoRepository proyectoRepository;
  @Mock
  private ProgramaRepository programaRepository;

  private ProyectoEntidadConvocanteService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ProyectoEntidadConvocanteServiceImpl(repository, proyectoRepository, programaRepository);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-E" })
  public void create_ReturnsProyectoEntidadConvocante() {
    // given: new ProyectoEntidadConvocante
    Long proyectoId = 1L;
    ProyectoEntidadConvocante proyectoEntidadConvocante = ProyectoEntidadConvocante.builder().proyectoId(proyectoId)
        .entidadRef("Entidad").build();

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(Proyecto.builder().id(proyectoId).activo(Boolean.TRUE).unidadGestionRef("2")
            .estado(EstadoProyecto.builder().estado(EstadoProyecto.Estado.BORRADOR).build()).build()));

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoEntidadConvocante>any()))
        .willAnswer(new Answer<ProyectoEntidadConvocante>() {
          @Override
          public ProyectoEntidadConvocante answer(InvocationOnMock invocation) throws Throwable {
            ProyectoEntidadConvocante givenProyectoEntidadConvocante = invocation.getArgument(0,
                ProyectoEntidadConvocante.class);
            ProyectoEntidadConvocante newProyectoEntidadConvocante = new ProyectoEntidadConvocante();
            BeanUtils.copyProperties(givenProyectoEntidadConvocante, newProyectoEntidadConvocante);
            newProyectoEntidadConvocante.setId(1L);
            return newProyectoEntidadConvocante;
          }
        });

    // when: create ProyectoEntidadConvocante
    ProyectoEntidadConvocante created = service.create(proyectoEntidadConvocante);

    // then: new demo is created
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isNotNull();
    Assertions.assertThat(created.getProyectoId()).isEqualTo(proyectoEntidadConvocante.getProyectoId());
    Assertions.assertThat(created.getEntidadRef()).isEqualTo(proyectoEntidadConvocante.getEntidadRef());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-E" })
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a ProyectoEntidadConvocante with id filled
    ProyectoEntidadConvocante proyectoEntidadConvocante = ProyectoEntidadConvocante.builder().id(1L).proyectoId(1L)
        .entidadRef("Entidad").build();

    Assertions.assertThatThrownBy(
        // when: create ProyectoEntidadConvocante
        () -> service.create(proyectoEntidadConvocante))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-E" })
  public void setPrograma_WithExistingId_And_ProgramActive_ReturnsProyectoEntidadConvocante() {
    // given: existing ProyectoEntidadConvocante and active Programa
    Long proyectoId = 1L;
    Long proyectoEntidadConvocanteId = 1L;
    Programa programa = Programa.builder().id(1L).id(1L).nombre("p1").activo(Boolean.TRUE).build();

    BDDMockito.given(programaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(programa));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(ProyectoEntidadConvocante
        .builder().id(proyectoEntidadConvocanteId).proyectoId(proyectoId).entidadRef("Entidad").build()));
    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(Proyecto.builder().id(proyectoId).activo(Boolean.TRUE).unidadGestionRef("2")
            .estado(EstadoProyecto.builder().estado(EstadoProyecto.Estado.BORRADOR).build()).build()));

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoEntidadConvocante>any()))
        .willAnswer(new Answer<ProyectoEntidadConvocante>() {
          @Override
          public ProyectoEntidadConvocante answer(InvocationOnMock invocation) throws Throwable {
            ProyectoEntidadConvocante givenProyectoEntidadConvocante = invocation.getArgument(0,
                ProyectoEntidadConvocante.class);
            return givenProyectoEntidadConvocante;
          }
        });

    // when: update ProyectoEntidadConvocante
    ProyectoEntidadConvocante updated = service.setPrograma(proyectoEntidadConvocanteId, programa);

    // then: ProyectoEntidadConvocante Programa is updated
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(proyectoEntidadConvocanteId);
    Assertions.assertThat(updated.getPrograma()).isNotNull();
    Programa returnProgrma = updated.getPrograma();
    Assertions.assertThat(returnProgrma.getId()).isEqualTo(programa.getId());
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-E" })
  public void setPrograma_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: a ProyectoEntidadConvocante with non existing id
    Long proyectoEntidadConvocanteId = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing ProyectoEntidadConvocante
        () -> service.setPrograma(proyectoEntidadConvocanteId, null))
        // then: NotFoundException is thrown
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-E" })
  public void setPrograma_WithoutId_ThrowsIllegalArgumentException() {
    // given: no ProyectoEntidadConvocante id
    Long proyectoEntidadConvocanteId = null;

    Assertions.assertThatThrownBy(
        // when: update ProyectoEntidadConvocante
        () -> service.setPrograma(proyectoEntidadConvocanteId, null))
        // then: throw exception as id must be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-E" })
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: no id
    Assertions.assertThatThrownBy(
        // when: delete by no id
        () -> service.delete(null))
        // then: throw exception as id must be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-E" })
  public void delete_NonExistingId_ThrowsNotFoundException() {
    // given: a non existing id
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete by non existing id
        () -> service.delete(1L))
        // then: throw NotFoundException as id must exist
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-E" })
  public void delete_WithExistingId_DeletesProyectoEntidadConvocante() {
    // given: existing id
    Long proyectoEntidadConvocanteId = 1L;
    Long proyectoId = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(ProyectoEntidadConvocante
        .builder().id(proyectoEntidadConvocanteId).proyectoId(proyectoId).entidadRef("Entidad").build()));
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.anyLong());
    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(Proyecto.builder().id(proyectoId).activo(Boolean.TRUE).unidadGestionRef("2")
            .estado(EstadoProyecto.builder().estado(EstadoProyecto.Estado.BORRADOR).build()).build()));

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(1L))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-E" })
  public void findAllByProyecto_Unlimited_ReturnsFullProyectoEntidadConvocanteList() {
    // given: One hundred ProyectoEntidadConvocante
    Long proyectoId = 1L;
    List<ProyectoEntidadConvocante> proyectoEntidadConvocantes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      ProyectoEntidadConvocante proyectoEntidadConvocante = ProyectoEntidadConvocante.builder().id(Long.valueOf(i))
          .proyectoId(proyectoId).entidadRef("Entidad" + String.format("%03d", i)).build();
      proyectoEntidadConvocantes.add(proyectoEntidadConvocante);
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoEntidadConvocante>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(proyectoEntidadConvocantes));
    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(Proyecto.builder().id(proyectoId).activo(Boolean.TRUE).unidadGestionRef("2")
            .estado(EstadoProyecto.builder().estado(EstadoProyecto.Estado.BORRADOR).build()).build()));

    // when: find unlimited
    Page<ProyectoEntidadConvocante> page = service.findAllByProyecto(1L, null, Pageable.unpaged());

    // then: Get a page with one hundred Demos
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  @WithMockUser(authorities = { "CSP-PRO-E" })
  public void findAllByProyecto_WithPaging_ReturnsPage() {
    // given: One hundred ProyectoEntidadConvocante for Proyecto with id = 1
    Long proyectoId = 1L;
    List<ProyectoEntidadConvocante> proyectoEntidadConvocantes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      ProyectoEntidadConvocante proyectoEntidadConvocante = ProyectoEntidadConvocante.builder().id(Long.valueOf(i))
          .proyectoId(proyectoId).entidadRef("Entidad" + String.format("%03d", i)).build();
      proyectoEntidadConvocantes.add(proyectoEntidadConvocante);
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoEntidadConvocante>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ProyectoEntidadConvocante>>() {
          @Override
          public Page<ProyectoEntidadConvocante> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<ProyectoEntidadConvocante> content = proyectoEntidadConvocantes.subList(fromIndex, toIndex);
            Page<ProyectoEntidadConvocante> page = new PageImpl<>(content, pageable, proyectoEntidadConvocantes.size());
            return page;
          }
        });
    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(Proyecto.builder().id(proyectoId).activo(Boolean.TRUE).unidadGestionRef("2")
            .estado(EstadoProyecto.builder().estado(EstadoProyecto.Estado.BORRADOR).build()).build()));

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoEntidadConvocante> page = service.findAllByProyecto(1L, null, paging);

    // then: A Page with ten ProyectoEntidadConvocante are returned containing
    // EntidadRef='Entidad031' to 'Entidad040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      ProyectoEntidadConvocante proyectoEntidadConvocante = page.getContent().get(i);
      Assertions.assertThat(proyectoEntidadConvocante.getEntidadRef()).isEqualTo("Entidad" + String.format("%03d", j));
    }
  }
}
