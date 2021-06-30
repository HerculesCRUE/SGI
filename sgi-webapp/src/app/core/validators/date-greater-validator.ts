import { AbstractControl, ValidatorFn, FormControl, FormGroup, ValidationErrors } from '@angular/forms';
import { fcall } from 'q';



/**
 * Comprueba que la primera fecha es anterior a la segunda.
 *
 * @param fechaAnterior Nombre del campo contra el que se quiere hacer la validacion.
 * @param fechaPosterior Nombre del campo que se quiere validar.
 * @param fechaAnteriorObligatoria indica si es obligatoria la fechaanterior para que sea valida la fechaposteior
 * (opcional por defecto true)
 */
export function DateGreatValidator(horaInicio: string, horaFin: string, minutoInicio: string, minutoFin: string): ValidatorFn {
  return (formGroup: FormGroup): ValidationErrors | null => {
    const horaInicioControl = formGroup.controls[horaInicio];
    const horaFinControl = formGroup.controls[horaFin];

    const minInicioControl = formGroup.controls[minutoInicio];
    const minFinControl = formGroup.controls[minutoFin];

    if ((!horaInicioControl.value && horaInicioControl.value !== 0)
      || (!horaFinControl.value && horaFinControl.value !== 0)
      || (!minInicioControl.value && minInicioControl.value !== 0)
      || (!minFinControl.value && minFinControl.value !== 0)) {
      return;
    }
    if (horaInicioControl.value > horaFinControl.value) {
      establecerErrores(horaInicioControl, horaFinControl, minInicioControl, minFinControl);
    } else if (horaInicioControl.value === horaFinControl.value && minInicioControl.value >= minFinControl.value) {
      establecerErrores(horaInicioControl, horaFinControl, minInicioControl, minFinControl);
    } else {
      limpiarErrores(horaInicioControl, horaFinControl, minInicioControl, minFinControl);
    }
    return null;
  };
}


function establecerErrores(horaInicioControl, horaFinControl, minInicioControl, minFinControl) {
  horaInicioControl.setErrors({ horaIncorrecta: true });
  horaFinControl.setErrors({ horaIncorrecta: true });

  minInicioControl.setErrors({ horaIncorrecta: true });
  minFinControl.setErrors({ horaIncorrecta: true });

}


function limpiarErrores(horaInicioControl, horaFinControl, minInicioControl, minFinControl) {
  if (horaInicioControl.errors) {
    delete horaInicioControl.errors['horaIncorrecta'];
    horaInicioControl.updateValueAndValidity({ onlySelf: true });
  }
  if (horaFinControl.errors) {
    delete horaFinControl.errors['horaIncorrecta'];
    horaFinControl.updateValueAndValidity({ onlySelf: true });
  }
  if (minInicioControl.errors) {
    delete minInicioControl.errors['horaIncorrecta'];
    minInicioControl.updateValueAndValidity({ onlySelf: true });
  }
  if (minFinControl.errors) {
    delete minFinControl.errors['horaIncorrecta'];
    minFinControl.updateValueAndValidity({ onlySelf: true });
  }
}
