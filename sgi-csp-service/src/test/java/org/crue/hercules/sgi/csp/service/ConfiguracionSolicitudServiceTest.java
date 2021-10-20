package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaFaseNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.model.Convocatoria.Estado;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaFaseRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.DocumentoRequeridoSolicitudRepository;
import org.crue.hercules.sgi.csp.service.impl.ConfiguracionSolicitudServiceImpl;
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

public class ConfiguracionSolicitudServiceTest extends BaseServiceTest {

  @Mock
  private ConfiguracionSolicitudRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ConvocatoriaFaseRepository convocatoriaFaseRepository;
  @Mock
  private DocumentoRequeridoSolicitudRepository documentoRequeridoSolicitudRepository;
  @Mock
  private ConvocatoriaService convocatoriaService;

  private ConfiguracionSolicitudService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConfiguracionSolicitudServiceImpl(repository, convocatoriaRepository, convocatoriaFaseRepository,
        documentoRequeridoSolicitudRepository, convocatoriaService);
  }

  @Test
  public void create_withConvocatoriaRegistrada_ReturnsConfiguracionSolicitud() {
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
  public void create_WithConvocatoriaBorrador_ReturnsConfiguracionSolicitud() {
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
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a ConfiguracionSolicitud with id filled
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(1L, 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: create ConfiguracionSolicitud
        () -> service.create(configuracionSolicitud))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear la ConfiguracionSolicitud");
  }

  @Test
  public void create_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: a ConfiguracionSolicitud without convocatoria
    ConfiguracionSolicitud configuracionSolicitud = generarMockConfiguracionSolicitud(null, 1L, 1L);
    configuracionSolicitud.setConvocatoriaId(null);

    Assertions.assertThatThrownBy(
        // when: create ConfiguracionSolicitud
        () -> service.create(configuracionSolicitud))
        // then: throw exception as convocatoria can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Convocatoria no puede ser null en ConfiguracionSolicitud");
  }

  @Test
  public void create_WithNoExistingConvocatoria_ThrowsNotFoundException() {
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
  public void create_WithDuplicatedConvocatoria_ThrowsIllegalArgumentException() {
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
            "Ya existe ConfiguracionSolicitud para la convocatoria %s", configuracionSolicitud.getConvocatoriaId());
  }

  @Test
  public void create_WithConvocatoriaRegistradaAndWithoutTramitacionSGI_ThrowsIllegalArgumentException() {
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
            "Habilitar presentacion SGI no puede ser null para crear ConfiguracionSolicitud cuando la convocatoria está registrada");
  }

  @Test
  public void create_WithTramitacionSGITrueAndWithoutFasePresentacion_ThrowsIllegalArgumentException() {
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
        .hasMessage("Plazo presentación solicitudes no puede ser null cuando se establece presentacion SGI");
  }

  @Test
  public void create_WithTramitacionSGIFalseAndWithoutFasePresentacion_DoesNotThrowAnyException() {
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
  public void create_NoExistingFasePresentacion_ThrowsNotFoundException() {
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
  public void update_withConvocatoriaRegistrada_ReturnsConfiguracionSolicitud() {
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
  public void update_WithConvocatoriaBorrador_ReturnsConfiguracionSolicitud() {
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
  public void update_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: update ConfiguracionSolicitud without convocatoria
    ConfiguracionSolicitud updatedConfiguracionSolicitud = generarMockConfiguracionSolicitud(1L, 1L, 1L);
    updatedConfiguracionSolicitud.setConvocatoriaId(null);

    Assertions.assertThatThrownBy(
        // when: update ConfiguracionSolicitud
        () -> service.update(updatedConfiguracionSolicitud, null))
        // then: throw exception as convocatoria can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Convocatoria no puede ser null en ConfiguracionSolicitud");
  }

  @Test
  public void update_NotFoundByConvocatoria_ThrowsNotFoundException() {
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
  public void update_WithConvocatoriaRegistradaAndWithoutTramitacionSGI_ThrowsIllegalArgumentException() {
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
            "Habilitar presentacion SGI no puede ser null para crear ConfiguracionSolicitud cuando la convocatoria está registrada");
  }

  @Test
  public void update_WithTramitacionSGIFalseAndWithoutFasePresentacion_DoesNotThrowAnyException() {
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
  public void update_NoExistingFasePresentacion_ThrowsNotFoundException() {
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
  public void update_FasePresentacionWithoutDocumentosRequeridos_DoesNotThrowAnyException() {
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
  public void update_FasePresentacionWithDocumentosRequeridos_ThrowsIllegalArgumentException() {
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
            "Si ya existen documentos requeridos solicitud asociados a la configuración, no se puede cambiar la fase");
  }

  @Test
  public void findByIdConvocatoria_WithExistingId_ReturnsConfiguracionSolicitud() throws Exception {
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
  public void findByConvocatoriaId_WithNoExistingConvocatoria_ThrowsNotFoundException() throws Exception {
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
    // @formatter:off
    TipoFase tipoFase = TipoFase.builder()
        .id(convocatoriaFaseId)
        .nombre("nombre-1")
        .activo(Boolean.TRUE)
        .build();

    ConvocatoriaFase convocatoriaFase = ConvocatoriaFase.builder()
        .id(convocatoriaFaseId)
        .convocatoriaId(convocatoriaId)
        .tipoFase(tipoFase)
        .fechaInicio(Instant.parse("2020-10-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-10-15T00:00:00Z"))
        .observaciones("observaciones")
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
   * Función que genera un objeto DocumentoRequeridoSolicitud a partir de una
   * ConfiguracionSolicitud
   * 
   * @param configuracionSolicitud objeto ConfiguracionSolicitud
   * @return el objeto DocumentoRequeridoSolicitud
   */
  private DocumentoRequeridoSolicitud generarMockDocumentoRequeridoSolicitud(
      ConfiguracionSolicitud configuracionSolicitud) {

    // @formatter:on
    return DocumentoRequeridoSolicitud.builder().id(configuracionSolicitud.getId())
        .configuracionSolicitudId(configuracionSolicitud.getId())
        .tipoDocumento(generarMockTipoDocumento(configuracionSolicitud.getId()))
        .observaciones("observacionesDocumentoRequeridoSolicitud-" + configuracionSolicitud.getId()).build();
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
}
