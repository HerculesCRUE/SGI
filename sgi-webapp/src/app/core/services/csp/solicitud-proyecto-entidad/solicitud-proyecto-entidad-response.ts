import { IConvocatoriaEntidadFinanciadoraBackend } from '@core/models/csp/backend/convocatoria-entidad-financiadora-backend';
import { IConvocatoriaEntidadGestoraBackend } from '@core/models/csp/backend/convocatoria-entidad-gestora-backend';
import { ISolicitudProyectoEntidadFinanciadoraAjenaBackend } from '@core/models/csp/backend/solicitud-proyecto-entidad-financiadora-ajena-backend';

export interface ISolicitudProyectoEntidadResponse {
  id: number;
  solicitudProyectoId: number;
  solicitudProyectoEntidadFinanciadoraAjena: ISolicitudProyectoEntidadFinanciadoraAjenaBackend;
  convocatoriaEntidadFinanciadora: IConvocatoriaEntidadFinanciadoraBackend;
  convocatoriaEntidadGestora: IConvocatoriaEntidadGestoraBackend;
}
