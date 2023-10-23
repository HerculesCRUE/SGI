package org.crue.hercules.sgi.eti.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.EstadoMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.crue.hercules.sgi.eti.repository.EstadoMemoriaRepository;
import org.crue.hercules.sgi.eti.service.impl.EstadoMemoriaServiceImpl;
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
 * EstadoMemoriaServiceTest
 */
public class EstadoMemoriaServiceTest extends BaseServiceTest {

  @Mock
  private EstadoMemoriaRepository estadoMemoriaRepository;

  private EstadoMemoriaService estadoMemoriaService;

  @BeforeEach
  public void setUp() throws Exception {
    estadoMemoriaService = new EstadoMemoriaServiceImpl(estadoMemoriaRepository);
  }

  @Test
  public void find_WithId_ReturnsEstadoMemoria() {

    BDDMockito.given(estadoMemoriaRepository.findById(1L)).willReturn(Optional.of(generarMockEstadoMemoria(1L, 1L)));

    EstadoMemoria estadoMemoria = estadoMemoriaService.findById(1L);

    Assertions.assertThat(estadoMemoria.getId()).isEqualTo(1L);
    Assertions.assertThat(estadoMemoria.getMemoria().getTitulo()).isEqualTo("Memoria001");
    Assertions.assertThat(estadoMemoria.getTipoEstadoMemoria().getNombre()).isEqualTo("TipoEstadoMemoria001");

  }

  @Test
  public void find_NotFound_ThrowsEstadoMemoriaNotFoundException() throws Exception {
    BDDMockito.given(estadoMemoriaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> estadoMemoriaService.findById(1L))
        .isInstanceOf(EstadoMemoriaNotFoundException.class);
  }

