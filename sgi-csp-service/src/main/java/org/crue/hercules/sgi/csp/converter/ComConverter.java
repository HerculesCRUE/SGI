package org.crue.hercules.sgi.csp.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput;
import org.springframework.util.CollectionUtils;

public class ComConverter {

  private ComConverter() {
    // To prevent instances
  }

  /**
   * Convierte un listado de personas a destinatarios de un email. Solo se tienen
   * en cuenta aquellas personas que tengan un email principal
   * 
   * @param personas Listado de personas
   * @return Listado de destinatarios
   */
  public static List<Recipient> toRecipients(List<PersonaOutput> personas) {
    if (CollectionUtils.isEmpty(personas)) {
      return new ArrayList<>();
    }
    return personas.stream().filter(persona -> {
      return persona.getEmails() != null
          && persona.getEmails().stream().anyMatch(PersonaOutput.Email::getPrincipal);
    }).map(
        persona -> new Recipient(persona.getNombre() + " " + persona.getApellidos(),
            persona.getEmails().stream().filter(
                PersonaOutput.Email::getPrincipal).findFirst().get().getEmail()))
        .collect(Collectors.toList());
  }
}
