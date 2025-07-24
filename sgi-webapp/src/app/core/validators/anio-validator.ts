import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function anioValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    // Si el valor es null o undefined, se considera válido
    if (value === null || value === undefined) {
      return null;
    }
    const isValid = /^\d{4}$/.test(value);
    return isValid ? null : { invalidAnio: true };
  };
}