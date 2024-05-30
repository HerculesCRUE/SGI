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
public class ConvocatoriaReunionDto extends BaseRestDto {

  private ComiteDto comite;
  private Instant fechaEvaluacion;
  private Instant fechaLimite;
  private Boolean videoconferencia;
  private String lugar;
  private String ordenDia;
  private Integer anio;
  private Long numeroActa;
  private TipoConvocatoriaReunionDto tipoConvocatoriaReunion;
  private Integer horaInicio;
  private Integer minutoInicio;
  private Integer horaInicioSegunda;
  private Integer minutoInicioSegunda;
  private Instant fechaEnvio;
  private Boolean activo;

}