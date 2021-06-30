import { DateTime } from "luxon";

export interface IProyectoIVA {
  id: number;
  proyectoId: number;
  iva: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
}