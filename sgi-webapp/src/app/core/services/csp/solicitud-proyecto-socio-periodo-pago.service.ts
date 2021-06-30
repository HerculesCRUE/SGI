import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_PROYECTO_SOCIO_PERIODO_PAGO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-socio-periodo-pago.converter';
import { ISolicitudProyectoSocioPeriodoPagoBackend } from '@core/models/csp/backend/solicitud-proyecto-socio-periodo-pago-backend';
import { ISolicitudProyectoSocioPeriodoPago } from '@core/models/csp/solicitud-proyecto-socio-periodo-pago';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoSocioPeriodoPagoService
  extends SgiMutableRestService<number, ISolicitudProyectoSocioPeriodoPagoBackend, ISolicitudProyectoSocioPeriodoPago> {
  private static readonly MAPPING = '/solicitudproyectosocioperiodopago';

  constructor(protected http: HttpClient) {
    super(
      SolicitudProyectoSocioPeriodoPagoService.name,
      `${environment.serviceServers.csp}${SolicitudProyectoSocioPeriodoPagoService.MAPPING}`,
      http,
      SOLICITUD_PROYECTO_SOCIO_PERIODO_PAGO_CONVERTER
    );
  }

  updateList(proyectoSolictudSocioId: number, entities: ISolicitudProyectoSocioPeriodoPago[]):
    Observable<ISolicitudProyectoSocioPeriodoPago[]> {
    return this.http.patch<ISolicitudProyectoSocioPeriodoPagoBackend[]>(
      `${this.endpointUrl}/${proyectoSolictudSocioId}`,
      this.converter.fromTargetArray(entities)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }
}
