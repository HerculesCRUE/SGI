import { DateTime } from 'luxon';
import { IEstadoValidacionIP } from './estado-validacion-ip';
import { ITipoFacturacion } from './tipo-facturacion';

export interface IProyectoFacturacion {
  id: number;
  comentario: string;
  fechaConformidad: DateTime;
  fechaEmision: DateTime;
  importeBase: number;
  numeroPrevision: number;
  porcentajeIVA: number;
  proyectoId: number;
  tipoFacturacion: ITipoFacturacion;
  estadoValidacionIP: IEstadoValidacionIP;
}
