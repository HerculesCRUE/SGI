import { IConvocatoriaEntidadFinanciadora } from './convocatoria-entidad-financiadora';
import { IConvocatoriaEntidadGestora } from './convocatoria-entidad-gestora';
import { ISolicitudProyectoEntidadFinanciadoraAjena } from './solicitud-proyecto-entidad-financiadora-ajena';

export interface ISolicitudProyectoEntidad {
  id: number;
  solicitudProyectoId: number;
  solicitudProyectoEntidadFinanciadoraAjena: ISolicitudProyectoEntidadFinanciadoraAjena;
  convocatoriaEntidadFinanciadora: IConvocatoriaEntidadFinanciadora;
  convocatoriaEntidadGestora: IConvocatoriaEntidadGestora;
}
