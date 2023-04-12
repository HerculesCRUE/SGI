import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IApartado } from '@core/models/eti/apartado';
import { IBloque } from '@core/models/eti/bloque';
import { environment } from '@env';
import { SgiReadOnlyRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BloqueService extends SgiReadOnlyRestService<number, IBloque> {
  private static readonly MAPPING = '/bloques';

  constructor(protected http: HttpClient) {
    super(
      BloqueService.name,
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
    return this.find<IApartado, IApartado>(`${this.endpointUrl}/${id}/apartados`, options);
  }

  /**
   * Devuelve el bloque de comentarios generales
   */
  getBloqueComentariosGenerales(): Observable<IBloque> {
    return this.http.get<IBloque>(`${this.endpointUrl}/comentarios-generales`);
  }

}
