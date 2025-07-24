import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ApartadoService } from '@core/services/eti/apartado.service';
import { BloqueService } from '@core/services/eti/bloque.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { EvaluacionComentarioFragment } from '../evaluacion-formulario/evaluacion-comentarios/evaluacion-comentarios.fragment';
import { EvaluacionDatosMemoriaFragment } from '../evaluacion-formulario/evaluacion-datos-memoria/evaluacion-datos-memoria.fragment';
import { EvaluacionDocumentacionFragment } from '../evaluacion-formulario/evaluacion-documentacion/evaluacion-documentacion.fragment';
import { EvaluacionFormularioActionService, Rol } from '../evaluacion-formulario/evaluacion-formulario.action.service';
import { IEvaluacionWithComentariosEnviados } from './evaluacion-evaluador-listado/evaluacion-evaluador-listado.component';


@Injectable()
export class EvaluacionEvaluadorActionService extends EvaluacionFormularioActionService {

  private readonly: boolean;

  constructor(
    private readonly logger: NGXLogger,
    fb: FormBuilder,
    route: ActivatedRoute,
    service: EvaluacionService,
    personaService: PersonaService,
    authService: SgiAuthService,
    private readonly apartadoService: ApartadoService,
    private readonly bloqueService: BloqueService
  ) {
    super();
    this.evaluacion = {} as IEvaluacionWithComentariosEnviados;
    if (route.snapshot.data.evaluacion) {
      this.evaluacion = route.snapshot.data.evaluacion;
      this.enableEdit();
    }
    this.comentarios = new EvaluacionComentarioFragment(logger, this.evaluacion?.id, Rol.EVALUADOR, service, personaService, authService, this.apartadoService, this.bloqueService);
    this.datosMemoria = new EvaluacionDatosMemoriaFragment(logger, fb, this.evaluacion?.id, service, personaService);
    this.documentacion = new EvaluacionDocumentacionFragment(this.evaluacion?.id);

    this.addFragment(this.FRAGMENT.COMENTARIOS, this.comentarios);
    this.addFragment(this.FRAGMENT.MEMORIA, this.datosMemoria);
    this.addFragment(this.FRAGMENT.DOCUMENTACION, this.documentacion);

    this.comentarios.initialize();
    this.comentarios.comentarios$.subscribe(_ => {
      this.comentarios.setDictamen(this.evaluacion?.dictamen);
    });
  }

  getEvaluacion(): IEvaluacionWithComentariosEnviados {
    return this.evaluacion;
  }

  getRol(): Rol {
    return Rol.EVALUADOR;
  }

}
