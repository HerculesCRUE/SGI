package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

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
public class ProyectoApartadosToBeCopied implements Serializable {
  private boolean elegibilidad;
  private boolean equipo;
  private boolean responsableEconomico;
  private boolean socios;
  private boolean equiposSocios;
}
