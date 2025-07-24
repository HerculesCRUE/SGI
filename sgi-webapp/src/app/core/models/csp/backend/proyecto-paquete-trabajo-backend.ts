import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IProyectoPaqueteTrabajoBackend {
  /** Id */
  id: number;
  /** Id de Proyecto */
  proyectoId: number;
  /** nombre */
  nombre: string;
  /** fechaInicio */
  fechaInicio: string;
  /** fechaFin */
  fechaFin: string;
  /** personaMes */
  personaMes: number;
  /** descripcion */
  descripcion: I18nFieldValueResponse[];
}
