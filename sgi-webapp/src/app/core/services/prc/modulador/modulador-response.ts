import { Tipo } from '@core/models/prc/modulador';

export interface IModuladorResponse {
  id: number;
  areaRef: string;
  tipo: Tipo;
  valor1: number;
  valor2: number;
  valor3: number;
  valor4: number;
  valor5: number;
  convocatoriaBaremacionId: number;
}
