import { AbstractControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { IValidationError } from './models/validation-error';
import { IValidatorCompareToOptions } from './models/validator-compare-to-options';
import { IValidatorOptions } from './models/validator-options';
import { getOptionsCompareValues as getCompareValue } from './utils.validator';

const MSG_FORMLY_VALIDATIONS_DATE_IS_AFTER = marker('msg.formly.validations.date-is-after');
const MSG_FORMLY_VALIDATIONS_DATE_IS_BETWEEN = marker('msg.formly.validations.date-is-between');

export interface IDateValidatorOptions extends IValidatorOptions, Partial<Pick<IValidatorCompareToOptions, 'formStateProperty' | 'value'>> {
}

export interface IDateBetweenValidatorOptions extends IValidatorOptions, Partial<Pick<IValidatorCompareToOptions, 'formStateProperties' | 'values'>> {
}

export function dateIsAfter(
  control: AbstractControl,
  field: SgiFormlyFieldConfig,
  options: IDateValidatorOptions,
  translate: TranslateService
): IValidationError {
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
    name: 'date-is-after',
    customMessage: options.message ? eval('`' + options.message + '`') : null,
    defatultMessage: translate.instant(
      MSG_FORMLY_VALIDATIONS_DATE_IS_AFTER,
      {
        min: valueToCompare.setLocale(translate.currentLang).toLocaleString(DateTime.DATE_SHORT)
      }
    )
  };
}

export function dateIsBetween(
  control: AbstractControl,
  field: SgiFormlyFieldConfig,
  options: IDateBetweenValidatorOptions,
  translate: TranslateService
): IValidationError {
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
    name: 'date-is-between',
    customMessage: options.message ? eval('`' + options.message + '`') : null,
    defatultMessage: translate.instant(
      MSG_FORMLY_VALIDATIONS_DATE_IS_BETWEEN,
      {
        min: minDate.setLocale(translate.currentLang).toLocaleString(DateTime.DATE_SHORT),
        max: maxDate.setLocale(translate.currentLang).toLocaleString(DateTime.DATE_SHORT)
      }
    )
  };
}
