import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { ActionService } from '@core/services/action-service';
import { ConvocatoriaConceptoGastoCodigoEcService } from '@core/services/csp/convocatoria-concepto-gasto-codigo-ec.service';
import { ConvocatoriaConceptoGastoService } from '@core/services/csp/convocatoria-concepto-gasto.service';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { BehaviorSubject, Subject } from 'rxjs';
import { CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY } from './convocatoria-concepto-gasto-data.resolver';
import { ConvocatoriaConceptoGastoCodigoEcFragment } from './convocatoria-concepto-gasto-formulario/convocatoria-concepto-gasto-codigo-ec/convocatoria-concepto-gasto-codigo-ec.fragment';
import { ConvocatoriaConceptoGastoDatosGeneralesFragment } from './convocatoria-concepto-gasto-formulario/convocatoria-concepto-gasto-datos-generales/convocatoria-concepto-gasto-datos-generales.fragment';
import { CONVOCATORIA_CONCEPTO_GASTO_ROUTE_PARAMS } from './convocatoria-concepto-gasto-route-params';

export interface IConvocatoriaConceptoGastoData {
  convocatoria: IConvocatoria;
  selectedConvocatoriaConceptoGastos: IConvocatoriaConceptoGasto[];
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

  get convocatoriaConceptoGastos(): IConvocatoriaConceptoGasto[] {
    return this.data.selectedConvocatoriaConceptoGastos;
  }

  get permitido(): boolean {
    return this.data.permitido;
  }

  get conceptoGasto(): IConceptoGasto {
    return this.datosGenerales.getValue().conceptoGasto;
  }

  constructor(
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

    this.datosGenerales = new ConvocatoriaConceptoGastoDatosGeneralesFragment(id, this.data.convocatoria,
      convocatoriaConceptoGastoService, this.convocatoriaConceptoGastos, this.data.permitido, this.data.canEdit);

    this.codigosEconomicos = new ConvocatoriaConceptoGastoCodigoEcFragment(id,
      convocatoriaConceptoGastoService, convocatoriaConceptoGastoCodigoEcService, codigoEconomicoGastoService, !this.data.canEdit);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.CODIGOS_ECONOMICOS, this.codigosEconomicos);

    this.subscriptions.push(this.datosGenerales.conceptoGasto$.subscribe(
      (conceptoGasto => this.blockAddCodigosEconomicos$.next(!Boolean(conceptoGasto)))
    ));

    // Inicializamos los datos generales
    this.datosGenerales.initialize();
  }
}
