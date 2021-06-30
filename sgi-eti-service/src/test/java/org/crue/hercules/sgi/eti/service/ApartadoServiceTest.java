package org.crue.hercules.sgi.eti.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ApartadoNotFoundException;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.repository.ApartadoRepository;
import org.crue.hercules.sgi.eti.service.impl.ApartadoServiceImpl;
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
 * ApartadoServiceTest
 */
public class ApartadoServiceTest extends BaseServiceTest {

  @Mock
  private ApartadoRepository repository;

  private ApartadoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ApartadoServiceImpl(repository);
  }

  @Test
  public void find_WithExistingId_ReturnsApartado() {

    // given: Entidad con un determinado Id
    Apartado response = getMockData(1L, 1L, null);
    BDDMockito.given(repository.findById(response.getId())).willReturn(Optional.of(response));

    // when: Se busca la entidad por ese Id
    Apartado result = service.findById(response.getId());

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
    Assertions.assertThatThrownBy(() -> service.findById(id)).isInstanceOf(ApartadoNotFoundException.class);
  }

  @Test
  public void findAll_Unlimited_ReturnsFullApartadoList() {

    // given: Datos existentes
    List<Apartado> response = new LinkedList<Apartado>();
    response.add(getMockData(1L, 1L, null));
    response.add(getMockData(2L, 1L, 1L));

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Apartado>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(response));

    // when: Se buscan todos las datos
    Page<Apartado> result = service.findAll(null, Pageable.unpaged());

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getContent()).isEqualTo(response);
    Assertions.assertThat(result.getNumber()).isEqualTo(0);
    Assertions.assertThat(result.getSize()).isEqualTo(response.size());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findAll_Unlimited_ReturnEmptyPage() {

    // given: No hay datos
    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Apartado>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(new LinkedList<Apartado>()));

    // when: Se buscan todos las datos
    Page<Apartado> result = service.findAll(null, Pageable.unpaged());

    // then: Se recupera lista vacía
    Assertions.assertThat(result.isEmpty());
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {

    // given: Datos existentes
    List<Apartado> response = new LinkedList<Apartado>();
    response.add(getMockData(1L, 1L, null));
    response.add(getMockData(2L, 1L, 1L));
    response.add(getMockData(3L, 1L, 1L));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<Apartado> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Apartado>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<Apartado> result = service.findAll(null, pageable);

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
    List<Apartado> response = new LinkedList<Apartado>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<Apartado> pageResponse = new PageImpl<>(response, pageable, response.size());

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Apartado>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<Apartado> result = service.findAll(null, pageable);

    // then: Se recupera lista de datos paginados vacía
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void findByBloqueIdValidId() {
    // given: Entidad con un determinado Id
    List<Apartado> response = new LinkedList<Apartado>();
    Apartado apartado = getMockData(1L, 1L, null);
    Bloque Bloque = apartado.getBloque();
    response.add(apartado);

    Page<Apartado> pageResponse = new PageImpl<>(response.subList(0, 1), Pageable.unpaged(), response.size());
    BDDMockito.given(repository.findByBloqueIdAndPadreIsNull(Bloque.getId(), Pageable.unpaged()))
        .willReturn(pageResponse);

    // when: Se busca la entidad por ese Id
    Page<Apartado> result = service.findByBloqueId(Bloque.getId(), Pageable.unpaged());

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(0, 1));
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findByBloqueIdNullId() {
    // given: EL id del bloque sea null
    Long id = null;
    try {
      // when: se quiera listar sus apartados
      service.findByBloqueId(id, Pageable.unpaged());
      Assertions.fail("El id no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Id no puede ser null para buscar un apartado por el Id de su Bloque");
    }
  }

  @Test
  public void findByPadreIdValid() {
    // given: Entidad con un determinado Id
    Long id = 123L;
    List<Apartado> response = new LinkedList<Apartado>();
    Apartado apartado = getMockData(1L, 1L, null);
    response.add(apartado);

    Page<Apartado> pageResponse = new PageImpl<>(response.subList(0, 1), Pageable.unpaged(), response.size());
    BDDMockito.given(repository.findByPadreId(id, Pageable.unpaged())).willReturn(pageResponse);

    // when: Se busca la entidad por ese Id
    Page<Apartado> result = service.findByPadreId(id, Pageable.unpaged());

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(0, 1));
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findByPadreIdNull() {
    // given: EL id del padre sea null
    Long id = null;
    try {
      // when: se quiera listar sus apartados hijos
      service.findByPadreId(id, Pageable.unpaged());
      Assertions.fail("Id no puede ser null para buscar una apartado por el Id de su padre");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Id no puede ser null para buscar un apartado por el Id de su padre");
    }
  }

  /**
   * Genera un objeto {@link Apartado}
   * 
   * @param id
   * @param bloqueId
   * @param componenteFormularioId
   * @param padreId
   * @return Apartado
   */
  private Apartado getMockData(Long id, Long bloqueId, Long padreId) {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion1");
    Bloque Bloque = new Bloque(bloqueId, formulario, "Bloque " + bloqueId, bloqueId.intValue());

    Apartado padre = (padreId != null) ? getMockData(padreId, bloqueId, null) : null;

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final Apartado data = new Apartado();
    data.setId(id);
    data.setBloque(Bloque);
    data.setNombre("Apartado" + txt);
    data.setPadre(padre);
    data.setOrden(id.intValue());
    data.setEsquema("{\"nombre\":\"EsquemaApartado" + txt + "\"}");

    return data;
  }

}
