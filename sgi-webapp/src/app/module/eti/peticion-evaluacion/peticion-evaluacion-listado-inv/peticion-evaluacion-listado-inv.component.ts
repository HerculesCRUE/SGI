import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { MSG_PARAMS } from '@core/i18n';
import { IMemoria } from '@core/models/eti/memoria';
import { IPeticionEvaluacionWithIsEliminable } from '@core/models/eti/peticion-evaluacion-with-is-eliminable';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ConfigService } from '@core/services/cnf/config.service';
import { DialogService } from '@core/services/dialog.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { PeticionEvaluacionListadoExportModalComponent } from '../modals/peticion-evaluacion-listado-export-modal/peticion-evaluacion-listado-export-modal.component';

const MSG_FOOTER = marker('btn.add.entity');
const MSG_DELETE = marker('msg.delete.entity');
const MSG_DELETE_SUCCESS = marker('msg.delete.entity.success');
const PETICION_EVALUACION_KEY = marker('eti.peticion-evaluacion-etica-proyecto');

@Component({
  selector: 'sgi-peticion-evaluacion-listado-inv',
  templateUrl: './peticion-evaluacion-listado-inv.component.html',
  styleUrls: ['./peticion-evaluacion-listado-inv.component.scss']
})
export class PeticionEvaluacionListadoInvComponent
  extends AbstractTablePaginationComponent<IPeticionEvaluacionWithIsEliminable>
  implements OnInit {


  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  textoCrear: string;
  textoDelete: string;
  textoDeleteSuccess: string;

  private limiteRegistrosExportacionExcel: string;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  peticionesEvaluacion$: Observable<IPeticionEvaluacionWithIsEliminable[]> = of();
  memorias$: Observable<IMemoria[]> = of();

  constructor(
    private readonly peticionesEvaluacionService: PeticionEvaluacionService,
    protected readonly snackBarService: SnackBarService,
    private readonly dialogService: DialogService,
    private readonly translate: TranslateService,
    private matDialog: MatDialog,
    private readonly cnfService: ConfigService,
  ) {
    super();

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }


  protected initColumns(): void {
    this.displayedColumns = ['codigo', 'titulo', 'fuenteFinanciacion', 'fechaInicio', 'fechaFin', 'acciones'];
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.formGroup = new FormGroup({
      comite: new FormControl(null, []),
      titulo: new FormControl('', []),
      codigo: new FormControl('', [])
    });

    this.suscripciones.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('eti-exp-max-num-registros-excel-peticion-evaluacion-listado').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  private setupI18N(): void {
    this.translate.get(
      PETICION_EVALUACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_FOOTER,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      PETICION_EVALUACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

    this.translate.get(
      PETICION_EVALUACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDeleteSuccess = value);
  }

  protected createObservable(): Observable<SgiRestListResult<IPeticionEvaluacionWithIsEliminable>> {
    return this.peticionesEvaluacionService.findAllPeticionEvaluacionMemoria(this.getFindOptions());
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    return new RSQLSgiRestFilter('comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
      .and('peticionEvaluacion.codigo', SgiRestFilterOperator.LIKE_ICASE, controls.codigo.value)
      .and('peticionEvaluacion.titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value);
  }

  protected loadTable(reset?: boolean) {
    this.peticionesEvaluacion$ = this.getObservableLoadTable(reset);
  }

  /**
   * Elimina la peticion de evaluación con el id recibido por parametro.
   * @param peticionEvaluacionId id de la petición de evaluación
   * @param event evento lanzado
   */
  borrar(peticionEvaluacionId: number, $event: Event): void {
    $event.stopPropagation();
    $event.preventDefault();

    const dialogServiceSubscriptionGetSubscription = this.dialogService.showConfirmation(this.textoDelete).subscribe(
      (aceptado: boolean) => {
        if (aceptado) {
          const peticionEvaluacionServiceDeleteSubscription = this.peticionesEvaluacionService
            .deleteById(peticionEvaluacionId)
            .pipe(
              map(() => {
                return this.loadTable();
              })
            ).subscribe(() => {
              this.snackBarService.showSuccess(this.textoDeleteSuccess);
            });
          this.suscripciones.push(peticionEvaluacionServiceDeleteSubscription);
        }
        aceptado = false;
      });
    this.suscripciones.push(dialogServiceSubscriptionGetSubscription);
  }

  public openExportModal() {
    const data: IBaseExportModalData = {
      findOptions: this.findOptions,
      totalRegistrosExportacionExcel: this.totalElementos,
      limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
    };

    const config = {
      data
    };
    this.matDialog.open(PeticionEvaluacionListadoExportModalComponent, config);
  }
}
