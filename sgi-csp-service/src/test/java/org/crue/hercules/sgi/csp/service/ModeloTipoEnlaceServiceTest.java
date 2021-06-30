package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ModeloTipoEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.TipoEnlaceRepository;
import org.crue.hercules.sgi.csp.service.impl.ModeloTipoEnlaceServiceImpl;
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

public class ModeloTipoEnlaceServiceTest extends BaseServiceTest {

  @Mock
  private ModeloEjecucionRepository modeloEjecucionRepository;

  @Mock
  private ModeloTipoEnlaceRepository modeloTipoEnlaceRepository;

  @Mock
  private TipoEnlaceRepository tipoEnlaceRepository;

  private ModeloTipoEnlaceService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ModeloTipoEnlaceServiceImpl(modeloEjecucionRepository, modeloTipoEnlaceRepository,
        tipoEnlaceRepository);
  }

  @Test
  public void create_ReturnsModeloTipoEnlace() {
    // given: Un nuevo ModeloTipoEnlace
    ModeloTipoEnlace modeloTipoEnlace = generarMockModeloTipoEnlace(null, 1L);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoEnlace.getModeloEjecucion()));

    BDDMockito.given(tipoEnlaceRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoEnlace.getTipoEnlace()));

    BDDMockito.given(modeloTipoEnlaceRepository.findByModeloEjecucionIdAndTipoEnlaceId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    BDDMockito.given(modeloTipoEnlaceRepository.save(modeloTipoEnlace)).will((InvocationOnMock invocation) -> {
      ModeloTipoEnlace modeloTipoEnlaceCreado = invocation.getArgument(0);
      modeloTipoEnlaceCreado.setId(1L);
      return modeloTipoEnlaceCreado;
    });

    // when: Creamos el ModeloTipoEnlace
    ModeloTipoEnlace modeloTipoEnlaceCreado = service.create(modeloTipoEnlace);

    // then: El ModeloTipoEnlace se crea correctamente
    Assertions.assertThat(modeloTipoEnlaceCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceCreado.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceCreado.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(modeloTipoEnlace.getModeloEjecucion().getId());
    Assertions.assertThat(modeloTipoEnlaceCreado.getTipoEnlace()).as("getTipoEnlace()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceCreado.getTipoEnlace().getId()).as("getTipoEnlace().getId()")
        .isEqualTo(modeloTipoEnlace.getTipoEnlace().getId());
    Assertions.assertThat(modeloTipoEnlaceCreado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoEnlace que ya tiene id
    ModeloTipoEnlace modeloTipoEnlace = generarMockModeloTipoEnlace(1L);

    // when: Creamos el ModeloTipoEnlace
    // then: Lanza una excepcion porque el ModeloTipoEnlace ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoEnlace)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithoutModeloEjecucionId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoEnlace con un ModeloEjecucion sin id
    ModeloTipoEnlace modeloTipoEnlace = generarMockModeloTipoEnlace(null);
    modeloTipoEnlace.getModeloEjecucion().setId(null);

    // when: Creamos el ModeloTipoEnlace
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoEnlace)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithoutTipoEnlaceId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoEnlace con un TipoEnlace sin id
    ModeloTipoEnlace modeloTipoEnlace = generarMockModeloTipoEnlace(null);
    modeloTipoEnlace.getTipoEnlace().setId(null);

    // when: Creamos el ModeloTipoEnlace
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoEnlace)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithNoExistingModeloEjecucion_ThrowsModeloEjecucionNotFoundException() {
    // given: Un nuevo ModeloTipoEnlace con un ModeleoEjecucion que no existe
    ModeloTipoEnlace modeloTipoEnlace = generarMockModeloTipoEnlace(null, 1L);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Creamos el ModeloTipoEnlace
    // then: Lanza una excepcion porque el ModeleoEjecucion no existe id
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoEnlace))
        .isInstanceOf(ModeloEjecucionNotFoundException.class);
  }

  @Test
  public void create_WithNoExistingTipoEnlace_ThrowsTipoEnlaceNotFoundException() {
    // given: Un nuevo ModeloTipoEnlace que ya tiene id
    ModeloTipoEnlace modeloTipoEnlace = generarMockModeloTipoEnlace(null, 1L);
    modeloTipoEnlace.getTipoEnlace().setActivo(false);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoEnlace.getModeloEjecucion()));

    BDDMockito.given(tipoEnlaceRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Creamos el ModeloTipoEnlace
    // then: Lanza una excepcion porque el TipoEnlace no existe id
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoEnlace))
        .isInstanceOf(TipoEnlaceNotFoundException.class);
  }

  @Test
  public void create_WithDisabledTipoEnlace_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoEnlace con un TipoEnlace que no esta activo
    ModeloTipoEnlace modeloTipoEnlace = generarMockModeloTipoEnlace(null, 1L);
    modeloTipoEnlace.getTipoEnlace().setActivo(false);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoEnlace.getModeloEjecucion()));

    BDDMockito.given(tipoEnlaceRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoEnlace.getTipoEnlace()));

    // when: Creamos el ModeloTipoEnlace
    // then: Lanza una excepcion porque el TipoEnlace no esta activo
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoEnlace)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithDuplicatedModeloEjecucionIdAndTipoEnlaceIdAndActivo_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoEnlace con una combinacion de ModeloEjecucionId y
    // TipoEnlaceId que ya existe y esta activo
    ModeloTipoEnlace modeloTipoEnlace = generarMockModeloTipoEnlace(null, 1L);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoEnlace.getModeloEjecucion()));

    BDDMockito.given(tipoEnlaceRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoEnlace.getTipoEnlace()));

    BDDMockito.given(modeloTipoEnlaceRepository.findByModeloEjecucionIdAndTipoEnlaceId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(generarMockModeloTipoEnlace(1L, 1L)));

    // when: Creamos el ModeloTipoEnlace
    // then: Lanza una excepcion porque ya existe esa relacion activa
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoEnlace)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithDuplicatedModeloEjecucionIdAndTipoEnlaceIdAndActivoFalse_ReturnEnableModeloTipoEnlace() {
    // given: Un nuevo ModeloTipoEnlace con una combinacion de ModeloEjecucionId y
    // TipoEnlaceId que ya existe y no esta activo
    ModeloTipoEnlace modeloTipoEnlace = generarMockModeloTipoEnlace(null, 1L);
    ModeloTipoEnlace modeloTipoEnlaceExiste = generarMockModeloTipoEnlace(1L, 1L);
    modeloTipoEnlaceExiste.setActivo(false);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoEnlace.getModeloEjecucion()));

    BDDMockito.given(tipoEnlaceRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoEnlace.getTipoEnlace()));

    BDDMockito.given(modeloTipoEnlaceRepository.findByModeloEjecucionIdAndTipoEnlaceId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoEnlaceExiste));

    BDDMockito.given(modeloTipoEnlaceRepository.save(modeloTipoEnlace))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Creamos el ModeloTipoEnlace
    ModeloTipoEnlace modeloTipoEnlaceCreado = service.create(modeloTipoEnlace);

    // then: El ModeloTipoEnlace se actualiza correctamente
    Assertions.assertThat(modeloTipoEnlaceCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceCreado.getId()).as("getId()").isEqualTo(modeloTipoEnlaceExiste.getId());
    Assertions.assertThat(modeloTipoEnlaceCreado.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceCreado.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(modeloTipoEnlaceExiste.getModeloEjecucion().getId());
    Assertions.assertThat(modeloTipoEnlaceCreado.getTipoEnlace()).as("getTipoEnlace()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceCreado.getTipoEnlace().getId()).as("getTipoEnlace().getId()")
        .isEqualTo(modeloTipoEnlaceExiste.getTipoEnlace().getId());
    Assertions.assertThat(modeloTipoEnlaceCreado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void disable_ReturnsModeloTipoEnlace() {
    // given: Un nuevo ModeloTipoEnlace activo
    ModeloTipoEnlace modeloTipoEnlace = generarMockModeloTipoEnlace(1L);

    BDDMockito.given(modeloTipoEnlaceRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoEnlace));
    BDDMockito.given(modeloTipoEnlaceRepository.save(ArgumentMatchers.<ModeloTipoEnlace>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el ModeloTipoEnlace
    ModeloTipoEnlace modeloTipoEnlaceActualizado = service.disable(modeloTipoEnlace.getId());

    // then: El ModeloTipoEnlace se desactiva correctamente.
    Assertions.assertThat(modeloTipoEnlaceActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceActualizado.getId()).as("getId()").isEqualTo(modeloTipoEnlace.getId());
    Assertions.assertThat(modeloTipoEnlaceActualizado.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceActualizado.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(modeloTipoEnlace.getModeloEjecucion().getId());
    Assertions.assertThat(modeloTipoEnlaceActualizado.getTipoEnlace()).as("getTipoEnlace()").isNotNull();
    Assertions.assertThat(modeloTipoEnlaceActualizado.getTipoEnlace().getId()).as("getTipoEnlace().getId()")
        .isEqualTo(modeloTipoEnlace.getTipoEnlace().getId());
    Assertions.assertThat(modeloTipoEnlaceActualizado.getActivo()).as("getActivo()").isEqualTo(false);

  }

  @Test
  public void disable_WithIdNotExist_ThrowsModeloTipoEnlaceNotFoundException() {
    // given: Un id de un ModeloTipoEnlace que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(modeloTipoEnlaceRepository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el ModeloTipoEnlace
    // then: Lanza una excepcion porque el ModeloTipoEnlace no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste))
        .isInstanceOf(ModeloTipoEnlaceNotFoundException.class);
  }

  @Test
  public void findById_ReturnsModeloTipoEnlace() {
    // given: Un ModeloTipoEnlace con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(modeloTipoEnlaceRepository.findById(idBuscado))
        .willReturn(Optional.of(generarMockModeloTipoEnlace(idBuscado)));

    // when: Buscamos el ModeloTipoEnlace por su id
    ModeloTipoEnlace modeloTipoEnlace = service.findById(idBuscado);

    // then: el ModeloTipoEnlace
    Assertions.assertThat(modeloTipoEnlace).as("isNotNull()").isNotNull();
    Assertions.assertThat(modeloTipoEnlace.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(modeloTipoEnlace.getModeloEjecucion()).as("getModeloEjecucion()").isNotNull();
    Assertions.assertThat(modeloTipoEnlace.getModeloEjecucion().getId()).as("getModeloEjecucion().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(modeloTipoEnlace.getTipoEnlace()).as("getTipoEnlace()").isNotNull();
    Assertions.assertThat(modeloTipoEnlace.getTipoEnlace().getId()).as("getTipoEnlace().getId()").isEqualTo(1L);
    Assertions.assertThat(modeloTipoEnlace.getActivo()).as("getActivo()").isEqualTo(true);

  }

  @Test
  public void findById_WithIdNotExist_ThrowsModeloTipoEnlaceNotFoundException() throws Exception {
    // given: Ningun ModeloTipoEnlace con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(modeloTipoEnlaceRepository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ModeloTipoEnlace por su id
    // then: lanza un ModeloTipoEnlaceNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ModeloTipoEnlaceNotFoundException.class);
  }

  @Test
  public void findAllByModeloEjecucion_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoEnlace para el ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoEnlace> modeloTipoEnlaces = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoEnlaces.add(generarMockModeloTipoEnlace(i));
    }

    BDDMockito.given(modeloTipoEnlaceRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoEnlace>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoEnlace>>() {
          @Override
          public Page<ModeloTipoEnlace> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoEnlaces.size() ? modeloTipoEnlaces.size() : toIndex;
            List<ModeloTipoEnlace> content = modeloTipoEnlaces.subList(fromIndex, toIndex);
            Page<ModeloTipoEnlace> page = new PageImpl<>(content, pageable, modeloTipoEnlaces.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoEnlace> page = service.findAllByModeloEjecucion(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los TipoEnlace del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoEnlace modeloTipoEnlace = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoEnlace.getTipoEnlace().getNombre()).isEqualTo("nombre-" + i);
    }
  }

  /**
   * Función que devuelve un objeto TipoEnlace
   * 
   * @param id
   * @param activo
   * @return TipoEnlace
   */
  private TipoEnlace generarMockTipoEnlace(Long id, Boolean activo) {
    return TipoEnlace.builder().id(id).nombre("nombre-" + id).descripcion("descripcion-" + id).activo(activo).build();
  }

  /**
   * Función que devuelve un objeto ModeloTipoEnlace
   * 
   * @param id id del ModeloTipoEnlace
   * @return el objeto ModeloTipoEnlace
   */
  private ModeloTipoEnlace generarMockModeloTipoEnlace(Long id) {
    return generarMockModeloTipoEnlace(id, id);
  }

  /**
   * Función que devuelve un objeto ModeloTipoEnlace
   * 
   * @param id id del ModeloTipoEnlace
   * @param id idTipoEnlace del TipoEnlace
   * @return el objeto ModeloTipoEnlace
   */
  private ModeloTipoEnlace generarMockModeloTipoEnlace(Long id, Long idTipoEnlace) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoEnlace modeloTipoFinalidad = new ModeloTipoEnlace();
    modeloTipoFinalidad.setId(id);
    modeloTipoFinalidad.setModeloEjecucion(modeloEjecucion);
    modeloTipoFinalidad.setTipoEnlace(generarMockTipoEnlace(idTipoEnlace, true));
    modeloTipoFinalidad.setActivo(true);

    return modeloTipoFinalidad;
  }

}
