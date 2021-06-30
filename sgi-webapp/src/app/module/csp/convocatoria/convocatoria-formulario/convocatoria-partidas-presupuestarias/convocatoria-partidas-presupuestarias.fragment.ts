import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaPartidaPresupuestariaService } from '@core/services/csp/convocatoria-partidas-presupuestarias.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ConvocatoriaPartidaPresupuestariaFragment extends Fragment {
  partidasPresupuestarias$ = new BehaviorSubject<StatusWrapper<IConvocatoriaPartidaPresupuestaria>[]>([]);
  partidasPresupuestariasEliminadas: StatusWrapper<IConvocatoriaPartidaPresupuestaria>[] = [];

  constructor(
    key: number,
    private convocatoriaService: ConvocatoriaService,
    private convocatoriaPartidaPresupuestariaService: ConvocatoriaPartidaPresupuestariaService,
    public readonly: boolean,
    public canEdit: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.convocatoriaService.findPartidasPresupuestarias(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((partidasPresupuestarias) => {
        this.partidasPresupuestarias$.next(partidasPresupuestarias.map(
          partidaPresupuestaria => new StatusWrapper<IConvocatoriaPartidaPresupuestaria>(partidaPresupuestaria))
        );
      });
    }
  }

  /**
   * Insertamos partida presupuestaria
   *
   * @param partidaPresupuestaria partida presupuestaria
   */
  public addPartidaPresupuestaria(partidaPresupuestaria: IConvocatoriaPartidaPresupuestaria): void {
    const wrapped = new StatusWrapper<IConvocatoriaPartidaPresupuestaria>(partidaPresupuestaria);
    wrapped.setCreated();
    const current = this.partidasPresupuestarias$.value;
    current.push(wrapped);
    this.partidasPresupuestarias$.next(current);
    this.setChanges(true);
    this.setErrors(false);
  }

  /**
   * Elimina la partida presupuestaria de la tabla y se añade a la lista de eliminados
   *
   * @param partidaPresupuestaria partida presupuestaria
   */
  public deletePartidaPresupuestaria(partidaPresupuestaria: StatusWrapper<IConvocatoriaPartidaPresupuestaria>): void {
    const current = this.partidasPresupuestarias$.value;
    const indexpartidaPresupuestaria = current.findIndex(
      (value: StatusWrapper<IConvocatoriaPartidaPresupuestaria>) => value === partidaPresupuestaria
    );

    if (!partidaPresupuestaria.created) {
      this.partidasPresupuestariasEliminadas.push(current[indexpartidaPresupuestaria]);
      this.setChanges(true);
    }

    current.splice(indexpartidaPresupuestaria, 1);
    this.partidasPresupuestarias$.next(current);
  }
  saveOrUpdate(): Observable<void> {
    return merge(
      this.deletePartidasPresupuestarias(),
      this.updatePartidasPresupuestarias(),
      this.createPartidasPresupuestarias()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deletePartidasPresupuestarias(): Observable<void> {
    if (this.partidasPresupuestariasEliminadas.length === 0) {
      return of(void 0);
    }
    return from(this.partidasPresupuestariasEliminadas).pipe(
      mergeMap((wrapped) => {
        return this.convocatoriaPartidaPresupuestariaService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.partidasPresupuestariasEliminadas = this.partidasPresupuestariasEliminadas.filter(deletedHito =>
                deletedHito.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private createPartidasPresupuestarias(): Observable<void> {
    const createdPartidasPresupuestarias = this.partidasPresupuestarias$.value.filter((convocatoriaHito) => convocatoriaHito.created);
    if (createdPartidasPresupuestarias.length === 0) {
      return of(void 0);
    }
    createdPartidasPresupuestarias.forEach(
      (wrapper) => wrapper.value.convocatoriaId = this.getKey() as number
    );
    return from(createdPartidasPresupuestarias).pipe(
      mergeMap((wrappedPartidasPresupuestarias) => {
        return this.convocatoriaPartidaPresupuestariaService.create(wrappedPartidasPresupuestarias.value).pipe(
          map((updatedPartidasPresupuestarias) => {
            const index = this.partidasPresupuestarias$.value.findIndex((currentPartidasPresupuestarias) => currentPartidasPresupuestarias === wrappedPartidasPresupuestarias);
            this.partidasPresupuestarias$.value[index] = new StatusWrapper<IConvocatoriaPartidaPresupuestaria>(updatedPartidasPresupuestarias);
          })
        );
      })
    );
  }

  private updatePartidasPresupuestarias(): Observable<void> {
    const updatePartidasPresupuestarias = this.partidasPresupuestarias$.value.filter((convocatoriaHito) => convocatoriaHito.edited);
    if (updatePartidasPresupuestarias.length === 0) {
      return of(void 0);
    }
    return from(updatePartidasPresupuestarias).pipe(
      mergeMap((wrappedPartidasPresupuestarias) => {
        return this.convocatoriaPartidaPresupuestariaService.update(wrappedPartidasPresupuestarias.value.id, wrappedPartidasPresupuestarias.value).pipe(
          map((updatedPartidasPresupuestarias) => {
            const index = this.partidasPresupuestarias$.value.findIndex((currentPartidasPresupuestarias) => currentPartidasPresupuestarias === wrappedPartidasPresupuestarias);
            this.partidasPresupuestarias$.value[index] = new StatusWrapper<IConvocatoriaPartidaPresupuestaria>(updatedPartidasPresupuestarias);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.partidasPresupuestarias$.value.some((wrapper) => wrapper.touched);
    return (this.partidasPresupuestariasEliminadas.length > 0 || touched);
  }


}
