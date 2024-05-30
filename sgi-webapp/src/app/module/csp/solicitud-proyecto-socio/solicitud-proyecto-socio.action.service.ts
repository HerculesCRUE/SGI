import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { ISolicitudProyectoSocioPeriodoJustificacion } from '@core/models/csp/solicitud-proyecto-socio-periodo-justificacion';
import { ISolicitudProyectoSocioPeriodoPago } from '@core/models/csp/solicitud-proyecto-socio-periodo-pago';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { ActionService } from '@core/services/action-service';
import { SolicitudProyectoSocioEquipoService } from '@core/services/csp/solicitud-proyecto-socio-equipo.service';
import { SolicitudProyectoSocioPeriodoJustificacionService } from '@core/services/csp/solicitud-proyecto-socio-periodo-justificacion.service';
import { SolicitudProyectoSocioPeriodoPagoService } from '@core/services/csp/solicitud-proyecto-socio-periodo-pago.service';
import { SolicitudProyectoSocioService } from '@core/services/csp/solicitud-proyecto-socio.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject } from 'rxjs';
import { SOLICITUD_PROYECTO_SOCIO_DATA_KEY } from './solicitud-proyecto-socio-data.resolver';
import { SolicitudProyectoSocioDatosGeneralesFragment } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-datos-generales/solicitud-proyecto-socio-datos-generales.fragment';
import { SolicitudProyectoSocioEquipoFragment } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-equipo/solicitud-proyecto-socio-equipo.fragment';
import { SolicitudProyectoSocioPeriodoJustificacionFragment } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-periodo-justificacion/solicitud-proyecto-socio-periodo-justificacion.fragment';
import { SolicitudProyectoSocioPeriodoPagoFragment } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-periodo-pago/solicitud-proyecto-socio-periodo-pago.fragment';
import { SOLICITUD_PROYECTO_SOCIO_ROUTE_PARAMS } from './solicitud-proyecto-socio-route-params';

export interface ISolicitudProyectoSocioData {
  readonly: boolean;
  solicitudProyecto: ISolicitudProyecto;
  solicitudProyectoSocios: ISolicitudProyectoSocio[];
}

@Injectable()
export class SolicitudProyectoSocioActionService extends ActionService {
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    PERIODOS_PAGOS: 'periodos-pagos',
    PERIODOS_JUSTIFICACION: 'periodos-justificacion',
    EQUIPO: 'equipo'
  };

  private datosGenerales: SolicitudProyectoSocioDatosGeneralesFragment;
  private periodosPago: SolicitudProyectoSocioPeriodoPagoFragment;
  private periodosJustificacion: SolicitudProyectoSocioPeriodoJustificacionFragment;
  private equipo: SolicitudProyectoSocioEquipoFragment;

  private readonly data: ISolicitudProyectoSocioData;

  get solicitudProyectoCoordinadorExterno(): boolean {
    return !this.data.solicitudProyecto.rolUniversidad?.coordinador;
  }

  get solicitudProyectoDuracion(): number {
    return this.data.solicitudProyecto.duracion;
  }

  get solicitudProyectoSocios(): ISolicitudProyectoSocio[] {
    return this.data.solicitudProyectoSocios;
  }

  get mesInicio(): number {
    return this.datosGenerales.getValue().mesInicio;
  }

  get mesFin(): number {
    return this.datosGenerales.getValue().mesFin;
  }

  get empresa(): IEmpresa {
    return this.datosGenerales.getValue().empresa;
  }

  get periodosPago$(): BehaviorSubject<StatusWrapper<ISolicitudProyectoSocioPeriodoPago>[]> {
    return this.periodosPago.periodoPagos$;
  }

  get periodosJustificacion$(): BehaviorSubject<StatusWrapper<ISolicitudProyectoSocioPeriodoJustificacion>[]> {
    return this.periodosJustificacion.periodoJustificaciones$;
  }

  get showPeriodoJustificacion(): boolean {
    return !this.solicitudProyectoCoordinadorExterno;
  }

  get showPeriodoPago(): boolean {
    return !this.solicitudProyectoCoordinadorExterno;
  }

  constructor(
    readonly logger: NGXLogger,
    route: ActivatedRoute,
    solicitudProyectoSocioService: SolicitudProyectoSocioService,
    solicitudProyectoSocioPeriodoPagoService: SolicitudProyectoSocioPeriodoPagoService,
    solicitudProyectoSocioPeriodoJustificacionService: SolicitudProyectoSocioPeriodoJustificacionService,
    solicitudProyectoSocioEquipoService: SolicitudProyectoSocioEquipoService,
    personaService: PersonaService
  ) {
    super();

    this.data = route.snapshot.data[SOLICITUD_PROYECTO_SOCIO_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(SOLICITUD_PROYECTO_SOCIO_ROUTE_PARAMS.ID));

    if (id) {
      this.enableEdit();
    }

    this.datosGenerales = new SolicitudProyectoSocioDatosGeneralesFragment(
      id, this.data.solicitudProyecto.id, solicitudProyectoSocioService, this.data.readonly);
    this.periodosPago = new SolicitudProyectoSocioPeriodoPagoFragment(logger, id,
      solicitudProyectoSocioService, solicitudProyectoSocioPeriodoPagoService, this.data.readonly);
    this.periodosJustificacion = new SolicitudProyectoSocioPeriodoJustificacionFragment(logger, id,
      solicitudProyectoSocioService, solicitudProyectoSocioPeriodoJustificacionService, this.data.readonly);
    this.equipo = new SolicitudProyectoSocioEquipoFragment(logger, id,
      solicitudProyectoSocioService, solicitudProyectoSocioEquipoService, personaService, this.data.readonly);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

    this.addFragment(this.FRAGMENT.PERIODOS_PAGOS, this.periodosPago);
    this.addFragment(this.FRAGMENT.PERIODOS_JUSTIFICACION, this.periodosJustificacion);
    this.addFragment(this.FRAGMENT.EQUIPO, this.equipo);

    // Inicializamos los datos generales ya que son necesarios en todas las pestañas
    this.datosGenerales.initialize();
  }

}
