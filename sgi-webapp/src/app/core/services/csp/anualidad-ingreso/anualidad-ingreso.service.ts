import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAnualidadIngreso } from '@core/models/csp/anualidad-ingreso';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ANUALIDAD_INGRESO_REQUEST_CONVERTER } from './anualidad-ingreso-request.converter';
import { IAnualidadIngresoResponse } from './anualidad-ingreso-response';
import { ANUALIDAD_INGRESO_RESPONSE_CONVERTER } from './anualidad-ingreso-response.converter';

@Injectable({
  providedIn: 'root'
})
export class AnualidadIngresoService extends SgiRestBaseService {
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
