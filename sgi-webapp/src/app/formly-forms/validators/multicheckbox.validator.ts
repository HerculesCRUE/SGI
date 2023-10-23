import { AbstractControl, ValidationErrors } from '@angular/forms';
import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { Observable } from 'rxjs';
import { IValidatorOptions } from './models/validator-options';

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
): ValidationErrors {
  const controlValues: string[] = control.value ?? [];

  const incompatibility = getIncompatibleOptions([...controlValues], options);
  if (!incompatibility) {
    return;
  }

  const option1 = getDisplayValue(field.templateOptions.options, incompatibility.option1);
  const option2 = getDisplayValue(field.templateOptions.options, incompatibility.option2);

  return {
    'multicheckbox-restricted': {
      message: options.message ? eval('`' + options.message + '`') : `Las respuestas \"${option1}\" y \"${option2}\" no son compatibles`
    }
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
