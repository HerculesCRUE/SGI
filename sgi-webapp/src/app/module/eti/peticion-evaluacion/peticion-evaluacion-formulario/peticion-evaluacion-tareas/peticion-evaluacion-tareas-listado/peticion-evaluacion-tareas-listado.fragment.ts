import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { ITareaWithIsEliminable } from '@core/models/eti/tarea-with-is-eliminable';
import { Fragment } from '@core/services/action-service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { TareaService } from '@core/services/eti/tarea.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';
import { EquipoInvestigadorListadoFragment } from '../../equipo-investigador/equipo-investigador-listado/equipo-investigador-listado.fragment';
import { MemoriasListadoFragment } from '../../memorias-listado/memorias-listado.fragment';

export class PeticionEvaluacionTareasFragment extends Fragment {

  tareas$: BehaviorSubject<StatusWrapper<ITareaWithIsEliminable>[]> = new BehaviorSubject<StatusWrapper<ITareaWithIsEliminable>[]>([]);

  private deletedTareas: StatusWrapper<ITareaWithIsEliminable>[] = [];
  equiposTrabajo: IEquipoTrabajo[] = [];
  memorias: IMemoriaPeticionEvaluacion[] = [];

  constructor(
    key: number,
    private personaService: PersonaService,
    private tareaService: TareaService,
    private peticionEvaluacionService: PeticionEvaluacionService,
    private equipoInvestigadorFragment: EquipoInvestigadorListadoFragment,
    private memoriaFragment: MemoriasListadoFragment) {
    super(key);
    this.setComplete(true);
  }

  onInitialize(): void {
    this.equipoInvestigadorFragment.initialize();
    this.memoriaFragment.initialize();
    if (this.getKey()) {
      this.loadTareas(this.getKey() as number);
    }
  }

  setEquiposTrabajo(equiposTrabajo: IEquipoTrabajo[]) {
    this.equiposTrabajo = equiposTrabajo;
  }

  setMemorias(memorias: IMemoriaPeticionEvaluacion[]) {
    this.memorias = memorias;
  }

  loadTareas(idPeticionEvaluacion: number): void {
    this.peticionEvaluacionService.findTareas(idPeticionEvaluacion).pipe(
      map((response) => {
        if (response.items) {
          response.items.forEach((tarea) => {
            this.personaService.findById(tarea.equipoTrabajo.persona.id).pipe(
              map((usuarioInfo) => {
                tarea.equipoTrabajo.persona = usuarioInfo;
              })
            ).subscribe();
          });
          return response.items.map((tarea) => new StatusWrapper<ITareaWithIsEliminable>(tarea));
        }
        else {
          return [];
        }
      })
    ).subscribe((tareas) => {
      this.tareas$.next(tareas);
    });
  }

  /**
   * Añade una nueva tarea
   *
   * @param tarea una tarea
   */
  addTarea(tarea: ITareaWithIsEliminable): void {
    const wrapped = new StatusWrapper<ITareaWithIsEliminable>(tarea);
    wrapped.setCreated();
    const current = this.tareas$.value;
    current.push(wrapped);
    this.tareas$.next(current);
    this.setChanges(true);
    this.setComplete(true);
  }

  /**
   * Elimina una tarea de la lista de tareas y la añade a la de tareas a eliminar.
   *
   * @param tarea una tarea
   */
  deleteTarea(tarea: StatusWrapper<ITareaWithIsEliminable>): void {
    const current = this.tareas$.value;
    const index = current.findIndex((value) => value === tarea);
    if (index >= 0) {
      if (!tarea.created) {
        current[index].setDeleted();
        this.deletedTareas.push(current[index]);
      }
      current.splice(index, 1);
      this.tareas$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteTareas(),
      this.updateTareas(),
      this.createTareas()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  /**
   * Elimina las tareas del equipo de trabajo.
   *
   * @param wrapperEquipoTrabajo un equipoTrabajo.
   */
  deleteTareasEquipoTrabajo(wrapperEquipoTrabajo: StatusWrapper<IEquipoTrabajo>): void {
    const current = this.tareas$.value;

    const currentWithoutTareasEquipoTrabajo = current.filter((wrapper) =>
      wrapper.value.equipoTrabajo.persona.id !== wrapperEquipoTrabajo.value.persona.id
    );

    if (currentWithoutTareasEquipoTrabajo.length !== current.length) {
      this.tareas$.next(currentWithoutTareasEquipoTrabajo);

      const hasChanges = currentWithoutTareasEquipoTrabajo.some((wrapper) => wrapper.touched);
      this.setChanges(hasChanges);
    }
  }

  private deleteTareas(): Observable<void> {
    if (this.deletedTareas.length === 0) {
      return of(void 0);
    }
    return from(this.deletedTareas).pipe(
      mergeMap((wrappedTarea) => {
        return this.peticionEvaluacionService
          .deleteTareaPeticionEvaluacion(this.getKey() as number, wrappedTarea.value.equipoTrabajo.id, wrappedTarea.value.id)
          .pipe(
            tap(_ => {
              this.deletedTareas = this.deletedTareas.filter(deletedTarea =>
                deletedTarea.value.id !== wrappedTarea.value.id);
            })
          );
      }));
  }

  private updateTareas(): Observable<void> {
    const editedTareas = this.tareas$.value.filter((tarea) => tarea.edited);
    if (editedTareas.length === 0) {
      return of(void 0);
    }

    return from(editedTareas).pipe(
      mergeMap((wrappedTarea) => {
        return this.tareaService.update(wrappedTarea.value.id, wrappedTarea.value).pipe(
          // TODO: Eliminar casteo ya que realmente el atributo de eliminable el backend no lo está retornando
          map((updatedTarea: ITareaWithIsEliminable) => {
            const index = this.tareas$.value.findIndex((currentTarea) => currentTarea === wrappedTarea);
            this.tareas$.value[index] = new StatusWrapper<ITareaWithIsEliminable>(wrappedTarea.value);
            this.tareas$.next(this.tareas$.value);
          })
        );
      }));
  }

  private createTareas(): Observable<void> {
    const createdTareas = this.tareas$.value.filter((tarea) => tarea.created);
    if (createdTareas.length === 0) {
      return of(void 0);
    }

    return from(createdTareas).pipe(
      mergeMap((wrappedTarea) => {
        wrappedTarea.value.equipoTrabajo = this.equiposTrabajo.find(
          equipo => equipo.persona.id === wrappedTarea.value.equipoTrabajo.persona.id);

        const idEquipoTrabajo = wrappedTarea.value.equipoTrabajo.id;

        return this.peticionEvaluacionService.createTarea(this.getKey() as number,
          idEquipoTrabajo, wrappedTarea.value).pipe(
            // TODO: Eliminar casteo ya que el back no está devolviendo el atributo eliminable
            map((savedTarea: ITareaWithIsEliminable) => {
              const currentTareas = this.tareas$.value.filter((currentTarea) => currentTarea !== wrappedTarea);
              savedTarea.equipoTrabajo = wrappedTarea.value.equipoTrabajo;
              currentTareas.push(new StatusWrapper<ITareaWithIsEliminable>(savedTarea));
              this.tareas$.next(currentTareas);
            })
          );
      }));
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.tareas$.value.some((wrapper) => wrapper.touched);
    if (this.deletedTareas.length > 0 || touched) {
      return false;
    }
    return true;
  }

}
