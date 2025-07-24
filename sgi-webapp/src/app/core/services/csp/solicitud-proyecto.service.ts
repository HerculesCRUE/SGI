import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ISolicitudProyectoResponse } from './solicitud-proyecto/solicitud-proyecto-response';
import { SOLICITUD_PROYECTO_RESPONSE_CONVERTER } from './solicitud-proyecto/solicitud-proyecto-response.converter';

const _SolicitudProyectoServiceMixinBase:
  CreateCtor<ISolicitudProyecto, ISolicitudProyecto, ISolicitudProyectoResponse, ISolicitudProyectoResponse> &
  UpdateCtor<number, ISolicitudProyecto, ISolicitudProyecto, ISolicitudProyectoResponse, ISolicitudProyectoResponse> &
  FindAllCtor<ISolicitudProyecto, ISolicitudProyectoResponse> &
  FindByIdCtor<number, ISolicitudProyecto, ISolicitudProyectoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          SOLICITUD_PROYECTO_RESPONSE_CONVERTER,
          SOLICITUD_PROYECTO_RESPONSE_CONVERTER
        ),
        SOLICITUD_PROYECTO_RESPONSE_CONVERTER,
        SOLICITUD_PROYECTO_RESPONSE_CONVERTER
      ),
      SOLICITUD_PROYECTO_RESPONSE_CONVERTER),
    SOLICITUD_PROYECTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoService extends _SolicitudProyectoServiceMixinBase {

  private static readonly MAPPING = '/solicitudproyecto';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudProyectoService.MAPPING}`,
      http
    );
  }

  /**
   * Comprueba si SolicitudProyectoDatos tiene SolicitudProyectoPresupuesto
   * relacionado
   *
   * @param id solicitudProyectoDatos
   */
  hasSolicitudPresupuesto(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/solicitudpresupuesto`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si SolicitudProyectoDatos tiene SolicitudProyectoSocio
   * relacionado
   *
   * @param id solicitudProyectoDatos
   */
  hasSolicitudSocio(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/solicitudsocio`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }


  hasPeriodosJustificacion(solicitudProyectoId: number): Observable<boolean> {

    const url = `${this.endpointUrl}/${solicitudProyectoId}/solicitudproyectosocios/periodosjustificacion`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  hasPeriodosPago(solicitudProyectoId: number): Observable<boolean> {

    const url = `${this.endpointUrl}/${solicitudProyectoId}/solicitudproyectosocios/periodospago`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  hasAnySolicitudProyectoSocioWithRolCoordinador(solicitudProyectoId: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${solicitudProyectoId}/solicitudproyectosocios/coordinador`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

}
