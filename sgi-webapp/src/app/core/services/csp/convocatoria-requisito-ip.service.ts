import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_REQUISITO_IP_CONVERTER } from '@core/converters/csp/convocatoria-requisito-ip.converter';
import { IConvocatoriaRequisitoIPBackend } from '@core/models/csp/backend/convocatoria-requisito-ip-backend';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaRequisitoIPService
  extends SgiMutableRestService<number, IConvocatoriaRequisitoIPBackend, IConvocatoriaRequisitoIP> {
  private static readonly MAPPING = '/convocatoria-requisitoips';

  constructor(private readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      ConvocatoriaRequisitoIPService.name,
      `${environment.serviceServers.csp}${ConvocatoriaRequisitoIPService.MAPPING}`,
      http,
      CONVOCATORIA_REQUISITO_IP_CONVERTER
    );
  }

  /**
   * Recupera el requisito ip de la convocatoria
   * @param id convocatoria
   */
  getRequisitoIPConvocatoria(id: number): Observable<IConvocatoriaRequisitoIP> {
    const endpointUrl = `${this.endpointUrl}/${id}`;
    return this.http.get<IConvocatoriaRequisitoIPBackend>(endpointUrl).pipe(
      map(response => this.converter.toTarget(response)),
      catchError((err) => {
        this.logger.error(err);
        return of({} as IConvocatoriaRequisitoIP);
      })
    );
  }
}
