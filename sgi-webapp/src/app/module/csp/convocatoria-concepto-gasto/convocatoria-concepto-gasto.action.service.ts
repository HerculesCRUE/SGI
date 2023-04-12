import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { ActionService } from '@core/services/action-service';
import { ConvocatoriaConceptoGastoCodigoEcService } from '@core/services/csp/convocatoria-concepto-gasto-codigo-ec.service';
import { ConvocatoriaConceptoGastoService } from '@core/services/csp/convocatoria-concepto-gasto.service';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, of, Subject, throwError } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY } from './convocatoria-concepto-gasto-data.resolver';
import { ConvocatoriaConceptoGastoCodigoEcFragment } from './convocatoria-concepto-gasto-formulario/convocatoria-concepto-gasto-codigo-ec/convocatoria-concepto-gasto-codigo-ec.fragment';
import { ConvocatoriaConceptoGastoDatosGeneralesFragment } from './convocatoria-concepto-gasto-formulario/convocatoria-concepto-gasto-datos-generales/convocatoria-concepto-gasto-datos-generales.fragment';
import { CONVOCATORIA_CONCEPTO_GASTO_ROUTE_PARAMS } from './convocatoria-concepto-gasto-route-params';

export interface IConvocatoriaConceptoGastoData {
  convocatoria: IConvocatoria;
  selectedConvocatoriaConceptoGastosPermitidos: IConvocatoriaConceptoGasto[];
  selectedConvocatoriaConceptoGastosNoPermitidos: IConvocatoriaConceptoGasto[];
  selectedConvocatoriaConceptoGastoCodigosEc: IConvocatoriaConceptoGastoCodigoEc[];
  permitido: boolean;
  readonly: boolean;
  canEdit: boolean;
}

@Injectable()
export class ConvocatoriaConceptoGastoActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    CODIGOS_ECONOMICOS: 'codigosEconomicos'
  };

  private datosGenerales: ConvocatoriaConceptoGastoDatosGeneralesFragment;
  private codigosEconomicos: ConvocatoriaConceptoGastoCodigoEcFragment;

  private readonly data: IConvocatoriaConceptoGastoData;

  public readonly blockAddCodigosEconomicos$: Subject<boolean> = new BehaviorSubject<boolean>(false);

  get convocatoriaConceptoGastosPermitidos(): IConvocatoriaConceptoGasto[] {
    return this.data.selectedConvocatoriaConceptoGastosPermitidos;
  }

  get convocatoriaConceptoGastosNoPermitidos(): IConvocatoriaConceptoGasto[] {
    return this.data.selectedConvocatoriaConceptoGastosNoPermitidos;
  }

  get convocatoriaConceptoGastoCodigosEc(): IConvocatoriaConceptoGastoCodigoEc[] {
    return this.data.selectedConvocatoriaConceptoGastoCodigosEc;
  }

  get permitido(): boolean {
    return this.data.permitido;
  }

  get conceptoGasto(): IConceptoGasto {
    return this.datosGenerales.getValue().conceptoGasto;
  }

  get convocatoriaConceptoGasto(): IConvocatoriaConceptoGasto {
    return this.datosGenerales.getValue();
  }

  constructor(
    private readonly logger: NGXLogger,
    route: ActivatedRoute,
    convocatoriaConceptoGastoService: ConvocatoriaConceptoGastoService,
    convocatoriaConceptoGastoCodigoEcService: ConvocatoriaConceptoGastoCodigoEcService,
    codigoEconomicoGastoService: CodigoEconomicoGastoService
  ) {
    super();
    this.data = route.snapshot.data[CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY];
    const id = route.snapshot.paramMap.get(CONVOCATORIA_CONCEPTO_GASTO_ROUTE_PARAMS.ID)
      ? Number(route.snapshot.paramMap.get(CONVOCATORIA_CONCEPTO_GASTO_ROUTE_PARAMS.ID))
      : undefined;

    if (id) {
      this.enableEdit();
    }

    this.datosGenerales = new ConvocatoriaConceptoGastoDatosGeneralesFragment(
      id,
      this.data.convocatoria,
      convocatoriaConceptoGastoService,
      this.convocatoriaConceptoGastosPermitidos,
      this.convocatoriaConceptoGastosNoPermitidos,
      this.convocatoriaConceptoGastoCodigosEc,
      this.data.permitido,
      this.data.canEdit
    );

    this.codigosEconomicos = new ConvocatoriaConceptoGastoCodigoEcFragment(
      id,
      convocatoriaConceptoGastoService,
      convocatoriaConceptoGastoCodigoEcService,
      codigoEconomicoGastoService,
      !this.data.canEdit
    );

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.CODIGOS_ECONOMICOS, this.codigosEconomicos);

    this.subscriptions.push(this.datosGenerales.conceptoGasto$.subscribe(
      (conceptoGasto => this.blockAddCodigosEconomicos$.next(!Boolean(conceptoGasto)))
    ));

    this.subscriptions.push(
      this.codigosEconomicos.convocatoriaConceptoGastoCodigoEcs$.pipe(
        map(codigosEconomicos => codigosEconomicos.map(codigoEconomico => codigoEconomico.value as IConvocatoriaConceptoGastoCodigoEc))
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

}
