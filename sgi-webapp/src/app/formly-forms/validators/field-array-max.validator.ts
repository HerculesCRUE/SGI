import { AbstractControl, ValidationErrors } from '@angular/forms';
import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { IValidatorOptions } from './models/validator-options';

export interface IFieldArrayMaxValidatorOptions extends IValidatorOptions {
  max: number;
}

export function fieldArrayMax(
  control: AbstractControl,
  field: SgiFormlyFieldConfig,
  options: IFieldArrayMaxValidatorOptions,
): ValidationErrors {
  const controlValues: any[] = control.value ?? [];
  const max = options.max;

  if (controlValues.length <= max) {
    return;
  }

  return {
    'field-array-max': {
      message: options.message ? eval('`' + options.message.replace('{{max}}', max.toString()) + '`') : `El número maximo de elementos es \"${max}\"`
    }
  };
}
