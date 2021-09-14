import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { ActionService } from '@core/services/action-service';
import { GastoProyectoService } from '@core/services/csp/gasto-proyecto/gasto-proyecto-service';
import { ProyectoAgrupacionGastoService } from '@core/services/csp/proyecto-agrupacion-gasto/proyecto-agrupacion-gasto.service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { GastoService } from '@core/services/sge/gasto/gasto.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { EJECUCION_ECONOMICA_DATA_KEY } from './ejecucion-economica-data.resolver';
import { DetalleOperacionesGastosFragment } from './ejecucion-economica-formulario/detalle-operaciones-gastos/detalle-operaciones-gastos.fragment';
import { DetalleOperacionesIngresosFragment } from './ejecucion-economica-formulario/detalle-operaciones-ingresos/detalle-operaciones-ingresos.fragment';
import { DetalleOperacionesModificacionesFragment } from './ejecucion-economica-formulario/detalle-operaciones-modificaciones/detalle-operaciones-modificaciones.fragment';
import { EjecucionPresupuestariaEstadoActualFragment } from './ejecucion-economica-formulario/ejecucion-presupuestaria-estado-actual/ejecucion-presupuestaria-estado-actual.fragment';
import { EjecucionPresupuestariaGastosFragment } from './ejecucion-economica-formulario/ejecucion-presupuestaria-gastos/ejecucion-presupuestaria-gastos.fragment';
import { EjecucionPresupuestariaIngresosFragment } from './ejecucion-economica-formulario/ejecucion-presupuestaria-ingresos/ejecucion-presupuestaria-ingresos.fragment';
import { FacturasGastosFragment } from './ejecucion-economica-formulario/facturas-gastos/facturas-gastos.fragment';
import { PersonalContratadoFragment } from './ejecucion-economica-formulario/personal-contratado/personal-contratado.fragment';
import { ProyectosFragment } from './ejecucion-economica-formulario/proyectos/proyectos.fragment';
import { ValidacionGastosFragment } from './ejecucion-economica-formulario/validacion-gastos/validacion-gastos.fragment';
import { ViajesDietasFragment } from './ejecucion-economica-formulario/viajes-dietas/viajes-dietas.fragment';
import { EJECUCION_ECONOMICA_ROUTE_PARAMS } from './ejecucion-economica-route-params';

export interface IEjecucionEconomicaData {
  readonly: boolean;
  proyectoSge: IProyectoSge;
  proyectosRelacionados: IProyecto[];
}

@Injectable()
export class EjecucionEconomicaActionService extends ActionService {

  public readonly FRAGMENT = {
    PROYECTOS: 'proyectos',
    EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL: 'ejecucion-presupuestaria-estado-actual',
    EJECUCION_PRESUPUESTARIA_GASTOS: 'ejecucion-presupuestaria-gastos',
    EJECUCION_PRESUPUESTARIA_INGRESOS: 'ejecucion-presupuestaria-ingresos',
    DETALLE_OPERACIONES_GASTOS: 'detalle-operaciones-gastos',
    DETALLE_OPERACIONES_INGRESOS: 'detalle-operaciones-ingresos',
    DETALLE_OPERACIONES_MODIFICACIONES: 'detalle-operaciones-modificaciones',
    FACTURAS_GASTOS: 'facturas-gastos',
    VIAJES_DIETAS: 'viajes-dietas',
    PERSONAL_CONTRATADO: 'personal-contratado',
    VALIDACION_GASTOS: 'validacion-gastos'
  };

  private proyectos: ProyectosFragment;
  private ejecucionPresupuestariaEstadoActual: EjecucionPresupuestariaEstadoActualFragment;
  private ejecucionPresupuestariaGastos: EjecucionPresupuestariaGastosFragment;
  private ejecucionPresupuestariaIngresos: EjecucionPresupuestariaIngresosFragment;
  private detalleOperacionesGastos: DetalleOperacionesGastosFragment;
  private detalleOperacionesIngresos: DetalleOperacionesIngresosFragment;
  private detalleOperacionesModificaciones: DetalleOperacionesModificacionesFragment;
  private facturasGastos: FacturasGastosFragment;
  private viajesDietas: ViajesDietasFragment;
  private personalContratado: PersonalContratadoFragment;
  private validacionGastos: ValidacionGastosFragment;

  private readonly data: IEjecucionEconomicaData;

