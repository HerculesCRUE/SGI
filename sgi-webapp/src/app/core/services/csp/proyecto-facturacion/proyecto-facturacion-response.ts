import { IEstadoValidacionIP } from '@core/models/csp/estado-validacion-ip';
import { ITipoFacturacion } from '@core/models/csp/tipo-facturacion';

export interface IProyectoFacturacionResponse {
  id: number;
  comentario: string;
  fechaConformidad: string;
  fechaEmision: string;
  importeBase: number;
  numeroPrevision: number;
  porcentajeIVA: number;
  proyectoId: number;
  tipoFacturacion: ITipoFacturacion;
  estadoValidacionIP: IEstadoValidacionIP;
  proyectoProrrogaId: number;
  proyectoSgeRef: string;
}
