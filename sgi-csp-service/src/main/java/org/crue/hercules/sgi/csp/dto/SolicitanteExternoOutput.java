package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;

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
public class SolicitanteExternoOutput implements Serializable {
  private Long id;
  private Long solicitudId;
  private String nombre;
  private String apellidos;
  private String tipoDocumentoRef;
  private String numeroDocumento;
  private String sexoRef;
  private Instant fechaNacimiento;
  private String paisNacimientoRef;
  private String telefono;
  private String email;
  private String direccion;
  private String paisContactoRef;
  private String comunidadRef;
  private String provinciaRef;
  private String ciudad;
  private String codigoPostal;
  private String solicitudUUID;
}