  constructor(
    route: ActivatedRoute,
    proyectoService: ProyectoService,
    personaService: PersonaService,
    proyectoAnualidadService: ProyectoAnualidadService,
    ejecucionEconomicaService: EjecucionEconomicaService,
    proyectoAgrupacionGastoService: ProyectoAgrupacionGastoService,
    gastoProyectoService: GastoProyectoService,
    gastoService: GastoService
  ) {
    super();

    this.data = route.snapshot.data[EJECUCION_ECONOMICA_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(EJECUCION_ECONOMICA_ROUTE_PARAMS.ID));

    this.proyectos = new ProyectosFragment(id, this.data.proyectoSge, this.data.proyectosRelacionados);

    this.ejecucionPresupuestariaEstadoActual = new EjecucionPresupuestariaEstadoActualFragment(
      id, this.data.proyectoSge, this.data.proyectosRelacionados,
      proyectoService, personaService, proyectoAnualidadService, ejecucionEconomicaService);

    this.ejecucionPresupuestariaGastos = new EjecucionPresupuestariaGastosFragment(
      id, this.data.proyectoSge, this.data.proyectosRelacionados,
      proyectoService, personaService, proyectoAnualidadService, ejecucionEconomicaService);

    this.ejecucionPresupuestariaIngresos = new EjecucionPresupuestariaIngresosFragment(
      id, this.data.proyectoSge, this.data.proyectosRelacionados,
      proyectoService, personaService, proyectoAnualidadService, ejecucionEconomicaService);

    this.detalleOperacionesGastos = new DetalleOperacionesGastosFragment(
      id, this.data.proyectoSge, this.data.proyectosRelacionados,
      proyectoService, personaService, proyectoAnualidadService, ejecucionEconomicaService);

    this.detalleOperacionesIngresos = new DetalleOperacionesIngresosFragment(
      id, this.data.proyectoSge, this.data.proyectosRelacionados,
      proyectoService, personaService, proyectoAnualidadService, ejecucionEconomicaService);

    this.detalleOperacionesModificaciones = new DetalleOperacionesModificacionesFragment(
      id, this.data.proyectoSge, this.data.proyectosRelacionados,
      proyectoService, personaService, proyectoAnualidadService, ejecucionEconomicaService);

    this.facturasGastos = new FacturasGastosFragment(
      id, this.data.proyectoSge, this.data.proyectosRelacionados,
      proyectoService, personaService, proyectoAnualidadService, proyectoAgrupacionGastoService,
      gastoProyectoService, ejecucionEconomicaService);

    this.viajesDietas = new ViajesDietasFragment(
      id, this.data.proyectoSge, this.data.proyectosRelacionados,
      proyectoService, personaService, proyectoAnualidadService, proyectoAgrupacionGastoService,
      gastoProyectoService, ejecucionEconomicaService);

    this.personalContratado = new PersonalContratadoFragment(
      id, this.data.proyectoSge, this.data.proyectosRelacionados,
      proyectoService, personaService, proyectoAnualidadService, proyectoAgrupacionGastoService,
      gastoProyectoService, ejecucionEconomicaService);

    this.validacionGastos = new ValidacionGastosFragment(
      id, this.data.proyectoSge, gastoService, proyectoService, gastoProyectoService, proyectoAgrupacionGastoService);

    this.addFragment(this.FRAGMENT.PROYECTOS, this.proyectos);
    this.addFragment(this.FRAGMENT.EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL, this.ejecucionPresupuestariaEstadoActual);
    this.addFragment(this.FRAGMENT.EJECUCION_PRESUPUESTARIA_GASTOS, this.ejecucionPresupuestariaGastos);
    this.addFragment(this.FRAGMENT.EJECUCION_PRESUPUESTARIA_INGRESOS, this.ejecucionPresupuestariaIngresos);
    this.addFragment(this.FRAGMENT.DETALLE_OPERACIONES_GASTOS, this.detalleOperacionesGastos);
    this.addFragment(this.FRAGMENT.DETALLE_OPERACIONES_INGRESOS, this.detalleOperacionesIngresos);
    this.addFragment(this.FRAGMENT.DETALLE_OPERACIONES_MODIFICACIONES, this.detalleOperacionesModificaciones);
    this.addFragment(this.FRAGMENT.FACTURAS_GASTOS, this.facturasGastos);
    this.addFragment(this.FRAGMENT.VIAJES_DIETAS, this.viajesDietas);
    this.addFragment(this.FRAGMENT.PERSONAL_CONTRATADO, this.personalContratado);
    this.addFragment(this.FRAGMENT.VALIDACION_GASTOS, this.validacionGastos);
  }

}
