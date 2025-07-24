package org.crue.hercules.sgi.eti.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ComentarioNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Comentario.TipoEstadoComentario;
import org.crue.hercules.sgi.eti.model.ComentarioTexto;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.repository.ActaRepository;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.EvaluadorRepository;
import org.crue.hercules.sgi.eti.service.impl.ComentarioServiceImpl;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * ComentarioServiceTest
 */
public class ComentarioServiceTest extends BaseServiceTest {

  @Mock
  private ComentarioRepository comentarioRepository;

  @Mock
  private EvaluacionRepository evaluacionRepository;

  @Mock
  private EvaluadorRepository evaluadorRepository;

  @Mock
  private ActaRepository actaRepository;

  private ComentarioService comentarioService;

  @BeforeEach
  public void setUp() throws Exception {
    comentarioService = new ComentarioServiceImpl(comentarioRepository, evaluacionRepository, evaluadorRepository,
        actaRepository);
  }

  @Test
  public void find_WithId_ReturnsComentario() {
    BDDMockito.given(comentarioRepository.findById(1L))
        .willReturn(Optional.of(generarMockComentario(1L, "Comentario1", 1L)));

    final Comentario comentario = comentarioService.findById(1L);

    Assertions.assertThat(comentario.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(comentario.getApartado()).as("apartado").isNotNull();
    Assertions.assertThat(comentario.getApartado().getId()).as("apartado.id").isEqualTo(100L);
    Assertions.assertThat(comentario.getEvaluacion()).as("evaluacion").isNotNull();
    Assertions.assertThat(comentario.getEvaluacion().getId()).as("evaluacion.id").isEqualTo(200L);
    Assertions.assertThat(comentario.getTipoComentario()).as("tipoComentario").isNotNull();
    Assertions.assertThat(comentario.getTipoComentario().getId()).as("tipoComentario.id").isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(comentario.getTexto(), Language.ES)).as("texto")
        .isEqualTo("Comentario1");
  }

  @Test
  public void find_NotFound_ThrowsComentarioNotFoundException() throws Exception {
    BDDMockito.given(comentarioRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> comentarioService.findById(1L)).isInstanceOf(ComentarioNotFoundException.class);
  }

  @Test
  public void findByEvaluacionGestorWithoutPagingValidId() {
    // given: EL id de la evaluación sea valido
    final Long evaluacionId = 1L;
    final Long tipoComentarioId = 1L;
    final int numeroComentario = 6;
    final List<Comentario> comentarios = new ArrayList<>();

    BDDMockito.given(evaluacionRepository.findById(1L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    for (int i = 0; i < numeroComentario; i++) {
      comentarios.add(generarMockComentario(Long.valueOf(i), "Comentario" + String.format("%03d", i), 1L));
    }
    BDDMockito.given(
        comentarioRepository.findByEvaluacionIdAndTipoComentarioId(evaluacionId, tipoComentarioId))
        .willReturn(comentarios);
    // when: se listen sus comentarios
    final List<Comentario> resultList = comentarioService.findByEvaluacionIdGestor(evaluacionId);

    // then: se debe devolver una lista de comentarios
    Assertions.assertThat(resultList.size()).isEqualTo(numeroComentario);

    for (int i = 0; i < numeroComentario; i++) {
      final Comentario comentario = resultList.get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(comentario.getTexto(), Language.ES))
          .isEqualTo("Comentario" + String.format("%03d", i));
    }
  }

  @Test
  public void findByEvaluacionGestorWithPagingValidId() {
    // given: EL id de la evaluación sea valido
    final Long evaluacionId = 1L;
    final int numeroComentario = 100;

    BDDMockito.given(evaluacionRepository.findById(1L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    final List<Comentario> comentarios = new ArrayList<>();
    for (int i = 1; i <= numeroComentario; i++) {
      comentarios.add(generarMockComentario(Long.valueOf(i), "Comentario" + String.format("%03d", i), 1L));
    }

    BDDMockito.given(comentarioRepository.findByEvaluacionIdAndTipoComentarioId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(comentarios);
    // when: se listen sus comentarios

    final List<Comentario> listResult = comentarioService.findByEvaluacionIdGestor(evaluacionId);

    // then: se debe devolver una lista de comentarios
    Assertions.assertThat(listResult.size()).isEqualTo(numeroComentario);
    for (int i = 0; i < 9; i++) {
      final Comentario comentario = listResult.get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(comentario.getTexto(), Language.ES))
          .isEqualTo("Comentario00" + (i + 1));
    }
  }

  @Test
  public void findByEvaluacionGestorNullId() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    try {
      // when: se quiera listar sus comentarios
      comentarioService.findByEvaluacionIdGestor(evaluacionId);
      Assertions.fail("Identificador de Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Evaluación no puede ser nulo");
    }
  }

  @Test
  public void findByEvaluacionEvaluadorWithoutPagingValidId() {
    // given: EL id de la evaluación sea valido
    final Long evaluacionId = 1L;
    final Long tipoComentarioId = 2L;
    final int numeroComentario = 6;
    final String personaRef = "user-002";
    final List<Comentario> comentarios = new ArrayList<>();
    BDDMockito.given(evaluacionRepository.findById(1L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    for (int i = 0; i < numeroComentario; i++) {
      comentarios.add(generarMockComentario(Long.valueOf(i), "Comentario" + String.format("%03d", i), 2L));
    }
    BDDMockito.given(
        comentarioRepository.findByEvaluacionIdAndTipoComentarioIdAndCreatedBy(evaluacionId, tipoComentarioId,
            personaRef))
        .willReturn(comentarios);

    BDDMockito.given(
        comentarioRepository.findByEvaluacionIdAndTipoComentarioIdAndCreatedByNotAndEstado(evaluacionId,
            tipoComentarioId, personaRef, TipoEstadoComentario.CERRADO))
        .willReturn(comentarios);
    // when: se listen sus comentarios
    final List<Comentario> listResult = comentarioService.findByEvaluacionIdEvaluador(evaluacionId,
        personaRef);

    // then: se debe devolver una lista de comentarios
    Assertions.assertThat(listResult.size()).isEqualTo(numeroComentario * 2);
    for (int i = 0; i < numeroComentario; i++) {
      final Comentario comentario = listResult.get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(comentario.getTexto(), Language.ES))
          .isEqualTo("Comentario" + String.format("%03d", i));
    }
  }

  @Test
  public void findByEvaluacionEvaluadorNullId() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    final String personaRef = "user-002";
    try {
      // when: se quiera listar sus comentario
      comentarioService.findByEvaluacionIdEvaluador(evaluacionId, personaRef);
      Assertions.fail("El id no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final EvaluacionNotFoundException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluación " + evaluacionId + " no existe");
    }
  }

  @Test
  public void findByEvaluacionActaWithoutPagingValidId() {
    // given: EL id de la evaluación sea valido
    final Long evaluacionId = 1L;
    final Long tipoComentarioId = 3L;
    final int numeroComentario = 6;
    final List<Comentario> comentarios = new ArrayList<>();

    BDDMockito.given(evaluacionRepository.findById(1L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    for (int i = 0; i < numeroComentario; i++) {
      comentarios.add(generarMockComentario(Long.valueOf(i), "Comentario" + String.format("%03d", i), 1L));
    }
    BDDMockito.given(
        comentarioRepository.findByEvaluacionIdAndTipoComentarioId(evaluacionId, tipoComentarioId))
        .willReturn(comentarios);
    // when: se listen sus comentarios
    final List<Comentario> resultList = comentarioService.findByEvaluacionIdActaGestor(evaluacionId);

    // then: se debe devolver una lista de comentarios
    Assertions.assertThat(resultList.size()).isEqualTo(numeroComentario);
    for (int i = 0; i < numeroComentario; i++) {
      final Comentario comentario = resultList.get(i);
      Assertions.assertThat(I18nHelper.getValueForLanguage(comentario.getTexto(), Language.ES))
          .isEqualTo("Comentario" + String.format("%03d", i));
    }
  }

  @Test
  public void findByEvaluacionActaWithPagingValidId() {
    // given: EL id de la evaluación sea valido
    final Long evaluacionId = 1L;
    final int numeroComentario = 100;

    BDDMockito.given(evaluacionRepository.findById(1L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    final List<Comentario> comentarios = new ArrayList<>();
    for (int i = 1; i <= numeroComentario; i++) {
      comentarios.add(generarMockComentario(Long.valueOf(i), "Comentario" + String.format("%03d", i), 1L));
    }
    BDDMockito.given(comentarioRepository.findByEvaluacionIdAndTipoComentarioId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(comentarios);
    // when: se listen sus comentarios
    final List<Comentario> resultList = comentarioService.findByEvaluacionIdActaGestor(evaluacionId);

    // then: se debe devolver una lista de comentarios
    Assertions.assertThat(resultList.size()).isEqualTo(numeroComentario);
  }

  @Test
  public void findByEvaluacionActaNullId() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    try {
      // when: se quiera listar sus comentarios
      comentarioService.findByEvaluacionIdActaGestor(evaluacionId);
      Assertions.fail("Identificador de Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Evaluación no puede ser nulo");
    }
  }

  @Test
  public void createComentarioGestorEvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;

    try {
      // when: se quiera añadir comentario
      comentarioService.createComentarioGestor(evaluacionId, new Comentario());
      Assertions.fail("Identificador de Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Evaluación no puede ser nulo");
    }
  }

  @Test
  public void createComentarioGestorEvaluacionNotExists() {
    // given: EL id de la evaluación es válido pero no existe
    final Long evaluacionId = 12L;

    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    Comentario comentario = generarMockComentario(null, "Create", 1L);
    comentario.setEvaluacion(null);

    try {
      // when: se quiera añadir comentarios
      comentarioService.createComentarioGestor(evaluacionId, comentario);
      Assertions.fail("Identificador de Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final EvaluacionNotFoundException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluación " + evaluacionId + " no existe");
    }
  }

  @Test
  public void createComentarioGestorComentariosNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;

    final Comentario comentario = generarMockComentario(null, "texto", 1L);
    // when: se quiere insertar un comentario cuya evaluacion no coincide con el
    // id indicado
    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    try {
      comentarioService.createComentarioGestor(evaluacionId, comentario);
      Assertions.fail("Comentario de Evaluación debe ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Comentario de Evaluación debe ser nulo");
    }
  }

  @Test
  public void createComentarioGestor_MemoriaEstadoEnEvaluacion() {
    final Long evaluacionId = 12L;
    final Long tipoEvaluacionId = 1L; // Retrospectiva
    final Long estadoMemoriaId = 5L; // En evaluación
    final Long comiteId = 1L;
    final Long estadoRetrospectivaId = 2L; // Completada
    final Long formularioId = 6L;

    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacionVariable(evaluacionId, tipoEvaluacionId, estadoMemoriaId,
            comiteId, estadoRetrospectivaId, formularioId)));

    final Comentario comentario = new Comentario();
    Formulario formulario = new Formulario();
    formulario.setId(formularioId);
    formulario.setTipo(Formulario.Tipo.RETROSPECTIVA);
    Bloque bloque = new Bloque(1L, formulario, 1, null);
    Apartado apartado = new Apartado();
    apartado.setId(1L);
    apartado.setBloque(bloque);
    comentario.setEvaluacion(null);
    comentario.setApartado(apartado);

    final Comentario comentarioNew = new Comentario();
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario a la evaluación
    final Comentario comentarioCreado = comentarioService.createComentarioGestor(evaluacionId, comentario);

    // then: Comprobamos que se crea el comentario correctamente.
    Assertions.assertThat(comentarioCreado).isNotNull();
  }

  @Test
  public void createComentarioGestor_MemoriaEstadoEnEvaluacionSegAnual() {
    final Long evaluacionId = 12L;
    final Long tipoEvaluacionId = 2L; // Memoria
    final Long estadoMemoriaId = 13L; // En evaluación seguimiento anual
    final Long comiteId = 2L;
    final Long estadoRetrospectivaId = 2L; // Completada
    final Long formularioId = 2L;

    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacionVariable(evaluacionId, tipoEvaluacionId, estadoMemoriaId,
            comiteId, estadoRetrospectivaId, formularioId)));

    final Comentario comentario = new Comentario();
    Formulario formulario = new Formulario();
    formulario.setId(formularioId);
    formulario.setTipo(Formulario.Tipo.MEMORIA);
    Bloque bloque = new Bloque(1L, formulario, 1, null);
    Apartado apartado = new Apartado();
    apartado.setId(1L);
    apartado.setBloque(bloque);
    comentario.setEvaluacion(null);
    comentario.setApartado(apartado);

    final Comentario comentarioNew = new Comentario();
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario a la evaluación
    final Comentario comentarioCreado = comentarioService.createComentarioGestor(evaluacionId, comentario);

    // then: Comprobamos que se crea el comentario correctamente.
    Assertions.assertThat(comentarioCreado).isNotNull();
  }

