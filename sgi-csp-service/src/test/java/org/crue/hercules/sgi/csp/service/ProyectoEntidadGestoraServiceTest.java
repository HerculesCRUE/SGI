package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoEntidadGestoraNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.crue.hercules.sgi.csp.repository.ProyectoEntidadGestoraRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoEntidadGestoraServiceImpl;
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
 * ProyectoEntidadGestoraServiceTest
 */

public class ProyectoEntidadGestoraServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoEntidadGestoraRepository repository;

  @Mock
  private ProyectoRepository proyectoRepository;

  private ProyectoEntidadGestoraService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ProyectoEntidadGestoraServiceImpl(repository, proyectoRepository);
  }

  @Test
  public void create_ReturnsProyectoEntidadGestora() {
    // given: Un nuevo ProyectoEntidadGestora
    ProyectoEntidadGestora proyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);
    proyectoEntidadGestora.setId(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(repository.existsProyectoEntidadGestoraByProyectoIdAndEntidadRef(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any())).willReturn(Boolean.FALSE);

    BDDMockito.given(repository.save(proyectoEntidadGestora)).will((InvocationOnMock invocation) -> {
      ProyectoEntidadGestora proyectoEntidadGestoraCreado = invocation.getArgument(0);
      proyectoEntidadGestoraCreado.setId(1L);
      return proyectoEntidadGestoraCreado;
    });

    // when: Creamos el ProyectoEntidadGestora
    ProyectoEntidadGestora proyectoEntidadGestoraCreado = service.create(proyectoEntidadGestora);

    // then: El ProyectoEntidadGestora se crea correctamente
    Assertions.assertThat(proyectoEntidadGestoraCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoEntidadGestoraCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoEntidadGestoraCreado.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoEntidadGestora.getProyectoId());
    Assertions.assertThat(proyectoEntidadGestoraCreado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(proyectoEntidadGestora.getEntidadRef());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ProyectoEntidadGestora que ya tiene id
    ProyectoEntidadGestora proyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);
    // when: Creamos el ProyectoEntidadGestora
    // then: Lanza una excepcion porque el ProyectoEntidadGestora ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(proyectoEntidadGestora))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ProyectoEntidadGestora id tiene que ser null para crear un nuevo ProyectoEntidadGestora");
  }

  @Test
  public void create_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoEntidadGestora without ProyectoId
    ProyectoEntidadGestora proyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);
    proyectoEntidadGestora.setId(null);
    proyectoEntidadGestora.setProyectoId(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoEntidadGestora
        () -> service.create(proyectoEntidadGestora))
        // then: throw exception as ProyectoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Proyecto no puede ser null para realizar la acción sobre ProyectoEntidadGestora");
  }

  @Test
  public void create_WithoutEntidadRef_ThrowsIllegalArgumentException() {
    // given: a ProyectoEntidadGestora without EntidadRef
    ProyectoEntidadGestora proyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);
    proyectoEntidadGestora.setId(null);
    proyectoEntidadGestora.setEntidadRef(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoEntidadGestora
        () -> service.create(proyectoEntidadGestora))
        // then: throw exception as Nombre is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("EntidadRef no puede ser null para realizar la acción sobre ProyectoEntidadGestora");
  }

  @Test
  public void create_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoEntidadGestora with non existing Proyecto
    ProyectoEntidadGestora proyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);
    proyectoEntidadGestora.setId(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: create ProyectoEntidadGestora
        () -> service.create(proyectoEntidadGestora))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  public void create_WithDuplicatedEntidadRef_ThrowsIllegalArgumentException() throws Exception {
    // given: ProyectoEntidadGestora with existing EntidadRef
    ProyectoEntidadGestora proyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);
    proyectoEntidadGestora.setId(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(repository.existsProyectoEntidadGestoraByProyectoIdAndEntidadRef(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: create ProyectoEntidadGestora
        () -> service.create(proyectoEntidadGestora))
        // then: IllegalArgumentException is thrown
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe una asociación activa para ese Proyecto y Entidad");
  }

  @Test
  public void update_ReturnsProyectoEntidadGestora() {
    // given: Un nuevo ProyectoEntidadGestora con el tipoEntidadGestora actualizado
    ProyectoEntidadGestora proyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);
    ProyectoEntidadGestora proyectoEntidadGestoraActualizado = generarMockProyectoEntidadGestora(1L, 1L);
    proyectoEntidadGestoraActualizado.setEntidadRef("entidad-modificada");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoEntidadGestora));

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito
        .given(repository.existsProyectoEntidadGestoraByIdNotAndProyectoIdAndEntidadRef(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.FALSE);

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoEntidadGestora>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ProyectoEntidadGestora
    ProyectoEntidadGestora updated = service.update(proyectoEntidadGestoraActualizado);

    // then: El ProyectoEntidadGestora se actualiza correctamente.
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(proyectoEntidadGestora.getId());
    Assertions.assertThat(updated.getProyectoId()).as("getProyectoId()")
        .isEqualTo(proyectoEntidadGestora.getProyectoId());
    Assertions.assertThat(updated.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(proyectoEntidadGestora.getEntidadRef());
  }

  @Test
  public void update_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoEntidadGestora without ProyectoId
    ProyectoEntidadGestora proyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);
    proyectoEntidadGestora.setEntidadRef("entidad-modificada");
    proyectoEntidadGestora.setProyectoId(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoEntidadGestora
        () -> service.update(proyectoEntidadGestora))
        // then: throw exception as ProyectoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Proyecto no puede ser null para realizar la acción sobre ProyectoEntidadGestora");
  }

  @Test
  public void update_WithoutEntidadRef_ThrowsIllegalArgumentException() {
    // given: a ProyectoEntidadGestora without EntidadRef
    ProyectoEntidadGestora proyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);
    proyectoEntidadGestora.setEntidadRef(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoEntidadGestora
        () -> service.update(proyectoEntidadGestora))
        // then: throw exception as EntidadRef is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("EntidadRef no puede ser null para realizar la acción sobre ProyectoEntidadGestora");
  }

  @Test
  public void update_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoEntidadGestora with non existing Proyecto
    ProyectoEntidadGestora proyectoEntidadGestoraOriginal = generarMockProyectoEntidadGestora(1L, 1L);
    ProyectoEntidadGestora proyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);
    proyectoEntidadGestora.setEntidadRef("entidad-modificada");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoEntidadGestoraOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: update ProyectoEntidadGestora
        () -> service.update(proyectoEntidadGestora))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  public void update_WithDuplicatedEntidadRef_ThrowsIllegalArgumentException() {
    // given: a ProyectoEntidadGestora with duplicated entidadRef
    ProyectoEntidadGestora proyectoEntidadGestoraOriginal = generarMockProyectoEntidadGestora(1L, 1L);
    ProyectoEntidadGestora proyectoEntidadGestora = generarMockProyectoEntidadGestora(1L, 1L);
    proyectoEntidadGestora.setEntidadRef("entidad-modificada");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoEntidadGestoraOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito
        .given(repository.existsProyectoEntidadGestoraByIdNotAndProyectoIdAndEntidadRef(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: update ProyectoEntidadGestora
        () -> service.update(proyectoEntidadGestora))
        // then: throw exception as nombre is duplicated
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe una asociación activa para ese Proyecto y Entidad");
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing ProyectoEntidadGestora
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.<Long>any());

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

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoEntidadGestoraNotFoundException.class);
  }

  @Test
  public void findAllByProyecto_ReturnsPage() {
    // given: Una lista con 37 ProyectoEntidadGestora para la Proyecto
    Long proyectoId = 1L;
    List<ProyectoEntidadGestora> proyectosEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectosEntidadesConvocantes.add(generarMockProyectoEntidadGestora(Long.valueOf(i), proyectoId));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoEntidadGestora>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectosEntidadesConvocantes.size() ? proyectosEntidadesConvocantes.size() : toIndex;
          List<ProyectoEntidadGestora> content = proyectosEntidadesConvocantes.subList(fromIndex, toIndex);
          Page<ProyectoEntidadGestora> pageResponse = new PageImpl<>(content, pageable,
              proyectosEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoEntidadGestora> page = service.findAllByProyecto(proyectoId, null, paging);

    // then: Devuelve la pagina 3 con los ProyectoEntidadGestora del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoEntidadGestora proyectoEntidadGestora = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyectoEntidadGestora.getId()).isEqualTo(Long.valueOf(i));
    }
  }

  /**
   * Función que devuelve un objeto ProyectoEntidadGestora
   * 
   * @param id         id del ProyectoEntidadGestora
   * @param proyectoId id del Proyecto
   * @return el objeto ProyectoEntidadGestora
   */
  private ProyectoEntidadGestora generarMockProyectoEntidadGestora(Long id, Long proyectoId) {

    // @formatter:off
    return ProyectoEntidadGestora.builder()
        .id(id)
        .proyectoId(proyectoId)
        .entidadRef("entidad-" + (id == null ? "" : String.format("%03d", id)))
        .build();
    // @formatter:on
  }
}
