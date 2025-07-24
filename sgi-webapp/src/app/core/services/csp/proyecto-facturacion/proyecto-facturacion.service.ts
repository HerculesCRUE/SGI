import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoFacturacion } from '@core/models/csp/proyecto-facturacion';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, UpdateCtor } from '@sgi/framework/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { IProyectoFacturacionRequest } from './proyecto-facturacion-request';
import { PROYECTO_FACTURACION_REQUEST_CONVERTER } from './proyecto-facturacion-request.converter';
import { IProyectoFacturacionResponse } from './proyecto-facturacion-response';
import { PROYECTO_FACTURACION_RESPONSE_CONVERTER } from './proyecto-facturacion-response.converter';

// tslint:disable-next-line: variable-name
const _ProyectoFacturacionMixinBase:
  CreateCtor<IProyectoFacturacion, IProyectoFacturacion,
    IProyectoFacturacionRequest, IProyectoFacturacionResponse> &
  UpdateCtor<number, IProyectoFacturacion, IProyectoFacturacion,
    IProyectoFacturacionRequest, IProyectoFacturacionResponse> &
  FindByIdCtor<number, IProyectoFacturacion, IProyectoFacturacionResponse> &
  FindAllCtor<IProyectoFacturacion, IProyectoFacturacionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          PROYECTO_FACTURACION_REQUEST_CONVERTER,
          PROYECTO_FACTURACION_RESPONSE_CONVERTER
        ),
        PROYECTO_FACTURACION_REQUEST_CONVERTER,
        PROYECTO_FACTURACION_RESPONSE_CONVERTER
      ),
      PROYECTO_FACTURACION_RESPONSE_CONVERTER
    ),
    PROYECTO_FACTURACION_RESPONSE_CONVERTER);
@Injectable({
  providedIn: 'root'
})
export class ProyectoFacturacionService extends _ProyectoFacturacionMixinBase {

  private static readonly MAPPING = '/proyectosfacturacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoFacturacionService.MAPPING}`,
      http
    );
  }

  deleteById(id: number) {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`).pipe(
      catchError((error: HttpErrorResponse) => {
        return throwError(error);
      })
    );
  }

  updateValidacionIP(id: number, proyectoFacturacion: IProyectoFacturacion): Observable<IProyectoFacturacion> {
    return this.http.patch<IProyectoFacturacionResponse>(
      `${this.endpointUrl}/${id}/validacion-ip`,
      PROYECTO_FACTURACION_REQUEST_CONVERTER.fromTarget(proyectoFacturacion)
    ).pipe(
      map(response => PROYECTO_FACTURACION_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  /**
   * Devuelve la lista de IProyectoFacturacion que no tienen numeroFacturaSge y que cumplen con los criterios de busqueda
   * 
   * @param options opciones de busqueda
   * @returns la lista de IProyectoFacturacion que cumplen con los criterios de busqueda
   */
  findFacturasPendientesEmitir(options: SgiRestFindOptions): Observable<IProyectoFacturacion[]> {
    return this.find<IProyectoFacturacionResponse, IProyectoFacturacion>(
      `${this.endpointUrl}/facturas-pendientes-emitir`,
      options,
      PROYECTO_FACTURACION_RESPONSE_CONVERTER).pipe(
        map(response => response.items)
      );
  }

}
