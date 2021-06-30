import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { IEvaluador } from '@core/models/eti/evaluador';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { EvaluadorDatosGeneralesFragment } from './evaluador-formulario/evaluador-datos-generales/evaluador-datos-generales.fragment';
import { PersonaService } from '@core/services/sgp/persona.service';
import { EvaluadorConflictosInteresFragment } from './evaluador-formulario/evaluador-conflictos-interes/evaluador-conflictos-interes-listado/evaluador-conflictos-interes-listado.fragment';
import { ConflictoInteresService } from '@core/services/eti/conflicto-interes.service';
import { NGXLogger } from 'ngx-logger';

@Injectable()
export class EvaluadorActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    CONFLICTO_INTERES: 'conflictoInteres'
  };

  private evaluador: IEvaluador;
  private datosGenerales: EvaluadorDatosGeneralesFragment;
  private conflictoInteres: EvaluadorConflictosInteresFragment;

  constructor(
    private readonly logger: NGXLogger,
    fb: FormBuilder, route: ActivatedRoute, service: EvaluadorService,
    personaService: PersonaService, conflictoInteresService: ConflictoInteresService
  ) {
    super();
    this.evaluador = {} as IEvaluador;
    if (route.snapshot.data.evaluador) {
      this.evaluador = route.snapshot.data.evaluador;
      this.enableEdit();
    }
    this.datosGenerales = new EvaluadorDatosGeneralesFragment(fb, this.evaluador?.id, service, personaService);
    this.conflictoInteres = new EvaluadorConflictosInteresFragment(this.evaluador?.id, service,
      personaService, conflictoInteresService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.CONFLICTO_INTERES, this.conflictoInteres);
  }
}
