package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaFaseNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria.Estado;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFaseObservaciones;
import org.crue.hercules.sgi.csp.model.ConvocatoriaObjeto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaObservaciones;
import org.crue.hercules.sgi.csp.model.ConvocatoriaTitulo;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitudObservaciones;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeograficoNombre;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumentoDescripcion;
import org.crue.hercules.sgi.csp.model.TipoDocumentoNombre;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFaseNombre;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidadNombre;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrenciaNombre;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaFaseRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.DocumentoRequeridoSolicitudRepository;
import org.crue.hercules.sgi.csp.service.impl.ConfiguracionSolicitudServiceImpl;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

class ConfiguracionSolicitudServiceTest extends BaseServiceTest {

  @Mock
  private ConfiguracionSolicitudRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ConvocatoriaFaseRepository convocatoriaFaseRepository;
  @Mock
  private DocumentoRequeridoSolicitudRepository documentoRequeridoSolicitudRepository;

  private ConfiguracionSolicitudService service;

  @BeforeEach
  void setUp() {
    service = new ConfiguracionSolicitudServiceImpl(repository, convocatoriaRepository, convocatoriaFaseRepository,
        documentoRequeridoSolicitudRepository);
  }

  @Test
  void create_withConvocatoriaRegistrada_ReturnsConfiguracionSolicitud() {
    // given: new ConfiguracionSolicitud
    Long convocatoriaId = 1L;
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(null, convocatoriaId, 1L);
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(convocatoriaFaseRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitud.getFasePresentacionSolicitudes()));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConfiguracionSolicitud>any()))
        .willAnswer(new Answer<ConfiguracionSolicitud>() {
          @Override
          public ConfiguracionSolicitud answer(InvocationOnMock invocation) throws Throwable {
            ConfiguracionSolicitud givenData = invocation.getArgument(0, ConfiguracionSolicitud.class);
            ConfiguracionSolicitud newData = new ConfiguracionSolicitud();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create ConfiguracionSolicitud
    ConfiguracionSolicitud created = service.create(configuracionSolicitud);

    // then: new ConfiguracionSolicitud is created
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isNotNull();
    Assertions.assertThat(created.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(configuracionSolicitud.getConvocatoriaId());
    Assertions.assertThat(created.getTramitacionSGI()).as("getTramitacionSGI()")
        .isEqualTo(configuracionSolicitud.getTramitacionSGI());
    Assertions.assertThat(created.getFasePresentacionSolicitudes().getId())
        .as("getFasePresentacionSolicitudes().getId()")
        .isEqualTo(configuracionSolicitud.getFasePresentacionSolicitudes().getId());
    Assertions.assertThat(created.getImporteMaximoSolicitud()).as("getImporteMaximoSolicitud()")
        .isEqualTo(configuracionSolicitud.getImporteMaximoSolicitud());
  }

  @Test
  void create_WithConvocatoriaBorrador_ReturnsConfiguracionSolicitud() {
    // given: new ConfiguracionSolicitud convocatoria estado borrador
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Convocatoria.Estado.BORRADOR);
    ConfiguracionSolicitud configuracionSolicitud = ConfiguracionSolicitud.builder().convocatoriaId(convocatoriaId)
        .build();

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConfiguracionSolicitud>any()))
        .willAnswer(new Answer<ConfiguracionSolicitud>() {
          @Override
          public ConfiguracionSolicitud answer(InvocationOnMock invocation) throws Throwable {
            ConfiguracionSolicitud givenData = invocation.getArgument(0, ConfiguracionSolicitud.class);
            ConfiguracionSolicitud newData = new ConfiguracionSolicitud();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create ConfiguracionSolicitud
    ConfiguracionSolicitud created = service.create(configuracionSolicitud);

    // then: new ConfiguracionSolicitud is created with minimum required data
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(configuracionSolicitud.getConvocatoriaId());
    Assertions.assertThat(created.getTramitacionSGI()).as("getTramitacionSGI()").isNull();
    Assertions.assertThat(created.getFasePresentacionSolicitudes()).as("getFasePresentacionSolicitudes()").isNull();
    Assertions.assertThat(created.getImporteMaximoSolicitud()).as("getImporteMaximoSolicitud()").isNull();
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: a ConfiguracionSolicitud with id filled
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: create ConfiguracionSolicitud
        () -> service.create(configuracionSolicitud))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Configuración solicitud debe ser nulo");
  }

  @Test
  void create_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: a ConfiguracionSolicitud without convocatoria
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(null, 1L, 1L);
    configuracionSolicitud.setConvocatoriaId(null);

    Assertions.assertThatThrownBy(
        // when: create ConfiguracionSolicitud
        () -> service.create(configuracionSolicitud))
        // then: throw exception as convocatoria can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Convocatoria no puede ser nulo");
  }

  @Test
  void create_WithNoExistingConvocatoria_ThrowsNotFoundException() {
    // given: a ConfiguracionSolicitud with no existing Convocatoria
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(null, 1L, 1L);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConfiguracionSolicitud
        () -> service.create(configuracionSolicitud))
        // then: throw exception as Convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  void create_WithDuplicatedConvocatoria_ThrowsIllegalArgumentException() {
    // given: a ConfiguracionSolicitud with duplicated Convocatoria
    ConfiguracionSolicitud configuracionSolicitudExistente = generarMockConfiguracionSolicitud(1L, 1L, 1L);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(null, 1L, 1L);

    BDDMockito.given(repository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitudExistente));

    Assertions.assertThatThrownBy(
        // when: create ConfiguracionSolicitud
        () -> service.create(configuracionSolicitud))
        // then: throw exception as Convocatoria already asigned
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "Configuración solicitud de Convocatoria ya existe");
  }

  @Test
  void create_WithConvocatoriaRegistradaAndWithoutTramitacionSGI_ThrowsIllegalArgumentException() {
    // given: a ConfiguracionSolicitud with convocatoria registrada and without
    // Tramitacion SGI
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(null, convocatoriaId, 1L);
    configuracionSolicitud.setTramitacionSGI(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    Assertions.assertThatThrownBy(
        // when: create ConfiguracionSolicitud
        () -> service.create(configuracionSolicitud))
        // then: throw exception as Tramitacion SGI can't be provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "Tramitación SGI de Configuración solicitud no puede ser nulo");
  }

  @Test
  void create_WithTramitacionSGITrueAndWithoutFasePresentacion_ThrowsIllegalArgumentException() {
    // given: a ConfiguracionSolicitud with TramitacionSGI = true and without
    // FasePresentacion
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(null, convocatoriaId, 1L);
    configuracionSolicitud.setTramitacionSGI(Boolean.TRUE);
    configuracionSolicitud.setFasePresentacionSolicitudes(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    Assertions.assertThatThrownBy(
        // when: create ConfiguracionSolicitud
        () -> service.create(configuracionSolicitud))
        // then: throw exception as FasePresentacion can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Plazo Presentación de Configuración solicitud no puede ser nulo");
  }

  @Test
  void create_WithTramitacionSGIFalseAndWithoutFasePresentacion_DoesNotThrowAnyException() {
    // given: a ConfiguracionSolicitud with TramitationSGI = false and without
    // FasePresentacion
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(null, convocatoriaId, 1L);
    configuracionSolicitud.setTramitacionSGI(Boolean.FALSE);
    configuracionSolicitud.setFasePresentacionSolicitudes(null);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    Assertions.assertThatCode(
        // when: create ConfiguracionSolicitud
        () -> service.create(configuracionSolicitud))
        // then: No exception thrown
        .doesNotThrowAnyException();
  }

  @Test
  void create_NoExistingFasePresentacion_ThrowsNotFoundException() {
    // given: a ConfiguracionSolicitud with no existing FasePresentacion
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(null, convocatoriaId, 1L);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(convocatoriaFaseRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConfiguracionSolicitud
        () -> service.create(configuracionSolicitud))
        // then: throw exception as FasePresentacion is not found
        .isInstanceOf(ConvocatoriaFaseNotFoundException.class);
  }

  @Test
  void update_withConvocatoriaRegistrada_ReturnsConfiguracionSolicitud() {
    // given: update existing ConfiguracionSolicitud
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud originalConfiguracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaId, 1L);
    ConfiguracionSolicitud updatedConfiguracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaId, 1L);
    updatedConfiguracionSolicitud.getFasePresentacionSolicitudes()
        .setFechaInicio(Instant.parse("2020-09-15T00:00:00Z"));
    updatedConfiguracionSolicitud.getFasePresentacionSolicitudes().setFechaFin(Instant.parse("2020-09-30T23:59:59Z"));
    updatedConfiguracionSolicitud.setTramitacionSGI(Boolean.FALSE);
    updatedConfiguracionSolicitud.setImporteMaximoSolicitud(BigDecimal.valueOf(54321));

    BDDMockito.given(repository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalConfiguracionSolicitud));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(convocatoriaFaseRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(updatedConfiguracionSolicitud.getFasePresentacionSolicitudes()));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConfiguracionSolicitud>any()))
        .willReturn(updatedConfiguracionSolicitud);

    // when: update ConfiguracionSolicitud
    ConfiguracionSolicitud updated = service.update(updatedConfiguracionSolicitud, convocatoriaId);

    // then: ConfiguracionSolicitud is updated
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isNotNull();
    Assertions.assertThat(updated.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(originalConfiguracionSolicitud.getConvocatoriaId());
    Assertions.assertThat(updated.getTramitacionSGI()).as("getTramitacionSGI()")
        .isEqualTo(updatedConfiguracionSolicitud.getTramitacionSGI());
    Assertions.assertThat(updated.getFasePresentacionSolicitudes().getId())
        .as("getFasePresentacionSolicitudes().getId()")
        .isEqualTo(updatedConfiguracionSolicitud.getFasePresentacionSolicitudes().getId());
    Assertions.assertThat(updated.getImporteMaximoSolicitud()).as("getImporteMaximoSolicitud()")
        .isEqualTo(updatedConfiguracionSolicitud.getImporteMaximoSolicitud());
  }

  @Test
  void update_WithConvocatoriaBorrador_ReturnsConfiguracionSolicitud() {
    // given: update existing ConfiguracionSolicitud with convocatoria estado
    // borrador
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    convocatoria.setEstado(Estado.BORRADOR);
    ConfiguracionSolicitud originalConfiguracionSolicitud = ConfiguracionSolicitud.builder().id(1L)
        .convocatoriaId(convocatoriaId).build();
    ConfiguracionSolicitud updatedConfiguracionSolicitud = ConfiguracionSolicitud.builder().id(1L)
        .convocatoriaId(convocatoriaId).importeMaximoSolicitud(BigDecimal.valueOf(54321)).build();

    BDDMockito.given(repository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalConfiguracionSolicitud));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConfiguracionSolicitud>any()))
        .willReturn(updatedConfiguracionSolicitud);

    // when: create ConfiguracionSolicitud
    ConfiguracionSolicitud updated = service.update(updatedConfiguracionSolicitud,
        originalConfiguracionSolicitud.getConvocatoriaId());

    // then: ConfiguracionSolicitud with minimum required data is updated
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(originalConfiguracionSolicitud.getConvocatoriaId());
    Assertions.assertThat(updated.getTramitacionSGI()).as("getTramitacionSGI()").isNull();
    Assertions.assertThat(updated.getFasePresentacionSolicitudes()).as("getFasePresentacionSolicitudes()").isNull();
    Assertions.assertThat(updated.getImporteMaximoSolicitud()).as("getImporteMaximoSolicitud()")
        .isEqualTo(updatedConfiguracionSolicitud.getImporteMaximoSolicitud());
  }

  @Test
  void update_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: update ConfiguracionSolicitud without convocatoria
    ConfiguracionSolicitud updatedConfiguracionSolicitud = generarMockConfiguracionSolicitud(1L, 1L, 1L);
    updatedConfiguracionSolicitud.setConvocatoriaId(null);

    Assertions.assertThatThrownBy(
        // when: update ConfiguracionSolicitud
        () -> service.update(updatedConfiguracionSolicitud, null))
        // then: throw exception as convocatoria can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Convocatoria no puede ser nulo");
  }

  @Test
  void update_NotFoundByConvocatoria_ThrowsNotFoundException() {
    // given: update ConfiguracionSolicitud not found by Convocatoria
    Long convocatoriaId = 1L;
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaId, 1L);

    BDDMockito.given(repository.findByConvocatoriaId(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConfiguracionSolicitud
        () -> service.update(configuracionSolicitud, configuracionSolicitud.getConvocatoriaId()))
        // then: throw exception as ConfiguracionSolicitud is not found by Convocatoria
        .isInstanceOf(ConfiguracionSolicitudNotFoundException.class);
  }

  @Test
  void update_WithConvocatoriaRegistradaAndWithoutTramitacionSGI_ThrowsIllegalArgumentException() {
    // given: a ConfiguracionSolicitud with convocatoria registrada and without
    // Tramitacion SGI
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud originalConfiguracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaId, 1L);
    ConfiguracionSolicitud updatedConfiguracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaId, 1L);
    updatedConfiguracionSolicitud.setTramitacionSGI(null);

    BDDMockito.given(repository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalConfiguracionSolicitud));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    Assertions.assertThatThrownBy(
        // when: update ConfiguracionSolicitud
        () -> service.update(updatedConfiguracionSolicitud, originalConfiguracionSolicitud.getConvocatoriaId()))
        // then: throw exception as Tramitacion SGI can't be provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "Tramitación SGI de Configuración solicitud no puede ser nulo");
  }

  @Test
  void update_WithTramitacionSGIFalseAndWithoutFasePresentacion_DoesNotThrowAnyException() {
    // given: a ConfiguracionSolicitud with TramitacionSGI = false and without
    // FasePresentacion
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud originalConfiguracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaId, 1L);
    ConfiguracionSolicitud updatedConfiguracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaId, 1L);
    updatedConfiguracionSolicitud.setTramitacionSGI(Boolean.FALSE);
    updatedConfiguracionSolicitud.setFasePresentacionSolicitudes(null);

    BDDMockito.given(repository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalConfiguracionSolicitud));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito
        .given(documentoRequeridoSolicitudRepository.findAll(
            ArgumentMatchers.<Specification<DocumentoRequeridoSolicitud>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Collections.emptyList()));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConfiguracionSolicitud>any()))
        .willReturn(updatedConfiguracionSolicitud);

    Assertions.assertThatCode(
        // when: update ConfiguracionSolicitud
        () -> service.update(updatedConfiguracionSolicitud, originalConfiguracionSolicitud.getConvocatoriaId()))
        // then: no exception thrown
        .doesNotThrowAnyException();
  }

  @Test
  void update_NoExistingFasePresentacion_ThrowsNotFoundException() {
    // given: a ConfiguracionSolicitud with no existing FasePresentacion
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud originalConfiguracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaId, 1L);
    ConfiguracionSolicitud updatedConfiguracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaId, 1L);

    BDDMockito.given(repository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalConfiguracionSolicitud));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(convocatoriaFaseRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConfiguracionSolicitud
        () -> service.update(updatedConfiguracionSolicitud, originalConfiguracionSolicitud.getConvocatoriaId()))
        // then: throw exception as FasePresentacion is not found
        .isInstanceOf(ConvocatoriaFaseNotFoundException.class);
  }

  @Test
  void update_FasePresentacionWithoutDocumentosRequeridos_DoesNotThrowAnyException() {
    // given: a ConfiguracionSolicitud with updated FasePresentacion without
    // DocumentosRequeridos assigned
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud originalConfiguracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaId, 1L);
    ConfiguracionSolicitud updatedConfiguracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaId, 2L);
    List<DocumentoRequeridoSolicitud> documentoRequeridoSolicitudList = new ArrayList<DocumentoRequeridoSolicitud>();

    BDDMockito.given(repository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalConfiguracionSolicitud));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito
        .given(documentoRequeridoSolicitudRepository.findAll(
            ArgumentMatchers.<Specification<DocumentoRequeridoSolicitud>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(documentoRequeridoSolicitudList, Pageable.unpaged(),
            documentoRequeridoSolicitudList.size()));

    BDDMockito.given(convocatoriaFaseRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(updatedConfiguracionSolicitud.getFasePresentacionSolicitudes()));

    BDDMockito.given(repository.save(ArgumentMatchers.<ConfiguracionSolicitud>any()))
        .willReturn(updatedConfiguracionSolicitud);

    Assertions.assertThatCode(
        // when: update ConfiguracionSolicitud
        () -> service.update(updatedConfiguracionSolicitud, originalConfiguracionSolicitud.getConvocatoriaId()))
        // then: no exception thrown
        .doesNotThrowAnyException();
  }

  @Test
  void update_FasePresentacionWithDocumentosRequeridos_ThrowsIllegalArgumentException() {
    // given: a ConfiguracionSolicitud with updated FasePresentacion and
    // DocumentosRequeridos assigned
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId, 1L, 1L, 1L, 1L, 1L, Boolean.TRUE);
    ConfiguracionSolicitud originalConfiguracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaId, 1L);
    ConfiguracionSolicitud updatedConfiguracionSolicitud = generarMockConfiguracionSolicitud(1L, convocatoriaId, 2L);
    List<DocumentoRequeridoSolicitud> documentoRequeridoSolicitudList = new ArrayList<DocumentoRequeridoSolicitud>();
    documentoRequeridoSolicitudList.add(generarMockDocumentoRequeridoSolicitud(originalConfiguracionSolicitud));

    BDDMockito.given(repository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(originalConfiguracionSolicitud));

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito
        .given(documentoRequeridoSolicitudRepository.findAll(
            ArgumentMatchers.<Specification<DocumentoRequeridoSolicitud>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(documentoRequeridoSolicitudList, Pageable.unpaged(),
            documentoRequeridoSolicitudList.size()));

    Assertions.assertThatThrownBy(
        // when: update ConfiguracionSolicitud
        () -> service.update(updatedConfiguracionSolicitud, originalConfiguracionSolicitud.getConvocatoriaId()))
        // then: throw exception as FasePresentacion has documents
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "Documento Requerido Solicitud de Configuración solicitud ya existe");
  }

  @Test
  void findByIdConvocatoria_WithExistingId_ReturnsConfiguracionSolicitud() throws Exception {
    // given: existing ConfiguracionSolicitud
    Long convocatoriaId = 1L;
    ConfiguracionSolicitud configuracionSolicitudExistente = generarMockConfiguracionSolicitud(1L, convocatoriaId, 1L);

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findByConvocatoriaId(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(configuracionSolicitudExistente));

    // when: find ConfiguracionSolicitud Convocatoria
    ConfiguracionSolicitud found = service.findByConvocatoriaId(configuracionSolicitudExistente.getConvocatoriaId());

    // then: ConfiguracionSolicitud is updated
    Assertions.assertThat(found).isNotNull();
    Assertions.assertThat(found.getId()).isNotNull();
    Assertions.assertThat(found.getConvocatoriaId()).as("getConvocatoriaId()")
        .isEqualTo(configuracionSolicitudExistente.getConvocatoriaId());
    Assertions.assertThat(found.getTramitacionSGI()).as("getTramitacionSGI()")
        .isEqualTo(configuracionSolicitudExistente.getTramitacionSGI());
    Assertions.assertThat(found.getFasePresentacionSolicitudes().getId()).as("getFasePresentacionSolicitudes().getId()")
        .isEqualTo(configuracionSolicitudExistente.getFasePresentacionSolicitudes().getId());
    Assertions.assertThat(found.getImporteMaximoSolicitud()).as("getImporteMaximoSolicitud()")
        .isEqualTo(configuracionSolicitudExistente.getImporteMaximoSolicitud());
  }

  @Test
  void findByConvocatoriaId_WithNoExistingConvocatoria_ThrowsNotFoundException() throws Exception {
    // given: no existing convocatoria
    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: find by non existing convocatoria
        () -> service.findByConvocatoriaId(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  /**
   * Genera un objeto ConfiguracionSolicitud
   * 
   * @param configuracionSolicitudId
   * @param convocatoriaId
   * @param convocatoriaFaseId
   * @return
   */
  private ConfiguracionSolicitud generarMockConfiguracionSolicitud(Long configuracionSolicitudId, Long convocatoriaId,
      Long convocatoriaFaseId) {

    Set<TipoFaseNombre> nombreTipoFase = new HashSet<>();
    nombreTipoFase.add(new TipoFaseNombre(Language.ES, "nombre-1"));

    // @formatter:off
    TipoFase tipoFase = TipoFase.builder()
        .id(convocatoriaFaseId)
        .nombre(nombreTipoFase)
        .activo(Boolean.TRUE)
        .build();

    Set<ConvocatoriaFaseObservaciones> obsConvocatoriaFase = new HashSet<>();
    obsConvocatoriaFase.add(new ConvocatoriaFaseObservaciones(Language.ES, "observaciones"));

    ConvocatoriaFase convocatoriaFase = ConvocatoriaFase.builder()
        .id(convocatoriaFaseId)
        .convocatoriaId(convocatoriaId)
        .tipoFase(tipoFase)
        .fechaInicio(Instant.parse("2020-10-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-10-15T00:00:00Z"))
        .observaciones(obsConvocatoriaFase)
        .build();

    ConfiguracionSolicitud configuracionSolicitud = ConfiguracionSolicitud.builder()
        .id(configuracionSolicitudId)
        .convocatoriaId(convocatoriaId)
        .tramitacionSGI(Boolean.TRUE)
        .fasePresentacionSolicitudes(convocatoriaFase)
        .importeMaximoSolicitud(BigDecimal.valueOf(12345))
        .build();
    // @formatter:on

    return configuracionSolicitud;
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
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(
        new ModeloEjecucionNombre(Language.ES, "nombreModeloEjecucion-" + String.format("%03d", modeloEjecucionId)));

    ModeloEjecucion modeloEjecucion = (modeloEjecucionId == null) ? null
        : ModeloEjecucion.builder()
            .id(modeloEjecucionId)
            .nombre(nombreModeloEjecucion)
            .activo(Boolean.TRUE)
            .build();

    Set<TipoFinalidadNombre> nombreTipoFinalidad = new HashSet<>();
    nombreTipoFinalidad.add(
        new TipoFinalidadNombre(Language.ES, "nombreTipoFinalidad-" + String.format("%03d", modeloTipoFinalidadId)));

    TipoFinalidad tipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : TipoFinalidad.builder()
            .id(modeloTipoFinalidadId)
            .nombre(nombreTipoFinalidad)
            .activo(Boolean.TRUE)
            .build();

    ModeloTipoFinalidad modeloTipoFinalidad = (modeloTipoFinalidadId == null) ? null
        : ModeloTipoFinalidad.builder()
            .id(modeloTipoFinalidadId)
            .modeloEjecucion(modeloEjecucion)
            .tipoFinalidad(tipoFinalidad)
            .activo(Boolean.TRUE)
            .build();

    Set<TipoRegimenConcurrenciaNombre> tipoRegimenConcurrenciaNombre = new HashSet<>();
    tipoRegimenConcurrenciaNombre.add(new TipoRegimenConcurrenciaNombre(Language.ES,
        "nombreTipoRegimenConcurrencia-" + String.format("%03d", tipoRegimenConcurrenciaId)));

    TipoRegimenConcurrencia tipoRegimenConcurrencia = (tipoRegimenConcurrenciaId == null) ? null
        : TipoRegimenConcurrencia.builder()
            .id(tipoRegimenConcurrenciaId)
            .nombre(tipoRegimenConcurrenciaNombre)
            .activo(Boolean.TRUE)
            .build();

    Set<TipoAmbitoGeograficoNombre> nombre = new HashSet<>();
    nombre.add(new TipoAmbitoGeograficoNombre(Language.ES,
        "nombreTipoAmbitoGeografico-" + String.format("%03d", tipoAmbitoGeogragicoId)));

    TipoAmbitoGeografico tipoAmbitoGeografico = (tipoAmbitoGeogragicoId == null) ? null
        : TipoAmbitoGeografico.builder()
            .id(tipoAmbitoGeogragicoId)
            .nombre(nombre)
            .activo(Boolean.TRUE)
            .build();

    Set<ConvocatoriaTitulo> convocatoriaTitulo = new HashSet<>();
    convocatoriaTitulo.add(new ConvocatoriaTitulo(Language.ES, "titulo-" + String.format("%03d", convocatoriaId)));

    Set<ConvocatoriaObjeto> convocatoriaObjeto = new HashSet<>();
    convocatoriaObjeto.add(new ConvocatoriaObjeto(Language.ES, "objeto-" + String.format("%03d", convocatoriaId)));

    Set<ConvocatoriaObservaciones> convocatoriaObservaciones = new HashSet<>();
    convocatoriaObservaciones
        .add(new ConvocatoriaObservaciones(Language.ES, "observaciones-" + String.format("%03d", convocatoriaId)));

    return Convocatoria.builder()
        .id(convocatoriaId)
        .unidadGestionRef((unidadGestionId == null) ? null : "unidad-" + String.format("%03d", unidadGestionId))
        .modeloEjecucion(modeloEjecucion)
        .codigo("codigo-" + String.format("%03d", convocatoriaId))
        .fechaPublicacion(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaProvisional(Instant.parse("2021-08-01T00:00:00Z"))
        .fechaConcesion(Instant.parse("2021-08-01T00:00:00Z"))
        .titulo(convocatoriaTitulo)
        .objeto(convocatoriaObjeto)
        .observaciones(convocatoriaObservaciones)
        .finalidad((modeloTipoFinalidad == null) ? null : modeloTipoFinalidad.getTipoFinalidad())
        .regimenConcurrencia(tipoRegimenConcurrencia)
        .estado(Convocatoria.Estado.REGISTRADA)
        .duracion(12)
        .ambitoGeografico(tipoAmbitoGeografico)
        .clasificacionCVN(ClasificacionCVN.AYUDAS)
        .activo(activo)
        .build();
  }

  /**
   * Función que genera un objeto DocumentoRequeridoSolicitud a partir de una
   * ConfiguracionSolicitud
   * 
   * @param configuracionSolicitud objeto ConfiguracionSolicitud
   * @return el objeto DocumentoRequeridoSolicitud
   */
  private DocumentoRequeridoSolicitud generarMockDocumentoRequeridoSolicitud(
      ConfiguracionSolicitud configuracionSolicitud) {
    Set<DocumentoRequeridoSolicitudObservaciones> obsDocumentoRequerido = new HashSet<>();
    obsDocumentoRequerido.add(
        new DocumentoRequeridoSolicitudObservaciones(Language.ES,
            "observacionesDocumentoRequeridoSolicitud-" + configuracionSolicitud.getId()));

    // @formatter:on
    return DocumentoRequeridoSolicitud.builder().id(configuracionSolicitud.getId())
        .configuracionSolicitudId(configuracionSolicitud.getId())
        .tipoDocumento(generarMockTipoDocumento(configuracionSolicitud.getId()))
        .observaciones(obsDocumentoRequerido).build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id) {
    Set<TipoDocumentoNombre> nombreTipoDocumento = new HashSet<>();
    nombreTipoDocumento.add(new TipoDocumentoNombre(Language.ES, "nombreTipoDocumento-" + id));

    Set<TipoDocumentoDescripcion> descripcionTipoDocumento = new HashSet<>();
    descripcionTipoDocumento.add(new TipoDocumentoDescripcion(Language.ES, "descripcionTipoDocumento-" + id));
    // @formatter:off
    return TipoDocumento.builder()
        .id(id)
        .nombre(nombreTipoDocumento)
        .descripcion(descripcionTipoDocumento)
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on
  }
}
