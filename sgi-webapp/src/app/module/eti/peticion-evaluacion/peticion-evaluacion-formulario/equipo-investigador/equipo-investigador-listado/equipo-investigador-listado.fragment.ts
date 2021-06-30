import { IEquipoTrabajoWithIsEliminable } from '@core/models/eti/equipo-trabajo-with-is-eliminable';
import { Fragment } from '@core/services/action-service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { DatosAcademicosService } from '@core/services/sgp/datos-academicos.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiAuthService } from '@sgi/framework/auth';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeAll, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class EquipoInvestigadorListadoFragment extends Fragment {

  equiposTrabajo$: BehaviorSubject<StatusWrapper<IEquipoTrabajoWithIsEliminable>[]>
    = new BehaviorSubject<StatusWrapper<IEquipoTrabajoWithIsEliminable>[]>([]);
  private deletedEquiposTrabajo: StatusWrapper<IEquipoTrabajoWithIsEliminable>[] = [];

  private selectedIdPeticionEvaluacion: number;

  constructor(
    key: number,
    private personaService: PersonaService,
    private peticionEvaluacionService: PeticionEvaluacionService,
    private sgiAuthService: SgiAuthService,
    private datosAcademicosService: DatosAcademicosService,
    private vinculacionService: VinculacionService,
  ) {
    super(key);
    this.selectedIdPeticionEvaluacion = key;
    this.setComplete(true);
  }

  onInitialize(): void {
    this.loadEquiposTrabajo(this.getKey() as number);
  }

  loadEquiposTrabajo(idPeticionEvaluacion: number): void {
    if (!this.isInitialized() || this.selectedIdPeticionEvaluacion !== idPeticionEvaluacion) {
      this.selectedIdPeticionEvaluacion = idPeticionEvaluacion;

      let equiposTrabajoRecuperados$: Observable<StatusWrapper<IEquipoTrabajoWithIsEliminable>[]>;

      // Si es una petición de evaluación nueva se añade el usuario actual a la lista
      if (!this.selectedIdPeticionEvaluacion) {
        equiposTrabajoRecuperados$ = this.getInvestigadorActual()
          .pipe(
            map((equipoTrabajo) => {
              const wrapper = new StatusWrapper<IEquipoTrabajoWithIsEliminable>(equipoTrabajo);
              wrapper.setCreated();
              return [wrapper];
            }),
            tap(_ => this.setChanges(true))
          );
      } else {
        equiposTrabajoRecuperados$ = this.peticionEvaluacionService.findEquipoInvestigador(idPeticionEvaluacion).pipe(
          map((personasEquipoTrabajo) => personasEquipoTrabajo.items
            .map((equipoTrabajo) => new StatusWrapper<IEquipoTrabajoWithIsEliminable>(equipoTrabajo))),
          mergeMap(personasEquipoTrabajo => {
            if (personasEquipoTrabajo.length === 0) {
              return of([] as StatusWrapper<IEquipoTrabajoWithIsEliminable>[]);
            }
            return from(personasEquipoTrabajo).pipe(
              map((element) => {
                return this.personaService.findById(element.value.persona.id).pipe(
                  map((persona) => {
                    element.value.persona = persona;
                    element.value.eliminable = element.value.persona.id !==
                      this.sgiAuthService.authStatus$.getValue().userRefId && element.value.eliminable;
                    return element;
                  }),
                  switchMap(() => {
                    return this.vinculacionService.findByPersonaId(element.value.persona.id).pipe(
                      map((vinculacion) => {
                        element.value.persona.vinculacion = vinculacion;
                        return element;
                      }),
                      catchError(() => of(element))
                    );
                  }),
                  switchMap(() => {
                    return this.datosAcademicosService.findByPersonaId(element.value.persona.id).pipe(
                      map((datosAcademicos) => {
                        element.value.persona.datosAcademicos = datosAcademicos;
                        return element;
                      }),
                      catchError(() => of(element))
                    );
                  }),
                  catchError(() => of(element))
                );
              }),
              mergeAll(),
              map(() => personasEquipoTrabajo)
            );
          })
        );
      }

      equiposTrabajoRecuperados$.subscribe((equiposTrabajo) => {
        this.setComplete(true);
        this.equiposTrabajo$.next(equiposTrabajo);
      });
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteEquipos(),
      this.createEquipos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete) {
          this.setChanges(false);
        }
      })
    );
  }

  /**
   * Añade un nuevo miembro al equipo investigador
   *
   * @param equipoTrabajo un equipoTrabajo
   */
  addEquipoTrabajo(equipoTrabajo: IEquipoTrabajoWithIsEliminable): void {
    const wrapped = new StatusWrapper<IEquipoTrabajoWithIsEliminable>(equipoTrabajo);
    wrapped.setCreated();
    const current = this.equiposTrabajo$.value;
    current.push(wrapped);
    this.equiposTrabajo$.next(current);
    this.setChanges(true);
    this.setComplete(true);
  }

  /**
   * Elimina un miembro del equipo investigador de la lista de equipos de trabajo y le añade a la de equipos de trabajo a eliminar.
   *
   * @param equipoTrabajo un equipoTrabajo
   */
  deleteEquipoTrabajo(equipoTrabajo: StatusWrapper<IEquipoTrabajoWithIsEliminable>): void {
    const current = this.equiposTrabajo$.value;
    const index = current.findIndex((value) => value === equipoTrabajo);
    if (index >= 0) {
      if (!equipoTrabajo.created) {
        current[index].setDeleted();
        this.deletedEquiposTrabajo.push(current[index]);
      }

      current.splice(index, 1);
      this.equiposTrabajo$.next(current);
      this.setChanges(true);
    }
  }

  /**
   * Devuelve un observable con el investigador actual como miembro del equipo de trabajo.
   *
   * @return observable con el investigador actual.
   */
  private getInvestigadorActual(): Observable<IEquipoTrabajoWithIsEliminable> {
    return this.personaService.findById(this.sgiAuthService.authStatus$?.getValue()?.userRefId)
      .pipe(
        map((persona) => {
          return {
            id: null,
            peticionEvaluacion: null,
            persona,
            eliminable: false
          };
        })
      );
  }

  private deleteEquipos(): Observable<void> {
    if (this.deletedEquiposTrabajo.length === 0) {
      return of(void 0);
    }
    return from(this.deletedEquiposTrabajo).pipe(
      mergeMap((wrappedEquipoTrabajo) => {
        return this.peticionEvaluacionService
          .deleteEquipoTrabajoPeticionEvaluacion(wrappedEquipoTrabajo.value.peticionEvaluacion.id, wrappedEquipoTrabajo.value.id)
          .pipe(
            tap(_ => {
              this.deletedEquiposTrabajo = this.deletedEquiposTrabajo.filter(deletedEquipoTrabajo =>
                deletedEquipoTrabajo.value.id !== wrappedEquipoTrabajo.value.id);
            })
          );
      }));
  }

  private createEquipos(): Observable<void> {
    const createdEquipos = this.equiposTrabajo$.value.filter((equipo) => equipo.created);
    if (createdEquipos.length === 0) {
      return of(void 0);
    }

    return from(createdEquipos).pipe(
      mergeMap((wrappedEquipoTrabajo) => {
        return this.peticionEvaluacionService.createEquipoTrabajo(this.getKey() as number, wrappedEquipoTrabajo.value).pipe(
          // TODO: Eliminar el casteo. Realmente existe la casuistica de que el backend no indica si el eliminable o no.
          map((savedEquipoTrabajo: IEquipoTrabajoWithIsEliminable) => {
            const index = this.equiposTrabajo$.value.findIndex((currentEquipoTrabajo) => currentEquipoTrabajo === wrappedEquipoTrabajo);
            this.equiposTrabajo$.value[index] = new StatusWrapper<IEquipoTrabajoWithIsEliminable>(savedEquipoTrabajo);
            this.equiposTrabajo$.next(this.equiposTrabajo$.value);
          })
        );
      }));
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.equiposTrabajo$.value.some((wrapper) => wrapper.touched);
    if (this.deletedEquiposTrabajo.length > 0 || touched) {
      return false;
    }
    return true;
  }
}
