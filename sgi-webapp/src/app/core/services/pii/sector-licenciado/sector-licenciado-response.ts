import { ISectorAplicacionResponse } from '../sector-aplicacion/sector-aplicacion-response';

export interface ISectorLicenciadoResponse {
  id: number;
  fechaInicioLicencia: string;
  fechaFinLicencia: string;
  invencionId: number;
  sectorAplicacion: ISectorAplicacionResponse;
  contratoRef: string;
  paisRef: string;
  exclusividad: boolean;
}
