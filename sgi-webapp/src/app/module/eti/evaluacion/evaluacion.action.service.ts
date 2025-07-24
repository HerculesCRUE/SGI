import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ApartadoService } from '@core/services/eti/apartado.service';
import { BloqueService } from '@core/services/eti/bloque.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { IEvaluacionWithComentariosEnviados } from '../evaluacion-evaluador/evaluacion-evaluador-listado/evaluacion-evaluador-listado.component';
import { EvaluacionComentarioFragment } from '../evaluacion-formulario/evaluacion-comentarios/evaluacion-comentarios.fragment';
import { EvaluacionDocumentacionFragment } from '../evaluacion-formulario/evaluacion-documentacion/evaluacion-documentacion.fragment';
import { EvaluacionEvaluacionFragment } from '../evaluacion-formulario/evaluacion-evaluacion/evaluacion-evaluacion.fragment';
import { EvaluacionFormularioActionService, Rol } from '../evaluacion-formulario/evaluacion-formulario.action.service';

@Injectable()
export class EvaluacionActionService extends EvaluacionFormularioActionService {

  constructor(
    private readonly logger: NGXLogger,
    fb: FormBuilder,
    route: ActivatedRoute,
    service: EvaluacionService,
    protected readonly snackBarService: SnackBarService,
    personaService: PersonaService,
    authService: SgiAuthService,
    apartadoService: ApartadoService,
    bloqueService: BloqueService) {
    super();
    this.evaluacion = {} as IEvaluacionWithComentariosEnviados;
    if (route.snapshot.data.evaluacion) {
      this.evaluacion = route.snapshot.data.evaluacion;
      this.enableEdit();
    }
    this.evaluaciones = new EvaluacionEvaluacionFragment(
      fb, this.evaluacion?.id, snackBarService, service, personaService);
    this.comentarios = new EvaluacionComentarioFragment(logger, this.evaluacion?.id, Rol.GESTOR, service, personaService, authService, apartadoService, bloqueService);
    this.documentacion = new EvaluacionDocumentacionFragment(this.evaluacion?.id);


    this.addFragment(this.FRAGMENT.COMENTARIOS, this.comentarios);
    this.addFragment(this.FRAGMENT.EVALUACIONES, this.evaluaciones);
    this.addFragment(this.FRAGMENT.DOCUMENTACION, this.documentacion);

    this.comentarios.initialize();
    this.evaluaciones.setComentarios(this.comentarios.comentarios$);
    this.comentarios.comentarios$.subscribe(_ => {
      this.comentarios.setDictamen(this.evaluacion?.dictamen);
    });
  }

  getRol(): Rol {
    return Rol.GESTOR;
  }
}
