package org.crue.hercules.sgi.eti.service;

import static org.mockito.ArgumentMatchers.anyLong;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.dto.DocumentoOutput;
import org.crue.hercules.sgi.eti.dto.MemoriaInput;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunionLugar;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunionOrdenDia;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Informe;
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
import org.crue.hercules.sgi.eti.repository.ApartadoRepository;
import org.crue.hercules.sgi.eti.repository.BloqueRepository;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.crue.hercules.sgi.eti.repository.ComiteRepository;
import org.crue.hercules.sgi.eti.repository.DocumentacionMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.EstadoMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.FormularioRepository;
import org.crue.hercules.sgi.eti.repository.InformeDocumentoRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.PeticionEvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.RespuestaRepository;
import org.crue.hercules.sgi.eti.repository.TareaRepository;
import org.crue.hercules.sgi.eti.service.impl.MemoriaServiceImpl;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiRepService;
import org.crue.hercules.sgi.framework.i18n.I18nConfig;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * MemoriaServiceTest
 */
class MemoriaServiceTest extends BaseServiceTest {

  @Mock
  private MemoriaRepository memoriaRepository;

  @Mock
  private EstadoMemoriaRepository estadoMemoriaRepository;

  @Mock
  private MemoriaService memoriaService;

  @Mock
  private EvaluacionRepository evaluacionRepository;

  @Mock
  private ComentarioRepository comentarioRepository;

  @Mock
  private PeticionEvaluacionRepository peticionEvaluacionRepository;

  @Mock
  private ComiteRepository comiteRepository;

  @Mock
  private DocumentacionMemoriaRepository documentacionMemoriaRepository;

  @Mock
  private RespuestaRepository respuestaRepository;

  @Mock
  private InformeService informeFormularioService;

  @Mock
  private TareaRepository tareaRepository;

  @Mock
  private InformeService informeService;

  @Mock
  private SgiApiRepService reportService;

  @Mock
  private SgdocService sgdocService;

  @Mock
  private ConfiguracionService configuracionService;

  @Mock
  private BloqueRepository bloqueRepository;

  @Mock
  private ApartadoRepository apartadoRepository;

  @Mock
  private ComunicadosService comunicadosService;

  @Mock
  private RetrospectivaService retrospectivaService;

  @Mock
  private InformeDocumentoRepository informeDocumentoRepository;
  @Mock
  private FormularioRepository formularioRepository;
  @Mock
  private SgiApiCnfService cnfService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private I18nConfig i18nConfig;

  @BeforeEach
  public void setUp() throws Exception {
    memoriaService = new MemoriaServiceImpl(sgiConfigProperties, memoriaRepository, estadoMemoriaRepository,
        evaluacionRepository, comentarioRepository, informeService,
        peticionEvaluacionRepository, comiteRepository, documentacionMemoriaRepository, respuestaRepository,
        tareaRepository, configuracionService, reportService, sgdocService, bloqueRepository, apartadoRepository,
        comunicadosService, retrospectivaService, informeDocumentoRepository, formularioRepository, cnfService,
        messageSource, i18nConfig);
  }

  @Test
  void find_WithId_ReturnsMemoria() {

    BDDMockito.given(memoriaRepository.findById(1L))
        .willReturn(Optional.of(generarMockMemoria(1L, "numRef-5598", "Memoria1", 1, 1L)));

    Memoria memoria = memoriaService.findById(1L);

    Assertions.assertThat(memoria.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(memoria.getTitulo(), Language.ES)).isEqualTo("Memoria1");
    Assertions.assertThat(memoria.getVersion()).isEqualTo(1);
    Assertions.assertThat(memoria.getNumReferencia()).isEqualTo("numRef-5598");

  }

