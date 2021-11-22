import { ISectorAplicacion } from '@core/models/pii/sector-aplicacion';

export interface ISectorLicenciadoResponse {
  id: number;
  fechaInicioLicencia: string;
  fechaFinLicencia: string;
  invencionId: number;
  sectorAplicacion: ISectorAplicacion;
  contratoRef: string;
  paisRef: string;
  exclusividad: boolean;
}
