package org.crue.hercules.sgi.rep.dto.eti;

import java.time.Instant;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EvaluacionDto extends BaseRestDto {

  private MemoriaDto memoria;
  private ConvocatoriaReunionDto convocatoriaReunion;
  private TipoEvaluacionDto tipoEvaluacion;
  private DictamenDto dictamen;
  private Instant fechaDictamen;
  private Integer version;
  private Boolean esRevMinima;
  private String comentario;
  private Boolean activo;

}