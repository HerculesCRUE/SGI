import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { SeguimientoComentarioFragment } from '../seguimiento-formulario/seguimiento-comentarios/seguimiento-comentarios.fragment';
import { SeguimientoDocumentacionFragment } from '../seguimiento-formulario/seguimiento-documentacion/seguimiento-documentacion.fragment';
import { SeguimientoEvaluacionFragment } from '../seguimiento-formulario/seguimiento-evaluacion/seguimiento-evaluacion.fragment';
import { Rol, SeguimientoFormularioActionService } from '../seguimiento-formulario/seguimiento-formulario.action.service';
import { IEvaluacionWithComentariosEnviados } from '../evaluacion-evaluador/evaluacion-evaluador-listado/evaluacion-evaluador-listado.component';

@Injectable()
export class GestionSeguimientoActionService extends SeguimientoFormularioActionService {


  constructor(
    fb: FormBuilder,
    route: ActivatedRoute,
    service: EvaluacionService,
    protected readonly snackBarService: SnackBarService,
    personaService: PersonaService,
    authService: SgiAuthService) {
    super();
    this.evaluacion = {} as IEvaluacionWithComentariosEnviados;
    if (route.snapshot.data.evaluacion) {
      this.evaluacion = route.snapshot.data.evaluacion;
      this.enableEdit();
    }
    this.evaluaciones = new SeguimientoEvaluacionFragment(
      fb, this.evaluacion?.id, snackBarService, service, personaService);
    this.comentarios = new SeguimientoComentarioFragment(this.evaluacion?.id, Rol.GESTOR, service, personaService, authService);
    this.documentacion = new SeguimientoDocumentacionFragment(this.evaluacion?.id);

    this.addFragment(this.FRAGMENT.COMENTARIOS, this.comentarios);
    this.addFragment(this.FRAGMENT.EVALUACIONES, this.evaluaciones);
    this.addFragment(this.FRAGMENT.DOCUMENTACION, this.documentacion);

    this.comentarios.initialize();
    this.evaluaciones.setComentarios(this.comentarios.comentarios$);
    this.comentarios.comentarios$.subscribe(_ => {
      this.comentarios.setDictamen(this.evaluacion?.dictamen);
    });
  }

  getEvaluacion(): IEvaluacionWithComentariosEnviados {
    return this.evaluacion;
  }

  getRol(): Rol {
    return Rol.GESTOR;
  }
}
