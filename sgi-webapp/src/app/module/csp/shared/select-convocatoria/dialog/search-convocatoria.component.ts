import { AfterViewInit, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SearchModalData } from '@core/component/select-dialog/select-dialog.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { Module } from '@core/module';
import { ROUTE_NAMES } from '@core/route.names';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import {
  RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult, SgiRestSortDirection
} from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeAll, switchMap, tap } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../../../csp-route-names';

const MSG_LISTADO_ERROR = marker('error.load');
const ENTITY_KEY = marker('csp.convocatoria');

export interface SearchConvocatoriaModalData extends SearchModalData {
  unidadesGestion: string[];
  investigador: boolean;
}

interface IConvocatoriaListado {
  convocatoria: IConvocatoria;
  fase: IConvocatoriaFase;
  entidadConvocante: IConvocatoriaEntidadConvocante;
  entidadConvocanteEmpresa: IEmpresa;
  entidadFinanciadora: IConvocatoriaEntidadFinanciadora;
  entidadFinanciadoraEmpresa: IEmpresa;
}

@Component({
  templateUrl: './search-convocatoria.component.html',
  styleUrls: ['./search-convocatoria.component.scss']
})
export class SearchConvocatoriaModalComponent implements OnInit, AfterViewInit {
  formGroup: FormGroup;

  displayedColumns = ['codigo', 'titulo', 'fechaInicioSolicitud', 'fechaFinSolicitud',
    'entidadConvocante', 'planInvestigacion', 'entidadFinanciadora',
    'fuenteFinanciacion', 'acciones'];
  elementosPagina = [5, 10, 25, 100];
  totalElementos = 0;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  convocatorias$: Observable<IConvocatoriaListado[]> = of();

  msgParamEntity: {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    public dialogRef: MatDialogRef<SearchConvocatoriaModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SearchConvocatoriaModalData,
    private readonly convocatoriaService: ConvocatoriaService,
    private empresaService: EmpresaService,
    private readonly snackBarService: SnackBarService,
    private readonly translate: TranslateService,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    this.formGroup = new FormGroup({
      codigo: new FormControl(''),
      titulo: new FormControl(this.data.searchTerm),
      fechaPublicacionDesde: new FormControl(null),
      fechaPublicacionHasta: new FormControl(null),
      abiertoPlazoPresentacionSolicitud: new FormControl(''),
    });
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      ENTITY_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

  }

  ngAfterViewInit(): void {
    this.buscarConvocatorias();

    merge(
      this.paginator.page,
      this.sort.sortChange
    ).pipe(
      tap(() => {
        this.buscarConvocatorias();
      })
    ).subscribe();
  }

  buscarConvocatorias(reset?: boolean) {
    const options: SgiRestFindOptions = {
      page: {
        index: reset ? 0 : this.paginator.pageIndex,
        size: this.paginator.pageSize
      },
      sort: new RSQLSgiRestSort(this.sort?.active, SgiRestSortDirection.fromSortDirection(this.sort?.direction)),
      filter: this.buildFilter()
    };

    let convocatoriaFindAll$: Observable<SgiRestListResult<IConvocatoria>>;
    if (this.data.investigador) {
      convocatoriaFindAll$ = this.convocatoriaService.findAllInvestigador(options);
    } else {
      convocatoriaFindAll$ = this.convocatoriaService.findAllRestringidos(options);
    }

    this.convocatorias$ = convocatoriaFindAll$
      .pipe(
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
                      }),
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
                          }),
                        );
                      }
                      return of(convocatoriaListado);
                    }),
                  );
                })
              );
            }),
            mergeAll(),
            map(() => {
              this.totalElementos = result.total;
              return result.items;
            })
          );
        }),
        catchError((error) => {
          this.logger.error(error);
          // On error reset pagination values
          this.paginator.firstPage();
          this.totalElementos = 0;
          this.snackBarService.showError(MSG_LISTADO_ERROR);
          return of([]);
        })
      );
  }

  private buildFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('codigo', SgiRestFilterOperator.LIKE_ICASE, controls.codigo.value)
      .and('fechaPublicacion', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaPublicacionDesde.value))
      .and('fechaPublicacion', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaPublicacionHasta.value))
      .and('abiertoPlazoPresentacionSolicitud', SgiRestFilterOperator.EQUALS, controls.abiertoPlazoPresentacionSolicitud.value?.toString());
    if (this.data.unidadesGestion?.length) {
      filter.and('unidadGestionRef', SgiRestFilterOperator.IN, this.data.unidadesGestion);
    }
    return filter;
  }

  closeModal(convocatoria?: IConvocatoria): void {
    this.dialogRef.close(convocatoria);
  }

  /**
   * Clean filters an reload the table
   */
  onClearFilters(): void {
    FormGroupUtil.clean(this.formGroup);
  }

  openCreate(): void {
    window.open(this.router.serializeUrl(this.router.createUrlTree(['/', Module.CSP.path, CSP_ROUTE_NAMES.CONVOCATORIA, ROUTE_NAMES.NEW])), '_blank');
  }

}
