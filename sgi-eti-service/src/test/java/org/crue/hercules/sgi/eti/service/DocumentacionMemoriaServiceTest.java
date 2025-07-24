package org.crue.hercules.sgi.eti.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.DocumentacionMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoriaNombre;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.MemoriaTitulo;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.model.TipoDocumentoNombre;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.repository.DocumentacionMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.FormularioRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.eti.service.impl.DocumentacionMemoriaServiceImpl;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * DocumentacionMemoriaServiceTest
 */
public class DocumentacionMemoriaServiceTest extends BaseServiceTest {

  @Mock
  private DocumentacionMemoriaRepository documentacionMemoriaRepository;

  @Mock
  private MemoriaRepository memoriaRepository;

  @Mock
  private TipoDocumentoRepository tipoDocumentoRepository;

  private DocumentacionMemoriaService documentacionMemoriaService;

  @Mock
  private TipoDocumentoService tipoDocumentoService;

  @Mock
  private FormularioRepository formularioRepository;

  @BeforeEach
  public void setUp() throws Exception {
    documentacionMemoriaService = new DocumentacionMemoriaServiceImpl(documentacionMemoriaRepository, memoriaRepository,
        tipoDocumentoRepository, formularioRepository);
  }

  @Test
  public void find_WithId_ReturnsDocumentacionMemoria() {

    Memoria memoria = generarMockMemoria(1L, "Memoria1", 1L);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);

    BDDMockito.given(documentacionMemoriaRepository.findById(1L))
        .willReturn(Optional.of(generarMockDocumentacionMemoria(1L, memoria, tipoDocumento)));

    DocumentacionMemoria documentacionMemoria = documentacionMemoriaService.findById(1L);

