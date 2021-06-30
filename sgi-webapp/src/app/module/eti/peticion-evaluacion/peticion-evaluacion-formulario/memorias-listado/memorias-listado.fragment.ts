import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { Fragment } from '@core/services/action-service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { catchError, endWith, map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class MemoriasListadoFragment extends Fragment {

  memorias$: BehaviorSubject<StatusWrapper<IMemoriaPeticionEvaluacion>[]> =
    new BehaviorSubject<StatusWrapper<IMemoriaPeticionEvaluacion>[]>([]);
  private deleted: StatusWrapper<IMemoriaPeticionEvaluacion>[] = [];

  constructor(key: number, private service: PeticionEvaluacionService, private memoriaService: MemoriaService) {
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

  public addMemoria(memoria: IMemoriaPeticionEvaluacion): void {
    const wrapped = new StatusWrapper<IMemoriaPeticionEvaluacion>(memoria);
    wrapped.setCreated();
    const current = this.memorias$.value;
    current.push(wrapped);
    this.memorias$.next(current);
    this.setChanges(true);
    this.setComplete(true);
  }

  public deleteMemoria(memoria: StatusWrapper<IMemoriaPeticionEvaluacion>) {
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
          return response.items;
        }),
        catchError(() => {
          return of([]);
        })
      ).subscribe(
        (memorias: IMemoriaPeticionEvaluacion[]) => {
          this.memorias$.next(memorias.map((memoria) => new StatusWrapper<IMemoriaPeticionEvaluacion>(memoria)));
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
