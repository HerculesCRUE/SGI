import { CausaExencion } from '@core/models/csp/proyecto';
import { TipoEntidad } from '@core/models/csp/relacion-ejecucion-economica';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { Fragment } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { map, mergeMap, toArray } from 'rxjs/operators';
import { IRelacionEjecucionEconomicaWithResponsables } from '../../ejecucion-economica.action.service';

export interface IRelacionEjecucionEconomicaWithIva extends IRelacionEjecucionEconomicaWithResponsables {
  iva: number;
  causaExencion: CausaExencion;
  sectorIva: boolean;
}

export class ProyectosFragment extends Fragment {
  relaciones$ = new BehaviorSubject<IRelacionEjecucionEconomicaWithIva[]>([]);

  constructor(
    key: number,
    private proyectoSge: IProyectoSge,
    private relaciones: IRelacionEjecucionEconomicaWithResponsables[],
    private proyectoService: ProyectoService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    from(this.relaciones as IRelacionEjecucionEconomicaWithIva[]).pipe(
      mergeMap(relacion => {
        if (relacion.tipoEntidad === TipoEntidad.PROYECTO) {
          return this.proyectoService.findById(relacion.id).pipe(
            map(proyecto => {
              relacion.iva = proyecto.iva?.iva;
              relacion.causaExencion = proyecto.causaExencion;
              relacion.sectorIva = this.proyectoSge.sectorIva;
              return relacion;
            })
          )
        }

        return of(relacion);
      }),
      toArray()
    ).subscribe(relaciones => this.relaciones$.next(relaciones));
  }

  saveOrUpdate(): Observable<void> {
    return of(void 0);
  }

}
