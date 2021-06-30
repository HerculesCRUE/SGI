import { FormlyFieldConfig } from '@ngx-formly/core';

export interface IChecklistResponse {
  id: number;
  personaRef: string;
  formly: {
    id: number;
    esquema: FormlyFieldConfig[];
  };
  fechaCreacion: string;
  respuesta: {
    [name: string]: any;
  };
}
