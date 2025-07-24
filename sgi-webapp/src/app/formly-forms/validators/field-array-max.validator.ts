import { AbstractControl } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { TranslateService } from '@ngx-translate/core';
import { IValidationError } from './models/validation-error';
import { IValidatorOptions } from './models/validator-options';

const MSG_FORMLY_VALIDATIONS_FIELD_ARRAY_MAX = marker('msg.formly.validations.field-array-max');

export interface IFieldArrayMaxValidatorOptions extends IValidatorOptions {
  max: number;
}

export function fieldArrayMax(
  control: AbstractControl,
  field: SgiFormlyFieldConfig,
  options: IFieldArrayMaxValidatorOptions,
  translate: TranslateService
): IValidationError {
  const controlValues: any[] = control.value ?? [];
  const max = options.max;

  if (controlValues.length <= max) {
    return;
  }

  return {
    name: 'field-array-max',
    customMessage: options.message ? eval('`' + options.message.replace('{{max}}', max.toString()) + '`') : null,
    defatultMessage: translate.instant(
      MSG_FORMLY_VALIDATIONS_FIELD_ARRAY_MAX,
      {
        max: max
      }
    )
  };
}
