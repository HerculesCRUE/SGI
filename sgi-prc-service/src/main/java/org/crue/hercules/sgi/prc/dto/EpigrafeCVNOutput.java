package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;
import java.util.List;

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
public class EpigrafeCVNOutput implements Serializable {
  private String epigrafeCVN;
  private List<String> codigosCVN;
}
