import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { ActionService } from '@core/services/action-service';
import { ConvocatoriaConceptoGastoPublicService } from '@core/services/csp/convocatoria-concepto-gasto-public.service';
import { CodigoEconomicoGastoPublicService } from '@core/services/sge/codigo-economico-gasto-public.service';
import { ConvocatoriaConceptoGastoCodigoEcPublicFragment } from './convocatoria-concepto-gasto-formulario/convocatoria-concepto-gasto-codigo-ec/convocatoria-concepto-gasto-codigo-ec-public.fragment';
import { ConvocatoriaConceptoGastoDatosGeneralesPublicFragment } from './convocatoria-concepto-gasto-formulario/convocatoria-concepto-gasto-datos-generales/convocatoria-concepto-gasto-datos-generales-public.fragment';
import { CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY } from './convocatoria-concepto-gasto-public-data.resolver';
import { CONVOCATORIA_CONCEPTO_GASTO_PUBLIC_ROUTE_PARAMS } from './convocatoria-concepto-gasto-public-route-params';

export interface IConvocatoriaConceptoGastoPublicData {
  convocatoria: IConvocatoria;
  permitido: boolean;
}

@Injectable()
export class ConvocatoriaConceptoGastoPublicActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    CODIGOS_ECONOMICOS: 'codigosEconomicos'
  };

  private datosGenerales: ConvocatoriaConceptoGastoDatosGeneralesPublicFragment;
  private codigosEconomicos: ConvocatoriaConceptoGastoCodigoEcPublicFragment;

  private readonly data: IConvocatoriaConceptoGastoPublicData;

  get permitido(): boolean {
    return this.data.permitido;
  }

  get conceptoGasto(): IConceptoGasto {
    return this.datosGenerales.getValue().conceptoGasto;
  }

  constructor(
    route: ActivatedRoute,
    convocatoriaConceptoGastoService: ConvocatoriaConceptoGastoPublicService,
    codigoEconomicoGastoService: CodigoEconomicoGastoPublicService
  ) {
    super();
    this.data = route.snapshot.data[CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY];
    const id = route.snapshot.paramMap.get(CONVOCATORIA_CONCEPTO_GASTO_PUBLIC_ROUTE_PARAMS.ID)
      ? Number(route.snapshot.paramMap.get(CONVOCATORIA_CONCEPTO_GASTO_PUBLIC_ROUTE_PARAMS.ID))
      : undefined;

    if (id) {
      this.enableEdit();
    }

    this.datosGenerales = new ConvocatoriaConceptoGastoDatosGeneralesPublicFragment(id, this.data.convocatoria,
      convocatoriaConceptoGastoService, this.data.permitido);

    this.codigosEconomicos = new ConvocatoriaConceptoGastoCodigoEcPublicFragment(id,
      convocatoriaConceptoGastoService, codigoEconomicoGastoService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.CODIGOS_ECONOMICOS, this.codigosEconomicos);

    // Inicializamos los datos generales
    this.datosGenerales.initialize();
  }
}
