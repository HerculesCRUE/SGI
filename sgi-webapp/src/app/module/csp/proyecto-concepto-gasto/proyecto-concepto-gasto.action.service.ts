import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { IProyectoConceptoGastoCodigoEc } from '@core/models/csp/proyecto-concepto-gasto-codigo-ec';
import { ActionService } from '@core/services/action-service';
import { ConvocatoriaConceptoGastoService } from '@core/services/csp/convocatoria-concepto-gasto.service';
import { ProyectoConceptoGastoCodigoEcService } from '@core/services/csp/proyecto-concepto-gasto-codigo-ec.service';
import { ProyectoConceptoGastoService } from '@core/services/csp/proyecto-concepto-gasto.service';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, of, Subject, throwError } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { PROYECTO_CONCEPTO_GASTO_DATA_KEY } from './proyecto-concepto-gasto-data.resolver';
import { ProyectoConceptoGastoCodigoEcFragment } from './proyecto-concepto-gasto-formulario/proyecto-concepto-gasto-codigo-ec/proyecto-concepto-gasto-codigo-ec.fragment';
import { ProyectoConceptoGastoDatosGeneralesFragment } from './proyecto-concepto-gasto-formulario/proyecto-concepto-gasto-datos-generales/proyecto-concepto-gasto-datos-generales.fragment';
import { PROYECTO_CONCEPTO_GASTO_ROUTE_PARAMS } from './proyecto-concepto-gasto-route-params';

export const CONVOCATORIA_CONCEPTO_GASTO_ID_KEY = 'convocatoriaConceptoGastoId';

export interface IProyectoConceptoGastoData {
  proyecto: IProyecto;
  selectedProyectoConceptosGastoPermitidos: IProyectoConceptoGasto[];
  selectedProyectoConceptosGastoNoPermitidos: IProyectoConceptoGasto[];
  selectedProyectoConceptosGastoCodigosEc: IProyectoConceptoGastoCodigoEc[];
  convocatoriaConceptoGastoId: number;
  permitido: boolean;
  readonly: boolean;
}

@Injectable()
export class ProyectoConceptoGastoActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    CODIGOS_ECONOMICOS: 'codigosEconomicos'
  };

  private datosGenerales: ProyectoConceptoGastoDatosGeneralesFragment;
  private codigosEconomicos: ProyectoConceptoGastoCodigoEcFragment;

  private readonly data: IProyectoConceptoGastoData;

  public readonly blockAddCodigosEconomicos$: Subject<boolean> = new BehaviorSubject<boolean>(false);

  get proyectoConceptosGastoPermitidos(): IProyectoConceptoGasto[] {
    return this.data.selectedProyectoConceptosGastoPermitidos;
  }

  get proyectoConceptosGastoNoPermitidos(): IProyectoConceptoGasto[] {
    return this.data.selectedProyectoConceptosGastoNoPermitidos;
  }

  get proyectoConceptosGastoCodigosEc(): IProyectoConceptoGastoCodigoEc[] {
    return this.data.selectedProyectoConceptosGastoCodigosEc;
  }

  get permitido(): boolean {
    return this.data.permitido;
  }

  get conceptoGasto(): IConceptoGasto {
    return this.datosGenerales.getValue().conceptoGasto;
  }

  get proyectoConceptoGasto(): IProyectoConceptoGasto {
    return this.datosGenerales.getValue();
  }


  constructor(
    private readonly logger: NGXLogger,
    route: ActivatedRoute,
    proyectoConceptoGastoService: ProyectoConceptoGastoService,
    proyectoConceptoGastoCodigoEcService: ProyectoConceptoGastoCodigoEcService,
    codigoEconomicoGastoService: CodigoEconomicoGastoService,
    private convocatoriaConceptoGastoService: ConvocatoriaConceptoGastoService,
  ) {
    super();
    this.data = route.snapshot.data[PROYECTO_CONCEPTO_GASTO_DATA_KEY];
    const id = route.snapshot.paramMap.get(PROYECTO_CONCEPTO_GASTO_ROUTE_PARAMS.ID)
      ? Number(route.snapshot.paramMap.get(PROYECTO_CONCEPTO_GASTO_ROUTE_PARAMS.ID))
      : undefined;

    const convocatoriaConceptoGastoId = this.data.convocatoriaConceptoGastoId ?? history.state[CONVOCATORIA_CONCEPTO_GASTO_ID_KEY];

    if (id) {
      this.enableEdit();
    }

    if (convocatoriaConceptoGastoId) {
      this.loadConvocatoriaConceptoGasto(convocatoriaConceptoGastoId);
    }

    this.datosGenerales = new ProyectoConceptoGastoDatosGeneralesFragment(
      id,
      this.data.proyecto,
      proyectoConceptoGastoService,
      this.proyectoConceptosGastoPermitidos,
      this.proyectoConceptosGastoNoPermitidos,
      this.proyectoConceptosGastoCodigosEc,
      this.data.permitido,
      this.data.readonly
    );

    this.codigosEconomicos = new ProyectoConceptoGastoCodigoEcFragment(
      id,
      this.data.proyecto,
      convocatoriaConceptoGastoId,
      proyectoConceptoGastoService,
      proyectoConceptoGastoCodigoEcService,
      convocatoriaConceptoGastoService,
      codigoEconomicoGastoService,
      this.data.readonly
    );

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.CODIGOS_ECONOMICOS, this.codigosEconomicos);

    this.subscriptions.push(this.datosGenerales.conceptoGasto$.subscribe(
      (conceptoGasto => this.blockAddCodigosEconomicos$.next(!Boolean(conceptoGasto)))
    ));

    this.subscriptions.push(
      this.codigosEconomicos.proyectoConceptoGastoCodigosEcs$.pipe(
        map(codigosEconomicos =>
          codigosEconomicos
            .map(codigoEconomico => codigoEconomico.proyectoCodigoEconomico?.value)
            .filter(codigoEconomico => !!codigoEconomico)
        )
      ).subscribe(
        (codigosEconomicos => this.datosGenerales.setCodigosEconomicos(codigosEconomicos))
      )
    );

    // Inicializamos los fragments
    this.datosGenerales.initialize();
    this.codigosEconomicos.initialize();
  }

  saveOrUpdate(): Observable<void> {
    this.performChecks(true);
    if (this.hasErrors()) {
      return throwError('Errores');
    }

    if (this.isEdit()) {
      // Si da error la actualizacion se captura el error y se reintenta una vez
      let cascade = of(void 0);
      if (this.datosGenerales.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.datosGenerales.saveOrUpdate().pipe(
            tap(() => this.datosGenerales.refreshInitialState(true)),
            catchError((err) => {
              this.logger.error(err);
              return of(void 0);
            })
          ))
        );
      }
      if (this.codigosEconomicos.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.codigosEconomicos.saveOrUpdate().pipe(tap(() => this.codigosEconomicos.refreshInitialState(true)))),
          catchError((err) => {
            this.logger.error(err);
            return of(void 0);
          })
        );
      }
      return cascade.pipe(
        switchMap(() => super.saveOrUpdate())
      );
    } else {
      return super.saveOrUpdate();
    }

  }


  private loadConvocatoriaConceptoGasto(id: number): void {
    if (id) {
      this.convocatoriaConceptoGastoService.findById(id).subscribe(convocatoriaConceptoGasto => {
        this.datosGenerales.setDatosConvocatoriaConceptoGasto(convocatoriaConceptoGasto);
      });
    }
  }

}
