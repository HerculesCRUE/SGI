import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";
import { Tipo } from "@core/models/csp/proyecto-prorroga";


export interface IProyectoProrrogaResponse {
  id: number;
  proyectoId: number;
  numProrroga: number;
  fechaConcesion: string;
  tipo: Tipo;
  fechaFin: string;
  importe: number;
  observaciones: I18nFieldValueResponse[];
}
