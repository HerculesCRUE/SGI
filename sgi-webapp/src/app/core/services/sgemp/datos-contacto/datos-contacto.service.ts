import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IDatosContacto } from '@core/models/sgemp/datos-contacto';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IDatosContactoResponse } from './datos-contacto-response';
import { DATOS_CONTACTO_RESPONSE_CONVERTER } from './datos-contacto-response.converter';

@Injectable({
  providedIn: 'root'
})
export class DatosContactoService extends SgiRestBaseService {
  private static readonly MAPPING = '/datos-contacto';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgemp}${DatosContactoService.MAPPING}`,
      http
    );
  }

  /**
   * Busca los datos de contacto de la empresa
   *
   * @param empresaId identificador de la empresa
   * @returns datos de contacto de la empresa
   */
  findByEmpresaId(empresaId: string): Observable<IDatosContacto> {
    return this.get<IDatosContactoResponse>(`${this.endpointUrl}/empresa/${empresaId}`).pipe(
      map(response => DATOS_CONTACTO_RESPONSE_CONVERTER.toTarget(response))
    );
  }

}
