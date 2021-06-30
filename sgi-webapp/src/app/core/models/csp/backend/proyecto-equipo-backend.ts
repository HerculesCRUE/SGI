import { IRolProyecto } from '../rol-proyecto';

export interface IProyectoEquipoBackend {
  id: number;
  proyectoId: number;
  rolProyecto: IRolProyecto;
  personaRef: string;
  horasDedicacion: number;
  fechaInicio: string;
  fechaFin: string;
}
