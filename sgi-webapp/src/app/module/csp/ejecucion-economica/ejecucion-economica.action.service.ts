import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IConfiguracion } from '@core/models/csp/configuracion';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { IRelacionEjecucionEconomica, TipoEntidad } from '@core/models/csp/relacion-ejecucion-economica';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { IPersona } from '@core/models/sgp/persona';
import { ActionService } from '@core/services/action-service';
import { GastoProyectoService } from '@core/services/csp/gasto-proyecto/gasto-proyecto-service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoConceptoGastoCodigoEcService } from '@core/services/csp/proyecto-concepto-gasto-codigo-ec.service';
import { ProyectoConceptoGastoService } from '@core/services/csp/proyecto-concepto-gasto.service';
import { ProyectoPeriodoJustificacionSeguimientoService } from '@core/services/csp/proyecto-periodo-justificacion-seguimiento/proyecto-periodo-justificacion-seguimiento.service';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { ProyectoPeriodoSeguimientoService } from '@core/services/csp/proyecto-periodo-seguimiento.service';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { ProyectoSeguimientoEjecucionEconomicaService } from '@core/services/csp/proyecto-seguimiento-ejecucion-economica/proyecto-seguimiento-ejecucion-economica.service';
import { ProyectoSeguimientoJustificacionService } from '@core/services/csp/proyecto-seguimiento-justificacion/proyecto-seguimiento-justificacion.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RequerimientoJustificacionService } from '@core/services/csp/requerimiento-justificacion/requerimiento-justificacion.service';
import { CalendarioFacturacionService } from '@core/services/sge/calendario-facturacion.service';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { GastoService } from '@core/services/sge/gasto/gasto.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { filter } from 'rxjs/operators';
import { EJECUCION_ECONOMICA_DATA_KEY } from './ejecucion-economica-data.resolver';
import { DetalleOperacionesGastosFragment } from './ejecucion-economica-formulario/detalle-operaciones-gastos/detalle-operaciones-gastos.fragment';
import { DetalleOperacionesIngresosFragment } from './ejecucion-economica-formulario/detalle-operaciones-ingresos/detalle-operaciones-ingresos.fragment';
import { DetalleOperacionesModificacionesFragment } from './ejecucion-economica-formulario/detalle-operaciones-modificaciones/detalle-operaciones-modificaciones.fragment';
import { EjecucionPresupuestariaEstadoActualFragment } from './ejecucion-economica-formulario/ejecucion-presupuestaria-estado-actual/ejecucion-presupuestaria-estado-actual.fragment';
import { EjecucionPresupuestariaGastosFragment } from './ejecucion-economica-formulario/ejecucion-presupuestaria-gastos/ejecucion-presupuestaria-gastos.fragment';
import { EjecucionPresupuestariaIngresosFragment } from './ejecucion-economica-formulario/ejecucion-presupuestaria-ingresos/ejecucion-presupuestaria-ingresos.fragment';
import { FacturasEmitidasFragment } from './ejecucion-economica-formulario/facturas-emitidas/facturas-emitidas.fragment';
import { FacturasGastosFragment } from './ejecucion-economica-formulario/facturas-gastos/facturas-gastos.fragment';
import { PersonalContratadoFragment } from './ejecucion-economica-formulario/personal-contratado/personal-contratado.fragment';
import { ProyectosFragment } from './ejecucion-economica-formulario/proyectos/proyectos.fragment';
import { SeguimientoJustificacionRequerimientosFragment } from './ejecucion-economica-formulario/seguimiento-justificacion-requerimientos/seguimiento-justificacion-requerimientos.fragment';
import { SeguimientoJustificacionResumenFragment } from './ejecucion-economica-formulario/seguimiento-justificacion-resumen/seguimiento-justificacion-resumen.fragment';
import { ValidacionGastosFragment } from './ejecucion-economica-formulario/validacion-gastos/validacion-gastos.fragment';
import { ViajesDietasFragment } from './ejecucion-economica-formulario/viajes-dietas/viajes-dietas.fragment';
import { EJECUCION_ECONOMICA_ROUTE_PARAMS } from './ejecucion-economica-route-params';

