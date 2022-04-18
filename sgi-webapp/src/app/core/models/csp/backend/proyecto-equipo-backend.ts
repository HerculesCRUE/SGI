import { IRolProyecto } from '../rol-proyecto';

export interface IProyectoEquipoBackend {
  id: number;
  proyectoId: number;
  rolProyecto: IRolProyecto;
  personaRef: string;
  fechaInicio: string;
  fechaFin: string;
}
