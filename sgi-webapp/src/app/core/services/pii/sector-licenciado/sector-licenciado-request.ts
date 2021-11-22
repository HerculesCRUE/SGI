export interface ISectorLicenciadoRequest {
  fechaInicioLicencia: string;
  fechaFinLicencia: string;
  invencionId: number;
  sectorAplicacionId: number;
  contratoRef: string;
  paisRef: string;
  exclusividad: boolean;
}
