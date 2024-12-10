import { IEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { Fragment } from '@core/services/action-service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

export class SolicitudHistoricoEstadosFragment extends Fragment {

  historicoEstado$: BehaviorSubject<StatusWrapper<IEstadoSolicitud>[]>;

  constructor(
    key: number,
    private solicitudService: SolicitudService,
    public readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
    this.historicoEstado$ = new BehaviorSubject<StatusWrapper<IEstadoSolicitud>[]>([]);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const options: SgiRestFindOptions = {
        sort: new RSQLSgiRestSort('fechaEstado', SgiRestSortDirection.DESC)
      };

      this.solicitudService.findEstadosSolicitud(this.getKey() as number, options).pipe(
        map((response) => response.items)
      ).subscribe((historicoEstados) => {
        this.historicoEstado$.next(historicoEstados.map(
          historicoEstado => new StatusWrapper<IEstadoSolicitud>(historicoEstado))
        );
      });
    }
  }

  saveOrUpdate(): Observable<string | number | void> {
    return of(void 0);
  }

}
