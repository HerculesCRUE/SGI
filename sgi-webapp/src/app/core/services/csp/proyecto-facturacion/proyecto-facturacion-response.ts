import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { IEstadoValidacionIP } from '@core/models/csp/estado-validacion-ip';
import { ITipoFacturacionResponse } from '../tipo-facturacion/tipo-facturacion-response';

export interface IProyectoFacturacionResponse {
  id: number;
  comentario: I18nFieldValueResponse[];
  estadoValidacionIP: IEstadoValidacionIP;
  fechaConformidad: string;
  fechaEmision: string;
  importeBase: number;
  numeroFacturaSge: string;
  numeroPrevision: number;
  porcentajeIVA: number;
  proyectoId: number;
  proyectoProrrogaId: number;
  proyectoSgeRef: string;
  tipoFacturacion: ITipoFacturacionResponse;
}