  @Test
  public void create_ReturnsEstadoMemoria() {
    // given: Un nuevo estado memoria
    EstadoMemoria estadoMemoriaNew = generarMockEstadoMemoria(null, 1L);

    EstadoMemoria estadoMemoria = generarMockEstadoMemoria(1L, 1L);

    BDDMockito.given(estadoMemoriaRepository.save(estadoMemoriaNew)).willReturn(estadoMemoria);

    // when: Creamos el estado memoria
    EstadoMemoria estadoMemoriaCreado = estadoMemoriaService.create(estadoMemoriaNew);

    // then: El estado memoria se crea correctamente
    Assertions.assertThat(estadoMemoriaCreado).isNotNull();
    Assertions.assertThat(estadoMemoriaCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(estadoMemoriaCreado.getMemoria().getId()).isEqualTo(1L);
    Assertions.assertThat(estadoMemoriaCreado.getTipoEstadoMemoria().getId()).isEqualTo(1L);
  }

  @Test
  public void create_EstadoMemoriaWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo estado memoria que tiene id
    EstadoMemoria estadoMemoriaNew = generarMockEstadoMemoria(1L, 1L);
    // when: Creamos el estado memoria
    // then: Lanza una excepcion porque el estado memoria ya tiene id
    Assertions.assertThatThrownBy(() -> estadoMemoriaService.create(estadoMemoriaNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsEstadoMemoria() {
    // given: Un nuevo estado memoria con la memoria y el tipo estado memoria
    // actualizado
    EstadoMemoria estadoMemoriaServicioActualizado = generarMockEstadoMemoria(1L, 2L);

    EstadoMemoria estadoMemoria = generarMockEstadoMemoria(1L, 1L);

    BDDMockito.given(estadoMemoriaRepository.findById(1L)).willReturn(Optional.of(estadoMemoria));
    BDDMockito.given(estadoMemoriaRepository.save(estadoMemoria)).willReturn(estadoMemoriaServicioActualizado);

    // when: Actualizamos el estado memoria
    EstadoMemoria estadoMemoriaActualizado = estadoMemoriaService.update(estadoMemoria);

    // then: El estado memoria se actualiza correctamente.
    Assertions.assertThat(estadoMemoriaActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(estadoMemoriaActualizado.getMemoria().getId()).isEqualTo(2L);
    Assertions.assertThat(estadoMemoriaActualizado.getTipoEstadoMemoria().getId()).isEqualTo(2L);

  }

  @Test
  public void update_ThrowsEstadoMemoriaNotFoundException() {
    // given: Un nuevo estado memoria a actualizar
    EstadoMemoria estadoMemoria = generarMockEstadoMemoria(1L, 1L);

    // then: Lanza una excepcion porque el estado memoria no existe
    Assertions.assertThatThrownBy(() -> estadoMemoriaService.update(estadoMemoria))
        .isInstanceOf(EstadoMemoriaNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un estado memoria que venga sin id
    EstadoMemoria estadoMemoria = generarMockEstadoMemoria(null, 1L);

    Assertions.assertThatThrownBy(
        // when: update estado memoria
        () -> estadoMemoriaService.update(estadoMemoria))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> estadoMemoriaService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsEstadoMemoriaNotFoundException() {
    // given: Id no existe
    BDDMockito.given(estadoMemoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> estadoMemoriaService.delete(1L))
        // then: Lanza EstadoMemoriaNotFoundException
        .isInstanceOf(EstadoMemoriaNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesEstadoMemoria() {
    // given: Id existente
    BDDMockito.given(estadoMemoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(estadoMemoriaRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> estadoMemoriaService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullEstadoMemoriaList() {
    // given: One hundred estado memoria
    List<EstadoMemoria> estadoMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      estadoMemorias.add(generarMockEstadoMemoria(Long.valueOf(i), Long.valueOf(i)));
    }

    BDDMockito.given(estadoMemoriaRepository.findAll(ArgumentMatchers.<Specification<EstadoMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(estadoMemorias));

    // when: find unlimited
    Page<EstadoMemoria> page = estadoMemoriaService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred estado memorias
    Assertions.assertThat(page.getContent()).hasSize(100);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred estado memorias
    List<EstadoMemoria> etadoMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      etadoMemorias.add(generarMockEstadoMemoria(Long.valueOf(i), Long.valueOf(i)));
    }

    BDDMockito.given(estadoMemoriaRepository.findAll(ArgumentMatchers.<Specification<EstadoMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<EstadoMemoria>>() {
          @Override
          public Page<EstadoMemoria> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<EstadoMemoria> content = etadoMemorias.subList(fromIndex, toIndex);
            Page<EstadoMemoria> page = new PageImpl<>(content, pageable, etadoMemorias.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<EstadoMemoria> page = estadoMemoriaService.findAll(null, paging);

    // then: A Page with ten Memorias are returned containing
    // titulo='Memoria031' to 'Memoria040'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      EstadoMemoria estadoMemoria = page.getContent().get(i);
      Assertions.assertThat(estadoMemoria.getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", j));
      Assertions.assertThat(estadoMemoria.getTipoEstadoMemoria().getNombre())
          .isEqualTo("TipoEstadoMemoria" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto estado memoria
   * 
   * @param id                   id del estado memoria
   * @param idDatosEstadoMemoria id de la memoria y del tipo estado memoria
   * @return el objeto estado memoria
   */
  private EstadoMemoria generarMockEstadoMemoria(Long id, Long idDatosEstadoMemoria) {
    return new EstadoMemoria(id,
        generarMockMemoria(idDatosEstadoMemoria, "ref-9898", "Memoria" + String.format("%03d", idDatosEstadoMemoria),
            1),
        generarMockTipoEstadoMemoria(idDatosEstadoMemoria,
            "TipoEstadoMemoria" + String.format("%03d", idDatosEstadoMemoria), Boolean.TRUE),
        Instant.now(), null);

  }

  /**
   * Función que devuelve un objeto Memoria.
   * 
   * @param id            id del memoria.
   * @param numReferencia número de la referencia de la memoria.
   * @param titulo        titulo de la memoria.
   * @param version       version de la memoria.
   * @return el objeto Memoria
   */

  private Memoria generarMockMemoria(Long id, String numReferencia, String titulo, Integer version) {

    return new Memoria(id, numReferencia, generarMockPeticionEvaluacion(id, titulo + " PeticionEvaluacion" + id),
        generarMockComite(id, "comite" + id, true), titulo, "user-00" + id,
        generarMockTipoMemoria(1L, "TipoMemoria1", true),
        generarMockTipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), Instant.now(), Boolean.TRUE,
        generarMockRetrospectiva(1L), version, Boolean.TRUE, null);
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param id     id del PeticionEvaluacion
   * @param titulo el título de PeticionEvaluacion
   * @return el objeto PeticionEvaluacion
   */
  private PeticionEvaluacion generarMockPeticionEvaluacion(Long id, String titulo) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo" + id);
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico" + id);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos("Objetivos" + id);
    peticionEvaluacion.setResumen("Resumen" + id);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria" + id);
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(titulo);
    peticionEvaluacion.setPersonaRef("user-00" + id);
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
  }

  /**
   * Función que devuelve un objeto comité.
   * 
   * @param id     identificador del comité.
   * @param comite comité.
   * @param activo indicador de activo.
   * @return nuevo comite.
   */
  private Comite generarMockComite(Long id, String comite, Boolean activo) {
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    return new Comite(id, comite, "nombreInvestigacion", Genero.M, formulario, activo);

  }

  /**
   * Función que devuelve un objeto tipo memoria.
   * 
   * @param id     identificador del tipo memoria.
   * @param nombre nobmre.
   * @param activo indicador de activo.
   * @return tipo memoria.
   */
  private TipoMemoria generarMockTipoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoMemoria(id, nombre, activo);

  }

  /**
   * Función que devuelve un objeto tipo estado memoria.
   * 
   * @param id     identificador del tipo estado memoria.
   * @param nombre nobmre.
   * @param activo indicador de activo.
   * @return tipo estado memoria.
   */
  private TipoEstadoMemoria generarMockTipoEstadoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoEstadoMemoria(id, nombre, activo);

  }

  /**
   * Genera un objeto {@link Retrospectiva}
   * 
   * @param id
   * @return Retrospectiva
   */
  private Retrospectiva generarMockRetrospectiva(Long id) {

    final Retrospectiva data = new Retrospectiva();
    data.setId(id);
    data.setEstadoRetrospectiva(generarMockDataEstadoRetrospectiva((id % 2 == 0) ? 2L : 1L));
    data.setFechaRetrospectiva(LocalDate.of(2020, 7, id.intValue()).atStartOfDay(ZoneOffset.UTC).toInstant());

    return data;
  }

  /**
   * Genera un objeto {@link EstadoRetrospectiva}
   * 
   * @param id
   * @return EstadoRetrospectiva
   */
  private EstadoRetrospectiva generarMockDataEstadoRetrospectiva(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final EstadoRetrospectiva data = new EstadoRetrospectiva();
    data.setId(id);
    data.setNombre("NombreEstadoRetrospectiva" + txt);
    data.setActivo(Boolean.TRUE);

    return data;
  }
}