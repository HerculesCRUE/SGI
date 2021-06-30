import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAnualidadIngreso } from '@core/models/csp/anualidad-ingreso';
import { environment } from '@env';
import {
  CreateCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindById,
  mixinUpdate, SgiRestBaseService, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ANUALIDAD_INGRESO_REQUEST_CONVERTER } from './anualidad-ingreso-request.converter';
import { ANUALIDAD_INGRESO_RESPONSE_CONVERTER } from './anualidad-ingreso-response.converter';
import { IAnualidadIngresoRequest } from './anualidad-ingreso-request';
import { IAnualidadIngresoResponse } from './anualidad-ingreso-response';

// tslint:disable-next-line: variable-name
const _AnualidadIngresoServiceMixinBase:
  CreateCtor<IAnualidadIngreso, IAnualidadIngreso, IAnualidadIngresoRequest, IAnualidadIngresoResponse> &
  UpdateCtor<number, IAnualidadIngreso, IAnualidadIngreso, IAnualidadIngresoRequest, IAnualidadIngresoResponse> &
  FindByIdCtor<number, IAnualidadIngreso, IAnualidadIngresoResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        ANUALIDAD_INGRESO_REQUEST_CONVERTER,
        ANUALIDAD_INGRESO_RESPONSE_CONVERTER
      ),
      ANUALIDAD_INGRESO_REQUEST_CONVERTER,
      ANUALIDAD_INGRESO_RESPONSE_CONVERTER
    ),
    ANUALIDAD_INGRESO_REQUEST_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class AnualidadIngresoService extends _AnualidadIngresoServiceMixinBase {
  private static readonly MAPPING = '/anualidadingreso';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${AnualidadIngresoService.MAPPING}`,
      http,
    );
  }

  /**
   * Actualiza el listado de IDConvocatoriaConceptoIngreso asociados a un IConvocatoriaConceptoIngresoCodigoEc
   *
   * @param id Id del IConvocatoriaConceptoIngreso
   * @param entities Listado de IConvocatoriaConceptoIngresoCodigoEc
   */
  updateList(id: number, entities: IAnualidadIngreso[]): Observable<IAnualidadIngreso[]> {
    return this.http.patch<IAnualidadIngresoResponse[]>(
      `${this.endpointUrl}/${id}`,
      ANUALIDAD_INGRESO_REQUEST_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map((response => ANUALIDAD_INGRESO_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }

}
