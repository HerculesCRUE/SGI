package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaDocumentoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ConvocatoriaDocumentoServiceTest
 */
public class ConvocatoriaDocumentoServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaDocumentoRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ModeloTipoFaseRepository modeloTipoFaseRepository;
  @Mock
  private ModeloTipoDocumentoRepository modeloTipoDocumentoRepository;

  private ConvocatoriaDocumentoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaDocumentoServiceImpl(repository, convocatoriaRepository, modeloTipoFaseRepository,
        modeloTipoDocumentoRepository);
  }

  @Test
  public void create_ReturnsConvocatoriaDocumento() {
    // given: new ConvocatoriaDocumento
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento newConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    newConvocatoriaDocumento.setId(null);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(newConvocatoriaDocumento, convocatoria);
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(newConvocatoriaDocumento, convocatoria);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    BDDMockito.given(repository.save(newConvocatoriaDocumento)).will((InvocationOnMock invocation) -> {
      ConvocatoriaDocumento convocatoriaDocumentoCreado = invocation.getArgument(0);
      convocatoriaDocumentoCreado.setId(1L);
      return convocatoriaDocumentoCreado;
    });

    // when: create ConvocatoriaDocumento
    ConvocatoriaDocumento created = service.create(newConvocatoriaDocumento);

    // then: new ConvocatoriaDocumento is created
    Assertions.assertThat(created).as("isNotNull()").isNotNull();
    Assertions.assertThat(created.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(created.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(newConvocatoriaDocumento.getConvocatoriaId());
    Assertions.assertThat(created.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(newConvocatoriaDocumento.getTipoFase().getId());
    Assertions.assertThat(newConvocatoriaDocumento.getTipoDocumento().getId()).as("getTipoDocumento()")
        .isEqualTo(newConvocatoriaDocumento.getTipoDocumento().getId());
    Assertions.assertThat(created.getNombre()).as("getNombre()").isEqualTo(newConvocatoriaDocumento.getNombre());
    Assertions.assertThat(created.getPublico()).as("getPublico()").isEqualTo(newConvocatoriaDocumento.getPublico());
    Assertions.assertThat(created.getObservaciones()).as("getObservaciones()")
        .isEqualTo(newConvocatoriaDocumento.getObservaciones());
    Assertions.assertThat(created.getDocumentoRef()).as("getDocumentoRef()")
        .isEqualTo(newConvocatoriaDocumento.getDocumentoRef());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: new ConvocatoriaDocumento with Id
    ConvocatoriaDocumento newConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, 1L, 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: Create ConvocatoriaDocumento
        () -> service.create(newConvocatoriaDocumento))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConvocatoriaDocumento id tiene que ser null para crear un nuevo ConvocatoriaDocumento");
  }

  @Test
  public void create_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: new ConvocatoriaDocumento without Convocatoria
    ConvocatoriaDocumento newConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, 1L, 1L, 1L);
    newConvocatoriaDocumento.setId(null);
    newConvocatoriaDocumento.setConvocatoriaId(null);

    Assertions.assertThatThrownBy(
        // when: Create ConvocatoriaDocumento
        () -> service.create(newConvocatoriaDocumento))
        // then: throw exception as Convocatoria is not provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Convocatoria no puede ser null en ConvocatoriaDocumento");
  }

  @Test
  public void create_WithoutNombre_ThrowsIllegalArgumentException() {
    // given: new ConvocatoriaDocumento without Nombre
    ConvocatoriaDocumento newConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, 1L, 1L, 1L);
    newConvocatoriaDocumento.setId(null);
    newConvocatoriaDocumento.setNombre(null);

    Assertions.assertThatThrownBy(
        // when: Create ConvocatoriaDocumento
        () -> service.create(newConvocatoriaDocumento))
        // then: throw exception as Nombre is not provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Es necesario indicar el nombre del documento");
  }

  @Test
  public void create_WithoutPublico_ThrowsIllegalArgumentException() {
    // given: new ConvocatoriaDocumento without Publico
    ConvocatoriaDocumento newConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, 1L, 1L, 1L);
    newConvocatoriaDocumento.setId(null);
    newConvocatoriaDocumento.setPublico(null);

    Assertions.assertThatThrownBy(
        // when: Create ConvocatoriaDocumento
        () -> service.create(newConvocatoriaDocumento))
        // then: throw exception as Publico is not provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Es necesario indicar si el documento es público");
  }

  @Test
  public void create_WithoutDocumentoRef_ThrowsIllegalArgumentException() {
    // given: new ConvocatoriaDocumento without DocumentoRef
    ConvocatoriaDocumento newConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, 1L, 1L, 1L);
    newConvocatoriaDocumento.setId(null);
    newConvocatoriaDocumento.setDocumentoRef(null);

    Assertions.assertThatThrownBy(
        // when: Create ConvocatoriaDocumento
        () -> service.create(newConvocatoriaDocumento))
        // then: throw exception as DocumentoRef is not provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Es necesario indicar la referencia al documento");
  }

  @Test
  public void create_WithNoExistingConvocatoria_ThrowsNotFoundException() {
    // given: new ConvocatoriaDocumento with no existing Convocatoria
    ConvocatoriaDocumento newConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, 1L, 1L, 1L);
    newConvocatoriaDocumento.setId(null);

    Assertions.assertThatThrownBy(
        // when: Create ConvocatoriaDocumento
        () -> service.create(newConvocatoriaDocumento))
        // then: throw exception as Convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  public void create_WithoutTipoFase_DoesNotThrowAnyException() {
    // given: a ConvocatoriaDocumento without TipoFase
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento newConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    newConvocatoriaDocumento.setId(null);
    newConvocatoriaDocumento.setTipoFase(null);
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(newConvocatoriaDocumento, convocatoria);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    BDDMockito.given(repository.save(newConvocatoriaDocumento)).will((InvocationOnMock invocation) -> {
      ConvocatoriaDocumento convocatoriaDocumentoCreado = invocation.getArgument(0);
      convocatoriaDocumentoCreado.setId(1L);
      return convocatoriaDocumentoCreado;
    });

    Assertions.assertThatCode(
        // when: create ConvocatoriaDocumento
        () -> service.create(newConvocatoriaDocumento))
        // then: no exception thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void create_WithNoExistingModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaDocumento with TipoFase not assigned to ModeloEjecucion
    // Convocatoria
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento newConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    newConvocatoriaDocumento.setId(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaDocumento
        () -> service.create(newConvocatoriaDocumento))
        // then: throw exception as ModeloTipoFase not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no disponible para el ModeloEjecucion '%s'",
            newConvocatoriaDocumento.getTipoFase().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void create_WithModeloTipoFaseDisabled_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaDocumento with TipoFase assigned to disabled
    // ModeloEjecucion
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento newConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    newConvocatoriaDocumento.setId(null);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(newConvocatoriaDocumento, convocatoria);
    modeloTipoFase.setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoFase));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaDocumento
        () -> service.create(newConvocatoriaDocumento))
        // then: throw exception as ModeloTipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoFase '%s' no está activo para el ModeloEjecucion '%s'",
            modeloTipoFase.getTipoFase().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void create_WithTipoFaseDisabled_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaDocumento with TipoFase disabled assigned to
    // ModeloEjecucion Convocatoria
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento newConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    newConvocatoriaDocumento.setId(null);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(newConvocatoriaDocumento, convocatoria);
    modeloTipoFase.getTipoFase().setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoFase));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaDocumento
        () -> service.create(newConvocatoriaDocumento))
        // then: throw exception as TipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no está activo", modeloTipoFase.getTipoFase().getNombre());
  }

  @Test
  public void create_WithNoExistingModeloTipoDocumento_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaDocumento with TipoDocumento not assigned to
    // ModeloEjecucion Convocatoria
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento newConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    newConvocatoriaDocumento.setId(null);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(newConvocatoriaDocumento, convocatoria);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaDocumento
        () -> service.create(newConvocatoriaDocumento))
        // then: throw exception as TipoDocumento not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoDocumento '%s' no disponible para el ModeloEjecucion '%s' y TipoFase '%s'",
            newConvocatoriaDocumento.getTipoDocumento().getNombre(), convocatoria.getModeloEjecucion().getNombre(),
            modeloTipoFase.getTipoFase().getNombre());
  }

  @Test
  public void create_WithModeloTipoDocumentoDisabled_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaDocumento with TipoDocumento assigned to disabled
    // ModeloEjecucion
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento newConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    newConvocatoriaDocumento.setId(null);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(newConvocatoriaDocumento, convocatoria);
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(newConvocatoriaDocumento, convocatoria);
    modeloTipoDocumento.setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaDocumento
        () -> service.create(newConvocatoriaDocumento))
        // then: throw exception as ModeloTipoDocumento is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoDocumento '%s' no está activo para el ModeloEjecucion '%s'",
            modeloTipoDocumento.getTipoDocumento().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void create_WithTipoDocumentoDisabled_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaDocumento with TipoDocumento disabled assigned to
    // ModeloEjecucion Convocatoria
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento newConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    newConvocatoriaDocumento.setId(null);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(newConvocatoriaDocumento, convocatoria);
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(newConvocatoriaDocumento, convocatoria);
    modeloTipoDocumento.getTipoDocumento().setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaDocumento
        () -> service.create(newConvocatoriaDocumento))
        // then: throw exception as TipoDocumento is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoDocumento '%s' no está activo", modeloTipoDocumento.getTipoDocumento().getNombre());
  }

  @Test
  public void update_ReturnsConvocatoriaDocumento() {
    // given: updated ConvocatoriaDocumento
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento originalConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    updatedConvocatoriaDocumento.setNombre("nombre-modificado");
    updatedConvocatoriaDocumento.setObservaciones("observaciones-modificadas");
    updatedConvocatoriaDocumento.setDocumentoRef("documentoRef-modificado");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(updatedConvocatoriaDocumento, convocatoria);
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(updatedConvocatoriaDocumento,
        convocatoria);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalConvocatoriaDocumento));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaDocumento>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Update
    ConvocatoriaDocumento updated = service.update(updatedConvocatoriaDocumento);

    // then: ConvocatoriaDocumento is updated
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(originalConvocatoriaDocumento.getId());
    Assertions.assertThat(updated.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(originalConvocatoriaDocumento.getConvocatoriaId());
    Assertions.assertThat(updated.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(originalConvocatoriaDocumento.getTipoFase().getId());
    Assertions.assertThat(originalConvocatoriaDocumento.getTipoDocumento().getId()).as("getTipoDocumento()")
        .isEqualTo(originalConvocatoriaDocumento.getTipoDocumento().getId());
    Assertions.assertThat(updated.getNombre()).as("getNombre()").isEqualTo(updatedConvocatoriaDocumento.getNombre());
    Assertions.assertThat(updated.getPublico()).as("getPublico()")
        .isEqualTo(originalConvocatoriaDocumento.getPublico());
    Assertions.assertThat(updated.getObservaciones()).as("getObservaciones()")
        .isEqualTo(updatedConvocatoriaDocumento.getObservaciones());
    Assertions.assertThat(updated.getDocumentoRef()).as("getDocumentoRef()")
        .isEqualTo(updatedConvocatoriaDocumento.getDocumentoRef());
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: a updated ConvocatoriaDocumento with id filled
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, 1L, 1L, 1L);
    updatedConvocatoriaDocumento.setId(null);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConvocatoriaDocumento id no puede ser null para actualizar un ConvocatoriaDocumento");
  }

  @Test
  public void update_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: a updated ConvocatoriaDocumento without Convocatoria
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, 1L, 1L, 1L);
    updatedConvocatoriaDocumento.setConvocatoriaId(null);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: throw exception as Convocatoria is not provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Convocatoria no puede ser null en ConvocatoriaDocumento");
  }

  @Test
  public void update_WithoutNombre_ThrowsIllegalArgumentException() {
    // given: a updated ConvocatoriaDocumento without Nombre
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, 1L, 1L, 1L);
    updatedConvocatoriaDocumento.setNombre(null);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: throw exception as Publico is not provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Es necesario indicar el nombre del documento");
  }

  @Test
  public void update_WithoutPublico_ThrowsIllegalArgumentException() {
    // given: a updated ConvocatoriaDocumento without Publico
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, 1L, 1L, 1L);
    updatedConvocatoriaDocumento.setPublico(null);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: throw exception as Publico is not provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Es necesario indicar si el documento es público");
  }

  @Test
  public void update_WithoutDocumentoRef_ThrowsIllegalArgumentException() {
    // given: a updated ConvocatoriaDocumento without DocumentoRef
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, 1L, 1L, 1L);
    updatedConvocatoriaDocumento.setDocumentoRef(null);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: throw exception as DocumentoRef is not provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Es necesario indicar la referencia al documento");
  }

  @Test
  public void update_WithNoExistingId_ThrowsNotFoundException() {
    // given: a updated ConvocatoriaDocumento with no existing Id
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, 1L, 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: throw exception as ID is not found
        .isInstanceOf(ConvocatoriaDocumentoNotFoundException.class);
  }

  @Test
  public void update_WithNoExistingConvocatoria_ThrowsNotFoundException() {
    // given: a updated ConvocatoriaDocumento with no existing Convocatoria
    ConvocatoriaDocumento originalConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, 1L, 1L, 1L);
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, 1L, 1L, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalConvocatoriaDocumento));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: throw exception as ID is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  public void update_WithoutTipoFase_DoesNotThrowAnyException() {
    // given: a ConvocatoriaDocumento without TipoFase
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento originalConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    updatedConvocatoriaDocumento.setObservaciones("observaciones-modificadas");
    updatedConvocatoriaDocumento.setDocumentoRef("documentoRef-modificado");
    updatedConvocatoriaDocumento.setTipoFase(null);
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(updatedConvocatoriaDocumento,
        convocatoria);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalConvocatoriaDocumento));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaDocumento>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    Assertions.assertThatCode(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: no exception thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void update_WithNoExistingModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaDocumento with TipoFase not assigned to ModeloEjecucion
    // Convocatoria
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento originalConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    updatedConvocatoriaDocumento.setObservaciones("observaciones-modificadas");
    updatedConvocatoriaDocumento.setDocumentoRef("documentoRef-modificado");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalConvocatoriaDocumento));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: throw exception as ModeloTipoFase not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no disponible para el ModeloEjecucion '%s'",
            updatedConvocatoriaDocumento.getTipoFase().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void update_TipoFaseWithSameTipoFaseAndModeloTipoFaseDisabled_DoesNotThrowAnyException() {
    // given: a ConvocatoriaDocumento with the same TipoFase assigned to disabled
    // ModeloEjecucion
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento originalConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    updatedConvocatoriaDocumento.setObservaciones("observaciones-modificadas");
    updatedConvocatoriaDocumento.setDocumentoRef("documentoRef-modificado");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(updatedConvocatoriaDocumento, convocatoria);
    modeloTipoFase.setActivo(Boolean.FALSE);
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(updatedConvocatoriaDocumento,
        convocatoria);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalConvocatoriaDocumento));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaDocumento>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    Assertions.assertThatCode(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: no exception thrown as ModeloTipoFase is the same
        .doesNotThrowAnyException();
  }

  @Test
  public void update_TipoFaseWithSameTipoFaseDisabled_DoesNotThrowAnyException() {
    // given: a ConvocatoriaDocumento with the same TipoFase disabled assigned to
    // ModeloEjecucion Convocatoria
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento originalConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    updatedConvocatoriaDocumento.setObservaciones("observaciones-modificadas");
    updatedConvocatoriaDocumento.setDocumentoRef("documentoRef-modificado");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(updatedConvocatoriaDocumento, convocatoria);
    modeloTipoFase.getTipoFase().setActivo(Boolean.FALSE);
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(updatedConvocatoriaDocumento,
        convocatoria);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalConvocatoriaDocumento));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaDocumento>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    Assertions.assertThatCode(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: no exceptions thrown as TipoFase is the same
        .doesNotThrowAnyException();
  }

  @Test
  public void update_TipoFaseWithModeloTipoFaseDisabled_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaDocumento with updated TipoFase assigned to disabled
    // ModeloEjecucion
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento originalConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    updatedConvocatoriaDocumento.setTipoFase(generarMockTipoFase(2L));
    updatedConvocatoriaDocumento.setObservaciones("observaciones-modificadas");
    updatedConvocatoriaDocumento.setDocumentoRef("documentoRef-modificado");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(updatedConvocatoriaDocumento, convocatoria);
    modeloTipoFase.setActivo(Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalConvocatoriaDocumento));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoFase));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: throw exception as ModeloTipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoFase '%s' no está activo para el ModeloEjecucion '%s'",
            modeloTipoFase.getTipoFase().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void update_TipoFaseWithTipoFaseDisabled_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaDocumento with updated TipoFase disabled assigned to
    // ModeloEjecucion Convocatoria
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento originalConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    updatedConvocatoriaDocumento.setTipoFase(generarMockTipoFase(2L));
    updatedConvocatoriaDocumento.setObservaciones("observaciones-modificadas");
    updatedConvocatoriaDocumento.setDocumentoRef("documentoRef-modificado");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(updatedConvocatoriaDocumento, convocatoria);
    modeloTipoFase.getTipoFase().setActivo(Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalConvocatoriaDocumento));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoFase));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: throw exception as TipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no está activo", modeloTipoFase.getTipoFase().getNombre());
  }

  @Test
  public void update_WithNoExistingModeloTipoDocumento_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaDocumento with Documento not assigned to ModeloEjecucion
    // Convocatoria
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento originalConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    updatedConvocatoriaDocumento.setObservaciones("observaciones-modificadas");
    updatedConvocatoriaDocumento.setDocumentoRef("documentoRef-modificado");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(updatedConvocatoriaDocumento, convocatoria);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalConvocatoriaDocumento));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: throw exception as ModeloTipoDocumento not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoDocumento '%s' no disponible para el ModeloEjecucion '%s' y TipoFase '%s'",
            updatedConvocatoriaDocumento.getTipoDocumento().getNombre(), convocatoria.getModeloEjecucion().getNombre(),
            modeloTipoFase.getTipoFase().getNombre());
  }

  @Test
  public void update_TipoDocumentoWithSameTipoDocumentoAndModeloTipoFaseDisabled_DoesNotThrowAnyException() {
    // given: a ConvocatoriaDocumento with the same TipoDocumento assigned to
    // disabled
    // ModeloEjecucion
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento originalConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    updatedConvocatoriaDocumento.setObservaciones("observaciones-modificadas");
    updatedConvocatoriaDocumento.setDocumentoRef("documentoRef-modificado");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(updatedConvocatoriaDocumento, convocatoria);
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(updatedConvocatoriaDocumento,
        convocatoria);
    modeloTipoDocumento.setActivo(Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalConvocatoriaDocumento));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaDocumento>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    Assertions.assertThatCode(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: no exception thrown as ModeloTipoDocumento is the same
        .doesNotThrowAnyException();
  }

  @Test
  public void update_TipoDocumentoWithSameTipoDocumentoDisabled_DoesNotThrowAnyException() {
    // given: a ConvocatoriaDocumento with the same Documento disabled assigned to
    // ModeloEjecucion Convocatoria
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento originalConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    updatedConvocatoriaDocumento.setObservaciones("observaciones-modificadas");
    updatedConvocatoriaDocumento.setDocumentoRef("documentoRef-modificado");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(updatedConvocatoriaDocumento, convocatoria);
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(updatedConvocatoriaDocumento,
        convocatoria);
    modeloTipoDocumento.getTipoDocumento().setActivo(Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalConvocatoriaDocumento));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConvocatoriaDocumento>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    Assertions.assertThatCode(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: no exceptions thrown as TipoDocumento is the same
        .doesNotThrowAnyException();
  }

  @Test
  public void update_TipoDocumentoWithModeloTipoDocumentoDisabled_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaDocumento with updated TipoDocumento assigned to
    // disabled ModeloEjecucion
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento originalConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    updatedConvocatoriaDocumento.setTipoDocumento(generarMockTipoDocumento(2L));
    updatedConvocatoriaDocumento.setObservaciones("observaciones-modificadas");
    updatedConvocatoriaDocumento.setDocumentoRef("documentoRef-modificado");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(updatedConvocatoriaDocumento, convocatoria);
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(updatedConvocatoriaDocumento,
        convocatoria);
    modeloTipoDocumento.setActivo(Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalConvocatoriaDocumento));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: throw exception as ModeloTipoDocumento is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoDocumento '%s' no está activo para el ModeloEjecucion '%s'",
            modeloTipoDocumento.getTipoDocumento().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void update_TipoDocumentoWithTipoDocumentoDisabled_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaDocumento with updated TipoDocumento disabled assigned
    // to ModeloEjecucion Convocatoria
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaDocumento originalConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    ConvocatoriaDocumento updatedConvocatoriaDocumento = generarMockConvocatoriaDocumento(1L, convocatoriaId, 1L, 1L);
    updatedConvocatoriaDocumento.setTipoDocumento(generarMockTipoDocumento(2L));
    updatedConvocatoriaDocumento.setObservaciones("observaciones-modificadas");
    updatedConvocatoriaDocumento.setDocumentoRef("documentoRef-modificado");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(updatedConvocatoriaDocumento, convocatoria);
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(updatedConvocatoriaDocumento,
        convocatoria);
    modeloTipoDocumento.getTipoDocumento().setActivo(Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalConvocatoriaDocumento));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(convocatoria));

    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<Long>any())).willReturn(Optional.of(modeloTipoFase));

    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaDocumento
        () -> service.update(updatedConvocatoriaDocumento))
        // then: throw exception as TipoDocumento is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoDocumento '%s' no está activo", modeloTipoDocumento.getTipoDocumento().getNombre());
  }

  @Test
  public void delete_WithExistingId_DoesNotThrowAnyException() {
    // given: existing convocatoriaDocumento
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.<Long>any());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() throws Exception {
    // given: no id
    Long id = null;

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: IllegalArgumentException is thrown
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaDocumentoNotFoundException.class);
  }

  @Test
  public void findById_ReturnsConvocatoriaDocumento() {
    // given: existing ConvocatoriaDocumento
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockConvocatoriaDocumento(idBuscado, 1L, 1L, 1L)));

    // when: find ConvocatoriaDocumento by id
    ConvocatoriaDocumento convocatoriaDocumento = service.findById(idBuscado);

    // then: ConvocatoriaDocumento is found
    Assertions.assertThat(convocatoriaDocumento).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaDocumento.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(convocatoriaDocumento.getConvocatoriaId()).as("getConvocatoriaId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaDocumento.getTipoFase().getId()).as("getTipoFase().getId()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaDocumento.getTipoDocumento().getId()).as("getTipoDocumento()").isEqualTo(1L);
    Assertions.assertThat(convocatoriaDocumento.getNombre()).as("getNombre()").isEqualTo("nombre doc-" + idBuscado);
    Assertions.assertThat(convocatoriaDocumento.getPublico()).as("getPublico()").isEqualTo(Boolean.TRUE);
    Assertions.assertThat(convocatoriaDocumento.getObservaciones()).as("getObservaciones()")
        .isEqualTo("observaciones-" + idBuscado);
    Assertions.assertThat(convocatoriaDocumento.getDocumentoRef()).as("getDocumentoRef()")
        .isEqualTo("documentoRef-" + idBuscado);
  }

  @Test
  public void findAllByConvocatoria_ReturnsPage() {
    // given: Una lista con 37 ConvocatoriaDocumento para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaDocumento> convocatoriasEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesConvocantes.add(generarMockConvocatoriaDocumento(i, 1L, 1L, 1L));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaDocumento>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > convocatoriasEntidadesConvocantes.size() ? convocatoriasEntidadesConvocantes.size()
              : toIndex;
          List<ConvocatoriaDocumento> content = convocatoriasEntidadesConvocantes.subList(fromIndex, toIndex);
          Page<ConvocatoriaDocumento> pageResponse = new PageImpl<>(content, pageable,
              convocatoriasEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConvocatoriaDocumento> page = service.findAllByConvocatoria(convocatoriaId, null, paging);

    // then: Devuelve la pagina 3 con los ConvocatoriaDocumento del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConvocatoriaDocumento ConvocatoriaDocumento = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(ConvocatoriaDocumento.getId()).isEqualTo(Long.valueOf(i));
    }
  }

  @Test
  public void findById_WithIdNotExist_ThrowsConvocatoriaDocumentoNotFoundException() throws Exception {
    // given: no existing ConvocatoriaDocumento
    Long idBuscado = 1L;

    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing Id
        () -> service.findById(idBuscado))
        //// then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaDocumentoNotFoundException.class);
  }

  /**
   * Función que genera Convocatoria
   * 
   * @param convocatoriaId
   * @param unidadGestionId
   * @param modeloEjecucionId
   * @param modeloTipoFinalidadId
   * @param tipoRegimenConcurrenciaId
   * @param tipoAmbitoGeogragicoId
   * @param activo
   * @return la convocatoria
   */
  private Convocatoria generarMockConvocatoria(Long convocatoriaId, Long unidadGestionId, Long modeloEjecucionId,
      Long modeloTipoFinalidadId, Long tipoRegimenConcurrenciaId, Long tipoAmbitoGeogragicoId, Boolean activo) {

    // @formatter:off
    ModeloEjecucion modeloEjecucion = (modeloEjecucionId == null) ? null
        : ModeloEjecucion.builder()
            .id(modeloEjecucionId)
            .nombre("nombreModeloEjecucion-" + String.format("%03d", modeloEjecucionId))
            .activo(Boolean.TRUE)
            .build();

    TipoFinalidad tipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : TipoFinalidad.builder()
            .id(modeloTipoFinalidadId)
            .nombre("nombreTipoFinalidad-" + String.format("%03d", modeloTipoFinalidadId))
            .activo(Boolean.TRUE)
            .build();

    ModeloTipoFinalidad modeloTipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : ModeloTipoFinalidad.builder()
            .id(modeloTipoFinalidadId)
            .modeloEjecucion(modeloEjecucion)
            .tipoFinalidad(tipoFinalidad)
            .activo(Boolean.TRUE)
            .build();

    TipoRegimenConcurrencia tipoRegimenConcurrencia = (tipoRegimenConcurrenciaId == null) ? null
        : TipoRegimenConcurrencia.builder()
            .id(tipoRegimenConcurrenciaId)
            .nombre("nombreTipoRegimenConcurrencia-" + String.format("%03d", tipoRegimenConcurrenciaId))
            .activo(Boolean.TRUE)
            .build();

    TipoAmbitoGeografico tipoAmbitoGeografico = (tipoAmbitoGeogragicoId == null) ? null
        : TipoAmbitoGeografico.builder()
            .id(tipoAmbitoGeogragicoId)
            .nombre("nombreTipoAmbitoGeografico-" + String.format("%03d", tipoAmbitoGeogragicoId))
            .activo(Boolean.TRUE)
            .build();

    Convocatoria convocatoria = Convocatoria.builder()
        .id(convocatoriaId)
        .unidadGestionRef((unidadGestionId == null) ? null : "unidad-" + String.format("%03d", unidadGestionId))
        .modeloEjecucion(modeloEjecucion)
        .codigo("codigo-" + String.format("%03d", convocatoriaId))
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaProvisional(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaConcesion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo("titulo-" + String.format("%03d", convocatoriaId))
        .objeto("objeto-" + String.format("%03d", convocatoriaId))
        .observaciones("observaciones-" + String.format("%03d", convocatoriaId))
        .finalidad((modeloTipoFinalidad == null) ? null : modeloTipoFinalidad.getTipoFinalidad())
        .regimenConcurrencia(tipoRegimenConcurrencia)
        .colaborativos(Boolean.TRUE)
        .estado(Convocatoria.Estado.REGISTRADA)
        .duracion(12)
        .ambitoGeografico(tipoAmbitoGeografico)
        .clasificacionCVN(ClasificacionCVN.AYUDAS)
        .activo(activo)
        .build();
    // @formatter:on

    return convocatoria;
  }

  /**
   * Función que devuelve un objeto ModeloTipoFase a partir de un objeto
   * ConvocatoriaDocumento
   * 
   * @param convocatoriaDocumento
   * @return el objeto ModeloTipoFase
   */
  private ModeloTipoFase generarMockModeloTipoFase(ConvocatoriaDocumento convocatoriaDocumento,
      Convocatoria convocatoria) {

    // @formatter:off
    return ModeloTipoFase.builder()
        .id(convocatoriaDocumento.getId() == null ? 1L : convocatoriaDocumento.getId())
        .modeloEjecucion(convocatoria.getModeloEjecucion())
        .tipoFase(convocatoriaDocumento.getTipoFase())
        .solicitud(Boolean.TRUE)
        .convocatoria(Boolean.TRUE)
        .proyecto(Boolean.TRUE)
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto ModeloTipoDocumento a partir de un objeto
   * ConvocatoriaDocumento
   * 
   * @param convocatoriaDocumento
   * @return el objeto ModeloTipoFase
   */
  private ModeloTipoDocumento generarMockModeloTipoDocumento(ConvocatoriaDocumento convocatoriaDocumento,
      Convocatoria convocatoria) {

    // @formatter:off
    return ModeloTipoDocumento.builder()
        .id(convocatoriaDocumento.getId() == null ? 1L : convocatoriaDocumento.getId())
        .modeloEjecucion(convocatoria.getModeloEjecucion())
        .modeloTipoFase(generarMockModeloTipoFase(convocatoriaDocumento, convocatoria))
        .tipoDocumento(convocatoriaDocumento.getTipoDocumento())
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id) {

    // @formatter:off
    return TipoDocumento.builder()
        .id(id)
        .nombre("nombreTipoDocumento-" + id)
        .descripcion("descripcionTipoDocumento-" + id)
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto TipoFase
   * 
   * @param id id del TipoFase
   * @return el objeto TipoFase
   */
  private TipoFase generarMockTipoFase(Long id) {

    // @formatter:off
    return TipoFase.builder()
        .id(id)
        .nombre("nombreTipoFase-" + id)
        .descripcion("descripcionTipoFase-" + id)
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on
  }

  private Convocatoria generarMockConvocatoria(Long convocatoriaId) {
    return generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto ConvocatoriaDocumento
   * 
   * @param id              id del ConvocatoriaDocumento
   * @param convocatoriaId
   * @param tipoFaseId
   * @param tipoDocumentoId
   * @return el objeto ConvocatoriaDocumento
   */
  private ConvocatoriaDocumento generarMockConvocatoriaDocumento(Long id, Long convocatoriaId, Long tipoFaseId,
      Long tipoDocumentoId) {

    TipoFase tipoFase = generarMockTipoFase(tipoFaseId);
    TipoDocumento tipoDocumento = generarMockTipoDocumento(tipoDocumentoId);

    // @formatter:off
    return ConvocatoriaDocumento.builder()
        .id(id)
        .convocatoriaId(convocatoriaId)
        .tipoFase(tipoFase)
        .tipoDocumento(tipoDocumento)
        .nombre("nombre doc-" + id)
        .publico(Boolean.TRUE)
        .observaciones("observaciones-" + id)
        .documentoRef("documentoRef-" + id)
        .build();
    // @formatter:on
  }

}
