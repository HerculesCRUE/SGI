import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IAgrupacionGastoConcepto } from '@core/models/csp/agrupacion-gasto-concepto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoAgrupacionGasto } from '@core/models/csp/proyecto-agrupacion-gasto';
import { ActionService } from '@core/services/action-service';
import { AgrupacionGastoConceptoService } from '@core/services/csp/agrupacio-gasto-concepto/agrupacion-gasto-concepto.service';
import { ProyectoAgrupacionGastoService } from '@core/services/csp/proyecto-agrupacion-gasto/proyecto-agrupacion-gasto.service';
import { NGXLogger } from 'ngx-logger';
import { PROYECTO_AGRUPACION_GASTO_DATA_KEY } from './proyecto-agrupacion-gasto-data.resolver';
import { AgrupacionGastoConceptoFragment } from './proyecto-agrupacion-gasto-formulario/agrupacion-gasto-concepto/agrupacion-gasto-concepto.fragment';
import { ProyectoAgrupacionGastoDatosGeneralesFragment } from './proyecto-agrupacion-gasto-formulario/proyecto-agrupacion-gasto-datos-generales/proyecto-agrupacion-gasto-datos-generales.fragment';
import { PROYECTO_AGRUPACION_GASTO_ROUTE_PARAMS } from './proyecto-agrupacion-gasto-route-params';

export interface IProyectoAgrupacionGastoData {
  proyecto: IProyecto;
  proyectoAgrupacionesGasto: IProyectoAgrupacionGasto[];
  readonly: boolean;
}
export interface IAgrupacionGastoConceptoData {
  agrupacion: IProyectoAgrupacionGasto;
  agrupacionGastoConceptos: IAgrupacionGastoConcepto[];
  readonly: boolean;
}

@Injectable()
export class ProyectoAgrupacionGastoActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    AGRUPACION_GASTO_CONCEPTO: 'agrupacionGastoConcepto',
  };

  private datosGenerales: ProyectoAgrupacionGastoDatosGeneralesFragment;
  private agrupacionGastoConcepto: AgrupacionGastoConceptoFragment;
  private data: IProyectoAgrupacionGastoData;

  get agrupaciones(): IProyectoAgrupacionGasto[] {
    return this.data.proyectoAgrupacionesGasto;
  }

  get proyectoId(): number {
    return this.data.proyecto.id;
  }

  get readonly(): boolean {
    return this.data.readonly;
  }

  constructor(
    logger: NGXLogger,
    service: ProyectoAgrupacionGastoService,
    agrupacioGastoConceptoService: AgrupacionGastoConceptoService,
    private proyectoAgrupacionGastoService: ProyectoAgrupacionGastoService,
    route: ActivatedRoute,
  ) {
    super();
    this.data = route.snapshot.data[PROYECTO_AGRUPACION_GASTO_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(PROYECTO_AGRUPACION_GASTO_ROUTE_PARAMS.ID));
    if (id) {
      this.enableEdit();
    }

    this.datosGenerales = new ProyectoAgrupacionGastoDatosGeneralesFragment(id, this.data.proyecto.id, service);
    this.agrupacionGastoConcepto = new AgrupacionGastoConceptoFragment(logger, id, agrupacioGastoConceptoService, proyectoAgrupacionGastoService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

    if (this.isEdit) {
      this.addFragment(this.FRAGMENT.AGRUPACION_GASTO_CONCEPTO, this.agrupacionGastoConcepto);
    }

    // Inicializamos por defectos los datos generales
    this.datosGenerales.initialize();
  }


}
