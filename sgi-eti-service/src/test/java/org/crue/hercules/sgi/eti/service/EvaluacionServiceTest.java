package org.crue.hercules.sgi.eti.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.converter.EvaluacionConverter;
import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.exceptions.ConvocatoriaReunionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunionLugar;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunionOrdenDia;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.MemoriaTitulo;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionDisMetodologico;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionObjetivos;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionResumen;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionTitulo;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.crue.hercules.sgi.eti.repository.ConvocatoriaReunionRepository;
import org.crue.hercules.sgi.eti.repository.EstadoMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.RetrospectivaRepository;
import org.crue.hercules.sgi.eti.service.impl.EvaluacionServiceImpl;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiRepService;
import org.crue.hercules.sgi.framework.i18n.I18nConfig;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * EvaluacionServiceTest
 */
class EvaluacionServiceTest extends BaseServiceTest {

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

  @Mock
  private EvaluadorService evaluadorService;

  @Mock
  private RetrospectivaService retrospectivaService;

  @Autowired
  private MessageSource messageSource;
  @Autowired
  private I18nConfig i18nConfig;

  @BeforeEach
  void setUp() throws Exception {
    evaluacionService = new EvaluacionServiceImpl(evaluacionRepository, memoriaService, comentarioRepository,
        convocatoriaReunionRepository, memoriaRepository,
        evaluacionConverter, reportService, sgdocService, comunicadosService, sgiConfigProperties, evaluadorService,
        retrospectivaService, messageSource, i18nConfig);
  }

  @Test
  void find_WithId_ReturnsEvaluacion() {
    BDDMockito.given(evaluacionRepository.findById(1L))
        .willReturn(Optional.of(generarMockEvaluacion(1L, null, 1L, 1L)));

    Evaluacion evaluacion = evaluacionService.findById(1L);

    Assertions.assertThat(evaluacion.getId()).isEqualTo(1L);

    Assertions.assertThat(I18nHelper.getValueForLanguage(evaluacion.getMemoria().getTitulo(), Language.ES))
        .isEqualTo("Memoria1");
    Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen1");
    Assertions.assertThat(evaluacion.getConvocatoriaReunion().getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacion.getTipoEvaluacion().getNombre()).isEqualTo("TipoEvaluacion1");
  }

  @Test
  void find_NotFound_ThrowsEvaluacionNotFoundException() throws Exception {
    BDDMockito.given(evaluacionRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> evaluacionService.findById(1L)).isInstanceOf(EvaluacionNotFoundException.class);
  }

