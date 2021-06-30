import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_SOCIO_PERIODO_PAGO_CONVERTER } from '@core/converters/csp/proyecto-socio-periodo-pago.converter';
import { IProyectoSocioPeriodoPagoBackend } from '@core/models/csp/backend/proyecto-socio-periodo-pago-backend';
import { IProyectoSocioPeriodoPago } from '@core/models/csp/proyecto-socio-periodo-pago';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProyectoSocioPeriodoPagoService
  extends SgiMutableRestService<number, IProyectoSocioPeriodoPagoBackend, IProyectoSocioPeriodoPago> {
  private static readonly MAPPING = '/proyectosocioperiodopagos';

  constructor(protected http: HttpClient) {
    super(
      ProyectoSocioPeriodoPagoService.name,
      `${environment.serviceServers.csp}${ProyectoSocioPeriodoPagoService.MAPPING}`,
      http,
      PROYECTO_SOCIO_PERIODO_PAGO_CONVERTER
    );
  }

  updateList(proyectoSocioId: number, entities: IProyectoSocioPeriodoPago[]):
    Observable<IProyectoSocioPeriodoPago[]> {
    return this.http.patch<IProyectoSocioPeriodoPagoBackend[]>(
      `${this.endpointUrl}/${proyectoSocioId}`,
      this.converter.fromTargetArray(entities)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }

}