export interface IEjecucionEconomicaData {
  readonly: boolean;
  proyectoSge: IProyectoSge;
  relaciones: IRelacionEjecucionEconomicaWithResponsables[];
  configuracion: IConfiguracion;
}

export interface IRelacionEjecucionEconomicaWithResponsables extends IRelacionEjecucionEconomica {
  responsables: IPersona[];
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
    VALIDACION_GASTOS: 'validacion-gastos',
    FACTURAS_EMITIDAS: 'facturas-emitidas',
    SEGUIMIENTO_JUSTIFICACION_RESUMEN: 'seguimiento-justificacion-resumen',
    SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTOS: 'seguimiento-justificacion-requerimientos'
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
  private facturasEmitidas: FacturasEmitidasFragment;
  private seguimientoJustificacionResumen: SeguimientoJustificacionResumenFragment;
  private seguimientoJustificacionRequerimientos: SeguimientoJustificacionRequerimientosFragment;

  private readonly data: IEjecucionEconomicaData;

  constructor(
    route: ActivatedRoute,
    proyectoService: ProyectoService,
    proyectoAnualidadService: ProyectoAnualidadService,
    ejecucionEconomicaService: EjecucionEconomicaService,
    gastoProyectoService: GastoProyectoService,
    gastoService: GastoService,
    calendarioFacturacionService: CalendarioFacturacionService,
    proyectoConceptoGastoCodigoEcService: ProyectoConceptoGastoCodigoEcService,
    proyectoConceptoGastoService: ProyectoConceptoGastoService,
    proyectoSeguimientoEjecucionEconomicaService: ProyectoSeguimientoEjecucionEconomicaService,
    empresaService: EmpresaService,
    proyectoPeriodoJustificacionService: ProyectoPeriodoJustificacionService,
    requerimientoJustificacionService: RequerimientoJustificacionService,
    proyectoProyectoSgeService: ProyectoProyectoSgeService,
    proyectoPeriodoSeguimientoService: ProyectoPeriodoSeguimientoService,
    proyectoSeguimientoJustificacionService: ProyectoSeguimientoJustificacionService,
    proyectoPeriodoJustificacionSeguimientoService: ProyectoPeriodoJustificacionSeguimientoService
  ) {
    super();

    this.data = route.snapshot.data[EJECUCION_ECONOMICA_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(EJECUCION_ECONOMICA_ROUTE_PARAMS.ID));

    this.proyectos = new ProyectosFragment(id, this.data.proyectoSge, this.data.relaciones, proyectoService);

    this.ejecucionPresupuestariaEstadoActual = new EjecucionPresupuestariaEstadoActualFragment(
      id, this.data.proyectoSge, this.data.relaciones,
      proyectoService, proyectoAnualidadService, ejecucionEconomicaService);

    this.ejecucionPresupuestariaGastos = new EjecucionPresupuestariaGastosFragment(
      id, this.data.proyectoSge, this.data.relaciones,
      proyectoService, proyectoAnualidadService, ejecucionEconomicaService);

    this.ejecucionPresupuestariaIngresos = new EjecucionPresupuestariaIngresosFragment(
      id, this.data.proyectoSge, this.data.relaciones,
      proyectoService, proyectoAnualidadService, ejecucionEconomicaService);

    this.detalleOperacionesGastos = new DetalleOperacionesGastosFragment(
      id, this.data.proyectoSge, this.data.relaciones,
      proyectoService, proyectoAnualidadService, ejecucionEconomicaService);

    this.detalleOperacionesIngresos = new DetalleOperacionesIngresosFragment(
      id, this.data.proyectoSge, this.data.relaciones,
      proyectoService, proyectoAnualidadService, ejecucionEconomicaService);

    this.detalleOperacionesModificaciones = new DetalleOperacionesModificacionesFragment(
      id, this.data.proyectoSge, this.data.relaciones,
      proyectoService, proyectoAnualidadService, ejecucionEconomicaService);

    this.facturasGastos = new FacturasGastosFragment(
      id, this.data.proyectoSge, this.data.relaciones,
      proyectoService, proyectoAnualidadService,
      gastoProyectoService, ejecucionEconomicaService, proyectoConceptoGastoCodigoEcService,
      proyectoConceptoGastoService, this.data.configuracion);

    this.viajesDietas = new ViajesDietasFragment(
      id, this.data.proyectoSge, this.data.relaciones,
      proyectoService, proyectoAnualidadService,
      gastoProyectoService, ejecucionEconomicaService, proyectoConceptoGastoCodigoEcService,
      proyectoConceptoGastoService, this.data.configuracion);

    this.personalContratado = new PersonalContratadoFragment(
      id, this.data.proyectoSge, this.data.relaciones,
      proyectoService, proyectoAnualidadService,
      gastoProyectoService, ejecucionEconomicaService, proyectoConceptoGastoCodigoEcService,
      proyectoConceptoGastoService, this.data.configuracion);

    this.validacionGastos = new ValidacionGastosFragment(
      id, this.data.proyectoSge, gastoService, proyectoService, gastoProyectoService);

    this.facturasEmitidas = new FacturasEmitidasFragment(
      id, this.data.proyectoSge, this.data.relaciones,
      proyectoService, proyectoAnualidadService, calendarioFacturacionService);

    this.seguimientoJustificacionResumen = new SeguimientoJustificacionResumenFragment(
      id, this.data.proyectoSge, this.data.relaciones.filter(relacion => relacion.tipoEntidad === TipoEntidad.PROYECTO),
      this.data.configuracion, proyectoService, proyectoSeguimientoEjecucionEconomicaService, empresaService,
      proyectoPeriodoJustificacionService, proyectoPeriodoSeguimientoService, proyectoSeguimientoJustificacionService,
      proyectoPeriodoJustificacionSeguimientoService
    );

    this.seguimientoJustificacionRequerimientos = new SeguimientoJustificacionRequerimientosFragment(
      id, proyectoSeguimientoEjecucionEconomicaService, requerimientoJustificacionService,
      proyectoProyectoSgeService, proyectoPeriodoJustificacionService
    );

    this.subscriptions.push(
      this.seguimientoJustificacionResumen.initialized$
        .pipe(
          filter(initialized => initialized)
        )
        .subscribe(() => {
          if (this.seguimientoJustificacionRequerimientos.isInitialized()) {
            // Si se inicializa el Resumen y se paso antes por Requerimientos se pasa al Resumen
            // el estado actual de los requerimientos para el seguimiento por anualidad.
            this.seguimientoJustificacionResumen.onRequerimientosJustificacionChanged(
              this.seguimientoJustificacionRequerimientos.getCurrentRequerimientosJustificacion()
            );
          } else {
            // Si se inicializa el Resumen y aun no se inicializo Requerimientos, se inicializa Requerimientos
            // para sincronizar el identificador de justificacion de los requerimientos.
            this.seguimientoJustificacionRequerimientos.initialize();
          }
        })
    );

    this.subscriptions.push(
      this.seguimientoJustificacionRequerimientos.getRequerimientoJustificacionAfterDeletion$()
        .pipe(
          filter(() => this.seguimientoJustificacionResumen.isInitialized())
        )
        .subscribe(requerimientosJustificacion =>
          this.seguimientoJustificacionResumen.onRequerimientosJustificacionChanged(requerimientosJustificacion)
        )
    );

    this.subscriptions.push(
      this.seguimientoJustificacionResumen.getPeriodoJustificacionChanged$()
        .pipe(
          filter(() => this.seguimientoJustificacionRequerimientos.isInitialized())
        )
        .subscribe(periodoJustificacion => this.seguimientoJustificacionRequerimientos.onPeriodoJustificacionChanged(periodoJustificacion))
    );

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
    this.addFragment(this.FRAGMENT.FACTURAS_EMITIDAS, this.facturasEmitidas);
    this.addFragment(this.FRAGMENT.SEGUIMIENTO_JUSTIFICACION_RESUMEN, this.seguimientoJustificacionResumen);
    this.addFragment(this.FRAGMENT.SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTOS, this.seguimientoJustificacionRequerimientos);
  }
}