  @Test
  public void createComentarioGestor_MemoriaEstadoEnSecretariaSegFinalAclaraciones() {
    final Long evaluacionId = 12L;
    final Long tipoEvaluacionId = 2L; // Memoria
    final Long estadoMemoriaId = 18L; // En Secretaria Seg Final Aclaraciones
    final Long comiteId = 3L;
    final Long estadoRetrospectivaId = 2L; // Completada
    final Long formularioId = 3L;

    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacionVariable(evaluacionId, tipoEvaluacionId, estadoMemoriaId,
            comiteId, estadoRetrospectivaId, formularioId)));

    final Comentario comentario = new Comentario();
    Formulario formulario = new Formulario();
    formulario.setId(formularioId);
    formulario.setTipo(Formulario.Tipo.MEMORIA);
    Bloque bloque = new Bloque(1L, formulario, 1, null);
    Apartado apartado = new Apartado();
    apartado.setId(1L);
    apartado.setBloque(bloque);
    comentario.setEvaluacion(null);
    comentario.setApartado(apartado);

    final Comentario comentarioNew = new Comentario();
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario a la evaluación
    final Comentario comentarioCreado = comentarioService.createComentarioGestor(evaluacionId, comentario);

    // then: Comprobamos que se crea el comentario correctamente.
    Assertions.assertThat(comentarioCreado).isNotNull();
  }

  @Test
  public void createComentarioGestor_MemoriaEstadoEnEvaluacionSegFinal() {
    final Long evaluacionId = 12L;
    final Long tipoEvaluacionId = 3L; // Seguimiento anual
    final Long estadoMemoriaId = 19L; // En Evaluacion Seg Final
    final Long comiteId = 1L;
    final Long estadoRetrospectivaId = 2L; // Completada
    final Long formularioId = 4L;

    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacionVariable(evaluacionId, tipoEvaluacionId, estadoMemoriaId,
            comiteId, estadoRetrospectivaId, formularioId)));

    final Comentario comentario = new Comentario();
    Formulario formulario = new Formulario();
    formulario.setId(formularioId);
    formulario.setTipo(Formulario.Tipo.SEGUIMIENTO_ANUAL);
    Bloque bloque = new Bloque(1L, formulario, 1, null);
    Apartado apartado = new Apartado();
    apartado.setId(1L);
    apartado.setBloque(bloque);
    comentario.setEvaluacion(null);
    comentario.setApartado(apartado);

    final Comentario comentarioNew = new Comentario();
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario a la evaluación
    final Comentario comentarioCreado = comentarioService.createComentarioGestor(evaluacionId, comentario);

    // then: Comprobamos que se crea el comentario correctamente.
    Assertions.assertThat(comentarioCreado).isNotNull();
  }

  @Test
  public void createComentarioGestor_RetrospectivaEstadoEnEvaluacion() {
    final Long evaluacionId = 12L;
    final Long tipoEvaluacionId = 4L; // Seguimiento final
    final Long estadoMemoriaId = 1L; // En Elaboración
    final Long comiteId = 1L;
    final Long estadoRetrospectivaId = 4L; // En evaluacion
    final Long formularioId = 5L;

    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacionVariable(evaluacionId, tipoEvaluacionId, estadoMemoriaId,
            comiteId, estadoRetrospectivaId, formularioId)));

    final Comentario comentario = new Comentario();
    Formulario formulario = new Formulario();
    formulario.setId(formularioId);
    formulario.setTipo(Formulario.Tipo.SEGUIMIENTO_FINAL);
    Bloque bloque = new Bloque(1L, formulario, 1, null);
    Apartado apartado = new Apartado();
    apartado.setId(1L);
    apartado.setBloque(bloque);
    comentario.setEvaluacion(null);
    comentario.setApartado(apartado);

    final Comentario comentarioNew = new Comentario();
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario a la evaluación
    final Comentario comentarioCreado = comentarioService.createComentarioGestor(evaluacionId, comentario);

    // then: Comprobamos que se crea el comentario correctamente.
    Assertions.assertThat(comentarioCreado).isNotNull();
  }

  @Test
  public void createComentarioEvaluadorValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final String personaRef = "user-002";
    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));
    final Comentario comentario = generarMockComentario(null, "texto", 2L);
    comentario.setEvaluacion(null);
    final Comentario comentarioNew = generarMockComentario(12L, "texto", 2L);
    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario
    final Comentario comentarioCreado = comentarioService.createComentarioEvaluador(evaluacionId, comentario,
        personaRef);

    // then: Comprobamos que se ha creado el comentario.
    Assertions.assertThat(comentarioCreado).isNotNull();
    Assertions.assertThat(comentarioCreado.getId()).isNotNull();

  }

  @Test
  public void createComentarioEvaluador_UsuarioIsEvaluador1() {
    // given: Una evaluación con un evaluador1
    final Long evaluacionId = 12L;
    final String personaRef = "persona_Ref";

    Evaluacion evaluacion = generarMockEvaluacion(evaluacionId);
    final Evaluador evaluador1 = new Evaluador();
    evaluador1.setPersonaRef(personaRef);
    evaluacion.setEvaluador1(evaluador1);

    BDDMockito.given(evaluacionRepository.findById(evaluacionId)).willReturn(Optional.of(evaluacion));

    final Comentario comentario = generarMockComentario(null, "texto", 1L);
    comentario.setEvaluacion(null);
    final Comentario comentarioNew = generarMockComentario(12L, "texto", 1L);

    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario a la evaluación con la personaRef "user-002"
    final Comentario comentarioCreado = comentarioService.createComentarioEvaluador(evaluacionId, comentario,
        personaRef);

    // then: Comprobamos que se crea el comentario correctamente.
    Assertions.assertThat(comentarioCreado).isNotNull();
    Assertions.assertThat(comentarioCreado.getId()).isNotNull();
  }

  @Test
  public void createComentarioEvaluador_UsuarioIsEvaluador2() {
    // given: Una evaluación con un evaluador2
    final Long evaluacionId = 12L;
    final String personaRef = "persona_Ref";

    Evaluacion evaluacion = generarMockEvaluacion(evaluacionId);
    final Evaluador evaluador2 = new Evaluador();
    evaluador2.setPersonaRef(personaRef);
    evaluacion.setEvaluador2(evaluador2);

    BDDMockito.given(evaluacionRepository.findById(evaluacionId)).willReturn(Optional.of(evaluacion));

    final Comentario comentario = generarMockComentario(null, "texto", 1L);
    comentario.setEvaluacion(null);
    final Comentario comentarioNew = generarMockComentario(12L, "texto", 1L);

    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario a la evaluación con la personaRef "user-002"
    final Comentario comentarioCreado = comentarioService.createComentarioEvaluador(evaluacionId, comentario,
        personaRef);

    // then: Comprobamos que se crea el comentario correctamente.
    Assertions.assertThat(comentarioCreado).isNotNull();
    Assertions.assertThat(comentarioCreado.getId()).isNotNull();
  }

  @Test
  public void createComentarioEvaluadorEvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    final String personaRef = "user-002";

    try {
      // when: se quiera añadir comentario
      comentarioService.createComentarioEvaluador(evaluacionId, new Comentario(), personaRef);
      Assertions.fail("Identificador de Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Evaluación no puede ser nulo");
    }
  }

  @Test
  public void createComentarioEvaluadorEvaluacionNotExists() {
    // given: EL id de la evaluación es válido pero no existe
    final Long evaluacionId = 12L;
    final String personaRef = "user-002";
    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    Comentario comentario = generarMockComentario(null, "Create", 1L);
    comentario.setEvaluacion(null);
    try {
      // when: se quiere añadir comentario
      comentarioService.createComentarioEvaluador(evaluacionId, comentario, personaRef);
      Assertions.fail("Identificador de Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final EvaluacionNotFoundException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluación " + evaluacionId + " no existe");
    }
  }

  @Test
  public void createComentariosEvaluadorComentariosNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final String personaRef = "user-002";

    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    final Comentario comentario = generarMockComentario(null, "texto", 2L);
    // when: se quiere insertar un comentario cuya evaluacion no coincide con el
    // id indicado
    try {
      comentarioService.createComentarioEvaluador(evaluacionId, comentario, personaRef);
      Assertions.fail("Comentario de Evaluación debe ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Comentario de Evaluación debe ser nulo");
    }
  }

  @Test
  public void createComentarioGestor_Success() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));
    final Comentario comentario = generarMockComentario(null, "texto", 1L);
    comentario.setEvaluacion(null);
    final Comentario comentarioNew = generarMockComentario(12L, "texto", 1L);
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario
    final Comentario comentarioCreado = comentarioService.createComentarioGestor(evaluacionId, comentario);

    // then: Comprobamos que se ha creado el comentario.
    Assertions.assertThat(comentarioCreado).isNotNull();
    Assertions.assertThat(comentarioCreado.getId()).isNotNull();

  }

  @Test
  public void createComentarioActaGestorEvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;

    try {
      // when: se quiera añadir comentario
      comentarioService.createComentarioActaGestor(evaluacionId, new Comentario());
      Assertions.fail("Identificador de Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Evaluación no puede ser nulo");
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void createComentarioActaGestorEvaluacionNotExists() {
    // given: EL id de la evaluación es válido pero no existe
    final Long evaluacionId = 12L;

    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    Comentario comentario = generarMockComentario(null, "Create", 1L);
    comentario.setEvaluacion(null);

    try {
      // when: se quiera añadir comentarios
      comentarioService.createComentarioActaGestor(evaluacionId, comentario);
      Assertions.fail("Identificador de Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final EvaluacionNotFoundException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluación " + evaluacionId + " no existe");
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void createComentarioActaGestorComentariosNotValid() {
    // given: EL id de la evaluación no es válido
    final Long evaluacionId = 12L;

    final Comentario comentario = generarMockComentario(null, "texto", 1L);
    // when: se quiere insertar un comentario cuya evaluacion no coincide con el
    // id indicado
    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    try {
      comentarioService.createComentarioActaGestor(evaluacionId, comentario);
      Assertions.fail("Comentario de Evaluación debe ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Comentario de Evaluación debe ser nulo");
    }
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void createComentarioActaGestor_estadoMemoria26L_Success() {
    // given: Una evaluación con una memoria en estado 26L
    final Long estadoMemoriaId = TipoEstadoMemoria.Tipo.EN_EVALUACION_REVISION_MINIMA.getId();
    final Long evaluacionId = 12L;
    Evaluacion evaluacion = generarMockEvaluacion(evaluacionId);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion(4L, "Seguimiento Final", Boolean.TRUE);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);

    TipoEstadoMemoria estadoMemoria = new TipoEstadoMemoria();
    estadoMemoria.setId(estadoMemoriaId);
    evaluacion.getMemoria().setEstadoActual(estadoMemoria);

    BDDMockito.given(evaluacionRepository.findById(evaluacionId)).willReturn(Optional.of(evaluacion));

    final Comentario comentario = generarMockComentario(null, "texto", 1L);
    comentario.getApartado().getBloque().getFormulario()
        .setId(evaluacion.getMemoria().getFormularioSeguimientoFinal().getId());
    comentario.setEvaluacion(null);
    final Comentario comentarioNew = generarMockComentario(12L, "texto", 1L);
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario
    final Comentario comentarioCreado = comentarioService.createComentarioActaGestor(evaluacionId, comentario);

    // then: Comprobamos que se ha creado el comentario.
    Assertions.assertThat(comentarioCreado).isNotNull();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void createComentarioActaGestor_estadoMemoria5L_Success() {
    // given: Una evaluación con una memoria en estado 5L
    final Long estadoMemoriaId = 5L; // En evaluación
    final Long evaluacionId = 12L;
    Evaluacion evaluacion = generarMockEvaluacion(evaluacionId);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion(4L, "Seguimiento Final", Boolean.TRUE);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);

    TipoEstadoMemoria estadoMemoria = new TipoEstadoMemoria();
    estadoMemoria.setId(estadoMemoriaId);
    evaluacion.getMemoria().setEstadoActual(estadoMemoria);

    BDDMockito.given(evaluacionRepository.findById(evaluacionId)).willReturn(Optional.of(evaluacion));

    final Comentario comentario = generarMockComentario(null, "texto", 1L);
    comentario.getApartado().getBloque().getFormulario().setId(
        evaluacion.getMemoria().getFormularioSeguimientoFinal().getId());
    comentario.setEvaluacion(null);
    final Comentario comentarioNew = generarMockComentario(12L, "texto", 1L);
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario
    final Comentario comentarioCreado = comentarioService.createComentarioActaGestor(evaluacionId, comentario);

    // then: Comprobamos que se ha creado el comentario.
    Assertions.assertThat(comentarioCreado).isNotNull();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void createComentarioActaGestor_estadoMemoria13L_Success() {
    // given: Una evaluación con una memoria en estado 13L
    final Long estadoMemoriaId = 13L; // En evaluación seguimiento anual
    final Long evaluacionId = 12L;
    Evaluacion evaluacion = generarMockEvaluacion(evaluacionId);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion(4L, "Seguimiento Final", Boolean.TRUE);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);

    TipoEstadoMemoria estadoMemoria = new TipoEstadoMemoria();
    estadoMemoria.setId(estadoMemoriaId);
    evaluacion.getMemoria().setEstadoActual(estadoMemoria);

    BDDMockito.given(evaluacionRepository.findById(evaluacionId)).willReturn(Optional.of(evaluacion));

    final Comentario comentario = generarMockComentario(null, "texto", 1L);
    comentario.getApartado().getBloque().getFormulario().setId(
        evaluacion.getMemoria().getFormularioSeguimientoFinal().getId());
    comentario.setEvaluacion(null);
    final Comentario comentarioNew = generarMockComentario(12L, "texto", 1L);
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario
    final Comentario comentarioCreado = comentarioService.createComentarioActaGestor(evaluacionId, comentario);

    // then: Comprobamos que se ha creado el comentario.
    Assertions.assertThat(comentarioCreado).isNotNull();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void createComentarioActaGestor_estadoMemoria18L_Success() {
    // given: Una evaluación con una memoria en estado 18L
    final Long estadoMemoriaId = 18L; // En secretaría seguimiento final aclaraciones
    final Long evaluacionId = 12L;
    Evaluacion evaluacion = generarMockEvaluacion(evaluacionId);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion(4L, "Seguimiento Final", Boolean.TRUE);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);

    TipoEstadoMemoria estadoMemoria = new TipoEstadoMemoria();
    estadoMemoria.setId(estadoMemoriaId);
    evaluacion.getMemoria().setEstadoActual(estadoMemoria);

    BDDMockito.given(evaluacionRepository.findById(evaluacionId)).willReturn(Optional.of(evaluacion));

    final Comentario comentario = generarMockComentario(null, "texto", 1L);
    comentario.getApartado().getBloque().getFormulario().setId(
        evaluacion.getMemoria().getFormularioSeguimientoFinal().getId());
    comentario.setEvaluacion(null);
    final Comentario comentarioNew = generarMockComentario(12L, "texto", 1L);
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario
    final Comentario comentarioCreado = comentarioService.createComentarioActaGestor(evaluacionId, comentario);

    // then: Comprobamos que se ha creado el comentario.
    Assertions.assertThat(comentarioCreado).isNotNull();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void createComentarioActaGestor_estadoMemoria19L_Success() {
    // given: Una evaluación con una memoria en estado 19L
    final Long estadoMemoriaId = 19L; // En evaluación seguimiento final
    final Long evaluacionId = 12L;
    Evaluacion evaluacion = generarMockEvaluacion(evaluacionId);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion(4L, "Seguimiento Final", Boolean.TRUE);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);

    TipoEstadoMemoria estadoMemoria = new TipoEstadoMemoria();
    estadoMemoria.setId(estadoMemoriaId);
    evaluacion.getMemoria().setEstadoActual(estadoMemoria);

    BDDMockito.given(evaluacionRepository.findById(evaluacionId)).willReturn(Optional.of(evaluacion));

    final Comentario comentario = generarMockComentario(null, "texto", 1L);
    comentario.getApartado().getBloque().getFormulario().setId(
        evaluacion.getMemoria().getFormularioSeguimientoFinal().getId());
    comentario.setEvaluacion(null);
    final Comentario comentarioNew = generarMockComentario(12L, "texto", 1L);
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario
    final Comentario comentarioCreado = comentarioService.createComentarioActaGestor(evaluacionId, comentario);

    // then: Comprobamos que se ha creado el comentario.
    Assertions.assertThat(comentarioCreado).isNotNull();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-ACT-V" })
  public void createComentarioActaGestor_estadoRetrospectiva4L_Success() {
    // given: Una evaluación con una memoria con una retrospectiva en estado 4L
    final Long estadoMemoriaId = 1L; // En elaboración
    final Long estadoRetrospectivaId = 4L; // En evaluación
    final Long evaluacionId = 12L;
    Evaluacion evaluacion = generarMockEvaluacion(evaluacionId);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion(1L, "Retrospectiva", Boolean.TRUE);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);

    TipoEstadoMemoria estadoMemoria = new TipoEstadoMemoria();
    estadoMemoria.setId(estadoMemoriaId);
    Retrospectiva retrospectiva = new Retrospectiva();
    EstadoRetrospectiva estadoRetrospectiva = new EstadoRetrospectiva();
    estadoRetrospectiva.setId(estadoRetrospectivaId);
    retrospectiva.setEstadoRetrospectiva(estadoRetrospectiva);

    evaluacion.getMemoria().setRetrospectiva(retrospectiva);
    evaluacion.getMemoria().setEstadoActual(estadoMemoria);

    BDDMockito.given(evaluacionRepository.findById(evaluacionId)).willReturn(Optional.of(evaluacion));

    final Comentario comentario = generarMockComentario(null, "texto", 1L);
    comentario.getApartado().getBloque().getFormulario().setId(
        evaluacion.getMemoria().getFormularioRetrospectiva().getId());
    comentario.setEvaluacion(null);
    final Comentario comentarioNew = generarMockComentario(12L, "texto", 1L);
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentarioNew);

    // when: se crea el comentario
    final Comentario comentarioCreado = comentarioService.createComentarioActaGestor(evaluacionId, comentario);

    // then: Comprobamos que se ha creado el comentario.
    Assertions.assertThat(comentarioCreado).isNotNull();
  }

  @Test
  public void updateComentarioGestor_EvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    try {
      // when: se quiera añadir comentarios
      comentarioService.updateComentarioGestor(evaluacionId, generarMockComentario(1L, "comentario1", 1L));
      Assertions.fail("Identificador de Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Evaluación no puede ser nulo");
    }
  }

  @Test
  public void updateComentarioGestor_ThrowNotFoundException() {
    // given: EL comentario a actualizar no existe
    final Long evaluacionId = 12L;
    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    Assertions
        .assertThatThrownBy(
            () -> comentarioService.updateComentarioGestor(evaluacionId, generarMockComentario(1L, "comentario1", 1L)))
        .isInstanceOf(ComentarioNotFoundException.class);

  }

  @Test
  public void updateComentarioGestor_EvaluacionIdNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;

    try {

      Comentario comentario = generarMockComentario(1L, "comentario1", 1L);
      Comentario comentarioActualizado = generarMockComentario(1L, "comentario1 actualizado", 1L);

      BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));
      BDDMockito.given(comentarioRepository.findById(1L)).willReturn(Optional.of(comentario));
      // when: se quiera actualizar un comentario cuya evaluación no es la misma que
      // la recibida

      comentarioService.updateComentarioGestor(evaluacionId, comentarioActualizado);

      Assertions.fail("Comentario no pertenece a Evaluación");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Comentario no pertenece a Evaluación");
    }
  }

  @Test
  public void updateComentarioGestor_BloqueComentariodNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;

    try {

      Comentario comentarioActualizado = generarMockComentario(1L, "comentario1 actualizado", 1L);

      comentarioActualizado.getApartado().getBloque().getFormulario().setId(4L);

      BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

      // when: se quiera actualizar un comentario cuyo bloque comentario no es apto
      // para el comité y tipo de evaluación

      comentarioService.updateComentarioGestor(evaluacionId, comentarioActualizado);
    } catch (final NoRelatedEntitiesException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("No todos/as los/as Comentario proporcionados/as están relacionados/as con el/la Tipo Evaluación");
    }
  }

  @Test
  public void updateComentarioGestor_TipoComentarioNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;

    final Comentario comentario = generarMockComentario(comentarioId, "texto", 2L);

    // when: se quiera actualizar un comentario cuya evaluacion no
    // coincide con el id indicado
    try {
      comentarioService.updateComentarioGestor(evaluacionId, comentario);
      Assertions.fail("No se puede Modificar Tipo Comentario Tipo Gestor para Tipo Comentario");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("No se puede Modificar Tipo Comentario Tipo Gestor para Tipo Comentario");
    }
  }

  @Test
  public void updateComentarioGestor_MemoriaEstadoEnEvaluacion() {
    final Long evaluacionId = 12L;
    final Long tipoEvaluacionId = 1L; // Retrospectiva
    final Long estadoMemoriaId = 5L; // En evaluación
    final Long comiteId = 1L;
    final Long estadoRetrospectivaId = 2L; // Completada
    final Long formularioId = 6L;

    Evaluacion evaluacion = generarMockEvaluacionVariable(evaluacionId, tipoEvaluacionId, estadoMemoriaId, comiteId,
        estadoRetrospectivaId, formularioId);

    final Comentario comentario = new Comentario();
    Formulario formulario = new Formulario();
    formulario.setId(formularioId);
    formulario.setTipo(Formulario.Tipo.RETROSPECTIVA);
    Bloque bloque = new Bloque(1L, formulario, 1, null);
    Apartado apartado = new Apartado();
    TipoComentario tipoComentario = new TipoComentario(1L, "GESTOR", Boolean.TRUE);
    apartado.setId(1L);
    apartado.setBloque(bloque);
    comentario.setId(12L);
    comentario.setEvaluacion(evaluacion);
    comentario.setApartado(apartado);
    comentario.setTipoComentario(tipoComentario);

    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(evaluacion));
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentario);
    BDDMockito.given(comentarioRepository.findById(evaluacionId)).willReturn(Optional.of(comentario));

    final Comentario resultados = comentarioService.updateComentarioGestor(evaluacionId, comentario);

    Assertions.assertThat(resultados != null);
  }

  @Test
  public void updateComentarioGestor_MemoriaEstadoEnEvaluacionSegAnual() {
    final Long evaluacionId = 12L;
    final Long tipoEvaluacionId = 2L; // Memoria
    final Long estadoMemoriaId = 13L; // En evaluación seguimiento anual
    final Long comiteId = 2L;
    final Long estadoRetrospectivaId = 2L; // Completada
    final Long formularioId = 2L;

    Evaluacion evaluacion = generarMockEvaluacionVariable(evaluacionId, tipoEvaluacionId, estadoMemoriaId, comiteId,
        estadoRetrospectivaId, formularioId);

    final Comentario comentario = new Comentario();
    Formulario formulario = new Formulario();
    formulario.setId(formularioId);
    formulario.setTipo(Formulario.Tipo.MEMORIA);
    Bloque bloque = new Bloque(1L, formulario, 1, null);
    Apartado apartado = new Apartado();
    TipoComentario tipoComentario = new TipoComentario(1L, "GESTOR", Boolean.TRUE);
    apartado.setId(1L);
    apartado.setBloque(bloque);
    comentario.setId(12L);
    comentario.setEvaluacion(evaluacion);
    comentario.setApartado(apartado);
    comentario.setTipoComentario(tipoComentario);

    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(evaluacion));
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentario);
    BDDMockito.given(comentarioRepository.findById(evaluacionId)).willReturn(Optional.of(comentario));

    final Comentario resultados = comentarioService.updateComentarioGestor(evaluacionId, comentario);

    Assertions.assertThat(resultados != null);
  }

  @Test
  public void updateComentarioGestor_MemoriaEstadoEnSecretariaSegFinalAclaraciones() {
    final Long evaluacionId = 12L;
    final Long tipoEvaluacionId = 2L; // Memoria
    final Long estadoMemoriaId = 18L; // En Secretaria Seg Final Aclaraciones
    final Long comiteId = 3L;
    final Long estadoRetrospectivaId = 2L; // Completada
    final Long formularioId = 3L;

    Evaluacion evaluacion = generarMockEvaluacionVariable(evaluacionId, tipoEvaluacionId, estadoMemoriaId, comiteId,
        estadoRetrospectivaId, formularioId);

    final Comentario comentario = new Comentario();
    Formulario formulario = new Formulario();
    formulario.setId(formularioId);
    formulario.setTipo(Formulario.Tipo.MEMORIA);
    Bloque bloque = new Bloque(1L, formulario, 1, null);
    Apartado apartado = new Apartado();
    TipoComentario tipoComentario = new TipoComentario(1L, "GESTOR", Boolean.TRUE);
    apartado.setId(1L);
    apartado.setBloque(bloque);
    comentario.setId(12L);
    comentario.setEvaluacion(evaluacion);
    comentario.setApartado(apartado);
    comentario.setTipoComentario(tipoComentario);

    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(evaluacion));
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentario);
    BDDMockito.given(comentarioRepository.findById(evaluacionId)).willReturn(Optional.of(comentario));

    final Comentario resultados = comentarioService.updateComentarioGestor(evaluacionId, comentario);

    Assertions.assertThat(resultados != null);
  }

  @Test
  public void updateComentarioGestor_MemoriaEstadoEnEvaluacionSegFinal() {
    final Long evaluacionId = 12L;
    final Long tipoEvaluacionId = 3L; // Seguimiento anual
    final Long estadoMemoriaId = 19L; // En Evaluacion Seg Final
    final Long comiteId = 1L;
    final Long estadoRetrospectivaId = 2L; // Completada
    final Long formularioId = 4L;

    Evaluacion evaluacion = generarMockEvaluacionVariable(evaluacionId, tipoEvaluacionId, estadoMemoriaId, comiteId,
        estadoRetrospectivaId, formularioId);

    final Comentario comentario = new Comentario();
    Formulario formulario = new Formulario();
    formulario.setId(formularioId);
    formulario.setTipo(Formulario.Tipo.SEGUIMIENTO_ANUAL);
    Bloque bloque = new Bloque(1L, formulario, 1, null);
    Apartado apartado = new Apartado();
    TipoComentario tipoComentario = new TipoComentario(1L, "GESTOR", Boolean.TRUE);
    apartado.setId(1L);
    apartado.setBloque(bloque);
    comentario.setId(12L);
    comentario.setEvaluacion(evaluacion);
    comentario.setApartado(apartado);
    comentario.setTipoComentario(tipoComentario);

    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(evaluacion));
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentario);
    BDDMockito.given(comentarioRepository.findById(evaluacionId)).willReturn(Optional.of(comentario));

    final Comentario resultados = comentarioService.updateComentarioGestor(evaluacionId, comentario);

    Assertions.assertThat(resultados != null);
  }

  @Test
  public void updateComentarioGestor_RetrospectivaEstadoEnEvaluacion() {
    final Long evaluacionId = 12L;
    final Long tipoEvaluacionId = 4L; // Seguimiento final
    final Long estadoMemoriaId = 1L; // En Elaboración
    final Long comiteId = 1L;
    final Long estadoRetrospectivaId = 4L; // En evaluacion
    final Long formularioId = 5L;

    Evaluacion evaluacion = generarMockEvaluacionVariable(evaluacionId, tipoEvaluacionId, estadoMemoriaId, comiteId,
        estadoRetrospectivaId, formularioId);

    final Comentario comentario = new Comentario();
    Formulario formulario = new Formulario();
    formulario.setId(formularioId);
    formulario.setTipo(Formulario.Tipo.SEGUIMIENTO_FINAL);
    Bloque bloque = new Bloque(1L, formulario, 1, null);
    Apartado apartado = new Apartado();
    TipoComentario tipoComentario = new TipoComentario(1L, "GESTOR", Boolean.TRUE);
    apartado.setId(1L);
    apartado.setBloque(bloque);
    comentario.setId(12L);
    comentario.setEvaluacion(evaluacion);
    comentario.setApartado(apartado);
    comentario.setTipoComentario(tipoComentario);

    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(evaluacion));
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentario);
    BDDMockito.given(comentarioRepository.findById(evaluacionId)).willReturn(Optional.of(comentario));

    final Comentario resultados = comentarioService.updateComentarioGestor(evaluacionId, comentario);

    Assertions.assertThat(resultados != null);
  }

  @Test
  public void updateComentarioGestor_Success() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;

    final Comentario comentario = generarMockComentario(12L, "texto", 1L);
    comentario.setEvaluacion(generarMockEvaluacion(evaluacionId));
    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentario);

    BDDMockito.given(comentarioRepository.findById(evaluacionId)).willReturn(Optional.of(comentario));

    // when: se quiera actualizar una lista de comentarios
    final Comentario resultados = comentarioService.updateComentarioGestor(evaluacionId, comentario);

    // then: obtenemos una lista de comentarios actualizados
    Assertions.assertThat(resultados != null);
  }

  @Test
  public void updateComentarioEvaluador_EvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    final String personaRef = "user-002";
    try {
      // when: se quiera añadir comentarios
      comentarioService.updateComentarioEvaluador(evaluacionId, generarMockComentario(1L, "comentario1", 2L),
          personaRef);
      Assertions.fail("Identificador de Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Evaluación no puede ser nulo");
    }
  }

  @Test
  public void updateComentarioEvaluador_ThrowNotFoundException() {
    // given: EL comentario a actualizar no existe
    final Long evaluacionId = 12L;
    final String personaRef = "user-002";
    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    Assertions.assertThatThrownBy(() -> comentarioService.updateComentarioEvaluador(evaluacionId,
        generarMockComentario(1L, "comentario1", 2L), personaRef)).isInstanceOf(ComentarioNotFoundException.class);

  }

  @Test
  public void updateComentarioEvaluador_EvaluacionIdNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final String personaRef = "user-002";

    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    try {

      Comentario comentario = generarMockComentario(1L, "comentario1", 2L);
      Comentario comentarioActualizado = generarMockComentario(1L, "comentario1 actualizado", 2L);

      BDDMockito.given(comentarioRepository.findById(1L)).willReturn(Optional.of(comentario));
      // when: se quiera actualizar un comentario cuya evaluación no es la misma que
      // la recibida
      comentarioService.updateComentarioEvaluador(evaluacionId, comentarioActualizado, personaRef);

      Assertions.fail("Comentario no pertenece a Evaluación");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Comentario no pertenece a Evaluación");
    }
  }

  @Test
  public void updateComentarioEvaluador_TipoComentarioNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;
    final String personaRef = "user-002";

    final Comentario comentario = generarMockComentario(comentarioId, "texto", 1L);

    // when: se quiera actualizar un comentario cuya evaluacion no
    // coincide con el id indicado
    try {
      comentarioService.updateComentarioEvaluador(evaluacionId, comentario, personaRef);
      Assertions.fail("No se puede Modificar Tipo Comentario Tipo Evaluador para Tipo Comentario");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("No se puede Modificar Tipo Comentario Tipo Evaluador para Tipo Comentario");
    }
  }

  @Test
  public void updateComentarioEvaluador_UsuarioIsEvaluador1() {
    // given: Una evaluación con un evaluador1
    final Long evaluacionId = 12L;
    final String personaRef = "user-002";

    Evaluacion evaluacion = generarMockEvaluacion(evaluacionId);
    final Evaluador evaluador1 = new Evaluador();
    evaluador1.setPersonaRef(personaRef);
    evaluacion.setEvaluador1(evaluador1);

    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    final Comentario comentario = generarMockComentario(12L, "texto", 2L);
    comentario.setEvaluacion(generarMockEvaluacion(evaluacionId));
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentario);

    BDDMockito.given(comentarioRepository.findById(evaluacionId)).willReturn(Optional.of(comentario));

    // when: se modifica el comentario a la evaluación con la personaRef "user-002"
    final Comentario resultados = comentarioService.updateComentarioEvaluador(evaluacionId, comentario, personaRef);

    // then: obtenemos una lista de comentarios actualizados
    Assertions.assertThat(resultados != null);
  }

  @Test
  public void updateComentarioEvaluador_UsuarioIsEvaluador2() {
    // given: Una evaluación con un evaluador2
    final Long evaluacionId = 12L;
    final String personaRef = "user-002";

    Evaluacion evaluacion = generarMockEvaluacion(evaluacionId);
    final Evaluador evaluador2 = new Evaluador();
    evaluador2.setPersonaRef(personaRef);
    evaluacion.setEvaluador1(evaluador2);

    BDDMockito.given(evaluacionRepository.findById(evaluacionId))
        .willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    final Comentario comentario = generarMockComentario(12L, "texto", 2L);
    comentario.setEvaluacion(generarMockEvaluacion(evaluacionId));
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentario);

    BDDMockito.given(comentarioRepository.findById(evaluacionId)).willReturn(Optional.of(comentario));

    // when: se modifica el comentario a la evaluación con la personaRef "user-002"
    final Comentario resultados = comentarioService.updateComentarioEvaluador(evaluacionId, comentario, personaRef);

    // then: obtenemos una lista de comentarios actualizados
    Assertions.assertThat(resultados != null);
  }

  @Test
  public void updateComentarioEvaluador_Success() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final String personaRef = "user-002";

    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    final Comentario comentario = generarMockComentario(12L, "texto", 2L);
    comentario.setEvaluacion(generarMockEvaluacion(evaluacionId));
    BDDMockito.given(comentarioRepository.save(comentario)).willReturn(comentario);

    BDDMockito.given(comentarioRepository.findById(evaluacionId)).willReturn(Optional.of(comentario));

    // when: se quiera actualizar una lista de comentarios
    final Comentario resultados = comentarioService.updateComentarioEvaluador(evaluacionId, comentario, personaRef);

    // then: obtenemos una lista de comentarios actualizados
    Assertions.assertThat(resultados != null);
  }

  @Test
  public void deleteComentarioGestor_EvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    final Long comentarioId = 1L;

    try {
      // when: se quiera eliminar comentario
      comentarioService.deleteComentarioGestor(evaluacionId, comentarioId);
      Assertions.fail("Identificador de Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Identificador de Evaluación no puede ser nulo");
    }
  }

  @Test
  public void deleteComentarioGestor_EvaluacionNotExists() {
    // given: EL id de la evaluación es válido pero no existe
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;

    try {
      // when: se quiere eliminar comentario
      comentarioService.deleteComentarioGestor(evaluacionId, comentarioId);
      Assertions.fail("El id de la Evaluación debe existir para eliminar un comentario.");
      // then: se debe lanzar una excepción
    } catch (final EvaluacionNotFoundException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluación " + evaluacionId + " no existe");
    }
  }

  @Test
  public void deleteComentarioGestorEvaluacion_ComentarioIdNull() {
    // given: EL id de la evaluación es válido pero no existe
    final Long evaluacionId = 12L;
    try {
      // when: se quiera eliminar comentario
      comentarioService.deleteComentarioGestor(evaluacionId, null);
      Assertions.fail("Identificador de Comentario no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Identificador de Comentario no puede ser nulo");
    }
  }

  @Test
  public void deleteComentarioGestorComentarios_EvaluacionIdNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;

    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));
    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 1L)));

    // when: se quiera eliminar un comentario cuya evaluacion no coincide
    // con el id indicado
    try {
      comentarioService.deleteComentarioGestor(evaluacionId, comentarioId);
      Assertions.fail("Comentario no pertenece a Evaluación");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Comentario no pertenece a Evaluación");
    }
  }

  @Test
  public void deleteComentarioGestorComentarios_TipoComentarioNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;

    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 2L)));

    // when: se quiera eliminar un comentario cuya evaluacion no coincide
    // con el id indicado
    try {
      comentarioService.deleteComentarioGestor(evaluacionId, comentarioId);
      Assertions.fail("Comentario no pertenece a Evaluación");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Comentario no pertenece a Evaluación");
    }
  }

  @Test
  public void deleteComentarioGestorComentarios_throwNotFoundException() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;

    // when: se quiera eliminar un comentario que no existe
    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    Assertions.assertThatThrownBy(() -> comentarioService.deleteComentarioGestor(evaluacionId, comentarioId))
        .isInstanceOf(ComentarioNotFoundException.class);

  }

  @Test
  public void deleteComentarioGestor_MemoriaEstadoEnEvaluacion() {
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;
    final Long tipoEvaluacionId = 1L; // Retrospectiva
    final Long estadoMemoriaId = 5L; // En evaluación
    final Long comiteId = 1L;
    final Long estadoRetrospectivaId = 2L; // Completada

    BDDMockito.given(evaluacionRepository.findById(12L))
        .willReturn(Optional.of(generarMockEvaluacionVariable(evaluacionId, tipoEvaluacionId, estadoMemoriaId,
            comiteId, estadoRetrospectivaId, 1L)));

    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 1L)));

    try {
      comentarioService.deleteComentarioGestor(evaluacionId, comentarioId);
      Assertions.fail("Comentario no pertenece a Evaluación");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Comentario no pertenece a Evaluación");
    }
  }

  @Test
  public void deleteComentarioGestor_MemoriaEstadoEnEvaluacionSegAnual() {
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;
    final Long tipoEvaluacionId = 1L; // Retrospectiva
    final Long estadoMemoriaId = 13L; // En Evaluacion Seg Anual
    final Long comiteId = 1L;
    final Long estadoRetrospectivaId = 2L; // Completada

    BDDMockito.given(evaluacionRepository.findById(12L))
        .willReturn(Optional.of(generarMockEvaluacionVariable(evaluacionId, tipoEvaluacionId, estadoMemoriaId,
            comiteId, estadoRetrospectivaId, 1L)));

    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 1L)));

    try {
      comentarioService.deleteComentarioGestor(evaluacionId, comentarioId);
      Assertions.fail("Comentario no pertenece a Evaluación");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Comentario no pertenece a Evaluación");
    }
  }

  @Test
  public void deleteComentarioGestor_MemoriaEstadoEnSecretariaSegFinalAclaraciones() {
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;
    final Long tipoEvaluacionId = 1L; // Retrospectiva
    final Long estadoMemoriaId = 18L; // En Secretaria Seg Final Aclaraciones
    final Long comiteId = 1L;
    final Long estadoRetrospectivaId = 2L; // Completada

    BDDMockito.given(evaluacionRepository.findById(12L))
        .willReturn(Optional.of(generarMockEvaluacionVariable(evaluacionId, tipoEvaluacionId, estadoMemoriaId,
            comiteId, estadoRetrospectivaId, 1L)));

    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 1L)));

    try {
      comentarioService.deleteComentarioGestor(evaluacionId, comentarioId);
      Assertions.fail("Comentario no pertenece a Evaluación");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Comentario no pertenece a Evaluación");
    }
  }

  @Test
  public void deleteComentarioGestor_MemoriaEstadoEnEvaluacionSegFinal() {
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;
    final Long tipoEvaluacionId = 1L; // Retrospectiva
    final Long estadoMemoriaId = 19L; // En Evaluacion Seg Final
    final Long comiteId = 1L;
    final Long estadoRetrospectivaId = 2L; // Completada

    BDDMockito.given(evaluacionRepository.findById(12L))
        .willReturn(Optional.of(generarMockEvaluacionVariable(evaluacionId, tipoEvaluacionId, estadoMemoriaId,
            comiteId, estadoRetrospectivaId, 1L)));

    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 1L)));

    try {
      comentarioService.deleteComentarioGestor(evaluacionId, comentarioId);
      Assertions.fail("Comentario no pertenece a Evaluación");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Comentario no pertenece a Evaluación");
    }
  }

  @Test
  public void deleteComentarioGestor_RetrospectivaEstadoEnEvaluacion() {
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;
    final Long tipoEvaluacionId = 1L; // Retrospectiva
    final Long estadoMemoriaId = 1L; // En Elaboracion
    final Long comiteId = 1L;
    final Long estadoRetrospectivaId = 4L; // En Evaluacion

    BDDMockito.given(evaluacionRepository.findById(12L))
        .willReturn(Optional.of(generarMockEvaluacionVariable(evaluacionId, tipoEvaluacionId, estadoMemoriaId,
            comiteId, estadoRetrospectivaId, 1L)));

    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 1L)));

    try {
      comentarioService.deleteComentarioGestor(evaluacionId, comentarioId);
      Assertions.fail("Comentario no pertenece a Evaluación");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Comentario no pertenece a Evaluación");
    }
  }

  @Test
  public void deleteComentarioEvaluador_EvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    final Long comentarioId = 1L;
    final String personaRef = "user-002";

    try {
      // when: se quiera eliminar comentario
      comentarioService.deleteComentarioEvaluador(evaluacionId, comentarioId, personaRef);
      Assertions.fail("Identificador de Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Identificador de Evaluación no puede ser nulo");
    }
  }

  @Test
  public void deleteComentariEvaluador_EvaluacionNotExists() {
    // given: EL id de la evaluación es válido pero no existe
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;
    final String personaRef = "user-002";

    try {
      // when: se quiere eliminar comentario
      comentarioService.deleteComentarioEvaluador(evaluacionId, comentarioId, personaRef);
      Assertions.fail("El id de la Evaluación debe existir para eliminar un comentario.");
      // then: se debe lanzar una excepción
    } catch (final EvaluacionNotFoundException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluación " + evaluacionId + " no existe");
    }
  }

  @Test
  public void deleteComentarioEvaluadorEvaluacion_ComentarioIdNull() {
    // given: EL id de la evaluación es válido pero no existe
    final Long evaluacionId = 12L;
    final String personaRef = "user-002";

    try {
      // when: se quiera eliminar comentario
      comentarioService.deleteComentarioEvaluador(evaluacionId, null, personaRef);
      Assertions.fail("Identificador de Comentario no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Identificador de Comentario no puede ser nulo");
    }
  }

  @Test
  public void deleteComentarioEvaluadorComentarios_EvaluacionIdNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;
    final String personaRef = "user-002";

    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 2L)));

    // when: se quiera eliminar un comentario cuya evaluacion no coincide
    // con el id indicado
    try {
      comentarioService.deleteComentarioEvaluador(evaluacionId, comentarioId, personaRef);
      Assertions.fail("Comentario no pertenece a Evaluación");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Comentario no pertenece a Evaluación");
    }
  }

  @Test
  public void deleteComentarioEvaluadorComentarios_TipoComentarioNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 200L;
    final Long comentarioId = 1L;
    final String personaRef = "user-002";

    BDDMockito.given(evaluacionRepository.findById(200L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 1L)));

    // when: se quiera eliminar un comentario cuya evaluacion no coincide
    // con el id indicado
    try {
      comentarioService.deleteComentarioEvaluador(evaluacionId, comentarioId, personaRef);
      Assertions.fail("No se puede Eliminar Tipo Comentario para Comentario");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("No se puede Eliminar Tipo Comentario para Comentario");
    }
  }

  @Test
  public void deleteComentarioEvaluadprComentarios_throwNotFoundException() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;
    final String personaRef = "user-002";

    BDDMockito.given(evaluacionRepository.findById(12L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    // when: se quiera eliminar un comentario que no existe

    Assertions
        .assertThatThrownBy(() -> comentarioService.deleteComentarioEvaluador(evaluacionId, comentarioId, personaRef))
        .isInstanceOf(ComentarioNotFoundException.class);

  }

  @Test
  public void deleteComentarioEvaluador_Evaluador1IsPersonaRef() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 200L;
    final Long comentarioId = 1L;
    final String personaRef = "user-002";

    BDDMockito.given(evaluacionRepository.findById(200L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 2L)));

    comentarioService.deleteComentarioEvaluador(evaluacionId, comentarioId, personaRef);

  }

  @Test
  public void deleteComentarioEvaluador_UsuarioIsEvaluador2() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 200L;
    final Long comentarioId = 1L;
    final String personaRef = "user-002";

    BDDMockito.given(evaluacionRepository.findById(200L)).willReturn(Optional.of(generarMockEvaluacion(evaluacionId)));

    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 2L)));

    comentarioService.deleteComentarioEvaluador(evaluacionId, comentarioId, personaRef);
  }

  @Test
  public void deleteComentarioActaGestor_EvaluacionIdNull() {
    // given: EL id de la evaluación sea null
    final Long evaluacionId = null;
    final Long comentarioId = 1L;

    try {
      // when: se quiera eliminar comentario
      comentarioService.deleteComentarioActaGestor(evaluacionId, comentarioId);
      Assertions.fail("Identificador de Evaluación no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Identificador de Evaluación no puede ser nulo");
    }
  }

  @Test
  public void deleteComentarioActaGestor_EvaluacionNotExists() {
    // given: EL id de la evaluación es válido pero no existe
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;

    try {
      // when: se quiere eliminar comentario
      comentarioService.deleteComentarioActaGestor(evaluacionId, comentarioId);
      Assertions.fail("El id de la Evaluación debe existir para eliminar un comentario.");
      // then: se debe lanzar una excepción
    } catch (final EvaluacionNotFoundException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Evaluación " + evaluacionId + " no existe");
    }
  }

  @Test
  public void deleteComentarioActaGestor_ComentarioIdNull() {
    // given: EL id de la evaluación es válido pero el comentario es null
    final Long evaluacionId = 12L;
    final Long comentarioId = null;

    try {
      // when: se quiera eliminar comentario
      comentarioService.deleteComentarioActaGestor(evaluacionId, comentarioId);
      Assertions.fail("Identificador de Comentario no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Identificador de Comentario no puede ser nulo");
    }
  }

  @Test
  public void deleteComentarioActaGestor_EvaluacionIdNotValid() {
    // given: EL id de la evaluación no es válido
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;

    BDDMockito.given(evaluacionRepository.existsById(12L)).willReturn(Boolean.TRUE);

    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 2L)));

    // when: se quiera eliminar un comentario cuya evaluacion no coincide
    // con el id indicado
    try {
      comentarioService.deleteComentarioActaGestor(evaluacionId, comentarioId);
      Assertions.fail("Comentario no pertenece a Evaluación");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("Comentario no pertenece a Evaluación");
    }
  }

  @Test
  void deleteComentarioActaGestor_TipoComentarioNotValid() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 200L;
    final Long comentarioId = 1L;

    BDDMockito.given(evaluacionRepository.existsById(200L)).willReturn(Boolean.TRUE);

    BDDMockito.given(comentarioRepository.findById(comentarioId))
        .willReturn(Optional.of(generarMockComentario(comentarioId, "", 1L)));

    // when: se quiera eliminar un comentario
    try {
      comentarioService.deleteComentarioActaGestor(evaluacionId, comentarioId);
      Assertions.fail("No se puede Eliminar Tipo Comentario para Comentario");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("No se puede Eliminar Tipo Comentario para Comentario");
    }
  }

  @Test
  public void deleteComentarioActaGestor_throwNotFoundException() {
    // given: EL id de la evaluación es válido
    final Long evaluacionId = 12L;
    final Long comentarioId = 1L;

    BDDMockito.given(evaluacionRepository.existsById(12L)).willReturn(Boolean.TRUE);

    // when: se quiera eliminar un comentario que no existe

    Assertions.assertThatThrownBy(() -> comentarioService.deleteComentarioActaGestor(evaluacionId, comentarioId))
        .isInstanceOf(ComentarioNotFoundException.class);

  }

  /**
   * Función que devuelve un objeto Comentario
   * 
   * @param id    id de la comentario
   * @param texto texto del comentario
   * @return el objeto Comentario
   */
  private Comentario generarMockComentario(final Long id, final String texto, Long tipoComentarioId) {
    final Apartado apartado = new Apartado();
    apartado.setId(100L);

    Formulario formulario = new Formulario();
    formulario.setId(1L);
    formulario.setTipo(Formulario.Tipo.MEMORIA);

    Bloque bloque = new Bloque(1L, formulario, 1, null);
    apartado.setBloque(bloque);

    final Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(200L);

    final TipoComentario tipoComentario = new TipoComentario();
    tipoComentario.setId(tipoComentarioId);

    Set<ComentarioTexto> txt = new HashSet<>();
    txt.add(new ComentarioTexto(Language.ES, texto));
    final Comentario comentario = new Comentario();
    comentario.setId(id);
    comentario.setApartado(apartado);
    comentario.setEvaluacion(evaluacion);
    comentario.setTipoComentario(tipoComentario);
    comentario.setTexto(txt);

    return comentario;
  }

  private Evaluacion generarMockEvaluacion(final Long id) {
    final Memoria memoria = new Memoria();
    Formulario formulario = new Formulario();
    formulario.setId(1L);
    formulario.setTipo(Formulario.Tipo.MEMORIA);
    Formulario formularioSeguimientoAnual = new Formulario();
    formularioSeguimientoAnual.setId(4L);
    formularioSeguimientoAnual.setTipo(Formulario.Tipo.MEMORIA);
    Formulario formularioSeguimientoFinal = new Formulario();
    formularioSeguimientoFinal.setId(5L);
    formularioSeguimientoFinal.setTipo(Formulario.Tipo.MEMORIA);
    Formulario formularioRetrospectiva = new Formulario();
    formularioRetrospectiva.setId(6L);
    formularioRetrospectiva.setTipo(Formulario.Tipo.MEMORIA);

    final TipoEstadoMemoria estadoMemoria = new TipoEstadoMemoria();
    final Evaluador evaluador = new Evaluador();
    evaluador.setPersonaRef("user-002");
    estadoMemoria.setId(TipoEstadoMemoria.Tipo.EN_EVALUACION.getId());
    memoria.setEstadoActual(estadoMemoria);
    memoria.setFormulario(formulario);
    memoria.setFormularioSeguimientoAnual(formularioSeguimientoAnual);
    memoria.setFormularioSeguimientoFinal(formularioSeguimientoFinal);
    memoria.setFormularioRetrospectiva(formularioRetrospectiva);

    Comite comite = new Comite();
    comite.setId(1L);
    comite.setCodigo("CEI");
    comite.setActivo(Boolean.TRUE);
    memoria.setComite(comite);

    final Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setMemoria(memoria);
    evaluacion.setEvaluador1(evaluador);
    evaluacion.setEvaluador2(evaluador);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion(2L, "Memoria", Boolean.TRUE);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);

    return evaluacion;
  }

  private Evaluacion generarMockEvaluacionVariable(final Long id, final Long tipoEvaluacionId,
      final Long estadoMemoriaId, final Long comiteId, final Long estadoRetrospectivaId, Long formularioId) {

    final Memoria memoria = new Memoria();
    final TipoEstadoMemoria estadoMemoria = new TipoEstadoMemoria();
    final Evaluador evaluador = new Evaluador();
    evaluador.setPersonaRef("user-002");
    estadoMemoria.setId(estadoMemoriaId);
    memoria.setEstadoActual(estadoMemoria);

    final EstadoRetrospectiva estadoRetrospectiva = new EstadoRetrospectiva(estadoRetrospectivaId, "Nombre Estado",
        Boolean.TRUE);
    final Retrospectiva retrospectiva = new Retrospectiva(1L, estadoRetrospectiva, Instant.now());
    memoria.setRetrospectiva(retrospectiva);

    Formulario formulario = new Formulario();
    formulario.setId(formularioId);
    formulario.setTipo(Formulario.Tipo.MEMORIA);
    Comite comite = new Comite();
    comite.setId(comiteId);
    comite.setCodigo("nombreComite");
    comite.setActivo(Boolean.TRUE);

    memoria.setFormulario(formulario);
    memoria.setFormularioSeguimientoAnual(formulario);
    memoria.setFormularioSeguimientoFinal(formulario);
    memoria.setFormularioRetrospectiva(formulario);
    memoria.setComite(comite);

    final Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setMemoria(memoria);
    evaluacion.setEvaluador1(evaluador);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion(tipoEvaluacionId, "Nombre", Boolean.TRUE);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);

    return evaluacion;
  }

}
