import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_PROYECTO_PRESUPUESTO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-presupuesto.converter';
import { ISolicitudProyectoPresupuestoBackend } from '@core/models/csp/backend/solicitud-proyecto-presupuesto-backend';
import { ISolicitudProyectoEntidad } from '@core/models/csp/solicitud-proyecto-entidad';
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ISolicitudProyectoEntidadResponse } from './solicitud-proyecto-entidad-response';
import { SOLICITUD_PROYECTO_ENTIDAD_RESPONSE_CONVERTER } from './solicitud-proyecto-entidad-response.converter';

// tslint:disable-next-line: variable-name
const _SolicitudProyectoEntidadServiceMixinBase:
  FindByIdCtor<number, ISolicitudProyectoEntidad, ISolicitudProyectoEntidadResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    SOLICITUD_PROYECTO_ENTIDAD_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoEntidadService extends _SolicitudProyectoEntidadServiceMixinBase {
  private static readonly MAPPING = '/solicitudesproyectosentidades';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudProyectoEntidadService.MAPPING}`,
      http
    );
  }

  /**
   * Recupera los ISolicitudProyectoPresupuesto de la entidad
   *
   * @param id Id de la solicitudProyectoEntidad
   * @param options opciones de busqueda
   * @returns observable con la lista de ISolicitudProyectoPresupuesto de la entidad de la solicitud
   */
  findAllSolicitudProyectoPresupuestoEntidadConvocatoria(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<ISolicitudProyectoPresupuesto>> {
    return this.find<ISolicitudProyectoPresupuestoBackend, ISolicitudProyectoPresupuesto>(
      `${this.endpointUrl}/${id}/solicitudproyectopresupuestos`,
      options,
      SOLICITUD_PROYECTO_PRESUPUESTO_CONVERTER
    );
  }

}
