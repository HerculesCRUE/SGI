import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TipoPartida } from '@core/enums/tipo-partida';
import { IAnualidadResumen } from '@core/models/csp/anualidad-resumen';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoAnualidadResumen } from '@core/models/csp/proyecto-anualidad-resumen';
import { ActionService } from '@core/services/action-service';
import { AnualidadGastoService } from '@core/services/csp/anualidad-gasto/anualidad-gasto.service';
import { AnualidadIngresoService } from '@core/services/csp/anualidad-ingreso/anualidad-ingreso.service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { CodigoEconomicoIngresoService } from '@core/services/sge/codigo-economico-ingreso.service';
import { NGXLogger } from 'ngx-logger';
import { PROYECTO_ANUALIDAD_DATA_KEY } from './proyecto-anualidad-data.resolver';
import { ProyectoAnualidadDatosGeneralesFragment } from './proyecto-anualidad-formulario/proyecto-anualidad-datos-generales/proyecto-anualidad-datos-generales.fragment';
import { ProyectoAnualidadGastosFragment } from './proyecto-anualidad-formulario/proyecto-anualidad-gastos/proyecto-anualidad-gastos.fragment';
import { ProyectoAnualidadIngresosFragment } from './proyecto-anualidad-formulario/proyecto-anualidad-ingresos/proyecto-anualidad-ingresos.fragment';
import { ProyectoAnualidadResumenFragment } from './proyecto-anualidad-formulario/proyecto-anualidad-resumen/proyecto-anualidad-resumen.fragment';
import { PROYECTO_ANUALIDAD_ROUTE_PARAMS } from './proyecto-anualidad-route-params';

export interface IProyectoAnualidadData {
  proyecto: IProyecto;
  proyectoAnualidadResumen: IProyectoAnualidadResumen[];
  readonly: boolean;
}

@Injectable()
export class ProyectoAnualidadActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    GASTOS: 'gastos',
    INGRESOS: 'ingresos',
    RESUMEN: 'resumen'
  };

  private datosGenerales: ProyectoAnualidadDatosGeneralesFragment;
  private anualidadGastos: ProyectoAnualidadGastosFragment;
  private anualidadIngresos: ProyectoAnualidadIngresosFragment;
  private anualidadResumen: ProyectoAnualidadResumenFragment;

  private readonly data: IProyectoAnualidadData;

  get proyectoModeloEjecucionId(): number {
    return this.data.proyecto.modeloEjecucion.id;
  }

  get anualidades(): IProyectoAnualidadResumen[] {
    return this.data.proyectoAnualidadResumen;
  }

  get readonly(): boolean {
    return this.data.readonly;
  }

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    proyectoAnualidadService: ProyectoAnualidadService,
    anualidadGastoService: AnualidadGastoService,
    anualidadIngresoService: AnualidadIngresoService,
    codigoEconomicoGastoService: CodigoEconomicoGastoService,
    codigoEconomicoIngresoService: CodigoEconomicoIngresoService
  ) {
    super();
    this.data = route.snapshot.data[PROYECTO_ANUALIDAD_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(PROYECTO_ANUALIDAD_ROUTE_PARAMS.ID));

    if (id) {
      this.enableEdit();
    }

    this.datosGenerales = new ProyectoAnualidadDatosGeneralesFragment(
      id, this.data.proyecto, proyectoAnualidadService, this.data.readonly);
    this.anualidadGastos = new ProyectoAnualidadGastosFragment(
      logger, id, this.data.proyecto.id, proyectoAnualidadService, anualidadGastoService, codigoEconomicoGastoService);
    this.anualidadIngresos = new ProyectoAnualidadIngresosFragment(
      logger, id, this.data.proyecto.id, proyectoAnualidadService, anualidadIngresoService, codigoEconomicoIngresoService);
    this.anualidadResumen = new ProyectoAnualidadResumenFragment(id, proyectoAnualidadService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.GASTOS, this.anualidadGastos);
    this.addFragment(this.FRAGMENT.INGRESOS, this.anualidadIngresos);
    this.addFragment(this.FRAGMENT.RESUMEN, this.anualidadResumen);

    this.subscriptions.push(this.datosGenerales.fechaInicio$.subscribe(value => {
      if (value) {
        this.anualidadGastos.fechaInicioAnualidad = value;
      }
    }));

    this.subscriptions.push(this.anualidadGastos.anualidadGastos$.subscribe(anualidadesGastos => {
      const anualidadesIngresos =
        this.anualidadResumen.anualidades$.value.filter(anualidadResumenIngreso => anualidadResumenIngreso.tipo === TipoPartida.INGRESO);

      const anulidadesGasto = anualidadesGastos.map(anualidadGasto => {
        return {
          tipo: TipoPartida.GASTO,
          codigoPartidaPresupuestaria: anualidadGasto.value.proyectoPartida.codigo,
          importePresupuesto: anualidadGasto.value.importePresupuesto,
          importeConcedido: anualidadGasto.value.importeConcedido
        } as IAnualidadResumen;
      });

      this.anualidadResumen.anualidades$.next(anulidadesGasto.concat(anualidadesIngresos));

    }));

    this.subscriptions.push(this.anualidadIngresos.anualidadIngresos$.subscribe(anualidadesIngresos => {
      const anualidadesGastos =
        this.anualidadResumen.anualidades$.value.filter(anualidadResumenGastos => anualidadResumenGastos.tipo === TipoPartida.GASTO);

      const anulidadesIngreso = anualidadesIngresos.map(anualidadIngreso => {
        return {
          tipo: TipoPartida.INGRESO,
          codigoPartidaPresupuestaria: anualidadIngreso.value.proyectoPartida.codigo,
          importeConcedido: anualidadIngreso.value.importeConcedido
        } as IAnualidadResumen;
      });

      this.anualidadResumen.anualidades$.next(anualidadesGastos.concat(anulidadesIngreso));

    }));


    this.subscriptions.push(this.datosGenerales.fechaFin$.subscribe(value => {
      if (value) {
        this.anualidadGastos.fechaFinAnualidad = value;
      }
    }));

  }
}
