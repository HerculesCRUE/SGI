import { IRequerimientoJustificacion } from './requerimiento-justificacion';

export interface IIncidenciaDocumentacionRequerimiento {
  id: number;
  requerimientoJustificacion: IRequerimientoJustificacion;
  nombreDocumento: string;
  incidencia: string;
  alegacion: string;
}
