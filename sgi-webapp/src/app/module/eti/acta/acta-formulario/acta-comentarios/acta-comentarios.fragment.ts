import { IComentario } from '@core/models/eti/comentario';
import { IDictamen } from '@core/models/eti/dictamen';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { ESTADO_MEMORIA } from '@core/models/eti/tipo-estado-memoria';
import { TIPO_EVALUACION } from '@core/models/eti/tipo-evaluacion';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiAuthService } from '@sgi/framework/auth';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { endWith, map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { Rol } from '../../acta-rol';
import { ActaService } from '@core/services/eti/acta.service';

export class ActaComentariosFragment extends Fragment {

  comentarios$: BehaviorSubject<StatusWrapper<IComentario>[]> = new BehaviorSubject<StatusWrapper<IComentario>[]>([]);
  comentariosEliminados: StatusWrapper<IComentario>[] = [];
  comentariosActa: IComentario[];
  private dictamen: IDictamen;
  private selectedIdConvocatoria: number;
  evaluaciones$: BehaviorSubject<IEvaluacion[]> = new BehaviorSubject<IEvaluacion[]>([]);
  private idsEvaluacion: number[] = [];
  showAddComentarios = true;

  public isRolGestor(): boolean {
    return this.rol === Rol.GESTOR;
  }

  constructor(
    key: number,
    private actaService: ActaService,
    private service: EvaluacionService,
    private convocatoriaReunionService: ConvocatoriaReunionService,
    private readonly personaService: PersonaService,
    private readonly authService: SgiAuthService,
    private rol: Rol
  ) {
    super(key);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.subscriptions.push(this.actaService.findById(this.getKey() as number).subscribe(
        value => {
          this.selectedIdConvocatoria = value.convocatoriaReunion?.id;
          this.loadEvaluaciones(value.convocatoriaReunion?.id);
        }));
    }
  }

  loadEvaluaciones(idConvocatoria: number): void {
    if (!this.isInitialized() || this.selectedIdConvocatoria !== idConvocatoria) {
      this.selectedIdConvocatoria = idConvocatoria;
      this.subscriptions.push(this.convocatoriaReunionService.findEvaluacionesActivas(this.selectedIdConvocatoria).pipe(
        map((response) => response.items),
        switchMap((evaluaciones) => {
          const evaluacionesComentario: IEvaluacion[] = [];
          const current = [];
          return from(evaluaciones).pipe(
            mergeMap(evaluacion => {
              if (evaluacion.dictamen?.activo) {
                evaluacionesComentario.push(evaluacion);
                this.idsEvaluacion.push(evaluacion.id);
                this.showAddComentarios = this.checkAddComentario(evaluacion);
                return this.service.getComentariosActa(evaluacion.id, this.isRolGestor()).pipe(
                  map((comentarios) => {
                    if (comentarios.length > 0) {
                      this.comentarios$.value.forEach(comentario => {
                        if (!evaluacionesComentario.some(ev => ev.id === comentario.value.evaluacion.id)) {
                          evaluacionesComentario.push(comentario.value.evaluacion);
                          this.idsEvaluacion.push(comentario.value.evaluacion.id);
                        }
                      });
                      comentarios.forEach(comentario => {
                        this.personaService.findById(comentario.evaluador.id).subscribe(persona => {
                          current.push(new StatusWrapper<IComentario>(comentario));
                          if (!evaluacionesComentario.some(ev => ev.id === comentario.evaluacion.id)) {
                            evaluacionesComentario.push(comentario.evaluacion);
                            this.idsEvaluacion.push(comentario.evaluacion.id);
                          }
                          comentario.evaluador = persona;
                          this.comentarios$.next([...current]);
                          this.evaluaciones$.next([...evaluacionesComentario]);
                        });
                      });
                    } else {
                      this.evaluaciones$.next([...evaluacionesComentario]);
                    }
                  }));
              } else {
                this.showAddComentarios = false;
                return of(null);
              }
            })
          );
        })).subscribe());
    }
  }

  saveOrUpdate(): Observable<void> {
    this.setChanges(false);
    return merge(
      this.deleteComentarioActa(),
      this.createComentarioActa()
    ).pipe(
      takeLast(1)
    );
  }

  public addComentario(comentario: IComentario) {
    this.personaService.findById(this.authService.authStatus$.value.userRefId).subscribe(persona => {
      comentario.evaluador = persona;
      const wrapped = new StatusWrapper<IComentario>(comentario);
      wrapped.setCreated();
      const current = this.comentarios$.value;
      current.push(wrapped);
      this.comentarios$.next(current);
      this.setChanges(true);
      this.setErrors(false);
    });
  }

  public deleteComentario(comentario: StatusWrapper<IComentario>) {
    const current = this.comentarios$.value;
    const index = current.findIndex((value) => value === comentario);
    if (index >= 0) {
      if (!comentario.created) {
        this.comentariosEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.comentarios$.next(current);
      this.setChanges(true);
    }
    if (this.comentarios$.value.length === 0 &&
      (this.dictamen?.id === 2 || this.dictamen?.id === 3)) {
      this.setErrors(true);
    } else {
      this.setErrors(false);
    }
  }

  private deleteComentarioActa(): Observable<void> {
    if (this.comentariosEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.comentariosEliminados).pipe(
      mergeMap((wrappedComentario) => {
        const evaluacion = this.evaluaciones$.value.filter(ev => ev.memoria.id === wrappedComentario.value.memoria.id)[0];
        return this.service.deleteComentarioActa(evaluacion.id, wrappedComentario.value.id, this.isRolGestor()).pipe(
          map(() => {
            this.comentariosEliminados = this.comentariosEliminados.filter(
              comentario => comentario.value.id !== wrappedComentario.value.id);
          }));
      }),
      endWith()
    );
  }

  private createComentarioActa(): Observable<void> {
    const comentariosCreados = this.comentarios$.value.filter((comentario) => comentario.created);
    if (comentariosCreados.length === 0) {
      return of(void 0);
    }
    return from(comentariosCreados).pipe(
      mergeMap((wrappedComentario) => {
        const evaluacion = this.evaluaciones$.value.filter(ev => ev.memoria.id === wrappedComentario.value.memoria.id)[0];
        return this.service.createComentarioActa(evaluacion.id, wrappedComentario.value, this.isRolGestor()).pipe(
          map((savedComentario) => {
            const index = this.comentarios$.value.findIndex((currentComentario) => currentComentario === wrappedComentario);
            wrappedComentario.value.id = savedComentario.id;
            this.comentarios$.value[index] = new StatusWrapper<IComentario>(wrappedComentario.value);
            this.comentarios$.next(this.comentarios$.value);
            this.comentarios$.value.forEach((currentComentario) => {
              if (currentComentario === wrappedComentario) {
                currentComentario.setEdited();
                currentComentario.value.id = savedComentario.id;
              }
            });
          })
        );
      }),
      endWith()
    );
  }

  private checkAddComentario(evaluacion: IEvaluacion): boolean {
    if (evaluacion.dictamen?.activo &&
      ((evaluacion.convocatoriaReunion.tipoConvocatoriaReunion.id === 1 && (evaluacion.memoria.estadoActual.id === ESTADO_MEMORIA.EN_EVALUACION || evaluacion.tipoEvaluacion.id === TIPO_EVALUACION.RETROSPECTIVA)) ||
        evaluacion.convocatoriaReunion.tipoConvocatoriaReunion.id > 1 && (evaluacion.memoria.estadoActual.id === ESTADO_MEMORIA.EN_EVALUACION_SEGUIMIENTO_FINAL ||
          evaluacion.memoria.estadoActual.id === ESTADO_MEMORIA.EN_EVALUACION_SEGUIMIENTO_ANUAL))) {
      return true;
    } else {
      return false;
    }
  }

}