  @Test
  void create_ReturnsEvaluacion() {
    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 2; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-5" + String.format("%03d", i),
          "Memoria" + String.format("%03d", i), 1, 1L));
    }

    // given: Una nueva Evaluacion
    Evaluacion evaluacionNew = generarMockEvaluacion(null, " New", 1L, 1L);

    Evaluacion evaluacion = generarMockEvaluacion(1L, " New", 1L, 1L);

    BDDMockito.given(convocatoriaReunionRepository.existsById(1L)).willReturn(Boolean.TRUE);

    BDDMockito.given(memoriaRepository.existsById(evaluacion.getMemoria().getId())).willReturn(Boolean.TRUE);

    BDDMockito.given(memoriaRepository.findAllMemoriasAsignablesConvocatoria(ArgumentMatchers.anyLong()))
        .willReturn(memorias);

    BDDMockito.given(convocatoriaReunionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(evaluacionNew.getConvocatoriaReunion()));
    BDDMockito.given(evaluacionRepository.save(evaluacionNew)).willReturn(evaluacion);

    // when: Creamos la Evaluacion
    Evaluacion evaluacionCreado = evaluacionService.create(evaluacionNew);

    // then: La Evaluacion se crea correctamente
    Assertions.assertThat(evaluacionCreado).isNotNull();
    Assertions.assertThat(evaluacionCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(evaluacion.getMemoria().getTitulo(), Language.ES))
        .isEqualTo("Memoria New");
    Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen New");
    Assertions.assertThat(evaluacion.getConvocatoriaReunion().getId()).isEqualTo(1L);
  }

  @Test
  void create_MemoriaEnSecretariaRevMinima_ReturnsEvaluacion() {
    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 2; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-5" + String.format("%03d", i),
          "Memoria" + String.format("%03d", i), 1, 1L));
    }
    // given: Una nueva Evaluacion con TipoEstadoMemoria = En secretaría revisión
    // mínima
    final Evaluacion evaluacionNew = generarMockEvaluacion(null, " New", 4L, 3L);

    final Evaluacion evaluacion = generarMockEvaluacion(1L, " New", 4L, 3L);

    BDDMockito.given(convocatoriaReunionRepository.existsById(1L)).willReturn(Boolean.TRUE);

    BDDMockito.given(memoriaRepository.existsById(evaluacion.getMemoria().getId())).willReturn(Boolean.TRUE);

    BDDMockito.given(memoriaRepository.findAllMemoriasAsignablesConvocatoria(ArgumentMatchers.anyLong()))
        .willReturn(memorias);

    BDDMockito.given(convocatoriaReunionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(evaluacionNew.getConvocatoriaReunion()));
    BDDMockito.given(evaluacionRepository.save(evaluacionNew)).willReturn(evaluacion);

    // when: Creamos la Evaluacion
    final Evaluacion evaluacionCreado = evaluacionService.create(evaluacionNew);

    // then: La Evaluacion se crea correctamente
    Assertions.assertThat(evaluacionCreado).isNotNull();
    Assertions.assertThat(evaluacionCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(evaluacion.getMemoria().getTitulo(), Language.ES))
        .isEqualTo("Memoria New");
    Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen New");
    Assertions.assertThat(evaluacion.getConvocatoriaReunion().getId()).isEqualTo(1L);
  }

  @Test
  void create_MemoriaEnSecretariaSegAnual_ReturnsEvaluacion() {
    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 2; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-5" + String.format("%03d", i),
          "Memoria" + String.format("%03d", i), 1, 1L));
    }

    // given: Una nueva Evaluacion con TipoEstadoMemoria = En secretaría seguimiento
    // anual
    final Evaluacion evaluacionNew = generarMockEvaluacion_ConvocatoriaReuSeguimiento(null, " New", 12L, 3L);

    final Evaluacion evaluacion = generarMockEvaluacion_ConvocatoriaReuSeguimiento(1L, " New", 12L, 3L);

    BDDMockito.given(convocatoriaReunionRepository.existsById(1L)).willReturn(Boolean.TRUE);

    BDDMockito.given(memoriaRepository.existsById(evaluacion.getMemoria().getId())).willReturn(Boolean.TRUE);

    BDDMockito.given(memoriaRepository.findAllMemoriasAsignablesConvocatoria(ArgumentMatchers.anyLong()))
        .willReturn(memorias);

    BDDMockito.given(convocatoriaReunionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(evaluacionNew.getConvocatoriaReunion()));
    BDDMockito.given(evaluacionRepository.save(evaluacionNew)).willReturn(evaluacion);

    // when: Creamos la Evaluacion
    final Evaluacion evaluacionCreado = evaluacionService.create(evaluacionNew);

    // then: La Evaluacion se crea correctamente
    Assertions.assertThat(evaluacionCreado).isNotNull();
    Assertions.assertThat(evaluacionCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(evaluacion.getMemoria().getTitulo(), Language.ES))
        .isEqualTo("Memoria New");
    Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen New");
    Assertions.assertThat(evaluacion.getConvocatoriaReunion().getId()).isEqualTo(1L);
  }

  @Test
  void create_EvaluacionWithId_ThrowsIllegalArgumentException() {
    // given: Una nueva evaluacion que ya tiene id
    Evaluacion evaluacionNew = generarMockEvaluacion(1L, " New", 1L, 1L);
    // when: Creamos la evaluacion
    // then: Lanza una excepcion porque la evaluacion ya tiene id
    Assertions.assertThatThrownBy(() -> evaluacionService.create(evaluacionNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void create_EvaluacionWithId_ThrowsConvocatoriaReunionNotFoundException() {
    // given: Una nueva evaluacion que ya tiene id
    Evaluacion evaluacionNew = generarMockEvaluacion(null, " New", 1L, 1L);

    // when: Creamos la evaluacion
    // then: Lanza una excepcion porque la evaluacion ya tiene id
    Assertions.assertThatThrownBy(() -> evaluacionService.create(evaluacionNew))
        .isInstanceOf(ConvocatoriaReunionNotFoundException.class);
  }

  @Test
  void create_EvaluacionWithId_ThrowsMemoriaNotFoundException() {
    // given: Una nueva evaluacion que ya tiene id
    Evaluacion evaluacionNew = generarMockEvaluacion(null, " New", 1L, 1L);

    BDDMockito.given(convocatoriaReunionRepository.existsById(1L)).willReturn(Boolean.TRUE);

    // when: Creamos la evaluacion
    // then: Lanza una excepcion porque la evaluacion ya tiene id
    Assertions.assertThatThrownBy(() -> evaluacionService.create(evaluacionNew))
        .isInstanceOf(MemoriaNotFoundException.class);
  }

  @Test
  void update_ReturnsEvaluacion() {
    // given: Una nueva evaluacion con el servicio actualizado
    Evaluacion evaluacionServicioActualizado = generarMockEvaluacion(1L, " actualizado", 1L, 1L);

    Evaluacion evaluacion = generarMockEvaluacion(1L, null, 1L, 1L);

    BDDMockito.given(evaluacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(evaluacion));
    BDDMockito.given(evaluacionRepository.save(evaluacion)).willReturn(evaluacionServicioActualizado);

    // when: Actualizamos la evaluacion
    Evaluacion evaluacionActualizado = evaluacionService.update(evaluacion);

    // then: La evaluacion se actualiza correctamente.
    Assertions.assertThat(evaluacionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(evaluacionActualizado.getMemoria().getTitulo(), Language.ES))
        .isEqualTo("Memoria actualizado");
    Assertions.assertThat(evaluacionActualizado.getDictamen().getNombre()).isEqualTo("Dictamen actualizado");
    Assertions.assertThat(evaluacionActualizado.getConvocatoriaReunion().getId()).isEqualTo(1L);

  }

  @Test
  void update_IsRevMinima_ReturnsEvaluacion() {
    // La Evaluación es de Revisión Mínima y el dictamen es "Favorable"
    final Evaluacion evaluacionServicioActualizado = generarMockEvaluacion(1L, " actualizado", 1L, 1L);
    evaluacionServicioActualizado.setEsRevMinima(Boolean.TRUE);

    final Evaluacion evaluacion = generarMockEvaluacion(1L, null, 1L, 1L);

    BDDMockito.given(evaluacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(evaluacion));
    BDDMockito.given(evaluacionRepository.save(evaluacion)).willReturn(evaluacionServicioActualizado);

    // when: Actualizamos la evaluacion
    final Evaluacion evaluacionActualizado = evaluacionService.update(evaluacion);

    // then: La evaluacion se actualiza correctamente.
    Assertions.assertThat(evaluacionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(evaluacionActualizado.getMemoria().getTitulo(), Language.ES))
        .isEqualTo("Memoria actualizado");
    Assertions.assertThat(evaluacionActualizado.getDictamen().getNombre()).isEqualTo("Dictamen actualizado");
    Assertions.assertThat(evaluacionActualizado.getConvocatoriaReunion().getId()).isEqualTo(1L);

  }

  @Test
  void update_MemoriaRevMin_ReturnsEvaluacion() {
    // La Evaluación es de Revisión Mínima y el dictamen es "Favorable"
    final Evaluacion evaluacionServicioActualizado = generarMockEvaluacion(1L, " actualizado", 4L, 1L);

    // El estado de la memoria es "En secretaria revisión mínima"
    final Evaluacion evaluacion = generarMockEvaluacion(1L, null, 4L, 1L);
    evaluacion.getDictamen().setNombre("FAVORABLE");

    BDDMockito.given(evaluacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(evaluacion));
    BDDMockito.given(evaluacionRepository.save(evaluacion)).willReturn(evaluacionServicioActualizado);

    // when: Actualizamos la evaluacion
    final Evaluacion evaluacionActualizado = evaluacionService.update(evaluacion);

    // then: La evaluacion se actualiza correctamente.
    Assertions.assertThat(evaluacionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(evaluacionActualizado.getMemoria().getTitulo(), Language.ES))
        .isEqualTo("Memoria actualizado");
    Assertions.assertThat(evaluacionActualizado.getDictamen().getNombre()).isEqualTo("Dictamen actualizado");
    Assertions.assertThat(evaluacionActualizado.getConvocatoriaReunion().getId()).isEqualTo(1L);

  }

  @Test
  void update_MemoriaEnEval_ReturnsEvaluacion() {
    // La Evaluación es de Revisión Mínima y el dictamen es "Favorable"
    final Evaluacion evaluacionServicioActualizado = generarMockEvaluacion(1L, " actualizado", 5L, 1L);

    // El estado de la memoria es "En evaluación"
    final Evaluacion evaluacion = generarMockEvaluacion(1L, null, 5L, 1L);
    evaluacion.getDictamen().setNombre("FAVORABLE");

    BDDMockito.given(evaluacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(evaluacion));
    BDDMockito.given(evaluacionRepository.save(evaluacion)).willReturn(evaluacionServicioActualizado);

    // when: Actualizamos la evaluacion
    final Evaluacion evaluacionActualizado = evaluacionService.update(evaluacion);

    // then: La evaluacion se actualiza correctamente.
    Assertions.assertThat(evaluacionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(evaluacionActualizado.getMemoria().getTitulo(), Language.ES))
        .isEqualTo("Memoria actualizado");
    Assertions.assertThat(evaluacionActualizado.getDictamen().getNombre()).isEqualTo("Dictamen actualizado");
    Assertions.assertThat(evaluacionActualizado.getConvocatoriaReunion().getId()).isEqualTo(1L);

  }

  @Test
  void update_ThrowsEvaluacionNotFoundException() {
    // given: Una nueva evaluacion a actualizar
    Evaluacion evaluacion = generarMockEvaluacion(1L, null, 1L, 1L);

    // then: Lanza una excepcion porque la evaluacion no existe
    Assertions.assertThatThrownBy(() -> evaluacionService.update(evaluacion))
        .isInstanceOf(EvaluacionNotFoundException.class);

  }

  @Test
  void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Una Evaluacion que venga sin id
    Evaluacion evaluacion = generarMockEvaluacion(null, "1", 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: update Evaluacion
        () -> evaluacionService.update(evaluacion))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> evaluacionService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void delete_NonExistingId_ThrowsEvaluacionNotFoundException() {
    // given: Id no existe
    BDDMockito.given(evaluacionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> evaluacionService.delete(1L))
        // then: Lanza EvaluacionNotFoundException
        .isInstanceOf(EvaluacionNotFoundException.class);
  }

  @Test
  void delete_WithExistingId_DeletesEvaluacion() {
    // given: Id existente
    BDDMockito.given(evaluacionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(evaluacionRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> evaluacionService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  void findAll_Unlimited_ReturnsFullEvaluacionList() {
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
    Assertions.assertThat(page.getContent()).hasSize(100);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  void findAll_WithPaging_ReturnsPage() {
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
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Evaluacion evaluacion = page.getContent().get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(evaluacion.getMemoria().getTitulo(), Language.ES))
          .isEqualTo("Memoria" + String.format("%03d", j));
      Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen" + String.format("%03d", j));
      Assertions.assertThat(evaluacion.getConvocatoriaReunion().getId()).isEqualTo(1L);
    }
  }

  @Test
  void findAllActivasByConvocatoriaReunionId_Unlimited_ReturnsFullEvaluacionList() {

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
  void findAllActivasByConvocatoriaReunionId_Unlimited_ReturnEmptyPage() {

    // given: No hay datos con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    BDDMockito
        .given(evaluacionRepository.findAllByActivoTrueAndConvocatoriaReunionIdAndEsRevMinimaFalse(
            ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos las datos
    Page<Evaluacion> result = evaluacionService.findAllActivasByConvocatoriaReunionId(convocatoriaReunionId,
        Pageable.unpaged());

    // then: Se recupera lista vacía
    Assertions.assertThat(result.isEmpty());
  }

  @Test
  void findAllActivasByConvocatoriaReunionId_WithPaging_ReturnsPage() {

    // given: Datos existentes con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    List<Evaluacion> response = new LinkedList<Evaluacion>();
    response.add(generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 1L, 1L));
    response.add(generarMockEvaluacion(Long.valueOf(3), String.format("%03d", 3), 1L, 1L));
    response.add(generarMockEvaluacion(Long.valueOf(5), String.format("%03d", 5), 1L, 1L));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(evaluacionRepository.findAllByActivoTrueAndConvocatoriaReunionIdAndEsRevMinimaFalse(
        ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<Evaluacion> result = evaluacionService.findAllActivasByConvocatoriaReunionId(convocatoriaReunionId, pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  void findAllActivasByConvocatoriaReunionId_WithPaging_ReturnEmptyPage() {

    // given: No hay datos con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;

    List<Evaluacion> response = new LinkedList<Evaluacion>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResponse = new PageImpl<>(response, pageable, response.size());

    BDDMockito.given(evaluacionRepository.findAllByActivoTrueAndConvocatoriaReunionIdAndEsRevMinimaFalse(
        ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<Evaluacion> result = evaluacionService.findAllActivasByConvocatoriaReunionId(convocatoriaReunionId, pageable);

    // then: Se recupera lista de datos paginados vacía
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void findEvaluacionesAnterioresByMemoriaMemoriaIdNull() {
    // given: EL id de la memoria sea null
    Long memoriaId = null;
    Long evaluacionId = 1L;
    Long tipoComentarioId = 1L;
    try {
      // when: se listar sus evaluaciones
      evaluacionService.findEvaluacionesAnterioresByMemoria(memoriaId, evaluacionId, tipoComentarioId,
          Pageable.unpaged());
      Assertions.fail("Identificador de Memoria no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Memoria no puede ser nulo");
    }
  }

  @Test
  void findEvaluacionesAnterioresByMemoriaEvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    Long memoriaId = 1L;
    Long evaluacionId = null;
    Long tipoComentarioId = 1L;
    try {
      // when: se listar sus evaluaciones
      evaluacionService.findEvaluacionesAnterioresByMemoria(memoriaId, evaluacionId, tipoComentarioId,
          Pageable.unpaged());
      Assertions.fail("Identificador de Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Evaluación no puede ser nulo");
    }
  }

  @Test
  void findEvaluacionesAnterioresByMemoriaIdValid() {
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

    // página 1 con 2 elementos por página
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

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  void findAllByMemoriaAndRetrospectivaEnEvaluacion_ReturnsFiltratedEvaluacionList() {
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
    Assertions.assertThat(page.getContent()).hasSize(100);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  void findAllByMemoriaAndRetrospectivaEnEvaluacion_WithPaging_ReturnsPage() {
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
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Evaluacion evaluacion = page.getContent().get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(evaluacion.getMemoria().getTitulo(), Language.ES))
          .isEqualTo("Memoria" + String.format("%03d", j));
      Assertions.assertThat(evaluacion.getDictamen().getNombre()).isEqualTo("Dictamen" + String.format("%03d", j));
    }
  }

  @Test
  void findByEvaluadorPersonaRef_IdNull() {
    // given: Es personaRef es null
    String personaRef = null;
    try {
      // when: se quiere listar sus evaluaciones
      evaluacionService.findByEvaluador(personaRef, null, Pageable.unpaged());
      Assertions.fail("Referencia Persona de Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Referencia Persona de Evaluación no puede ser nulo");
    }
  }

  @Test
  void findByEvaluador_IdValid() {
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
  void findEvaluacionesEnSeguimientosByEvaluador_PersonaRefNull() {
    // given: Es personaRef es null
    String personaRef = null;
    try {
      // when: se quiere listar sus evaluaciones
      evaluacionService.findEvaluacionesEnSeguimientosByEvaluador(personaRef, null, Pageable.unpaged());
      Assertions.fail("Referencia Persona de Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Referencia Persona de Evaluación no puede ser nulo");
    }
  }

  @Test
  void findEvaluacionesEnSeguimientosByEvaluador_Success() {
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
  void findByEvaluacionesEnSeguimientoFinal_ReturnsList() {
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

    BDDMockito.given(evaluacionRepository.findByEvaluacionesEnSeguimientoAnualOrFinal(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(evaluaciones));

    // when: find unlimited
    Page<Evaluacion> page = evaluacionService.findByEvaluacionesEnSeguimientoAnualOrFinal(null, Pageable.unpaged());

    // then: Get a page with ten Evaluaciones
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(10);
  }

  @Test
  void deleteEvaluacion_fecha_anterior() {

    Evaluacion evaluacion = generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 1L, 2L);

    Optional<Evaluacion> response = Optional.of(evaluacion);
    BDDMockito.given(evaluacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(response);

    try {

      evaluacionService.deleteEvaluacion(response.get().getConvocatoriaReunion().getId(), response.get().getId());
    } catch (

    IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Fecha Actual debe ser anterior Fecha Convocatoria");
    }

  }

  @Test
  void deleteEvaluacion_ConvocatoriaReunionIsNotValid() {
    // given: idConvocatoriaReunion = 2L
    Long idConvocatoriaReunion = 2L;
    final Evaluacion evaluacion = generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 1L, 2L);
    final Optional<Evaluacion> response = Optional.of(evaluacion);
    BDDMockito.given(evaluacionRepository.findById(ArgumentMatchers.anyLong())).willReturn(response);

    try {

      evaluacionService.deleteEvaluacion(idConvocatoriaReunion, response.get().getId());
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluación no pertenece a Convocatoria Reunión");
    }

  }

  @Test
  void findAllByMemoriaId_ReturnsFullEvaluacionList() {

    // given: Datos existentes con memoriaId = 1
    Set<MemoriaTitulo> mTitulo = new HashSet<>();
    mTitulo.add(new MemoriaTitulo(Language.ES, "Memoria"));
    Memoria memoria = new Memoria();
    memoria.setId(1L);
    memoria.setNumReferencia("numRef-001");
    memoria.setTitulo(mTitulo);
    memoria.setPersonaRef("user-001");
    memoria.setFechaEnvioSecretaria(Instant.now());
    memoria.setRequiereRetrospectiva(Boolean.TRUE);
    memoria.setVersion(3);
    memoria.setActivo(Boolean.TRUE);

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
  void findAllByMemoriaId_ReturnEmptyPage() {

    Set<MemoriaTitulo> mTitulo = new HashSet<>();
    mTitulo.add(new MemoriaTitulo(Language.ES, "Memoria"));
    Memoria memoria = new Memoria();
    memoria.setId(1L);
    memoria.setNumReferencia("numRef-001");
    memoria.setTitulo(mTitulo);
    memoria.setPersonaRef("user-001");
    memoria.setFechaEnvioSecretaria(Instant.now());
    memoria.setRequiereRetrospectiva(Boolean.TRUE);
    memoria.setVersion(3);
    memoria.setActivo(Boolean.TRUE);

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

    // then: Se recupera lista de datos paginados vacía
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void findAllByMemoriaId_IdNull() {
    // given: Es memoriaId es null
    Long memoriaId = null;
    try {
      // when: se quiere listar sus evaluaciones
      evaluacionService.findAllByMemoriaId(memoriaId, Pageable.unpaged());
      Assertions.fail("Identificador de Memoria no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Memoria no puede ser nulo");
    }
  }

  /**
   * Función que devuelve un objeto Evaluacion
   * 
   * @param id                    id del Evaluacion
   * @param sufijo                el sufijo para título y nombre
   * @param idTipoEstadoMemoria   id del tipo de estado de la memoria
   * @param idEstadoRetrospectiva id del estado de la retrospectiva
   * @return el objeto Evaluacion
   */

  private Evaluacion generarMockEvaluacion(final Long id, final String sufijo, final Long idTipoEstadoMemoria,
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

    Set<PeticionEvaluacionTitulo> peTitulo = new HashSet<>();
    peTitulo.add(new PeticionEvaluacionTitulo(Language.ES, "PeticionEvaluacion1"));
    Set<PeticionEvaluacionResumen> resumen = new HashSet<>();
    resumen.add(new PeticionEvaluacionResumen(Language.ES, "Resumen"));
    Set<PeticionEvaluacionObjetivos> objetivos = new HashSet<>();
    objetivos.add(new PeticionEvaluacionObjetivos(Language.ES, "Objetivos1"));
    Set<PeticionEvaluacionDisMetodologico> disMetodologico = new HashSet<>();
    disMetodologico.add(new PeticionEvaluacionDisMetodologico(Language.ES, "DiseñoMetodologico1"));
    final PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico(disMetodologico);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos(objetivos);
    peticionEvaluacion.setResumen(resumen);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(peTitulo);
    peticionEvaluacion.setPersonaRef("user-001");
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    final Comite comite = new Comite();
    comite.setId(1L);
    comite.setCodigo("Comite1");
    comite.setActivo(Boolean.TRUE);

    final TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(idTipoEstadoMemoria); // En elaboración

    final EstadoRetrospectiva estadoRetrospectiva = new EstadoRetrospectiva();
    estadoRetrospectiva.setId(idEstadoRetrospectiva); // Pendiente

    final Retrospectiva retrospectiva = new Retrospectiva();
    retrospectiva.setId(id);
    retrospectiva.setEstadoRetrospectiva(estadoRetrospectiva);
    retrospectiva.setFechaRetrospectiva(Instant.now());

    Set<MemoriaTitulo> mTitulo = new HashSet<>();
    mTitulo.add(new MemoriaTitulo(Language.ES, "Memoria" + sufijoStr));
    final Memoria memoria = new Memoria();
    memoria.setId(1L);
    memoria.setNumReferencia("numRef-001");
    memoria.setPeticionEvaluacion(peticionEvaluacion);
    memoria.setComite(comite);
    memoria.setTitulo(mTitulo);
    memoria.setPersonaRef("user-00" + id);
    memoria.setTipo(Memoria.Tipo.NUEVA);
    memoria.setEstadoActual(tipoEstadoMemoria);
    memoria.setFechaEnvioSecretaria(Instant.now());
    memoria.setRequiereRetrospectiva(Boolean.TRUE);
    memoria.setRetrospectiva(retrospectiva);
    memoria.setVersion(3);
    memoria.setActivo(Boolean.TRUE);

    final TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    Set<ConvocatoriaReunionLugar> lugar = new HashSet<>();
    lugar.add(new ConvocatoriaReunionLugar(Language.ES, "Lugar"));
    Set<ConvocatoriaReunionOrdenDia> ordenDia = new HashSet<>();
    ordenDia.add(new ConvocatoriaReunionOrdenDia(Language.ES,
        "Orden del día convocatoria reunión"));
    final ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(1L); // Ordinaria
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.parse("2020-05-10T00:00:00Z"));
    convocatoriaReunion.setFechaLimite(Instant.now());
    convocatoriaReunion.setVideoconferencia(false);
    convocatoriaReunion.setLugar(lugar);
    convocatoriaReunion.setOrdenDia(ordenDia);
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

  private Evaluacion generarMockEvaluacion_ConvocatoriaReuSeguimiento(final Long id, final String sufijo,
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

    Set<PeticionEvaluacionTitulo> peTitulo = new HashSet<>();
    peTitulo.add(new PeticionEvaluacionTitulo(Language.ES, "PeticionEvaluacion1"));
    Set<PeticionEvaluacionResumen> resumen = new HashSet<>();
    resumen.add(new PeticionEvaluacionResumen(Language.ES, "Resumen"));
    Set<PeticionEvaluacionObjetivos> objetivos = new HashSet<>();
    objetivos.add(new PeticionEvaluacionObjetivos(Language.ES, "Objetivos1"));
    Set<PeticionEvaluacionDisMetodologico> disMetodologico = new HashSet<>();
    disMetodologico.add(new PeticionEvaluacionDisMetodologico(Language.ES, "DiseñoMetodologico1"));
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico(disMetodologico);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos(objetivos);
    peticionEvaluacion.setResumen(resumen);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(peTitulo);
    peticionEvaluacion.setPersonaRef("user-001");
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Comite comite = new Comite();
    comite.setId(1L);
    comite.setCodigo("Comite1");
    comite.setActivo(Boolean.TRUE);

    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(idTipoEstadoMemoria);

    EstadoRetrospectiva estadoRetrospectiva = new EstadoRetrospectiva();
    estadoRetrospectiva.setId(idEstadoRetrospectiva);

    Retrospectiva retrospectiva = new Retrospectiva();
    retrospectiva.setId(id);
    retrospectiva.setEstadoRetrospectiva(estadoRetrospectiva);
    retrospectiva.setFechaRetrospectiva(Instant.now());

    Set<MemoriaTitulo> mTitulo = new HashSet<>();
    mTitulo.add(new MemoriaTitulo(Language.ES, "Memoria" + sufijoStr));
    Memoria memoria = new Memoria();
    memoria.setId(1L);
    memoria.setNumReferencia("numRef-001");
    memoria.setPeticionEvaluacion(peticionEvaluacion);
    memoria.setComite(comite);
    memoria.setTitulo(mTitulo);
    memoria.setPersonaRef("user-00" + id);
    memoria.setTipo(Memoria.Tipo.NUEVA);
    memoria.setEstadoActual(tipoEstadoMemoria);
    memoria.setFechaEnvioSecretaria(Instant.now());
    memoria.setRequiereRetrospectiva(Boolean.TRUE);
    memoria.setRetrospectiva(retrospectiva);
    memoria.setVersion(3);
    memoria.setActivo(Boolean.TRUE);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(3L, "Seguimiento", Boolean.TRUE);

    Set<ConvocatoriaReunionLugar> lugar = new HashSet<>();
    lugar.add(new ConvocatoriaReunionLugar(Language.ES, "Lugar"));
    Set<ConvocatoriaReunionOrdenDia> ordenDia = new HashSet<>();
    ordenDia.add(new ConvocatoriaReunionOrdenDia(Language.ES,
        "Orden del día convocatoria reunión"));
    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(1L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.parse("2020-05-10T00:00:00Z"));
    convocatoriaReunion.setFechaLimite(Instant.now());
    convocatoriaReunion.setVideoconferencia(false);
    convocatoriaReunion.setLugar(lugar);
    convocatoriaReunion.setOrdenDia(ordenDia);
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

  /**
   * Función que devuelve un objeto Memoria.
   * 
   * @param id            id del memoria.
   * @param numReferencia número de la referencia de la memoria.
   * @param titulo        titulo de la memoria.
   * @param version       version de la memoria.
   * @return el objeto Memoria
   */

  private Memoria generarMockMemoria(Long id, String numReferencia, String titulo, Integer version,
      Long idTipoEstadoMemoria) {

    Set<MemoriaTitulo> mTitulo = new HashSet<>();
    mTitulo.add(new MemoriaTitulo(Language.ES, titulo));
    Memoria memoria = new Memoria();
    memoria.setId(id);
    memoria.setNumReferencia(numReferencia);
    memoria.setPeticionEvaluacion(generarMockPeticionEvaluacion(id, titulo + " PeticionEvaluacion" + id));
    memoria.setComite(generarMockComite(id, "comite" + id, true));
    memoria.setTitulo(mTitulo);
    memoria.setPersonaRef("user-00" + id);
    memoria.setTipo(Memoria.Tipo.NUEVA);
    memoria.setEstadoActual(generarMockTipoEstadoMemoria(idTipoEstadoMemoria, "Estado", Boolean.TRUE));
    memoria.setFechaEnvioSecretaria(Instant.now());
    memoria.setRequiereRetrospectiva(Boolean.TRUE);
    memoria.setRetrospectiva(generarMockRetrospectiva(1L));
    memoria.setVersion(version);
    memoria.setActivo(Boolean.TRUE);

    return memoria;
  }

  /**
   * Función que devuelve un objeto comité.
   * 
   * @param id     identificador del comité.
   * @param comite comité.
   * @param activo indicador de activo.
   */
  private Comite generarMockComite(Long id, String codigo, Boolean activo) {
    Comite comite = new Comite();
    comite.setId(id);
    comite.setCodigo(codigo);
    comite.setActivo(activo);

    return comite;

  }

  /**
   * Función que devuelve un objeto TipoEstadoMemoria.
   * 
   * @param id     identificador del TipoEstadoMemoria.
   * @param nombre nombre.
   * @param activo indicador de activo.
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

    Set<PeticionEvaluacionTitulo> tit = new HashSet<>();
    tit.add(new PeticionEvaluacionTitulo(Language.ES, titulo));
    Set<PeticionEvaluacionResumen> resumen = new HashSet<>();
    resumen.add(new PeticionEvaluacionResumen(Language.ES, "Resumen" + id));
    Set<PeticionEvaluacionObjetivos> objetivos = new HashSet<>();
    objetivos.add(new PeticionEvaluacionObjetivos(Language.ES, "Objetivos" + id));
    Set<PeticionEvaluacionDisMetodologico> disMetodologico = new HashSet<>();
    disMetodologico.add(new PeticionEvaluacionDisMetodologico(Language.ES, "DiseñoMetodologico" + id));
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo" + id);
    peticionEvaluacion.setDisMetodologico(disMetodologico);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos(objetivos);
    peticionEvaluacion.setResumen(resumen);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria" + id);
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(tit);
    peticionEvaluacion.setPersonaRef("user-00" + id);
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    return peticionEvaluacion;
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