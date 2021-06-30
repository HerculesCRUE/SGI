package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaRetrospectivaFormulario implements Serializable {
  private static final long serialVersionUID = 8454547215344558766L;
  public String evaluacionRetrospectivaRadio;
  public List<String> motivoEvaluacionRetrospectivaCheck;
  public Date fechaEvRetrospectiva;
  public String especificarMotivo;
}
