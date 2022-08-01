package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.SolicitanteExterno;

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
public class SolicitanteExternoInput implements Serializable {

  @NotNull
  private Long solicitudId;

  @Size(max = SolicitanteExterno.NOMBRE_LENGTH)
  @NotBlank
  private String nombre;

  @Size(max = SolicitanteExterno.APELLIDOS_LENGTH)
  @NotBlank
  private String apellidos;

  @Size(max = SolicitanteExterno.ENTIDAD_REF_LENGTH)
  @NotBlank
  private String tipoDocumentoRef;

  @Size(max = SolicitanteExterno.NUMERO_DOCUMENTO_LENGTH)
  @NotBlank
  private String numeroDocumento;

  @Size(max = SolicitanteExterno.ENTIDAD_REF_LENGTH)
  private String sexoRef;

  private Instant fechaNacimiento;

  @Size(max = SolicitanteExterno.ENTIDAD_REF_LENGTH)
  private String paisNacimientoRef;

  @Size(max = SolicitanteExterno.TELEFONO_LENGTH)
  private String telefono;

  @Size(max = SolicitanteExterno.EMAIL_LENGTH)
  private String email;

  @Size(max = SolicitanteExterno.DIRECCION_LENGTH)
  private String direccion;

  @Size(max = SolicitanteExterno.ENTIDAD_REF_LENGTH)
  private String paisContactoRef;

  @Size(max = SolicitanteExterno.ENTIDAD_REF_LENGTH)
  private String comunidadRef;

  @Size(max = SolicitanteExterno.ENTIDAD_REF_LENGTH)
  private String provinciaRef;

  @Size(max = SolicitanteExterno.CIUDAD_LENGTH)
  private String ciudad;

  @Size(max = SolicitanteExterno.CODIGO_POSTAL_LENGTH)
  private String codigoPostal;

}
