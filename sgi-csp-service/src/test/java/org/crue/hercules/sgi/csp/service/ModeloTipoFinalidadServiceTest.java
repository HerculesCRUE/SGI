package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ModeloTipoFinalidadNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoFinalidadNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFinalidadRepository;
import org.crue.hercules.sgi.csp.repository.TipoFinalidadRepository;
import org.crue.hercules.sgi.csp.service.impl.ModeloTipoFinalidadServiceImpl;
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

public class ModeloTipoFinalidadServiceTest extends BaseServiceTest {

  @Mock
  private ModeloTipoFinalidadRepository modeloTipoFinalidadRepository;
  @Mock
  private ModeloEjecucionRepository modeloEjecucionRepository;
  @Mock
  private TipoFinalidadRepository tipoFinalidadRepository;

  private ModeloTipoFinalidadService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ModeloTipoFinalidadServiceImpl(modeloTipoFinalidadRepository, modeloEjecucionRepository,
        tipoFinalidadRepository);
  }

  @Test
  public void create_ReturnsModeloTipoFinalidad() {
    // given: new ModeloTipoFinalidad
    ModeloTipoFinalidad data = generarModeloTipoFinalidad(null, 1L, 1L);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(data.getModeloEjecucion()));
    BDDMockito.given(tipoFinalidadRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(data.getTipoFinalidad()));

    BDDMockito.given(modeloTipoFinalidadRepository.save(ArgumentMatchers.<ModeloTipoFinalidad>any()))
        .willAnswer(new Answer<ModeloTipoFinalidad>() {
          @Override
          public ModeloTipoFinalidad answer(InvocationOnMock invocation) throws Throwable {
            ModeloTipoFinalidad givenData = invocation.getArgument(0, ModeloTipoFinalidad.class);
            ModeloTipoFinalidad newData = new ModeloTipoFinalidad();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create ModeloTipoFinalidad
    ModeloTipoFinalidad created = service.create(data);

    // then: new ModeloTipoFinalidad is created
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isNotNull();
    Assertions.assertThat(created.getModeloEjecucion().getId()).isEqualTo(data.getModeloEjecucion().getId());
    Assertions.assertThat(created.getTipoFinalidad().getId()).isEqualTo(data.getTipoFinalidad().getId());
    Assertions.assertThat(created.getActivo()).isEqualTo(data.getActivo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a ModeloTipoFinalidad with id filled
    ModeloTipoFinalidad data = generarModeloTipoFinalidad(1L, 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoFinalidad
        () -> service.create(data))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithoutModeloEjecucionId_ThrowsIllegalArgumentException() {
    // given: a ModeloTipoFinalidad without ModeloEjecucionId
    ModeloTipoFinalidad data = generarModeloTipoFinalidad(null, null, 1L);

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoFinalidad
        () -> service.create(data))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithoutTipoFinalidadId_ThrowsIllegalArgumentException() {
    // given: a ModeloTipoFinalidad without TipoFinalidadId
    ModeloTipoFinalidad data = generarModeloTipoFinalidad(null, 1L, null);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(data.getModeloEjecucion()));

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoFinalidad
        () -> service.create(data))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithNoExistingModeloEjecucion_404() {
    // given: a ModeloTipoFinalidad with non existing ModeloEjecucion
    ModeloTipoFinalidad data = generarModeloTipoFinalidad(null, 1L, 1L);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoFinalidad
        () -> service.create(data))
        // then: throw exception as ModeloEjecucion is not found
        .isInstanceOf(ModeloEjecucionNotFoundException.class);
  }

  @Test
  public void create_WithNoExistingTipoFinalidad_404() {
    // given: a ModeloTipoFinalidad with non existing TipoFinalidad
    ModeloTipoFinalidad data = generarModeloTipoFinalidad(null, 1L, 1L);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(data.getModeloEjecucion()));
    BDDMockito.given(tipoFinalidadRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoFinalidad
        () -> service.create(data))
        // then: throw exception as TipoFinalidad is not found
        .isInstanceOf(TipoFinalidadNotFoundException.class);
  }

  @Test
  public void create_WithDisabledTipoFinalidad_ThrowsIllegalArgumentException() {
    // given: a ModeloTipoFinalidad with disabled TipoFinalidad
    ModeloTipoFinalidad data = generarModeloTipoFinalidad(null, 1L, 1L);
    data.getTipoFinalidad().setActivo(Boolean.FALSE);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(data.getModeloEjecucion()));
    BDDMockito.given(tipoFinalidadRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(data.getTipoFinalidad()));

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoFinalidad
        () -> service.create(data))
        // then: throw exception as ModeloTipoFinalidad is disabled
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithDuplicatedModeloEjecucionIdAndTipoEnlaceIdAndActivo_ThrowsIllegalArgumentException() {
    // given: a ModeloTipoFinalidad enabled with same
    // ModeloEjecucionId And TipoEnlaceId
    ModeloTipoFinalidad modeloTipoFinalidadExistente = generarModeloTipoFinalidad(1L, 1L, 1L);

    ModeloTipoFinalidad newData = generarModeloTipoFinalidad(null, 1L, 1L);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newData.getModeloEjecucion()));
    BDDMockito.given(tipoFinalidadRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newData.getTipoFinalidad()));
    BDDMockito.given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFinalidadExistente));

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoFinalidad
        () -> service.create(newData))
        // then: throw exception as same enabled ModeloTipoFinalidad exists
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithDuplicatedModeloEjecucionIdAndTipoEnlaceIdAndActivoFalse_ReturnEnableModeloTipoFinalidad() {
    // given: a ModeloTipoFinalidad disabled with same
    // ModeloEjecucionId And TipoEnlaceId
    ModeloTipoFinalidad modeloTipoFinalidadExistente = generarModeloTipoFinalidad(1L, 1L, 1L);
    modeloTipoFinalidadExistente.setActivo(Boolean.FALSE);
    ModeloTipoFinalidad newData = generarModeloTipoFinalidad(null, 1L, 1L);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newData.getModeloEjecucion()));
    BDDMockito.given(tipoFinalidadRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newData.getTipoFinalidad()));
    BDDMockito.given(modeloTipoFinalidadRepository.findByModeloEjecucionIdAndTipoFinalidadId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFinalidadExistente));

    BDDMockito.given(modeloTipoFinalidadRepository.save(ArgumentMatchers.<ModeloTipoFinalidad>any()))
        .willAnswer(new Answer<ModeloTipoFinalidad>() {
          @Override
          public ModeloTipoFinalidad answer(InvocationOnMock invocation) throws Throwable {
            ModeloTipoFinalidad givenData = invocation.getArgument(0, ModeloTipoFinalidad.class);
            return givenData;
          }
        });

    // when: create ModeloTipoFinalidad
    ModeloTipoFinalidad created = service.create(newData);

    // then: existing ModeloTipoFinalidad is enabled
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isNotNull();
    Assertions.assertThat(created.getModeloEjecucion().getId())
        .isEqualTo(modeloTipoFinalidadExistente.getModeloEjecucion().getId());
    Assertions.assertThat(created.getTipoFinalidad().getId()).isEqualTo(newData.getTipoFinalidad().getId());
    Assertions.assertThat(created.getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Test
  public void disable_WithExistingId_ReturnsModeloTipoFinalidad() {
    // given: existing ModeloTipoFinalidad
    ModeloTipoFinalidad data = generarModeloTipoFinalidad(1L, 1L, 1L);

    BDDMockito.given(modeloTipoFinalidadRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(data));

    BDDMockito.given(modeloTipoFinalidadRepository.save(ArgumentMatchers.<ModeloTipoFinalidad>any()))
        .willAnswer(new Answer<ModeloTipoFinalidad>() {
          @Override
          public ModeloTipoFinalidad answer(InvocationOnMock invocation) throws Throwable {
            ModeloTipoFinalidad givenData = invocation.getArgument(0, ModeloTipoFinalidad.class);
            givenData.setActivo(Boolean.FALSE);
            return givenData;
          }
        });

    // when: disable ModeloTipoFinalidad
    ModeloTipoFinalidad disabledData = service.disable(data.getId());

    // then: ModeloTipoFinalidad is disabled
    Assertions.assertThat(disabledData).isNotNull();
    Assertions.assertThat(disabledData.getId()).isNotNull();
    Assertions.assertThat(disabledData.getId()).isEqualTo(data.getId());
    Assertions.assertThat(disabledData.getModeloEjecucion().getId()).isEqualTo(data.getModeloEjecucion().getId());
    Assertions.assertThat(disabledData.getTipoFinalidad().getId()).isEqualTo(data.getTipoFinalidad().getId());
    Assertions.assertThat(disabledData.getActivo()).isEqualTo(Boolean.FALSE);
  }

  @Test
  public void disable_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    ModeloTipoFinalidad data = generarModeloTipoFinalidad(1L, 1L, 1L);

    BDDMockito.given(modeloTipoFinalidadRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing TipoFinalidad
        () -> service.disable(data.getId()))
        // then: NotFoundException is thrown
        .isInstanceOf(ModeloTipoFinalidadNotFoundException.class);
  }

  @Test
  public void findById_WithExistingId_ReturnsModeloTipoFinalidad() throws Exception {
    // given: existing ModeloTipoFinalidad
    ModeloTipoFinalidad givenData = generarModeloTipoFinalidad(1L, 1L, 1L);

    BDDMockito.given(modeloTipoFinalidadRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(givenData));

    // when: find by id ModeloTipoFinalidad
    ModeloTipoFinalidad data = service.findById(givenData.getId());

    // then: returns TipoFinalidad
    Assertions.assertThat(data).isNotNull();
    Assertions.assertThat(data.getId()).isNotNull();
    Assertions.assertThat(data.getId()).isEqualTo(givenData.getId());
    Assertions.assertThat(data.getModeloEjecucion().getId()).isEqualTo(givenData.getModeloEjecucion().getId());
    Assertions.assertThat(data.getTipoFinalidad().getId()).isEqualTo(givenData.getTipoFinalidad().getId());
    Assertions.assertThat(data.getActivo()).isEqualTo(givenData.getActivo());
  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    BDDMockito.given(modeloTipoFinalidadRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(ModeloTipoFinalidadNotFoundException.class);
  }

  @Test
  public void findAllByModeloEjecucion_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoFinalidad para el ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoFinalidad> modeloTipoFinalidades = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoFinalidades.add(generarModeloTipoFinalidad(i, idModeloEjecucion, i));
    }

    BDDMockito.given(modeloTipoFinalidadRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoFinalidad>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoFinalidad>>() {
          @Override
          public Page<ModeloTipoFinalidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoFinalidades.size() ? modeloTipoFinalidades.size() : toIndex;
            List<ModeloTipoFinalidad> content = modeloTipoFinalidades.subList(fromIndex, toIndex);
            Page<ModeloTipoFinalidad> page = new PageImpl<>(content, pageable, modeloTipoFinalidades.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoFinalidad> page = service.findAllByModeloEjecucion(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoFinalidad del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoFinalidad modeloTipoFinalidad = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoFinalidad.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(modeloTipoFinalidad.getModeloEjecucion().getId()).isEqualTo(idModeloEjecucion);
      Assertions.assertThat(modeloTipoFinalidad.getTipoFinalidad().getId()).isEqualTo(Long.valueOf(i));
    }
  }

  /**
   * Función que devuelve un objeto ModeloTipoFinalidad
   * 
   * @param modeloTipoFinalidadId
   * @param modeloEjecucionId
   * @param tipoFinalidadId
   * @return el objeto ModeloTipoFinalidad
   */
  private ModeloTipoFinalidad generarModeloTipoFinalidad(Long modeloTipoFinalidadId, Long modeloEjecucionId,
      Long tipoFinalidadId) {

    ModeloTipoFinalidad modeloTipoFinalidad = new ModeloTipoFinalidad();
    modeloTipoFinalidad.setId(modeloTipoFinalidadId);
    modeloTipoFinalidad
        .setModeloEjecucion(ModeloEjecucion.builder().id(modeloEjecucionId).activo(Boolean.TRUE).build());
    modeloTipoFinalidad.setTipoFinalidad(TipoFinalidad.builder().id(tipoFinalidadId).activo(Boolean.TRUE).build());
    modeloTipoFinalidad.setActivo(Boolean.TRUE);

    return modeloTipoFinalidad;
  }
}
