package org.crue.hercules.sgi.com.dto.sgdoc;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document implements Serializable {

  private static final long serialVersionUID = 1L;

  private String documentoRef;

  private String nombre;

  private Integer version;

  private LocalDateTime fechaCreacion;

  private String tipo;

  private String autorRef;
}