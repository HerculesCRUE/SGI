package org.crue.hercules.sgi.eer.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad;

public class MiembroSociedadEmpresaOrMiembroSociedadPersonaEmpresaComposicionSociedadNotNullValidator
    implements
    ConstraintValidator<MiembroSociedadEmpresaOrMiembroSociedadPersonaEmpresaComposicionSociedadNotNull, EmpresaComposicionSociedad> {

  @Override
  public boolean isValid(EmpresaComposicionSociedad value, ConstraintValidatorContext context) {

    if (value == null) {
      return false;
    }

    if (value.getMiembroSociedadEmpresaRef() != null && value.getMiembroSociedadPersonaRef() != null) {
      return false;
    }

    return (value.getMiembroSociedadPersonaRef() != null || value.getMiembroSociedadEmpresaRef() != null);

  }

}