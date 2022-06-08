import { TipoCuantia } from '@core/models/prc/baremo';

export interface IBaremoResponse {
  id: number;
  peso: number;
  puntos: number;
  cuantia: number;
  tipoCuantia: TipoCuantia;
  configuracionBaremoId: number;
  convocatoriaBaremacionId: number;
}
