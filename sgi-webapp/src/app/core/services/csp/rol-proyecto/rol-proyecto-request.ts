import { Equipo, Orden } from "@core/models/csp/rol-proyecto";

export interface IRolProyectoRequest {
  nombre: string;
  abreviatura: string;
  descripcion: string;
  rolPrincipal: boolean;
  baremablePRC: boolean;
  orden: Orden;
  equipo: Equipo;
  activo: boolean;
}
