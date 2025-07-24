import { AbstractControl } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { TranslateService } from '@ngx-translate/core';
import { IValidationError } from './models/validation-error';
import { IValidatorOptions } from './models/validator-options';

const MSG_FORMLY_VALIDATIONS_EMAIL_PRINCIPAL_UNIQUE = marker('msg.formly.validations.email-principal-unique');
const MSG_FORMLY_VALIDATIONS_EMAIL_PRINCIPAL_REQUIRED = marker('msg.formly.validations.email-principal-required');

export interface IEmailPrincipalUniqueValidatorOptions extends IValidatorOptions {
  messagePrincipalUnique: string;
  messagePrincipalRequired: string;
}

export function emailPrincipalUniqueValidator(
  control: AbstractControl,
  field: SgiFormlyFieldConfig,
  options: IEmailPrincipalUniqueValidatorOptions,
  translate: TranslateService
): IValidationError {
  const controlValues: any[] = control.value ?? [];

  if (controlValues.length && controlValues.filter(c => c.principal).length === 1) {
    return;
  }

  let customMessage: string;
  let defatultMessage: string;
  if (controlValues.some(c => c.principal)) {
    customMessage = options.messagePrincipalUnique ? eval('`' + options.messagePrincipalUnique + '`') : null;
    defatultMessage = MSG_FORMLY_VALIDATIONS_EMAIL_PRINCIPAL_UNIQUE;
  } else {
    customMessage = options.messagePrincipalRequired ? eval('`' + options.messagePrincipalRequired + '`') : null;
    defatultMessage = MSG_FORMLY_VALIDATIONS_EMAIL_PRINCIPAL_REQUIRED;
  }

  return {
    name: 'email-principal-unique',
    customMessage: customMessage,
    defatultMessage: defatultMessage
  }
}
