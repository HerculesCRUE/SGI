import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { Fragment } from '@core/services/action-service';
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
      (value: StatusWrapper<IConvocatoriaEnlace>) => value.value === wrapper.value
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
    return this.updateEnlaces()
      .pipe(
        map(updatedEnlaces => {
          this.enlace$.next(updatedEnlaces.map(updatedEnlace => new StatusWrapper(updatedEnlace)));
        }),
        tap(() => this.setChanges(false)),
      );
  }

  private updateEnlaces(): Observable<IConvocatoriaEnlace[]> {
    return this.convocatoriaService.updateEnlaces(this.getKey() as number, this.enlace$.value.map(wrapper => wrapper.value));
  }

  getSelectedUrls(): string[] {
    const urls = this.enlace$.value.map(enlace => enlace.value.url);
    return urls;
  }
}
