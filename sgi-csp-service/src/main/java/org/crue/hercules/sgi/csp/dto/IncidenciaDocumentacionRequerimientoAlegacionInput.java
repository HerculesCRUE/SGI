package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.BaseEntity;

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
public class IncidenciaDocumentacionRequerimientoAlegacionInput implements Serializable {
  @Size(max = BaseEntity.DEFAULT_LONG_TEXT_LENGTH)
  private String alegacion;
}
