package org.crue.hercules.sgi.csp.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudCambioEstadoAlegacionesData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudCambioEstadoDefinitivoData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudCambioEstadoProvisionalData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudCambioEstadoSolicitadaData;
import org.crue.hercules.sgi.csp.dto.com.CspComSolicitudPeticionEvaluacionData;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class SolicitudComServiceTest {

  @Mock
  private SgiApiComService emailService;
  @Mock
  private SolicitanteDataService solicitanteDataService;
  @Mock
  private SgiApiCnfService configService;

  private SolicitudComService solicitudComService;

  @BeforeEach
  void setUp() {
    solicitudComService = new SolicitudComService(
        emailService,
        configService,
        solicitanteDataService);
  }

  @Test
  void enviarComunicadoSolicitudCambioEstadoSolicitada_ShouldDoNothing() throws Exception {
    Long solicitudId = 1L;
    String solicitanteRef = "2323423";
    String unidadGestionRef = "OPE";
    String tituloConvocatoria = "TESTING CONVOCATORIA";
    Instant fechaPublicacionConvocatoria = Instant.now();
    Instant fechaCambioEstadoSolicitud = Instant.now();
    EmailOutput emailOutput = EmailOutput.builder().id(1L).build();

    BDDMockito.given(configService
        .findStringListByName(ArgumentMatchers.anyString())).willReturn(Arrays.asList("fjalonso@um.com"));

    BDDMockito
        .given(emailService.createComunicadoSolicitudCambioEstadoSolicitada(
            ArgumentMatchers.<CspComSolicitudCambioEstadoSolicitadaData>any(), ArgumentMatchers.<List<Recipient>>any()))
        .willReturn(emailOutput);

    this.solicitudComService.enviarComunicadoSolicitudCambioEstadoSolicitada(solicitudId,
        solicitanteRef,
        unidadGestionRef,
        tituloConvocatoria,
        fechaPublicacionConvocatoria,
        fechaCambioEstadoSolicitud);

    verify(this.emailService, Mockito.times(1)).sendEmail(ArgumentMatchers.anyLong());
  }

  @Test
  void enviarComunicadoSolicitudCambioEstadoAlegaciones() throws Exception {
    Long solicitudId = 1L;
    String solicitanteRef = "2323423";
    String unidadGestionRef = "OPE";
    String tituloConvocatoria = "TESTING CONVOCATORIA";
    String codigoInterno = "11";
    Instant fechaPublicacionConvocatoria = Instant.now();
    Instant fechaCambioEstadoSolicitud = Instant.now();
    EmailOutput emailOutput = EmailOutput.builder().id(1L).build();

    BDDMockito.given(configService
        .findStringListByName(ArgumentMatchers.anyString())).willReturn(Arrays.asList("fjalonso@um.com"));

    BDDMockito
        .given(emailService.createComunicadoSolicitudCambioEstadoAlegaciones(
            ArgumentMatchers.<CspComSolicitudCambioEstadoAlegacionesData>any(),
            ArgumentMatchers.<List<Recipient>>any()))
        .willReturn(emailOutput);

    this.solicitudComService.enviarComunicadoSolicitudCambioEstadoAlegaciones(solicitudId,
        solicitanteRef,
        unidadGestionRef,
        tituloConvocatoria,
        codigoInterno,
        fechaPublicacionConvocatoria,
        fechaCambioEstadoSolicitud);

    verify(this.emailService, Mockito.times(1)).sendEmail(ArgumentMatchers.anyLong());
  }

  @Test
  void enviarComunicadoSolicitudCambioEstadoExclProv_WithSolicitanteRef_ShouldSendMail() throws Exception {
    Long solicitudId = 1L;
    String solicitanteRef = "2324944";
    String tituloConvocatoria = "Convocatoria Test";
    Instant fechaProvisionalConvocatoria = Instant.now();
    EmailOutput emailOutput = EmailOutput.builder().id(1L).build();
    List<ConvocatoriaEnlace> convocatoriaEnlaces = Arrays.asList(buildMockConvocatoriaEnlace(1L));

    BDDMockito.given(this.solicitanteDataService.getSolicitanteRecipients(anyLong(), anyString()))
        .willReturn(this.buildMockSolicitanteExternoRecipients());

    BDDMockito
        .given(emailService.createComunicadoSolicitudCambioEstadoExclProv(
            ArgumentMatchers.<CspComSolicitudCambioEstadoProvisionalData>any(),
            ArgumentMatchers.<List<Recipient>>any()))
        .willReturn(emailOutput);

    solicitudComService.enviarComunicadoSolicitudCambioEstadoExclProv(solicitudId, solicitanteRef, tituloConvocatoria,
        fechaProvisionalConvocatoria, convocatoriaEnlaces);

    verify(this.emailService, Mockito.times(1)).sendEmail(ArgumentMatchers.anyLong());
  }

  @Test
  void enviarComunicadoSolicitudCambioEstadoExclDef_WithoutSolicitanteRef_ShouldSendMail() throws Exception {
    Long solicitudId = 1L;
    String solicitanteRef = "";
    String tituloConvocatoria = "Convocatoria Test Solicitente Externo";
    Instant fechaProvisionalConvocatoria = Instant.now();
    EmailOutput emailOutput = EmailOutput.builder().id(1L).build();
    List<ConvocatoriaEnlace> convocatoriaEnlaces = Arrays.asList(buildMockConvocatoriaEnlace(1L));

    BDDMockito.given(this.solicitanteDataService.getSolicitanteRecipients(anyLong(), anyString()))
        .willReturn(this.buildMockSolicitanteExternoRecipients());

    BDDMockito
        .given(emailService.createComunicadoSolicitudCambioEstadoExclDef(
            ArgumentMatchers.<CspComSolicitudCambioEstadoDefinitivoData>any(),
            ArgumentMatchers.<List<Recipient>>any()))
        .willReturn(emailOutput);

    solicitudComService.enviarComunicadoSolicitudCambioEstadoExclDef(solicitudId, solicitanteRef, tituloConvocatoria,
        fechaProvisionalConvocatoria, convocatoriaEnlaces);

    verify(this.emailService, Mockito.times(1)).sendEmail(ArgumentMatchers.anyLong());
  }

  @Test
  void enviarComunicadoSolicitudCambioEstadoConcProv_WithoutSolicitanteRef_ShouldSendMail() throws Exception {
    Long solicitudId = 1L;
    String solicitanteRef = "";
    String tituloConvocatoria = "Convocatoria Test Solicitente Externo";
    Instant fechaProvisionalConvocatoria = Instant.now();
    EmailOutput emailOutput = EmailOutput.builder().id(1L).build();
    List<ConvocatoriaEnlace> convocatoriaEnlaces = Arrays.asList(buildMockConvocatoriaEnlace(1L));

    BDDMockito.given(this.solicitanteDataService.getSolicitanteRecipients(anyLong(), anyString()))
        .willReturn(this.buildMockSolicitanteExternoRecipients());

    BDDMockito
        .given(emailService.createComunicadoSolicitudCambioEstadoConcProv(
            ArgumentMatchers.<CspComSolicitudCambioEstadoProvisionalData>any(),
            ArgumentMatchers.<List<Recipient>>any()))
        .willReturn(emailOutput);

    solicitudComService.enviarComunicadoSolicitudCambioEstadoConcProv(solicitudId, solicitanteRef, tituloConvocatoria,
        fechaProvisionalConvocatoria, convocatoriaEnlaces);

    verify(this.emailService, Mockito.times(1)).sendEmail(ArgumentMatchers.anyLong());
  }

  @Test
  void enviarComunicadoSolicitudCambioEstadoDenProv_WithoutSolicitanteRef_ShouldSendMail() throws Exception {
    Long solicitudId = 1L;
    String solicitanteRef = "";
    String tituloConvocatoria = "Convocatoria Test Solicitente Externo";
    Instant fechaProvisionalConvocatoria = Instant.now();
    EmailOutput emailOutput = EmailOutput.builder().id(1L).build();
    List<ConvocatoriaEnlace> convocatoriaEnlaces = Arrays.asList(buildMockConvocatoriaEnlace(1L));

    BDDMockito.given(this.solicitanteDataService.getSolicitanteRecipients(anyLong(), anyString()))
        .willReturn(this.buildMockSolicitanteExternoRecipients());

    BDDMockito
        .given(emailService.createComunicadoSolicitudCambioEstadoDenProv(
            ArgumentMatchers.<CspComSolicitudCambioEstadoProvisionalData>any(),
            ArgumentMatchers.<List<Recipient>>any()))
        .willReturn(emailOutput);

    solicitudComService.enviarComunicadoSolicitudCambioEstadoDenProv(solicitudId, solicitanteRef, tituloConvocatoria,
        fechaProvisionalConvocatoria, convocatoriaEnlaces);

    verify(this.emailService, Mockito.times(1)).sendEmail(ArgumentMatchers.anyLong());
  }

  @Test
  void enviarComunicadoSolicitudCambioEstadoConc_WithoutSolicitanteRef_ShouldSendMail() throws Exception {
    Long solicitudId = 1L;
    String solicitanteRef = "";
    String tituloConvocatoria = "Convocatoria Test Solicitente Externo";
    Instant fechaProvisionalConvocatoria = Instant.now();
    EmailOutput emailOutput = EmailOutput.builder().id(1L).build();
    List<ConvocatoriaEnlace> convocatoriaEnlaces = Arrays.asList(buildMockConvocatoriaEnlace(1L));

    BDDMockito.given(this.solicitanteDataService.getSolicitanteRecipients(anyLong(), anyString()))
        .willReturn(this.buildMockSolicitanteExternoRecipients());

    BDDMockito
        .given(emailService.createComunicadoSolicitudCambioEstadoConc(
            ArgumentMatchers.<CspComSolicitudCambioEstadoDefinitivoData>any(),
            ArgumentMatchers.<List<Recipient>>any()))
        .willReturn(emailOutput);

    solicitudComService.enviarComunicadoSolicitudCambioEstadoConc(solicitudId, solicitanteRef, tituloConvocatoria,
        fechaProvisionalConvocatoria, convocatoriaEnlaces);

    verify(this.emailService, Mockito.times(1)).sendEmail(ArgumentMatchers.anyLong());
  }

  @Test
  void enviarComunicadoSolicitudCambioEstadoDen_WithoutSolicitanteRef_ShouldSendMail() throws Exception {
    Long solicitudId = 1L;
    String solicitanteRef = "";
    String tituloConvocatoria = "Convocatoria Test Solicitente Externo";
    Instant fechaProvisionalConvocatoria = Instant.now();
    EmailOutput emailOutput = EmailOutput.builder().id(1L).build();
    List<ConvocatoriaEnlace> convocatoriaEnlaces = Arrays.asList(buildMockConvocatoriaEnlace(1L));

    BDDMockito.given(this.solicitanteDataService.getSolicitanteRecipients(anyLong(), anyString()))
        .willReturn(this.buildMockSolicitanteExternoRecipients());
    ;

    BDDMockito
        .given(emailService.createComunicadoSolicitudCambioEstadoDen(
            ArgumentMatchers.<CspComSolicitudCambioEstadoDefinitivoData>any(),
            ArgumentMatchers.<List<Recipient>>any()))
        .willReturn(emailOutput);

    solicitudComService.enviarComunicadoSolicitudCambioEstadoDen(solicitudId, solicitanteRef, tituloConvocatoria,
        fechaProvisionalConvocatoria, convocatoriaEnlaces);

    verify(this.emailService, Mockito.times(1)).sendEmail(ArgumentMatchers.anyLong());
  }

  @Test
  void enviarComunicadoSolicitudAltaPeticionEvaluacionEti_WithoutSolicitanteRef_ShouldSendMail() throws Exception {
    Long solicitudId = 1L;
    String codigoPeticionEvaluacion = "20202";
    String codigoSolicitud = "sol-001";
    String solicitanteRef = "";
    EmailOutput emailOutput = EmailOutput.builder().id(1L).build();

    BDDMockito.given(this.solicitanteDataService.getSolicitanteRecipients(anyLong(), anyString()))
        .willReturn(this.buildMockSolicitanteExternoRecipients());

    BDDMockito
        .given(emailService.createComunicadoSolicitudPeticionEvaluacionEti(
            ArgumentMatchers.<CspComSolicitudPeticionEvaluacionData>any(),
            ArgumentMatchers.<List<Recipient>>any()))
        .willReturn(emailOutput);

    solicitudComService.enviarComunicadoSolicitudAltaPeticionEvaluacionEti(solicitudId, codigoPeticionEvaluacion,
        codigoSolicitud,
        solicitanteRef);

    verify(this.emailService, Mockito.times(1)).sendEmail(ArgumentMatchers.anyLong());
  }

  private ConvocatoriaEnlace buildMockConvocatoriaEnlace(Long id) {
    return ConvocatoriaEnlace.builder()
        .convocatoriaId(1L)
        .descripcion("Enlace Test")
        .id(id)
        .tipoEnlace(TipoEnlace.builder()
            .id(1L)
            .activo(Boolean.TRUE)
            .nombre("ENLACE MOCKED")
            .build())
        .build();
  }

  private List<Recipient> buildMockSolicitanteExternoRecipients() {
    return Arrays.asList(Recipient.builder()
        .address("fjalonso@um.com")
        .name("fjalonso@um.com")
        .build());
  }
}