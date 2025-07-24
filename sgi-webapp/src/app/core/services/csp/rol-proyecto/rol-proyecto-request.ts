import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";
import { Equipo, Orden } from "@core/models/csp/rol-proyecto";

export interface IRolProyectoRequest {
  nombre: I18nFieldValueRequest[];
  abreviatura: I18nFieldValueRequest[];
  descripcion: I18nFieldValueRequest[];
  rolPrincipal: boolean;
  baremablePRC: boolean;
  orden: Orden;
  equipo: Equipo;
  activo: boolean;
}
