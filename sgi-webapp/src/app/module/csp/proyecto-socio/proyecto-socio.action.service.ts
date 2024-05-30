import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Estado } from '@core/models/csp/estado-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { ActionService } from '@core/services/action-service';
import { ProyectoSocioEquipoService } from '@core/services/csp/proyecto-socio-equipo.service';
import { ProyectoSocioPeriodoJustificacionService } from '@core/services/csp/proyecto-socio-periodo-justificacion.service';
import { ProyectoSocioPeriodoPagoService } from '@core/services/csp/proyecto-socio-periodo-pago.service';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { NGXLogger } from 'ngx-logger';
import { PROYECTO_SOCIO_DATA_KEY } from './proyecto-socio-data.resolver';
import { ProyectoSocioDatosGeneralesFragment } from './proyecto-socio-formulario/proyecto-socio-datos-generales/proyecto-socio-datos-generales.fragment';
import { ProyectoSocioEquipoFragment } from './proyecto-socio-formulario/proyecto-socio-equipo/proyecto-socio-equipo.fragment';
import { ProyectoSocioPeriodoJustificacionFragment } from './proyecto-socio-formulario/proyecto-socio-periodo-justificacion/proyecto-socio-periodo-justificacion.fragment';
import { ProyectoSocioPeriodoPagoFragment } from './proyecto-socio-formulario/proyecto-socio-periodo-pago/proyecto-socio-periodo-pago.fragment';
import { PROYECTO_SOCIO_ROUTE_PARAMS } from './proyecto-socio-route-params';

export interface IProyectoSocioData {
  proyecto: IProyecto;
  proyectoSocios: IProyectoSocio[];
  readonly: boolean;
}

@Injectable()
export class ProyectoSocioActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    EQUIPO: 'equipo',
    PERIODO_PAGO: 'periodo-pago',
    PERIODO_JUSTIFICACION: 'periodo-justificacion'
  };

  private datosGenerales: ProyectoSocioDatosGeneralesFragment;
  private equipo: ProyectoSocioEquipoFragment;
  private periodoPago: ProyectoSocioPeriodoPagoFragment;
  private periodosJustificacion: ProyectoSocioPeriodoJustificacionFragment;

  private data: IProyectoSocioData;

  get coordinadorExterno(): boolean {
    return !this.data.proyecto.rolUniversidad.coordinador;
  }

  get proyectoSocios(): IProyectoSocio[] {
    return this.data.proyectoSocios;
  }

  get proyectoSocio(): IProyectoSocio {
    return this.datosGenerales.getValue();
  }

  get proyectoEstado(): Estado {
    return this.data?.proyecto?.estado?.estado;
  }

  get showPeriodoJustificacion(): boolean {
    return !this.coordinadorExterno;
  }

  get showPeriodoPago(): boolean {
    return !this.coordinadorExterno;
  }

  get readonly(): boolean {
    return this.data.readonly;
  }

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    empresaService: EmpresaService,
    proyectoSocioService: ProyectoSocioService,
    proyectoEquipoSocioService: ProyectoSocioEquipoService,
    personaService: PersonaService,
    proyectoSocioPeriodoPagoService: ProyectoSocioPeriodoPagoService,
    proyectoSocioPeriodoJustificacionService: ProyectoSocioPeriodoJustificacionService
  ) {
    super();

    this.data = route.snapshot.data[PROYECTO_SOCIO_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(PROYECTO_SOCIO_ROUTE_PARAMS.ID));
    if (id) {
      this.enableEdit();
    }

    this.datosGenerales = new ProyectoSocioDatosGeneralesFragment(id, this.data.proyecto.id,
      proyectoSocioService, empresaService, this.data.readonly);
    this.equipo = new ProyectoSocioEquipoFragment(logger, id, proyectoSocioService,
      proyectoEquipoSocioService, personaService);
    this.periodoPago = new ProyectoSocioPeriodoPagoFragment(logger, id, proyectoSocioService,
      proyectoSocioPeriodoPagoService);
    this.periodosJustificacion = new ProyectoSocioPeriodoJustificacionFragment(logger, id,
      proyectoSocioService, proyectoSocioPeriodoJustificacionService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.EQUIPO, this.equipo);
    this.addFragment(this.FRAGMENT.PERIODO_PAGO, this.periodoPago);
    this.addFragment(this.FRAGMENT.PERIODO_JUSTIFICACION, this.periodosJustificacion);

    // Inicializamos por defectos los datos generales
    this.datosGenerales.initialize();
  }
}
