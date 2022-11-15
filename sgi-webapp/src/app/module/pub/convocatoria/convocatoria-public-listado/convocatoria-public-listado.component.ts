import { Component, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { ROUTE_NAMES } from '@core/route.names';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { EmpresaPublicService } from '@core/services/sgemp/empresa-public.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestFilter, SgiRestListResult } from '@sgi/framework/http/';
import { from, Observable, of } from 'rxjs';
import { map, mergeAll, mergeMap, switchMap } from 'rxjs/operators';
import { PUB_ROUTE_NAMES } from '../../pub-route-names';
import { CONVOCATORIA_PUBLIC_ID_KEY } from '../../solicitud/solicitud-crear/solicitud-public-crear.guard';

const MSG_ERROR = marker('error.load');

interface IConvocatoriaListado {
  convocatoria: IConvocatoria;
  fase: IConvocatoriaFase;
  entidadConvocante: IConvocatoriaEntidadConvocante;
  entidadConvocanteEmpresa: IEmpresa;
  entidadFinanciadora: IConvocatoriaEntidadFinanciadora;
  entidadFinanciadoraEmpresa: IEmpresa;
}

@Component({
  selector: 'sgi-convocatoria-public-listado',
  templateUrl: './convocatoria-public-listado.component.html',
  styleUrls: ['./convocatoria-public-listado.component.scss']
})
export class ConvocatoriaPublicListadoComponent
  extends AbstractTablePaginationComponent<IConvocatoriaListado>
  implements OnInit, OnDestroy {

  convocatorias$: Observable<IConvocatoriaListado[]>;

  mapTramitable: Map<number, boolean> = new Map();

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get PUB_ROUTE_NAMES() {
    return PUB_ROUTE_NAMES;
  }

  get ROUTE_NAMES() {
    return ROUTE_NAMES;
  }

  constructor(
    protected snackBarService: SnackBarService,
    private convocatoriaService: ConvocatoriaPublicService,
    private empresaService: EmpresaPublicService
  ) {
    super(snackBarService, MSG_ERROR);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.filter = this.createFilter();
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IConvocatoriaListado>> {
    return this.convocatoriaService.findAllInvestigador(this.getFindOptions(reset)).pipe(
      map(result => {
        const convocatorias = result.items.map((convocatoria) => {
          return {
            convocatoria,
            entidadConvocante: {} as IConvocatoriaEntidadConvocante,
            entidadConvocanteEmpresa: {} as IEmpresa,
            entidadFinanciadora: {} as IConvocatoriaEntidadFinanciadora,
            entidadFinanciadoraEmpresa: {} as IEmpresa,
            fase: {} as IConvocatoriaFase
          } as IConvocatoriaListado;
        });
        return {
          page: result.page,
          total: result.total,
          items: convocatorias
        } as SgiRestListResult<IConvocatoriaListado>;
      }),
      switchMap((result) => {
        return from(result.items).pipe(
          mergeMap(element => {
            return this.convocatoriaService.tramitable(element.convocatoria.id).pipe(
              map((value) => {
                this.mapTramitable.set(element.convocatoria.id, value);
                return element;
              })
            );
          }),
          map((convocatoriaListado) => {
            return this.convocatoriaService.findEntidadesFinanciadoras(convocatoriaListado.convocatoria.id).pipe(
              map(entidadFinanciadora => {
                if (entidadFinanciadora.items.length > 0) {
                  convocatoriaListado.entidadFinanciadora = entidadFinanciadora.items[0];
                }
                return convocatoriaListado;
              }),
              switchMap(() => {
                if (convocatoriaListado.entidadFinanciadora.id) {
                  return this.empresaService.findById(convocatoriaListado.entidadFinanciadora.empresa.id).pipe(
                    map(empresa => {
                      convocatoriaListado.entidadFinanciadoraEmpresa = empresa;
                      return convocatoriaListado;
                    })
                  );
                }
                return of(convocatoriaListado);
              }),
              switchMap(() => {
                return this.convocatoriaService.findAllConvocatoriaFases(convocatoriaListado.convocatoria.id).pipe(
                  map(convocatoriaFase => {
                    if (convocatoriaFase.items.length > 0) {
                      convocatoriaListado.fase = convocatoriaFase.items[0];
                    }
                    return convocatoriaListado;
                  })
                );
              }),
              switchMap(() => {
                return this.convocatoriaService.findAllConvocatoriaEntidadConvocantes(convocatoriaListado.convocatoria.id).pipe(
                  map(convocatoriaEntidadConvocante => {
                    if (convocatoriaEntidadConvocante.items.length > 0) {
                      convocatoriaListado.entidadConvocante = convocatoriaEntidadConvocante.items[0];
                    }
                    return convocatoriaListado;
                  }),
                  switchMap(() => {
                    if (convocatoriaListado.entidadConvocante.id) {
                      return this.empresaService.findById(convocatoriaListado.entidadConvocante.entidad.id).pipe(
                        map(empresa => {
                          convocatoriaListado.entidadConvocanteEmpresa = empresa;
                          return convocatoriaListado;
                        })
                      );
                    }
                    return of(convocatoriaListado);
                  }),
                );
              })
            );
          }),
          mergeAll(),
          map(() => result)
        );
      }),
    );
  }

  protected initColumns(): void {
    this.columnas = [
      'titulo', 'codigo', 'fechaInicioSolicitud', 'fechaFinSolicitud',
      'entidadConvocante', 'entidadFinanciadora',
      'fuenteFinanciacion', 'acciones'
    ];
  }

  protected loadTable(reset?: boolean): void {
    this.convocatorias$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    return null;
  }

  getSolicitudState(idConvocatoria: number) {
    return { [CONVOCATORIA_PUBLIC_ID_KEY]: idConvocatoria };
  }

}
