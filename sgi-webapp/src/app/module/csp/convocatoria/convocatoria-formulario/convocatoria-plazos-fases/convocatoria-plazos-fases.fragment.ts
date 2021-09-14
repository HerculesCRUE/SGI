import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { Fragment } from '@core/services/action-service';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { ConvocatoriaFaseService } from '@core/services/csp/convocatoria-fase.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ConvocatoriaPlazosFasesFragment extends Fragment {
  plazosFase$ = new BehaviorSubject<StatusWrapper<IConvocatoriaFase>[]>([]);
  private fasesEliminadas: StatusWrapper<IConvocatoriaFase>[] = [];
  fasePresentacionSolicitudes: string;

  constructor(
    key: number,
    private convocatoriaService: ConvocatoriaService,
    private convocatoriaFaseService: ConvocatoriaFaseService,
    private configuracionSolicitudService: ConfiguracionSolicitudService,
    public readonly: boolean,
    public canEdit: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.convocatoriaService.findAllConvocatoriaFases(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((plazosFases) => {
        this.plazosFase$.next(plazosFases.map(
          plazosFase => new StatusWrapper<IConvocatoriaFase>(plazosFase))
        );
      });
      this.configuracionSolicitudService.findByConvocatoriaId(this.getKey() as number).subscribe(
        (configuracionSolicitud) => {
          if (configuracionSolicitud != null && configuracionSolicitud.fasePresentacionSolicitudes != null) {
            this.fasePresentacionSolicitudes = configuracionSolicitud.fasePresentacionSolicitudes.tipoFase.nombre;
          } else {
            this.fasePresentacionSolicitudes = undefined;
          }
        })
    }
  }

  /**
   * Recuperamos todas las convocatorias fase
   */
  public getConvocatoriasFases(): IConvocatoriaFase[] {
    const fechas = this.plazosFase$.value.map(plazoFase => plazoFase.value);
    return fechas;
  }

  /**
   * Insertamos plazos fase
   *
   * @param plazoFase PlazoFase
   */
  public addPlazosFases(plazoFase: IConvocatoriaFase) {
    const wrapped = new StatusWrapper<IConvocatoriaFase>(plazoFase);
    wrapped.setCreated();
    const current = this.plazosFase$.value;
    current.push(wrapped);
    this.plazosFase$.next(current);
    this.setChanges(true);
    this.setErrors(false);
  }

  public deleteFase(wrapper: StatusWrapper<IConvocatoriaFase>) {
    const current = this.plazosFase$.value;
    const index = current.findIndex(
      (value: StatusWrapper<IConvocatoriaFase>) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.fasesEliminadas.push(current[index]);
      }
      wrapper.setDeleted();
      current.splice(index, 1);
      this.plazosFase$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteFases(),
      this.updateFases(),
      this.createFases()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.plazosFase$.next(this.plazosFase$.value);
          this.setChanges(false);
        }
      })
    );
  }

  private deleteFases(): Observable<void> {
    if (this.fasesEliminadas.length === 0) {
      return of(void 0);
    }
    return from(this.fasesEliminadas).pipe(
      mergeMap((wrapped) => {
        return this.convocatoriaFaseService.deleteById(wrapped.value.id).pipe(
          tap(() => {
            this.fasesEliminadas = this.fasesEliminadas.filter(deletedFase =>
              deletedFase.value.id !== wrapped.value.id);
          })
        );
      })
    );
  }

  private createFases(): Observable<void> {
    const createdFases = this.plazosFase$.value.filter((convocatoriaFase) => convocatoriaFase.created);
    if (createdFases.length === 0) {
      return of(void 0);
    }
    createdFases.forEach(
      (wrapper: StatusWrapper<IConvocatoriaFase>) => wrapper.value.convocatoriaId = this.getKey() as number
    );
    return from(createdFases).pipe(
      mergeMap((wrappedFase) => {
        return this.convocatoriaFaseService.create(wrappedFase.value).pipe(
          map((result) => {
            const index = this.plazosFase$.value.findIndex((currentFases) => currentFases === wrappedFase);
            this.plazosFase$.value[index] = new StatusWrapper<IConvocatoriaFase>(result);
          })
        );
      })
    );
  }

  private updateFases(): Observable<void> {
    const updateFases = this.plazosFase$.value.filter((convocatoriaFase) => convocatoriaFase.edited);
    if (updateFases.length === 0) {
      return of(void 0);
    }
    return from(updateFases).pipe(
      mergeMap((wrappedFases) => {
        return this.convocatoriaFaseService.update(wrappedFases.value.id, wrappedFases.value).pipe(
          map((updatedFases) => {
            const index = this.plazosFase$.value.findIndex((currentFases) => currentFases === wrappedFases);
            this.plazosFase$.value[index] = new StatusWrapper<IConvocatoriaFase>(updatedFases);
          })
        );
      })
    );
  }

  /**
   * Comprueba si se ejecutaron correctamente todos borrados, actualizaciones y creaciones.
   *
   * @returns true si no queda ningun cambio pendiente.
   */
  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.plazosFase$.value.some((wrapper) => wrapper.touched);
    const hasNoDeleted = this.fasesEliminadas.length > 0;

    return !hasTouched && !hasNoDeleted;
  }

}
