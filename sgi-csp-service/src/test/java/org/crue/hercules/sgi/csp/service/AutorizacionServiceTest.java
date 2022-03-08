package org.crue.hercules.sgi.csp.service;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.DocumentoOutput;
import org.crue.hercules.sgi.csp.exceptions.AlreadyInEstadoAutorizacionException;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.crue.hercules.sgi.csp.repository.AutorizacionRepository;
import org.crue.hercules.sgi.csp.repository.EstadoAutorizacionRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiRepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

class AutorizacionServiceTest extends BaseServiceTest {

  @Mock
  private AutorizacionRepository autorizacionRepository;
  @Mock
  private EstadoAutorizacionRepository estadoAutorizacionRepository;
  @Mock
  private SgiApiRepService reportService;
  @Mock
  private SgdocService sgdocService;
  @Mock
  private SgiConfigProperties sgiConfigProperties;

  private AutorizacionService autorizacionService;

  @BeforeEach
  public void setup() {
    this.autorizacionService = new AutorizacionService(
        autorizacionRepository,
        estadoAutorizacionRepository,
        reportService,
        sgdocService,
        sgiConfigProperties);
  }

  @Test
  void create_WithAutorizacionIdNotNull_ThrowsIllegalArgumentException() {
    Autorizacion autorizacion = this.buildMockAutorizacion(1L, null);

    Assertions.assertThatThrownBy(() -> this.autorizacionService.create(autorizacion))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_WithAutorizacionIdNull_ThrowsIllegalArgumentException() {
    Autorizacion autorizacion = this.buildMockAutorizacion(null, null);

    Assertions.assertThatThrownBy(() -> this.autorizacionService.update(autorizacion))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void delete_WithAutorizacionIdNull_ThrowsIllegalArgumentException() {
    Assertions.assertThatThrownBy(() -> this.autorizacionService.delete(null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @WithMockUser(authorities = { "CSP-AUT-E" })
  @Test
  void cambiarEstado_WithSameEstado_ThrowsAlreadyInEstadoAutorizacionException() {
    Long autorizacionId = 1L;
    Long newEstadoId = 1L;
    EstadoAutorizacion newEstado = this.buildMockEstadoAutorizacion(newEstadoId, autorizacionId,
        EstadoAutorizacion.Estado.AUTORIZADA);
    Autorizacion autorizacion = this.buildMockAutorizacion(autorizacionId, newEstado);

    BDDMockito.given(this.autorizacionRepository.findById(anyLong())).willReturn(Optional.of(autorizacion));

    Assertions.assertThatThrownBy(() -> this.autorizacionService.cambiarEstado(autorizacionId, newEstado))
        .isInstanceOf(AlreadyInEstadoAutorizacionException.class);
  }

  @Test
  void presentable_WithAutorizacionIdNull_ThrowsIllegalArgumentException() {
    Assertions.assertThatThrownBy(() -> this.autorizacionService.presentable(null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void generarDocumentoAutorizacion_ReturnsDocumentoOutput() throws IOException {
    String filename = "application.yml";
    final Resource docFile = new ClassPathResource("application.yml");

    final DocumentoOutput expectedDocumento = DocumentoOutput.builder()
        .archivo(IOUtils.toByteArray(docFile.getInputStream()))
        .documentoRef("application.yml")
        .autorRef("user")
        .fechaCreacion(LocalDateTime.now())
        .nombre("application.yml")
        .build();

    BDDMockito.given(this.reportService.getInformeAutorizacion(anyLong())).willReturn(docFile);

    BDDMockito.given(this.sgiConfigProperties.getTimeZone()).willReturn(TimeZone.getDefault());

    BDDMockito.given(this.sgdocService.uploadInforme(anyString(), ArgumentMatchers.<Resource>any()))
        .willReturn(expectedDocumento);

    DocumentoOutput documento = autorizacionService.generarDocumentoAutorizacion(1L);

    Assertions.assertThat(documento).isNotNull();
    Assertions.assertThat(documento.getArchivo()).isEqualTo(expectedDocumento.getArchivo());
    Assertions.assertThat(documento.getDocumentoRef()).isEqualTo(expectedDocumento.getDocumentoRef());
    Assertions.assertThat(documento.getAutorRef()).isEqualTo(expectedDocumento.getAutorRef());
    Assertions.assertThat(documento.getFechaCreacion()).isEqualTo(expectedDocumento.getFechaCreacion());
    Assertions.assertThat(documento.getNombre()).isEqualTo(expectedDocumento.getNombre());

  }

  private EstadoAutorizacion buildMockEstadoAutorizacion(Long id, Long autorizacionId,
      EstadoAutorizacion.Estado estado) {
    return EstadoAutorizacion.builder()
        .id(id)
        .autorizacionId(autorizacionId)
        .estado(estado)
        .build();
  }

  private Autorizacion buildMockAutorizacion(Long id, EstadoAutorizacion estado) {
    return Autorizacion.builder()
        .id(id)
        .estado(estado)
        .build();
  }
}