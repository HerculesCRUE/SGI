import { IRolProyecto } from '../rol-proyecto';

export interface IProyectoSocioEquipoBackend {
  id: number;
  proyectoSocioId: number;
  rolProyecto: IRolProyecto;
  personaRef: string;
  fechaInicio: string;
  fechaFin: string;
}
