import { ITipoFase } from '../tipos-configuracion';

export interface IConvocatoriaFaseBackend {
  id: number;
  convocatoriaId: number;
  tipoFase: ITipoFase;
  fechaInicio: string;
  fechaFin: string;
  observaciones: string;
}
