package org.crue.hercules.sgi.rep.dto.eti;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BloqueNombreDto implements Serializable {
  private static final long serialVersionUID = 1L;

  private String lang;
  private String value;
}
