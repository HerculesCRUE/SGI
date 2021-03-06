package org.crue.hercules.sgi.eti.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.converter.EvaluacionConverter;
import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.exceptions.ConvocatoriaReunionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.crue.hercules.sgi.eti.repository.ConvocatoriaReunionRepository;
import org.crue.hercules.sgi.eti.repository.EstadoMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.RetrospectivaRepository;
import org.crue.hercules.sgi.eti.service.impl.EvaluacionServiceImpl;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiRepService;
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
 * EvaluacionServiceTest
 */
public class EvaluacionServiceTest extends BaseServiceTest {

  @Mock
  private EvaluacionRepository evaluacionRepository;
  @Mock
  private EstadoMemoriaRepository estadoMemoriaRepository;
  @Mock
  private RetrospectivaRepository retrospectivaRepository;

  private EvaluacionService evaluacionService;
  @Mock
  private MemoriaService memoriaService;
  @Mock
  private ConvocatoriaReunionRepository convocatoriaReunionRepository;

  @Mock
  private ComentarioRepository comentarioRepository;

  @Mock
  private MemoriaRepository memoriaRepository;

  @Mock
  private EvaluacionConverter evaluacionConverter;

  @Mock
  private SgiApiRepService reportService;

  @Mock
  private SgdocService sgdocService;

  @Mock
  private ComunicadosService comunicadosService;

  @Mock
  private SgiConfigProperties sgiConfigProperties;

  @BeforeEach
  public void setUp() throws Exception {
    evaluacionService = new EvaluacionServiceImpl(evaluacionRepository, estadoMemoriaRepository,
        retrospectivaRepository, memoriaService, comentarioRepository, convocatoriaReunionRepository, memoriaRepository,
        evaluacionConverter, reportService, sgdocService, comunicadosService, sgiConfigProperties);
  }

  @Test
  public void find_WithId_ReturnsEvaluacion() {
    BDDMockito.given(evaluacionRepository.findById(1L))
        .willReturn(Optional.of(generarMockEvaluacion(1L, null, 1L, 1L)));

    Evaluacion evaluacion = evaluacionService.findById(1L);

    Assertions.assertThat(evaluacion.getId()).isEqualTo(1L);

    Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen1");
    Assertions.assertThat(evaluacion.getConvocatoriaReunion().getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacion.getTipoEvaluacion().getNombre()).isEqualTo("TipoEvaluacion1");
  }

  @Test
  public void find_NotFound_ThrowsEvaluacionNotFoundException() throws Exception {
    BDDMockito.given(evaluacionRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> evaluacionService.findById(1L)).isInstanceOf(EvaluacionNotFoundException.class);
  }

