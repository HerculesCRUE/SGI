import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { NGXLogger } from 'ngx-logger';
import { SeguimientoComentarioFragment } from '../seguimiento-formulario/seguimiento-comentarios/seguimiento-comentarios.fragment';
import { SeguimientoDatosMemoriaFragment } from '../seguimiento-formulario/seguimiento-datos-memoria/seguimiento-datos-memoria.fragment';
import { SeguimientoDocumentacionFragment } from '../seguimiento-formulario/seguimiento-documentacion/seguimiento-documentacion.fragment';
import { Rol, SeguimientoFormularioActionService } from '../seguimiento-formulario/seguimiento-formulario.action.service';


@Injectable()
export class SeguimientoEvaluarActionService extends SeguimientoFormularioActionService {

  constructor(
    private readonly logger: NGXLogger,
    fb: FormBuilder,
    route: ActivatedRoute,
    service: EvaluacionService,
    personaService: PersonaService
  ) {
    super();
    this.evaluacion = {} as IEvaluacion;
    if (route.snapshot.data.evaluacion) {
      this.evaluacion = route.snapshot.data.evaluacion;
      this.enableEdit();
    }
    this.comentarios = new SeguimientoComentarioFragment(this.evaluacion?.id, Rol.EVALUADOR, service);
    this.datosMemoria = new SeguimientoDatosMemoriaFragment(this.logger, fb, this.evaluacion?.id, service, personaService);
    this.documentacion = new SeguimientoDocumentacionFragment(this.evaluacion?.id);

    this.addFragment(this.FRAGMENT.COMENTARIOS, this.comentarios);
    this.addFragment(this.FRAGMENT.MEMORIA, this.datosMemoria);
    this.addFragment(this.FRAGMENT.DOCUMENTACION, this.documentacion);
  }

  getEvaluacion(): IEvaluacion {
    return this.evaluacion;
  }

  getRol(): Rol {
    return Rol.EVALUADOR;
  }
}
