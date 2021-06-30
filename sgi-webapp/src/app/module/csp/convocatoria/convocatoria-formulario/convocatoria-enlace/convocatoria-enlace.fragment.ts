import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaEnlaceService } from '@core/services/csp/convocatoria-enlace.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ConvocatoriaEnlaceFragment extends Fragment {
  enlace$ = new BehaviorSubject<StatusWrapper<IConvocatoriaEnlace>[]>([]);
  private enlaceEliminados: StatusWrapper<IConvocatoriaEnlace>[] = [];

  constructor(
    key: number,
    private convocatoriaService: ConvocatoriaService,
    private convocatoriaEnlaceService: ConvocatoriaEnlaceService,
    public readonly: boolean,
    public canEdit: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.convocatoriaService.getEnlaces(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((enlace) => {
        this.enlace$.next(enlace.map(
          enlaces => new StatusWrapper<IConvocatoriaEnlace>(enlaces))
        );
      });
    }
  }

  public addEnlace(enlace: IConvocatoriaEnlace): void {
    const wrapped = new StatusWrapper<IConvocatoriaEnlace>(enlace);
    wrapped.setCreated();
    const current = this.enlace$.value;
    current.push(wrapped);
    this.enlace$.next(current);
    this.setChanges(true);
  }

  public deleteEnlace(wrapper: StatusWrapper<IConvocatoriaEnlace>): void {
    const current = this.enlace$.value;
    const index = current.findIndex(
      (value: StatusWrapper<IConvocatoriaEnlace>) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.enlaceEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.enlace$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteEnlaces(),
      this.updateEnlaces(),
      this.createEnlaces()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteEnlaces(): Observable<void> {
    if (this.enlaceEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.enlaceEliminados).pipe(
      mergeMap((wrapped) => {
        return this.convocatoriaEnlaceService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.enlaceEliminados = this.enlaceEliminados.filter(deletedEnlace =>
                deletedEnlace.value.id !== wrapped.value.id);
            })
          );
      }));
  }

  private createEnlaces(): Observable<void> {
    const createdEnlaces = this.enlace$.value.filter((convocatoriaEnlace) => convocatoriaEnlace.created);
    if (createdEnlaces.length === 0) {
      return of(void 0);
    }
    createdEnlaces.forEach(
      (wrapper: StatusWrapper<IConvocatoriaEnlace>) => wrapper.value.convocatoriaId = this.getKey() as number
    );
    return from(createdEnlaces).pipe(
      mergeMap((wrappedEnlaces) => {
        return this.convocatoriaEnlaceService.create(wrappedEnlaces.value).pipe(
          map((updatedEnlaces) => {
            const index = this.enlace$.value.findIndex((currentEnlaces) => currentEnlaces === wrappedEnlaces);
            this.enlace$.value[index] = new StatusWrapper<IConvocatoriaEnlace>(updatedEnlaces);
          })
        );
      })
    );
  }

  private updateEnlaces(): Observable<void> {
    const updateEnlaces = this.enlace$.value.filter((convocatoriaEnlace) => convocatoriaEnlace.edited);
    if (updateEnlaces.length === 0) {
      return of(void 0);
    }
    return from(updateEnlaces).pipe(
      mergeMap((wrappedEnlaces) => {
        return this.convocatoriaEnlaceService.update(wrappedEnlaces.value.id, wrappedEnlaces.value).pipe(
          map((updatedEnlaces) => {
            const index = this.enlace$.value.findIndex((currentEnlaces) => currentEnlaces === wrappedEnlaces);
            this.enlace$.value[index] = new StatusWrapper<IConvocatoriaEnlace>(updatedEnlaces);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.enlace$.value.some((wrapper) => wrapper.touched);
    return (this.enlaceEliminados.length > 0 || touched);
  }

  getSelectedUrls(): string[] {
    const urls = this.enlace$.value.map(enlace => enlace.value.url);
    return urls;
  }
}
