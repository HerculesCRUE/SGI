package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConceptoGastoDescripcion;
import org.crue.hercules.sgi.csp.model.ConceptoGastoNombre;
import org.crue.hercules.sgi.csp.repository.ConceptoGastoRepository;
import org.crue.hercules.sgi.csp.service.impl.ConceptoGastoServiceImpl;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ConceptoGastoServiceTest
 */
@Import({ ConceptoGastoServiceImpl.class })
class ConceptoGastoServiceTest extends BaseServiceTest {

  @MockBean
  private ConceptoGastoRepository repository;

  @Autowired
  private ConceptoGastoService service;

  @Test
  void create_ReturnsConceptoGasto() {
    // given: Un nuevo ConceptoGasto
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(null);

    BDDMockito
        .given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
            ArgumentMatchers.<String>any()))
        .willReturn(Optional.empty());

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
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConceptoGasto que ya tiene id
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L);

    // when: Creamos el ConceptoGasto
    // then: Lanza una excepcion porque el ConceptoGasto ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(conceptoGasto)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Concepto de Gasto debe ser nulo");
  }

  @Test
  void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConceptoGasto con un nombre que ya existe
    ConceptoGasto conceptoGastoNew = generarMockConceptoGasto(null, "nombreRepetido");
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L, "nombreRepetido");

    BDDMockito.given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
        ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(conceptoGasto));

    Assertions.assertThatThrownBy(
        // when: create ConceptoGasto
        () -> service.create(conceptoGastoNew))
        // then: throw exception as Nombre already exists
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un concepto de gasto con el nombre '%s'",
            I18nHelper.getValueForLanguage(conceptoGastoNew.getNombre(), Language.ES));
  }

  @Test
  void update_ReturnsConceptoGasto() {
    // given: Un nuevo ConceptoGasto con el nombre actualizado
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L);
    ConceptoGasto conceptoGastoNombreActualizado = generarMockConceptoGasto(1L, "NombreActualizado");

    BDDMockito.given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
        ArgumentMatchers.<String>any()))
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
  void update_WithIdNotExist_ThrowsConceptoGastoNotFoundException() {
    // given: Un ConceptoGasto actualizado con un id que no existe
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L, "ConceptoGasto");

    BDDMockito.given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
        ArgumentMatchers.<String>any()))
        .willReturn(Optional.empty());

    // when: Actualizamos el ConceptoGasto
    // then: Lanza una excepcion porque el ConceptoGasto no existe
    Assertions.assertThatThrownBy(() -> service.update(conceptoGasto))
        .isInstanceOf(ConceptoGastoNotFoundException.class);
  }

  @Test
  void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un ConceptoGasto actualizado con un nombre que ya existe
    ConceptoGasto conceptoGastoActualizado = generarMockConceptoGasto(1L, "nombreRepetido");
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(2L, "nombreRepetido");

    BDDMockito.given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
        ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(conceptoGasto));

    // when: Actualizamos el ConceptoGasto
    // then: Lanza una excepcion porque ya existe otro ConceptoGasto con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(conceptoGastoActualizado))
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un concepto de gasto con el nombre '%s'",
            I18nHelper.getValueForLanguage(conceptoGasto.getNombre(), Language.ES));
  }

  @Test
  void enable_ReturnsConceptoGasto() {
    // given: Un ConceptoGasto inactivo
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L);
    conceptoGasto.setActivo(false);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(conceptoGasto));
    BDDMockito.given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
        ArgumentMatchers.<String>any()))
        .willReturn(Optional.empty());
    BDDMockito.given(repository.save(ArgumentMatchers.<ConceptoGasto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Activamos el ConceptoGasto
    ConceptoGasto conceptoGastoActualizado = service.enable(conceptoGasto.getId());

    // then: El ConceptoGasto se activa correctamente.
    Assertions.assertThat(conceptoGastoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(conceptoGastoActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(conceptoGastoActualizado.getNombre()).as("getNombre()").isEqualTo(conceptoGasto.getNombre());
    Assertions.assertThat(conceptoGastoActualizado.getActivo()).as("getActivo()").isTrue();
  }

  @Test
  void enable_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un ConceptoGasto inactivo con un nombre que ya existe activo
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(1L, "nombreRepetido");
    conceptoGasto.setActivo(false);
    ConceptoGasto conceptoGastoRepetido = generarMockConceptoGasto(2L, "nombreRepetido");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(conceptoGasto));
    BDDMockito.given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
        ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(conceptoGastoRepetido));

    // when: activamos el ConceptoGasto
    // then: Lanza una excepcion porque el ConceptoGasto no existe
    Assertions.assertThatThrownBy(() -> service.enable(conceptoGasto.getId()))
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un concepto de gasto con el nombre '%s'",
            I18nHelper.getValueForLanguage(conceptoGasto.getNombre(), Language.ES));
  }

  @Test
  void enable_WithIdNotExist_ThrowsConceptoGastoNotFoundException() {
    // given: Un id de un ConceptoGasto que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: Activamos el ConceptoGasto
    // then: Lanza una excepcion porque el ConceptoGasto no existe
    Assertions.assertThatThrownBy(() -> service.enable(idNoExiste)).isInstanceOf(ConceptoGastoNotFoundException.class);
  }

  @Test
  void disable_ReturnsConceptoGasto() {
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
    Assertions.assertThat(conceptoGastoActualizado.getActivo()).as("getActivo()").isFalse();

  }

  @Test
  void disable_WithIdNotExist_ThrowsConceptoGastoNotFoundException() {
    // given: Un id de un ConceptoGasto que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el ConceptoGasto
    // then: Lanza una excepcion porque el ConceptoGasto no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste)).isInstanceOf(ConceptoGastoNotFoundException.class);
  }

  @Test
  void update_WithNombreRepetido_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConceptoGasto con un nombre que ya existe
    ConceptoGasto conceptoGastoUpdated = generarMockConceptoGasto(1L, "nombreRepetido");
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(2L, "nombreRepetido");

    BDDMockito.given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
        ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(conceptoGasto));

    // when: Actualizamos el ConceptoGasto
    // then: Lanza una excepcion porque ya existe otro ConceptoGasto con ese nombre
    Assertions.assertThatThrownBy(() -> service.update(conceptoGastoUpdated))
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un concepto de gasto con el nombre '%s'",
            I18nHelper.getValueForLanguage(conceptoGasto.getNombre(), Language.ES));
  }

  @Test
  void findAll_ReturnsPage() {
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
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConceptoGasto conceptoGasto = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(I18nHelper.getValueForLanguage(conceptoGasto.getNombre(), Language.ES))
          .isEqualTo("ConceptoGasto" + String.format("%03d", i));
    }
  }

  @Test
  void findAllTodos_ReturnsPage() {
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
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConceptoGasto conceptoGasto = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(I18nHelper.getValueForLanguage(conceptoGasto.getNombre(), Language.ES))
          .isEqualTo("ConceptoGasto" + String.format("%03d", i));
    }
  }

  @Test
  void findById_ReturnsConceptoGasto() {
    // given: Un ConceptoGasto con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockConceptoGasto(idBuscado)));

    // when: Buscamos el ConceptoGasto por su id
    ConceptoGasto conceptoGasto = service.findById(idBuscado);

    // then: el ConceptoGasto
    Assertions.assertThat(conceptoGasto).as("isNotNull()").isNotNull();
    Assertions.assertThat(conceptoGasto.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(I18nHelper.getValueForLanguage(conceptoGasto.getNombre(), Language.ES)).as("getNombre()")
        .isEqualTo("nombre-1");
    Assertions.assertThat(conceptoGasto.getActivo()).as("getActivo()").isTrue();
  }

  @Test
  void findById_WithIdNotExist_ThrowsConceptoGastoNotFoundException() throws Exception {
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
    Set<ConceptoGastoNombre> nombreConceptoGasto = new HashSet<>();
    nombreConceptoGasto.add(new ConceptoGastoNombre(Language.ES, nombre));

    Set<ConceptoGastoDescripcion> descripcionConceptoGasto = new HashSet<>();
    descripcionConceptoGasto.add(new ConceptoGastoDescripcion(Language.ES, "descripcion-" + id));

    ConceptoGasto conceptoGasto = new ConceptoGasto();
    conceptoGasto.setId(id);
    conceptoGasto.setNombre(nombreConceptoGasto);
    conceptoGasto.setDescripcion(descripcionConceptoGasto);
    conceptoGasto.setActivo(true);
    conceptoGasto.setCostesIndirectos(true);

    return conceptoGasto;
  }

}
