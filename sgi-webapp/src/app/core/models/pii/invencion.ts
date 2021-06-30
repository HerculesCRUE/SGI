import { DateTime } from "luxon";
import { ITipoProteccion } from "./tipo-proteccion";

export interface IInvencion {
  id: number;
  titulo: string;
  fechaComunicacion: DateTime;
  descripcion: string;
  comentarios: string;
  tipoProteccion: ITipoProteccion;
  activo: boolean;
}