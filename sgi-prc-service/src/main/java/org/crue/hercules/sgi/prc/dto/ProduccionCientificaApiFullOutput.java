package org.crue.hercules.sgi.prc.dto;

import java.util.List;

import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AcreditacionInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AutorInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.CampoProduccionCientificaInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.IndiceImpactoInput;

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
public class ProduccionCientificaApiFullOutput extends ProduccionCientificaApiOutput {

  private List<CampoProduccionCientificaInput> campos;
  private List<AutorInput> autores;
  private List<IndiceImpactoInput> indicesImpacto;
  private List<AcreditacionInput> acreditaciones;
  private List<Long> proyectos;

}
