import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IPrograma } from '@core/models/csp/programa';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { EmpresaPublicService } from '@core/services/sgemp/empresa-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';

export interface ConvocatoriaEntidadConvocantePublicData {
  empresa: IEmpresa;
  entidadConvocante: StatusWrapper<IConvocatoriaEntidadConvocante>;
  plan: IPrograma;
  programa: IPrograma;
  modalidad: IPrograma;
}

export class ConvocatoriaEntidadesConvocantesPublicFragment extends Fragment {
  data$ = new BehaviorSubject<ConvocatoriaEntidadConvocantePublicData[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private convocatoriaService: ConvocatoriaPublicService,
    private empresaService: EmpresaPublicService
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
            const element: ConvocatoriaEntidadConvocantePublicData = {
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

  private loadEmpresa(data: ConvocatoriaEntidadConvocantePublicData): Observable<ConvocatoriaEntidadConvocantePublicData> {
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

  private fillRelationshipData(data: ConvocatoriaEntidadConvocantePublicData): void {
    const modalidad = data.entidadConvocante.value.programa;
    const programa = this.getSecondLevelPrograma(modalidad);
    const plan = programa?.padre ? programa.padre : modalidad;

    data.plan = plan;
    data.programa = programa?.id === plan?.id ? undefined : programa;
    data.modalidad = modalidad?.id === programa?.id ? undefined : modalidad;
  }

  saveOrUpdate(): Observable<void> {
    throw new Error('Method not implemented.');
  }

}
