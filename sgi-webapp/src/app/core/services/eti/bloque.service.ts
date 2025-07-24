import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IApartado } from '@core/models/eti/apartado';
import { IBloque } from '@core/models/eti/bloque';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IApartadoResponse } from './apartado/apartado-response';
import { APARTADO_RESPONSE_CONVERTER } from './apartado/apartado-response.converter';
import { IBloqueResponse } from './bloque/bloque-response';
import { BLOQUE_RESPONSE_CONVERTER } from './bloque/bloque-response.converter';

// tslint:disable-next-line: variable-name
const _BloqueServiceMixinBase:
  FindByIdCtor<number, IBloque, IBloqueResponse> &
  FindAllCtor<IBloque, IBloqueResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService,
      BLOQUE_RESPONSE_CONVERTER
    ),
    BLOQUE_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class BloqueService extends _BloqueServiceMixinBase {
  private static readonly MAPPING = '/bloques';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${BloqueService.MAPPING}`,
      http
    );
  }

  /**
  * Devuelve los apartados de un bloque
  *
  * @param id Id del bloque
  * @param options Opciones de paginación
  */
  getApartados(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IApartado>> {
    return this.find<IApartadoResponse, IApartado>(`${this.endpointUrl}/${id}/apartados`, options, APARTADO_RESPONSE_CONVERTER);
  }


  /**
   * Devuelve el bloque de comentarios generales
   */
  getBloqueComentariosGenerales(): Observable<IBloque> {
    return this.get<IBloqueResponse>(`${this.endpointUrl}/comentarios-generales`).pipe(
      map(response => BLOQUE_RESPONSE_CONVERTER.toTarget(response))
    );
  }

}
