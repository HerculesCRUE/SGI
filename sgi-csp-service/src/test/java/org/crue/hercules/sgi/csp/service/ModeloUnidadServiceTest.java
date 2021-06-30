package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ModeloUnidadNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.csp.repository.ModeloUnidadRepository;
import org.crue.hercules.sgi.csp.service.impl.ModeloUnidadServiceImpl;
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
 * ModeloUnidadServiceTest
 */
public class ModeloUnidadServiceTest extends BaseServiceTest {

  @Mock
  private ModeloEjecucionRepository modeloEjecucionRepository;

  @Mock
  private ModeloUnidadRepository modeloUnidadRepository;

  private ModeloUnidadService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ModeloUnidadServiceImpl(modeloEjecucionRepository, modeloUnidadRepository);
  }

  @Test
  public void create_ReturnsModeloUnidad() {
    // given: Un nuevo ModeloUnidad
    ModeloUnidad modeloUnidad = generarMockModeloUnidad(null, "unidad-1");

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloUnidad.getModeloEjecucion()));

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.empty());

    BDDMockito.given(modeloUnidadRepository.save(modeloUnidad)).will((InvocationOnMock invocation) -> {
      ModeloUnidad modeloUnidadCreado = invocation.getArgument(0);
      modeloUnidadCreado.setId(1L);
      return modeloUnidadCreado;
    });

    // when: Creamos el ModeloUnidad
    ModeloUnidad modeloUnidadCreado = service.create(modeloUnidad);

    // then: El ModeloUnidad se crea correctamente
    Assertions.assertThat(modeloUnidadCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloUnidadCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(modeloUnidadCreado.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloUnidadCreado.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(modeloUnidad.getModeloEjecucion().getId());
    Assertions.assertThat(modeloUnidadCreado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(modeloUnidad.getUnidadGestionRef());
    Assertions.assertThat(modeloUnidadCreado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloUnidad que ya tiene id
    ModeloUnidad modeloUnidad = generarMockModeloUnidad(1L);

    // when: Creamos el ModeloUnidad
    // then: Lanza una excepcion porque el ModeloUnidad ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(modeloUnidad)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithoutModeloEjecucionId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloUnidad con un ModeloEjecucion sin id
    ModeloUnidad modeloUnidad = generarMockModeloUnidad(null, "unidad-1");
    modeloUnidad.getModeloEjecucion().setId(null);

    // when: Creamos el ModeloUnidad
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(modeloUnidad)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithNoExistingModeloEjecucion_ThrowsModeloUnidadNotFoundException() {
    // given: Un nuevo ModeloUnidad con un ModeleoEjecucion que no existe
    ModeloUnidad modeloUnidad = generarMockModeloUnidad(null, "unidad-1");

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Creamos el ModeloUnidad
    // then: Lanza una excepcion porque el ModeloUnidad no existe id
    Assertions.assertThatThrownBy(() -> service.create(modeloUnidad))
        .isInstanceOf(ModeloEjecucionNotFoundException.class);
  }

  @Test
  public void create_WithDuplicatedModeloEjecucionIdAndUnidadGestionRefAndActivo_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloUnidad con una combinacion de ModeloEjecucionId y
    // unidadGestionRef que ya existe y esta activo
    ModeloUnidad modeloUnidad = generarMockModeloUnidad(null, "unidad-1");

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloUnidad.getModeloEjecucion()));

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(generarMockModeloUnidad(1L, "unidad-1")));

    // when: Creamos el ModeloUnidad
    // then: Lanza una excepcion porque ya existe esa relacion activa
    Assertions.assertThatThrownBy(() -> service.create(modeloUnidad)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithDuplicatedModeloEjecucionIdAndUnidadGestionRefAndActivoFalse_ReturnEnableModeloUnidad() {
    // given: Un nuevo ModeloUnidad con una combinacion de ModeloEjecucionId y
    // unidadGestionRef que ya existe y no esta activo
    ModeloUnidad modeloUnidad = generarMockModeloUnidad(null, "unidad-1");
    ModeloUnidad modeloUnidadExiste = generarMockModeloUnidad(1L, "unidad-1");
    modeloUnidadExiste.setActivo(false);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloUnidad.getModeloEjecucion()));

    BDDMockito.given(modeloUnidadRepository.findByModeloEjecucionIdAndUnidadGestionRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(modeloUnidadExiste));

    BDDMockito.given(modeloUnidadRepository.save(modeloUnidad))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Creamos el ModeloUnidad
    ModeloUnidad modeloUnidadCreado = service.create(modeloUnidad);

    // then: El ModeloUnidad se actualiza correctamente
    Assertions.assertThat(modeloUnidadCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloUnidadCreado.getId()).as("getId()").isEqualTo(modeloUnidadExiste.getId());
    Assertions.assertThat(modeloUnidadCreado.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloUnidadCreado.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(modeloUnidadExiste.getModeloEjecucion().getId());
    Assertions.assertThat(modeloUnidadCreado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(modeloUnidadExiste.getUnidadGestionRef());
    Assertions.assertThat(modeloUnidadCreado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void disable_ReturnsModeloUnidad() {
    // given: Un nuevo ModeloUnidad activo
    ModeloUnidad modeloUnidad = generarMockModeloUnidad(1L);

    BDDMockito.given(modeloUnidadRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloUnidad));
    BDDMockito.given(modeloUnidadRepository.save(ArgumentMatchers.<ModeloUnidad>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el ModeloUnidad
    ModeloUnidad modeloUnidadActualizado = service.disable(modeloUnidad.getId());

    // then: El ModeloUnidad se desactiva correctamente.
    Assertions.assertThat(modeloUnidadActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloUnidadActualizado.getId()).as("getId()").isEqualTo(modeloUnidad.getId());
    Assertions.assertThat(modeloUnidadActualizado.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloUnidadActualizado.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(modeloUnidad.getModeloEjecucion().getId());
    Assertions.assertThat(modeloUnidadActualizado.getUnidadGestionRef()).as("getUnidadGestionRef()")
        .isEqualTo(modeloUnidad.getUnidadGestionRef());
    Assertions.assertThat(modeloUnidadActualizado.getActivo()).as("getActivo()").isEqualTo(false);
  }

  @Test
  public void disable_WithIdNotExist_ThrowsModeloUnidadNotFoundException() {
    // given: Un id de un ModeloUnidad que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(modeloUnidadRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el ModeloUnidad
    // then: Lanza una excepcion porque el ModeloUnidad no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste)).isInstanceOf(ModeloUnidadNotFoundException.class);
  }

  @Test
  public void findById_ReturnsModeloUnidad() {
    // given: Un ModeloUnidad con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(modeloUnidadRepository.findById(idBuscado))
        .willReturn(Optional.of(generarMockModeloUnidad(idBuscado, "unidad-1")));

    // when: Buscamos el ModeloUnidad por su id
    ModeloUnidad modeloUnidad = service.findById(idBuscado);

    // then: el ModeloUnidad
    Assertions.assertThat(modeloUnidad).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloUnidad.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(modeloUnidad.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloUnidad.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()").isEqualTo(1L);
    Assertions.assertThat(modeloUnidad.getUnidadGestionRef()).as("getUnidadGestionRef()").isEqualTo("unidad-1");
    Assertions.assertThat(modeloUnidad.getActivo()).as("getActivo()").isEqualTo(true);

  }

  @Test
  public void findById_WithIdNotExist_ThrowsModeloUnidadNotFoundException() throws Exception {
    // given: Ningun ModeloUnidad con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(modeloUnidadRepository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ModeloUnidad por su id
    // then: lanza un ModeloUnidadNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado)).isInstanceOf(ModeloUnidadNotFoundException.class);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 ModeloUnidad
    List<ModeloUnidad> modelosUnidadModeloEjecucion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modelosUnidadModeloEjecucion.add(generarMockModeloUnidad(i));
    }

    BDDMockito.given(modeloUnidadRepository.findAll(ArgumentMatchers.<Specification<ModeloUnidad>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloUnidad>>() {
          @Override
          public Page<ModeloUnidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modelosUnidadModeloEjecucion.size() ? modelosUnidadModeloEjecucion.size() : toIndex;
            List<ModeloUnidad> content = modelosUnidadModeloEjecucion.subList(fromIndex, toIndex);
            Page<ModeloUnidad> page = new PageImpl<>(content, pageable, modelosUnidadModeloEjecucion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloUnidad> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los ModeloUnidad del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloUnidad modeloUnidad = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloUnidad.getUnidadGestionRef()).isEqualTo("ModeloUnidad" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllByModeloEjecucion_ReturnsPage() {
    // given: Una lista con 37 ModeloUnidad para el ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloUnidad> modelosUnidadModeloEjecucion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modelosUnidadModeloEjecucion.add(generarMockModeloUnidad(i));
    }

    BDDMockito.given(modeloUnidadRepository.findAll(ArgumentMatchers.<Specification<ModeloUnidad>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloUnidad>>() {
          @Override
          public Page<ModeloUnidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modelosUnidadModeloEjecucion.size() ? modelosUnidadModeloEjecucion.size() : toIndex;
            List<ModeloUnidad> content = modelosUnidadModeloEjecucion.subList(fromIndex, toIndex);
            Page<ModeloUnidad> page = new PageImpl<>(content, pageable, modelosUnidadModeloEjecucion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloUnidad> page = service.findAllByModeloEjecucion(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloUnidad del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloUnidad modeloUnidad = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloUnidad.getUnidadGestionRef()).isEqualTo("ModeloUnidad" + String.format("%03d", i));
    }
  }

  /**
   * Función que devuelve un objeto ModeloUnidad
   * 
   * @param id               id del ModeloUnidad
   * @param unidadGestionRef unidadGestionRef
   * @return el objeto ModeloUnidad
   */
  private ModeloUnidad generarMockModeloUnidad(Long id, String unidadGestionRef) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(id);
    modeloUnidad.setModeloEjecucion(modeloEjecucion);
    modeloUnidad.setUnidadGestionRef(unidadGestionRef);
    modeloUnidad.setActivo(true);

    return modeloUnidad;
  }

  /**
   * Función que devuelve un objeto ModeloUnidad
   * 
   * @param id id del ModeloUnidad
   * @return el objeto ModeloUnidad
   */
  private ModeloUnidad generarMockModeloUnidad(Long id) {
    return generarMockModeloUnidad(id, "ModeloUnidad" + String.format("%03d", id));
  }

}