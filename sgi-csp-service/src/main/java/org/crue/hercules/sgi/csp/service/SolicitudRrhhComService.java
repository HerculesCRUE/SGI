package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.com.CspComCambioEstadoRechazadaSolTipoRrhhData;
import org.crue.hercules.sgi.csp.dto.com.CspComCambioEstadoSolicitadaSolTipoRrhhData;
import org.crue.hercules.sgi.csp.dto.com.CspComCambioEstadoSolicitadaSolTipoRrhhData.CspComCambioEstadoSolicitadaSolTipoRrhhDataBuilder;
import org.crue.hercules.sgi.csp.dto.com.CspComCambioEstadoValidadaSolTipoRrhhData;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput.Email;
import org.crue.hercules.sgi.csp.exceptions.SolicitudRrhhNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudRrhh;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRrhhRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolicitudRrhhComService {
  public static final String PATH_DELIMITER = "/";
  private static final String PATH_MENU_VALIDACION_TUTOR = PATH_DELIMITER + "inv/validacion-solicitudes-tutor";

  private final ConvocatoriaRepository convocatoriaRepository;
  private final SgiApiSgpService sgiApiSgpService;
  private final SgiApiComService emailService;
  private final SgiConfigProperties sgiConfigProperties;
  private final SolicitudRrhhRepository solicitudRrhhRepository;
  private final SolicitanteDataService solicitanteDataService;

  public void enviarComunicadoCambioEstadoSolicitadaSolTipoRrhh(Instant fechaEstado, Solicitud solicitud)
      throws JsonProcessingException {

    CspComCambioEstadoSolicitadaSolTipoRrhhDataBuilder dataBuilder = CspComCambioEstadoSolicitadaSolTipoRrhhData
        .builder()
        .fechaEstado(fechaEstado)
        .nombreApellidosSolicitante(
            this.solicitanteDataService.getSolicitanteNombreApellidos(solicitud.getId(), solicitud.getSolicitanteRef()))
        .codigoInternoSolicitud(solicitud.getCodigoRegistroInterno())
        .enlaceAplicacionMenuValidacionTutor(getEnlaceAplicacionMenuValidacionTutor());

    this.fillConvocatoriaData(dataBuilder, solicitud.getConvocatoriaId());

    log.debug(
        "Construyendo comunicado aviso cambio de estado SOLICITADA a la solicitud de tipo RRHH {} para enviarlo inmediatamente al solicitante",
        solicitud.getId());

    EmailOutput comunicado = this.emailService
        .createComunicadoCambioEstadoSolicitadaSolTipoRrhh(dataBuilder.build(),
            this.getTutorRecipients(solicitud.getId()));
    this.emailService.sendEmail(comunicado.getId());
  }

  public void enviarComunicadoCambioEstadoValidadaSolTipoRrhh(Instant fechaEstado, Solicitud solicitud)
      throws JsonProcessingException {

    CspComCambioEstadoValidadaSolTipoRrhhData data = CspComCambioEstadoValidadaSolTipoRrhhData
        .builder()
        .fechaEstado(fechaEstado)
        .nombreApellidosSolicitante(
            this.solicitanteDataService.getSolicitanteNombreApellidos(solicitud.getId(), solicitud.getSolicitanteRef()))
        .codigoInternoSolicitud(solicitud.getCodigoRegistroInterno())
        .tituloConvocatoria(this.getTituloConvocatoria(solicitud.getConvocatoriaId()))
        .build();

    log.debug(
        "Construyendo comunicado aviso cambio de estado VALIDADA a la solicitud de tipo RRHH {} para enviarlo inmediatamente al solicitante",
        solicitud.getId());

    EmailOutput comunicado = this.emailService
        .createComunicadoCambioEstadoValidadaSolTipoRrhh(data,
            this.solicitanteDataService.getSolicitanteRecipients(solicitud.getId(), solicitud.getSolicitanteRef()));
    this.emailService.sendEmail(comunicado.getId());
  }

  public void enviarComunicadoCambioEstadoRechazadaSolTipoRrhh(Instant fechaEstado, Solicitud solicitud)
      throws JsonProcessingException {

    CspComCambioEstadoRechazadaSolTipoRrhhData data = CspComCambioEstadoRechazadaSolTipoRrhhData
        .builder()
        .fechaEstado(fechaEstado)
        .nombreApellidosSolicitante(
            this.solicitanteDataService.getSolicitanteNombreApellidos(solicitud.getId(), solicitud.getSolicitanteRef()))
        .codigoInternoSolicitud(solicitud.getCodigoRegistroInterno())
        .tituloConvocatoria(this.getTituloConvocatoria(solicitud.getConvocatoriaId()))
        .build();

    log.debug(
        "Construyendo comunicado aviso cambio de estado RECHAZADA a la solicitud de tipo RRHH {} para enviarlo inmediatamente al solicitante",
        solicitud.getId());

    EmailOutput comunicado = this.emailService
        .createComunicadoCambioEstadoRechazadaSolTipoRrhh(data,
            this.solicitanteDataService.getSolicitanteRecipients(solicitud.getId(), solicitud.getSolicitanteRef()));
    this.emailService.sendEmail(comunicado.getId());

  }

  private void fillConvocatoriaData(
      CspComCambioEstadoSolicitadaSolTipoRrhhDataBuilder dataBuilder,
      Long convocatoriaId) {
    if (convocatoriaId != null) {
      Optional<Convocatoria> convocatoria = this.convocatoriaRepository.findById(convocatoriaId);
      if (convocatoria.isPresent()) {
        dataBuilder.tituloConvocatoria(convocatoria.get().getTitulo());
        dataBuilder.fechaProvisionalConvocatoria(convocatoria.get().getFechaProvisional());
      }
    }
  }

  private String getTituloConvocatoria(Long convocatoriaId) {
    if (convocatoriaId != null) {
      Optional<Convocatoria> convocatoria = this.convocatoriaRepository.findById(convocatoriaId);
      if (convocatoria.isPresent()) {
        return convocatoria.get().getTitulo();
      }
    }
    return null;
  }

  private String getEnlaceAplicacionMenuValidacionTutor() {
    return this.sgiConfigProperties.getWebUrl() + PATH_MENU_VALIDACION_TUTOR;
  }

  private List<Recipient> getTutorRecipients(Long solicitudId) {
    String tutorRef = this.solicitudRrhhRepository.findBySolicitudId(solicitudId).map(SolicitudRrhh::getTutorRef)
        .orElseThrow(() -> new SolicitudRrhhNotFoundException(solicitudId));
    PersonaOutput datosSolicitante = this.sgiApiSgpService.findById(tutorRef);

    return datosSolicitante.getEmails().stream().filter(Email::getPrincipal)
        .map(email -> Recipient
            .builder().name(email.getEmail()).address(email.getEmail())
            .build())
        .collect(Collectors.toList());
  }

}
