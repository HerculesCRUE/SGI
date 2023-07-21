import { Orden, Equipo } from "@core/models/csp/rol-proyecto";

export interface IRolProyectoResponse {
  id: number;
  nombre: string;
  abreviatura: string;
  descripcion: string;
  rolPrincipal: boolean;
  baremablePRC: boolean;
  orden: Orden;
  equipo: Equipo;
  activo: boolean;
}
