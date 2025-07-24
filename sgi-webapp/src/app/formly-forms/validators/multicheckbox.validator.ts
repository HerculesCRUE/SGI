import { AbstractControl } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { IValidationError } from './models/validation-error';
import { IValidatorOptions } from './models/validator-options';

const MSG_FORMLY_VALIDATIONS_MULTICHECKBOX_RESTRICTED = marker('msg.formly.validations.multicheckbox-restricted');

export interface IMulticheckboxValidatorOptions extends IValidatorOptions {
  restrictions?: {
    notValidCombinations: {
      option: string,
      incompatibleOptions: string[]
    }[]
  }
}

interface IMulticheckboxOption {
  value: string,
  label: string
}

interface IIncompatibilities {
  option1: string;
  option2: string;
}

export function multicheckboxRestricted(
  control: AbstractControl,
  field: SgiFormlyFieldConfig,
  options: IMulticheckboxValidatorOptions,
  translate: TranslateService
): IValidationError {
  const controlValues: string[] = control.value ?? [];

  const incompatibility = getIncompatibleOptions([...controlValues], options);
  if (!incompatibility) {
    return;
  }

  const option1 = getDisplayValue(field.templateOptions.options, incompatibility.option1);
  const option2 = getDisplayValue(field.templateOptions.options, incompatibility.option2);

  return {
    name: 'multicheckbox-restricted',
    customMessage: options.message ? eval('`' + options.message + '`') : null,
    defatultMessage: translate.instant(
      MSG_FORMLY_VALIDATIONS_MULTICHECKBOX_RESTRICTED,
      {
        option1: option1,
        option2: option2
      }
    )
  };
}

function getIncompatibleOptions(controlValues: string[], options: IMulticheckboxValidatorOptions): IIncompatibilities | null {
  if (!controlValues?.length) {
    return null;
  }

  let currentControlValue = controlValues.shift();

  const incompatibleOptions = options.restrictions.notValidCombinations.find(c => c.option === currentControlValue)?.incompatibleOptions;
  const incompatibility = incompatibleOptions.find(option => controlValues.includes(option));
  if (!!incompatibility) {
    return {
      option1: currentControlValue,
      option2: incompatibility
    };
  }

  if (controlValues.length > 0) {
    return getIncompatibleOptions(controlValues, options);
  }

  return null;
}

function getDisplayValue(options: IMulticheckboxOption[] | Observable<IMulticheckboxOption[]>, optionValue: string): string {
  if (Array.isArray(options)) {
    return options.find(option => option.value === optionValue)?.label ?? optionValue;
  }
} 
