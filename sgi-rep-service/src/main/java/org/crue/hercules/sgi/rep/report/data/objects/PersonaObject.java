package org.crue.hercules.sgi.rep.report.data.objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.crue.hercules.sgi.rep.dto.sgp.EmailDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.enums.Genero;

import lombok.Getter;

@Getter
public class PersonaObject {

  private String id;
  private String nombre;
  private String apellidos;
  private Genero genero;
  private String email;
  private String numeroDocumento;

  public PersonaObject(PersonaDto dto) {
    this.id = dto.getId();
    this.nombre = dto.getNombre();
    this.apellidos = dto.getApellidos();
    this.genero = dto.getSexo().getId().equals("V") ? Genero.M : Genero.F;
    this.numeroDocumento = dto.getNumeroDocumento();

    if (null != dto.getEmails() && !dto.getEmails().isEmpty()) {
      this.email = dto.getEmails().stream()
          .filter(e -> null != e.getPrincipal() && e.getPrincipal().equals(Boolean.TRUE)).findFirst()
          .orElse(new EmailDto())
          .getEmail();
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    PersonaObject rhs = (PersonaObject) obj;
    return new EqualsBuilder()
        .appendSuper(super.equals(obj))
        .append(id, rhs.id)
        .isEquals();
  }

  @Override
  public int hashCode() {
    // you pick a hard-coded, randomly chosen, non-zero, odd number
    // ideally different for each class
    return new HashCodeBuilder(7, 40).append(id).toHashCode();
  }
}
