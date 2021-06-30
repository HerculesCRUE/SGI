import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Clase para manejar los formGroups de los formularios
 */
export class FormGroupUtil {
  private static errorFormGroup = 'No existe el formGroup';
  private static errorFormControl = 'No existe el formControl';

  /**
   * Comprueba que todos los datos de un formGroup son válidos.
   * Si no hay fallos devuelve true.
   * En caso contrario, marca todos los campos con errores y devuelve false.
   *
   * @param formGroup FormGroup a comprobar
   *
   * @returns si contiene errores el formulario o no
   */
  static valid(formGroup: FormGroup): boolean {
    return this.getNumErrors(formGroup) === 0;
  }

  /**
   * Devuelve el número de errores que contiene un formGroup
   *
   * @param formGroup FormGroup a comprobar
   *
   * @returns Número de errores
   */
  static getNumErrors(formGroup: FormGroup): number {
    let errors = 0;
    if (formGroup) {
      const list = Object.keys(formGroup.controls);
      list.forEach((key) => {
        const abstractControl = formGroup.get(key);
        if (this.getError(formGroup, key) != null) {
          errors++;
          abstractControl.markAllAsTouched();
        }
      });
      return errors;
    } else {
      throw new Error(this.errorFormGroup);
    }
  }

  /**
   * Comprueba si un dato de un formGroup es inválido y
   * ha sido modificado por el persona.
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   *
   * @returns Si hay errores o no
   */
  static checkError(formGroup: FormGroup, key: string): boolean {
    if (formGroup) {
      const abstractControl = formGroup.get(key);
      if (abstractControl) {
        return (
          abstractControl.invalid &&
          (abstractControl.dirty || abstractControl.touched)
        );
      }
      throw new Error(this.getErrorKey(key));
    } else {
      throw new Error(this.errorFormGroup);
    }
  }

  /**
   * Devuelve los errores de un dato concreto de un formGroup
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   *
   * @returns los errores que contenga el dato
   */
  static getError(formGroup: FormGroup, key: string): ValidationErrors {
    if (formGroup) {
      const abstractControl = formGroup.get(key);
      if (abstractControl) {
        return abstractControl.errors;
      }
      throw new Error(this.getErrorKey(key));
    } else {
      throw new Error(this.errorFormGroup);
    }
  }

  /**
   * Devuelve el valor de un dato concreto de un formGroup.
   * Si no existe el dato devuelve null
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   *
   * @returns El dato concreto solicitado
   */
  static getValue(formGroup: FormGroup, key: string): any {
    if (formGroup) {
      const abstractControl = formGroup.get(key);
      if (abstractControl) {
        return abstractControl.value;
      }
      throw new Error(this.getErrorKey(key));
    } else {
      throw new Error(this.errorFormGroup);
    }
  }

  /**
   * Cambia el valor de un dato concreto de un formGroup
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   * @param value Nuevo valor
   */
  static setValue(formGroup: FormGroup, key: string, value: any): void {
    if (formGroup) {
      const abstractControl = formGroup.get(key);
      if (abstractControl) {
        abstractControl.setValue(value);
      } else {
        throw new Error(this.getErrorKey(key));
      }
    } else {
      throw new Error(this.errorFormGroup);
    }
  }

  /**
   * Cambia los validadores a un dato concreto de un formGroup
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   * @param validator Lista de validadores
   * @param initValue Valor inicial del dato
   */
  static changeValidator(formGroup: FormGroup, key: string, validator: ValidatorFn[], initValue?: any): void {
    if (formGroup) {
      const abstractControl = formGroup.get(key);
      if (abstractControl) {
        formGroup.setControl(key, new FormControl(initValue, validator));
      } else {
        throw new Error(this.getErrorKey(key));
      }
    } else {
      throw new Error(this.errorFormGroup);
    }
  }

  /**
   * Añade un nuevo dato a un formGroup
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   * @param formControl Nuevo valor
   */
  static addFormControl(formGroup: FormGroup, key: string, formControl: FormControl): void {
    if (formGroup) {
      if (formControl) {
        formGroup.addControl(key, formControl);
      } else {
        throw new Error(this.errorFormControl);
      }
    } else {
      throw new Error(this.errorFormGroup);
    }
  }

  /**
   * Limpia todos los campos del formGroup
   *
   * @param formGroup Formgroup
   */
  static clean(formGroup: FormGroup): void {
    if (formGroup) {
      const list = Object.keys(formGroup.controls);
      list.forEach((key) => {
        this.setValue(formGroup, key, '');
      });
    } else {
      throw new Error(this.errorFormGroup);
    }
  }

  /**
   * Devuelve el mensaje de error cuando no se encuentra un formControl
   *
   * @param key identificador del formControl
   */
  private static getErrorKey(key: string) {
    return `No existe el valor ${key} dentro del formGroup`;
  }

  /**
   * Comprueba si existe o no el validador indicado en el formGroup
   * 
   * @param formGroup FormGroup
   * @param key Identificador del campo del formulario
   * @param validator Validador a comprobar si existe
   * @return true/false
   */
  static hasValidator(formGroup: FormGroup, key: string, validator: string): boolean {
    const control = formGroup.get(key);
    if (control.validator instanceof Function) {
      const validators = control.validator(control);
      return !!(validators && validators.hasOwnProperty(validator));
    } else {
      return false;
    }
  }

}
