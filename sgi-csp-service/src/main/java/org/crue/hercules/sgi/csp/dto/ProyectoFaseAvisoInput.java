package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
public class ProyectoFaseAvisoInput implements Serializable {
  @NotNull
  private Instant fechaEnvio;
  @NotEmpty
  private String asunto;
  @NotEmpty
  private String contenido;
  @NotEmpty
  @Valid
  private List<Destinatario> destinatarios;
  @NotNull
  private Boolean incluirIpsProyecto;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Destinatario implements Serializable {
    @NotEmpty
    private String nombre;
    @NotEmpty
    @Email
    private String email;
  }
}
