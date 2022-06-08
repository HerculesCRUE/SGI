import { IConflictoInteres } from '@core/models/eti/conflicto-interes';
import { Fragment } from '@core/services/action-service';
import { ConflictoInteresService } from '@core/services/eti/conflicto-interes.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class EvaluadorConflictosInteresFragment extends Fragment {

  conflictos$: BehaviorSubject<StatusWrapper<IConflictoInteres>[]> = new BehaviorSubject<StatusWrapper<IConflictoInteres>[]>([]);
  private deleted: StatusWrapper<IConflictoInteres>[] = [];

  constructor(
    key: number,
    private evaluadorService: EvaluadorService,
    private personaService: PersonaService,
    private conflictoInteresService: ConflictoInteresService) {
    super(key);
    this.setComplete(true);
  }

  onInitialize(): void {
    if (this.getKey()) {
      this.loadConflictosInteres(this.getKey() as number);
    }
  }

  loadConflictosInteres(idEvaluador: number): void {
    this.evaluadorService.findConflictosInteres(idEvaluador).pipe(
      map((response) => {
        if (response.items) {
          response.items.forEach((conflictoInteres) => {
            this.personaService.findById(conflictoInteres.personaConflicto.id).pipe(
              map((usuarioInfo) => {
                conflictoInteres.personaConflicto = usuarioInfo;
              })
            ).subscribe();
          });
          return response.items.map((conflictoInteres) => new StatusWrapper<IConflictoInteres>(conflictoInteres));
        }
        else {
          return [];
        }
      })
    ).subscribe((conflictosInteres) => {
      this.conflictos$.next(conflictosInteres);
    });
  }

  /**
   * Añade un nuevo conflicto de interés
   *
   * @param conflictoInteres un conflicto interés
   */
  addConflicto(conflictoInteres: IConflictoInteres): void {
    const wrapped = new StatusWrapper<IConflictoInteres>(conflictoInteres);
    wrapped.setCreated();
    const current = this.conflictos$.value;
    current.push(wrapped);
    this.conflictos$.next(current);
    this.setChanges(true);
    this.setComplete(true);
  }

  public deleteConflictoInteres(conflictoInteres: StatusWrapper<IConflictoInteres>) {
    const current = this.conflictos$.value;
    const index = current.findIndex((value) => value === conflictoInteres);

    if (index >= 0) {
      if (!conflictoInteres.created) {
        current[index].setDeleted();
        this.deleted.push(current[index]);
      }
      current.splice(index, 1);
      this.conflictos$.next(current);
      this.setChanges(true);
    }
    if (current.length === 0) {
      this.setComplete(false);
    }

  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteConflictos(),
      this.createConflictos()
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

  private deleteConflictos(): Observable<void> {
    if (this.deleted.length === 0) {
      return of(void 0);
    }
    return from(this.deleted).pipe(
      mergeMap((wrappedConflicto) => {
        return this.conflictoInteresService.deleteById(wrappedConflicto.value.id).pipe(
          map(_ => {
            this.deleted = this.deleted.filter(deleted => deleted.value.id !== wrappedConflicto.value.id);
          })
        );
      }));
  }

  private createConflictos(): Observable<void> {
    const createdConflictos = this.conflictos$.value.filter((conflicto) => conflicto.created);
    if (createdConflictos.length === 0) {
      return of(void 0);
    }

    return from(createdConflictos).pipe(
      mergeMap((wrappedConflicto) => {
        wrappedConflicto.value.evaluador.id = this.getKey() as number;
        return this.conflictoInteresService.create(wrappedConflicto.value).pipe(
          map((savedConflicto) => {
            const index = this.conflictos$.value.findIndex((currentConflicto) => currentConflicto === wrappedConflicto);
            wrappedConflicto.value.id = savedConflicto.id;
            this.conflictos$.value[index] = new StatusWrapper<IConflictoInteres>(wrappedConflicto.value);
            this.conflictos$.next(this.conflictos$.value);
          })
        );
      }));
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.conflictos$.value.some((wrapper) => wrapper.touched);
    if (this.deleted.length > 0 || touched) {
      return false;
    }
    return true;
  }
}
