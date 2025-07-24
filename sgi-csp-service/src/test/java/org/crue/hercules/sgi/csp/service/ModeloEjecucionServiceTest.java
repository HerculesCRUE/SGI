package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionDescripcion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.service.impl.ModeloEjecucionServiceImpl;
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
 * ModeloEjecucionServiceTest
 */
@Import({ ModeloEjecucionServiceImpl.class })
class ModeloEjecucionServiceTest extends BaseServiceTest {

  @MockBean
  private ModeloEjecucionRepository modeloEjecucionRepository;

  @MockBean
  private ProyectoRepository proyectoRepository;

  @Autowired
  private ModeloEjecucionService modeloEjecucionService;

  @Test
  void create_ReturnsModeloEjecucion() {
    // given: Un nuevo ModeloEjecucion
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(null);

    BDDMockito
        .given(modeloEjecucionRepository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
            ArgumentMatchers.<String>any()))
        .willReturn(Optional.empty());

    BDDMockito.given(modeloEjecucionRepository.save(modeloEjecucion)).will((InvocationOnMock invocation) -> {
      ModeloEjecucion modeloEjecucionCreado = invocation.getArgument(0);
      modeloEjecucionCreado.setId(1L);
      return modeloEjecucionCreado;
    });

    // when: Creamos el ModeloEjecucion
    ModeloEjecucion modeloEjecucionCreado = modeloEjecucionService.create(modeloEjecucion);

    // then: El ModeloEjecucion se crea correctamente
    Assertions.assertThat(modeloEjecucionCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloEjecucionCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(modeloEjecucionCreado.getNombre()).as("getNombre").isEqualTo(modeloEjecucion.getNombre());
    Assertions.assertThat(modeloEjecucionCreado.getActivo()).as("getActivo").isEqualTo(modeloEjecucion.getActivo());
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloEjecucion que ya tiene id
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(1L);

    // when: Creamos el ModeloEjecucion
    // then: Lanza una excepcion porque el ModeloEjecucion ya tiene id
    Assertions.assertThatThrownBy(() -> modeloEjecucionService.create(modeloEjecucion))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Modelo Ejecución debe ser nulo");
  }

  @Test
  void create_WithNombreRepetido_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloEjecucion con un nombre que ya existe
    ModeloEjecucion modeloEjecucionNew = generarMockModeloEjecucion(null, "nombreRepetido");
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(1L, "nombreRepetido");

    BDDMockito
        .given(modeloEjecucionRepository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
            ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(modeloEjecucion));

