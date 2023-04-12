import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IBloque } from '@core/models/eti/bloque';
import { IFormulario } from '@core/models/eti/formulario';
import { environment } from '@env';
import { SgiReadOnlyRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FormularioService extends SgiReadOnlyRestService<number, IFormulario>{

  private static readonly MAPPING = '/formularios';

  constructor(protected http: HttpClient) {
    super(FormularioService.name, `${environment.serviceServers.eti}${FormularioService.MAPPING}`, http);
  }

  getBloques(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IBloque>> {
    return this.find<IBloque, IBloque>(`${this.endpointUrl}/${id}/bloques`, options);
  }

  /**
   * Actualiza el estado de la memoria o de la retrospectiva al estado final
   * correspondiente al tipo de formulario completado.
   *
   * @param memoriaId identificador de la memoria
   * @param tipoFormulario tipo de formulario
   */
  completado(memoriaId: number, tipoFormulario: number): Observable<void> {
    const url = `${this.endpointUrl}/completado`;
    let params = new HttpParams();
    params = params.append('memoriaId', memoriaId?.toString());
    params = params.append('tipoFormulario', tipoFormulario?.toString());

    return this.http.head<void>(url, { params });
  }

}