  @Test
  public void create_ReturnsEvaluacion() {
    // given: Una nueva Evaluacion
    Evaluacion evaluacionNew = generarMockEvaluacion(null, " New", 1L, 1L);

    Evaluacion evaluacion = generarMockEvaluacion(1L, " New", 1L, 1L);

    BDDMockito.given(convocatoriaReunionRepository.existsById(1L)).willReturn(Boolean.TRUE);

    BDDMockito.given(memoriaRepository.existsById(evaluacion.getMemoria().getId())).willReturn(Boolean.TRUE);

    BDDMockito.given(convocatoriaReunionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(evaluacionNew.getConvocatoriaReunion()));
    BDDMockito.given(evaluacionRepository.save(evaluacionNew)).willReturn(evaluacion);

    // when: Creamos la Evaluacion
    Evaluacion evaluacionCreado = evaluacionService.create(evaluacionNew);

    // then: La Evaluacion se crea correctamente
    Assertions.assertThat(evaluacionCreado).isNotNull();
    Assertions.assertThat(evaluacionCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria New");
    Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen New");
    Assertions.assertThat(evaluacion.getConvocatoriaReunion().getId()).isEqualTo(1L);
  }

  @Test
  public void create_MemoriaEnSecretariaRevMinima_ReturnsEvaluacion() {
    // given: Una nueva Evaluacion con TipoEstadoMemoria = En secretar??a revisi??n
    // m??nima
    final Evaluacion evaluacionNew = generarMockEvaluacion(null, " New", 4L, 3L);

    final Evaluacion evaluacion = generarMockEvaluacion(1L, " New", 4L, 3L);

    BDDMockito.given(convocatoriaReunionRepository.existsById(1L)).willReturn(Boolean.TRUE);

    BDDMockito.given(memoriaRepository.existsById(evaluacion.getMemoria().getId())).willReturn(Boolean.TRUE);

    BDDMockito.given(convocatoriaReunionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(evaluacionNew.getConvocatoriaReunion()));
    BDDMockito.given(evaluacionRepository.save(evaluacionNew)).willReturn(evaluacion);

    // when: Creamos la Evaluacion
    final Evaluacion evaluacionCreado = evaluacionService.create(evaluacionNew);

    // then: La Evaluacion se crea correctamente
    Assertions.assertThat(evaluacionCreado).isNotNull();
    Assertions.assertThat(evaluacionCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria New");
    Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen New");
    Assertions.assertThat(evaluacion.getConvocatoriaReunion().getId()).isEqualTo(1L);
  }

  @Test
  public void create_MemoriaEnSecretariaSegAnual_ReturnsEvaluacion() {
    // given: Una nueva Evaluacion con TipoEstadoMemoria = En secretar??a seguimiento
    // anual
    final Evaluacion evaluacionNew = generarMockEvaluacion_ConvocatoriaReuSeguimiento(null, " New", 12L, 3L);

    final Evaluacion evaluacion = generarMockEvaluacion_ConvocatoriaReuSeguimiento(1L, " New", 12L, 3L);

    BDDMockito.given(convocatoriaReunionRepository.existsById(1L)).willReturn(Boolean.TRUE);

    BDDMockito.given(memoriaRepository.existsById(evaluacion.getMemoria().getId())).willReturn(Boolean.TRUE);

    BDDMockito.given(convocatoriaReunionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(evaluacionNew.getConvocatoriaReunion()));
    BDDMockito.given(evaluacionRepository.save(evaluacionNew)).willReturn(evaluacion);

    // when: Creamos la Evaluacion
    final Evaluacion evaluacionCreado = evaluacionService.create(evaluacionNew);

    // then: La Evaluacion se crea correctamente
    Assertions.assertThat(evaluacionCreado).isNotNull();
    Assertions.assertThat(evaluacionCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria New");
    Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen New");
    Assertions.assertThat(evaluacion.getConvocatoriaReunion().getId()).isEqualTo(1L);
  }

  @Test
  public void create_EvaluacionWithId_ThrowsIllegalArgumentException() {
    // given: Una nueva evaluacion que ya tiene id
    Evaluacion evaluacionNew = generarMockEvaluacion(1L, " New", 1L, 1L);
    // when: Creamos la evaluacion
    // then: Lanza una excepcion porque la evaluacion ya tiene id
    Assertions.assertThatThrownBy(() -> evaluacionService.create(evaluacionNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_EvaluacionWithId_ThrowsConvocatoriaReunionNotFoundException() {
    // given: Una nueva evaluacion que ya tiene id
    Evaluacion evaluacionNew = generarMockEvaluacion(null, " New", 1L, 1L);

    // when: Creamos la evaluacion
    // then: Lanza una excepcion porque la evaluacion ya tiene id
    Assertions.assertThatThrownBy(() -> evaluacionService.create(evaluacionNew))
        .isInstanceOf(ConvocatoriaReunionNotFoundException.class);
  }

  @Test
  public void create_EvaluacionWithId_ThrowsMemoriaNotFoundException() {
    // given: Una nueva evaluacion que ya tiene id
    Evaluacion evaluacionNew = generarMockEvaluacion(null, " New", 1L, 1L);

    BDDMockito.given(convocatoriaReunionRepository.existsById(1L)).willReturn(Boolean.TRUE);

    // when: Creamos la evaluacion
    // then: Lanza una excepcion porque la evaluacion ya tiene id
    Assertions.assertThatThrownBy(() -> evaluacionService.create(evaluacionNew))
        .isInstanceOf(MemoriaNotFoundException.class);
  }

  @Test
  public void update_ReturnsEvaluacion() {
    // given: Una nueva evaluacion con el servicio actualizado
    Evaluacion evaluacionServicioActualizado = generarMockEvaluacion(1L, " actualizado", 1L, 1L);

    Evaluacion evaluacion = generarMockEvaluacion(1L, null, 1L, 1L);

    BDDMockito.given(evaluacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(evaluacion));
    BDDMockito.given(evaluacionRepository.save(evaluacion)).willReturn(evaluacionServicioActualizado);

    // when: Actualizamos la evaluacion
    Evaluacion evaluacionActualizado = evaluacionService.update(evaluacion);

    // then: La evaluacion se actualiza correctamente.
    Assertions.assertThat(evaluacionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacionActualizado.getMemoria().getTitulo()).isEqualTo("Memoria actualizado");
    Assertions.assertThat(evaluacionActualizado.getDictamen().getNombre()).isEqualTo("Dictamen actualizado");
    Assertions.assertThat(evaluacionActualizado.getConvocatoriaReunion().getId()).isEqualTo(1L);

  }

  @Test
  public void update_IsRevMinima_ReturnsEvaluacion() {
    // La Evaluaci??n es de Revisi??n M??nima y el dictamen es "Favorable"
    final Evaluacion evaluacionServicioActualizado = generarMockEvaluacion(1L, " actualizado", 1L, 1L);
    evaluacionServicioActualizado.setEsRevMinima(Boolean.TRUE);

    final Evaluacion evaluacion = generarMockEvaluacion(1L, null, 1L, 1L);

    BDDMockito.given(evaluacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(evaluacion));
    BDDMockito.given(evaluacionRepository.save(evaluacion)).willReturn(evaluacionServicioActualizado);

    // when: Actualizamos la evaluacion
    final Evaluacion evaluacionActualizado = evaluacionService.update(evaluacion);

    // then: La evaluacion se actualiza correctamente.
    Assertions.assertThat(evaluacionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacionActualizado.getMemoria().getTitulo()).isEqualTo("Memoria actualizado");
    Assertions.assertThat(evaluacionActualizado.getDictamen().getNombre()).isEqualTo("Dictamen actualizado");
    Assertions.assertThat(evaluacionActualizado.getConvocatoriaReunion().getId()).isEqualTo(1L);

  }

  @Test
  public void update_MemoriaRevMin_ReturnsEvaluacion() {
    // La Evaluaci??n es de Revisi??n M??nima y el dictamen es "Favorable"
    final Evaluacion evaluacionServicioActualizado = generarMockEvaluacion(1L, " actualizado", 4L, 1L);

    // El estado de la memoria es "En secretaria revisi??n m??nima"
    final Evaluacion evaluacion = generarMockEvaluacion(1L, null, 4L, 1L);
    evaluacion.getDictamen().setNombre("FAVORABLE");

    BDDMockito.given(evaluacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(evaluacion));
    BDDMockito.given(evaluacionRepository.save(evaluacion)).willReturn(evaluacionServicioActualizado);

    // when: Actualizamos la evaluacion
    final Evaluacion evaluacionActualizado = evaluacionService.update(evaluacion);

    // then: La evaluacion se actualiza correctamente.
    Assertions.assertThat(evaluacionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacionActualizado.getMemoria().getTitulo()).isEqualTo("Memoria actualizado");
    Assertions.assertThat(evaluacionActualizado.getDictamen().getNombre()).isEqualTo("Dictamen actualizado");
    Assertions.assertThat(evaluacionActualizado.getConvocatoriaReunion().getId()).isEqualTo(1L);

  }

  @Test
  public void update_MemoriaEnEval_ReturnsEvaluacion() {
    // La Evaluaci??n es de Revisi??n M??nima y el dictamen es "Favorable"
    final Evaluacion evaluacionServicioActualizado = generarMockEvaluacion(1L, " actualizado", 5L, 1L);

    // El estado de la memoria es "En evaluaci??n"
    final Evaluacion evaluacion = generarMockEvaluacion(1L, null, 5L, 1L);
    evaluacion.getDictamen().setNombre("FAVORABLE");

    BDDMockito.given(evaluacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(evaluacion));
    BDDMockito.given(evaluacionRepository.save(evaluacion)).willReturn(evaluacionServicioActualizado);

    // when: Actualizamos la evaluacion
    final Evaluacion evaluacionActualizado = evaluacionService.update(evaluacion);

    // then: La evaluacion se actualiza correctamente.
    Assertions.assertThat(evaluacionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacionActualizado.getMemoria().getTitulo()).isEqualTo("Memoria actualizado");
    Assertions.assertThat(evaluacionActualizado.getDictamen().getNombre()).isEqualTo("Dictamen actualizado");
    Assertions.assertThat(evaluacionActualizado.getConvocatoriaReunion().getId()).isEqualTo(1L);

  }

  @Test
  public void update_ThrowsEvaluacionNotFoundException() {
    // given: Una nueva evaluacion a actualizar
    Evaluacion evaluacion = generarMockEvaluacion(1L, null, 1L, 1L);

    // then: Lanza una excepcion porque la evaluacion no existe
    Assertions.assertThatThrownBy(() -> evaluacionService.update(evaluacion))
        .isInstanceOf(EvaluacionNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Una Evaluacion que venga sin id
    Evaluacion evaluacion = generarMockEvaluacion(null, "1", 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: update Evaluacion
        () -> evaluacionService.update(evaluacion))
        // then: Lanza una excepci??n, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> evaluacionService.delete(null))
        // then: Lanza una excepci??n
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsEvaluacionNotFoundException() {
    // given: Id no existe
    BDDMockito.given(evaluacionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> evaluacionService.delete(1L))
        // then: Lanza EvaluacionNotFoundException
        .isInstanceOf(EvaluacionNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesEvaluacion() {
    // given: Id existente
    BDDMockito.given(evaluacionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(evaluacionRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> evaluacionService.delete(1L))
        // then: No se lanza ninguna excepci??n
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullEvaluacionList() {
    // given: One hundred Evaluacion
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i), 1L, 1L));
    }

    BDDMockito.given(evaluacionRepository.findAll(ArgumentMatchers.<Specification<Evaluacion>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluaciones));

    // when: find unlimited
    Page<Evaluacion> page = evaluacionService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred Evaluaciones
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred Evaluaciones
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i), 1L, 1L));
    }

    BDDMockito.given(evaluacionRepository.findAll(ArgumentMatchers.<Specification<Evaluacion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<Evaluacion>>() {
          @Override
          public Page<Evaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Evaluacion> content = evaluaciones.subList(fromIndex, toIndex);
            Page<Evaluacion> page = new PageImpl<>(content, pageable, evaluaciones.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Evaluacion> page = evaluacionService.findAll(null, paging);

    // then: A Page with ten Evaluaciones are returned containing
    // resumen='Evaluacion031' to 'Evaluacion040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Evaluacion evaluacion = page.getContent().get(i);
      Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", j));
      Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen" + String.format("%03d", j));
      Assertions.assertThat(evaluacion.getConvocatoriaReunion().getId()).isEqualTo(1L);
    }
  }

  @Test
  public void findAllActivasByConvocatoriaReunionId_Unlimited_ReturnsFullEvaluacionList() {

    // given: Datos existentes con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    List<Evaluacion> response = new LinkedList<Evaluacion>();
    response.add(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 1L, 1L));
    response.add(generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3), 1L, 1L));

    BDDMockito.given(evaluacionRepository.findAllByActivoTrueAndConvocatoriaReunionIdAndEsRevMinimaFalse(
        ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(response));

    // when: Se buscan todos las datos
    Page<Evaluacion> result = evaluacionService.findAllActivasByConvocatoriaReunionId(convocatoriaReunionId,
        Pageable.unpaged());

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getContent()).isEqualTo(response);
    Assertions.assertThat(result.getNumber()).isZero();
    Assertions.assertThat(result.getSize()).isEqualTo(response.size());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findAllActivasByConvocatoriaReunionId_Unlimited_ReturnEmptyPage() {

    // given: No hay datos con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    BDDMockito
        .given(evaluacionRepository.findAllByActivoTrueAndConvocatoriaReunionIdAndEsRevMinimaFalse(
            ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos las datos
    Page<Evaluacion> result = evaluacionService.findAllActivasByConvocatoriaReunionId(convocatoriaReunionId,
        Pageable.unpaged());

    // then: Se recupera lista vac??a
    Assertions.assertThat(result.isEmpty());
  }

  @Test
  public void findAllActivasByConvocatoriaReunionId_WithPaging_ReturnsPage() {

    // given: Datos existentes con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    List<Evaluacion> response = new LinkedList<Evaluacion>();
    response.add(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 1L, 1L));
    response.add(generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3), 1L, 1L));
    response.add(generarMockEvaluacion(Long.valueOf(5), String.format("%03d", 5), 1L, 1L));

    // p??gina 1 con 2 elementos por p??gina
    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(evaluacionRepository.findAllByActivoTrueAndConvocatoriaReunionIdAndEsRevMinimaFalse(
        ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<Evaluacion> result = evaluacionService.findAllActivasByConvocatoriaReunionId(convocatoriaReunionId, pageable);

    // then: Se recuperan los datos correctamente seg??n la paginaci??n solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findAllActivasByConvocatoriaReunionId_WithPaging_ReturnEmptyPage() {

    // given: No hay datos con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;

    List<Evaluacion> response = new LinkedList<Evaluacion>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResponse = new PageImpl<>(response, pageable, response.size());

    BDDMockito.given(evaluacionRepository.findAllByActivoTrueAndConvocatoriaReunionIdAndEsRevMinimaFalse(
        ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<Evaluacion> result = evaluacionService.findAllActivasByConvocatoriaReunionId(convocatoriaReunionId, pageable);

    // then: Se recupera lista de datos paginados vac??a
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void findEvaluacionesAnterioresByMemoriaMemoriaIdNull() {
    // given: EL id de la memoria sea null
    Long memoriaId = null;
    Long evaluacionId = 1L;
    Long tipoComentarioId = 1L;
    try {
      // when: se listar sus evaluaciones
      evaluacionService.findEvaluacionesAnterioresByMemoria(memoriaId, evaluacionId, tipoComentarioId,
          Pageable.unpaged());
      Assertions.fail("El id de la memoria no puede ser nulo para mostrar sus evaluaciones");
      // then: se debe lanzar una excepci??n
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El id de la memoria no puede ser nulo para mostrar sus evaluaciones");
    }
  }

  @Test
  public void findEvaluacionesAnterioresByMemoriaEvaluacionIdNull() {
    // given: EL id de la evaluaci??n sea null
    Long memoriaId = 1L;
    Long evaluacionId = null;
    Long tipoComentarioId = 1L;
    try {
      // when: se listar sus evaluaciones
      evaluacionService.findEvaluacionesAnterioresByMemoria(memoriaId, evaluacionId, tipoComentarioId,
          Pageable.unpaged());
      Assertions.fail("El id de la evaluaci??n no puede ser nulo para recuperar las evaluaciones anteriores");
      // then: se debe lanzar una excepci??n
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El id de la evaluaci??n no puede ser nulo para recuperar las evaluaciones anteriores");
    }
  }

  @Test
  public void findEvaluacionesAnterioresByMemoriaIdValid() {
    // given: EL id de la memoria es valido
    Long evaluacionId = 12L;
    Long memoriaId = 1L;
    Long tipoComentarioId = 1L;
    List<EvaluacionWithNumComentario> response = new LinkedList<EvaluacionWithNumComentario>();
    response.add(new EvaluacionWithNumComentario(
        generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 1L, 1L), Long.valueOf(1)));
    response.add(new EvaluacionWithNumComentario(
        generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3), 1L, 1L), Long.valueOf(3)));
    response.add(new EvaluacionWithNumComentario(
        generarMockEvaluacion(Long.valueOf(5), String.format("%03d", 5), 1L, 1L), Long.valueOf(5)));

    // p??gina 1 con 2 elementos por p??gina
    Pageable pageable = PageRequest.of(1, 2);
    Page<EvaluacionWithNumComentario> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(
        evaluacionRepository.findEvaluacionesAnterioresByMemoria(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(pageResponse);

    BDDMockito.given(evaluacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 1L, 1L)));

    // when: Se buscan los datos paginados
    Page<EvaluacionWithNumComentario> result = evaluacionService.findEvaluacionesAnterioresByMemoria(memoriaId,
        evaluacionId, tipoComentarioId, pageable);

    // then: Se recuperan los datos correctamente seg??n la paginaci??n solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findAllByMemoriaAndRetrospectivaEnEvaluacion_ReturnsFiltratedEvaluacionList() {
    // given: One hundred Evaluacion
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i), 1L, 1L));
    }

    BDDMockito.given(evaluacionRepository.findAllByMemoriaAndRetrospectivaEnEvaluacion(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluaciones));

    // when: find unlimited
    Page<Evaluacion> page = evaluacionService.findAllByMemoriaAndRetrospectivaEnEvaluacion(null, Pageable.unpaged());

    // then: Get a page with one hundred Evaluaciones
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAllByMemoriaAndRetrospectivaEnEvaluacion_WithPaging_ReturnsPage() {
    // given: One hundred Evaluaciones
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i), 1L, 1L));
    }

