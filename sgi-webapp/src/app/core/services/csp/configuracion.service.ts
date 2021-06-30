import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConfiguracion } from '@core/models/csp/configuracion';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConfiguracionService extends SgiRestBaseService {
  private static readonly MAPPING = '/configuraciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConfiguracionService.MAPPING}`,
      http
    );
  }

  /**
   * Devuelve la configuración
   */
  getConfiguracion(): Observable<IConfiguracion> {
    return this.http.get<IConfiguracion>(`${this.endpointUrl}`);
  }

}
