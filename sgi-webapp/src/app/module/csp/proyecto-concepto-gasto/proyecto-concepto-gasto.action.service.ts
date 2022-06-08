import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { ActionService } from '@core/services/action-service';
import { ConvocatoriaConceptoGastoService } from '@core/services/csp/convocatoria-concepto-gasto.service';
import { ProyectoConceptoGastoCodigoEcService } from '@core/services/csp/proyecto-concepto-gasto-codigo-ec.service';
import { ProyectoConceptoGastoService } from '@core/services/csp/proyecto-concepto-gasto.service';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { BehaviorSubject, Subject } from 'rxjs';
import { PROYECTO_CONCEPTO_GASTO_DATA_KEY } from './proyecto-concepto-gasto-data.resolver';
import { ProyectoConceptoGastoCodigoEcFragment } from './proyecto-concepto-gasto-formulario/proyecto-concepto-gasto-codigo-ec/proyecto-concepto-gasto-codigo-ec.fragment';
import { ProyectoConceptoGastoDatosGeneralesFragment } from './proyecto-concepto-gasto-formulario/proyecto-concepto-gasto-datos-generales/proyecto-concepto-gasto-datos-generales.fragment';
import { PROYECTO_CONCEPTO_GASTO_ROUTE_PARAMS } from './proyecto-concepto-gasto-route-params';

export const CONVOCATORIA_CONCEPTO_GASTO_ID_KEY = 'convocatoriaConceptoGastoId';

export interface IProyectoConceptoGastoData {
  proyecto: IProyecto;
  selectedProyectoConceptosGasto: IProyectoConceptoGasto[];
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

  get proyectoConceptoGastos(): IProyectoConceptoGasto[] {
    return this.data.selectedProyectoConceptosGasto;
  }

  get permitido(): boolean {
    return this.data.permitido;
  }

  get conceptoGasto(): IConceptoGasto {
    return this.datosGenerales.getValue().conceptoGasto;
  }


  constructor(
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

    this.datosGenerales = new ProyectoConceptoGastoDatosGeneralesFragment(id, this.data.proyecto,
      proyectoConceptoGastoService, this.proyectoConceptoGastos, this.data.permitido, this.data.readonly);

    this.codigosEconomicos = new ProyectoConceptoGastoCodigoEcFragment(id, this.data.proyecto, convocatoriaConceptoGastoId,
      proyectoConceptoGastoService, proyectoConceptoGastoCodigoEcService, convocatoriaConceptoGastoService,
      codigoEconomicoGastoService, this.data.readonly);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.CODIGOS_ECONOMICOS, this.codigosEconomicos);

    this.subscriptions.push(this.datosGenerales.conceptoGasto$.subscribe(
      (conceptoGasto => this.blockAddCodigosEconomicos$.next(!Boolean(conceptoGasto)))
    ));

    // Inicializamos los datos generales
    this.datosGenerales.initialize();
  }

  private loadConvocatoriaConceptoGasto(id: number): void {
    if (id) {
      this.convocatoriaConceptoGastoService.findById(id).subscribe(convocatoriaConceptoGasto => {
        this.datosGenerales.setDatosConvocatoriaConceptoGasto(convocatoriaConceptoGasto);
      });
    }
  }

}
