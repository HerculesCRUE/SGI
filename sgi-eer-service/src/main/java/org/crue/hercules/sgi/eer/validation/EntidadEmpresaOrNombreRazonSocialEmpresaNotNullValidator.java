package org.crue.hercules.sgi.eer.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.eer.model.Empresa;

public class EntidadEmpresaOrNombreRazonSocialEmpresaNotNullValidator
    implements ConstraintValidator<EntidadEmpresaOrNombreRazonSocialEmpresaNotNull, Empresa> {

  @Override
  public boolean isValid(Empresa value, ConstraintValidatorContext context) {

    if (value == null) {
      return false;
    }

    if (value.getEntidadRef() != null && value.getNombreRazonSocial() != null) {
      return false;
    }

    return (value.getEntidadRef() != null || value.getNombreRazonSocial() != null);

  }

}