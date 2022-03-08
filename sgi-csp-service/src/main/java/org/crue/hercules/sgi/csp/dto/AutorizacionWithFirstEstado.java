package org.crue.hercules.sgi.csp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AutorizacionWithFirstEstado extends AutorizacionOutput {

  /** Fecha First Estado Autorizacion */
  private Instant fechaFirstEstado;

  public AutorizacionWithFirstEstado(Long id, String observaciones, String responsableRef,
      String tituloProyecto, String entidadRef, String solicitanteRef,
      Integer horasDedicacion, String datosResponsable, String datosEntidad,
      String datosConvocatoria, Long convocatoriaId, Long estadoId, Instant fechaFirstEstado) {

    super(id, observaciones, responsableRef, solicitanteRef, tituloProyecto,
        entidadRef, horasDedicacion, datosResponsable, datosEntidad,
        datosConvocatoria, convocatoriaId, estadoId);

    this.fechaFirstEstado = fechaFirstEstado;
  }

}
