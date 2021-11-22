import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITramoReparto, Tipo } from '@core/models/pii/tramo-reparto';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, mixinCreate, mixinFindAll, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ITramoRepartoRequest } from './tramo-reparto-request';
import { TRAMO_REPARTO_REQUEST_CONVERTER } from './tramo-reparto-request.converter';


// tslint:disable-next-line: variable-name
const _TramoRepartoServiceMixinBase:
  FindAllCtor<ITramoReparto, ITramoReparto> &
  CreateCtor<ITramoReparto, ITramoReparto, ITramoRepartoRequest, ITramoReparto> &
  UpdateCtor<number, ITramoReparto, ITramoReparto, ITramoRepartoRequest, ITramoReparto> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        TRAMO_REPARTO_REQUEST_CONVERTER,
      ),
      TRAMO_REPARTO_REQUEST_CONVERTER
    )
  );

@Injectable({
  providedIn: 'root'
})
export class TramoRepartoService extends _TramoRepartoServiceMixinBase {
  private static readonly MAPPING = '/tramosreparto';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${TramoRepartoService.MAPPING}`,
      http,
    );
  }

  /**
   * Elimina el tramo de reparto
   * @param options id del tramo de reparto.
   */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

  /**
   * Comprueba si existe un Tramo de Reparto con el Tipo indicado
   * @param tipo del Tramo de Reparto.
   * @returns true si existe y false sino existe.
   */
  existTipoTramoReparto(tipo: Tipo): Observable<boolean> {
    return this.http.head<boolean>(this.endpointUrl, { params: { tipo }, observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si un Tramo de Reparto es modificable/eliminable
   * @param id del Tramo de Reparto.
   * @returns true si es modificable/eliminable y false sino es modificable/eliminable.
   */
  isTipoTramoRepartoModificable(id: number): Observable<boolean> {
    return this.http.head<boolean>(`${this.endpointUrl}/${id}/modificable`, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }
}
