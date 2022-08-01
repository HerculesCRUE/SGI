package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoCategoria;

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
public class SolicitudRrhhRequisitoCategoriaInput implements Serializable {

  @NotNull
  private Long solicitudRrhhId;

  @NotNull
  private Long requisitoIpCategoriaProfesionalId;

  @NotNull
  @Size(max = SolicitudRrhhRequisitoCategoria.ENTIDAD_REF_LENGTH)
  private String documentoRef;

}
