import { FormlyFieldConfig } from '@ngx-formly/core';

export interface IFormly {
  id: number;
  nombre: string;
  version: number;
  esquema: FormlyFieldConfig[];
}
