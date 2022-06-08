import { TipoCuantia } from '@core/models/prc/baremo';

export interface IBaremoRequest {
  peso: number;
  puntos: number;
  cuantia: number;
  tipoCuantia: TipoCuantia;
  configuracionBaremoId: number;
  convocatoriaBaremacionId: number;
}
