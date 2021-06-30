import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConfiguracion } from '@core/models/eti/configuracion';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConfiguracionService extends SgiRestService<number, IConfiguracion>{

  private static readonly MAPPING = '/configuraciones';

  constructor(protected http: HttpClient) {
    super(ConfiguracionService.name,
      `${environment.serviceServers.eti}` + ConfiguracionService.MAPPING, http);
  }

  /**
   * Devuelve la configuración por su clave
   * @param clave la clave de la configuración.
   */
  getConfiguracion(): Observable<IConfiguracion> {
    return this.http.get<IConfiguracion>(`${this.endpointUrl}`);
  }
}
