package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
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
public class SolicitudRrhhInput implements Serializable {

  @NotNull
  private Long id;

  @Size(max = SolicitudRrhh.ENTIDAD_REF_LENGTH)
  private String universidadRef;

  @Size(max = SolicitudRrhh.ENTIDAD_REF_LENGTH)
  private String areaAnepRef;

  @Size(max = SolicitudRrhh.UNIVERSIDAD_LENGTH)
  private String universidad;

}
