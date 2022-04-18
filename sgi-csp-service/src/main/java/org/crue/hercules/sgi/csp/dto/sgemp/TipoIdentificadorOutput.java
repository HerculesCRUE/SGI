package org.crue.hercules.sgi.csp.dto.sgemp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoIdentificadorOutput implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  String id;
  String nombre;
}
