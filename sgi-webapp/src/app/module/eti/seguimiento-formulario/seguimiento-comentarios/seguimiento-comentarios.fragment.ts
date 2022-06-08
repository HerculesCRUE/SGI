import { IComentario } from '@core/models/eti/comentario';
import { IDictamen } from '@core/models/eti/dictamen';
import { Fragment } from '@core/services/action-service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiAuthService } from '@sgi/framework/auth';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { endWith, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';
import { Rol } from '../seguimiento-formulario.action.service';

export class SeguimientoComentarioFragment extends Fragment {
  comentarios$: BehaviorSubject<StatusWrapper<IComentario>[]> = new BehaviorSubject<StatusWrapper<IComentario>[]>([]);
  comentariosEliminados: StatusWrapper<IComentario>[] = [];
  private dictamen: IDictamen;

  constructor(
    key: number,
    private rol: Rol,
    private service: EvaluacionService,
    private readonly personaService: PersonaService,
    private readonly authService: SgiAuthService
  ) {
    super(key);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      // Se muestra el listado de los comentarios del EVALUADOR
      let evaluacionComentarios$ = this.service.getComentariosEvaluador(this.getKey() as number);
      if (this.rol === Rol.GESTOR) {
        // Se muestran el listado de los comentarios del GESTOR
        evaluacionComentarios$ = this.service.getComentariosGestor(this.getKey() as number);
      }

      this.subscriptions.push(evaluacionComentarios$.pipe(
        switchMap(result => {
          return from(result.items).pipe(
            mergeMap(element => {
              return this.personaService.findById(element.evaluador.id).pipe(
                map(persona => {
                  element.evaluador = persona;
                  return element;
                })
              );
            }),
            map(() => result)
          );
        }),
        map(response => response.items)
      ).subscribe((comentarios) => {
        this.comentarios$.next(comentarios.map(comentario => new StatusWrapper<IComentario>(comentario)));
      }));
    }
  }

  saveOrUpdate(): Observable<void> {
    if (this.rol === Rol.GESTOR) {
      return merge(
        this.deleteComentarioGestor(),
        this.updateComentarioGestor(),
        this.createComentarioGestor()
      ).pipe(
        takeLast(1),
        tap(() => this.setChanges(false))
      );
    } else {
      return merge(
        this.deleteComentarioEvaluador(),
        this.updateComentarioEvaluador(),
        this.createComentarioEvaluador()
      ).pipe(
        takeLast(1),
        tap(() => this.setChanges(false))
      );
    }
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
      (this.dictamen?.id === 6 || this.dictamen?.id === 8)) {
      this.setErrors(true);
    } else {
      this.setErrors(false);
    }
  }

  private deleteComentarioGestor(): Observable<void> {
    if (this.comentariosEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.comentariosEliminados).pipe(
      mergeMap((wrappedComentario) => {

        return this.service.deleteComentarioGestor(this.getKey() as number, wrappedComentario.value.id);
      }),
      endWith()
    );
  }

  private deleteComentarioEvaluador(): Observable<void> {
    if (this.comentariosEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.comentariosEliminados).pipe(
      mergeMap((wrappedComentario) => {

        return this.service.deleteComentarioEvaluador(this.getKey() as number, wrappedComentario.value.id);
      }),
      endWith()
    );
  }

  private updateComentarioGestor(): Observable<void> {
    const comentariosEditados = this.comentarios$.value.filter((comentario) => comentario.edited);
    if (comentariosEditados.length === 0) {
      return of(void 0);
    }
    return from(comentariosEditados).pipe(
      mergeMap((wrappedComentario) => {

        return this.service.updateComentarioGestor(this.getKey() as number, wrappedComentario.value, wrappedComentario.value.id).pipe(
          map((updatedComentario) => {
            const index = this.comentarios$.value.findIndex((currentComentario) => currentComentario === wrappedComentario);
            this.comentarios$.value[index] = new StatusWrapper<IComentario>(wrappedComentario.value);
            this.comentarios$.next(this.comentarios$.value);
          })
        );
      }),
      endWith()
    );
  }

  private updateComentarioEvaluador(): Observable<void> {
    const comentariosEditados = this.comentarios$.value.filter((comentario) => comentario.edited);
    if (comentariosEditados.length === 0) {
      return of(void 0);
    }
    return from(comentariosEditados).pipe(
      mergeMap((wrappedComentario) => {

        return this.service.updateComentarioEvaluador(this.getKey() as number, wrappedComentario.value, wrappedComentario.value.id).pipe(
          map((updatedComentario) => {
            const index = this.comentarios$.value.findIndex((currentComentario) => currentComentario === wrappedComentario);
            this.comentarios$.value[index] = new StatusWrapper<IComentario>(wrappedComentario.value);
            this.comentarios$.next(this.comentarios$.value);
          })
        );
      }),
      endWith()
    );
  }

  private createComentarioGestor(): Observable<void> {
    const comentariosCreados = this.comentarios$.value.filter((comentario) => comentario.created);
    if (comentariosCreados.length === 0) {
      return of(void 0);
    }

    return from(comentariosCreados).pipe(
      mergeMap((wrappedComentario) => {

        return this.service.createComentarioGestor(this.getKey() as number, wrappedComentario.value).pipe(
          map((savedComentario) => {
            const index = this.comentarios$.value.findIndex((currentComentario) => currentComentario === wrappedComentario);
            wrappedComentario.value.id = savedComentario.id;
            this.comentarios$.value[index] = new StatusWrapper<IComentario>(wrappedComentario.value);
            this.comentarios$.next(this.comentarios$.value);
          })
        );
      }),
      endWith()
    );
  }

  private createComentarioEvaluador(): Observable<void> {
    const comentariosCreados = this.comentarios$.value.filter((comentario) => comentario.created);
    if (comentariosCreados.length === 0) {
      return of(void 0);
    }

    return from(comentariosCreados).pipe(
      mergeMap((wrappedComentario) => {

        return this.service.createComentarioEvaluador(this.getKey() as number, wrappedComentario.value).pipe(
          map((savedComentario) => {
            const index = this.comentarios$.value.findIndex((currentComentario) => currentComentario === wrappedComentario);
            wrappedComentario.value.id = savedComentario.id;
            this.comentarios$.value[index] = new StatusWrapper<IComentario>(wrappedComentario.value);
            this.comentarios$.next(this.comentarios$.value);
          })
        );
      }),
      endWith()
    );
  }

  setDictamen(dictamen: IDictamen) {
    this.dictamen = dictamen;
    this.setErrors((this.dictamen?.id === 6 || this.dictamen?.id === 8) && this.comentarios$.value.length === 0);
  }
}
