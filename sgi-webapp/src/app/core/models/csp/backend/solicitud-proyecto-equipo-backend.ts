import { IRolProyecto } from '../rol-proyecto';

export interface ISolicitudProyectoEquipoBackend {
  id: number;
  solicitudProyectoId: number;
  personaRef: string;
  rolProyecto: IRolProyecto;
  mesInicio: number;
  mesFin: number;
}
