import { FormlyFieldConfig } from '@ngx-formly/core';
import { DateTime } from 'luxon';
import { LuxonUtils } from './luxon-utils';
const ISO8601 = /^\d{4}-\d\d-\d\dT\d\d:\d\d:\d\d(\.\d+)?(([+-]\d\d:\d\d)|Z)?$/;

// Formly data types
enum DATE_TYPES {
  DATE_PICKER = 'datepicker',
  DATE_TIME_PICKER = 'dateTimePicker'
}

export class FormlyUtils {

  private constructor() { }

  public static convertFormlyToJSON(model: any, fields: FormlyFieldConfig[]) {
    this.convertTableOneElementToJSON(model, fields);
    this.convertDateTimeToString(model, fields);
  }

  public static convertJSONToFormly(model: any, fields: FormlyFieldConfig[]) {
    this.convertJSONToTableOneElement(model, fields);
    this.convertStringToDateTime(model);
  }

  private static convertJSONToTableOneElement(model: any, fields: FormlyFieldConfig[]) {
    const fieldsByType = this.getFieldsByType(fields, 'table-crud-one-element');

    this.parseTableOneElement(model, fieldsByType, false);
  }

  private static convertTableOneElementToJSON(model: any, fields: FormlyFieldConfig[]) {
    const fieldsByType = this.getFieldsByType(fields, 'table-crud-one-element');

    this.parseTableOneElement(model, fieldsByType, true);
  }

  /**
   * Parsea los elementos de tipo table-crud-one-element
   * @param model Modelo de datos
   * @param fieldsByType Listado de FormlyFieldConfig a modificar
   * @param toJSON Indica si convierte de JSON a TableOne o a la inversa
   * @returns model
   */
  private static parseTableOneElement(model: any, fieldsByType: FormlyFieldConfig[], toJSON: boolean) {
    if ((model === null || model === undefined) && typeof model !== 'object') {
      return model;
    }

    if (fieldsByType && fieldsByType.length > 0) {
      for (const key of Object.keys(model)) {
        const value = model[key];
        const indexFieldByType = fieldsByType.findIndex(field => field.key === key);
        const fieldTableOne = fieldsByType[indexFieldByType];

        if (indexFieldByType >= 0) {
          const arrTableOne = value as any[];
          const arrNewModel = [];
          if (arrTableOne && arrTableOne instanceof Array && arrTableOne.length > 0) {
            if (!toJSON) {
              const nameElement = fieldTableOne.fieldArray.fieldGroup[0].key as string;
              arrTableOne.forEach(element => arrNewModel.push({ [nameElement]: element }));
            } else {
              arrTableOne.forEach(element => arrNewModel.push(Object.values(element)));
            }
          }
          model[key] = arrNewModel;

          fieldsByType = fieldsByType.filter((arrayValue, index, arr) => index !== indexFieldByType);
          if (!fieldsByType || fieldsByType.length === 0) {
            return model;
          }
        } else if (value && typeof value === 'object' && !(value instanceof DateTime)) {
          this.parseTableOneElement(value, fieldsByType, toJSON);
        }
      }
    } else {
      return model;
    }
  }

  private static convertDateTimeToString(model: any, fields: FormlyFieldConfig[]) {
    const fieldsByType = [...this.getFieldsByType(fields, DATE_TYPES.DATE_PICKER),
    ...this.getFieldsByType(fields, DATE_TYPES.DATE_TIME_PICKER)];

    this.parseDateTimeToString(model, fieldsByType);
  }

  private static parseDateTimeToString(model: any, fieldsByType: FormlyFieldConfig[]) {
    if ((model === null || model === undefined) && typeof model !== 'object') {
      return model;
    }

    if (fieldsByType && fieldsByType.length > 0) {
      for (const key of Object.keys(model)) {
        const value = model[key];
        if (value) {
          if (value instanceof DateTime) {
            const indexFieldByType = fieldsByType.findIndex(field => field.key === key);
            if (indexFieldByType >= 0) {
              if (fieldsByType[indexFieldByType].type === DATE_TYPES.DATE_PICKER) {
                model[key] = LuxonUtils.toBackend(value, true);
              } else if (fieldsByType[indexFieldByType].type === DATE_TYPES.DATE_TIME_PICKER) {
                model[key] = LuxonUtils.toBackend(value);
              }
              fieldsByType = fieldsByType.filter((arrayValue, index, arr) => index !== indexFieldByType);
              if (!fieldsByType || fieldsByType.length === 0) {
                return model;
              }
            }
          } else if (typeof value === 'object') {
            this.parseDateTimeToString(value, fieldsByType);
          }
        }
      }
    } else {
      return model;
    }
  }


  private static convertStringToDateTime(model) {
    if ((model === null || model === undefined) && typeof model !== 'object') {
      return model;
    }

    for (const key of Object.keys(model)) {
      const value = model[key];
      if (value) {
        if (this.isIso8601(value)) {
          model[key] = LuxonUtils.fromBackend(value);
        } else if (typeof value === 'object') {
          this.convertStringToDateTime(value);
        }
      }
    }
  }

  private static isIso8601(value) {
    if (value === null || value === undefined) {
      return false;
    }

    return ISO8601.test(value);
  }

  private static getFieldsByType(formlyFieldConfig: FormlyFieldConfig[], type: string): FormlyFieldConfig[] {
    const fields: FormlyFieldConfig[] = [];

    if (formlyFieldConfig.length) {
      formlyFieldConfig.forEach(field => {
        if (field.type === type) {
          fields.push(field);
        }
        if (field.fieldGroup) {
          fields.push(...this.getFieldsByType(field.fieldGroup, type));
        }
      });
    }
    return fields;
  }
}
