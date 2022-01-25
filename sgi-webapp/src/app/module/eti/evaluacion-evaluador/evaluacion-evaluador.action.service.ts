import { X } from '@angular/cdk/keycodes';
import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { EvaluacionComentarioFragment } from '../evaluacion-formulario/evaluacion-comentarios/evaluacion-comentarios.fragment';
import { EvaluacionDatosMemoriaFragment } from '../evaluacion-formulario/evaluacion-datos-memoria/evaluacion-datos-memoria.fragment';
import { EvaluacionDocumentacionFragment } from '../evaluacion-formulario/evaluacion-documentacion/evaluacion-documentacion.fragment';
import { EvaluacionFormularioActionService, Rol } from '../evaluacion-formulario/evaluacion-formulario.action.service';


@Injectable()
export class EvaluacionEvaluadorActionService extends EvaluacionFormularioActionService {

  constructor(
    fb: FormBuilder,
    route: ActivatedRoute,
    service: EvaluacionService,
    personaService: PersonaService,
    authService: SgiAuthService
  ) {
    super();
    this.evaluacion = {} as IEvaluacion;
    if (route.snapshot.data.evaluacion) {
      this.evaluacion = route.snapshot.data.evaluacion;
      this.enableEdit();
    }
    this.comentarios = new EvaluacionComentarioFragment(this.evaluacion?.id, Rol.EVALUADOR, service, personaService, authService);
    this.datosMemoria = new EvaluacionDatosMemoriaFragment(fb, this.evaluacion?.id, service, personaService);
    this.documentacion = new EvaluacionDocumentacionFragment(this.evaluacion?.id);

    this.addFragment(this.FRAGMENT.COMENTARIOS, this.comentarios);
    this.addFragment(this.FRAGMENT.MEMORIA, this.datosMemoria);
    this.addFragment(this.FRAGMENT.DOCUMENTACION, this.documentacion);

    this.comentarios.initialize();
    this.comentarios.comentarios$.subscribe(_ => {
      this.comentarios.setDictamen(this.evaluacion?.dictamen);
    });
  }

  getEvaluacion(): IEvaluacion {
    return this.evaluacion;
  }

  getRol(): Rol {
    return Rol.EVALUADOR;
  }

}
