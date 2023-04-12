import { IComentario } from '@core/models/eti/comentario';
import { Group } from '@core/services/action-service';
import { FormlyFieldConfig, FormlyTemplateOptions } from '@ngx-formly/core';

export interface SgiFormlyFieldConfig extends FormlyFieldConfig {
  group?: Group;
  templateOptions?: SgiFormlyTemplateOptions;
}

export interface SgiFormlyTemplateOptions extends FormlyTemplateOptions {
  comentario?: IComentario;
  locked?: boolean;
  modified?: boolean;
}
