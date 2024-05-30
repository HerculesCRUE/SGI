package org.crue.hercules.sgi.csp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.Configuracion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.repository.ConfiguracionRepository;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConvocatoriaPartidaCodigoOrPartidaRefRequiredValidator
    implements ConstraintValidator<ConvocatoriaPartidaCodigoOrPartidaRefRequired, ConvocatoriaPartida> {

  private final ConfiguracionRepository configuracionRepository;

  @Override
  public boolean isValid(ConvocatoriaPartida value, ConstraintValidatorContext context) {

    if (value == null) {
      return false;
    }

    Configuracion configuracion = configuracionRepository.findFirstByOrderByIdAsc().orElse(null);
    if (configuracion == null) {
      return false;
    }

    boolean partidasPresupuestariasSGE = configuracion.getPartidasPresupuestariasSGE();

    return (partidasPresupuestariasSGE && StringUtils.hasText(value.getPartidaRef()))
        || (!partidasPresupuestariasSGE && StringUtils.hasText(value.getCodigo()));
  }

}