import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IEvaluacionWithIsEliminable } from '@core/models/eti/evaluacion-with-is-eliminable';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class ConvocatoriaReunionAsignacionMemoriasListadoFragment extends Fragment {

  evaluaciones$: BehaviorSubject<StatusWrapper<IEvaluacionWithIsEliminable>[]> =
    new BehaviorSubject<StatusWrapper<IEvaluacionWithIsEliminable>[]>([]);
  private deleted: StatusWrapper<IEvaluacionWithIsEliminable>[] = [];

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private service: EvaluacionService,
    private personaService: PersonaService,
    private convocatoriaReunionService: ConvocatoriaReunionService
  ) {
    super(key);

    // Para que permita crear convocatorias sin memorias asignadas
    this.setComplete(true);
  }

  onInitialize(): void {
    if (this.getKey()) {
      this.loadEvaluaciones(this.getKey() as number);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteEvaluaciones(),
      this.createEvaluaciones(),
      this.updateEvaluaciones()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }
      )
    );
  }

  public addEvaluacion(evaluacion: IEvaluacionWithIsEliminable) {
    const wrapped = new StatusWrapper<IEvaluacionWithIsEliminable>(evaluacion);
    wrapped.setCreated();
    const current = this.evaluaciones$.value;
    current.push(wrapped);
    this.evaluaciones$.next(current);
    this.setChanges(true);
    // Como no es obligario tener memorias asignadas no aplica.
    // this.setComplete(true);
  }

  public deleteEvaluacion(evaluacion: StatusWrapper<IEvaluacionWithIsEliminable>) {
    const current = this.evaluaciones$.value;
    const index = current.findIndex((value) => value === evaluacion);
    if (index >= 0) {
      if (!evaluacion.created) {
        this.deleted.push(current[index]);
      }
      current.splice(index, 1);
      this.evaluaciones$.next(current);
      this.setChanges(true);
    }
  }

  private loadEvaluaciones(idConvocatoria: number): void {
    this.service
      .findAllByConvocatoriaReunionIdAndNoEsRevMinima(
        idConvocatoria
      ).pipe(
        map((response) => {
          // Return the values
          return response.items;
        }),
        switchMap((evaluaciones) => {
          if (!evaluaciones || evaluaciones.length === 0) {
            return of([]);
          }

          const personaIdsEvaluadores = new Set<string>();

          evaluaciones.forEach((evaluacion: IEvaluacion) => {
            personaIdsEvaluadores.add(evaluacion?.evaluador1?.persona?.id);
            personaIdsEvaluadores.add(evaluacion?.evaluador2?.persona?.id);
          });

          return this.personaService.findAllByIdIn([...personaIdsEvaluadores]).pipe(
            map((result) => {
              const personas = result.items;

              evaluaciones.forEach((evaluacion: IEvaluacion) => {
                const datosPersonaEvaluador1 = personas.find((persona) =>
                  evaluacion.evaluador1.persona.id === persona.id);
                evaluacion.evaluador1.persona = datosPersonaEvaluador1;

                const datosPersonaEvaluador2 = personas.find((persona) =>
                  evaluacion.evaluador2.persona.id === persona.id);
                evaluacion.evaluador2.persona = datosPersonaEvaluador2;
              });

              return evaluaciones;
            }));
        }),
        catchError((error) => {
          // On error reset pagination values
          // this.snackBarService.showError('eti.convocatoriaReunion.listado.error');
          this.logger.error(error);
          return of([]);
        })
      ).subscribe(
        (evaluaciones) => {
          this.evaluaciones$.next(evaluaciones.map((ev) => new StatusWrapper<IEvaluacionWithIsEliminable>(ev)));
        }
      );
  }

  private createEvaluaciones(): Observable<void> {
    const evaluacionesCreadas = this.evaluaciones$.value.filter((evaluacion) => evaluacion.created);
    if (evaluacionesCreadas.length === 0) {
      return of(void 0);
    }
    return from(evaluacionesCreadas).pipe(
      mergeMap((evaluacion) => {
        evaluacion.value.convocatoriaReunion.id = this.getKey() as number;
        return this.service.create(evaluacion.value).pipe(
          // TODO: Eliminar casteo ya que el back no retorna el atributo eliminable
          map((savedEvaluacion: IEvaluacionWithIsEliminable) => {
            const index = this.evaluaciones$.value.findIndex((wrapped) => wrapped === evaluacion);
            this.evaluaciones$[index] = new StatusWrapper<IEvaluacionWithIsEliminable>(savedEvaluacion);
          })
        );
      }),
      takeLast(1)
    );
  }

  private updateEvaluaciones(): Observable<void> {
    const evaluacionesEditadas = this.evaluaciones$.value.filter((evaluacion) => evaluacion.edited);
    if (evaluacionesEditadas.length === 0) {
      return of(void 0);
    }
    return from(evaluacionesEditadas).pipe(
      mergeMap((evaluacion) => {
        evaluacion.value.convocatoriaReunion.id = this.getKey() as number;
        return this.service.update(evaluacion.value.id, evaluacion.value).pipe(
          // TODO: Eliminar casteo ya que realmente el back no retorna el atributo eliminable
          map((updatedEvaluacion: IEvaluacionWithIsEliminable) => {
            const index = this.evaluaciones$.value.findIndex((wrapped) => wrapped === evaluacion);
            this.evaluaciones$[index] = new StatusWrapper<IEvaluacionWithIsEliminable>(updatedEvaluacion);
          })
        );
      }),
      takeLast(1)
    );
  }

  private deleteEvaluaciones(): Observable<void> {
    if (this.deleted.length === 0) {
      return of(void 0);
    }
    return from(this.deleted).pipe(
      mergeMap((wrappedEvaluacion) => {
        return this.convocatoriaReunionService
          .deleteEvaluacion(wrappedEvaluacion.value.convocatoriaReunion.id, wrappedEvaluacion.value.id).pipe(
            map(_ => {
              this.deleted = this.deleted.filter(deleted => deleted.value.id !== wrappedEvaluacion.value.id);
            })
          );
      }));
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.evaluaciones$.value.some((wrapper) => wrapper.touched);
    if (this.deleted.length > 0 || touched) {
      return false;
    }
    return true;
  }
}
