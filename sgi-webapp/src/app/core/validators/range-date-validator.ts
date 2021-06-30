import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { DateTime } from 'luxon';

export interface DateRange {
  fechaInicio: DateTime;
  fechaFin: DateTime;
}

export class RangeDateValidator {
  static notOverlaps(dateRanges: DateRange[]): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const date: DateTime = control.value;
      for (const dataRange of dateRanges) {
        if (dataRange.fechaInicio <= date && dataRange.fechaFin >= date) {
          return { overlaps: true } as ValidationErrors;
        }
      }
      return null;
    };
  }
}
