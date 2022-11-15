package org.crue.hercules.sgi.eti.service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.DocumentacionMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.repository.DocumentacionMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.FormularioRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.eti.service.impl.DocumentacionMemoriaServiceImpl;
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
    Assertions.assertThat(documentacionMemoria.getMemoria().getTitulo()).isEqualTo("Memoria1");
    Assertions.assertThat(documentacionMemoria.getTipoDocumento().getNombre()).isEqualTo("TipoDocumento1");
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
    Formulario formulario = generarMockFormulario(1L);

    BDDMockito.given(formularioRepository.findByMemoriaId(ArgumentMatchers.anyLong())).willReturn(formulario);
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
      Assertions.fail("El id de la memoria no puede ser nulo para mostrar su documentación");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El id de la memoria no puede ser nulo para mostrar su documentación");
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
      Assertions.fail("El id de la memoria no puede ser nulo para mostrar su documentación");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El id de la memoria no puede ser nulo para mostrar su documentación");
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
      Assertions.fail("El id de la memoria no puede ser nulo para mostrar su documentación");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El id de la memoria no puede ser nulo para mostrar su documentación");
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
      Assertions.fail("El id de la memoria no puede ser nulo para mostrar su documentación");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El id de la memoria no puede ser nulo para mostrar su documentación");
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
      Assertions.fail("DocumentacionMemoria id tiene que ser null para crear un nuevo DocumentacionMemoria");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("DocumentacionMemoria id tiene que ser null para crear un nuevo DocumentacionMemoria");
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
      Assertions.fail("El identificador de la memoria no puede ser null para crear un nuevo documento asociado a esta");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El identificador de la memoria no puede ser null para crear un nuevo documento asociado a esta");
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
      Assertions.fail("La memoria no se encuentra en un estado adecuado para añadir documentación.");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("La memoria no se encuentra en un estado adecuado para añadir documentación.");
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
      Assertions.fail("DocumentacionMemoria id tiene que ser null para crear un nuevo DocumentacionMemoria");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("DocumentacionMemoria id tiene que ser null para crear un nuevo DocumentacionMemoria");
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
          "El identificador de la memoria no puede ser null para crear un nuevo documento de tipo seguimiento anual asociado a esta");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "El identificador de la memoria no puede ser null para crear un nuevo documento de tipo seguimiento anual asociado a esta");
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
          .fail("La memoria no se encuentra en un estado adecuado para añadir documentación de seguimiento anual");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("La memoria no se encuentra en un estado adecuado para añadir documentación de seguimiento anual");
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
      Assertions.fail("DocumentacionMemoria id tiene que ser null para crear un nuevo DocumentacionMemoria");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("DocumentacionMemoria id tiene que ser null para crear un nuevo DocumentacionMemoria");
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
          "El identificador de la memoria no puede ser null para crear un nuevo documento de tipo seguimiento final asociado a esta");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "El identificador de la memoria no puede ser null para crear un nuevo documento de tipo seguimiento final asociado a esta");
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
          .fail("La memoria no se encuentra en un estado adecuado para añadir documentación de seguimiento final");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("La memoria no se encuentra en un estado adecuado para añadir documentación de seguimiento final");
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
      Assertions.fail("DocumentacionMemoria id tiene que ser null para crear un nuevo DocumentacionMemoria");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("DocumentacionMemoria id tiene que ser null para crear un nuevo DocumentacionMemoria");
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
          "El identificador de la memoria no puede ser null para crear un nuevo documento de tipo retrospectiva asociado a esta");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "El identificador de la memoria no puede ser null para crear un nuevo documento de tipo retrospectiva asociado a esta");
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
          "La retrospectiva no se encuentra en un estado adecuado para crear documentación de tipo retrospectiva");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "La retrospectiva no se encuentra en un estado adecuado para crear documentación de tipo retrospectiva");
    }

  }

  @Test
  public void deleteDocumentacionSeguimientoAnual_DocumentacionMemoriaIdNull() {

    try {

      // when: borramos con id documentación memoria a null
      documentacionMemoriaService.deleteDocumentacionSeguimientoAnual(1L, null);
      Assertions.fail(
          "DocumentacionMemoria id tiene no puede ser null para eliminar uun documento de tipo seguimiento anual");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "DocumentacionMemoria id tiene no puede ser null para eliminar uun documento de tipo seguimiento anual");
    }

  }

  @Test
  public void deleteDocumentacionSeguimientoAnual_MemoriaIdNull() {
    try {

      // when: Se elimina la documentación enviando id memoria a null
      documentacionMemoriaService.deleteDocumentacionSeguimientoAnual(null, 1L);
      Assertions.fail(
          "El identificador de la memoria no puede ser null para eliminar un documento de tipo seguimiento anual asociado a esta");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "El identificador de la memoria no puede ser null para eliminar un documento de tipo seguimiento anual asociado a esta");
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
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 9L);

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
          .fail("La memoria no se encuentra en un estado adecuado para eliminar documentación de seguimiento anual");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "La memoria no se encuentra en un estado adecuado para eliminar documentación de seguimiento anual");
    }

  }

  @Test
  public void deleteDocumentacionSeguimientoFinal_DocumentacionMemoriaIdNull() {

    try {

      // when: borramos con id documentación memoria a null
      documentacionMemoriaService.deleteDocumentacionSeguimientoFinal(1L, null);
      Assertions.fail(
          "DocumentacionMemoria id tiene no puede ser null para eliminar uun documento de tipo seguimiento final");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "DocumentacionMemoria id tiene no puede ser null para eliminar uun documento de tipo seguimiento final");
    }

  }

  @Test
  public void deleteDocumentacionSeguimientoFinal_MemoriaIdNull() {
    try {

      // when: Se elimina la documentación enviando id memoria a null
      documentacionMemoriaService.deleteDocumentacionSeguimientoFinal(null, 1L);
      Assertions.fail(
          "El identificador de la memoria no puede ser null para eliminar un documento de tipo seguimiento final asociado a esta");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "El identificador de la memoria no puede ser null para eliminar un documento de tipo seguimiento final asociado a esta");
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
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 14L);

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
          .fail("La memoria no se encuentra en un estado adecuado para eliminar documentación de seguimiento final");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "La memoria no se encuentra en un estado adecuado para eliminar documentación de seguimiento final");
    }

  }

  @Test
  public void deleteDocumentacionRetrospectiva_DocumentacionMemoriaIdNull() {

    try {

      // when: borramos con id documentación memoria a null
      documentacionMemoriaService.deleteDocumentacionRetrospectiva(1L, null);
      Assertions
          .fail("DocumentacionMemoria id tiene no puede ser null para eliminar uun documento de tipo retrospectiva");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "DocumentacionMemoria id tiene no puede ser null para eliminar uun documento de tipo retrospectiva");
    }

  }

  @Test
  public void deleteDocumentacionRetrospectiva_MemoriaIdNull() {
    try {

      // when: Se elimina la documentación enviando id memoria a null
      documentacionMemoriaService.deleteDocumentacionRetrospectiva(null, 1L);
      Assertions.fail(
          "El identificador de la memoria no puede ser null para eliminar un documento de tipo retrospectiva asociado a esta");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "El identificador de la memoria no puede ser null para eliminar un documento de tipo retrospectiva asociado a esta");
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
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 14L);

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
          .fail("La retrospectiva no se encuentra en un estado adecuado para eliminar documentación de retrospectiva");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "La retrospectiva no se encuentra en un estado adecuado para eliminar documentación de retrospectiva");
    }

  }

  @Test
  public void deleteDocumentacionInicial_DocumentacionMemoriaIdNull() {

    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      // when: borramos con id documentación memoria a null
      documentacionMemoriaService.deleteDocumentacionInicial(1L, null, authentication);
      Assertions.fail("DocumentacionMemoria id tiene no puede ser null para eliminar uun documento de tipo inicial");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("DocumentacionMemoria id tiene no puede ser null para eliminar uun documento de tipo inicial");
    }

  }

  @Test
  public void deleteDocumentacionInicial_MemoriaIdNull() {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      // when: Se elimina la documentación enviando id memoria a null
      documentacionMemoriaService.deleteDocumentacionInicial(null, 1L, authentication);
      Assertions.fail(
          "El identificador de la memoria no puede ser null para eliminar un documento de tipo inicial asociado a esta");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage()).isEqualTo(
          "El identificador de la memoria no puede ser null para eliminar un documento de tipo inicial asociado a esta");
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
    Memoria memoria = generarMockMemoria(1L, "Memoria1", 9L);

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
      Assertions.fail("La memoria no se encuentra en un estado adecuado para eliminar documentación inicial");
    } catch (

    final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("La memoria no se encuentra en un estado adecuado para eliminar documentación inicial");
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

  public DocumentacionMemoria generarMockDocumentacionMemoria(Long id, Memoria memoria, TipoDocumento tipoDocumento) {

    DocumentacionMemoria documentacionMemoria = new DocumentacionMemoria();
    documentacionMemoria.setId(id);
    documentacionMemoria.setMemoria(memoria);
    documentacionMemoria.setTipoDocumento(tipoDocumento);
    documentacionMemoria.setDocumentoRef("doc-00" + id);
    documentacionMemoria.setNombre("doc-00" + id);

    return documentacionMemoria;
  }

  /**
   * Función que devuelve un objeto Memoria
   * 
   * @param id       id del Memoria
   * @param titulo   título de la memoria
   * @param estadoId id del estado actual
   * @return el objeto Memoria
   */

  public Memoria generarMockMemoria(Long id, String titulo, Long estadoId) {

    Memoria memoria = new Memoria();
    memoria.setId(id);
    memoria.setTitulo(titulo);

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

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  public TipoDocumento generarMockTipoDocumento(Long id) {

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre("TipoDocumento" + id);

    return tipoDocumento;
  }

  private Formulario generarMockFormulario(Long id) {
    return new Formulario(id, "Nombre", "Descripcion");
  }
}