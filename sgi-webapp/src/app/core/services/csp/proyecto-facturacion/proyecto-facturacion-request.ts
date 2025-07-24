import { I18nFieldValueRequest } from '@core/i18n/i18n-field-request';
import { TipoEstadoValidacion } from '@core/models/csp/estado-validacion-ip';

export interface IProyectoFacturacionRequest {
  comentario: I18nFieldValueRequest[];
  estadoValidacionIP: {
    comentario: I18nFieldValueRequest[];
    estado: TipoEstadoValidacion;
    id: number;
  }
  fechaConformidad: string;
  fechaEmision: string;
  importeBase: number;
  numeroFacturaSge: string;
  numeroPrevision: number;
  porcentajeIVA: number;
  proyectoId: number;
  proyectoProrrogaId: number;
  proyectoSgeRef: string;
  tipoFacturacionId: number;
}
