package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RespuestaRetrospectivaFormulario implements Serializable {
  private static final long serialVersionUID = 8454547215344558766L;

  private String evaluacionRetrospectivaRadio;
  private Date fechaEvRetrospectiva;
}
