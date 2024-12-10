import { AbstractControl, ValidationErrors } from '@angular/forms';
import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { IValidatorOptions } from './models/validator-options';

export function emailValidator(
  control: AbstractControl,
  field: SgiFormlyFieldConfig,
  options: IValidatorOptions,
): ValidationErrors {

  const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

  if (!control.value || emailPattern.test(control.value)) {
    return null;
  }

  return {
    'email': {
      message: options.message ? eval('`' + options.message + '`') : `El formato del email no es valido`
    }
  };
}
