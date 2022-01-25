import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAutorizacion } from '@core/models/csp/autorizacion';
import { ICertificadoAutorizacion } from '@core/models/csp/certificado-autorizacion';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IEstadoAutorizacion } from '@core/models/csp/estado-autorizacion';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ICertificadoAutorizacionResponse } from '../certificado-autorizacion/certificado-autorizacion-response';
import { CERTIFICADO_AUTORIZACION_RESPONSE_CONVERTER } from '../certificado-autorizacion/certificado-autorizacion-response.converter';
import { IConvocatoriaTituloResponse } from '../convocatoria/convocatoria-titulo-response';
import { CONVOCATORIA_TITULO_RESPONSE_CONVERTER } from '../convocatoria/convocatoria-titulo-response.converter';
import { IEstadoAutorizacionResponse } from '../estado-autorizacion/estado-autorizacion-response';
import { ESTADO_AUTORIZACION_RESPONSE_CONVERTER } from '../estado-autorizacion/estado-autorizacion-response.converter';
import { IAutorizacionRequest } from './autorizacion-request';
import { AUTORIZACION_REQUEST_CONVERTER } from './autorizacion-request.converter';
import { IAutorizacionResponse } from './autorizacion-response';
import { AUTORIZACION_RESPONSE_CONVERTER } from './autorizacion-response.converter';

// tslint:disable-next-line: variable-name
const _AutorizacionMixinBase:
  CreateCtor<IAutorizacion, IAutorizacion, IAutorizacionRequest, IAutorizacionResponse> &
  UpdateCtor<number, IAutorizacion, IAutorizacion, IAutorizacionRequest, IAutorizacionResponse> &
  FindByIdCtor<number, IAutorizacion, IAutorizacionResponse> &
  FindAllCtor<IAutorizacion, IAutorizacionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          AUTORIZACION_REQUEST_CONVERTER,
          AUTORIZACION_RESPONSE_CONVERTER
        ),
        AUTORIZACION_REQUEST_CONVERTER,
        AUTORIZACION_RESPONSE_CONVERTER
      ),
      AUTORIZACION_RESPONSE_CONVERTER
    ),
    AUTORIZACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class AutorizacionService extends _AutorizacionMixinBase {
  private static readonly MAPPING = '/autorizaciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${AutorizacionService.MAPPING}`,
      http,
    );
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

  /**
   * Acción de presentacion de una autorizacion
   * @param id identificador de la autorizacion a presentar
   */
  presentar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/presentar`, undefined);
  }

  /**
   * Comprueba si la Autorizacion es o no presentable
   * @param id el identificador de la autorizacion a comporobar
   */
  presentable(id: number): Observable<boolean> {
    return this.http.head(`${this.endpointUrl}/${id}/presentable`, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Realiza el cambio de estado de estado de una autorizacion.
   *
   * @param id identificador de la autorizacion.
   * @param estadoAutorizacion Nuevo estado de la autorizacion.
   */
  cambiarEstado(id: number, estadoAutorizacion: IEstadoAutorizacion): Observable<IEstadoAutorizacion> {
    return this.http.patch<IEstadoAutorizacionResponse>(`${this.endpointUrl}/${id}/cambiar-estado`,
      ESTADO_AUTORIZACION_RESPONSE_CONVERTER.fromTarget(estadoAutorizacion)
    ).pipe(
      map((response => ESTADO_AUTORIZACION_RESPONSE_CONVERTER.toTarget(response)))
    );
  }

  /**
   * Comprueba si Autorizacion tiene NotificacionProyectoExternoCVN relacionado
   *
   * @param id Autorizacion
   */
  hasAutorizacionNotificacionProyectoExterno(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/vinculacionesnotificacionesproyectosexternos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Recupera listado de historico estado
   * @param id autorizacion
   * @param options opciones de búsqueda.
   */
  findEstadosAutorizacion(autorizacionId: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IEstadoAutorizacion>> {
    return this.find<IEstadoAutorizacionResponse, IEstadoAutorizacion>(
      `${this.endpointUrl}/${autorizacionId}/estados`,
      options,
      ESTADO_AUTORIZACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Recupera listado de historico estado
   * @param id autorizacion
   * @param options opciones de búsqueda.
   */
  findCertificadosAutorizacion(autorizacionId: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<ICertificadoAutorizacion>> {
    return this.find<ICertificadoAutorizacionResponse, ICertificadoAutorizacion>(
      `${this.endpointUrl}/${autorizacionId}/certificados`,
      options,
      CERTIFICADO_AUTORIZACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Comprueba si la autorizacion tiene asociado algun CertificadoAutorizacion con el campo 
   * visible a 'true'.
   * @param id identificador del {@link Autorizacion}
   * @return  estado de la respuesta, 200 si contiene, 204 si no contiene.
   */
  hasCertificadoAutorizacionVisible(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/hascertificadoautorizacionvisible`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

  /**
   * Devuelve el listado de autorizaciones que puede ver un investigador
   *
   * @param options opciones de búsqueda.
   */
  findAllInvestigador(options?: SgiRestFindOptions): Observable<SgiRestListResult<IAutorizacion>> {
    return this.find<IAutorizacionResponse, IAutorizacion>(
      `${this.endpointUrl}/investigador`,
      options,
      AUTORIZACION_RESPONSE_CONVERTER);
  }

  /**
   * Devuelve los datos de la convocatoria de una solicitud
   *
   * @param id Id de la autorizacion
   */
  findConvocatoria(id: number): Observable<IConvocatoria> {
    return this.http.get<IConvocatoriaTituloResponse>(
      `${this.endpointUrl}/${id}/convocatoria`
    ).pipe(
      map(response => CONVOCATORIA_TITULO_RESPONSE_CONVERTER.toTarget(response))
    );
  }

}
