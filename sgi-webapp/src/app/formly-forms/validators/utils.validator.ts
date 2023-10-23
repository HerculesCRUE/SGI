import { FormArray, FormGroup, ValidationErrors } from '@angular/forms';
import { IValidatorOptions } from './models/validator-options';
import { IValidatorCompareToOptions } from './models/validator-compare-to-options';


export function requiredChecked(formGroup: FormGroup): ValidationErrors {
  let checked = 0;
  Object.keys(formGroup.controls).forEach(key => {
    const control = formGroup.controls[key];
    if (!control.disabled && control.value === true) {
      checked++;
    }
  });
  if (checked < 1) {
    return null;
  }
  return {
    required: true,
  };
}

export function requiredRowTable(formArray: FormArray): ValidationErrors {
  return !formArray.value || formArray.length === 0 ? { oneRowRequired: true } : null;
}

export function getOptionsCompareValues(formState: any, options: IValidatorOptions & Partial<IValidatorCompareToOptions>): any {

  if (!options?.compareTo) {
    return null;
  }

  let returnValue;

  switch (options.compareTo) {
    case 'formStateProperties':
      returnValue = options.formStateProperties.map(property => getFormStateProperty(formState, property?.split('.')));
      break;
    case 'formStateProperty':
      returnValue = getFormStateProperty(formState, options.formStateProperty?.split('.'));
      break;
    case 'value':
      returnValue = options.value;
      break;
    case 'values':
      returnValue = options.values;
      break;
    default:
      console.error('Not valid options.compareTo', options.compareTo);
      returnValue = null;
      break;
  }

  return returnValue;
}

function getFormStateProperty(formState: any, propertyPath: string[]): any {
  if (!formState || !propertyPath?.length) {
    return null;
  }

  const currentPath = propertyPath.shift();

  if (propertyPath.length > 0) {
    return getFormStateProperty(formState[currentPath], propertyPath);
  }

  return formState[currentPath];
}