    Assertions.assertThat(documentacionMemoria.getId()).isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(documentacionMemoria.getMemoria().getTitulo(), Language.ES))
        .isEqualTo("Memoria1");
    Assertions
        .assertThat(I18nHelper.getValueForLanguage(documentacionMemoria.getTipoDocumento().getNombre(), Language.ES))
        .isEqualTo("TipoDocumento1");
    Assertions.assertThat(documentacionMemoria.getDocumentoRef()).isEqualTo("doc-001");

  }

  @Test
  public void find_NotFound_ThrowsDocumentacionMemoriaNotFoundException() throws Exception {
    BDDMockito.given(documentacionMemoriaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> documentacionMemoriaService.findById(1L))
        .isInstanceOf(DocumentacionMemoriaNotFoundException.class);
  }

  @Test
  public void findDocumentacionMemoriaIdValid() {
    // given: EL id de la memoria es valido
    Long memoriaId = 12L;
    Memoria memoria = generarMockMemoria(memoriaId, "Titulo", 1L);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(memoria));

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    List<DocumentacionMemoria> response = new LinkedList<DocumentacionMemoria>();
    response.add(generarMockDocumentacionMemoria(Long.valueOf(1), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(3), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(5), memoria, tipoDocumento));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<DocumentacionMemoria> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(documentacionMemoriaRepository.findAll(ArgumentMatchers.<Specification<DocumentacionMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<DocumentacionMemoria> result = documentacionMemoriaService.findDocumentacionMemoria(memoriaId, pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findDocumentacionMemoriaIdNotValid() {
    // given: EL id de la memoria sea null
    Long memoriaId = null;
    try {
      // when: se listar sus evaluaciones
      documentacionMemoriaService.findDocumentacionMemoria(memoriaId, Pageable.unpaged());
      Assertions.fail("Identificador de Memoria no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Memoria no puede ser nulo");
    }
  }

  @Test
  public void findDocumentacionSeguimientoAnualIdValid() {
    // given: EL id de la memoria es valido
    Long memoriaId = 12L;
    Memoria memoria = generarMockMemoria(memoriaId, "Titulo", 9L);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(memoria));

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    List<DocumentacionMemoria> response = new LinkedList<DocumentacionMemoria>();
    response.add(generarMockDocumentacionMemoria(Long.valueOf(1), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(3), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(5), memoria, tipoDocumento));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<DocumentacionMemoria> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(documentacionMemoriaRepository.findAll(ArgumentMatchers.<Specification<DocumentacionMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<DocumentacionMemoria> result = documentacionMemoriaService.findDocumentacionSeguimientoAnual(memoriaId,
        pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findDocumentacionSeguimientoAnualIdNotValid() {
    // given: EL id de la memoria sea null
    Long memoriaId = null;
    try {
      // when: se listar sus evaluaciones
      documentacionMemoriaService.findDocumentacionSeguimientoAnual(memoriaId, Pageable.unpaged());
      Assertions.fail("Identificador de Memoria no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Memoria no puede ser nulo");
    }
  }

  @Test
  public void findDocumentacionSeguimientoFinalIdValid() {
    // given: EL id de la memoria es valido
    Long memoriaId = 12L;
    Memoria memoria = generarMockMemoria(memoriaId, "Titulo", 14L);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(memoria));

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    List<DocumentacionMemoria> response = new LinkedList<DocumentacionMemoria>();
    response.add(generarMockDocumentacionMemoria(Long.valueOf(1), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(3), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(5), memoria, tipoDocumento));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<DocumentacionMemoria> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(documentacionMemoriaRepository.findAll(ArgumentMatchers.<Specification<DocumentacionMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<DocumentacionMemoria> result = documentacionMemoriaService.findDocumentacionSeguimientoFinal(memoriaId,
        pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findDocumentacionSeguimientoFinalIdNotValid() {
    // given: EL id de la memoria sea null
    Long memoriaId = null;
    try {
      // when: se listar sus evaluaciones
      documentacionMemoriaService.findDocumentacionSeguimientoFinal(memoriaId, Pageable.unpaged());
      Assertions.fail("Identificador de Memoria no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Memoria no puede ser nulo");
    }
  }

  @Test
  public void findDocumentacionRetrospectivaIdValid() {
    // given: EL id de la memoria es valido
    Long memoriaId = 12L;
    Memoria memoria = generarMockMemoria(memoriaId, "Titulo", 1L);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(memoria));

    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    List<DocumentacionMemoria> response = new LinkedList<DocumentacionMemoria>();
    response.add(generarMockDocumentacionMemoria(Long.valueOf(1), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(3), memoria, tipoDocumento));
    response.add(generarMockDocumentacionMemoria(Long.valueOf(5), memoria, tipoDocumento));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<DocumentacionMemoria> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(documentacionMemoriaRepository.findAll(ArgumentMatchers.<Specification<DocumentacionMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<DocumentacionMemoria> result = documentacionMemoriaService.findDocumentacionRetrospectiva(memoriaId, pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findDocumentacionRetrospectivaIdNotValid() {
    // given: EL id de la memoria sea null
    Long memoriaId = null;
    try {
      // when: se listar sus evaluaciones
      documentacionMemoriaService.findDocumentacionRetrospectiva(memoriaId, Pageable.unpaged());
      Assertions.fail("Identificador de Memoria no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Memoria no puede ser nulo");
    }
  }

  @Test
  public void createDocumentacionInicial_ReturnsDocumentacion() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 5L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria,
        generarMockTipoDocumento(4L));

    DocumentacionMemoria documentacionMemoria = generarMockDocumentacionMemoria(1L, memoria,
        generarMockTipoDocumento(4L));

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(Mockito.anyLong())).willReturn(Optional.of(memoria));

    BDDMockito.given(documentacionMemoriaRepository.save(documentacionMemoriaNew)).willReturn(documentacionMemoria);

    // when: Creamos la documentación memoria
    DocumentacionMemoria documentacionMemoriaCreada = documentacionMemoriaService.createDocumentacionInicial(1L,
        documentacionMemoriaNew);

    // then: El documentación memoria se crea correctamente
    Assertions.assertThat(documentacionMemoriaCreada).isNotNull();
    Assertions.assertThat(documentacionMemoriaCreada.getId()).isEqualTo(1L);
    Assertions.assertThat(documentacionMemoria.getMemoria().getId()).isEqualTo(1L);
  }

  @Test
  public void createDocumentacionInicialInvestigador_ReturnsDocumentacion() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria,
        generarMockTipoDocumento(4L));

    DocumentacionMemoria documentacionMemoria = generarMockDocumentacionMemoria(1L, memoria,
        generarMockTipoDocumento(4L));

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(Mockito.anyLong())).willReturn(Optional.of(memoria));

    BDDMockito.given(documentacionMemoriaRepository.save(documentacionMemoriaNew)).willReturn(documentacionMemoria);

    // when: Creamos la documentación memoria
    DocumentacionMemoria documentacionMemoriaCreada = documentacionMemoriaService
        .createDocumentacionInicialInvestigador(1L,
            documentacionMemoriaNew);

    // then: El documentación memoria se crea correctamente
    Assertions.assertThat(documentacionMemoriaCreada).isNotNull();
    Assertions.assertThat(documentacionMemoriaCreada.getId()).isEqualTo(1L);
    Assertions.assertThat(documentacionMemoria.getMemoria().getId()).isEqualTo(1L);
  }

  @Test
  public void createDocumentacionInicial_IdDocumentacionNotNull() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(1L, memoria,
        generarMockTipoDocumento(4L));

    try {
      // when: Creamos la documentación memoria con id de documentación memoria
      // distinto de null
      documentacionMemoriaService.createDocumentacionInicial(1L, documentacionMemoriaNew);
      Assertions.fail("Identificador de Documentación Memoria debe ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Documentación Memoria debe ser nulo");
    }

  }

  @Test
  public void createDocumentacionInicial_IdMemoriaNull() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria,
        generarMockTipoDocumento(4L));

    try {
      // when: Creamos la documentación memoria con id de memoria null
      documentacionMemoriaService.createDocumentacionInicial(null, documentacionMemoriaNew);
      Assertions.fail("Identificador de Memoria no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Memoria no puede ser nulo");
    }

  }

  @Test
  public void createDocumentacionInicial_throwNotFoundMemoria() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria,
        generarMockTipoDocumento(4L));
    Assertions
        .assertThatThrownBy(
            () -> documentacionMemoriaService.createDocumentacionInicial(1L, documentacionMemoriaNew))
        .isInstanceOf(MemoriaNotFoundException.class);

  }

  @Test
  public void createDocumentacionInicial_failStatusMemoria() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 4L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria,
        generarMockTipoDocumento(4L));

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(Mockito.anyLong())).willReturn(Optional.of(memoria));

    try {
      // when: Creamos la documentación memoria con id de memoria null
      documentacionMemoriaService.createDocumentacionInicial(1L, documentacionMemoriaNew);
      Assertions.fail("La memoria no se encuentra en un estado adecuado para realizar la acción");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("La memoria no se encuentra en un estado adecuado para realizar la acción");
    }

  }

  @Test
  public void createSeguimientoAnual_ReturnsDocumentacion() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 9L);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria, tipoDocumento);

    DocumentacionMemoria documentacionMemoria = generarMockDocumentacionMemoria(1L, memoria, tipoDocumento);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(Mockito.anyLong())).willReturn(Optional.of(memoria));

    BDDMockito.given(tipoDocumentoRepository.findByFormularioIdAndActivoTrue(Mockito.anyLong()))
        .willReturn(Arrays.asList(tipoDocumento));

    BDDMockito.given(documentacionMemoriaRepository.save(documentacionMemoriaNew)).willReturn(documentacionMemoria);

    // when: Creamos la documentación memoria
    DocumentacionMemoria documentacionMemoriaCreada = documentacionMemoriaService.createSeguimientoAnual(1L,
        documentacionMemoriaNew);

    // then: El documentación memoria se crea correctamente
    Assertions.assertThat(documentacionMemoriaCreada).isNotNull();
    Assertions.assertThat(documentacionMemoriaCreada.getId()).isEqualTo(1L);
    Assertions.assertThat(documentacionMemoria.getMemoria().getId()).isEqualTo(1L);
  }

  @Test
  public void createSeguimientoAnual_IdDocumentacionNotNull() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 9L);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(1L, memoria, tipoDocumento);

    try {
      // when: Creamos la documentación memoria con id de documentación memoria
      // distinto de null
      documentacionMemoriaService.createSeguimientoAnual(1L, documentacionMemoriaNew);
      Assertions.fail("Identificador de Documentación Memoria debe ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Documentación Memoria debe ser nulo");
    }

  }

  @Test
  public void createSeguimientoAnual_IdMemoriaNull() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 9L);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria, tipoDocumento);

    try {
      // when: Creamos la documentación memoria con id de memoria null
      documentacionMemoriaService.createSeguimientoAnual(null, documentacionMemoriaNew);
      Assertions.fail(
          "Identificador de Memoria no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "Identificador de Memoria no puede ser nulo");
    }

  }

  @Test
  public void createSeguimientoAnual_throwNotFoundMemoria() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 9L);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria, tipoDocumento);

    Assertions.assertThatThrownBy(() -> documentacionMemoriaService.createSeguimientoAnual(1L, documentacionMemoriaNew))
        .isInstanceOf(MemoriaNotFoundException.class);

  }

  @Test
  public void createSeguimientoAnual_failStatusMemoria() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 1L);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria, tipoDocumento);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(Mockito.anyLong())).willReturn(Optional.of(memoria));

    try {
      // when: Creamos la documentación memoria con id de memoria null
      documentacionMemoriaService.createSeguimientoAnual(1L, documentacionMemoriaNew);
      Assertions
          .fail("La memoria no se encuentra en un estado adecuado para realizar la acción");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("La memoria no se encuentra en un estado adecuado para realizar la acción");
    }

  }

  @Test
  public void createSeguimientoFinal_ReturnsDocumentacion() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 16L);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria, tipoDocumento);

    DocumentacionMemoria documentacionMemoria = generarMockDocumentacionMemoria(1L, memoria, tipoDocumento);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(Mockito.anyLong())).willReturn(Optional.of(memoria));

    BDDMockito.given(tipoDocumentoRepository.findByFormularioIdAndActivoTrue(Mockito.anyLong()))
        .willReturn(Arrays.asList(tipoDocumento));

    BDDMockito.given(documentacionMemoriaRepository.save(documentacionMemoriaNew)).willReturn(documentacionMemoria);

    // when: Creamos la documentación memoria
    DocumentacionMemoria documentacionMemoriaCreada = documentacionMemoriaService.createSeguimientoFinal(1L,
        documentacionMemoriaNew);

    // then: El documentación memoria se crea correctamente
    Assertions.assertThat(documentacionMemoriaCreada).isNotNull();
    Assertions.assertThat(documentacionMemoriaCreada.getId()).isEqualTo(1L);
    Assertions.assertThat(documentacionMemoria.getMemoria().getId()).isEqualTo(1L);
  }

  @Test
  public void createSeguimientoFinal_IdDocumentacionNotNull() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 16L);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(1L, memoria, tipoDocumento);

    try {
      // when: Creamos la documentación memoria con id de documentación memoria
      // distinto de null
      documentacionMemoriaService.createSeguimientoFinal(1L, documentacionMemoriaNew);
      Assertions.fail("Identificador de Documentación Memoria debe ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Documentación Memoria debe ser nulo");
    }

  }

  @Test
  public void createSeguimientoFinal_IdMemoriaNull() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 16L);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria, tipoDocumento);

    try {
      // when: Creamos la documentación memoria con id de memoria null
      documentacionMemoriaService.createSeguimientoFinal(null, documentacionMemoriaNew);
      Assertions.fail(
          "Identificador de Memoria no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "Identificador de Memoria no puede ser nulo");
    }

  }

  @Test
  public void createSeguimientoFinal_throwNotFoundMemoria() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 16L);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria, tipoDocumento);

    Assertions.assertThatThrownBy(() -> documentacionMemoriaService.createSeguimientoFinal(1L, documentacionMemoriaNew))
        .isInstanceOf(MemoriaNotFoundException.class);

  }

  @Test
  public void createSeguimientoFinal_failStatusMemoria() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 1L);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria, tipoDocumento);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(Mockito.anyLong())).willReturn(Optional.of(memoria));

    try {
      // when: Creamos la documentación memoria con id de memoria null
      documentacionMemoriaService.createSeguimientoFinal(1L, documentacionMemoriaNew);
      Assertions
          .fail("La memoria no se encuentra en un estado adecuado para realizar la acción");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("La memoria no se encuentra en un estado adecuado para realizar la acción");
    }

  }

  @Test
  public void createRetrospectiva_ReturnsDocumentacion() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 16L);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria, tipoDocumento);

    DocumentacionMemoria documentacionMemoria = generarMockDocumentacionMemoria(1L, memoria, tipoDocumento);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(Mockito.anyLong())).willReturn(Optional.of(memoria));

    BDDMockito.given(tipoDocumentoRepository.findByFormularioIdAndActivoTrue(Mockito.anyLong()))
        .willReturn(Arrays.asList(tipoDocumento));

    BDDMockito.given(documentacionMemoriaRepository.save(documentacionMemoriaNew)).willReturn(documentacionMemoria);

    // when: Creamos la documentación memoria
    DocumentacionMemoria documentacionMemoriaCreada = documentacionMemoriaService.createRetrospectiva(1L,
        documentacionMemoriaNew);

    // then: El documentación memoria se crea correctamente
    Assertions.assertThat(documentacionMemoriaCreada).isNotNull();
    Assertions.assertThat(documentacionMemoriaCreada.getId()).isEqualTo(1L);
    Assertions.assertThat(documentacionMemoria.getMemoria().getId()).isEqualTo(1L);
  }

  @Test
  public void createRetrospectiva_IdDocumentacionNotNull() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 16L);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(1L, memoria, tipoDocumento);

    try {
      // when: Creamos la documentación memoria con id de documentación memoria
      // distinto de null
      documentacionMemoriaService.createRetrospectiva(1L, documentacionMemoriaNew);
      Assertions.fail("Identificador de Documentación Memoria debe ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Documentación Memoria debe ser nulo");
    }

  }

  @Test
  public void createRetrospectiva_IdMemoriaNull() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 16L);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria, tipoDocumento);

    try {
      // when: Creamos la documentación memoria con id de memoria null
      documentacionMemoriaService.createRetrospectiva(null, documentacionMemoriaNew);
      Assertions.fail(
          "Identificador de Memoria no puede ser nulo");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "Identificador de Memoria no puede ser nulo");
    }

  }

  @Test
  public void createRetrospectiva_throwNotFoundMemoria() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 16L);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria, tipoDocumento);

    Assertions.assertThatThrownBy(() -> documentacionMemoriaService.createRetrospectiva(1L, documentacionMemoriaNew))
        .isInstanceOf(MemoriaNotFoundException.class);

  }

  @Test
  public void createRetrospectiva_failStatusMemoria() {
    // given: Una nueva documentación
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 9L);
    EstadoRetrospectiva estadoRetrospectiva = memoria.getRetrospectiva().getEstadoRetrospectiva();
    estadoRetrospectiva.setId(3L);
    memoria.getRetrospectiva().setEstadoRetrospectiva(estadoRetrospectiva);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(1L);
    DocumentacionMemoria documentacionMemoriaNew = generarMockDocumentacionMemoria(null, memoria, tipoDocumento);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(Mockito.anyLong())).willReturn(Optional.of(memoria));

    try {
      // when: Creamos la documentación memoria con id de memoria null
      documentacionMemoriaService.createRetrospectiva(1L, documentacionMemoriaNew);
      Assertions.fail(
          "La retrospectiva no se encuentra en un estado adecuado para realizar la acción");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "La retrospectiva no se encuentra en un estado adecuado para realizar la acción");
    }

  }

  @Test
  public void deleteDocumentacionSeguimientoAnual_DocumentacionMemoriaIdNull() {

    try {

      // when: borramos con id documentación memoria a null
      documentacionMemoriaService.deleteDocumentacionSeguimientoAnual(1L, null);
      Assertions.fail(
          "Identificador de Documentación Memoria no puede ser nulo");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "Identificador de Documentación Memoria no puede ser nulo");
    }

  }

  @Test
  public void deleteDocumentacionSeguimientoAnual_MemoriaIdNull() {
    try {

      // when: Se elimina la documentación enviando id memoria a null
      documentacionMemoriaService.deleteDocumentacionSeguimientoAnual(null, 1L);
      Assertions.fail(
          "Identificador de Memoria no puede ser nulo");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "Identificador de Memoria no puede ser nulo");
    }

  }

  @Test
  public void deleteDocumentacionSeguimientoAnual_throwMemoriaNotFound() {

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> documentacionMemoriaService.deleteDocumentacionSeguimientoAnual(1L, 1L))
        .isInstanceOf(MemoriaNotFoundException.class);

  }

  @Test
  public void deleteDocumentacionSeguimientoAnual_throwTipoDocumentoNotFound() {
    // given: memoria
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 9L, 4L);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.of(memoria));

    BDDMockito.given(
        documentacionMemoriaRepository.findByIdAndMemoriaIdAndTipoDocumentoFormularioIdAndMemoriaActivoTrue(1L, 1L, 4L))
        .willReturn(Optional.empty());
    Assertions.assertThatThrownBy(() -> documentacionMemoriaService.deleteDocumentacionSeguimientoAnual(1L, 1L))
        .isInstanceOf(DocumentacionMemoriaNotFoundException.class);

  }

  @Test
  public void deleteDocumentacionSeguimientoAnual_estadoMemoriaFail() {
    // given: memoria en estado inadecuado
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 4L);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.of(memoria));

    try {

      // when: se elimina la documentación
      documentacionMemoriaService.deleteDocumentacionSeguimientoAnual(1L, 1L);
      Assertions
          .fail("La memoria no se encuentra en un estado adecuado para realizar la acción");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "La memoria no se encuentra en un estado adecuado para realizar la acción");
    }

  }

  @Test
  public void deleteDocumentacionSeguimientoFinal_DocumentacionMemoriaIdNull() {

    try {

      // when: borramos con id documentación memoria a null
      documentacionMemoriaService.deleteDocumentacionSeguimientoFinal(1L, null);
      Assertions.fail(
          "Identificador de Documentación Memoria no puede ser nulo");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "Identificador de Documentación Memoria no puede ser nulo");
    }

  }

  @Test
  public void deleteDocumentacionSeguimientoFinal_MemoriaIdNull() {
    try {

      // when: Se elimina la documentación enviando id memoria a null
      documentacionMemoriaService.deleteDocumentacionSeguimientoFinal(null, 1L);
      Assertions.fail(
          "Identificador de Memoria no puede ser nulo");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "Identificador de Memoria no puede ser nulo");
    }

  }

  @Test
  public void deleteDocumentacionSeguimientoFinal_throwMemoriaNotFound() {

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> documentacionMemoriaService.deleteDocumentacionSeguimientoFinal(1L, 1L))
        .isInstanceOf(MemoriaNotFoundException.class);

  }

  @Test
  public void deleteDocumentacionSeguimientoFinal_throwTipoDocumentoNotFound() {
    // given: memoria
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 14L, 5L);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.of(memoria));

    BDDMockito.given(
        documentacionMemoriaRepository.findByIdAndMemoriaIdAndTipoDocumentoFormularioIdAndMemoriaActivoTrue(1L, 1L, 5L))
        .willReturn(Optional.empty());
    Assertions.assertThatThrownBy(() -> documentacionMemoriaService.deleteDocumentacionSeguimientoFinal(1L, 1L))
        .isInstanceOf(DocumentacionMemoriaNotFoundException.class);

  }

  @Test
  public void deleteDocumentacionSeguimientoFinal_estadoMemoriaFail() {
    // given: memoria en estado inadecuado
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 4L);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.of(memoria));

    try {

      // when: se elimina la documentación
      documentacionMemoriaService.deleteDocumentacionSeguimientoFinal(1L, 1L);
      Assertions
          .fail("La memoria no se encuentra en un estado adecuado para realizar la acción");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "La memoria no se encuentra en un estado adecuado para realizar la acción");
    }

  }

  @Test
  public void deleteDocumentacionRetrospectiva_DocumentacionMemoriaIdNull() {

    try {

      // when: borramos con id documentación memoria a null
      documentacionMemoriaService.deleteDocumentacionRetrospectiva(1L, null);
      Assertions
          .fail("Identificador de Documentación Memoria no puede ser nulo");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "Identificador de Documentación Memoria no puede ser nulo");
    }

  }

  @Test
  public void deleteDocumentacionRetrospectiva_MemoriaIdNull() {
    try {

      // when: Se elimina la documentación enviando id memoria a null
      documentacionMemoriaService.deleteDocumentacionRetrospectiva(null, 1L);
      Assertions.fail(
          "Identificador de Memoria no puede ser nulo");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "Identificador de Memoria no puede ser nulo");
    }

  }

  @Test
  public void deleteDocumentacionRetrospectiva_throwMemoriaNotFound() {

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> documentacionMemoriaService.deleteDocumentacionRetrospectiva(1L, 1L))
        .isInstanceOf(MemoriaNotFoundException.class);

  }

  @Test
  public void deleteDocumentacionRetrospectiva_throwTipoDocumentoNotFound() {
    // given: memoria
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 14L, 6L);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.of(memoria));

    BDDMockito.given(
        documentacionMemoriaRepository.findByIdAndMemoriaIdAndTipoDocumentoFormularioIdAndMemoriaActivoTrue(1L, 1L, 6L))
        .willReturn(Optional.empty());
    Assertions.assertThatThrownBy(() -> documentacionMemoriaService.deleteDocumentacionRetrospectiva(1L, 1L))
        .isInstanceOf(DocumentacionMemoriaNotFoundException.class);

  }

  @Test
  public void deleteDocumentacionRetrospectiva_estadoMemoriaFail() {
    // given: memoria en estado inadecuado
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 16L);

    EstadoRetrospectiva estadoRetrospectiva = memoria.getRetrospectiva().getEstadoRetrospectiva();
    estadoRetrospectiva.setId(3L);
    memoria.getRetrospectiva().setEstadoRetrospectiva(estadoRetrospectiva);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.of(memoria));

    try {

      // when: se elimina la documentación
      documentacionMemoriaService.deleteDocumentacionRetrospectiva(1L, 1L);
      Assertions
          .fail("La retrospectiva no se encuentra en un estado adecuado para realizar la acción");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "La retrospectiva no se encuentra en un estado adecuado para realizar la acción");
    }

  }

  @Test
  public void deleteDocumentacionInicial_DocumentacionMemoriaIdNull() {

    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      // when: borramos con id documentación memoria a null
      documentacionMemoriaService.deleteDocumentacionInicial(1L, null, authentication);
      Assertions.fail("Identificador de Documentación Memoria no puede ser nulo");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Identificador de Documentación Memoria no puede ser nulo");
    }

  }

  @Test
  public void deleteDocumentacionInicial_MemoriaIdNull() {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      // when: Se elimina la documentación enviando id memoria a null
      documentacionMemoriaService.deleteDocumentacionInicial(null, 1L, authentication);
      Assertions.fail(
          "Identificador de Memoria no puede ser nulo");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "Identificador de Memoria no puede ser nulo");
    }

  }

  @Test
  public void deleteDocumentacionInicial_throwMemoriaNotFound() {

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> documentacionMemoriaService.deleteDocumentacionSeguimientoAnual(1L, 1L))
        .isInstanceOf(MemoriaNotFoundException.class);

  }

  @Test
  public void deleteDocumentacionInicial_throwTipoDocumentoNotFound() {
    // given: memoria
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 9L, 4L);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.of(memoria));

    BDDMockito.given(
        documentacionMemoriaRepository.findByIdAndMemoriaIdAndTipoDocumentoFormularioIdAndMemoriaActivoTrue(1L, 1L, 4L))
        .willReturn(Optional.empty());
    Assertions.assertThatThrownBy(() -> documentacionMemoriaService.deleteDocumentacionSeguimientoAnual(1L, 1L))
        .isInstanceOf(DocumentacionMemoriaNotFoundException.class);

  }

  @Test
  @WithMockUser(username = "user", authorities = { "ETI-PEV-INV-ER" })
  public void deleteDocumentacionInicial_estadoMemoriaFail() {
    // given: memoria en estado inadecuado
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 4L);

    BDDMockito.given(memoriaRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.of(memoria));

    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      // when: se elimina la documentación
      documentacionMemoriaService.deleteDocumentacionInicial(1L, 1L, authentication);
      Assertions.fail("La memoria no se encuentra en un estado adecuado para realizar la acción");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("La memoria no se encuentra en un estado adecuado para realizar la acción");
    }

  }

  /**
   * Función que devuelve un objeto DocumentacionMemoria
   * 
   * @param id            id de DocumentacionMemoria
   * @param memoria       la Memoria de DocumentacionMemoria
   * @param tipoDocumento el TipoDocumento de DocumentacionMemoria
   * @return el objeto DocumentacionMemoria
   */

  private DocumentacionMemoria generarMockDocumentacionMemoria(Long id, Memoria memoria, TipoDocumento tipoDocumento) {

    Set<DocumentacionMemoriaNombre> nombre = new HashSet<>();
    nombre.add(new DocumentacionMemoriaNombre(Language.ES, "doc-00" + id));
    DocumentacionMemoria documentacionMemoria = new DocumentacionMemoria();
    documentacionMemoria.setId(id);
    documentacionMemoria.setMemoria(memoria);
    documentacionMemoria.setTipoDocumento(tipoDocumento);
    documentacionMemoria.setDocumentoRef("doc-00" + id);
    documentacionMemoria.setNombre(nombre);

    return documentacionMemoria;
  }

  /**
   * Función que devuelve un objeto Memoria
   * 
   * @param id           id del Memoria
   * @param titulo       título de la memoria
   * @param estadoId     id del estado actual
   * @param formularioId id del formulario
   * @return el objeto Memoria
   */
  private Memoria generarMockMemoria(Long id, String titulo, Long estadoId, Long formularioId) {

    Formulario formulario = new Formulario();
    formulario.setId(formularioId);
    formulario.setCodigo("M10/2020/001");

    Set<MemoriaTitulo> mTitulo = new HashSet<>();
    mTitulo.add(new MemoriaTitulo(Language.ES, titulo));
    Memoria memoria = new Memoria();
    memoria.setId(id);
    memoria.setTitulo(mTitulo);
    memoria.setFormulario(formulario);
    memoria.setFormularioSeguimientoAnual(formulario);
    memoria.setFormularioSeguimientoFinal(formulario);
    memoria.setFormularioRetrospectiva(formulario);

    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(estadoId);

    memoria.setEstadoActual(tipoEstadoMemoria);
    memoria.setActivo(Boolean.TRUE);

    EstadoRetrospectiva estadoRetrospectiva = new EstadoRetrospectiva();
    estadoRetrospectiva.setId(1L);
    Retrospectiva retrospectiva = new Retrospectiva();
    retrospectiva.setId(1L);
    retrospectiva.setEstadoRetrospectiva(estadoRetrospectiva);
    memoria.setRetrospectiva(retrospectiva);

    return memoria;
  }

  private Memoria generarMockMemoria(Long id, String titulo, Long estadoId) {
    return generarMockMemoria(id, titulo, estadoId, 1L);
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id) {

    Set<TipoDocumentoNombre> nombre = new HashSet<>();
    nombre.add(new TipoDocumentoNombre(Language.ES, "TipoDocumento" + id));
    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nombre);

    return tipoDocumento;
  }

}