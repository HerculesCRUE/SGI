import { IDictamen } from '@core/models/eti/dictamen';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { TIPO_COMENTARIO } from '@core/models/eti/tipo-comentario';
import { ActionService } from '@core/services/action-service';
import { Observable, of } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { EvaluacionComentarioFragment } from '../evaluacion-formulario/evaluacion-comentarios/evaluacion-comentarios.fragment';
import { EvaluacionDatosMemoriaFragment } from '../evaluacion-formulario/evaluacion-datos-memoria/evaluacion-datos-memoria.fragment';
import { EvaluacionDocumentacionFragment } from '../evaluacion-formulario/evaluacion-documentacion/evaluacion-documentacion.fragment';
import { EvaluacionEvaluacionFragment } from './evaluacion-evaluacion/evaluacion-evaluacion.fragment';
import { IEvaluacionWithComentariosEnviados } from '../evaluacion-evaluador/evaluacion-evaluador-listado/evaluacion-evaluador-listado.component';

export enum Rol { EVALUADOR = TIPO_COMENTARIO.EVALUADOR, GESTOR = TIPO_COMENTARIO.GESTOR }

export abstract class EvaluacionFormularioActionService extends ActionService {

  public readonly FRAGMENT = {
    COMENTARIOS: 'comentarios',
    EVALUACIONES: 'evaluaciones',
    MEMORIA: 'memoria',
    DOCUMENTACION: 'documentarion'
  };

  protected evaluacion: IEvaluacionWithComentariosEnviados;
  protected comentarios: EvaluacionComentarioFragment;
  protected datosMemoria: EvaluacionDatosMemoriaFragment;
  protected documentacion: EvaluacionDocumentacionFragment;
  protected evaluaciones: EvaluacionEvaluacionFragment;


  getEvaluacion(): IEvaluacionWithComentariosEnviados {
    return this.evaluacion;
  }

  setDictamen(dictamen: IDictamen): void {
    this.comentarios.setDictamen(dictamen);
  }

  abstract getRol(): Rol;

  saveOrUpdate(): Observable<void> {
    let cascade = of(void 0);

    if (this.evaluaciones?.hasChanges()) {
      cascade = cascade.pipe(
        switchMap(() => this.evaluaciones.saveOrUpdate().pipe(tap(() => this.evaluaciones.refreshInitialState(true))))
      );
    }

    return cascade.pipe(
      switchMap(() => super.saveOrUpdate())
    );
  }

}
