import { AbstractControl, FormGroup, ValidationErrors } from '@angular/forms';
import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { DateTime } from 'luxon';
import { IValidatorCompareToOptions } from './models/validator-compare-to-options';
import { IValidatorOptions } from './models/validator-options';
import { getOptionsCompareValues as getCompareValue } from './utils.validator';

export interface IDateValidatorOptions extends IValidatorOptions, Partial<Pick<IValidatorCompareToOptions, 'formStateProperty' | 'value'>> {
}

export interface IDateBetweenValidatorOptions extends IValidatorOptions, Partial<Pick<IValidatorCompareToOptions, 'formStateProperties' | 'values'>> {
}

export function dateIsAfter(
  control: AbstractControl,
  field: SgiFormlyFieldConfig,
  options: IDateValidatorOptions,
): ValidationErrors {
  let valueToCompare: DateTime | string = getCompareValue(field.options.formState, options);
  let controlValue: DateTime | string = options.errorPath ? control.value[options.errorPath] : control.value;

  if (control instanceof FormGroup && options.errorPath && options.compareTo === 'formStateProperty') {
    control.controls[options.errorPath].markAsTouched();
  }

  if (typeof valueToCompare === 'string') {
    valueToCompare = DateTime.fromISO(valueToCompare);
  }

  if (typeof controlValue === 'string') {
    controlValue = DateTime.fromISO(controlValue);
  }

  if (!valueToCompare || controlValue > valueToCompare) {
    return null;
  }

  return {
    'date-is-after': {
      message: options.message ? eval('`' + options.message + '`') : `La fecha debe de ser posterior al ${valueToCompare.toLocaleString(DateTime.DATE_SHORT)}`
    }
  };
}

export function dateIsBetween(
  control: AbstractControl,
  field: SgiFormlyFieldConfig,
  options: IDateBetweenValidatorOptions,
): ValidationErrors {
  let [minDate, maxDate]: (DateTime | string)[] = getCompareValue(field.options.formState, options);
  let controlValue: DateTime | string = options.errorPath ? control.value[options.errorPath] : control.value;

  if (typeof minDate === 'string') {
    minDate = DateTime.fromISO(minDate);
  }

  if (typeof maxDate === 'string') {
    maxDate = DateTime.fromISO(maxDate);
  }

  if (typeof controlValue === 'string') {
    controlValue = DateTime.fromISO(controlValue);
  }

  if (!minDate || !maxDate || (controlValue >= minDate && controlValue <= maxDate)) {
    return null;
  }

  return {
    'date-is-between': {
      message: options.message ? eval('`' + options.message + '`') : `La fecha debe de estar entre ${minDate.toLocaleString(DateTime.DATE_SHORT)} y ${maxDate.toLocaleString(DateTime.DATE_SHORT)}`
    }
  };
}
