import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_IVA_CONVERTER } from '@core/converters/csp/proyecto-iva.converter';
import { IProyectoIVABackend } from '@core/models/csp/backend/proyecto-iva-backend';
import { IProyectoIVA } from '@core/models/csp/proyecto-iva';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProyectoIVAService
  extends SgiMutableRestService<number, IProyectoIVABackend, IProyectoIVA> {
  private static readonly MAPPING = '/proyectoiva';

  constructor(protected http: HttpClient) {
    super(
      ProyectoIVAService.name,
      `${environment.serviceServers.csp}${ProyectoIVAService.MAPPING}`,
      http,
      PROYECTO_IVA_CONVERTER
    );
  }

  /**
   * Recupera los IProyectoIVA del proyecto
   *
   * @param proyectoId Id del proyecto
   * @param options opciones de busqueda
   * @returns observable con la lista de IProyectoIVA del proyecto
   */
  findAllByProyectoId(proyectoId: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoIVA>> {
    return this.find<IProyectoIVABackend, IProyectoIVA>(
      `${this.endpointUrl}/${proyectoId}/historico`,
      options,
      PROYECTO_IVA_CONVERTER
    );
  }

}
