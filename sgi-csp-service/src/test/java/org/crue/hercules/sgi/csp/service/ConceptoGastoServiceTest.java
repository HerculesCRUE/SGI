package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.repository.ConceptoGastoRepository;
import org.crue.hercules.sgi.csp.service.impl.ConceptoGastoServiceImpl;
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

/**
 * ConceptoGastoServiceTest
 */
public class ConceptoGastoServiceTest extends BaseServiceTest {

  @Mock
  private ConceptoGastoRepository repository;

  private ConceptoGastoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConceptoGastoServiceImpl(repository);
  }

  @Test
  public void create_ReturnsConceptoGasto() {
    // given: Un nuevo ConceptoGasto
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(null);

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(conceptoGasto.getNombre())).willReturn(Optional.empty());

    BDDMockito.given(repository.save(conceptoGasto)).will((InvocationOnMock invocation) -> {
      ConceptoGasto conceptoGastoCreado = invocation.getArgument(0);
      conceptoGastoCreado.setId(1L);
      return conceptoGastoCreado;
    });

    // when: Creamos el ConceptoGasto
    ConceptoGasto conceptoGastoCreado = service.create(conceptoGasto);

    // then: El ConceptoGasto se crea correctamente
    Assertions.assertThat(conceptoGastoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(conceptoGastoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(conceptoGastoCreado.getNombre()).as("getNombre").isEqualTo(conceptoGasto.getNombre());
    Assertions.assertThat(conceptoGastoCreado.getActivo()).as("getActivo").isEqualTo(conceptoGasto.getActivo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConceptoGasto que ya tiene id
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L);

    // when: Creamos el ConceptoGasto
    // then: Lanza una excepcion porque el ConceptoGasto ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(conceptoGasto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConceptoGasto id tiene que ser null para crear un nuevo ConceptoGasto");
  }

  @Test
  public void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConceptoGasto con un nombre que ya existe
    ConceptoGasto conceptoGastoNew = generarMockConceptoGasto(null, "nombreRepetido");
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L, "nombreRepetido");

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(conceptoGastoNew.getNombre()))
        .willReturn(Optional.of(conceptoGasto));

    // when: Creamos el ConceptoGasto
    // then: Lanza una excepcion porque hay otro ConceptoGasto con ese nombre
    Assertions.assertThatThrownBy(() -> service.create(conceptoGastoNew)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un ConceptoGasto con el nombre " + conceptoGastoNew.getNombre());
  }

  @Test
  public void update_ReturnsConceptoGasto() {
    // given: Un nuevo ConceptoGasto con el nombre actualizado
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L);
    ConceptoGasto conceptoGastoNombreActualizado = generarMockConceptoGasto(1L, "NombreActualizado");

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(conceptoGastoNombreActualizado.getNombre()))
        .willReturn(Optional.empty());

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(conceptoGasto));
    BDDMockito.given(repository.save(ArgumentMatchers.<ConceptoGasto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ConceptoGasto
    ConceptoGasto conceptoGastoActualizado = service.update(conceptoGastoNombreActualizado);

    // then: El ConceptoGasto se actualiza correctamente.
    Assertions.assertThat(conceptoGastoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(conceptoGastoActualizado.getId()).as("getId()").isEqualTo(conceptoGasto.getId());
    Assertions.assertThat(conceptoGastoActualizado.getNombre()).as("getNombre()")
        .isEqualTo(conceptoGastoNombreActualizado.getNombre());
    Assertions.assertThat(conceptoGastoActualizado.getActivo()).as("getActivo()").isEqualTo(conceptoGasto.getActivo());
  }

  @Test
  public void update_WithIdNotExist_ThrowsConceptoGastoNotFoundException() {
    // given: Un ConceptoGasto actualizado con un id que no existe
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L, "ConceptoGasto");

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(conceptoGasto.getNombre())).willReturn(Optional.empty());

    // when: Actualizamos el ConceptoGasto
    // then: Lanza una excepcion porque el ConceptoGasto no existe
    Assertions.assertThatThrownBy(() -> service.update(conceptoGasto))
        .isInstanceOf(ConceptoGastoNotFoundException.class);
  }

  @Test
  public void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un ConceptoGasto actualizado con un nombre que ya existe
    ConceptoGasto conceptoGastoActualizado = generarMockConceptoGasto(1L, "nombreRepetido");
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(2L, "nombreRepetido");

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(conceptoGastoActualizado.getNombre()))
        .willReturn(Optional.of(conceptoGasto));

    // when: Actualizamos el ConceptoGasto
    // then: Lanza una excepcion porque hay otro ConceptoGasto con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(conceptoGastoActualizado))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un ConceptoGasto con el nombre " + conceptoGastoActualizado.getNombre());
  }

  @Test
  public void enable_ReturnsConceptoGasto() {
    // given: Un ConceptoGasto inactivo
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L);
    conceptoGasto.setActivo(false);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(conceptoGasto));
    BDDMockito.given(repository.findByNombreAndActivoIsTrue(conceptoGasto.getNombre())).willReturn(Optional.empty());
    BDDMockito.given(repository.save(ArgumentMatchers.<ConceptoGasto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Activamos el ConceptoGasto
    ConceptoGasto conceptoGastoActualizado = service.enable(conceptoGasto.getId());

    // then: El ConceptoGasto se activa correctamente.
    Assertions.assertThat(conceptoGastoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(conceptoGastoActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(conceptoGastoActualizado.getNombre()).as("getNombre()").isEqualTo(conceptoGasto.getNombre());
    Assertions.assertThat(conceptoGastoActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void enable_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un ConceptoGasto inactivo con un nombre que ya existe activo
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L, "nombreRepetido");
    conceptoGasto.setActivo(false);
    ConceptoGasto conceptoGastoRepetido = generarMockConceptoGasto(2L, "nombreRepetido");

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(conceptoGasto.getNombre()))
        .willReturn(Optional.of(conceptoGastoRepetido));

    // when: Activamos el ConceptoGasto
    // then: Lanza una excepcion porque hay otro ConceptoGasto con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(conceptoGasto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un ConceptoGasto con el nombre %s", conceptoGasto.getNombre());
  }

  @Test
  public void enable_WithIdNotExist_ThrowsConceptoGastoNotFoundException() {
    // given: Un id de un ConceptoGasto que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: Activamos el ConceptoGasto
    // then: Lanza una excepcion porque el ConceptoGasto no existe
    Assertions.assertThatThrownBy(() -> service.enable(idNoExiste)).isInstanceOf(ConceptoGastoNotFoundException.class);
  }

  @Test
  public void disable_ReturnsConceptoGasto() {
    // given: Un nuevo ConceptoGasto activo
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(conceptoGasto));
    BDDMockito.given(repository.save(ArgumentMatchers.<ConceptoGasto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el ConceptoGasto
    ConceptoGasto conceptoGastoActualizado = service.disable(conceptoGasto.getId());

    // then: El ConceptoGasto se desactiva correctamente.
    Assertions.assertThat(conceptoGastoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(conceptoGastoActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(conceptoGastoActualizado.getNombre()).as("getNombre()").isEqualTo(conceptoGasto.getNombre());
    Assertions.assertThat(conceptoGastoActualizado.getActivo()).as("getActivo()").isEqualTo(false);

  }

  @Test
  public void disable_WithIdNotExist_ThrowsConceptoGastoNotFoundException() {
    // given: Un id de un ConceptoGasto que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el ConceptoGasto
    // then: Lanza una excepcion porque el ConceptoGasto no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste)).isInstanceOf(ConceptoGastoNotFoundException.class);
  }

  @Test
  public void update_WithNombreRepetido_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConceptoGasto con un nombre que ya existe
    ConceptoGasto conceptoGastoUpdated = generarMockConceptoGasto(1L, "nombreRepetido");
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(2L, "nombreRepetido");

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(conceptoGastoUpdated.getNombre()))
        .willReturn(Optional.of(conceptoGasto));

    // when: Actualizamos el ConceptoGasto
    // then: Lanza una excepcion porque ya existe otro ConceptoGasto con ese
    // nombre
    Assertions.assertThatThrownBy(() -> service.update(conceptoGastoUpdated))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 ConceptoGasto
    List<ConceptoGasto> conceptoGastoes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      conceptoGastoes.add(generarMockConceptoGasto(i, "ConceptoGasto" + String.format("%03d", i)));
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ConceptoGasto>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConceptoGasto>>() {
          @Override
          public Page<ConceptoGasto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > conceptoGastoes.size() ? conceptoGastoes.size() : toIndex;
            List<ConceptoGasto> content = conceptoGastoes.subList(fromIndex, toIndex);
            Page<ConceptoGasto> page = new PageImpl<>(content, pageable, conceptoGastoes.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConceptoGasto> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los ConceptoGasto del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConceptoGasto conceptoGasto = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(conceptoGasto.getNombre()).isEqualTo("ConceptoGasto" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllTodos_ReturnsPage() {
    // given: Una lista con 37 ConceptoGasto
    List<ConceptoGasto> conceptoGastoes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      conceptoGastoes.add(generarMockConceptoGasto(i, "ConceptoGasto" + String.format("%03d", i)));
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ConceptoGasto>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConceptoGasto>>() {
          @Override
          public Page<ConceptoGasto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > conceptoGastoes.size() ? conceptoGastoes.size() : toIndex;
            List<ConceptoGasto> content = conceptoGastoes.subList(fromIndex, toIndex);
            Page<ConceptoGasto> page = new PageImpl<>(content, pageable, conceptoGastoes.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConceptoGasto> page = service.findAllTodos(null, paging);

    // then: Devuelve la pagina 3 con los ConceptoGasto del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConceptoGasto conceptoGasto = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(conceptoGasto.getNombre()).isEqualTo("ConceptoGasto" + String.format("%03d", i));
    }
  }

  @Test
  public void findById_ReturnsConceptoGasto() {
    // given: Un ConceptoGasto con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockConceptoGasto(idBuscado)));

    // when: Buscamos el ConceptoGasto por su id
    ConceptoGasto conceptoGasto = service.findById(idBuscado);

    // then: el ConceptoGasto
    Assertions.assertThat(conceptoGasto).as("isNotNull()").isNotNull();
    Assertions.assertThat(conceptoGasto.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(conceptoGasto.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(conceptoGasto.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsConceptoGastoNotFoundException() throws Exception {
    // given: Ningun ConceptoGasto con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ConceptoGasto por su id
    // then: lanza un ConceptoGastoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado)).isInstanceOf(ConceptoGastoNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto ConceptoGasto
   * 
   * @param id id del ConceptoGasto
   * @return el objeto ConceptoGasto
   */
  private ConceptoGasto generarMockConceptoGasto(Long id) {
    return generarMockConceptoGasto(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto ConceptoGasto
   * 
   * @param id     id del ConceptoGasto
   * @param nombre nombre del ConceptoGasto
   * @return el objeto ConceptoGasto
   */
  private ConceptoGasto generarMockConceptoGasto(Long id, String nombre) {
    ConceptoGasto conceptoGasto = new ConceptoGasto();
    conceptoGasto.setId(id);
    conceptoGasto.setNombre(nombre);
    conceptoGasto.setDescripcion("descripcion-" + id);
    conceptoGasto.setActivo(true);

    return conceptoGasto;
  }

}
