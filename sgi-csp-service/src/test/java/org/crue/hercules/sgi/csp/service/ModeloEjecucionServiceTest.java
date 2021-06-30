package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.csp.service.impl.ModeloEjecucionServiceImpl;
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
 * ModeloEjecucionServiceTest
 */
public class ModeloEjecucionServiceTest extends BaseServiceTest {

  @Mock
  private ModeloEjecucionRepository modeloEjecucionRepository;

  private ModeloEjecucionService modeloEjecucionService;

  @BeforeEach
  public void setUp() throws Exception {
    modeloEjecucionService = new ModeloEjecucionServiceImpl(modeloEjecucionRepository);
  }

  @Test
  public void create_ReturnsModeloEjecucion() {
    // given: Un nuevo ModeloEjecucion
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(null);

    BDDMockito.given(modeloEjecucionRepository.findByNombreAndActivoIsTrue(modeloEjecucion.getNombre()))
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
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloEjecucion que ya tiene id
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(1L);

    // when: Creamos el ModeloEjecucion
    // then: Lanza una excepcion porque el ModeloEjecucion ya tiene id
    Assertions.assertThatThrownBy(() -> modeloEjecucionService.create(modeloEjecucion))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloEjecucion id tiene que ser null para crear un nuevo ModeloEjecucion");
  }

  @Test
  public void create_WithNombreRepetido_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloEjecucion con un nombre que ya existe
    ModeloEjecucion modeloEjecucionNew = generarMockModeloEjecucion(null, "nombreRepetido");
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(1L, "nombreRepetido");

    BDDMockito.given(modeloEjecucionRepository.findByNombreAndActivoIsTrue(modeloEjecucionNew.getNombre()))
        .willReturn(Optional.of(modeloEjecucion));

    // when: Creamos el ModeloEjecucion
    // then: Lanza una excepcion porque hay otro ModeloEjecucion con ese nombre
    Assertions.assertThatThrownBy(() -> modeloEjecucionService.create(modeloEjecucionNew))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un ModeloEjecucion activo con el nombre '%s'", modeloEjecucionNew.getNombre());
  }

  @Test
  public void update_ReturnsModeloEjecucion() {
    // given: Un nuevo ModeloEjecucion con el nombre actualizado
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(1L);
    ModeloEjecucion modeloEjecucionNombreActualizado = generarMockModeloEjecucion(1L, "NombreActualizado");

    BDDMockito
        .given(modeloEjecucionRepository.findByNombreAndActivoIsTrue(modeloEjecucionNombreActualizado.getNombre()))
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
  public void update_WithIdNotExist_ThrowsModeloEjecucionNotFoundException() {
    // given: Un ModeloEjecucion a actualizar con un id que no existe
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(1L, "ModeloEjecucion");

    // when: Actualizamos el ModeloEjecucion
    // then: Lanza una excepcion porque el ModeloEjecucion no existe
    Assertions.assertThatThrownBy(() -> modeloEjecucionService.update(modeloEjecucion))
        .isInstanceOf(ModeloEjecucionNotFoundException.class);
  }

  @Test
  public void enable_ReturnsModeloEjecucion() {
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
  public void enable_WithIdNotExist_ThrowsTipoFinanciacionNotFoundException() {
    // given: Un id de un ModeloEjecucion que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el ModeloEjecucion
    // then: Lanza una excepcion porque el ModeloEjecucion no existe
    Assertions.assertThatThrownBy(() -> modeloEjecucionService.enable(idNoExiste))
        .isInstanceOf(ModeloEjecucionNotFoundException.class);
  }

  @Test
  public void enable_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: Un ModeloEjecucion inactivo con nombre existente
    ModeloEjecucion modeloEjecucionExistente = generarMockModeloEjecucion(2L);
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(1L);
    modeloEjecucion.setActivo(Boolean.FALSE);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloEjecucion));
    BDDMockito.given(modeloEjecucionRepository.findByNombreAndActivoIsTrue(ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(modeloEjecucionExistente));

    // when: activamos el ModeloEjecucion
    // then: Lanza una excepcion porque el ModeloEjecucion no existe
    Assertions.assertThatThrownBy(() -> modeloEjecucionService.enable(modeloEjecucion.getId()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un ModeloEjecucion activo con el nombre '%s'", modeloEjecucion.getNombre());

  }

  @Test
  public void disable_ReturnsModeloEjecucion() {
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
    Assertions.assertThat(modeloEjecucionActualizado.getActivo()).as("getActivo()").isEqualTo(false);

  }

  @Test
  public void disable_WithIdNotExist_ThrowsModeloEjecucionNotFoundException() {
    // given: Un id de un ModeloEjecucion que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el ModeloEjecucion
    // then: Lanza una excepcion porque el ModeloEjecucion no existe
    Assertions.assertThatThrownBy(() -> modeloEjecucionService.disable(idNoExiste))
        .isInstanceOf(ModeloEjecucionNotFoundException.class);
  }

  @Test
  public void update_WithNombreRepetido_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloEjecucion con un nombre que ya existe
    ModeloEjecucion modeloEjecucionUpdated = generarMockModeloEjecucion(1L, "nombreRepetido");
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(2L, "nombreRepetido");

    BDDMockito.given(modeloEjecucionRepository.findByNombreAndActivoIsTrue(modeloEjecucionUpdated.getNombre()))
        .willReturn(Optional.of(modeloEjecucion));

    // when: Actualizamos el ModeloEjecucion
    // then: Lanza una excepcion porque ya existe otro ModeloEjecucion con ese
    // nombre
    Assertions.assertThatThrownBy(() -> modeloEjecucionService.update(modeloEjecucionUpdated))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un ModeloEjecucion activo con el nombre '%s'", modeloEjecucionUpdated.getNombre());
  }

  @Test
  public void findAll_ReturnsPage() {
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
    Page<ModeloEjecucion> page = modeloEjecucionService.findAll(null, paging);

    // then: Devuelve la pagina 3 con los ModeloEjecucion del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloEjecucion modeloEjecucion = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloEjecucion.getNombre()).isEqualTo("ModeloEjecucion" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllTodos_ReturnsPage() {
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
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloEjecucion modeloEjecucion = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloEjecucion.getNombre()).isEqualTo("ModeloEjecucion" + String.format("%03d", i));
    }
  }

  @Test
  public void findById_ReturnsModeloEjecucion() {
    // given: Un ModeloEjecucion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(modeloEjecucionRepository.findById(idBuscado))
        .willReturn(Optional.of(generarMockModeloEjecucion(idBuscado)));

    // when: Buscamos el ModeloEjecucion por su id
    ModeloEjecucion modeloEjecucion = modeloEjecucionService.findById(idBuscado);

    // then: el ModeloEjecucion
    Assertions.assertThat(modeloEjecucion).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloEjecucion.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(modeloEjecucion.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(modeloEjecucion.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-1");
    Assertions.assertThat(modeloEjecucion.getActivo()).as("getActivo()").isEqualTo(true);

  }

  @Test
  public void findById_WithIdNotExist_ThrowsModeloEjecucionNotFoundException() throws Exception {
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

    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(id);
    modeloEjecucion.setNombre(nombre);
    modeloEjecucion.setDescripcion("descripcion-" + id);
    modeloEjecucion.setActivo(Boolean.TRUE);

    return modeloEjecucion;
  }

}