import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IPrograma } from '@core/models/csp/programa';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaEntidadConvocanteService } from '@core/services/csp/convocatoria-entidad-convocante.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, takeLast, tap } from 'rxjs/operators';

export interface ConvocatoriaEntidadConvocanteData {
  empresa: IEmpresa;
  entidadConvocante: StatusWrapper<IConvocatoriaEntidadConvocante>;
  plan: IPrograma;
  programa: IPrograma;
  modalidad: IPrograma;
}

export class ConvocatoriaEntidadesConvocantesFragment extends Fragment {
  private entidadesConvocantesEliminadas: ConvocatoriaEntidadConvocanteData[] = [];
  data$ = new BehaviorSubject<ConvocatoriaEntidadConvocanteData[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private convocatoriaService: ConvocatoriaService,
    private convocatoriaEntidadConvocanteService: ConvocatoriaEntidadConvocanteService,
    private empresaService: EmpresaService,
    public isConvocatoriaVinculada: boolean,
    public hasEditPerm: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const subscription = this.convocatoriaService.findAllConvocatoriaEntidadConvocantes(this.getKey() as number).pipe(
        map((response) => response.items),
        map(convocatoriaEntidadConvocantes => {
          return convocatoriaEntidadConvocantes.map(entidadConvocante => {
            const element: ConvocatoriaEntidadConvocanteData = {
              empresa: {} as IEmpresa,
              entidadConvocante: new StatusWrapper<IConvocatoriaEntidadConvocante>(entidadConvocante),
              modalidad: undefined,
              programa: undefined,
              plan: undefined
            };
            this.fillRelationshipData(element);
            return element;
          });
        }),
        mergeMap(entidadConvocanteData => {
          return from(entidadConvocanteData).pipe(
            mergeMap((data) => {
              return this.loadEmpresa(data);
            })
          );
        }),
      ).subscribe((convocatoriaEntidadConvocanteData) => {
        const current = this.data$.value;
        current.push(convocatoriaEntidadConvocanteData);
        this.data$.next(current);
      });
      this.subscriptions.push(subscription);
    }
  }

  private loadEmpresa(data: ConvocatoriaEntidadConvocanteData): Observable<ConvocatoriaEntidadConvocanteData> {
    return this.empresaService.findById(data.entidadConvocante.value.entidad.id).pipe(
      map(empresa => {
        data.empresa = empresa;
        return data;
      }),
      catchError((error) => {
        this.logger.error(error);
        return of(data);
      })
    );
  }

  private getSecondLevelPrograma(programa: IPrograma): IPrograma {
    if (programa?.padre?.padre) {
      return this.getSecondLevelPrograma(programa.padre);
    }
    return programa;
  }

  private fillRelationshipData(data: ConvocatoriaEntidadConvocanteData): void {
    const modalidad = data.entidadConvocante.value.programa;
    const programa = this.getSecondLevelPrograma(modalidad);
    const plan = programa?.padre ? programa.padre : modalidad;

    data.plan = plan;
    data.programa = programa?.id === plan?.id ? undefined : programa;
    data.modalidad = modalidad?.id === programa?.id ? undefined : modalidad;
  }

  public deleteConvocatoriaEntidadConvocante(data: ConvocatoriaEntidadConvocanteData) {
    if (!data.entidadConvocante.created) {
      this.entidadesConvocantesEliminadas.push(data);
    }

    const current = this.data$.value;
    const index = current.findIndex(value => value === data);
    current.splice(index, 1);
    this.data$.next(current);

    this.setChanges(true);
  }

  public updateConvocatoriaEntidadConvocante(data: ConvocatoriaEntidadConvocanteData) {
    this.fillRelationshipData(data);
    if (!data.entidadConvocante.created) {
      data.entidadConvocante.setEdited();
    }
    const current = this.data$.value;
    this.data$.next(current);
    this.setChanges(true);
  }

  public addConvocatoriaEntidadConvocante(data: ConvocatoriaEntidadConvocanteData) {
    this.fillRelationshipData(data);
    data.entidadConvocante.setCreated();
    const current = this.data$.value;
    current.push(data);
    this.data$.next(current);
    this.setChanges(true);
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteConvocatoriaEntidadConvocantes(),
      this.updateConvocatoriaEntidadConvocantes(),
      this.createConvocatoriaEntidadConvocantes()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteConvocatoriaEntidadConvocantes(): Observable<void> {
    if (this.entidadesConvocantesEliminadas.length === 0) {
      return of(void 0);
    }
    return from(this.entidadesConvocantesEliminadas).pipe(
      mergeMap((data) => {
        return this.convocatoriaEntidadConvocanteService.deleteById(data.entidadConvocante.value.id).pipe(
          tap(() => {
            this.entidadesConvocantesEliminadas = this.entidadesConvocantesEliminadas.filter(deleted =>
              deleted === data);
          })
        );
      })
    );
  }

  private updateConvocatoriaEntidadConvocantes(): Observable<void> {
    const editedEntidades = this.data$.value.filter((value) => value.entidadConvocante.edited);
    if (editedEntidades.length === 0) {
      return of(void 0);
    }
    return from(editedEntidades).pipe(
      mergeMap((data) => {
        return this.convocatoriaEntidadConvocanteService.update(
          data.entidadConvocante.value.id, data.entidadConvocante.value).pipe(
            map((updatedEntidad) => {
              data.entidadConvocante = new StatusWrapper<IConvocatoriaEntidadConvocante>(updatedEntidad);
              this.fillRelationshipData(data);
              this.data$.next(this.data$.value);
            })
          );
      })
    );
  }

  private createConvocatoriaEntidadConvocantes(): Observable<void> {
    const createdEntidades = this.data$.value.filter((value) => value.entidadConvocante.created);
    if (createdEntidades.length === 0) {
      return of(void 0);
    }
    createdEntidades.forEach(
      (wrapper) => wrapper.entidadConvocante.value.convocatoriaId = this.getKey() as number
    );
    return from(createdEntidades).pipe(
      mergeMap((data) => {
        return this.convocatoriaEntidadConvocanteService.create(data.entidadConvocante.value).pipe(
          map((createdEntidad) => {
            data.entidadConvocante = new StatusWrapper<IConvocatoriaEntidadConvocante>(createdEntidad);
            this.fillRelationshipData(data);
            this.data$.next(this.data$.value);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.data$.value.some((wrapper) => wrapper.entidadConvocante.touched);
    return (this.entidadesConvocantesEliminadas.length > 0 || touched);
  }
}
