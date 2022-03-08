package org.crue.hercules.sgi.prc.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AutorInput;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

public class FirmaOrPersonaRefOrNombreAndApellidosAutorValidator
    implements ConstraintValidator<FirmaOrPersonaRefOrNombreAndApellidosAutor, AutorInput> {

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(AutorInput value, ConstraintValidatorContext context) {
    if (value == null) {
      return false;
    }

    return isValidAutor(value);
  }

  private boolean isValidAutor(AutorInput value) {
    // Si no viene ninguno de los campos informado
    // Si vienen vacíos firma y personaRef y nombre o apellidos
    if (!StringUtils.hasText(value.getFirma()) &&
        !StringUtils.hasText(value.getPersonaRef()) &&
        (!StringUtils.hasText(value.getNombre()) ||
            !StringUtils.hasText(value.getApellidos()))) {
      return false;
    }

    // Si viene informado o firma o personaRef o nombre y apellidos
    return StringUtils.hasText(value.getFirma()) ||
        StringUtils.hasText(value.getPersonaRef()) ||
        (StringUtils.hasText(value.getNombre()) && StringUtils.hasText(value.getApellidos()));
  }
}
