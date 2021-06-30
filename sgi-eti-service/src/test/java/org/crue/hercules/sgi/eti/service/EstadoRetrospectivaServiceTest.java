package org.crue.hercules.sgi.eti.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.EstadoRetrospectivaNotFoundException;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.repository.EstadoRetrospectivaRepository;
import org.crue.hercules.sgi.eti.service.impl.EstadoRetrospectivaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * EstadoRetrospectivaServiceTest
 */
public class EstadoRetrospectivaServiceTest extends BaseServiceTest {

  @Mock
  private EstadoRetrospectivaRepository repository;

  private EstadoRetrospectivaService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new EstadoRetrospectivaServiceImpl(repository);
  }

  @Test
  public void create_ReturnsEstadoRetrospectiva() {

    // given: Nueva entidad sin Id
    EstadoRetrospectiva response = getMockData(1L);

    BDDMockito.given(repository.save(response)).willReturn(response);

    // when: Se crea la entidad
    EstadoRetrospectiva result = service.create(response);

    // then: La entidad se crea correctamente
    Assertions.assertThat(result).isEqualTo(response);
  }

  @Test
  public void update_WithExistingId_ReturnsEstadoRetrospectiva() {

    // given: Entidad existente que se va a actualizar
    EstadoRetrospectiva estadoRetrospectiva = getMockData(1L);
    EstadoRetrospectiva estadoRetrospectivaActualizada = getMockData(2L);
    estadoRetrospectivaActualizada.setId(estadoRetrospectiva.getId());

    BDDMockito.given(repository.findById(estadoRetrospectiva.getId())).willReturn(Optional.of(estadoRetrospectiva));
    BDDMockito.given(repository.save(estadoRetrospectivaActualizada)).willReturn(estadoRetrospectivaActualizada);

    // when: Se actualiza la entidad
    EstadoRetrospectiva result = service.update(estadoRetrospectivaActualizada);

    // then: Los datos se actualizan correctamente
    Assertions.assertThat(result).isEqualTo(estadoRetrospectivaActualizada);
  }

  @Test
  public void update_WithNoExistingId_ThrowsNotFoundException() {

    // given: Entidad a actualizar que no existe
    EstadoRetrospectiva estadoRetrospectivaActualizada = getMockData(1L);

    // when: Se actualiza la entidad
    // then: Se produce error porque no encuentra la entidad a actualizar
    Assertions.assertThatThrownBy(() -> service.update(estadoRetrospectivaActualizada))
        .isInstanceOf(EstadoRetrospectivaNotFoundException.class);
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Entidad a actualizar sin Id
    EstadoRetrospectiva estadoRetrospectivaActualizada = getMockData(1L);
    estadoRetrospectivaActualizada.setId(null);

    // when: Se actualiza la entidad
    // then: Se produce error porque no tiene Id
    Assertions.assertThatThrownBy(() -> service.update(estadoRetrospectivaActualizada))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesEstadoRetrospectiva() {

    // given: Entidad existente
    EstadoRetrospectiva response = getMockData(1L);

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(repository).deleteById(response.getId());

    // when: Se elimina la entidad
    service.delete(response.getId());

    // then: La entidad se elimina correctamente
    BDDMockito.given(repository.findById(response.getId())).willReturn(Optional.empty());
    Assertions.assertThatThrownBy(() -> service.findById(response.getId()))
        .isInstanceOf(EstadoRetrospectivaNotFoundException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsNotFoundException() {

    // given: Id de una entidad que no existe
    Long id = 1L;

    // when: Se elimina la entidad
    // then: Se produce error porque no encuentra la entidad a actualizar
    Assertions.assertThatThrownBy(() -> service.delete(id)).isInstanceOf(EstadoRetrospectivaNotFoundException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {

    // given: Id de una entidad vacía
    Long id = null;

    // when: Se elimina la entidad
    // then: Se produce error porque no encuentra la entidad a actualizar
    Assertions.assertThatThrownBy(() -> service.delete(id)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void find_WithExistingId_ReturnsEstadoRetrospectiva() {

    // given: Entidad con un determinado Id
    EstadoRetrospectiva response = getMockData(1L);
    BDDMockito.given(repository.findById(response.getId())).willReturn(Optional.of(response));

    // when: Se busca la entidad por ese Id
    EstadoRetrospectiva result = service.findById(response.getId());

    // then: Se recupera la entidad con el Id
    Assertions.assertThat(result).isEqualTo(response);
  }

  @Test
  public void find_WithNoExistingId_ThrowsNotFoundException() throws Exception {

    // given: No existe entidad con el id indicado
    Long id = 1L;
    BDDMockito.given(repository.findById(id)).willReturn(Optional.empty());

    // when: Se busca entidad con ese id
    // then: Se produce error porque no encuentra la entidad con ese Id
    Assertions.assertThatThrownBy(() -> service.findById(id)).isInstanceOf(EstadoRetrospectivaNotFoundException.class);
  }

  @Test
  public void findAll_Unlimited_ReturnsFullEstadoRetrospectivaList() {

    // given: Datos existentes
    List<EstadoRetrospectiva> response = new LinkedList<EstadoRetrospectiva>();
    response.add(getMockData(1L));
    response.add(getMockData(2L));

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<EstadoRetrospectiva>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(response));

    // when: Se buscan todos las datos
    Page<EstadoRetrospectiva> result = service.findAll(null, Pageable.unpaged());

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getContent()).isEqualTo(response);
    Assertions.assertThat(result.getNumber()).isEqualTo(0);
    Assertions.assertThat(result.getSize()).isEqualTo(response.size());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findAll_Unlimited_ReturnEmptyPage() {

    // given: No hay datos
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<EstadoRetrospectiva>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(new LinkedList<EstadoRetrospectiva>()));

    // when: Se buscan todos las datos
    Page<EstadoRetrospectiva> result = service.findAll(null, Pageable.unpaged());

    // then: Se recupera lista vacía
    Assertions.assertThat(result.isEmpty());
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {

    // given: Datos existentes
    List<EstadoRetrospectiva> response = new LinkedList<EstadoRetrospectiva>();
    response.add(getMockData(1L));
    response.add(getMockData(2L));
    response.add(getMockData(3L));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<EstadoRetrospectiva> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<EstadoRetrospectiva>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<EstadoRetrospectiva> result = service.findAll(null, pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findAll_WithPaging_ReturnEmptyPage() {

    // given: No hay datos
    List<EstadoRetrospectiva> response = new LinkedList<EstadoRetrospectiva>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<EstadoRetrospectiva> pageResponse = new PageImpl<>(response, pageable, response.size());

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<EstadoRetrospectiva>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<EstadoRetrospectiva> result = service.findAll(null, pageable);

    // then: Se recupera lista de datos paginados vacía
    Assertions.assertThat(result).isEmpty();
  }

  /**
   * Genera un objeto {@link EstadoRetrospectiva}
   * 
   * @param id
   * @return EstadoRetrospectiva
   */
  private EstadoRetrospectiva getMockData(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final EstadoRetrospectiva data = new EstadoRetrospectiva();
    data.setId(id);
    data.setNombre("NombreEstadoRetrospectiva" + txt);
    data.setActivo(Boolean.TRUE);

    return data;
  }

}
