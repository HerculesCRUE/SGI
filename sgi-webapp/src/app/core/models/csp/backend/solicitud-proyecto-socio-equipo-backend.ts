import { IRolProyecto } from '../rol-proyecto';

export interface ISolicitudProyectoSocioEquipoBackend {
  id: number;
  solicitudProyectoSocioId: number;
  personaRef: string;
  rolProyecto: IRolProyecto;
  mesInicio: number;
  mesFin: number;
}
