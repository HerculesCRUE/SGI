package org.crue.hercules.sgi.csp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.com.CspComAddModCertAutorizacionPartProyectoExtData;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.csp.exceptions.AutorizacionNotFoundException;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.CertificadoAutorizacion;
import org.crue.hercules.sgi.csp.repository.AutorizacionRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificadoAutorizacionComService {

  private final AutorizacionRepository autorizacionRepository;
  private final SgiConfigProperties sgiConfigProperties;
  private final SgiApiComService emailService;
  private final SgiApiSgpService sgiApiSgpService;

  public void enviarComunicadoAddModificarCertificadoAutorizacionParticipacionProyectoExterno(
      CertificadoAutorizacion certificadoAutorizacion) {
    try {
      Autorizacion autorizacion = this.autorizacionRepository.findById(certificadoAutorizacion.getAutorizacionId())
          .orElseThrow(() -> new AutorizacionNotFoundException(certificadoAutorizacion.getAutorizacionId()));

      CspComAddModCertAutorizacionPartProyectoExtData data = CspComAddModCertAutorizacionPartProyectoExtData.builder()
          .tituloProyectoExt(autorizacion.getTituloProyecto())
          .enlaceAplicacion(sgiConfigProperties.getWebUrl())
          .build();

      log.debug("Construyendo comunicado para enviarlo inmediatamente al solicitante");

      EmailOutput comunicado = this.emailService
          .createComunicadoAddModificarCertificadoAutorizacionParticipacionProyectoExterno(data,
              this.getSolicitanteRecipients(autorizacion.getSolicitanteRef()));
      this.emailService.sendEmail(comunicado.getId());

    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private List<Recipient> getSolicitanteRecipients(String solicitanteRef) {
    PersonaOutput datosSolicitante = this.sgiApiSgpService.findById(solicitanteRef);

    return datosSolicitante.getEmails().stream().filter(email -> email.getPrincipal())
        .map(email -> Recipient
            .builder().name(email.getEmail()).address(email.getEmail())
            .build())
        .collect(Collectors.toList());
  }
}
