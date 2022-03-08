package org.crue.hercules.sgi.prc.validation;

import java.time.Instant;
import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.math.NumberUtils;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.CampoProduccionCientificaInput;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo;
import org.crue.hercules.sgi.prc.model.ConfiguracionCampo.TipoFormato;
import org.crue.hercules.sgi.prc.repository.ConfiguracionCampoRepository;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class ValorFormatValidator implements ConstraintValidator<ValorFormat, CampoProduccionCientificaInput> {

  private ConfiguracionCampoRepository repository;

  public ValorFormatValidator(ConfiguracionCampoRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(CampoProduccionCientificaInput value, ConstraintValidatorContext context) {
    if (Objects.isNull(value) || Objects.isNull(value.getCodigoCVN()) || CollectionUtils.isEmpty(value.getValores())) {
      return true;
    }

    try {
      ConfiguracionCampo configuracionCampo = repository
          .findByCodigoCVN(CodigoCVN.getByInternValue(value.getCodigoCVN()))
          .orElse(null);
      if (Objects.isNull(configuracionCampo)) {
        return true;
      }

      boolean isValid = isValidValorFormat(configuracionCampo.getTipoFormato(), value);
      if (!isValid) {
        addEntityMessageParameter(value, configuracionCampo.getTipoFormato(), context);
      }
      return isValid;
    } catch (Exception e) {
      addEntityMessageParameter(value, null, context);
      return false;
    }
  }

  private boolean isValidValorFormat(TipoFormato tipoFormato, CampoProduccionCientificaInput value) {
    String valor = value.getValores().get(0);
    if (StringUtils.hasText(valor)) {
      switch (tipoFormato) {
        case FECHA:
          try {
            if (valor.matches("^(\\d{4})-(\\d{2})-(\\d{2})$")) {
              valor = valor + "T00:00:00Z";
            }
            Instant.parse(valor);
            return true;
          } catch (Exception e) {
            return false;
          }
        case NUMERO:
          return NumberUtils.isCreatable(valor) && valor.length() <= 18;
        case BOOLEANO:
          return valor.equalsIgnoreCase("true") || valor.equalsIgnoreCase("false");
        default:
          return true;
      }
    }
    return true;

  }

  private void addEntityMessageParameter(CampoProduccionCientificaInput value, TipoFormato tipoFormato,
      ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("codigoCVN", value.getCodigoCVN());
    hibernateContext.addMessageParameter("value", value.getValores().get(0));
    hibernateContext.addMessageParameter("format", null != tipoFormato ? tipoFormato : "");
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode("valores").addConstraintViolation();
  }
}
