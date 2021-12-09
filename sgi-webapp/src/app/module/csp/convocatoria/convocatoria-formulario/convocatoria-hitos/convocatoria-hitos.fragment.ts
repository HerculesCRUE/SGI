import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaHitoService } from '@core/services/csp/convocatoria-hito.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ConvocatoriaHitosFragment extends Fragment {
  hitos$ = new BehaviorSubject<StatusWrapper<IConvocatoriaHito>[]>([]);
  private hitosEliminados: StatusWrapper<IConvocatoriaHito>[] = [];

  constructor(
    key: number,
    private convocatoriaService: ConvocatoriaService,
    private convocatoriaHitoService: ConvocatoriaHitoService,
    public readonly: boolean,
    public canEdit: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.convocatoriaService.findHitosConvocatoria(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((hitos) => {
        this.hitos$.next(hitos.map(
          listaHitos => new StatusWrapper<IConvocatoriaHito>(listaHitos))
        );
      });
    }
  }

  public addHito(hito: IConvocatoriaHito) {
    const wrapped = new StatusWrapper<IConvocatoriaHito>(hito);
    wrapped.setCreated();
    const current = this.hitos$.value;
    current.push(wrapped);
    this.hitos$.next(current);
    this.setChanges(true);
  }

  public deleteHito(wrapper: StatusWrapper<IConvocatoriaHito>) {
    const current = this.hitos$.value;
    const index = current.findIndex(
      (value) => value.value === wrapper.value
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.hitosEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.hitos$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteHitos(),
      this.updateHitos(),
      this.createHitos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteHitos(): Observable<void> {
    if (this.hitosEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.hitosEliminados).pipe(
      mergeMap((wrapped) => {
        return this.convocatoriaHitoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.hitosEliminados = this.hitosEliminados.filter(deletedHito =>
                deletedHito.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private createHitos(): Observable<void> {
    const createdHitos = this.hitos$.value.filter((convocatoriaHito) => convocatoriaHito.created);
    if (createdHitos.length === 0) {
      return of(void 0);
    }
    createdHitos.forEach(
      (wrapper) => wrapper.value.convocatoriaId = this.getKey() as number
    );
    return from(createdHitos).pipe(
      mergeMap((wrappedHitos) => {
        return this.convocatoriaHitoService.create(wrappedHitos.value).pipe(
          map((createdHito) => {
            const index = this.hitos$.value.findIndex((currenthitos) => currenthitos === wrappedHitos);
            const hitoListado = wrappedHitos.value;
            hitoListado.id = createdHito.id;
            this.hitos$.value[index] = new StatusWrapper<IConvocatoriaHito>(hitoListado);
            this.hitos$.next(this.hitos$.value);
          })
        );
      })
    );
  }

  private updateHitos(): Observable<void> {
    const updateHitos = this.hitos$.value.filter((convocatoriaHito) => convocatoriaHito.edited);
    if (updateHitos.length === 0) {
      return of(void 0);
    }
    return from(updateHitos).pipe(
      mergeMap((wrappedHitos) => {
        return this.convocatoriaHitoService.update(wrappedHitos.value.id, wrappedHitos.value).pipe(
          map((updatedHitos) => {
            const index = this.hitos$.value.findIndex((currenthitos) => currenthitos === wrappedHitos);
            this.hitos$.value[index] = new StatusWrapper<IConvocatoriaHito>(updatedHitos);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.hitos$.value.some((wrapper) => wrapper.touched);
    return !(this.hitosEliminados.length > 0 || touched);
  }

}
