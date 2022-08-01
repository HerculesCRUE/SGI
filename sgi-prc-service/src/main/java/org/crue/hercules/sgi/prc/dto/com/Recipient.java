package org.crue.hercules.sgi.prc.dto.com;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/*Recipient del API del módulo de Comunicados*/
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Recipient implements Serializable {

  /** Id */
  private String name;

  /** Referencia usuario */
  private String address;

}
