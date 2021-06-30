package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.DocumentoRequeridoSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.DocumentoRequeridoSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.service.impl.DocumentoRequeridoSolicitudServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class DocumentoRequeridoSolicitudServiceTest extends BaseServiceTest {

  @Mock
  private DocumentoRequeridoSolicitudRepository documentoRequeridoSolicitudRepository;
  @Mock
  private ConfiguracionSolicitudRepository configuracionSolicitudRepository;
  @Mock
  private ModeloTipoFaseRepository modeloTipoFaseRepository;
  @Mock
  private ModeloTipoDocumentoRepository modeloTipoDocumentoRepository;
  @Mock
  private ConvocatoriaService convocatoriaService;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;

  private DocumentoRequeridoSolicitudService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new DocumentoRequeridoSolicitudServiceImpl(documentoRequeridoSolicitudRepository,
        configuracionSolicitudRepository, modeloTipoFaseRepository, modeloTipoDocumentoRepository, convocatoriaService,
        convocatoriaRepository);
  }

  @Test
  public void create_ReturnsDocumentoRequeridoSolicitud() {
    // given: new DocumentoRequeridoSolicitud
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);

    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    newDocumentoRequeridoSolicitud.setId(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional
            .of(generarMockModeloTipoFase(convocatoria, configuracionSolicitud.getFasePresentacionSolicitudes())));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional
            .of(generarMockModeloTipoDocumento(newDocumentoRequeridoSolicitud, convocatoria, configuracionSolicitud)));
    BDDMockito.given(documentoRequeridoSolicitudRepository.save(ArgumentMatchers.<DocumentoRequeridoSolicitud>any()))
        .willAnswer(new Answer<DocumentoRequeridoSolicitud>() {
          @Override
          public DocumentoRequeridoSolicitud answer(InvocationOnMock invocation) throws Throwable {
            DocumentoRequeridoSolicitud givenData = invocation.getArgument(0, DocumentoRequeridoSolicitud.class);
            DocumentoRequeridoSolicitud newData = new DocumentoRequeridoSolicitud();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create DocumentoRequeridoSolicitud
    DocumentoRequeridoSolicitud createdDocumentoRequeridoSolicitud = service.create(newDocumentoRequeridoSolicitud);

    // then: new DocumentoRequeridoSolicitud is created
    Assertions.assertThat(createdDocumentoRequeridoSolicitud).isNotNull();
    Assertions.assertThat(createdDocumentoRequeridoSolicitud.getId()).isNotNull();
    Assertions.assertThat(createdDocumentoRequeridoSolicitud.getConfiguracionSolicitudId())
        .isEqualTo(newDocumentoRequeridoSolicitud.getConfiguracionSolicitudId());
    Assertions.assertThat(createdDocumentoRequeridoSolicitud.getTipoDocumento().getId())
        .isEqualTo(newDocumentoRequeridoSolicitud.getTipoDocumento().getId());
    Assertions.assertThat(createdDocumentoRequeridoSolicitud.getObservaciones())
        .isEqualTo(newDocumentoRequeridoSolicitud.getObservaciones());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with id filled
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear DocumentoRequeridoSolicitud");
  }

  @Test
  public void create_WithoutConfiguracionId_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud without ConfigracionId
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, null);
    newDocumentoRequeridoSolicitud.setId(null);

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as ConfigracionId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConfiguracionSolicitud no puede ser null en la DocumentoRequeridoSolicitud");
  }

  @Test
  public void create_WithoutTipoDocumento_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud without TipoDocumento
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    newDocumentoRequeridoSolicitud.setId(null);
    newDocumentoRequeridoSolicitud.setTipoDocumento(null);

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as TipoDocumento is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoDocumento no puede ser null en la DocumentoRequeridoSolicitud");
  }

  @Test
  public void create_WithNoExistingConfiguracionSolicitud_ThrowsNotFoundException() {
    // given: a DocumentoRequeridoSolicitud without No existing
    // ConfiguracionSolicitud
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    newDocumentoRequeridoSolicitud.setId(null);

    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as ConfiguracionSolicitud is not found
        .isInstanceOf(ConfiguracionSolicitudNotFoundException.class);
  }

  @Test
  public void create_WithoutFasePresentacionSolicitudes_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud without FasePresentacionSolicitudes
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    newDocumentoRequeridoSolicitud.setId(null);
    configuracionSolicitud.setFasePresentacionSolicitudes(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as FasePresentacionSolicitudes is null
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "Solo se pueden añadir documentos asociados a la Fase del plazo de presentación de solicitudes en la configuración de la convocatoria");
  }

  @Test
  public void create_WithNoExistingModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with FasePresentacionSolicitudes
    // TipoFase not assigned to ModeloEjecucion Convocatoria
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);

    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    newDocumentoRequeridoSolicitud.setId(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as ModeloTipoFase not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no disponible para el ModeloEjecucion '%s'",
            configuracionSolicitud.getFasePresentacionSolicitudes().getTipoFase().getNombre(),
            convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void create_WithModeloTipoFaseDisabled_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with FasePresentacionSolicitudes
    // TipoFase assigned to disabled ModeloEjecucion
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    newDocumentoRequeridoSolicitud.setId(null);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(convocatoria,
        configuracionSolicitud.getFasePresentacionSolicitudes());
    modeloTipoFase.setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as ModeloTipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoFase '%s' no está activo para el ModeloEjecucion '%s'",
            modeloTipoFase.getTipoFase().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void create_WithTipoFaseDisabled_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with FasePresentacionSolicitudes
    // TipoFase disabled assigned to ModeloEjecucion Convocatoria
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    newDocumentoRequeridoSolicitud.setId(null);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(convocatoria,
        configuracionSolicitud.getFasePresentacionSolicitudes());
    modeloTipoFase.getTipoFase().setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as TipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no está activo", modeloTipoFase.getTipoFase().getNombre());
  }

  @Test
  public void create_WithNoExistingModeloTipoDocumento_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with TipoDocumento nos assigned to
    // ModeloEjecucion or ModeloTipoFase
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    newDocumentoRequeridoSolicitud.setId(null);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(convocatoria,
        configuracionSolicitud.getFasePresentacionSolicitudes());

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as ModeloTipoDocumento not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoDocumento '%s' no disponible para el ModeloEjecucion '%s' y TipoFase '%s'",
            newDocumentoRequeridoSolicitud.getTipoDocumento().getNombre(),
            convocatoria.getModeloEjecucion().getNombre(), modeloTipoFase.getTipoFase().getNombre());
  }

  @Test
  public void create_WithModeloTipoDocumentoDisabled_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with TipoDocumento assigned to disabled
    // ModeloEjecucion
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    newDocumentoRequeridoSolicitud.setId(null);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(convocatoria,
        configuracionSolicitud.getFasePresentacionSolicitudes());
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(newDocumentoRequeridoSolicitud,
        convocatoria, configuracionSolicitud);
    modeloTipoDocumento.setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento));

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as ModeloTipoDocumento is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoDocumento '%s' no está activo para el ModeloEjecucion '%s'",
            modeloTipoDocumento.getTipoDocumento().getNombre(), convocatoria.getModeloEjecucion().getNombre());

  }

  @Test
  public void create_WithTipoDocumentoDisabled_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with TipoDocumento disabled assigned to
    // ModeloEjecucion
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    newDocumentoRequeridoSolicitud.setId(null);
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(convocatoria,
        configuracionSolicitud.getFasePresentacionSolicitudes());
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(newDocumentoRequeridoSolicitud,
        convocatoria, configuracionSolicitud);
    modeloTipoDocumento.getTipoDocumento().setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento));

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as TipoDocumento is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoDocumento '%s' no está activo", modeloTipoDocumento.getTipoDocumento().getNombre());
  }

  @Test
  public void create_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud when modificable returns False
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud newDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    newDocumentoRequeridoSolicitud.setId(null);

    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: create DocumentoRequeridoSolicitud
        () -> service.create(newDocumentoRequeridoSolicitud))
        // then: throw exception as Convocatoria is not modificable
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede crear DocumentoRequeridoSolicitud. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
  }

  @Test
  public void update_ReturnsDocumentoRequeridoSolicitud() {
    // given: update DocumentoRequeridoSolicitud
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");
    updatedDocumentoRequeridoSolicitud.setTipoDocumento(generarMockTipoDocumento(2L));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.anyLong()))
        .willReturn(Optional
            .of(generarMockModeloTipoFase(convocatoria, configuracionSolicitud.getFasePresentacionSolicitudes())));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(
            generarMockModeloTipoDocumento(updatedDocumentoRequeridoSolicitud, convocatoria, configuracionSolicitud)));
    BDDMockito.given(documentoRequeridoSolicitudRepository.save(ArgumentMatchers.<DocumentoRequeridoSolicitud>any()))
        .willReturn(updatedDocumentoRequeridoSolicitud);

    // when: update DocumentoRequeridoSolicitud
    DocumentoRequeridoSolicitud updated = service.update(updatedDocumentoRequeridoSolicitud);

    // then: DocumentoRequeridoSolicitud is updated
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isNotNull();
    Assertions.assertThat(updated.getConfiguracionSolicitudId())
        .isEqualTo(originalDocumentoRequeridoSolicitud.getConfiguracionSolicitudId());
    Assertions.assertThat(updated.getTipoDocumento().getId())
        .isEqualTo(updatedDocumentoRequeridoSolicitud.getTipoDocumento().getId());
    Assertions.assertThat(updated.getObservaciones()).isEqualTo(updatedDocumentoRequeridoSolicitud.getObservaciones());
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: a updated DocumentoRequeridoSolicitud with id filled
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    updatedDocumentoRequeridoSolicitud.setId(null);

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("DocumentoRequeridoSolicitud id no puede ser null para actualizar un DocumentoRequeridoSolicitud");
  }

  @Test
  public void update_WithNoExistingDocumentoRequeridoSolicitud_ThrowsNotFoundException() {
    // given: a DocumentoRequeridoSolicitud with no existing Id
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as id is not found
        .isInstanceOf(DocumentoRequeridoSolicitudNotFoundException.class);
  }

  @Test
  public void update_WithoutConfiguracionId_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud without ConfigracionId
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, null);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as ConfigracionId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConfiguracionSolicitud no puede ser null en la DocumentoRequeridoSolicitud");
  }

  @Test
  public void update_WithoutTipoDocumento_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud without TipoDocumento
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    updatedDocumentoRequeridoSolicitud.setTipoDocumento(null);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as TipoDocumento is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoDocumento no puede ser null en la DocumentoRequeridoSolicitud");
  }

  @Test
  public void update_WithNoExistingConfiguracionSolicitud_ThrowsNotFoundException() {
    // given: a DocumentoRequeridoSolicitud with No existing ConfiguracionSolicitud
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as ConfiguracionSolicitud is not found
        .isInstanceOf(ConfiguracionSolicitudNotFoundException.class);
  }

  @Test
  public void update_WithoutFasePresentacionSolicitudes_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud without FasePresentacionSolicitudes
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    configuracionSolicitud.setFasePresentacionSolicitudes(null);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as FasePresentacionSolicitudes is null
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "Solo se pueden añadir documentos asociados a la Fase del plazo de presentación de solicitudes en la configuración de la convocatoria");
  }

  @Test
  public void update_WithNoExistingModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with FasePresentacionSolicitudes
    // TipoFase not assigned to ModeloEjecucion Convocatoria
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as ModeloTipoFase not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no disponible para el ModeloEjecucion '%s'",
            configuracionSolicitud.getFasePresentacionSolicitudes().getTipoFase().getNombre(),
            convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void update_WithModeloTipoFaseDisabled_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with FasePresentacionSolicitudes
    // TipoFase assigned to disabled ModeloEjecucion
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(convocatoria,
        configuracionSolicitud.getFasePresentacionSolicitudes());
    modeloTipoFase.setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as ModeloTipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoFase '%s' no está activo para el ModeloEjecucion '%s'",
            modeloTipoFase.getTipoFase().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void update_WithTipoFaseDisabled_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with FasePresentacionSolicitudes
    // TipoFase disabled assigned to ModeloEjecucion Convocatoria
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(convocatoria,
        configuracionSolicitud.getFasePresentacionSolicitudes());
    modeloTipoFase.getTipoFase().setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as TipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no está activo", modeloTipoFase.getTipoFase().getNombre());
  }

  @Test
  public void update_WithNoExistingModeloTipoDocumento_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with TipoDocumento nos assigned to
    // ModeloEjecucion or ModeloTipoFase
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(convocatoria,
        configuracionSolicitud.getFasePresentacionSolicitudes());

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as ModeloTipoDocumento not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoDocumento '%s' no disponible para el ModeloEjecucion '%s' y TipoFase '%s'",
            updatedDocumentoRequeridoSolicitud.getTipoDocumento().getNombre(),
            convocatoria.getModeloEjecucion().getNombre(), modeloTipoFase.getTipoFase().getNombre());
  }

  @Test
  public void update_TipoDocumentoWithModeloTipoDocumentoDisabled_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with TipoDocumento assigned to disabled
    // ModeloEjecucion
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    updatedDocumentoRequeridoSolicitud.setTipoDocumento(generarMockTipoDocumento(2L));
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(convocatoria,
        configuracionSolicitud.getFasePresentacionSolicitudes());
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(updatedDocumentoRequeridoSolicitud,
        convocatoria, configuracionSolicitud);
    modeloTipoDocumento.setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento));

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as ModeloTipoDocumento is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoDocumento '%s' no está activo para el ModeloEjecucion '%s'",
            modeloTipoDocumento.getTipoDocumento().getNombre(), convocatoria.getModeloEjecucion().getNombre());
  }

  @Test
  public void update_WithModeloTipoDocumentoDisabledAndSameTipoDocumentoId_DoesNotThrowAnyException() {
    // given: a DocumentoRequeridoSolicitud with TipoDocumento assigned to disabled
    // ModeloEjecucion without updating TipoDocumento
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(convocatoria,
        configuracionSolicitud.getFasePresentacionSolicitudes());
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(updatedDocumentoRequeridoSolicitud,
        convocatoria, configuracionSolicitud);
    modeloTipoDocumento.setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento));
    BDDMockito.given(documentoRequeridoSolicitudRepository.save(ArgumentMatchers.<DocumentoRequeridoSolicitud>any()))
        .willReturn(updatedDocumentoRequeridoSolicitud);

    Assertions.assertThatCode(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: no exception thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void update_TipoDocumentoWithTipoDocumentoDisabled_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud with TipoDocumento disabled assigned
    // ModeloEjecucion
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    updatedDocumentoRequeridoSolicitud.setTipoDocumento(generarMockTipoDocumento(2L));
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(convocatoria,
        configuracionSolicitud.getFasePresentacionSolicitudes());
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(updatedDocumentoRequeridoSolicitud,
        convocatoria, configuracionSolicitud);
    modeloTipoDocumento.getTipoDocumento().setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento));

    Assertions.assertThatCode(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: throw exception as TipoDocumento is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoDocumento '%s' no está activo", modeloTipoDocumento.getTipoDocumento().getNombre());
  }

  @Test
  public void update_WithTipoDocumentoDisabledAndSameTipoDocumentoId_DoesNotThrowAnyException() {
    // given: a DocumentoRequeridoSolicitud with TipoDocumento disabled assigned to
    // ModeloEjecucion without updating TipoDocumento
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud originalDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    DocumentoRequeridoSolicitud updatedDocumentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);
    updatedDocumentoRequeridoSolicitud.setObservaciones("observaciones-modificadas");
    ModeloTipoFase modeloTipoFase = generarMockModeloTipoFase(convocatoria,
        configuracionSolicitud.getFasePresentacionSolicitudes());
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(updatedDocumentoRequeridoSolicitud,
        convocatoria, configuracionSolicitud);
    modeloTipoDocumento.getTipoDocumento().setActivo(Boolean.FALSE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalDocumentoRequeridoSolicitud));
    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong())).willReturn(Optional.of(modeloTipoFase));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(modeloTipoDocumento));
    BDDMockito.given(documentoRequeridoSolicitudRepository.save(ArgumentMatchers.<DocumentoRequeridoSolicitud>any()))
        .willReturn(updatedDocumentoRequeridoSolicitud);

    Assertions.assertThatCode(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(updatedDocumentoRequeridoSolicitud))
        // then: no exception thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void update_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: a DocumentoRequeridoSolicitud when modificable return false
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);
    DocumentoRequeridoSolicitud documentoRequeridoSolicitud = generarMockDocumentoRequeridoSolicitud(1L, 1L);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(documentoRequeridoSolicitud));

    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));

    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.anyLong(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: update DocumentoRequeridoSolicitud
        () -> service.update(documentoRequeridoSolicitud))
        // then: throw exception as Convocatoria is not modificable
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede modificar DocumentoRequeridoSolicitud. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
  }

  @Test
  public void delete_WithExistingId_DoesNotThrowAnyException() {
    // given: existing DocumentoRequeridoSolicitud
    Long id = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(1L, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoria, 1L);

    BDDMockito.given(configuracionSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud));
    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockDocumentoRequeridoSolicitud(id, 1L)));
    BDDMockito.given(convocatoriaService.modificable(ArgumentMatchers.<Long>any(), ArgumentMatchers.<String>any(),
        ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(documentoRequeridoSolicitudRepository).deleteById(ArgumentMatchers.anyLong());

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
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("DocumentoRequeridoSolicitud id no puede ser null para eliminar un DocumentoRequeridoSolicitud");
  }

  @Test
  public void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(DocumentoRequeridoSolicitudNotFoundException.class);
  }

  @Test
  public void findByIdConvocatoria_WithExistingId_ReturnsConfiguracionSolicitud() throws Exception {
    // given: existing ConfiguracionSolicitud
    DocumentoRequeridoSolicitud documentoRequeridoSolicitudExistente = generarMockDocumentoRequeridoSolicitud(1L, 1L);

    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(documentoRequeridoSolicitudExistente));

    // when: find ConfiguracionSolicitud Convocatoria
    DocumentoRequeridoSolicitud found = service.findById(documentoRequeridoSolicitudExistente.getId());

    // then: ConfiguracionSolicitud is updated
    Assertions.assertThat(found).isNotNull();
    Assertions.assertThat(found.getId()).isNotNull();
    Assertions.assertThat(found.getId()).as("getId()").isEqualTo(documentoRequeridoSolicitudExistente.getId());
    Assertions.assertThat(found.getConfiguracionSolicitudId()).as("getConfiguracionSolicitudId()")
        .isEqualTo(documentoRequeridoSolicitudExistente.getConfiguracionSolicitudId());
    Assertions.assertThat(found.getTipoDocumento().getId()).as("getTipoDocumento().getId()")
        .isEqualTo(documentoRequeridoSolicitudExistente.getTipoDocumento().getId());
    Assertions.assertThat(found.getObservaciones()).as("getObservaciones()")
        .isEqualTo(documentoRequeridoSolicitudExistente.getObservaciones());
  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing convocatoria
    BDDMockito.given(documentoRequeridoSolicitudRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing convocatoria
        () -> service.findById(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(DocumentoRequeridoSolicitudNotFoundException.class);
  }

  @Test
  public void findAllByConvocatoria_ReturnsPage() {
    // given: Una lista con 37 DocumentoRequeridoSolicitud para la Convocatoria
    Long convocatoriaId = 1L;
    List<DocumentoRequeridoSolicitud> convocatoriasEntidadesGestoras = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesGestoras.add(generarMockDocumentoRequeridoSolicitud(i, 1L));
    }

    BDDMockito
        .given(documentoRequeridoSolicitudRepository.findAll(
            ArgumentMatchers.<Specification<DocumentoRequeridoSolicitud>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<DocumentoRequeridoSolicitud>>() {
          @Override
          public Page<DocumentoRequeridoSolicitud> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > convocatoriasEntidadesGestoras.size() ? convocatoriasEntidadesGestoras.size() : toIndex;
            List<DocumentoRequeridoSolicitud> content = convocatoriasEntidadesGestoras.subList(fromIndex, toIndex);
            Page<DocumentoRequeridoSolicitud> page = new PageImpl<>(content, pageable,
                convocatoriasEntidadesGestoras.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<DocumentoRequeridoSolicitud> page = service.findAllByConvocatoria(convocatoriaId, null, paging);

    // then: Devuelve la pagina 3 con los DocumentoRequeridoSolicitud del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      DocumentoRequeridoSolicitud DocumentoRequeridoSolicitud = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(DocumentoRequeridoSolicitud.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(DocumentoRequeridoSolicitud.getObservaciones())
          .isEqualTo("observacionesDocumentoRequeridoSolicitud-" + i);
    }
  }

  /**
   * Función que devuelve un objeto DocumentoRequeridoSolicitud
   * 
   * @param id id del DocumentoRequeridoSolicitud
   * @return el objeto DocumentoRequeridoSolicitud
   */
  private DocumentoRequeridoSolicitud generarMockDocumentoRequeridoSolicitud(Long id, Long configuracionSolicitudId) {

    // @formatter:off
    return DocumentoRequeridoSolicitud.builder()
        .id(id)
        .configuracionSolicitudId(configuracionSolicitudId)
        .tipoDocumento(generarMockTipoDocumento(id))
        .observaciones("observacionesDocumentoRequeridoSolicitud-" + id)
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto ModeloTipoFase a partir de un objeto
   * ConvocatoriaFase
   * 
   * @param convocatoriaTipoFase
   * @return el objeto ModeloTipoFase
   */
  private ModeloTipoFase generarMockModeloTipoFase(Convocatoria convocatoria, ConvocatoriaFase convocatoriaTipoFase) {

    // @formatter:off
    return ModeloTipoFase.builder()
        .id(convocatoriaTipoFase.getId() == null ? 1L : convocatoriaTipoFase.getId())
        .modeloEjecucion(convocatoria.getModeloEjecucion())
        .tipoFase(convocatoriaTipoFase.getTipoFase())
        .solicitud(Boolean.TRUE)
        .convocatoria(Boolean.TRUE)
        .proyecto(Boolean.TRUE)
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto ModeloTipoDocumento a partir de un objeto
   * DocumentoRequeridoSolicitud
   * 
   * @param documentoRequeridoSolicitud
   * @return el objeto ModeloTipoFase
   */
  private ModeloTipoDocumento generarMockModeloTipoDocumento(DocumentoRequeridoSolicitud documentoRequeridoSolicitud,
      Convocatoria convocatoria, ConfiguracionSolicitud configuracionSolicitud) {

    // @formatter:off
    return ModeloTipoDocumento.builder()
        .id(documentoRequeridoSolicitud.getId() == null ? 1L : documentoRequeridoSolicitud.getId())
        .modeloEjecucion(convocatoria.getModeloEjecucion())
        .modeloTipoFase(generarMockModeloTipoFase(
            convocatoria, configuracionSolicitud.getFasePresentacionSolicitudes()))
        .tipoDocumento(documentoRequeridoSolicitud.getTipoDocumento())
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

  /**
   * Genera un objeto ConfiguracionSolicitud
   * 
   * @param configuracionSolicitudId
   * @param convocatoriaId
   * @param convocatoriaFaseId
   * @return
   */
  private ConfiguracionSolicitud generarMockConfiguracionSolicitud(Long configuracionSolicitudId,
      Convocatoria convocatoria, Long convocatoriaFaseId) {
    TipoFase tipoFase = generarMockTipoFase(1L);

    // @formatter:off
    ConvocatoriaFase convocatoriaFase = ConvocatoriaFase.builder()
        .id(convocatoriaFaseId)
        .convocatoriaId(convocatoria.getId())
        .tipoFase(tipoFase)
        .fechaInicio(Instant.parse("2020-10-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-10-15T00:00:00Z"))
        .observaciones("observaciones")
        .build();

    ConfiguracionSolicitud configuracionSolicitud = ConfiguracionSolicitud.builder()
        .id(configuracionSolicitudId)
        .convocatoriaId(convocatoria.getId())
        .tramitacionSGI(Boolean.TRUE)
        .fasePresentacionSolicitudes(convocatoriaFase)
        .importeMaximoSolicitud(BigDecimal.valueOf(12345))
        .formularioSolicitud(FormularioSolicitud.ESTANDAR)
        .build();
    // @formatter:on

    return configuracionSolicitud;
  }

  private ModeloEjecucion generarMockModeloEjecucion(Long modeloEjecucionId) {
    // @formatter:off
    ModeloEjecucion modeloEjecucion = (modeloEjecucionId == null) ? null
        : ModeloEjecucion.builder()
            .id(modeloEjecucionId)
            .nombre("nombreModeloEjecucion-" + String.format("%03d", modeloEjecucionId))
            .activo(Boolean.TRUE)
            .build();
    // @formatter:on
    return modeloEjecucion;
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
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(modeloEjecucionId);

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
}
