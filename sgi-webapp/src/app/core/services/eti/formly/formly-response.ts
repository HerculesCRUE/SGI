import { FormlyFieldConfig } from '@ngx-formly/core';

export interface IFormlyResponse {
  id: number;
  nombre: string;
  version: number;
  esquema: FormlyFieldConfig[];
}
