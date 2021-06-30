import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http/';
import { from, Observable, of } from 'rxjs';
import { map, mergeAll, mergeMap, switchMap } from 'rxjs/operators';
import { INV_ROUTE_NAMES } from 'src/app/module/inv/inv-route-names';
import { CONVOCATORIA_ID_KEY } from '../../solicitud/solicitud-crear/solicitud-crear.guard';

const MSG_ERROR = marker('error.load');
const AREA_TEMATICA_KEY = marker('csp.area-tematica');

interface IConvocatoriaListado {
  convocatoria: IConvocatoria;
  fase: IConvocatoriaFase;
  entidadConvocante: IConvocatoriaEntidadConvocante;
  entidadConvocanteEmpresa: IEmpresa;
  entidadFinanciadora: IConvocatoriaEntidadFinanciadora;
  entidadFinanciadoraEmpresa: IEmpresa;
}

@Component({
  selector: 'sgi-convocatoria-listado-inv',
  templateUrl: './convocatoria-listado-inv.component.html',
  styleUrls: ['./convocatoria-listado-inv.component.scss']
})
export class ConvocatoriaListadoInvComponent extends AbstractTablePaginationComponent<IConvocatoriaListado>
  implements OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  convocatorias$: Observable<IConvocatoriaListado[]>;

  busquedaAvanzada = false;
  mapTramitable: Map<number, boolean> = new Map();
  msgParamAreaTematicaEntity = {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get INV_ROUTE_NAMES() {
    return INV_ROUTE_NAMES;
  }

  constructor(
    protected snackBarService: SnackBarService,
    private convocatoriaService: ConvocatoriaService,
    private empresaService: EmpresaService,
    private translate: TranslateService,
    private authService: SgiAuthService
  ) {
    super(snackBarService, MSG_ERROR);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(18%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.formGroup = new FormGroup({
      codigo: new FormControl(''),
      titulo: new FormControl(''),
      fechaPublicacionDesde: new FormControl(null),
      fechaPublicacionHasta: new FormControl(null),
      abiertoPlazoPresentacionSolicitud: new FormControl(true),
      aplicarFiltro: new FormControl(true),
      finalidad: new FormControl(null),
      ambitoGeografico: new FormControl(null),
      entidadConvocante: new FormControl(null),
      entidadFinanciadora: new FormControl(null),
      fuenteFinanciacion: new FormControl(null),
      areaTematica: new FormControl(null),
    });

    this.filter = this.createFilter();
  }

  private setupI18N(): void {
    this.translate.get(
      AREA_TEMATICA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamAreaTematicaEntity = { entity: value });
  }

  protected createObservable(): Observable<SgiRestListResult<IConvocatoriaListado>> {
    const observable$ = this.convocatoriaService.findAllInvestigador(this.getFindOptions()).pipe(
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
            if (this.authService.hasAuthority('CSP-SOL-INV-C')) {
              return this.convocatoriaService.tramitable(element.convocatoria.id).pipe(
                map((value) => {
                  this.mapTramitable.set(element.convocatoria.id, value);
                  return element;
                })
              );
            }
            return of(element);
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
          map(() => result)
        );
      }),
    );

    return observable$;
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
    const controls = this.formGroup.controls;
    return new RSQLSgiRestFilter('codigo', SgiRestFilterOperator.LIKE_ICASE, controls.codigo.value)
      .and('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('fechaPublicacion', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaPublicacionDesde.value))
      .and('fechaPublicacion', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaPublicacionHasta.value))
      .and('abiertoPlazoPresentacionSolicitud', SgiRestFilterOperator.EQUALS, controls.abiertoPlazoPresentacionSolicitud.value?.toString())
      .and('finalidad.id', SgiRestFilterOperator.EQUALS, controls.finalidad.value?.id?.toString())
      .and('ambitoGeografico.id', SgiRestFilterOperator.EQUALS, controls.ambitoGeografico.value?.id?.toString())
      .and('entidadesConvocantes.entidadRef', SgiRestFilterOperator.EQUALS, controls.entidadConvocante.value?.id)
      .and('entidadesFinanciadoras.entidadRef', SgiRestFilterOperator.EQUALS, controls.entidadFinanciadora.value?.id)
      .and('entidadesFinanciadoras.fuenteFinanciacion.id', SgiRestFilterOperator.EQUALS, controls.fuenteFinanciacion.value?.id?.toString())
      .and('areasTematicas.areaTematica.id', SgiRestFilterOperator.EQUALS, controls.areaTematica.value?.id?.toString());
  }

  onClearFilters() {
    this.formGroup.reset();
    this.formGroup.controls.abiertoPlazoPresentacionSolicitud.setValue(true);
    this.onSearch();
  }

  /**
   * Mostrar busqueda avanzada
   */
  toggleBusquedaAvanzada() {
    this.busquedaAvanzada = !this.busquedaAvanzada;
    this.formGroup.controls.abiertoPlazoPresentacionSolicitud.setValue(!this.busquedaAvanzada);
    this.formGroup.controls.aplicarFiltro.setValue(!this.busquedaAvanzada);
    this.onSearch();
  }

  getSolicitudState(idConvocatoria: number) {
    return { [CONVOCATORIA_ID_KEY]: idConvocatoria };
  }

}
