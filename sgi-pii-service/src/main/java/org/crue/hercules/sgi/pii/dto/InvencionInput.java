package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.model.Invencion;
import org.springframework.format.annotation.DateTimeFormat;

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
public class InvencionInput implements Serializable {
  @NotEmpty
  @Size(max = Invencion.TITULO_LENGTH)
  private String titulo;

  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Instant fechaComunicacion;

  @NotEmpty
  @Size(max = Invencion.LONG_TEXT_LENGTH)
  private String descripcion;

  @Size(max = Invencion.LONG_TEXT_LENGTH)
  private String comentarios;

  private String proyectoRef;

  @NotNull
  private Long tipoProteccionId;
}
