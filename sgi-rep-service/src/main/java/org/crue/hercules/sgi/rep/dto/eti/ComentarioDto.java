package org.crue.hercules.sgi.rep.dto.eti;

import java.util.List;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
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
public class ComentarioDto extends BaseRestDto {

  private MemoriaDto memoria;
  private ApartadoDto apartado;
  private EvaluacionDto evaluacion;
  private TipoComentarioDto tipoComentario;
  private List<I18nFieldValueDto> texto;
  private String createdBy;
  private String personaCreated;
  private String sexoPersonaCreated;
}