    // when: Creamos el ModeloEjecucion
    // then: Lanza una excepcion porque hay otro ModeloEjecucion con ese nombre
    Assertions.assertThatThrownBy(() -> modeloEjecucionService.create(modeloEjecucionNew))
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un modelo de ejecución con el nombre '%s'",
            I18nHelper.getValueForLanguage(modeloEjecucion.getNombre(), Language.ES));
  }

  @Test
  void update_ReturnsModeloEjecucion() {
    // given: Un nuevo ModeloEjecucion con el nombre actualizado
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(1L);
    ModeloEjecucion modeloEjecucionNombreActualizado = generarMockModeloEjecucion(1L, "NombreActualizado");

    BDDMockito
        .given(modeloEjecucionRepository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
            ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(modeloEjecucion));

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloEjecucion));
    BDDMockito.given(modeloEjecucionRepository.save(ArgumentMatchers.<ModeloEjecucion>any()))
        .willReturn(modeloEjecucionNombreActualizado);

    // when: Actualizamos el ModeloEjecucion
    ModeloEjecucion modeloEjecucionActualizado = modeloEjecucionService.update(modeloEjecucionNombreActualizado);

    // then: El ModeloEjecucion se actualiza correctamente.
    Assertions.assertThat(modeloEjecucionActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloEjecucionActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(modeloEjecucionActualizado.getNombre()).as("getNombre()")
        .isEqualTo(modeloEjecucionNombreActualizado.getNombre());

  }

  @Test
  void update_WithIdNotExist_ThrowsModeloEjecucionNotFoundException() {
    // given: Un ModeloEjecucion a actualizar con un id que no existe
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(1L, "ModeloEjecucion");

    // when: Actualizamos el ModeloEjecucion
    // then: Lanza una excepcion porque el ModeloEjecucion no existe
    Assertions.assertThatThrownBy(() -> modeloEjecucionService.update(modeloEjecucion))
        .isInstanceOf(ModeloEjecucionNotFoundException.class);
  }

  @Test
  void enable_ReturnsModeloEjecucion() {
    // given: Un nuevo ModeloEjecucion inactivo
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(1L);
    modeloEjecucion.setActivo(Boolean.FALSE);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloEjecucion));
    BDDMockito.given(modeloEjecucionRepository.save(ArgumentMatchers.<ModeloEjecucion>any()))
        .willAnswer(new Answer<ModeloEjecucion>() {
          @Override
          public ModeloEjecucion answer(InvocationOnMock invocation) throws Throwable {
            ModeloEjecucion givenData = invocation.getArgument(0, ModeloEjecucion.class);
            givenData.setActivo(Boolean.TRUE);
            return givenData;
          }
        });

    // when: activamos el ModeloEjecucion
    ModeloEjecucion modeloEjecucionActualizado = modeloEjecucionService.enable(modeloEjecucion.getId());

    // then: El ModeloEjecucion se activa correctamente.
    Assertions.assertThat(modeloEjecucionActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloEjecucionActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(modeloEjecucionActualizado.getNombre()).as("getNombre()")
        .isEqualTo(modeloEjecucion.getNombre());
    Assertions.assertThat(modeloEjecucionActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(modeloEjecucion.getDescripcion());
    Assertions.assertThat(modeloEjecucionActualizado.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);

  }

  @Test
  void enable_WithIdNotExist_ThrowsTipoFinanciacionNotFoundException() {
    // given: Un id de un ModeloEjecucion que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el ModeloEjecucion
    // then: Lanza una excepcion porque el ModeloEjecucion no existe
    Assertions.assertThatThrownBy(() -> modeloEjecucionService.enable(idNoExiste))
        .isInstanceOf(ModeloEjecucionNotFoundException.class);
  }

  @Test
  void enable_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un ModeloEjecucion inactivo con nombre existente
    ModeloEjecucion modeloEjecucionExistente = generarMockModeloEjecucion(2L);
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(1L);
    modeloEjecucion.setActivo(Boolean.FALSE);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloEjecucion));
    BDDMockito
        .given(modeloEjecucionRepository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
            ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(modeloEjecucionExistente));

    // when: activamos el ModeloEjecucion
    // then: Lanza una excepcion porque el ModeloEjecucion no existe
    Assertions.assertThatThrownBy(() -> modeloEjecucionService.enable(modeloEjecucion.getId()))
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un modelo de ejecución con el nombre '%s'",
            I18nHelper.getValueForLanguage(modeloEjecucion.getNombre(), Language.ES));

  }

  @Test
  void disable_ReturnsModeloEjecucion() {
    // given: Un nuevo ModeloEjecucion activo
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(1L);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloEjecucion));
    BDDMockito.given(modeloEjecucionRepository.save(ArgumentMatchers.<ModeloEjecucion>any()))
        .willAnswer(new Answer<ModeloEjecucion>() {
          @Override
          public ModeloEjecucion answer(InvocationOnMock invocation) throws Throwable {
            ModeloEjecucion givenData = invocation.getArgument(0, ModeloEjecucion.class);
            givenData.setActivo(Boolean.FALSE);
            return givenData;
          }
        });

    // when: Desactivamos el ModeloEjecucion
    ModeloEjecucion modeloEjecucionActualizado = modeloEjecucionService.disable(modeloEjecucion.getId());

    // then: El ModeloEjecucion se desactiva correctamente.
    Assertions.assertThat(modeloEjecucionActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloEjecucionActualizado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(modeloEjecucionActualizado.getNombre()).as("getNombre()")
        .isEqualTo(modeloEjecucion.getNombre());
    Assertions.assertThat(modeloEjecucionActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(modeloEjecucion.getDescripcion());
    Assertions.assertThat(modeloEjecucionActualizado.getActivo()).as("getActivo()").isFalse();

  }

  @Test
  void disable_WithIdNotExist_ThrowsModeloEjecucionNotFoundException() {
    // given: Un id de un ModeloEjecucion que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el ModeloEjecucion
    // then: Lanza una excepcion porque el ModeloEjecucion no existe
    Assertions.assertThatThrownBy(() -> modeloEjecucionService.disable(idNoExiste))
        .isInstanceOf(ModeloEjecucionNotFoundException.class);
  }

  @Test
  void update_WithNombreRepetido_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloEjecucion con un nombre que ya existe
    ModeloEjecucion modeloEjecucionUpdated = generarMockModeloEjecucion(1L, "nombreRepetido");
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(2L, "nombreRepetido");

    BDDMockito
        .given(modeloEjecucionRepository.findByNombreLangAndNombreValueAndActivoIsTrue(ArgumentMatchers.<Language>any(),
            ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(modeloEjecucion));

    // when: Actualizamos el ModeloEjecucion
    // then: Lanza una excepcion porque ya existe otro ModeloEjecucion con ese
    // nombre
    Assertions.assertThatThrownBy(() -> modeloEjecucionService.update(modeloEjecucionUpdated))
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un modelo de ejecución con el nombre '%s'",
            I18nHelper.getValueForLanguage(modeloEjecucion.getNombre(), Language.ES));
  }

  @Test
  void findAll_ReturnsList() {
    // given: Una lista con 37 ModeloEjecucion
    List<ModeloEjecucion> modelosEjecucion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modelosEjecucion.add(generarMockModeloEjecucion(i, "ModeloEjecucion" + String.format("%03d", i)));
    }

    BDDMockito
        .given(modeloEjecucionRepository.findAll(ArgumentMatchers.<Specification<ModeloEjecucion>>any()))
        .willReturn(modelosEjecucion);

    List<ModeloEjecucion> listado = modeloEjecucionService.findAll(null);
    Assertions.assertThat(listado.size()).isEqualTo(37);
    for (int i = 31; i < 37; i++) {
      ModeloEjecucion modeloEjecucion = listado.get(i - 1);
      Assertions.assertThat(I18nHelper.getValueForLanguage(modeloEjecucion.getNombre(), Language.ES))
          .isEqualTo("ModeloEjecucion" + String.format("%03d", i));
    }
  }

  @Test
  void findAllTodos_ReturnsPage() {
    // given: Una lista con 37 ModeloEjecucion
    List<ModeloEjecucion> modelosEjecucion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modelosEjecucion.add(generarMockModeloEjecucion(i, "ModeloEjecucion" + String.format("%03d", i)));
    }

    BDDMockito.given(modeloEjecucionRepository.findAll(ArgumentMatchers.<Specification<ModeloEjecucion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloEjecucion>>() {
          @Override
          public Page<ModeloEjecucion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modelosEjecucion.size() ? modelosEjecucion.size() : toIndex;
            List<ModeloEjecucion> content = modelosEjecucion.subList(fromIndex, toIndex);
            Page<ModeloEjecucion> page = new PageImpl<>(content, pageable, modelosEjecucion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloEjecucion> page = modeloEjecucionService.findAllTodos(null, paging);

    // then: Devuelve la pagina 3 con los ModeloEjecucion del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloEjecucion modeloEjecucion = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(I18nHelper.getValueForLanguage(modeloEjecucion.getNombre(), Language.ES))
          .isEqualTo("ModeloEjecucion" + String.format("%03d", i));
    }
  }

  @Test
  void findById_ReturnsModeloEjecucion() {
    // given: Un ModeloEjecucion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(modeloEjecucionRepository.findById(idBuscado))
        .willReturn(Optional.of(generarMockModeloEjecucion(idBuscado)));

    // when: Buscamos el ModeloEjecucion por su id
    ModeloEjecucion modeloEjecucion = modeloEjecucionService.findById(idBuscado);

    // then: el ModeloEjecucion
    Assertions.assertThat(modeloEjecucion).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloEjecucion.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(I18nHelper.getValueForLanguage(modeloEjecucion.getNombre(), Language.ES)).as("getNombre()")
        .isEqualTo("nombre-1");
    Assertions.assertThat(I18nHelper.getValueForLanguage(modeloEjecucion.getDescripcion(), Language.ES))
        .as("getDescripcion()").isEqualTo("descripcion-1");
    Assertions.assertThat(modeloEjecucion.getActivo()).as("getActivo()").isTrue();

  }

  @Test
  void findById_WithIdNotExist_ThrowsModeloEjecucionNotFoundException() throws Exception {
    // given: Ningun ModeloEjecucion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(modeloEjecucionRepository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ModeloEjecucion por su id
    // then: lanza un ModeloEjecucionNotFoundException
    Assertions.assertThatThrownBy(() -> modeloEjecucionService.findById(idBuscado))
        .isInstanceOf(ModeloEjecucionNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto ModeloEjecucion
   * 
   * @param id id del ModeloEjecucion
   * @return el objeto ModeloEjecucion
   */
  private ModeloEjecucion generarMockModeloEjecucion(Long id) {
    return generarMockModeloEjecucion(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto ModeloEjecucion
   * 
   * @param id id del ModeloEjecucion
   * @return el objeto ModeloEjecucion
   */
  private ModeloEjecucion generarMockModeloEjecucion(Long id, String nombre) {
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, nombre));

    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion = new HashSet<>();
    descripcionModeloEjecucion.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-" + id));

    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(id);
    modeloEjecucion.setNombre(nombreModeloEjecucion);
    modeloEjecucion.setDescripcion(descripcionModeloEjecucion);
    modeloEjecucion.setActivo(Boolean.TRUE);

    return modeloEjecucion;
  }

}