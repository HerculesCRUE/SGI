package org.crue.hercules.sgi.eti.service;

import static org.mockito.ArgumentMatchers.anyLong;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.crue.hercules.sgi.eti.model.Configuracion;
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
import org.crue.hercules.sgi.eti.repository.ApartadoRepository;
import org.crue.hercules.sgi.eti.repository.BloqueRepository;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.crue.hercules.sgi.eti.repository.ComiteRepository;
import org.crue.hercules.sgi.eti.repository.DocumentacionMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.EstadoMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.EstadoRetrospectivaRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.PeticionEvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.RespuestaRepository;
import org.crue.hercules.sgi.eti.repository.TareaRepository;
import org.crue.hercules.sgi.eti.service.impl.MemoriaServiceImpl;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiRepService;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
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
  private EstadoRetrospectivaRepository estadoRetrospectivaRepository;

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

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @BeforeEach
  public void setUp() throws Exception {
    memoriaService = new MemoriaServiceImpl(sgiConfigProperties, memoriaRepository, estadoMemoriaRepository,
        estadoRetrospectivaRepository, evaluacionRepository, comentarioRepository, informeService,
        peticionEvaluacionRepository, comiteRepository, documentacionMemoriaRepository, respuestaRepository,
        tareaRepository, configuracionService, reportService, sgdocService, bloqueRepository, apartadoRepository,
        comunicadosService, retrospectivaService);
  }

  @Test
  void find_WithId_ReturnsMemoria() {

    BDDMockito.given(memoriaRepository.findById(1L))
        .willReturn(Optional.of(generarMockMemoria(1L, "numRef-5598", "Memoria1", 1, 1L)));

    Memoria memoria = memoriaService.findById(1L);

    Assertions.assertThat(memoria.getId()).isEqualTo(1L);
    Assertions.assertThat(memoria.getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(memoria.getVersion()).isEqualTo(1);
    Assertions.assertThat(memoria.getNumReferencia()).isEqualTo("numRef-5598");

  }

  @Test
  void find_NotFound_ThrowsMemoriaNotFoundException() throws Exception {
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> memoriaService.findById(1L)).isInstanceOf(MemoriaNotFoundException.class);
  }

  @Test
  public void findByComite_WithId_ReturnsMemoria() {

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
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
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
      Assertions.fail("El identificador del comité no puede ser null para recuperar sus tipos de memoria asociados.");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El identificador del comité no puede ser null para recuperar sus tipos de memoria asociados.");
    }
  }

  @Test
  void findAllMemoriasPeticionEvaluacionModificables_PeticionEvaluacionIdNull() throws Exception {

    try {
      // when: Creamos la memoria
      memoriaService.findAllMemoriasPeticionEvaluacionModificables(1L, null, null);
      Assertions.fail(
          "El identificador de la petición de evaluación no puede ser null para recuperar sus tipos de memoria asociados.");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "El identificador de la petición de evaluación no puede ser null para recuperar sus tipos de memoria asociados.");
    }
  }

  @Test
  void create_ReturnsMemoriaTipoMemoriaNuevo() {
    // given: Una nueva Memoria
    Memoria memoriaNew = generarMockMemoria(null, "numRef-5598", "MemoriaNew", 1, 1L);
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);
    memoriaNew.setPeticionEvaluacion(peticionEvaluacion);

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "MemoriaNew", 1, 1L);

    BDDMockito.given(peticionEvaluacionRepository.findByIdAndActivoTrue(1L))
        .willReturn(Optional.of(peticionEvaluacion));
    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(memoriaNew.getComite().getId()))
        .willReturn(Optional.of(memoriaNew.getComite()));

    BDDMockito.given(memoriaRepository.save(memoriaNew)).willReturn(memoria);

    // when: Creamos la memoria
    Memoria memoriaCreado = memoriaService.create(memoriaNew);

    // then: La memoria se crea correctamente
    Assertions.assertThat(memoriaCreado).isNotNull();
    Assertions.assertThat(memoriaCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(memoriaCreado.getTitulo()).isEqualTo("MemoriaNew");
    Assertions.assertThat(memoriaCreado.getNumReferencia()).isEqualTo("numRef-5598");
  }

  @Test
  void create_ReturnsMemoriaTipoMemoriaRatificacion() {
    // given: Una nueva Memoria
    Memoria memoriaNew = generarMockMemoria(null, "numRef-5598", "MemoriaNew", 1, 1L);
    TipoMemoria tipoMemoria = generarMockTipoMemoria(3L, "TipoMemoria3", true);
    memoriaNew.setTipoMemoria(tipoMemoria);
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);
    memoriaNew.setPeticionEvaluacion(peticionEvaluacion);

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "MemoriaNew", 1, 1L);

    BDDMockito.given(peticionEvaluacionRepository.findByIdAndActivoTrue(1L))
        .willReturn(Optional.of(peticionEvaluacion));
    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(memoriaNew.getComite().getId()))
        .willReturn(Optional.of(memoriaNew.getComite()));

    BDDMockito.given(memoriaRepository.save(memoriaNew)).willReturn(memoria);

    // when: Creamos la memoria
    Memoria memoriaCreado = memoriaService.create(memoriaNew);

    // then: La memoria se crea correctamente
    Assertions.assertThat(memoriaCreado).isNotNull();
    Assertions.assertThat(memoriaCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(memoriaCreado.getTitulo()).isEqualTo("MemoriaNew");
    Assertions.assertThat(memoriaCreado.getNumReferencia()).isEqualTo("numRef-5598");
  }

  @Test
  public void create_MemoriaWithId_ThrowsIllegalArgumentException() {
    // given: Una nueva Memoria que ya tiene id
    Memoria memoriaNew = generarMockMemoria(1L, "numRef-5598", "MemoriaNew", 1, 1L);
    // when: Creamos la Memoria
    // then: Lanza una excepcion porque la Memoria ya tiene id
    Assertions.assertThatThrownBy(() -> memoriaService.create(memoriaNew)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void create_MemoriaIdNull() {
    // given: Una nueva Memoria
    Memoria memoriaNew = generarMockMemoria(1L, "numRef-5598", "MemoriaNew", 1, 1L);

    try {
      // when: Creamos la memoria
      memoriaService.create(memoriaNew);
      Assertions.fail("Memoria id tiene que ser null para crear una nueva memoria");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Memoria id tiene que ser null para crear una nueva memoria");
    }

  }

  @Test
  void create_PeticionEvaluacionIdNull() {
    // given: Una nueva Memoria
    Memoria memoriaNew = generarMockMemoria(null, "numRef-5598", "MemoriaNew", 1, 1L);

    try {
      // when: Creamos la memoria
      memoriaService.create(memoriaNew);
      Assertions.fail("Petición evaluación id no puede ser null para crear una nueva memoria");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Petición evaluación id no puede ser null para crear una nueva memoria");
    }

  }

  @Test
  void create_ThrowPeticionEvaluacionNotFound() {
    // given: Una nueva Memoria
    Memoria memoriaNew = generarMockMemoria(null, "numRef-5598", "MemoriaNew", 1, 1L);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);
    memoriaNew.setPeticionEvaluacion(peticionEvaluacion);

    BDDMockito.given(peticionEvaluacionRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> memoriaService.create(memoriaNew))
        .isInstanceOf(PeticionEvaluacionNotFoundException.class);

  }

  @Test
  void create_ThrowComiteNotFound() {
    // given: Una nueva Memoria
    Memoria memoriaNew = generarMockMemoria(null, "numRef-5598", "MemoriaNew", 1, 1L);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);
    memoriaNew.setPeticionEvaluacion(peticionEvaluacion);

    BDDMockito.given(peticionEvaluacionRepository.findByIdAndActivoTrue(1L))
        .willReturn(Optional.of(peticionEvaluacion));
    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(memoriaNew.getComite().getId()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> memoriaService.create(memoriaNew)).isInstanceOf(ComiteNotFoundException.class);

  }

  @Test
  void create_FailTipoMemoria() {
    // given: Una nueva Memoria
    Memoria memoriaNew = generarMockMemoria(null, "numRef-5598", "MemoriaNew", 1, 1L);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);
    memoriaNew.setPeticionEvaluacion(peticionEvaluacion);
    memoriaNew.getTipoMemoria().setId(2L);

    BDDMockito.given(peticionEvaluacionRepository.findByIdAndActivoTrue(1L))
        .willReturn(Optional.of(peticionEvaluacion));
    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(memoriaNew.getComite().getId()))
        .willReturn(Optional.of(memoriaNew.getComite()));

    try {
      // when: Creamos la memoria
      memoriaService.create(memoriaNew);
      Assertions.fail("La memoria no es del tipo adecuado para realizar una copia a partir de otra memoria.");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("La memoria no es del tipo adecuado para realizar una copia a partir de otra memoria.");
    }

  }

  @Test
  void createModificada_ReturnsMemoria() {
    // given: Una nueva Memoria
    Memoria memoriaNew = generarMockMemoria(null, "", "MemoriaNew", 1, 1L);
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);
    memoriaNew.setPeticionEvaluacion(peticionEvaluacion);
    memoriaNew.getTipoMemoria().setId(2L);

    Memoria memoriaOld = generarMockMemoria(2L, "M10/2020/001", "MemoriaNew", 1, 1L);

    Memoria memoria = generarMockMemoria(3L, "M10/2020/001MR1", "MemoriaNew", 1, 1L);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(2L)).willReturn(Optional.of(memoriaOld));

    BDDMockito.given(peticionEvaluacionRepository.findByIdAndActivoTrue(1L))
        .willReturn(Optional.of(peticionEvaluacion));
    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(memoriaNew.getComite().getId()))
        .willReturn(Optional.of(memoriaNew.getComite()));

    BDDMockito
        .given(memoriaRepository
            .findFirstByNumReferenciaContainingAndComiteIdOrderByNumReferenciaDesc(memoriaOld.getNumReferencia(), 2L))
        .willReturn((memoriaOld));

    BDDMockito.given(documentacionMemoriaRepository.findByMemoriaIdAndMemoriaActivoTrue(memoriaOld.getId(), null))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    BDDMockito.given(bloqueRepository.findByFormularioId(Constantes.FORMULARIO_RETROSPECTIVA, null))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    BDDMockito.given(respuestaRepository.findByMemoriaIdAndMemoriaActivoTrue(memoriaOld.getId(), null))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    BDDMockito.given(memoriaRepository.save(memoriaNew)).willReturn(memoria);

    // when: Creamos la memoria
    Memoria memoriaCreado = memoriaService.createModificada(memoriaNew, 2L);

    // then: La memoria se crea correctamente
    Assertions.assertThat(memoriaCreado).isNotNull();
    Assertions.assertThat(memoriaCreado.getId()).isEqualTo(3L);
    Assertions.assertThat(memoriaCreado.getTitulo()).isEqualTo("MemoriaNew");
    Assertions.assertThat(memoriaCreado.getNumReferencia()).isEqualTo("M10/2020/001MR1");
  }

  @Test
  void createModificada_MemoriaWithId_ThrowsIllegalArgumentException() {
    // given: Una nueva Memoria que ya tiene id
    Memoria memoriaNew = generarMockMemoria(1L, "numRef-5598", "MemoriaNew", 1, 1L);
    // when: Creamos la Memoria
    // then: Lanza una excepcion porque la Memoria ya tiene id
    Assertions.assertThatThrownBy(() -> memoriaService.createModificada(memoriaNew, 2L))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void createModificada_MemoriaIdNull() {
    // given: Una nueva Memoria
    Memoria memoriaNew = generarMockMemoria(1L, "numRef-5598", "MemoriaNew", 1, 1L);

    try {
      // when: Creamos la memoria
      memoriaService.createModificada(memoriaNew, 2L);
      Assertions.fail("Memoria id tiene que ser null para crear una nueva memoria");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Memoria id tiene que ser null para crear una nueva memoria");
    }

  }

  @Test
  void createModificada_PeticionEvaluacionIdNull() {
    // given: Una nueva Memoria
    Memoria memoriaNew = generarMockMemoria(null, "numRef-5598", "MemoriaNew", 1, 1L);

    try {
      // when: Creamos la memoria
      memoriaService.createModificada(memoriaNew, 2L);
      Assertions.fail("Petición evaluación id no puede ser null para crear una nueva memoria");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Petición evaluación id no puede ser null para crear una nueva memoria");
    }

  }

  @Test
  void createModificada_ThrowPeticionEvaluacionNotFound() {
    // given: Una nueva Memoria
    Memoria memoriaNew = generarMockMemoria(null, "numRef-5598", "MemoriaNew", 1, 1L);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);
    memoriaNew.setPeticionEvaluacion(peticionEvaluacion);

    BDDMockito.given(peticionEvaluacionRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> memoriaService.createModificada(memoriaNew, 2L))
        .isInstanceOf(PeticionEvaluacionNotFoundException.class);

  }

  @Test
  void createModificada_ThrowComiteNotFound() {
    // given: Una nueva Memoria
    Memoria memoriaNew = generarMockMemoria(null, "numRef-5598", "MemoriaNew", 1, 1L);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);
    memoriaNew.setPeticionEvaluacion(peticionEvaluacion);

    BDDMockito.given(peticionEvaluacionRepository.findByIdAndActivoTrue(1L))
        .willReturn(Optional.of(peticionEvaluacion));
    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(memoriaNew.getComite().getId()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> memoriaService.createModificada(memoriaNew, 2L))
        .isInstanceOf(ComiteNotFoundException.class);

  }

  @Test
  void createModificada_FailTipoMemoria() {
    // given: Una nueva Memoria
    Memoria memoriaNew = generarMockMemoria(null, "", "MemoriaNew", 1, 1L);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(1L);
    memoriaNew.setPeticionEvaluacion(peticionEvaluacion);
    memoriaNew.getTipoMemoria().setId(1L);

    BDDMockito.given(peticionEvaluacionRepository.findByIdAndActivoTrue(1L))
        .willReturn(Optional.of(peticionEvaluacion));
    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(memoriaNew.getComite().getId()))
        .willReturn(Optional.of(memoriaNew.getComite()));

    try {
      // when: Creamos la memoria
      memoriaService.createModificada(memoriaNew, 2L);
      Assertions.fail("La memoria no es del tipo adecuado para realizar una copia a partir de otra memoria.");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("La memoria no es del tipo adecuado para realizar una copia a partir de otra memoria.");
    }

  }

  @Test
  void update_EstadoActualEnElaboracion_ReturnsMemoria() {
    // given: Una nueva Memoria con el servicio actualizado
    Memoria memoriaServicioActualizado = generarMockMemoria(1L, "numRef-99", "Memoria 1 actualizada", 1, 1L);

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "Memoria1", 1, 1L);

    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));
    BDDMockito.given(memoriaRepository.save(memoria)).willReturn(memoriaServicioActualizado);

    // when: Actualizamos la Memoria
    Memoria memoriaActualizado = memoriaService.update(memoria);

    // then: La Memoria se actualiza correctamente.
    Assertions.assertThat(memoriaActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(memoriaActualizado.getTitulo()).isEqualTo("Memoria 1 actualizada");
    Assertions.assertThat(memoriaActualizado.getNumReferencia()).isEqualTo("numRef-99");

  }

  @Test
  void update_EstadoActualCompletada_ReturnsMemoria() {
    // given: Una nueva Memoria inactiva
    Memoria memoriaServicioActualizado = generarMockMemoria(1L, "numRef-99", "Memoria 1 actualizada", 1, 2L);
    memoriaServicioActualizado.setActivo(Boolean.FALSE);

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "Memoria1", 1, 3L);

    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));
    BDDMockito.given(memoriaRepository.save(memoria)).willReturn(memoriaServicioActualizado);

    // when: Actualizamos la Memoria
    Memoria memoriaActualizado = memoriaService.update(memoria);

    // then: La Memoria se actualiza correctamente.
    Assertions.assertThat(memoriaActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(memoriaActualizado.getTitulo()).isEqualTo("Memoria 1 actualizada");
    Assertions.assertThat(memoriaActualizado.getNumReferencia()).isEqualTo("numRef-99");

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
  void update_EstadoActualInvalid_ThrowsIllegalArgumentException() {

    // given: Una nueva Memoria con activo a false
    Memoria memoriaInactiva = generarMockMemoria(1L, "numRef-99", "Memoria", 1, 1L);
    memoriaInactiva.setActivo(Boolean.FALSE);

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "Memoria1", 1, 1L);

    memoria.getEstadoActual().setId(3L);

    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));

    Assertions.assertThatThrownBy(
        // when: create Convocatoria
        () -> memoriaService.update(memoriaInactiva))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El estado actual de la memoria no es el correcto para desactivar la memoria");

  }

  @Test
  void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> memoriaService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void delete_NonExistingId_ThrowsMemoriaNotFoundException() {
    // given: Id no existe
    BDDMockito.given(memoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> memoriaService.delete(1L))
        // then: Lanza MemoriaNotFoundException
        .isInstanceOf(MemoriaNotFoundException.class);
  }

  @Test
  void delete_WithExistingId_DeletesMemoria() {
    // given: Id existente
    BDDMockito.given(memoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(memoriaRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> memoriaService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
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
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
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
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
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
    Assertions.assertThat(result.size()).isEqualTo(10);
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
    Assertions.assertThat(result.size()).isEqualTo(10);
    for (int i = 0, j = 1; i < 10; i++, j++) {
      Memoria memoria = result.get(i);
      Assertions.assertThat(memoria.getTitulo()).isEqualTo("Memoria" + String.format("%03d", j));
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
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
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
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
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

    Assertions.assertThat(result.size()).isEqualTo(10);
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
    Assertions.assertThat(result.size()).isEqualTo(10);
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

    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
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
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
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

    BDDMockito.given(estadoRetrospectivaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarEstadoRetrospectiva(1L)));

    // when: find unlimited asignables para tipo convocatoria seguimiento
    Memoria returnMemoria = memoriaService
        .getEstadoAnteriorMemoria(generarMockMemoria(1L, "ref-001", "TituloMemoria", 1, 2L));

    Assertions.assertThat(returnMemoria.getId()).isEqualTo(1L);
    Assertions.assertThat(returnMemoria.getTitulo()).isEqualTo("TituloMemoria");
  }

  @Test
  void updateEstadoAnteriorMemoria_returnsMemoriaNull() {

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "Memoria1", 1, 3L);

    BDDMockito.given(memoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(memoria));

    BDDMockito.given(estadoMemoriaRepository.findAllByMemoriaIdOrderByFechaEstadoDesc(ArgumentMatchers.anyLong()))
        .willReturn(generarEstadosMemoria(2L));
    // BDDMockito.given(memoriaRepository.save(ArgumentMatchers.<Memoria>any())).willReturn(memoriaServicioActualizado);

    // when: find unlimited asignables para tipo convocatoria seguimiento
    Memoria memoriaEstadoActualizado = memoriaService.updateEstadoAnteriorMemoria(1L);

    Assertions.assertThat(memoriaEstadoActualizado).isNull();
  }

  @Test
  void updateEstadoAnteriorMemoriaEnEvaluacion_returnsMemoriaNull() {
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION);

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "Memoria1", 1, 3L);
    memoria.setEstadoActual(tipoEstadoMemoria);

    BDDMockito.given(memoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(memoria));

    Memoria memoriaEstadoActualizado = memoriaService.updateEstadoAnteriorMemoria(1L);

    Assertions.assertThat(memoriaEstadoActualizado).isNull();
  }

  @Test
  void updateEstadoAnteriorMemoriaEnSecretaria_returnsMemoriaNull() {
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA);

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "Memoria1", 1, 3L);
    memoria.setEstadoActual(tipoEstadoMemoria);

    BDDMockito.given(memoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(memoria));

    Memoria memoriaEstadoActualizado = memoriaService.updateEstadoAnteriorMemoria(1L);

    Assertions.assertThat(memoriaEstadoActualizado).isNull();
  }

  @Test
  public void updateEstadoAnteriorMemoriaEnSecretariaRevisionMinima_returnsMemoriaNull() {
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_REVISION_MINIMA);

    Memoria memoria = generarMockMemoria(1L, "numRef-5598", "Memoria1", 1, 3L);
    memoria.setEstadoActual(tipoEstadoMemoria);

    BDDMockito.given(memoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(memoria));

    Memoria memoriaEstadoActualizado = memoriaService.updateEstadoAnteriorMemoria(1L);

    Assertions.assertThat(memoriaEstadoActualizado).isNull();
  }

  @Test
  public void updateEstadoAnterior_MemoriaNotPresent_ThrowsMemoriaNotFoundException() {
    Assertions.assertThatThrownBy(() -> memoriaService.updateEstadoAnteriorMemoria(1L))
        .isInstanceOf(MemoriaNotFoundException.class);
  }

  public List<EstadoMemoria> generarEstadosMemoria(Long id) {
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

  public EstadoRetrospectiva generarEstadoRetrospectiva(Long id) {
    EstadoRetrospectiva estadoRetrospectiva = new EstadoRetrospectiva();
    estadoRetrospectiva.setActivo(true);
    estadoRetrospectiva.setId(id);
    return estadoRetrospectiva;
  }

  public void enviarSecretaria_WithId() {

    Memoria memoria = generarMockMemoria(1L, "numRef-111", "Memoria1", 1, 6L);
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));

    Evaluacion evaluacion = generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 6L, 1L, 1);
    BDDMockito
        .given(evaluacionRepository
            .findFirstByMemoriaIdAndTipoEvaluacionIdAndActivoTrueOrderByVersionDesc(memoria.getId(), 1L))
        .willReturn(Optional.of(evaluacion));

    Memoria memoriaActualizada = generarMockMemoria(1L, "numRef-111", "Memoria1", 2, 4L);
    Evaluacion evaluacionNueva = generarMockEvaluacion(null, String.format("%03d", 1), 4L, 1L, 2);

    memoriaService.enviarSecretaria(memoria.getId(), "user-001");

    Assertions.assertThat(memoriaActualizada.getId()).isEqualTo(1L);
    Assertions.assertThat(memoriaActualizada.getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(memoriaActualizada.getVersion()).isEqualTo(2);
    Assertions.assertThat(memoriaActualizada.getNumReferencia()).isEqualTo("numRef-111");
    Assertions.assertThat(memoriaActualizada.getEstadoActual().getId()).isEqualTo(4L);
    Assertions.assertThat(memoriaActualizada.getPeticionEvaluacion().getPersonaRef()).isEqualTo("user-001");
    Assertions.assertThat(evaluacionNueva.getVersion()).isEqualTo(memoriaActualizada.getVersion());
    Assertions.assertThat(evaluacionNueva.getMemoria().getId()).isEqualTo(memoriaActualizada.getId());

  }

  public void enviarSecretaria_WithId_EstadoEnAclaraciónSeguimientoFinal() {
    // given: Una nueva Memoria (21L=En Aclaración Seguimiento Final)
    Memoria memoria = generarMockMemoria(1L, "numRef-111", "Memoria1", 1, 21L);
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));

    Evaluacion evaluacion = generarMockEvaluacion(Long.valueOf(1), String.format("%03d", 1), 21L, 1L, 1);
    BDDMockito
        .given(evaluacionRepository
            .findFirstByMemoriaIdAndTipoEvaluacionIdAndActivoTrueOrderByVersionDesc(memoria.getId(), 1L))
        .willReturn(Optional.of(evaluacion));

    Memoria memoriaActualizada = generarMockMemoria(1L, "numRef-111", "Memoria1", 2, 18L);
    Evaluacion evaluacionNueva = generarMockEvaluacion(null, String.format("%03d", 1), 18L, 1L, 2);

    // when: enviamos la memoria
    memoriaService.enviarSecretaria(memoria.getId(), "user-001");

    // then: La memoria se envía correctamente
    Assertions.assertThat(memoriaActualizada.getId()).isEqualTo(1L);
    Assertions.assertThat(memoriaActualizada.getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(memoriaActualizada.getVersion()).isEqualTo(2);
    Assertions.assertThat(memoriaActualizada.getNumReferencia()).isEqualTo("numRef-111");
    Assertions.assertThat(memoriaActualizada.getEstadoActual().getId()).isEqualTo(18L);
    Assertions.assertThat(memoriaActualizada.getPeticionEvaluacion().getPersonaRef()).isEqualTo("user-001");
    Assertions.assertThat(evaluacionNueva.getVersion()).isEqualTo(memoriaActualizada.getVersion());
    Assertions.assertThat(evaluacionNueva.getMemoria().getId()).isEqualTo(memoriaActualizada.getId());

  }

  public void enviarSecretaria_WithId_EstadoCompletadaSeguimientoAnual() {

    // given: Una nueva Memoria (11L=Completada Seguimiento Anual)
    Memoria memoria = generarMockMemoria(1L, "numRef-111", "Memoria1", 1, 11L);
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));

    Memoria memoriaActualizada = generarMockMemoria(1L, "numRef-111", "Memoria1", 2, 12L);

    // when: enviamos la memoria
    memoriaService.enviarSecretaria(memoria.getId(), "user-001");

    // then: La memoria se envía correctamente
    Assertions.assertThat(memoriaActualizada.getId()).isEqualTo(1L);
    Assertions.assertThat(memoriaActualizada.getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(memoriaActualizada.getVersion()).isEqualTo(2);
    Assertions.assertThat(memoriaActualizada.getNumReferencia()).isEqualTo("numRef-111");
    Assertions.assertThat(memoriaActualizada.getEstadoActual().getId()).isEqualTo(12L);
    Assertions.assertThat(memoriaActualizada.getPeticionEvaluacion().getPersonaRef()).isEqualTo("user-001");

  }

  public void enviarSecretaria_WithId_EstadoCompletadaSeguimientoFinal() {

    // given: Una nueva Memoria (16L=Completada Seguimiento Final)
    Memoria memoria = generarMockMemoria(1L, "numRef-111", "Memoria1", 1, 16L);
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));

    Memoria memoriaActualizada = generarMockMemoria(1L, "numRef-111", "Memoria1", 2, 17L);

    // when: enviamos la memoria
    memoriaService.enviarSecretaria(memoria.getId(), "user-001");

    // then: La memoria se envía correctamente
    Assertions.assertThat(memoriaActualizada.getId()).isEqualTo(1L);
    Assertions.assertThat(memoriaActualizada.getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(memoriaActualizada.getVersion()).isEqualTo(2);
    Assertions.assertThat(memoriaActualizada.getNumReferencia()).isEqualTo("numRef-111");
    Assertions.assertThat(memoriaActualizada.getEstadoActual().getId()).isEqualTo(17L);
    Assertions.assertThat(memoriaActualizada.getPeticionEvaluacion().getPersonaRef()).isEqualTo("user-001");

  }

  public void enviarSecretaria_WithId_EstadoNoProcedeEvaluar() {

    // given: Una nueva Memoria (8L=No procede evaluar)
    Memoria memoria = generarMockMemoria(1L, "numRef-111", "Memoria1", 1, 8L);
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));

    Memoria memoriaActualizada = generarMockMemoria(1L, "numRef-111", "Memoria1", 2, 3L);

    // when: enviamos la memoria
    memoriaService.enviarSecretaria(memoria.getId(), "user-001");

    // then: La memoria se envía correctamente
    Assertions.assertThat(memoriaActualizada.getId()).isEqualTo(1L);
    Assertions.assertThat(memoriaActualizada.getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(memoriaActualizada.getVersion()).isEqualTo(2);
    Assertions.assertThat(memoriaActualizada.getNumReferencia()).isEqualTo("numRef-111");
    Assertions.assertThat(memoriaActualizada.getEstadoActual().getId()).isEqualTo(3L);
    Assertions.assertThat(memoriaActualizada.getPeticionEvaluacion().getPersonaRef()).isEqualTo("user-001");

  }

  public void enviarSecretaria_WithId_EstadoPendienteCorrecciones() {

    // given: Una nueva Memoria (7L=Pendiente de correcciones)
    Memoria memoria = generarMockMemoria(1L, "numRef-111", "Memoria1", 1, 7L);
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));

    Memoria memoriaActualizada = generarMockMemoria(1L, "numRef-111", "Memoria1", 2, 3L);

    // when: enviamos la memoria
    memoriaService.enviarSecretaria(memoria.getId(), "user-001");

    // then: La memoria se envía correctamente
    Assertions.assertThat(memoriaActualizada.getId()).isEqualTo(1L);
    Assertions.assertThat(memoriaActualizada.getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(memoriaActualizada.getVersion()).isEqualTo(2);
    Assertions.assertThat(memoriaActualizada.getNumReferencia()).isEqualTo("numRef-111");
    Assertions.assertThat(memoriaActualizada.getEstadoActual().getId()).isEqualTo(3L);
    Assertions.assertThat(memoriaActualizada.getPeticionEvaluacion().getPersonaRef()).isEqualTo("user-001");

  }

  public void enviarSecretaria_WithId_EstadoCompletada() {

    // given: Una nueva Memoria (2L=Completada)
    Memoria memoria = generarMockMemoria(1L, "numRef-111", "Memoria1", 1, 2L);
    BDDMockito.given(memoriaRepository.findById(1L)).willReturn(Optional.of(memoria));

    Memoria memoriaActualizada = generarMockMemoria(1L, "numRef-111", "Memoria1", 2, 3L);

    // when: enviamos la memoria
    memoriaService.enviarSecretaria(memoria.getId(), "user-001");

    // then: La memoria se envía correctamente
    Assertions.assertThat(memoriaActualizada.getId()).isEqualTo(1L);
    Assertions.assertThat(memoriaActualizada.getTitulo()).isEqualTo("Memoria1");
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
    tipoEstadoMemoria.setId(Constantes.TIPO_ESTADO_MEMORIA_ARCHIVADO);
    EstadoMemoria estadoMemoria = new EstadoMemoria(null, memoria, tipoEstadoMemoria, Instant.now());

    memoriaServicioActualizado.setEstadoActual(tipoEstadoMemoria);
    BDDMockito.given(this.estadoMemoriaRepository.findTopByMemoriaIdOrderByFechaEstadoDesc(anyLong()))
        .willReturn(estadoMemoria);

    // when: Actualizamos la Memoria con el estado archivado
    memoriaService.archivarNoPresentados();

    Assertions.assertThat(memoriaServicioActualizado.getEstadoActual().getId())
        .isEqualTo(Constantes.TIPO_ESTADO_MEMORIA_ARCHIVADO);

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

    return new Memoria(id, numReferencia, generarMockPeticionEvaluacion(id, titulo + " PeticionEvaluacion" + id),
        generarMockComite(id, "comite" + id, true), titulo, "user-00" + id,
        generarMockTipoMemoria(1L, "TipoMemoria1", true),
        generarMockTipoEstadoMemoria(idTipoEstadoMemoria, "Estado", Boolean.TRUE), Instant.now(), Boolean.TRUE,
        generarMockRetrospectiva(1L), version, "CodOrganoCompetente", Boolean.TRUE, null);
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
    memoria.setEstadoActual(tipoEstadoMemoria);
    memoria.setTitulo("Memoria" + String.format("%03d", id));
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

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo" + id);
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico" + id);
    peticionEvaluacion.setExterno(Boolean.FALSE);
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
   */
  private Comite generarMockComite(Long id, String comite, Boolean activo) {
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    return new Comite(id, comite, "nombreSecretario", "nombreInvestigacion", Genero.M, "nombreDecreto", "articulo",
        formulario, activo);

  }

  /**
   * Función que devuelve un objeto tipo memoria.
   * 
   * @param id     identificador del tipo memoria.
   * @param nombre nobmre.
   * @param activo indicador de activo.
   */
  private TipoMemoria generarMockTipoMemoria(Long id, String nombre, Boolean activo) {
    return new TipoMemoria(id, nombre, activo);

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

  public Evaluacion generarMockEvaluacion(Long id, String sufijo, Long idTipoEstadoMemoria, Long idEstadoRetrospectiva,
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

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico1");
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

    Memoria memoria = generarMockMemoria(1L, "numRef-001", "Memoria" + sufijoStr, version, idTipoEstadoMemoria);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(1L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.now());
    convocatoriaReunion.setFechaLimite(Instant.now());
    convocatoriaReunion.setLugar("Lugar");
    convocatoriaReunion.setOrdenDia("Orden del día convocatoria reunión");
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

  public Configuracion generarMockConfiguracion() {

    Configuracion configuracion = new Configuracion();

    configuracion.setId(1L);
    configuracion.setMesesArchivadaPendienteCorrecciones(20);
    configuracion.setDiasLimiteEvaluador(3);
    configuracion.setDiasArchivadaInactivo(2);

    return configuracion;
  }
}