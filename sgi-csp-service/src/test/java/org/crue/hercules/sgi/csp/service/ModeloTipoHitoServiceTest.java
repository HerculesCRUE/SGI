package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ModeloTipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoHitoRepository;
import org.crue.hercules.sgi.csp.repository.TipoHitoRepository;
import org.crue.hercules.sgi.csp.service.impl.ModeloTipoHitoServiceImpl;
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

/**
 * ModeloTipoHitoServiceTest
 */
public class ModeloTipoHitoServiceTest extends BaseServiceTest {

  @Mock
  private ModeloTipoHitoRepository modeloTipoHitoRepository;
  @Mock
  private ModeloEjecucionRepository modeloEjecucionRepository;
  @Mock
  private TipoHitoRepository tipoHitoRepository;

  private ModeloTipoHitoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ModeloTipoHitoServiceImpl(modeloTipoHitoRepository, modeloEjecucionRepository, tipoHitoRepository);
  }

  @Test
  public void create_ReturnsModeloTipoHito() {
    // given: new ModeloTipoHito
    ModeloTipoHito data = generarModeloTipoHito(null, 1L, 1L);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(data.getModeloEjecucion()));
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(data.getTipoHito()));

    BDDMockito.given(modeloTipoHitoRepository.save(ArgumentMatchers.<ModeloTipoHito>any()))
        .willAnswer(new Answer<ModeloTipoHito>() {
          @Override
          public ModeloTipoHito answer(InvocationOnMock invocation) throws Throwable {
            ModeloTipoHito givenData = invocation.getArgument(0, ModeloTipoHito.class);
            ModeloTipoHito newData = new ModeloTipoHito();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create ModeloTipoHito
    ModeloTipoHito created = service.create(data);

    // then: new ModeloTipoHito is created
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isNotNull();
    Assertions.assertThat(created.getModeloEjecucion().getId()).isEqualTo(data.getModeloEjecucion().getId());
    Assertions.assertThat(created.getTipoHito().getId()).isEqualTo(data.getTipoHito().getId());
    Assertions.assertThat(created.getSolicitud()).isEqualTo(data.getSolicitud());
    Assertions.assertThat(created.getProyecto()).isEqualTo(data.getProyecto());
    Assertions.assertThat(created.getConvocatoria()).isEqualTo(data.getConvocatoria());
    Assertions.assertThat(created.getActivo()).isEqualTo(data.getActivo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a ModeloTipoHito with id filled
    ModeloTipoHito data = generarModeloTipoHito(1L, 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoHito
        () -> service.create(data))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithoutModeloEjecucionId_ThrowsIllegalArgumentException() {
    // given: a ModeloTipoHito without ModeloEjecucionId
    ModeloTipoHito data = generarModeloTipoHito(null, null, 1L);

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoHito
        () -> service.create(data))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithoutTipoHitoId_ThrowsIllegalArgumentException() {
    // given: a ModeloTipoHito without TipoHitoId
    ModeloTipoHito data = generarModeloTipoHito(null, 1L, null);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(data.getModeloEjecucion()));

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoHito
        () -> service.create(data))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithNoExistingModeloEjecucion_404() {
    // given: a ModeloTipoHito with non existing ModeloEjecucion
    ModeloTipoHito data = generarModeloTipoHito(null, 1L, 1L);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoHito
        () -> service.create(data))
        // then: throw exception as ModeloEjecucion is not found
        .isInstanceOf(ModeloEjecucionNotFoundException.class);
  }

  @Test
  public void create_WithNoExistingTipoHito_404() {
    // given: a ModeloTipoHito with non existing TipoHito
    ModeloTipoHito data = generarModeloTipoHito(null, 1L, 1L);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(data.getModeloEjecucion()));
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoHito
        () -> service.create(data))
        // then: throw exception as TipoHito is not found
        .isInstanceOf(TipoHitoNotFoundException.class);
  }

  @Test
  public void create_WithDisabledTipoHito_ThrowsIllegalArgumentException() {
    // given: a ModeloTipoHito with disabled TipoHito
    ModeloTipoHito data = generarModeloTipoHito(null, 1L, 1L);
    data.getTipoHito().setActivo(Boolean.FALSE);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(data.getModeloEjecucion()));
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(data.getTipoHito()));

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoHito
        () -> service.create(data))
        // then: throw exception as ModeloTipoHito is disabled
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithDuplicatedModeloEjecucionIdAndTipoEnlaceIdAndActivo_ThrowsIllegalArgumentException() {
    // given: a ModeloTipoHito enabled with same
    // ModeloEjecucionId And TipoEnlaceId
    ModeloTipoHito modeloTipoHitoExistente = generarModeloTipoHito(1L, 1L, 1L);

    ModeloTipoHito newData = generarModeloTipoHito(null, 1L, 1L);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newData.getModeloEjecucion()));
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newData.getTipoHito()));
    BDDMockito.given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoHitoExistente));

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoHito
        () -> service.create(newData))
        // then: throw exception as same enabled ModeloTipoHito exists
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithDuplicatedModeloEjecucionIdAndTipoEnlaceIdAndActivoFalse_ReturnEnableModeloTipoHito() {
    // given: a ModeloTipoHito disabled with same
    // ModeloEjecucionId And TipoEnlaceId
    ModeloTipoHito modeloTipoHitoExistente = generarModeloTipoHito(1L, 1L, 1L);
    modeloTipoHitoExistente.setActivo(Boolean.FALSE);
    ModeloTipoHito newData = generarModeloTipoHito(null, 1L, 1L);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newData.getModeloEjecucion()));
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newData.getTipoHito()));
    BDDMockito.given(modeloTipoHitoRepository.findByModeloEjecucionIdAndTipoHitoId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoHitoExistente));

    BDDMockito.given(modeloTipoHitoRepository.save(ArgumentMatchers.<ModeloTipoHito>any()))
        .willAnswer(new Answer<ModeloTipoHito>() {
          @Override
          public ModeloTipoHito answer(InvocationOnMock invocation) throws Throwable {
            ModeloTipoHito givenData = invocation.getArgument(0, ModeloTipoHito.class);
            return givenData;
          }
        });

    // when: create ModeloTipoHito
    ModeloTipoHito created = service.create(newData);

    // then: existing ModeloTipoHito is enabled
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isNotNull();
    Assertions.assertThat(created.getModeloEjecucion().getId())
        .isEqualTo(modeloTipoHitoExistente.getModeloEjecucion().getId());
    Assertions.assertThat(created.getTipoHito().getId()).isEqualTo(newData.getTipoHito().getId());
    Assertions.assertThat(created.getSolicitud()).isEqualTo(newData.getSolicitud());
    Assertions.assertThat(created.getProyecto()).isEqualTo(newData.getProyecto());
    Assertions.assertThat(created.getConvocatoria()).isEqualTo(newData.getConvocatoria());
    Assertions.assertThat(created.getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Test
  public void create_WithNoOption_ThrowsIllegalArgumentException() {
    // given: a ModeloTipoHito with no option selected
    ModeloTipoHito data = generarModeloTipoHito(null, 1L, 1L);
    data.setSolicitud(Boolean.FALSE);
    data.setProyecto(Boolean.FALSE);
    data.setConvocatoria(Boolean.FALSE);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(data.getModeloEjecucion()));
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(data.getTipoHito()));

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoHito
        () -> service.create(data))
        // then: throw exception as no option is selected
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithExistingId_ReturnsModeloTipoHito() {
    // given: existing ModeloTipoHito
    ModeloTipoHito modeloTipoHitoExistente = generarModeloTipoHito(1L, 1L, 1L);
    ModeloTipoHito modeloTipoHito = generarModeloTipoHito(1L, 1L, 1L);
    modeloTipoHito.setSolicitud(Boolean.FALSE);
    modeloTipoHito.setProyecto(Boolean.TRUE);
    modeloTipoHito.setConvocatoria(Boolean.FALSE);

    BDDMockito.given(modeloTipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoHitoExistente));

    BDDMockito.given(modeloTipoHitoRepository.save(ArgumentMatchers.<ModeloTipoHito>any()))
        .willAnswer(new Answer<ModeloTipoHito>() {
          @Override
          public ModeloTipoHito answer(InvocationOnMock invocation) throws Throwable {
            ModeloTipoHito givenModeloTipoHito = invocation.getArgument(0, ModeloTipoHito.class);
            givenModeloTipoHito.setSolicitud(modeloTipoHito.getSolicitud());
            givenModeloTipoHito.setProyecto(modeloTipoHito.getProyecto());
            givenModeloTipoHito.setConvocatoria(modeloTipoHito.getConvocatoria());
            return givenModeloTipoHito;
          }
        });

    // when: update ModeloTipoHito
    ModeloTipoHito updated = service.update(modeloTipoHito);

    // then: new ModeloTipoHito is updated
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(modeloTipoHitoExistente.getId());
    Assertions.assertThat(updated.getModeloEjecucion().getId())
        .isEqualTo(modeloTipoHitoExistente.getModeloEjecucion().getId());
    Assertions.assertThat(updated.getTipoHito().getId()).isEqualTo(modeloTipoHitoExistente.getTipoHito().getId());
    Assertions.assertThat(updated.getSolicitud()).isEqualTo(modeloTipoHito.getSolicitud());
    Assertions.assertThat(updated.getProyecto()).isEqualTo(modeloTipoHito.getProyecto());
    Assertions.assertThat(updated.getConvocatoria()).isEqualTo(modeloTipoHito.getConvocatoria());
    Assertions.assertThat(updated.getActivo()).isEqualTo(modeloTipoHitoExistente.getActivo());

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: a ModeloTipoHito without
    ModeloTipoHito data = generarModeloTipoHito(null, 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: update ModeloTipoHito
        () -> service.update(data))
        // then: throw exception as no id was provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_NoExistingModeloTipoHito_404() {
    // given: No existing TipoHito
    ModeloTipoHito data = generarModeloTipoHito(99L, 1L, 1L);

    BDDMockito.given(modeloTipoHitoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ModeloTipoHito
        () -> service.update(data))
        // then: throw exception as ModeloTipoHito is not found
        .isInstanceOf(ModeloTipoHitoNotFoundException.class);
  }

  @Test
  public void update_DisabledModeloTipoHito_ThrowsIllegalArgumentException() {
    // given: a ModeloTipoHito to be updated is disabled
    ModeloTipoHito modeloTipoHitoExistente = generarModeloTipoHito(1L, 1L, 1L);
    modeloTipoHitoExistente.setActivo(Boolean.FALSE);
    ModeloTipoHito modeloTipoHito = generarModeloTipoHito(1L, 1L, 1L);
    modeloTipoHito.setSolicitud(Boolean.FALSE);
    modeloTipoHito.setProyecto(Boolean.TRUE);
    modeloTipoHito.setConvocatoria(Boolean.FALSE);

    BDDMockito.given(modeloTipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoHitoExistente));

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoHito
        () -> service.update(modeloTipoHito))
        // then: throw exception as no option is selected
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithNoOption_ThrowsIllegalArgumentException() {
    // given: a ModeloTipoHito to be updated with no option selected
    ModeloTipoHito modeloTipoHitoExistente = generarModeloTipoHito(1L, 1L, 1L);
    ModeloTipoHito modeloTipoHito = generarModeloTipoHito(1L, 1L, 1L);
    modeloTipoHito.setSolicitud(Boolean.FALSE);
    modeloTipoHito.setProyecto(Boolean.FALSE);
    modeloTipoHito.setConvocatoria(Boolean.FALSE);

    BDDMockito.given(modeloTipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoHitoExistente));

    Assertions.assertThatThrownBy(
        // when: create ModeloTipoHito
        () -> service.update(modeloTipoHito))
        // then: throw exception as no option is selected
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void disable_WithExistingId_ReturnsModeloTipoHito() {
    // given: existing ModeloTipoHito
    ModeloTipoHito data = generarModeloTipoHito(1L, 1L, 1L);

    BDDMockito.given(modeloTipoHitoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(data));

    BDDMockito.given(modeloTipoHitoRepository.save(ArgumentMatchers.<ModeloTipoHito>any()))
        .willAnswer(new Answer<ModeloTipoHito>() {
          @Override
          public ModeloTipoHito answer(InvocationOnMock invocation) throws Throwable {
            ModeloTipoHito givenData = invocation.getArgument(0, ModeloTipoHito.class);
            givenData.setActivo(Boolean.FALSE);
            return givenData;
          }
        });

    // when: disable ModeloTipoHito
    ModeloTipoHito disabledData = service.disable(data.getId());

    // then: ModeloTipoHito is disabled
    Assertions.assertThat(disabledData).isNotNull();
    Assertions.assertThat(disabledData.getId()).isNotNull();
    Assertions.assertThat(disabledData.getId()).isEqualTo(data.getId());
    Assertions.assertThat(disabledData.getModeloEjecucion().getId()).isEqualTo(data.getModeloEjecucion().getId());
    Assertions.assertThat(disabledData.getTipoHito().getId()).isEqualTo(data.getTipoHito().getId());
    Assertions.assertThat(disabledData.getSolicitud()).isEqualTo(data.getSolicitud());
    Assertions.assertThat(disabledData.getProyecto()).isEqualTo(data.getProyecto());
    Assertions.assertThat(disabledData.getConvocatoria()).isEqualTo(data.getConvocatoria());
    Assertions.assertThat(disabledData.getActivo()).isEqualTo(Boolean.FALSE);
  }

  @Test
  public void disable_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    ModeloTipoHito data = generarModeloTipoHito(1L, 1L, 1L);

    BDDMockito.given(modeloTipoHitoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing TipoFinalidad
        () -> service.disable(data.getId()))
        // then: NotFoundException is thrown
        .isInstanceOf(ModeloTipoHitoNotFoundException.class);
  }

  @Test
  public void findById_WithExistingId_ReturnsModeloTipoHito() throws Exception {
    // given: existing ModeloTipoHito
    ModeloTipoHito givenData = generarModeloTipoHito(1L, 1L, 1L);

    BDDMockito.given(modeloTipoHitoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(givenData));

    // when: find by id ModeloTipoHito
    ModeloTipoHito data = service.findById(givenData.getId());

    // then: returns TipoFinalidad
    Assertions.assertThat(data).isNotNull();
    Assertions.assertThat(data.getId()).isNotNull();
    Assertions.assertThat(data.getId()).isEqualTo(givenData.getId());
    Assertions.assertThat(data.getModeloEjecucion().getId()).isEqualTo(givenData.getModeloEjecucion().getId());
    Assertions.assertThat(data.getTipoHito().getId()).isEqualTo(givenData.getTipoHito().getId());
    Assertions.assertThat(data.getSolicitud()).isEqualTo(givenData.getSolicitud());
    Assertions.assertThat(data.getProyecto()).isEqualTo(givenData.getProyecto());
    Assertions.assertThat(data.getConvocatoria()).isEqualTo(givenData.getConvocatoria());
    Assertions.assertThat(data.getActivo()).isEqualTo(givenData.getActivo());
  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    BDDMockito.given(modeloTipoHitoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(ModeloTipoHitoNotFoundException.class);
  }

  @Test
  public void findAllByModeloEjecucion_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoHito para el ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoHito> modeloTipoHitos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoHitos.add(generarModeloTipoHito(i, idModeloEjecucion, i));
    }

    BDDMockito.given(modeloTipoHitoRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoHito>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoHito>>() {
          @Override
          public Page<ModeloTipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoHitos.size() ? modeloTipoHitos.size() : toIndex;
            List<ModeloTipoHito> content = modeloTipoHitos.subList(fromIndex, toIndex);
            Page<ModeloTipoHito> page = new PageImpl<>(content, pageable, modeloTipoHitos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoHito> page = service.findAllByModeloEjecucion(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoHito del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoHito modeloTipoHito = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoHito.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(modeloTipoHito.getModeloEjecucion().getId()).isEqualTo(idModeloEjecucion);
      Assertions.assertThat(modeloTipoHito.getTipoHito().getId()).isEqualTo(Long.valueOf(i));
    }
  }

  @Test
  public void findAllByModeloEjecucionActivosConvocatoria_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoHito activos para convocatorias para el
    // ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoHito> modeloTipoHitos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoHitos.add(generarModeloTipoHito(i, idModeloEjecucion, i));
    }

    BDDMockito.given(modeloTipoHitoRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoHito>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoHito>>() {
          @Override
          public Page<ModeloTipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoHitos.size() ? modeloTipoHitos.size() : toIndex;
            List<ModeloTipoHito> content = modeloTipoHitos.subList(fromIndex, toIndex);
            Page<ModeloTipoHito> page = new PageImpl<>(content, pageable, modeloTipoHitos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoHito> page = service.findAllByModeloEjecucionActivosConvocatoria(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoHito del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoHito modeloTipoHito = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoHito.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(modeloTipoHito.getModeloEjecucion().getId()).isEqualTo(idModeloEjecucion);
      Assertions.assertThat(modeloTipoHito.getTipoHito().getId()).isEqualTo(Long.valueOf(i));
    }
  }

  @Test
  public void findAllByModeloEjecucionActivosProyecto_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoHito activos para convocatorias para el
    // ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoHito> modeloTipoHitos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoHitos.add(generarModeloTipoHito(i, idModeloEjecucion, i));
    }

    BDDMockito.given(modeloTipoHitoRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoHito>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoHito>>() {
          @Override
          public Page<ModeloTipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoHitos.size() ? modeloTipoHitos.size() : toIndex;
            List<ModeloTipoHito> content = modeloTipoHitos.subList(fromIndex, toIndex);
            Page<ModeloTipoHito> page = new PageImpl<>(content, pageable, modeloTipoHitos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoHito> page = service.findAllByModeloEjecucionActivosProyecto(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoHito del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoHito modeloTipoHito = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoHito.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(modeloTipoHito.getModeloEjecucion().getId()).isEqualTo(idModeloEjecucion);
      Assertions.assertThat(modeloTipoHito.getTipoHito().getId()).isEqualTo(Long.valueOf(i));
    }
  }

  @Test
  public void findAllByModeloEjecucionActivosSolicitud_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoHito activos para convocatorias para el
    // ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoHito> modeloTipoHitos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoHitos.add(generarModeloTipoHito(i, idModeloEjecucion, i));
    }

    BDDMockito.given(modeloTipoHitoRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoHito>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoHito>>() {
          @Override
          public Page<ModeloTipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoHitos.size() ? modeloTipoHitos.size() : toIndex;
            List<ModeloTipoHito> content = modeloTipoHitos.subList(fromIndex, toIndex);
            Page<ModeloTipoHito> page = new PageImpl<>(content, pageable, modeloTipoHitos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoHito> page = service.findAllByModeloEjecucionActivosSolicitud(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoHito del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoHito modeloTipoHito = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoHito.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(modeloTipoHito.getModeloEjecucion().getId()).isEqualTo(idModeloEjecucion);
      Assertions.assertThat(modeloTipoHito.getTipoHito().getId()).isEqualTo(Long.valueOf(i));
    }
  }

  /**
   * Función que devuelve un objeto TipoHito
   * 
   * @param id id del TipoHito
   * @return el objeto TipoHito
   */
  public TipoHito generarMockTipoHito(Long id) {
    return generarMockTipoHito(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoHito
   * 
   * @param id id del TipoHito
   * @return el objeto TipoHito
   */
  public TipoHito generarMockTipoHito(Long id, String nombre) {
    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id);
    tipoHito.setNombre(nombre);
    tipoHito.setDescripcion("descripcion-" + id);
    tipoHito.setActivo(Boolean.TRUE);
    return tipoHito;
  }

  /**
   * Función que devuelve un objeto ModeloTipoHito
   * 
   * @param modeloTipoHitoId
   * @param modeloEjecucionId
   * @param tipoHitoId
   * @return el objeto ModeloTipoHito
   */
  private ModeloTipoHito generarModeloTipoHito(Long modeloTipoHitoId, Long modeloEjecucionId, Long tipoHitoId) {

    ModeloTipoHito modeloTipoHito = new ModeloTipoHito();
    modeloTipoHito.setId(modeloTipoHitoId);
    modeloTipoHito.setModeloEjecucion(ModeloEjecucion.builder().id(modeloEjecucionId).activo(Boolean.TRUE).build());
    modeloTipoHito.setTipoHito(TipoHito.builder().id(tipoHitoId).activo(Boolean.TRUE).build());
    modeloTipoHito.setSolicitud(Boolean.TRUE);
    modeloTipoHito.setProyecto(Boolean.TRUE);
    modeloTipoHito.setConvocatoria(Boolean.TRUE);
    modeloTipoHito.setActivo(Boolean.TRUE);

    return modeloTipoHito;
  }

}
