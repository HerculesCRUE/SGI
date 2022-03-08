package org.crue.hercules.sgi.csp.converter;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput.Email;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class ComConverterTest {

  @Test
  void toRecipients_ReturnsRecipientList() {
    List<PersonaOutput> personasOutput = Arrays.asList(this.buildMockPersonaOutput("test1", "user", "testing testing",
        Arrays.asList(Email.builder().email("mail").principal(true).build())));

    List<Recipient> recipients = ComConverter.toRecipients(personasOutput);

    Assertions.assertThat(recipients).isNotNull();
    Assertions.assertThat(recipients.size()).isEqualTo(1);

    PersonaOutput persona = personasOutput.get(0);

    Assertions.assertThat(recipients.get(0).getName()).isEqualTo(persona.getNombre() + " " + persona.getApellidos());
    Assertions.assertThat(recipients.get(0).getAddress()).isEqualTo(persona.getEmails().get(0).getEmail());

  }

  private PersonaOutput buildMockPersonaOutput(String id, String nombre, String apellidos, List<Email> emails) {
    return PersonaOutput
        .builder()
        .id(id)
        .nombre(nombre)
        .apellidos(apellidos)
        .emails(emails)
        .build();
  }
}