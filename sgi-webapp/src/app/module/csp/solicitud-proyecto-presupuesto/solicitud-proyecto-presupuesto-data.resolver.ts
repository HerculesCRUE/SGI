import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IEntidad } from '@core/models/csp/entidad';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { SolicitudProyectoEntidadService } from '@core/services/csp/solicitud-proyecto-entidad/solicitud-proyecto-entidad.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { SOLICITUD_DATA_KEY } from '../solicitud/solicitud-data.resolver';
import { ISolicitudData } from '../solicitud/solicitud.action.service';
import { SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_PARAMS } from './solicitud-proyecto-presupuesto-route-params';
import { ISolicitudProyectoPresupuestoData } from './solicitud-proyecto-presupuesto.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const SOLICITUD_PROYECTO_PRESUPUESTO_DATA_KEY = 'solicitudProyectoPresupuestoData';
export const SOLICITUD_PROYECTO_PRESUPUESTO_AJENA_KEY = 'ajena';
export const SOLICITUD_PROYECTO_PRESUPUESTO_FINANCIADORA_KEY = 'financiadora';

@Injectable()
export class SolicitudProyectoPresupuestoDataResolver extends SgiResolverResolver<ISolicitudProyectoPresupuestoData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private empresaService: EmpresaService,
    private solicitudProyectoEntidadService: SolicitudProyectoEntidadService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<ISolicitudProyectoPresupuestoData> {
    const solicitudData: ISolicitudData = route.parent.data[SOLICITUD_DATA_KEY];
    const solicitudProyectoEntidadId = Number(
      route.paramMap.get(SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_PARAMS.SOLICITUD_PROYECTO_ENTIDAD_ID)
    );
    const ajena = route.parent.routeConfig.data[SOLICITUD_PROYECTO_PRESUPUESTO_AJENA_KEY];
    const financiadora = route.parent.routeConfig.data[SOLICITUD_PROYECTO_PRESUPUESTO_FINANCIADORA_KEY];

    let loadEntidad$: Observable<IEntidad>;
    if (ajena) {
      loadEntidad$ = this.getEntidadFinanciadoraSolicitud(solicitudProyectoEntidadId);
    } else {
      if (financiadora) {
        loadEntidad$ = this.getEntidadFinanciadoraConvocatoria(solicitudProyectoEntidadId);
      } else {
        loadEntidad$ = this.getEntidadGestoraConvocatoria(solicitudProyectoEntidadId);
      }
    }

    return loadEntidad$.pipe(
      map(entidad => {
        return {
          solicitudProyectoEntidadId,
          entidad,
          ajena,
          financiadora,
          readonly: solicitudData.readonly
        };
      }),
      switchMap(data => {
        return this.empresaService.findById(data.entidad.empresa.id).pipe(
          map(empresa => {
            data.entidad.empresa = empresa;
            return data;
          })
        );
      })
    );
  }

  private getEntidadFinanciadoraConvocatoria(solicitudProyectoEntidadId: number): Observable<IEntidad> {
    return this.solicitudProyectoEntidadService.findById(solicitudProyectoEntidadId).pipe(
      map(solicitudProyectoEntidad => solicitudProyectoEntidad.convocatoriaEntidadFinanciadora)
    );
  }

  private getEntidadFinanciadoraSolicitud(solicitudProyectoEntidadId: number): Observable<IEntidad> {
    return this.solicitudProyectoEntidadService.findById(solicitudProyectoEntidadId).pipe(
      map(solicitudProyectoEntidad => solicitudProyectoEntidad.solicitudProyectoEntidadFinanciadoraAjena)
    );
  }

  private getEntidadGestoraConvocatoria(solicitudProyectoEntidadId: number): Observable<IEntidad> {
    return this.solicitudProyectoEntidadService.findById(solicitudProyectoEntidadId).pipe(
      map(solicitudProyectoEntidad => solicitudProyectoEntidad.convocatoriaEntidadGestora)
    );
  }

}
