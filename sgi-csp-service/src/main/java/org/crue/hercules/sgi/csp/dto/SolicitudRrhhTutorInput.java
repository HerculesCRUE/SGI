package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.SolicitudRrhh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudRrhhTutorInput implements Serializable {

  @Size(max = SolicitudRrhh.ENTIDAD_REF_LENGTH)
  private String tutorRef;

}
