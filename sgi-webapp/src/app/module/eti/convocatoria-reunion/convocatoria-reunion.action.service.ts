import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { ActionService } from '@core/services/action-service';
import { AsistenteService } from '@core/services/eti/asistente.service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { CONVOCATORIA_REUNION_DATA_KEY } from './convocatoria-reunion-data.resolver';
import { ConvocatoriaReunionAsignacionMemoriasListadoFragment } from './convocatoria-reunion-formulario/convocatoria-reunion-asignacion-memorias/convocatoria-reunion-asignacion-memorias-listado/convocatoria-reunion-asignacion-memorias-listado.fragment';
import { ConvocatoriaReunionDatosGeneralesFragment } from './convocatoria-reunion-formulario/convocatoria-reunion-datos-generales/convocatoria-reunion-datos-generales.fragment';
import { CONVOCATORIA_REUNION_ROUTE_PARAMS } from './convocatoria-reunion-route-params';

export interface DatosAsignacionEvaluacion {
  idComite: number;
  idTipoConvocatoria: number;
  fechaLimite: DateTime;
}

export interface IConvocatoriaReunionData {
  convocatoriaReunion: IConvocatoriaReunion;
  readonly: boolean;
}

@Injectable()
export class ConvocatoriaReunionActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    ASIGNACION_MEMORIAS: 'asignacionMemorias'
  };

  convocatoriaReunion: IConvocatoriaReunion;
  private datosGenerales: ConvocatoriaReunionDatosGeneralesFragment;
  private asignacionMemorias: ConvocatoriaReunionAsignacionMemoriasListadoFragment;

  private readonly data: IConvocatoriaReunionData;

  get readonly(): boolean {
    return this.data?.readonly ?? false;
  }

  constructor(
    private readonly logger: NGXLogger,
    fb: FormBuilder,
    route: ActivatedRoute,
    service: ConvocatoriaReunionService,
    asistenteService: AsistenteService,
    evaluacionService: EvaluacionService,
    personaService: PersonaService,
    evaluadorService: EvaluadorService
  ) {
    super();
    this.data = route.snapshot.data[CONVOCATORIA_REUNION_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(CONVOCATORIA_REUNION_ROUTE_PARAMS.ID));
    if (id) {
      this.enableEdit();
    }
    this.datosGenerales = new ConvocatoriaReunionDatosGeneralesFragment(
      logger, fb, id, service, asistenteService, personaService, evaluadorService, this.readonly);
    this.asignacionMemorias = new ConvocatoriaReunionAsignacionMemoriasListadoFragment(
      logger, id, evaluacionService, personaService, service);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.ASIGNACION_MEMORIAS, this.asignacionMemorias);
  }

  initializeDatosGenerales(): void {
    this.datosGenerales.initialize();
  }

  /**
   * Recupera los datos de la convocatoria de reunión del formulario de datos generales,
   * si no se ha cargado el formulario de datos generales se recuperan los datos de la convocatoria que se esta editando.
   *
   * @returns los datos de la convocatoria de reunión.
   */
  getDatosGeneralesConvocatoriaReunion(): IConvocatoriaReunion {
    return this.datosGenerales.isInitialized() ? this.datosGenerales.getValue() : {} as IConvocatoriaReunion;
  }

  public getDatosAsignacion(): DatosAsignacionEvaluacion {
    // TODO: Arreglar la obtención de esta información cuando el usuario no ha pasado por los datos generales
    const datosAsignacionEvaluacion = {
      idComite: this.datosGenerales.getFormGroup().controls.comite.value?.id,
      idTipoConvocatoria: this.datosGenerales.getFormGroup().controls.tipoConvocatoriaReunion.value?.id,
      fechaLimite: this.datosGenerales.getFormGroup().controls.fechaLimite.value
    } as DatosAsignacionEvaluacion;
    return datosAsignacionEvaluacion;
  }

  onKeyChange(value: number) {
    this.asignacionMemorias.setKey(value);
  }

  /**
   * Comprueba si existen memorias asignadas a la convocatoria de reunión (Si hay evaluaciones)
   */
  hasMemoriasAssigned(): boolean {
    return this.asignacionMemorias.evaluaciones$.value.length > 0;
  }
}
