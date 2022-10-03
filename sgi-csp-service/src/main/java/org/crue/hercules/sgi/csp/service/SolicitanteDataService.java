package org.crue.hercules.sgi.csp.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput.Email;
import org.crue.hercules.sgi.csp.model.SolicitanteExterno;
import org.crue.hercules.sgi.csp.repository.SolicitanteExternoRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SolicitanteDataService {

  private final SolicitanteExternoRepository solicitanteExternoRepository;
  private final SgiApiSgpService sgiApiSgpService;

  public List<Recipient> getSolicitanteRecipients(Long solicitudId, String solicitanteRef) {

    String emailSolicitante = StringUtils
        .isNotEmpty(solicitanteRef)
            ? getEmailFromSolicitanteRef(solicitanteRef)
            : getEmailFromSolicitanteExterno(solicitudId);

    return Collections.singletonList(Recipient
        .builder().name(emailSolicitante).address(emailSolicitante)
        .build());
  }

  public String getSolicitanteNombreApellidos(Long solicitudId, String solicitanteRef) {
    return StringUtils
        .isNotEmpty(solicitanteRef)
            ? getNombreApellidosFromSolicitanteRef(solicitanteRef)
            : getNombreApellidosFromSolicitanteExterno(solicitudId);
  }

  private String getNombreApellidosFromSolicitanteExterno(Long solicitudId) {

    Optional<SolicitanteExterno> solicitanteExterno = this.solicitanteExternoRepository
        .findBySolicitudId(solicitudId);
    if (!solicitanteExterno.isPresent()) {
      return null;
    }

    return String.format("%s %s", solicitanteExterno.get().getNombre(),
        solicitanteExterno.get().getApellidos());
  }

  private String getNombreApellidosFromSolicitanteRef(String solicitanteRef) {
    PersonaOutput datosPersona = this.sgiApiSgpService.findById(solicitanteRef);
    if (datosPersona == null) {
      return null;
    }

    return String.format("%s %s", datosPersona.getNombre(), datosPersona.getApellidos());
  }

  private String getEmailFromSolicitanteExterno(Long solicitudId) {

    Optional<SolicitanteExterno> solicitanteExterno = this.solicitanteExternoRepository
        .findBySolicitudId(solicitudId);
    if (!solicitanteExterno.isPresent()) {
      return null;
    }

    return solicitanteExterno.get().getEmail();
  }

  private String getEmailFromSolicitanteRef(String solicitanteRef) {
    PersonaOutput datosPersona = this.sgiApiSgpService.findById(solicitanteRef);
    if (datosPersona == null) {
      return null;
    }

    return datosPersona.getEmails().stream().filter(Email::getPrincipal).findFirst().map(Email::getEmail)
        .orElse(null);
  }
}
