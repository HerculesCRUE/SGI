import { IEstadoValidacionIP, TipoEstadoValidacion } from '@core/models/csp/estado-validacion-ip';
import { DateTime } from 'luxon';

export interface IProyectoFacturacionRequest {
  comentario: string;
  fechaConformidad: string;
  fechaEmision: string;
  importeBase: number;
  numeroPrevision: number;
  porcentajeIVA: number;
  proyectoId: number;
  tipoFacturacionId: number;
  estadoValidacionIP: {
    id: number;
    comentario: string;
    estado: TipoEstadoValidacion;
  }
}