  @Test
  void find_NotFound_ThrowsMemoriaNotFoundException() throws Exception {
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> memoriaService.findById(1L)).isInstanceOf(MemoriaNotFoundException.class);
  }

  @Test
  void findByComite_WithId_ReturnsMemoria() {

    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.of(new Comite()));

    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-5" + String.format("%03d", i),
          "Memoria" + String.format("%03d", i), 1, 1L));
    }

    BDDMockito.given(memoriaRepository.findAllMemoriasPeticionEvaluacionModificables(1L, 1L,
        Pageable.unpaged())).willReturn(new PageImpl<>(memorias));

    // when: find unlimited
    Page<Memoria> page = memoriaService.findAllMemoriasPeticionEvaluacionModificables(1L, 1L, Pageable.unpaged());
    // then: Get a page with one hundred Memorias
    Assertions.assertThat(page.getContent()).hasSize(100);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);

  }

  @Test
  void findByComite_NotFound_ThrowsComiteNotFoundException() throws Exception {
    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> memoriaService.findAllMemoriasPeticionEvaluacionModificables(1L, 1L, null))
        .isInstanceOf(ComiteNotFoundException.class);
  }

  @Test
  void findAllMemoriasPeticionEvaluacionModificables_ComiteIdNull() throws Exception {

    try {
      // when: Creamos la memoria
      memoriaService.findAllMemoriasPeticionEvaluacionModificables(null, 1L, null);
      Assertions.fail("Identificador de Comité no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Comité no puede ser nulo");
    }
  }

  @Test
  void findAllMemoriasPeticionEvaluacionModificables_PeticionEvaluacionIdNull() throws Exception {

    try {
      // when: Creamos la memoria
      memoriaService.findAllMemoriasPeticionEvaluacionModificables(1L, null, null);
      Assertions.fail(
          "Identificador de Petición Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "Identificador de Petición Evaluación no puede ser nulo");
    }
  }

  @Test
  void create_ReturnsMemoriaTipoMemoriaNuevo() {
    // given: Una nueva Memoria
    MemoriaInput memoriaNew = generarMockMemoriaInput(1L, 1L, "MemoriaNew", Memoria.Tipo.NUEVA);

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "MemoriaNew", 1, 1L);

    BDDMockito.given(peticionEvaluacionRepository.findByIdAndActivoTrue(memoriaNew.getPeticionEvaluacionId()))
        .willReturn(Optional.of(memoria.getPeticionEvaluacion()));
    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(memoriaNew.getComiteId()))
        .willReturn(Optional.of(memoria.getComite()));

    BDDMockito.given(memoriaRepository.save(ArgumentMatchers.<Memoria>any())).willReturn(memoria);

    // when: Creamos la memoria
    Memoria memoriaCreado = memoriaService.create(memoriaNew);

    // then: La memoria se crea correctamente
    Assertions.assertThat(memoriaCreado).isNotNull();
    Assertions.assertThat(memoriaCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(memoriaCreado.getTitulo(), Language.ES))
        .isEqualTo("MemoriaNew");
    Assertions.assertThat(memoriaCreado.getNumReferencia()).isEqualTo("numRef-5598");
  }

  @Test
  void create_ReturnsMemoriaTipoMemoriaRatificacion() {
    // given: Una nueva Memoria
    MemoriaInput memoriaNew = generarMockMemoriaInput(1L, 1L, "MemoriaNew", Memoria.Tipo.NUEVA);
    memoriaNew.setTipo(Memoria.Tipo.RATIFICACION);

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "MemoriaNew", 1, 1L);

    BDDMockito.given(peticionEvaluacionRepository.findByIdAndActivoTrue(memoriaNew.getPeticionEvaluacionId()))
        .willReturn(Optional.of(memoria.getPeticionEvaluacion()));
    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(memoriaNew.getComiteId()))
        .willReturn(Optional.of(memoria.getComite()));

    BDDMockito.given(memoriaRepository.save(ArgumentMatchers.<Memoria>any())).willReturn(memoria);

    // when: Creamos la memoria
    Memoria memoriaCreado = memoriaService.create(memoriaNew);

    // then: La memoria se crea correctamente
    Assertions.assertThat(memoriaCreado).isNotNull();
    Assertions.assertThat(memoriaCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(memoriaCreado.getTitulo(), Language.ES))
        .isEqualTo("MemoriaNew");
    Assertions.assertThat(memoriaCreado.getNumReferencia()).isEqualTo("numRef-5598");
  }

  @Test
  void create_ThrowPeticionEvaluacionNotFound() {
    // given: Una nueva Memoria
    MemoriaInput memoriaNew = generarMockMemoriaInput(1L, 1L, "MemoriaNew", Memoria.Tipo.NUEVA);

    BDDMockito.given(peticionEvaluacionRepository.findByIdAndActivoTrue(memoriaNew.getPeticionEvaluacionId()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> memoriaService.create(memoriaNew))
        .isInstanceOf(PeticionEvaluacionNotFoundException.class);

  }

  @Test
  void create_ThrowComiteNotFound() {
    // given: Una nueva Memoria
    MemoriaInput memoriaNew = generarMockMemoriaInput(1L, 1L, "MemoriaNew", Memoria.Tipo.NUEVA);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);

    BDDMockito.given(peticionEvaluacionRepository.findByIdAndActivoTrue(memoriaNew.getPeticionEvaluacionId()))
        .willReturn(Optional.of(peticionEvaluacion));
    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(memoriaNew.getComiteId()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> memoriaService.create(memoriaNew)).isInstanceOf(ComiteNotFoundException.class);

  }

  @Test
  void create_FailTipoMemoria() {
    // given: Una nueva Memoria
    MemoriaInput memoriaNew = generarMockMemoriaInput(1L, 1L, "MemoriaNew", Memoria.Tipo.NUEVA);
    memoriaNew.setTipo(Memoria.Tipo.MODIFICACION);

    try {
      // when: Creamos la memoria
      memoriaService.create(memoriaNew);
      Assertions.fail("La memoria no es del tipo adecuado para realizar una copia a partir de otra memoria");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("La memoria no es del tipo adecuado para realizar una copia a partir de otra memoria");
    }

  }

  @Test
  void createModificada_ReturnsMemoria() {
    // given: Una nueva Memoria
    MemoriaInput memoriaNew = generarMockMemoriaInput(1L, 1L, "MemoriaNew", Memoria.Tipo.MODIFICACION);
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);
    Comite comite = new Comite();
    comite.setId(1L);
    comite.setFormularioRetrospectivaId(1L);

    Memoria memoriaOld = generarMockMemoria(2L, "M10/2020/001", "MemoriaNew", 1, 1L);

    Memoria memoria = generarMockMemoria(3L, "M10/2020/001MR1", "MemoriaNew", 1, 1L);
    memoria.setMemoriaOriginal(memoriaOld);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(memoriaOld.getId())).willReturn(Optional.of(memoriaOld));

    BDDMockito.given(peticionEvaluacionRepository.findByIdAndActivoTrue(memoriaNew.getPeticionEvaluacionId()))
        .willReturn(Optional.of(peticionEvaluacion));
    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(memoriaNew.getComiteId()))
        .willReturn(Optional.of(comite));

    BDDMockito
        .given(memoriaRepository
            .findFirstByNumReferenciaContainingAndComiteIdOrderByNumReferenciaDesc(memoriaOld.getNumReferencia(),
                memoriaOld.getId()))
        .willReturn((memoriaOld));

    BDDMockito.given(documentacionMemoriaRepository.findByMemoriaIdAndMemoriaActivoTrue(memoriaOld.getId(), null))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    BDDMockito.given(respuestaRepository.findByMemoriaIdAndMemoriaActivoTrue(memoriaOld.getId(), null))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    BDDMockito.given(memoriaRepository.save(ArgumentMatchers.<Memoria>any())).willReturn(memoria);

    // when: Creamos la memoria
    Memoria memoriaCreado = memoriaService.createModificada(memoriaNew, memoriaOld.getId());

    // then: La memoria se crea correctamente
    Assertions.assertThat(memoriaCreado).isNotNull();
    Assertions.assertThat(memoriaCreado.getId()).isEqualTo(3L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(memoriaCreado.getTitulo(), Language.ES))
        .isEqualTo("MemoriaNew");
    Assertions.assertThat(memoriaCreado.getNumReferencia()).isEqualTo("M10/2020/001MR1");
  }

  @Test
  void createModificada_ThrowPeticionEvaluacionNotFound() {
    // given: Una nueva Memoria
    MemoriaInput memoriaNew = generarMockMemoriaInput(1L, 1L, "MemoriaNew", Memoria.Tipo.MODIFICACION);
    Memoria memoria = generarMockMemoria(2L, "M10/2020/002", "MemoriaNew", 1, 1L);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(memoria.getId())).willReturn(Optional.of(memoria));
    BDDMockito.given(peticionEvaluacionRepository.findByIdAndActivoTrue(memoriaNew.getPeticionEvaluacionId()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> memoriaService.createModificada(memoriaNew, memoria.getId()))
        .isInstanceOf(PeticionEvaluacionNotFoundException.class);

  }

  @Test
  void createModificada_ThrowComiteNotFound() {
    // given: Una nueva Memoria
    MemoriaInput memoriaNew = generarMockMemoriaInput(1L, 1L, "MemoriaNew", Memoria.Tipo.MODIFICACION);
    Memoria memoria = generarMockMemoria(2L, "M10/2020/002", "MemoriaNew", 1, 1L);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(memoria.getId())).willReturn(Optional.of(memoria));
    BDDMockito.given(peticionEvaluacionRepository.findByIdAndActivoTrue(memoriaNew.getPeticionEvaluacionId()))
        .willReturn(Optional.of(peticionEvaluacion));
    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(memoriaNew.getComiteId()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> memoriaService.createModificada(memoriaNew, memoria.getId()))
        .isInstanceOf(ComiteNotFoundException.class);

  }

  @Test
  void createModificada_FailTipoMemoria() {
    // given: Una nueva Memoria
    MemoriaInput memoriaNew = generarMockMemoriaInput(1L, 1L, "MemoriaNew", Memoria.Tipo.NUEVA);

    try {
      // when: Creamos la memoria
      memoriaService.createModificada(memoriaNew, 2L);
      Assertions.fail("La memoria no es del tipo adecuado para realizar una copia a partir de otra memoria");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("La memoria no es del tipo adecuado para realizar una copia a partir de otra memoria");
    }

  }

  @Test
  void update_ResponsableMemoria_ReturnsMemoria() {
    // given: Una Memoria
    Memoria memoriaServicioActualizado = generarMockMemoria(1L, "numRef-99", "Memoria 1 actualizada", 1, 2L);
    memoriaServicioActualizado.setPersonaRef("personaRef");

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "Memoria1", 1, 3L);

    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));
    BDDMockito.given(memoriaRepository.save(memoria)).willReturn(memoriaServicioActualizado);

    // when: Actualizamos la Memoria
    Memoria memoriaActualizado = memoriaService.update(memoria);

    // then: La Memoria se actualiza correctamente.
    Assertions.assertThat(memoriaActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(memoriaActualizado.getPersonaRef()).isEqualTo("personaRef");
  }

  @Test
  void update_ThrowsMemoriaNotFoundException() {
    // given: Una nueva Memoria a actualizar
    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "Memoria1", 1, 1L);

    // then: Lanza una excepcion porque la Memoria no existe
    Assertions.assertThatThrownBy(() -> memoriaService.update(memoria)).isInstanceOf(MemoriaNotFoundException.class);

  }

  @Test
  void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Una Memoria que venga sin id
    Memoria memoria = generarMockMemoria(null, "numRef-5598", "Memoria1", 1, 1L);

    Assertions.assertThatThrownBy(
        // when: update Memoria
        () -> memoriaService.update(memoria))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void findAll_Unlimited_ReturnsFullMemoriaPeticionEvaluacionist() {
    // given: One hundred Memoria
    List<MemoriaPeticionEvaluacion> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockMemoriaPeticionEvaluacion(Long.valueOf(i)));
    }

    BDDMockito.given(memoriaRepository.findAllMemoriasEvaluaciones(ArgumentMatchers.<Specification<Memoria>>any(),
        ArgumentMatchers.<Pageable>any(), ArgumentMatchers.<String>any())).willReturn(new PageImpl<>(memorias));

    // when: find unlimited
    Page<MemoriaPeticionEvaluacion> page = memoriaService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred Memorias
    Assertions.assertThat(page.getContent()).hasSize(100);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  void findAll_WithPaging_ReturnsPage() {
    // given: One hundred Memorias
    List<MemoriaPeticionEvaluacion> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockMemoriaPeticionEvaluacion(Long.valueOf(i)));
    }

    BDDMockito
        .given(memoriaRepository.findAllMemoriasEvaluaciones(ArgumentMatchers.<Specification<Memoria>>any(),
            ArgumentMatchers.<Pageable>any(), ArgumentMatchers.<String>any()))
        .willAnswer(new Answer<Page<MemoriaPeticionEvaluacion>>() {
          @Override
          public Page<MemoriaPeticionEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<MemoriaPeticionEvaluacion> content = memorias.subList(fromIndex, toIndex);
            Page<MemoriaPeticionEvaluacion> page = new PageImpl<>(content, pageable, memorias.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<MemoriaPeticionEvaluacion> page = memoriaService.findAll(null, paging);

    // then: A Page with ten Memorias are returned containing
    // titulo='Memoria031' to 'Memoria040'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      MemoriaPeticionEvaluacion memoria = page.getContent().get(i);
      Assertions.assertThat(memoria.getNumReferencia()).isEqualTo("numRef-" + String.format("%03d", j));
    }
  }

  @Test
  void findAllMemoriasAsignablesConvocatoria_Unlimited_ReturnsFullMemoriaList() {
    // given: idConvocatoria, One hundred Memoria
    Long idConvocatoria = 1L;
    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-5" + String.format("%03d", i),
          "Memoria" + String.format("%03d", i), 1, 1L));
    }

    BDDMockito.given(memoriaRepository.findAllMemoriasAsignablesConvocatoria(ArgumentMatchers.anyLong()))
        .willReturn(memorias);

    // when: find unlimited asignables by convocatoria
    List<Memoria> result = memoriaService.findAllMemoriasAsignablesConvocatoria(idConvocatoria);

    // then: Get a page with one hundred Memorias
    Assertions.assertThat(result).hasSize(10);
  }

  @Test
  void findAllMemoriasAsignablesConvocatoria_WithPaging_ReturnsPage() {
    // given: idConvocatoria, One hundred Memoria
    Long idConvocatoria = 1L;
    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-5" + String.format("%03d", i),
          "Memoria" + String.format("%03d", i), 1, 1L));
    }

    BDDMockito.given(memoriaRepository.findAllMemoriasAsignablesConvocatoria(ArgumentMatchers.anyLong()))
        .willReturn(memorias);

    List<Memoria> result = memoriaService.findAllMemoriasAsignablesConvocatoria(idConvocatoria);

    // then: A Page with ten Memorias are returned containing
    // titulo='Memoria031' to 'Memoria040'
    Assertions.assertThat(result).hasSize(10);
    for (int i = 0, j = 1; i < 10; i++, j++) {
      Memoria memoria = result.get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(memoria.getTitulo(), Language.ES))
          .isEqualTo("Memoria" + String.format("%03d", j));
      Assertions.assertThat(memoria.getNumReferencia()).isEqualTo("numRef-5" + String.format("%03d", j));
    }
  }

  @Test
  void findAllAsignablesTipoConvocatoriaOrdExt_Unlimited_ReturnsFullMemoriaList() {

    // given: search query with comité y fecha límite de una convocatoria de tipo
    // ordinario o extraordinario
    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-5" + String.format("%03d", i),
          "Memoria" + String.format("%03d", i), 1, 1L));
    }

    BDDMockito
        .given(
            memoriaRepository.findAll(ArgumentMatchers.<Specification<Memoria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(memorias));

    // when: find unlimited asignables para tipo convocatoria ordinaria o
    // extraordinaria
    Page<Memoria> page = memoriaService.findAllAsignablesTipoConvocatoriaOrdExt(null, Pageable.unpaged());

    // then: Obtiene las
    // memorias en estado "En secretaria" con la fecha de envío es igual o menor a
    // la fecha límite de la convocatoria de reunión y las que tengan una
    // retrospectiva en estado "En secretaría".
    Assertions.assertThat(page.getContent()).hasSize(100);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  void findAllAsignablesTipoConvocatoriaSeguimiento_Unlimited_ReturnsFullMemoriaList() {

    // given: search query with comité y fecha límite de una convocatoria de tipo
    // seguimiento
    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-5" + String.format("%03d", i),
          "Memoria" + String.format("%03d", i), 1, 1L));
    }

    BDDMockito
        .given(
            memoriaRepository.findAll(ArgumentMatchers.<Specification<Memoria>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(memorias));

    // when: find unlimited asignables para tipo convocatoria seguimiento
    Page<Memoria> page = memoriaService.findAllAsignablesTipoConvocatoriaSeguimiento(null, Pageable.unpaged());

    // then: Obtiene Memorias en estado
    // "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
    // fecha de envío es igual o menor a la fecha límite de la convocatoria de
    // reunión.
    Assertions.assertThat(page.getContent()).hasSize(100);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  void findMemoriaByPeticionEvaluacionMaxVersion_Unlimited_ReturnsFullMemoriaPeticionEvaluacionList() {

    List<MemoriaPeticionEvaluacion> memorias = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      memorias.add(generarMockMemoriaPeticionEvaluacion(Long.valueOf(i)));
    }

    BDDMockito
        .given(memoriaRepository.findMemoriasEvaluacion(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any()))
        .willReturn(memorias);

    // when: find unlimited Memorias de petición evaluación
    List<MemoriaPeticionEvaluacion> result = memoriaService.findMemoriaByPeticionEvaluacionMaxVersion(1L);

    // then: Obtiene Memorias de petición evaluación con sus fecha límite y de
    // evaluación

    Assertions.assertThat(result).hasSize(10);
  }

  @Test
  void findMemoriaByPeticionEvaluacionMaxVersion_WithPaging_ReturnsPage() {
    // given: idPEticionEvaluacion, One hundred MemoriaPeticionEvaluacion
    Long idPeticionEvaluacion = 1L;
    List<MemoriaPeticionEvaluacion> memorias = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      memorias.add(generarMockMemoriaPeticionEvaluacion(Long.valueOf(i)));
    }

    BDDMockito
        .given(memoriaRepository.findMemoriasEvaluacion(ArgumentMatchers.anyLong(), ArgumentMatchers.<String>any()))
        .willReturn(memorias);

    List<MemoriaPeticionEvaluacion> result = memoriaService
        .findMemoriaByPeticionEvaluacionMaxVersion(idPeticionEvaluacion);

    // then: A Page with ten Memorias are returned containing
    // num referencia='NumRef-031' to 'NumRef-040'
    Assertions.assertThat(result).hasSize(10);
    for (int i = 0, j = 1; i < 10; i++, j++) {
      MemoriaPeticionEvaluacion memoria = result.get(i);
      Assertions.assertThat(memoria.getNumReferencia()).isEqualTo("numRef-" + String.format("%03d", j));
    }
  }

  @Test
  void findAllMemoriasWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria_Unlimited_ReturnsFullMemoriaPeticionEvaluacionList() {

    List<MemoriaPeticionEvaluacion> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockMemoriaPeticionEvaluacion(Long.valueOf(i)));
    }

    BDDMockito.given(memoriaRepository.findAllMemoriasEvaluaciones(ArgumentMatchers.<Specification<Memoria>>any(),
        ArgumentMatchers.<Pageable>any(), ArgumentMatchers.<String>any())).willReturn(new PageImpl<>(memorias));

    // when: find unlimited Memorias de petición evaluación
    Page<MemoriaPeticionEvaluacion> page = memoriaService
        .findAllMemoriasWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(null, Pageable.unpaged(),
            "user-001");

    // then: Obtiene Memorias de petición evaluación con sus fecha límite y de
    // evaluación

    Assertions.assertThat(page.getContent()).hasSize(100);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  void findAllMemoriasWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria_WithPaging_ReturnsPage() {
    // given: One hundred MemoriaPeticionEvaluacion
    List<MemoriaPeticionEvaluacion> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockMemoriaPeticionEvaluacion(Long.valueOf(i)));
    }

    BDDMockito
        .given(memoriaRepository.findAllMemoriasEvaluaciones(ArgumentMatchers.<Specification<Memoria>>any(),
            ArgumentMatchers.<Pageable>any(), ArgumentMatchers.<String>any()))
        .willAnswer(new Answer<Page<MemoriaPeticionEvaluacion>>() {
          @Override
          public Page<MemoriaPeticionEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<MemoriaPeticionEvaluacion> content = memorias.subList(fromIndex, toIndex);
            Page<MemoriaPeticionEvaluacion> page = new PageImpl<>(content, pageable, memorias.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10 asignables by convocatoria
    Pageable paging = PageRequest.of(3, 10);
    Page<MemoriaPeticionEvaluacion> page = memoriaService
        .findAllMemoriasWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(null, paging, "user-001");

    // then: A Page with ten Memorias are returned containing
    // num referencia='NumRef-031' to 'NumRef-040'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      MemoriaPeticionEvaluacion memoria = page.getContent().get(i);
      Assertions.assertThat(memoria.getNumReferencia()).isEqualTo("numRef-" + String.format("%03d", j));
    }
  }

  @Test
  void getEstadoAnteriorMemoria_returnMemoria() {

    List<EstadoMemoria> estados = new ArrayList<EstadoMemoria>();
    estados.addAll(generarEstadosMemoria(1L));
    estados.addAll(generarEstadosMemoria(2L));

    BDDMockito.given(estadoMemoriaRepository.findAllByMemoriaIdOrderByFechaEstadoDesc(ArgumentMatchers.anyLong()))
        .willReturn(estados);

    // when: find unlimited asignables para tipo convocatoria seguimiento
    Memoria returnMemoria = memoriaService
        .getMemoriaWithEstadoAnterior(generarMockMemoria(1L, "ref-001", "TituloMemoria", 1, 2L));

    Assertions.assertThat(returnMemoria.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(returnMemoria.getTitulo(), Language.ES))
        .isEqualTo("TituloMemoria");
  }

  @Test
  void updateEstadoAnteriorMemoriaEnEvaluacion_returnsMemoriaWithEstadoEnSecretaria() {
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(TipoEstadoMemoria.Tipo.EN_EVALUACION.getId());

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "Memoria1", 1, 3L);
    memoria.setEstadoActual(tipoEstadoMemoria);

    List<EstadoMemoria> estados = new ArrayList<EstadoMemoria>();
    estados.addAll(generarEstadosMemoria(TipoEstadoMemoria.Tipo.EN_EVALUACION.getId()));
    estados.addAll(generarEstadosMemoria(TipoEstadoMemoria.Tipo.EN_SECRETARIA.getId()));
    estados.addAll(generarEstadosMemoria(TipoEstadoMemoria.Tipo.COMPLETADA.getId()));
    estados.addAll(generarEstadosMemoria(TipoEstadoMemoria.Tipo.EN_ELABORACION.getId()));

    BDDMockito.given(estadoMemoriaRepository.findAllByMemoriaIdOrderByFechaEstadoDesc(ArgumentMatchers.anyLong()))
        .willReturn(estados);

    Evaluacion evaluacion = generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 6L, 1L, 1);
    evaluacion.setDictamen(null);
    BDDMockito
        .given(evaluacionRepository
            .findFirstByMemoriaIdAndActivoTrueOrderByVersionDescCreationDateDesc(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(evaluacion));

    BDDMockito.given(memoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(memoria));
    BDDMockito.given(memoriaRepository.save(memoria)).willReturn(memoria);

    Memoria memoriaEstadoActualizado = memoriaService.updateEstadoAnteriorMemoria(1L);

    Assertions.assertThat(memoriaEstadoActualizado).isNotNull();
    Assertions.assertThat(memoriaEstadoActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(memoriaEstadoActualizado.getEstadoActual().getId())
        .isEqualTo(TipoEstadoMemoria.Tipo.EN_SECRETARIA.getId());
  }

  @Test
  void updateEstadoAnteriorMemoriaEnSecretaria_returnsMemoriaWithEstadoCompletada() {
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(TipoEstadoMemoria.Tipo.EN_SECRETARIA.getId());

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "Memoria1", 1, 3L);
    memoria.setEstadoActual(tipoEstadoMemoria);

    List<EstadoMemoria> estados = new ArrayList<EstadoMemoria>();
    estados.addAll(generarEstadosMemoria(TipoEstadoMemoria.Tipo.EN_SECRETARIA.getId()));
    estados.addAll(generarEstadosMemoria(TipoEstadoMemoria.Tipo.COMPLETADA.getId()));
    estados.addAll(generarEstadosMemoria(TipoEstadoMemoria.Tipo.EN_ELABORACION.getId()));

    BDDMockito.given(estadoMemoriaRepository.findAllByMemoriaIdOrderByFechaEstadoDesc(ArgumentMatchers.anyLong()))
        .willReturn(estados);

    BDDMockito.doNothing().when(informeService).deleteLastInformeMemoria(ArgumentMatchers.anyLong());

    BDDMockito.given(memoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(memoria));
    BDDMockito.given(memoriaRepository.save(memoria)).willReturn(memoria);

    Memoria memoriaEstadoActualizado = memoriaService.updateEstadoAnteriorMemoria(1L);

    Assertions.assertThat(memoriaEstadoActualizado).isNotNull();
    Assertions.assertThat(memoriaEstadoActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(memoriaEstadoActualizado.getEstadoActual().getId())
        .isEqualTo(TipoEstadoMemoria.Tipo.COMPLETADA.getId());
    Mockito.verify(informeService, Mockito.times(1)).deleteLastInformeMemoria(ArgumentMatchers.anyLong());
  }

  @Test
  void updateEstadoAnterior_MemoriaNotPresent_ThrowsMemoriaNotFoundException() {
    Assertions.assertThatThrownBy(() -> memoriaService.updateEstadoAnteriorMemoria(1L))
        .isInstanceOf(MemoriaNotFoundException.class);
  }

  @Test
  public void enviarSecretaria_WithId() {

    Memoria memoria = generarMockMemoria(1L, "numRef-111", "Memoria1", 1, 6L);
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));

    Evaluacion evaluacion = generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 6L, 1L, 1);
    BDDMockito
        .given(evaluacionRepository
            .findFirstByMemoriaIdAndActivoTrueOrderByVersionDescCreationDateDesc(memoria.getId()))
        .willReturn(Optional.of(evaluacion));

    Memoria memoriaActualizada = generarMockMemoria(1L, "numRef-111", "Memoria1", 2, 4L);
    Evaluacion evaluacionNueva = generarMockEvaluacion(null, String.format("%03d", 1), 4L, 1L, 2);

    BDDMockito
        .given(informeService.create(ArgumentMatchers.any(Informe.class)))
        .willAnswer(AdditionalAnswers.returnsFirstArg());
    BDDMockito
        .given(sgdocService.uploadInforme(ArgumentMatchers.anyString(), ArgumentMatchers.nullable(Resource.class)))
        .willReturn(new DocumentoOutput());

    memoriaService.enviarSecretaria(memoria.getId(), "user-001");

    Assertions.assertThat(memoriaActualizada.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(memoriaActualizada.getTitulo(), Language.ES))
        .isEqualTo("Memoria1");
    Assertions.assertThat(memoriaActualizada.getVersion()).isEqualTo(2);
    Assertions.assertThat(memoriaActualizada.getNumReferencia()).isEqualTo("numRef-111");
    Assertions.assertThat(memoriaActualizada.getEstadoActual().getId()).isEqualTo(4L);
    Assertions.assertThat(memoriaActualizada.getPeticionEvaluacion().getPersonaRef()).isEqualTo("user-001");
    Assertions.assertThat(evaluacionNueva.getVersion()).isEqualTo(memoriaActualizada.getVersion());
    Assertions.assertThat(evaluacionNueva.getMemoria().getId()).isEqualTo(memoriaActualizada.getId());

  }

  @Test
  public void enviarSecretaria_WithId_EstadoEnAclaraciónSeguimientoFinal() {
    // given: Una nueva Memoria (21L=En Aclaración Seguimiento Final)
    Memoria memoria = generarMockMemoria(1L, "numRef-111", "Memoria1", 1, 21L);
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));

    Evaluacion evaluacion = generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 21L, 1L, 1);
    BDDMockito
        .given(evaluacionRepository
            .findFirstByMemoriaIdAndActivoTrueOrderByVersionDescCreationDateDesc(memoria.getId()))
        .willReturn(Optional.of(evaluacion));

    BDDMockito
        .given(evaluacionRepository
            .findFirstByMemoriaIdAndTipoEvaluacionIdAndActivoTrueOrderByVersionDesc(memoria.getId(), 4L))
        .willReturn(Optional.of(evaluacion));

    Memoria memoriaActualizada = generarMockMemoria(1L, "numRef-111", "Memoria1", 2, 18L);
    Evaluacion evaluacionNueva = generarMockEvaluacion(null, String.format("%03d", 1), 18L, 1L, 2);

    BDDMockito
        .given(informeService.create(ArgumentMatchers.any(Informe.class)))
        .willAnswer(AdditionalAnswers.returnsFirstArg());
    BDDMockito
        .given(sgdocService.uploadInforme(ArgumentMatchers.anyString(), ArgumentMatchers.nullable(Resource.class)))
        .willReturn(new DocumentoOutput());

    // when: enviamos la memoria
    memoriaService.enviarSecretaria(memoria.getId(), "user-001");

    // then: La memoria se envía correctamente
    Assertions.assertThat(memoriaActualizada.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(memoriaActualizada.getTitulo(), Language.ES))
        .isEqualTo("Memoria1");
    Assertions.assertThat(memoriaActualizada.getVersion()).isEqualTo(2);
    Assertions.assertThat(memoriaActualizada.getNumReferencia()).isEqualTo("numRef-111");
    Assertions.assertThat(memoriaActualizada.getEstadoActual().getId()).isEqualTo(18L);
    Assertions.assertThat(memoriaActualizada.getPeticionEvaluacion().getPersonaRef()).isEqualTo("user-001");
    Assertions.assertThat(evaluacionNueva.getVersion()).isEqualTo(memoriaActualizada.getVersion());
    Assertions.assertThat(evaluacionNueva.getMemoria().getId()).isEqualTo(memoriaActualizada.getId());

  }

  @Test
  public void enviarSecretaria_WithId_EstadoCompletadaSeguimientoAnual() {

    // given: Una nueva Memoria (11L=Completada Seguimiento Anual)
    Memoria memoria = generarMockMemoria(1L, "numRef-111", "Memoria1", 1, 11L);
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));

    Memoria memoriaActualizada = generarMockMemoria(1L, "numRef-111", "Memoria1", 2, 12L);

    BDDMockito
        .given(informeService.create(ArgumentMatchers.any(Informe.class)))
        .willAnswer(AdditionalAnswers.returnsFirstArg());
    BDDMockito
        .given(sgdocService.uploadInforme(ArgumentMatchers.anyString(), ArgumentMatchers.nullable(Resource.class)))
        .willReturn(new DocumentoOutput());

    // when: enviamos la memoria
    memoriaService.enviarSecretaria(memoria.getId(), "user-001");

    // then: La memoria se envía correctamente
    Assertions.assertThat(memoriaActualizada.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(memoriaActualizada.getTitulo(), Language.ES))
        .isEqualTo("Memoria1");
    Assertions.assertThat(memoriaActualizada.getVersion()).isEqualTo(2);
    Assertions.assertThat(memoriaActualizada.getNumReferencia()).isEqualTo("numRef-111");
    Assertions.assertThat(memoriaActualizada.getEstadoActual().getId()).isEqualTo(12L);
    Assertions.assertThat(memoriaActualizada.getPeticionEvaluacion().getPersonaRef()).isEqualTo("user-001");

  }

  @Test
  public void enviarSecretaria_WithId_EstadoCompletadaSeguimientoFinal() {

    // given: Una nueva Memoria (16L=Completada Seguimiento Final)
    Memoria memoria = generarMockMemoria(1L, "numRef-111", "Memoria1", 1, 16L);
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));

    Memoria memoriaActualizada = generarMockMemoria(1L, "numRef-111", "Memoria1", 2, 17L);

    BDDMockito
        .given(informeService.create(ArgumentMatchers.any(Informe.class)))
        .willAnswer(AdditionalAnswers.returnsFirstArg());
    BDDMockito
        .given(sgdocService.uploadInforme(ArgumentMatchers.anyString(), ArgumentMatchers.nullable(Resource.class)))
        .willReturn(new DocumentoOutput());

    // when: enviamos la memoria
    memoriaService.enviarSecretaria(memoria.getId(), "user-001");

    // then: La memoria se envía correctamente
    Assertions.assertThat(memoriaActualizada.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(memoriaActualizada.getTitulo(), Language.ES))
        .isEqualTo("Memoria1");
    Assertions.assertThat(memoriaActualizada.getVersion()).isEqualTo(2);
    Assertions.assertThat(memoriaActualizada.getNumReferencia()).isEqualTo("numRef-111");
    Assertions.assertThat(memoriaActualizada.getEstadoActual().getId()).isEqualTo(17L);
    Assertions.assertThat(memoriaActualizada.getPeticionEvaluacion().getPersonaRef()).isEqualTo("user-001");

  }

  @Test
  public void enviarSecretaria_WithId_EstadoNoProcedeEvaluar() {

    // given: Una nueva Memoria (8L=No procede evaluar)
    Memoria memoria = generarMockMemoria(1L, "numRef-111", "Memoria1", 1, 8L);
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));

    Memoria memoriaActualizada = generarMockMemoria(1L, "numRef-111", "Memoria1", 2, 3L);

    BDDMockito
        .given(informeService.create(ArgumentMatchers.any(Informe.class)))
        .willAnswer(AdditionalAnswers.returnsFirstArg());
    BDDMockito
        .given(sgdocService.uploadInforme(ArgumentMatchers.anyString(), ArgumentMatchers.nullable(Resource.class)))
        .willReturn(new DocumentoOutput());

    // when: enviamos la memoria
    memoriaService.enviarSecretaria(memoria.getId(), "user-001");

    // then: La memoria se envía correctamente
    Assertions.assertThat(memoriaActualizada.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(memoriaActualizada.getTitulo(), Language.ES))
        .isEqualTo("Memoria1");
    Assertions.assertThat(memoriaActualizada.getVersion()).isEqualTo(2);
    Assertions.assertThat(memoriaActualizada.getNumReferencia()).isEqualTo("numRef-111");
    Assertions.assertThat(memoriaActualizada.getEstadoActual().getId()).isEqualTo(3L);
    Assertions.assertThat(memoriaActualizada.getPeticionEvaluacion().getPersonaRef()).isEqualTo("user-001");

  }

  @Test
  public void enviarSecretaria_WithId_EstadoPendienteCorrecciones() {

    // given: Una nueva Memoria (7L=Pendiente de correcciones)
    Memoria memoria = generarMockMemoria(1L, "numRef-111", "Memoria1", 1, 7L);
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));

    Memoria memoriaActualizada = generarMockMemoria(1L, "numRef-111", "Memoria1", 2, 3L);

    BDDMockito
        .given(informeService.create(ArgumentMatchers.any(Informe.class)))
        .willAnswer(AdditionalAnswers.returnsFirstArg());
    BDDMockito
        .given(sgdocService.uploadInforme(ArgumentMatchers.anyString(), ArgumentMatchers.nullable(Resource.class)))
        .willReturn(new DocumentoOutput());

    // when: enviamos la memoria
    memoriaService.enviarSecretaria(memoria.getId(), "user-001");

    // then: La memoria se envía correctamente
    Assertions.assertThat(memoriaActualizada.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(memoriaActualizada.getTitulo(), Language.ES))
        .isEqualTo("Memoria1");
    Assertions.assertThat(memoriaActualizada.getVersion()).isEqualTo(2);
    Assertions.assertThat(memoriaActualizada.getNumReferencia()).isEqualTo("numRef-111");
    Assertions.assertThat(memoriaActualizada.getEstadoActual().getId()).isEqualTo(3L);
    Assertions.assertThat(memoriaActualizada.getPeticionEvaluacion().getPersonaRef()).isEqualTo("user-001");

  }

  @Test
  public void enviarSecretaria_WithId_EstadoCompletada() {

    // given: Una nueva Memoria (2L=Completada)
    Memoria memoria = generarMockMemoria(1L, "numRef-111", "Memoria1", 1, 2L);
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));

    Memoria memoriaActualizada = generarMockMemoria(1L, "numRef-111", "Memoria1", 2, 3L);

    BDDMockito
        .given(informeService.create(ArgumentMatchers.any(Informe.class)))
        .willAnswer(AdditionalAnswers.returnsFirstArg());
    BDDMockito
        .given(sgdocService.uploadInforme(ArgumentMatchers.anyString(), ArgumentMatchers.nullable(Resource.class)))
        .willReturn(new DocumentoOutput());

    // when: enviamos la memoria
    memoriaService.enviarSecretaria(memoria.getId(), "user-001");

    // then: La memoria se envía correctamente
    Assertions.assertThat(memoriaActualizada.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(memoriaActualizada.getTitulo(), Language.ES))
        .isEqualTo("Memoria1");
    Assertions.assertThat(memoriaActualizada.getVersion()).isEqualTo(2);
    Assertions.assertThat(memoriaActualizada.getNumReferencia()).isEqualTo("numRef-111");
    Assertions.assertThat(memoriaActualizada.getEstadoActual().getId()).isEqualTo(3L);
    Assertions.assertThat(memoriaActualizada.getPeticionEvaluacion().getPersonaRef()).isEqualTo("user-001");

  }

  @Test
  void update_archivarNoPresentados() {
    // given: Memorias a archivar
    Memoria memoria = generarMockMemoria(1L, "numRef-99", "Memoria", 1, 1L);
    Memoria memoriaServicioActualizado = generarMockMemoria(1L, "numRef-99", "MemoriaAct", 1, 1L);
    BDDMockito.given(configuracionService.findConfiguracion()).willReturn(generarMockConfiguracion());

    List<Memoria> memorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      memorias.add(generarMockMemoria(Long.valueOf(i), "numRef-5" + String.format("%03d", i),
          "Memoria" + String.format("%03d", i), 1, 1L));
    }

    BDDMockito.given(memoriaRepository.findAll(ArgumentMatchers.<Specification<Memoria>>any())).willReturn(memorias);
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(TipoEstadoMemoria.Tipo.ARCHIVADA.getId());
    EstadoMemoria estadoMemoria = new EstadoMemoria(null, memoria, tipoEstadoMemoria, Instant.now(), null);

    memoriaServicioActualizado.setEstadoActual(tipoEstadoMemoria);
    BDDMockito.given(this.estadoMemoriaRepository.findTopByMemoriaIdOrderByFechaEstadoDesc(anyLong()))
        .willReturn(estadoMemoria);

    // when: Actualizamos la Memoria con el estado archivado
    memoriaService.archivarNoPresentados();

    Assertions.assertThat(memoriaServicioActualizado.getEstadoActual().getId())
        .isEqualTo(TipoEstadoMemoria.Tipo.ARCHIVADA.getId());

  }

  private List<EstadoMemoria> generarEstadosMemoria(Long id) {
    List<EstadoMemoria> estadosMemoria = new ArrayList<EstadoMemoria>();
    EstadoMemoria estadoMemoria = new EstadoMemoria();
    estadoMemoria.setFechaEstado(Instant.now());
    estadoMemoria.setId(id);
    estadoMemoria.setMemoria(generarMockMemoria(1L, "ref-001", "TituloMemoria", 1, id));
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setActivo(true);
    tipoEstadoMemoria.setId(id);
    estadoMemoria.setTipoEstadoMemoria(tipoEstadoMemoria);
    estadosMemoria.add(estadoMemoria);
    return estadosMemoria;
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

    Formulario formulario = new Formulario();
    formulario.setId(1L);
    formulario.setTipo(Formulario.Tipo.MEMORIA);
    formulario.setCodigo("M10/2020/002");

    Set<MemoriaTitulo> mTitulo = new HashSet<>();
    mTitulo.add(new MemoriaTitulo(Language.ES, titulo));
    Memoria memoria = new Memoria();
    memoria.setId(id);
    memoria.setNumReferencia(numReferencia);
    memoria.setPeticionEvaluacion(generarMockPeticionEvaluacion(id, titulo + " PeticionEvaluacion" + id));
    memoria.setComite(generarMockComite(id, "comite" + id, true));
    memoria.setFormulario(formulario);
    memoria.setFormularioSeguimientoAnual(formulario);
    memoria.setFormularioSeguimientoFinal(formulario);
    memoria.setFormularioSeguimientoAnual(formulario);
    memoria.setFormularioRetrospectiva(formulario);
    memoria.setTitulo(mTitulo);
    memoria.setPersonaRef("user-00" + id);
    memoria.setTipo(Memoria.Tipo.NUEVA);
    memoria.setEstadoActual(generarMockTipoEstadoMemoria(idTipoEstadoMemoria, "Estado", Boolean.TRUE));
    memoria.setFechaEnvioSecretaria(Instant.now());
    memoria.setRequiereRetrospectiva(Boolean.FALSE);
    memoria.setRetrospectiva(generarMockRetrospectiva(1L));
    memoria.setVersion(version);
    memoria.setActivo(Boolean.TRUE);

    return memoria;
  }

  private MemoriaInput generarMockMemoriaInput(Long peticionEvaluacionId, Long comiteId, String titulo,
      Memoria.Tipo tipo) {
    List<I18nFieldValueDto> mTitulo = new ArrayList<>();
    mTitulo.add(new I18nFieldValueDto(Language.ES, titulo));
    MemoriaInput memoria = new MemoriaInput();
    memoria.setPeticionEvaluacionId(peticionEvaluacionId);
    memoria.setComiteId(comiteId);
    memoria.setTitulo(mTitulo);
    memoria.setTipo(tipo);

    return memoria;
  }

  /**
   * Crea una memoria de petición evaluación.
   * 
   * @param id identificador.
   */
  private MemoriaPeticionEvaluacion generarMockMemoriaPeticionEvaluacion(Long id) {

    MemoriaPeticionEvaluacion memoria = new MemoriaPeticionEvaluacion();
    memoria.setId(id);

    memoria.setNumReferencia("NumRef-" + String.format("%03d", id));

    Comite comite = new Comite();
    comite.setId(id);
    memoria.setComite(comite);

    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(id);

    Set<MemoriaTitulo> mTitulo = new HashSet<>();
    mTitulo.add(new MemoriaTitulo(Language.ES, "Memoria" + String.format("%03d", id)));

    memoria.setEstadoActual(tipoEstadoMemoria);
    memoria.setTitulo(mTitulo);
    memoria.setNumReferencia("numRef-" + String.format("%03d", id));
    memoria.setFechaEvaluacion(Instant.parse("2020-05-15T00:00:00Z"));
    memoria.setFechaLimite(Instant.parse("2020-08-18T23:59:59Z"));
    return memoria;
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
    comite.setPrefijoReferencia("M10");

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

  /**
   * Función que devuelve un objeto Evaluacion
   * 
   * @param id                    id del Evaluacion
   * @param sufijo                el sufijo para título y nombre
   * @param idTipoEstadoMemoria   id del tipo de estado de la memoria
   * @param idEstadoRetrospectiva id del estado de la retrospectiva
   * @return el objeto Evaluacion
   */

  private Evaluacion generarMockEvaluacion(Long id, String sufijo, Long idTipoEstadoMemoria, Long idEstadoRetrospectiva,
      Integer version) {

    String sufijoStr = (sufijo == null ? id.toString() : sufijo);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre("Dictamen" + sufijoStr);
    dictamen.setActivo(Boolean.TRUE);

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    Set<PeticionEvaluacionTitulo> titulo = new HashSet<>();
    titulo.add(new PeticionEvaluacionTitulo(Language.ES, "PeticionEvaluacion1"));
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
    peticionEvaluacion.setTitulo(titulo);
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

    Memoria memoria = generarMockMemoria(1L, "numRef-001", "Memoria" + sufijoStr, version, idTipoEstadoMemoria);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    Set<ConvocatoriaReunionLugar> lugar = new HashSet<>();
    lugar.add(new ConvocatoriaReunionLugar(Language.ES, "Lugar"));
    Set<ConvocatoriaReunionOrdenDia> ordenDia = new HashSet<>();
    ordenDia.add(new ConvocatoriaReunionOrdenDia(Language.ES,
        "Orden del día convocatoria reunión"));
    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(1L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.now().plus(5, ChronoUnit.DAYS));
    convocatoriaReunion.setFechaLimite(Instant.now().plus(4, ChronoUnit.DAYS));
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
    evaluacion.setVersion(version);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setEvaluador1(evaluador1);
    evaluacion.setEvaluador2(evaluador2);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }

  /**
   * Función que devuelve un objeto Configuracion
   * 
   * @return el objeto Configuracion
   */

  private Configuracion generarMockConfiguracion() {

    Configuracion configuracion = new Configuracion();

    configuracion.setId(1L);
    configuracion.setMesesArchivadaPendienteCorrecciones(20);
    configuracion.setDiasLimiteEvaluador(3);
    configuracion.setDiasArchivadaInactivo(2);

    return configuracion;
  }
}