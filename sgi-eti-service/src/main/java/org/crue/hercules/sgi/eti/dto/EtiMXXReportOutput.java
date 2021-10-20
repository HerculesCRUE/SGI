package org.crue.hercules.sgi.eti.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtiMXXReportOutput {
  private String titulo;
  private List<BloqueOutput> bloques;
}
