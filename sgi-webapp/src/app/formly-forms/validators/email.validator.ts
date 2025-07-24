import { AbstractControl } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { TranslateService } from '@ngx-translate/core';
import { IValidationError } from './models/validation-error';
import { IValidatorOptions } from './models/validator-options';

const MSG_FORMLY_VALIDATIONS_EMAIL = marker('msg.formly.validations.email');

export function emailValidator(
  control: AbstractControl,
  field: SgiFormlyFieldConfig,
  options: IValidatorOptions,
  translate: TranslateService
): IValidationError {

  const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

  if (!control.value || emailPattern.test(control.value)) {
    return null;
  }

  return {
    name: 'email',
    customMessage: options.message ? eval('`' + options.message + '`') : null,
    defatultMessage: translate.instant(MSG_FORMLY_VALIDATIONS_EMAIL)
  };
}
