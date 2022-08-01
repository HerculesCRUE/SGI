package org.crue.hercules.sgi.prc.dto.com;

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
public class PrcComValidarItemData implements Serializable {
  private String nombreEpigrafe;
  private String tituloItem;
  private Instant fechaItem;
}
