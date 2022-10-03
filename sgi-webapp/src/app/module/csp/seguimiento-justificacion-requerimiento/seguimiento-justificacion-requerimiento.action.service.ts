import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SgiError } from '@core/errors/sgi-error';
import { IIncidenciaDocumentacionRequerimiento } from '@core/models/csp/incidencia-documentacion-requerimiento';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { ActionService } from '@core/services/action-service';
import { AlegacionRequerimientoService } from '@core/services/csp/alegacion-requerimiento/alegacion-requerimiento.service';
import { GastoRequerimientoJustificacionService } from '@core/services/csp/gasto-requerimiento-justificacion/gasto-requerimiento-justificacion.service';
import { IncidenciaDocumentacionRequerimientoService } from '@core/services/csp/incidencia-documentacion-requerimiento/incidencia-documentacion-requerimiento.service';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { RequerimientoJustificacionService } from '@core/services/csp/requerimiento-justificacion/requerimiento-justificacion.service';
import { SeguimientoJustificacionService } from '@core/services/sge/seguimiento-justificacion/seguimiento-justificacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, concatMap, tap } from 'rxjs/operators';
import { EJECUCION_ECONOMICA_DATA_KEY } from '../ejecucion-economica/ejecucion-economica-data.resolver';
import { IEjecucionEconomicaData } from '../ejecucion-economica/ejecucion-economica.action.service';
import { REQUERIMIENTO_JUSTIFICACION_DATA_KEY } from './seguimiento-justificacion-requerimiento-data.resolver';
import { SeguimientoJustificacionRequerimientoDatosGeneralesFragment } from './seguimiento-justificacion-requerimiento-formulario/seguimiento-justificacion-requerimiento-datos-generales/seguimiento-justificacion-requerimiento-datos-generales.fragment';
import { SeguimientoJustificacionRequerimientoGastosFragment } from './seguimiento-justificacion-requerimiento-formulario/seguimiento-justificacion-requerimiento-gastos/seguimiento-justificacion-requerimiento-gastos.fragment';
import { SeguimientoJustificacionRequerimientoRespuestaAlegacionFragment } from './seguimiento-justificacion-requerimiento-formulario/seguimiento-justificacion-requerimiento-respuesta-alegacion/seguimiento-justificacion-requerimiento-respuesta-alegacion.fragment';
import { SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_PARAMS } from './seguimiento-justificacion-requerimiento-route-params';

export interface IRequerimientoJustificacionData {
  incidenciasDocumentacion: IIncidenciaDocumentacionRequerimiento[];
  requerimientoJustificacion: IRequerimientoJustificacion;
  canEdit: boolean;
}

@Injectable()
export class SeguimientoJustificacionRequerimientoActionService extends ActionService {
  private incidenciasDocumentacion$ = new BehaviorSubject<StatusWrapper<IIncidenciaDocumentacionRequerimiento>[]>([]);

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    GASTOS: 'gastos',
    RESPUESTA_ALEGACION: 'respuesta-alegacion'
  };

  private readonly data: IRequerimientoJustificacionData;
  private readonly dataEjecucionEconomica: IEjecucionEconomicaData;
  public readonly: boolean;

  private datosGenerales: SeguimientoJustificacionRequerimientoDatosGeneralesFragment;
  private gastos: SeguimientoJustificacionRequerimientoGastosFragment;
  private respuestaAlegacion: SeguimientoJustificacionRequerimientoRespuestaAlegacionFragment;

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    requerimientoJustificacionService: RequerimientoJustificacionService,
    proyectoPeriodoJustificacionService: ProyectoPeriodoJustificacionService,
    incidenciaDocumentacionRequerimientoService: IncidenciaDocumentacionRequerimientoService,
    seguimientoJustificacionService: SeguimientoJustificacionService,
    gastoRequerimientoJustificacionService: GastoRequerimientoJustificacionService,
    alegacionRequerimientoService: AlegacionRequerimientoService,
  ) {
    super();
    this.data = {} as IRequerimientoJustificacionData;
    this.dataEjecucionEconomica = route.snapshot.parent.data[EJECUCION_ECONOMICA_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_PARAMS.ID));
    if (id) {
      this.data = route.snapshot.data[REQUERIMIENTO_JUSTIFICACION_DATA_KEY];
      this.enableEdit();
    }
    this.incidenciasDocumentacion$ = new BehaviorSubject<StatusWrapper<IIncidenciaDocumentacionRequerimiento>[]>(
      this.getInitialIncidenciasDocumentacion(this.data.incidenciasDocumentacion)
    );

    this.datosGenerales = new SeguimientoJustificacionRequerimientoDatosGeneralesFragment(
      logger,
      this.data.requerimientoJustificacion,
      this.incidenciasDocumentacion$,
      this.data.canEdit,
      this.dataEjecucionEconomica.proyectoSge.id,
      requerimientoJustificacionService,
      incidenciaDocumentacionRequerimientoService
    );

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

    if (this.isEdit()) {
      this.gastos = new SeguimientoJustificacionRequerimientoGastosFragment(
        this.data.requerimientoJustificacion.id,
        this.dataEjecucionEconomica.proyectoSge.id,
        requerimientoJustificacionService,
        seguimientoJustificacionService,
        proyectoPeriodoJustificacionService,
        gastoRequerimientoJustificacionService
      );

      this.respuestaAlegacion = new SeguimientoJustificacionRequerimientoRespuestaAlegacionFragment(
        this.data.requerimientoJustificacion.id,
        this.incidenciasDocumentacion$,
        this.data.canEdit,
        alegacionRequerimientoService,
        requerimientoJustificacionService,
        incidenciaDocumentacionRequerimientoService
      );

      this.datosGenerales.initialize();
      this.subscriptions.push(
        this.datosGenerales.getCurrentRequerimientoJustificacion$()
          .subscribe(currentRequerimientoJustificacion => {
            this.gastos.currentRequerimientoJustificacion = currentRequerimientoJustificacion;
            this.respuestaAlegacion.onCurrentRequerimientoJustificacionChanges(currentRequerimientoJustificacion);
          }
          )
      );

      this.addFragment(this.FRAGMENT.GASTOS, this.gastos);
      this.addFragment(this.FRAGMENT.RESPUESTA_ALEGACION, this.respuestaAlegacion);
    }
  }

  private getInitialIncidenciasDocumentacion(incidenciasDocumentacion: IIncidenciaDocumentacionRequerimiento[]):
    StatusWrapper<IIncidenciaDocumentacionRequerimiento>[] {
    return incidenciasDocumentacion ?
      this.data.incidenciasDocumentacion.map(incidenciaDocumentacion => new StatusWrapper(incidenciaDocumentacion)) : [];
  }

  getIncidenciasDocumentacion$(): Observable<StatusWrapper<IIncidenciaDocumentacionRequerimiento>[]> {
    return this.incidenciasDocumentacion$.asObservable();
  }

  saveOrUpdate(action?: any): Observable<void> {
    this.performChecks(true);
    if (this.hasErrors()) {
      return throwError('Errores');
    }
    if (this.isEdit()) {
      if (this.datosGenerales.hasChanges()) {
        return this.datosGenerales.saveOrUpdate()
          .pipe(
            catchError(error => {
              if (error instanceof SgiError) {
                this.datosGenerales.pushProblems(error);
                error.managed = true;
              }
              return throwError(error);
            }),
            tap(() => this.datosGenerales.refreshInitialState(true)),
            concatMap(() => super.saveOrUpdate(action))
          );
      } else {
        return super.saveOrUpdate(action);
      }
    } else {
      return super.saveOrUpdate(action);
    }
  }
}
