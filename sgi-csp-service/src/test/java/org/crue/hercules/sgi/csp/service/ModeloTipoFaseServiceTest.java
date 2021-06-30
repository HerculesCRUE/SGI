package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ModeloTipoFaseNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.TipoFaseRepository;
import org.crue.hercules.sgi.csp.service.impl.ModeloTipoFaseServiceImpl;
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
 * ModeloTipoFaseServiceTest
 */
public class ModeloTipoFaseServiceTest extends BaseServiceTest {

  @Mock
  private ModeloTipoFaseRepository modeloTipoFaseRepository;

  @Mock
  private TipoFaseRepository tipoFaseRepository;

  @Mock
  private ModeloEjecucionRepository modeloEjecucionRepository;

  private ModeloTipoFaseService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ModeloTipoFaseServiceImpl(modeloTipoFaseRepository, tipoFaseRepository, modeloEjecucionRepository);
  }

  @Test
  public void create_ReturnsModeloTipoFase() {
    // given: Un nuevo ModeloTipoFase
    ModeloTipoFase modeloTipoFase = generarModeloTipoFaseConTipoFaseId(null);

    TipoFase tipoFase = generarMockTipoFase(2L, "TipoFase2");
    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoFase.getModeloEjecucion()));

    BDDMockito.given(tipoFaseRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(tipoFase));

    BDDMockito.given(modeloTipoFaseRepository.save(modeloTipoFase)).will((InvocationOnMock invocation) -> {
      ModeloTipoFase modeloTipoFaseCreado = invocation.getArgument(0);
      modeloTipoFaseCreado.setId(1L);
      return modeloTipoFaseCreado;
    });

    // when: Creamos el ModeloTipoFase
    ModeloTipoFase tipoFaseCreado = service.create(modeloTipoFase);

    // then: El ModeloTipoFase se crea correctamente
    Assertions.assertThat(tipoFaseCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoFaseCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(tipoFaseCreado.getConvocatoria()).as("getConvocatoria").isEqualTo(true);
    Assertions.assertThat(tipoFaseCreado.getProyecto()).as("getProyecto").isEqualTo(true);

  }

  @Test
  public void create_WithoutActivos_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoFase
    ModeloTipoFase modeloTipoFase = generarModeloTipoFaseConTipoFaseId(null);
    modeloTipoFase.setConvocatoria(false);
    modeloTipoFase.setProyecto(false);

    BDDMockito.given(modeloEjecucionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoFase.getModeloEjecucion()));

    TipoFase tipoFase = generarMockTipoFase(2L, "TipoFase2");

    BDDMockito.given(tipoFaseRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(tipoFase));

    // then: Se lanza una excepción
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoFase)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Debe seleccionarse si la fase está disponible para proyectos o convocatorias");

  }

  @Test
  public void create_WithoutModeloEjecucionId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoEnlace que ya tiene id
    ModeloTipoFase modeloTipoFase = generarModeloTipoFase(null);
    modeloTipoFase.getModeloEjecucion().setId(null);

    // when: Creamos el ModeloTipoEnlace
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoFase)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id ModeloEjecución no puede ser null");
  }

  @Test
  public void create_WithoutTipoFaseId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoEnlace que ya tiene id
    ModeloTipoFase modeloTipoFase = generarModeloTipoFase(null);
    modeloTipoFase.getTipoFase().setId(null);

    // when: Creamos el ModeloTipoEnlace
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(modeloTipoFase)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id TipoFase no puede ser null");
  }

  @Test
  public void update_ReturnsModeloTipoFase() {
    // given: Un nuevo ModeloTipoFase actualizado

    ModeloTipoFase modeloTipoFaseActualizado = generarModeloTipoFase(1L);
    modeloTipoFaseActualizado.setConvocatoria(false);

    ModeloTipoFase modeloTipoFase = generarModeloTipoFase(1L);

    BDDMockito.given(modeloTipoFaseRepository.findById(1L)).willReturn(Optional.of(modeloTipoFase));
    BDDMockito.given(modeloTipoFaseRepository.save(modeloTipoFase)).willReturn(modeloTipoFaseActualizado);

    // when: Actualizamos el ModeloTipoFase
    ModeloTipoFase modeloTipoFaseActualizadoCom = service.update(modeloTipoFaseActualizado);

    // then: El ModeloTipoFase se actualiza correctamente.
    Assertions.assertThat(modeloTipoFaseActualizadoCom.getId()).isEqualTo(1L);
    Assertions.assertThat(modeloTipoFaseActualizadoCom.getConvocatoria()).as("getActivoConvocatoria").isEqualTo(false);
  }

  @Test
  public void update_ThrowsModeloTipoFaseNotFoundException() {
    // given: Un nuevo modelo tipo Fase a actualizar
    ModeloTipoFase modeloTipoFase = generarModeloTipoFase(1L);

    // then: Lanza una excepcion porque el modelo tipo Fase no existe
    Assertions.assertThatThrownBy(() -> service.update(modeloTipoFase))
        .isInstanceOf(ModeloTipoFaseNotFoundException.class);

  }

  @Test
  public void update_withActivoFalse_ThrowsIllegalArgumentException() {
    // given: Un nuevo modelo tipo Fase a actualizar
    ModeloTipoFase modeloTipoFase = generarModeloTipoFase(1L);
    modeloTipoFase.setActivo(false);

    BDDMockito.given(modeloTipoFaseRepository.findById(1L)).willReturn(Optional.of(modeloTipoFase));

    Assertions.assertThatThrownBy(
        // when: Udpate con Activo false
        () -> service.update(modeloTipoFase))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El ModeloTipoFase tiene que estar activo");

  }

  @Test
  public void update_WithoutActivos_ThrowsIllegalArgumentException() {
    // given: Un nuevo ModeloTipoFase actualizado

    ModeloTipoFase modeloTipoFase = generarModeloTipoFase(1L);
    modeloTipoFase.setConvocatoria(false);
    modeloTipoFase.setProyecto(false);

    BDDMockito.given(modeloTipoFaseRepository.findById(1L)).willReturn(Optional.of(modeloTipoFase));

    Assertions.assertThatThrownBy(
        // when: Udpate con Convocatoria y Proyecto false
        () -> service.update(modeloTipoFase))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Debe seleccionarse si la fase está disponible para proyectos o convocatorias");
    ;
  }

  @Test
  public void find_WithExistingId_ReturnsModeloTipoFase() {

    // given: Entidad con un determinado Id
    ModeloTipoFase modeloTipoFase = generarModeloTipoFase(1L);
    BDDMockito.given(modeloTipoFaseRepository.findById(modeloTipoFase.getId())).willReturn(Optional.of(modeloTipoFase));

    // when: Se busca la entidad por ese Id
    ModeloTipoFase result = service.findById(modeloTipoFase.getId());

    // then: Se recupera la entidad con el Id
    Assertions.assertThat(result).isEqualTo(modeloTipoFase);
  }

  @Test
  public void find_WithNoExistingId_ThrowsNotFoundException() throws Exception {

    // given: No existe entidad con el id indicado
    Long id = 1L;
    BDDMockito.given(modeloTipoFaseRepository.findById(id)).willReturn(Optional.empty());

    // when: Se busca entidad con ese id
    // then: Se produce error porque no encuentra la entidad con ese Id
    Assertions.assertThatThrownBy(() -> service.findById(id)).isInstanceOf(ModeloTipoFaseNotFoundException.class);
  }

  @Test
  public void disable_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> service.disable(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void findAllByModeloEjecucion_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoFase para el ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoFase> modeloTipoFases = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoFases.add(generarModeloTipoFase(i));
    }

    BDDMockito.given(modeloTipoFaseRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoFase>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoFase>>() {
          @Override
          public Page<ModeloTipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoFases.size() ? modeloTipoFases.size() : toIndex;
            List<ModeloTipoFase> content = modeloTipoFases.subList(fromIndex, toIndex);
            Page<ModeloTipoFase> page = new PageImpl<>(content, pageable, modeloTipoFases.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoFase> page = service.findAllByModeloEjecucion(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoFase del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoFase modeloTipoFase = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoFase.getTipoFase().getNombre()).isEqualTo("TipoFase" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllByModeloEjecucionActivosConvocatoria_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoFase activos para convocatorias para el
    // ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoFase> tiposFaseModeloEjecucion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposFaseModeloEjecucion.add(generarModeloTipoFase(i));
    }

    BDDMockito.given(modeloTipoFaseRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoFase>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoFase>>() {
          @Override
          public Page<ModeloTipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposFaseModeloEjecucion.size() ? tiposFaseModeloEjecucion.size() : toIndex;
            List<ModeloTipoFase> content = tiposFaseModeloEjecucion.subList(fromIndex, toIndex);
            Page<ModeloTipoFase> page = new PageImpl<>(content, pageable, tiposFaseModeloEjecucion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoFase> page = service.findAllByModeloEjecucionActivosConvocatoria(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoFase del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoFase modeloTipoFase = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoFase.getTipoFase().getNombre()).isEqualTo("TipoFase" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllByModeloEjecucionActivosProyecto_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoFase activos para convocatorias para el
    // ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoFase> tiposFaseModeloEjecucion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposFaseModeloEjecucion.add(generarModeloTipoFase(i));
    }

    BDDMockito.given(modeloTipoFaseRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoFase>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoFase>>() {
          @Override
          public Page<ModeloTipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposFaseModeloEjecucion.size() ? tiposFaseModeloEjecucion.size() : toIndex;
            List<ModeloTipoFase> content = tiposFaseModeloEjecucion.subList(fromIndex, toIndex);
            Page<ModeloTipoFase> page = new PageImpl<>(content, pageable, tiposFaseModeloEjecucion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoFase> page = service.findAllByModeloEjecucionActivosProyecto(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoFase del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoFase modeloTipoFase = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoFase.getTipoFase().getNombre()).isEqualTo("TipoFase" + String.format("%03d", i));
    }
  }

  /**
   * Función que devuelve un objeto TipoFase
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoFase generarMockTipoFase(Long id, String nombre) {

    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id);
    tipoFase.setNombre(nombre);
    tipoFase.setDescripcion("descripcion-" + id);
    tipoFase.setActivo(Boolean.TRUE);

    return tipoFase;
  }

  /**
   * Función que devuelve un objeto ModeloTipoFase
   * 
   * @param id id del ModeloTipoFase
   * @return el objeto ModeloTipoFase
   */
  private ModeloTipoFase generarModeloTipoFase(Long id) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoFase modeloTipoFase = new ModeloTipoFase();
    modeloTipoFase.setId(id);
    modeloTipoFase.setModeloEjecucion(modeloEjecucion);
    modeloTipoFase.setTipoFase(generarMockTipoFase(id, "TipoFase" + String.format("%03d", id)));
    modeloTipoFase.setConvocatoria(true);
    modeloTipoFase.setProyecto(true);
    modeloTipoFase.setSolicitud(false);
    modeloTipoFase.setActivo(true);

    return modeloTipoFase;
  }

  /**
   * Función que devuelve un objeto ModeloTipoFase con un TipoFase con id
   * 
   * @param id id del ModeloTipoFase
   * @return el objeto ModeloTipoFase
   */
  private ModeloTipoFase generarModeloTipoFaseConTipoFaseId(Long id) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoFase modeloTipoFase = new ModeloTipoFase();
    modeloTipoFase.setId(id);
    modeloTipoFase.setModeloEjecucion(modeloEjecucion);
    modeloTipoFase.setTipoFase(generarMockTipoFase(1L, "TipoFase" + String.format("%03d", 1)));
    modeloTipoFase.setConvocatoria(true);
    modeloTipoFase.setProyecto(true);
    modeloTipoFase.setSolicitud(false);
    modeloTipoFase.setActivo(true);

    return modeloTipoFase;
  }
}