    BDDMockito.given(evaluacionRepository.findAllByMemoriaAndRetrospectivaEnEvaluacion(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<Evaluacion>>() {
          @Override
          public Page<Evaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Evaluacion> content = evaluaciones.subList(fromIndex, toIndex);
            Page<Evaluacion> page = new PageImpl<>(content, pageable, evaluaciones.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Evaluacion> page = evaluacionService.findAllByMemoriaAndRetrospectivaEnEvaluacion(null, paging);

    // then: A Page with ten Evaluaciones are returned containing
    // resumen='Evaluacion031' to 'Evaluacion040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Evaluacion evaluacion = page.getContent().get(i);
      Assertions.assertThat(evaluacion.getMemoria().getTitulo()).isEqualTo("Memoria" + String.format("%03d", j));
      Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen" + String.format("%03d", j));
    }
  }

  @Test
  public void findByEvaluadorPersonaRef_IdNull() {
    // given: Es personaRef es null
    String personaRef = null;
    try {
      // when: se quiere listar sus evaluaciones
      evaluacionService.findByEvaluador(personaRef, null, Pageable.unpaged());
      Assertions.fail("El personaRef de la evaluaci??n no puede ser nulo para mostrar sus evaluaciones");
      // then: se debe lanzar una excepci??n
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El personaRef de la evaluaci??n no puede ser nulo para mostrar sus evaluaciones");
    }
  }

  @Test
  public void findByEvaluador_IdValid() {
    // given: El personRef no es null
    String personaRef = "user-001";
    List<Evaluacion> response = new LinkedList<Evaluacion>();
    response.add(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 4L, 1L));
    response.add(generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3), 4L, 1L));
    response.add(generarMockEvaluacion(Long.valueOf(5), String.format("%03d", 5), 4L, 1L));
    Page<Evaluacion> pageResponse = new PageImpl<>(response);
    BDDMockito.given(evaluacionRepository.findByEvaluador(ArgumentMatchers.anyString(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(response));

    // when: se listar sus evaluaciones
    Page<Evaluacion> result = evaluacionService.findByEvaluador(personaRef, null, Pageable.unpaged());

    // then: recibe un listado
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(pageResponse.getContent());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findEvaluacionesEnSeguimientosByEvaluador_PersonaRefNull() {
    // given: Es personaRef es null
    String personaRef = null;
    try {
      // when: se quiere listar sus evaluaciones
      evaluacionService.findEvaluacionesEnSeguimientosByEvaluador(personaRef, null, Pageable.unpaged());
      Assertions.fail("El personaRef de la evaluaci??n no puede ser nulo para mostrar sus evaluaciones en seguimiento");
      // then: se debe lanzar una excepci??n
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El personaRef de la evaluaci??n no puede ser nulo para mostrar sus evaluaciones en seguimiento");
    }
  }

  @Test
  public void findEvaluacionesEnSeguimientosByEvaluador_Success() {
    // given: El personRef no es null
    String personaRef = "user-001";
    List<Evaluacion> response = new LinkedList<Evaluacion>();
    response.add(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 11L, 1L));
    response.add(generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3), 11L, 1L));
    response.add(generarMockEvaluacion(Long.valueOf(5), String.format("%03d", 5), 11L, 1L));
    Page<Evaluacion> pageResponse = new PageImpl<>(response);
    BDDMockito.given(evaluacionRepository.findEvaluacionesEnSeguimientosByEvaluador(ArgumentMatchers.anyString(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(response));

    // when: se listar sus evaluaciones
    Page<Evaluacion> result = evaluacionService.findEvaluacionesEnSeguimientosByEvaluador(personaRef, null,
        Pageable.unpaged());

    // then: recibe un listado
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(pageResponse.getContent());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findByEvaluacionesEnSeguimientoFinal_ReturnsList() {
    // given: Ten Evaluacion
    List<Evaluacion> evaluaciones = new ArrayList<>();
    for (int i = 1; i <= 3; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i), 19L, 1L));
    }
    for (int i = 1; i <= 3; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i), 13L, 1L));
    }
    for (int i = 1; i <= 4; i++) {
      evaluaciones.add(generarMockEvaluacion(Long.valueOf(i), String.format("%03d", i), 18L, 1L));
    }

    BDDMockito.given(evaluacionRepository.findByEvaluacionesEnSeguimientoFinal(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluaciones));

    // when: find unlimited
    Page<Evaluacion> page = evaluacionService.findByEvaluacionesEnSeguimientoFinal(null, Pageable.unpaged());

    // then: Get a page with ten Evaluaciones
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(10);
  }

  @Test
  public void deleteEvaluacion_fecha_anterior() {

    Evaluacion evaluacion = generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 1L, 2L);
    Memoria memoria = evaluacion.getMemoria();

    Optional<Evaluacion> response = Optional.of(evaluacion);
    Optional<Memoria> responseMemo = Optional.of(memoria);
    BDDMockito.given(evaluacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(response);
    BDDMockito.given(memoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(responseMemo);
    BDDMockito.given(memoriaService.getEstadoAnteriorMemoria(ArgumentMatchers.<Memoria>any())).willReturn(memoria);

    try {

      evaluacionService.deleteEvaluacion(response.get().getConvocatoriaReunion().getId(), response.get().getId());
    } catch (

    IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("La fecha de la convocatoria es anterior a la actual");
    }

  }

  @Test
  public void deleteEvaluacion_ConvocatoriaReunionIsNotValid() {
    // given: idConvocatoriaReunion = 2L
    Long idConvocatoriaReunion = 2L;
    final Evaluacion evaluacion = generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 1L, 2L);
    final Optional<Evaluacion> response = Optional.of(evaluacion);
    BDDMockito.given(evaluacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(response);

    try {

      evaluacionService.deleteEvaluacion(idConvocatoriaReunion, response.get().getId());
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("La evaluaci??n no pertenece a esta convocatoria de reuni??n");
    }

  }

  @Test
  public void findAllByMemoriaId_ReturnsFullEvaluacionList() {

    // given: Datos existentes con memoriaId = 1
    Memoria memoria = new Memoria(1L, "numRef-001", null, null, "Memoria", "user-001", null, null, Instant.now(),
        Boolean.TRUE, null, 3, "CodOrganoCompetente", Boolean.TRUE, null);

    Long memoriaId = 1L;
    List<Evaluacion> response = new LinkedList<Evaluacion>();
    response.add(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 1L, 1L));
    response.add(generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3), 1L, 1L));

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(memoria));
    BDDMockito.given(evaluacionRepository.findAll(ArgumentMatchers.<Specification<Evaluacion>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(response));

    // when: Se buscan todos las datos
    Page<Evaluacion> result = evaluacionService.findAllByMemoriaId(memoriaId, Pageable.unpaged());

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getContent()).isEqualTo(response);
    Assertions.assertThat(result.getNumber()).isZero();
    Assertions.assertThat(result.getSize()).isEqualTo(response.size());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findAllByMemoriaId_ReturnEmptyPage() {

    Memoria memoria = new Memoria(1L, "numRef-001", null, null, "Memoria", "user-001", null, null, Instant.now(),
        Boolean.TRUE, null, 3, "CodOrganoCompetente", Boolean.TRUE, null);
    // given: No hay datos con memoriaId = 1
    Long memoriaId = 1L;

    List<Evaluacion> response = new LinkedList<Evaluacion>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResponse = new PageImpl<>(response, pageable, response.size());

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(memoria));
    BDDMockito.given(evaluacionRepository.findAll(ArgumentMatchers.<Specification<Evaluacion>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<Evaluacion> result = evaluacionService.findAllByMemoriaId(memoriaId, pageable);

    // then: Se recupera lista de datos paginados vac??a
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void findAllByMemoriaId_IdNull() {
    // given: Es memoriaId es null
    Long memoriaId = null;
    try {
      // when: se quiere listar sus evaluaciones
      evaluacionService.findAllByMemoriaId(memoriaId, Pageable.unpaged());
      Assertions.fail("El id de la memoria no puede ser nulo para mostrar sus evaluaciones");
      // then: se debe lanzar una excepci??n
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El id de la memoria no puede ser nulo para mostrar sus evaluaciones");
    }
  }

  /**
   * Funci??n que devuelve un objeto Evaluacion
   * 
   * @param id                    id del Evaluacion
   * @param sufijo                el sufijo para t??tulo y nombre
   * @param idTipoEstadoMemoria   id del tipo de estado de la memoria
   * @param idEstadoRetrospectiva id del estado de la retrospectiva
   * @return el objeto Evaluacion
   */

  public Evaluacion generarMockEvaluacion(final Long id, final String sufijo, final Long idTipoEstadoMemoria,
      final Long idEstadoRetrospectiva) {

    final String sufijoStr = (sufijo == null ? id.toString() : sufijo);

    final Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre("Dictamen" + sufijoStr);
    dictamen.setActivo(Boolean.TRUE);

    final TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    final PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico("Dise??oMetodologico1");
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos("Objetivos1");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("PeticionEvaluacion1");
    peticionEvaluacion.setPersonaRef("user-001");
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    final Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    final Comite comite = new Comite(1L, "Comite1", "nombreSecretario", "nombreInvestigacion", Genero.M,
        "nombreDecreto", "articulo", formulario, Boolean.TRUE);

    final TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(1L); // Nueva
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    final TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(idTipoEstadoMemoria); // En elaboraci??n

    final EstadoRetrospectiva estadoRetrospectiva = new EstadoRetrospectiva();
    estadoRetrospectiva.setId(idEstadoRetrospectiva); // Pendiente

    final Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria" + sufijoStr,
        "user-00" + id, tipoMemoria, tipoEstadoMemoria, Instant.now(), Boolean.TRUE,
        new Retrospectiva(id, estadoRetrospectiva, Instant.now()), 3, "CodOrganoCompetente", Boolean.TRUE, null);

    final TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    final ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(1L); // Ordinaria
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.parse("2020-05-10T00:00:00Z"));
    convocatoriaReunion.setFechaLimite(Instant.now());
    convocatoriaReunion.setLugar("Lugar");
    convocatoriaReunion.setOrdenDia("Orden del d??a convocatoria reuni??n");
    convocatoriaReunion.setAnio(2020);
    convocatoriaReunion.setNumeroActa(100L);
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(Instant.now());
    convocatoriaReunion.setActivo(Boolean.TRUE);

    final TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L); // Retrospectiva
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    final Evaluador evaluador1 = new Evaluador();
    evaluador1.setId(1L);

    final Evaluador evaluador2 = new Evaluador();
    evaluador2.setId(2L);

    final Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(Instant.now());
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(convocatoriaReunion);
    evaluacion.setVersion(2);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setEvaluador1(evaluador1);
    evaluacion.setEvaluador2(evaluador2);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }

  public Evaluacion generarMockEvaluacion_ConvocatoriaReuSeguimiento(final Long id, final String sufijo,
      final Long idTipoEstadoMemoria, final Long idEstadoRetrospectiva) {

    String sufijoStr = (sufijo == null ? id.toString() : sufijo);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre("Dictamen" + sufijoStr);
    dictamen.setActivo(Boolean.TRUE);

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico("Dise??oMetodologico1");
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos("Objetivos1");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("PeticionEvaluacion1");
    peticionEvaluacion.setPersonaRef("user-001");
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", "nombreSecretario", "nombreInvestigacion", Genero.M, "nombreDecreto",
        "articulo", formulario, Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(1L);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(idTipoEstadoMemoria);

    EstadoRetrospectiva estadoRetrospectiva = new EstadoRetrospectiva();
    estadoRetrospectiva.setId(idEstadoRetrospectiva);

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria" + sufijoStr, "user-00" + id,
        tipoMemoria, tipoEstadoMemoria, Instant.now(), Boolean.TRUE,
        new Retrospectiva(id, estadoRetrospectiva, Instant.now()), 3, "CodOrganoCompetente", Boolean.TRUE, null);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(3L, "Seguimiento", Boolean.TRUE);

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(1L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.parse("2020-05-10T00:00:00Z"));
    convocatoriaReunion.setFechaLimite(Instant.now());
    convocatoriaReunion.setLugar("Lugar");
    convocatoriaReunion.setOrdenDia("Orden del d??a convocatoria reuni??n");
    convocatoriaReunion.setAnio(2020);
    convocatoriaReunion.setNumeroActa(100L);
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(Instant.now());
    convocatoriaReunion.setActivo(Boolean.TRUE);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Evaluador evaluador1 = new Evaluador();
    evaluador1.setId(1L);

    Evaluador evaluador2 = new Evaluador();
    evaluador2.setId(2L);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(Instant.now());
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(convocatoriaReunion);
    evaluacion.setVersion(2);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setEvaluador1(evaluador1);
    evaluacion.setEvaluador2(evaluador2);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }

  public List<EstadoMemoria> generarEstadosMemoria(Long id) {
    List<EstadoMemoria> estadosMemoria = new ArrayList<EstadoMemoria>();
    EstadoMemoria estadoMemoria = new EstadoMemoria();
    estadoMemoria.setFechaEstado(Instant.now());
    estadoMemoria.setId(id);
    estadoMemoria.setMemoria(new Memoria());
    estadosMemoria.add(estadoMemoria);
    return estadosMemoria;
  }

  public EstadoRetrospectiva generarEstadoRetrospectiva(Long id) {
    EstadoRetrospectiva estadoRetrospectiva = new EstadoRetrospectiva();
    estadoRetrospectiva.setActivo(true);
    estadoRetrospectiva.setId(id);
    return estadoRetrospectiva;
  }

}