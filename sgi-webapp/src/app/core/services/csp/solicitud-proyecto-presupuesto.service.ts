import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, SgiRestBaseService, UpdateCtor, mixinCreate, mixinFindAll, mixinUpdate } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ISolicitudProyectoPresupuestoResponse } from './solicitud-proyecto-presupuesto/solicitud-proyecto-presupuesto-response';
import { SOLICITUD_PROYECTO_PRESUPUESTO_CONVERTER } from './solicitud-proyecto-presupuesto/solicitud-proyecto-presupuesto.converter';

const _SolicitudProyectoPresupuestoServiceMixinBase:
  CreateCtor<ISolicitudProyectoPresupuesto, ISolicitudProyectoPresupuesto, ISolicitudProyectoPresupuestoResponse, ISolicitudProyectoPresupuestoResponse> &
  UpdateCtor<number, ISolicitudProyectoPresupuesto, ISolicitudProyectoPresupuesto, ISolicitudProyectoPresupuestoResponse, ISolicitudProyectoPresupuestoResponse> &
  FindAllCtor<ISolicitudProyectoPresupuesto, ISolicitudProyectoPresupuestoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        SOLICITUD_PROYECTO_PRESUPUESTO_CONVERTER,
        SOLICITUD_PROYECTO_PRESUPUESTO_CONVERTER
      ),
      SOLICITUD_PROYECTO_PRESUPUESTO_CONVERTER,
      SOLICITUD_PROYECTO_PRESUPUESTO_CONVERTER
    ),
    SOLICITUD_PROYECTO_PRESUPUESTO_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoPresupuestoService extends _SolicitudProyectoPresupuestoServiceMixinBase {
  private static readonly MAPPING = '/solicitudproyectopresupuestos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudProyectoPresupuestoService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
