import { FormArray, FormGroup, ValidationErrors } from '@angular/forms';

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
