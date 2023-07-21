import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConfigService } from '@core/services/cnf/config.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, zip } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { ISeguimientoListadoModalData, SeguimientoListadoExportModalComponent } from '../../seguimiento/modals/seguimiento-listado-export-modal/seguimiento-listado-export-modal.component';
import { RolPersona } from '../../seguimiento/seguimiento-listado-export.service';

@Component({
  selector: 'sgi-gestion-seguimiento-listado',
  templateUrl: './gestion-seguimiento-listado.component.html',
  styleUrls: ['./gestion-seguimiento-listado.component.scss']
})
export class GestionSeguimientoListadoComponent extends AbstractTablePaginationComponent<IEvaluacion> implements OnInit {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  evaluaciones$: Observable<IEvaluacion[]> = of();

  private limiteRegistrosExportacionExcel: string;

  get tipoColectivoSolicitante() {
    return TipoColectivo.SOLICITANTE_ETICA;
  }

  constructor(
    private readonly logger: NGXLogger,
    private readonly evaluacionesService: EvaluacionService,
    protected readonly personaService: PersonaService,
    private matDialog: MatDialog,
    private readonly cnfService: ConfigService,
  ) {
    super();

    this.suscripciones = [];

    this.totalElementos = 0;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(13%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.formGroup = new FormGroup({
      comite: new FormControl(null, []),
      fechaEvaluacionInicio: new FormControl(null, []),
      fechaEvaluacionFin: new FormControl(null, []),
      referenciaMemoria: new FormControl('', []),
      solicitante: new FormControl('', []),
      tipoEvaluacion: new FormControl(null, [])
    });

    this.suscripciones.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('eti-exp-max-num-registros-excel-seguimiento-listado').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IEvaluacion>> {
    return this.evaluacionesService.findSeguimientoMemoria(this.getFindOptions(reset));
  }

  protected initColumns(): void {
    this.displayedColumns = ['memoria.comite.comite', 'tipoEvaluacion', 'memoria.tipoMemoria.nombre', 'fechaDictamen', 'memoria.numReferencia', 'solicitante',
      'dictamen.nombre', 'version', 'acciones'];
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('memoria.comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
      .and('tipoEvaluacion.id', SgiRestFilterOperator.EQUALS, controls.tipoEvaluacion.value?.id?.toString())
      .and('fechaDictamen', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaEvaluacionInicio.value))
      .and('fechaDictamen', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaEvaluacionFin.value))
      .and('memoria.numReferencia', SgiRestFilterOperator.LIKE_ICASE, controls.referenciaMemoria.value)
      .and('memoria.peticionEvaluacion.personaRef', SgiRestFilterOperator.EQUALS, controls.solicitante.value.id);

    return filter;
  }

  protected loadTable(reset?: boolean) {
    // Do the request with paginator/sort/filter values
    this.evaluaciones$ = this.createObservable().pipe(
      switchMap((response) => {
        // Map response total
        this.totalElementos = response.total;
        // Reset pagination to first page
        if (reset) {
          this.paginator.pageIndex = 0;
        }

        if (response.items) {
          // Solicitantes
          const listObservables: Observable<IEvaluacion>[] = [];
          response.items.forEach((evaluacion) => {
            const evaluacion$ = this.personaService.findById(
              evaluacion.memoria?.peticionEvaluacion?.solicitante?.id
            ).pipe(
              map((personaInfo) => {
                evaluacion.memoria.peticionEvaluacion.solicitante = personaInfo;
                return evaluacion;
              })
            );
            listObservables.push(evaluacion$);
          });

          return zip(...listObservables);
        } else {
          return of([]);
        }
      }),
      catchError((error) => {
        this.logger.error(error);
        // On error reset pagination values
        this.paginator.firstPage();
        this.totalElementos = 0;
        this.processError(error);
        return of([]);
      })
    );
  }

  /**
   * Clean filters an reload the table
   */
  protected resetFilters(): void {
    super.resetFilters();
    this.formGroup.controls.fechaEvaluacionInicio.setValue(null);
    this.formGroup.controls.fechaEvaluacionFin.setValue(null);
  }

  public openExportModal() {
    const data: ISeguimientoListadoModalData = {
      findOptions: this.findOptions,
      rolPersona: RolPersona.GESTOR,
      totalRegistrosExportacionExcel: this.totalElementos,
      limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
    };

    const config = {
      data
    };
    this.matDialog.open(SeguimientoListadoExportModalComponent, config);
  }

}
