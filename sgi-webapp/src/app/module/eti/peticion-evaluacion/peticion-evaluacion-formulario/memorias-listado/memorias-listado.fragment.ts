import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { IPersona } from '@core/models/sgp/persona';
import { Fragment } from '@core/services/action-service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { catchError, endWith, map, mergeMap, switchMap, takeLast, tap, toArray } from 'rxjs/operators';

export interface IMemoriaPeticionEvaluacionWithLastEvaluacion extends IMemoriaPeticionEvaluacion {
  evaluacion: IEvaluacion
}

export class MemoriasListadoFragment extends Fragment {

  memorias$: BehaviorSubject<StatusWrapper<IMemoriaPeticionEvaluacionWithLastEvaluacion>[]> =
    new BehaviorSubject<StatusWrapper<IMemoriaPeticionEvaluacionWithLastEvaluacion>[]>([]);
  private deleted: StatusWrapper<IMemoriaPeticionEvaluacionWithLastEvaluacion>[] = [];

  solicitantePeticionEvaluacion: IPersona;

  get isModuleInv(): boolean {
    return this.isInvestigador;
  }

  constructor(
    key: number,
    private readonly service: PeticionEvaluacionService,
    private readonly memoriaService: MemoriaService,
    private readonly isInvestigador: boolean) {
    super(key);
    this.setComplete(true);
  }

  onInitialize(): void {
    if (this.getKey()) {
      this.loadMemorias(this.getKey() as number);
    }
    this.setComplete(true);
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.delete(),
    ).pipe(
      takeLast(1),
      tap(() => this.setChanges(false))
    );
  }

  public addMemoria(memoria: IMemoriaPeticionEvaluacionWithLastEvaluacion): void {
    const wrapped = new StatusWrapper<IMemoriaPeticionEvaluacionWithLastEvaluacion>(memoria);
    wrapped.setCreated();
    const current = this.memorias$.value;
    current.push(wrapped);
    this.memorias$.next(current);
    this.setChanges(true);
    this.setComplete(true);
  }

  public deleteMemoria(memoria: StatusWrapper<IMemoriaPeticionEvaluacionWithLastEvaluacion>) {
    const current = this.memorias$.value;
    const index = current.findIndex((value) => value === memoria);
    if (index >= 0) {
      if (!memoria.created) {
        this.deleted.push(current[index]);
      }
      current.splice(index, 1);
      this.memorias$.next(current);
      this.setChanges(true);
    }
    if (current.length === 0) {
      this.setComplete(false);
    }
  }

  public loadMemorias(idPeticionEvaluacion: number): void {
    this.service
      .findMemorias(
        idPeticionEvaluacion
      ).pipe(
        map((response) => {
          // Return the values
          return response.items as IMemoriaPeticionEvaluacionWithLastEvaluacion[];
        }),
        switchMap(memorias => from(memorias).pipe(
          mergeMap(memoria =>
            this.memoriaService.getLastEvaluacionMemoria(memoria.id).pipe(
              map(evaluacion => {
                memoria.evaluacion = evaluacion;
                return memoria;
              })
            )
          ),
          toArray(),
          map(() => memorias)
        )
        ),
        catchError(() => {
          return of([]);
        })
      ).subscribe(
        (memorias: IMemoriaPeticionEvaluacionWithLastEvaluacion[]) => {
          this.memorias$.next(memorias.map((memoria) => new StatusWrapper<IMemoriaPeticionEvaluacionWithLastEvaluacion>(memoria)));
        }
      );
  }

  private delete(): Observable<void> {
    if (this.deleted.length === 0) {
      return of(void 0);
    }
    return from(this.deleted).pipe(
      mergeMap((wrappedMemoria) => {

        return this.memoriaService.deleteById(wrappedMemoria.value.id);
      }),
      endWith()
    );
  }

}
