import { TipoEstadoValidacion } from '@core/models/csp/estado-validacion-ip';

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
  proyectoProrrogaId: number;
  proyectoSgeRef: string;
}
