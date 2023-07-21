import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ConfigService } from '@core/services/cnf/config.service';
import { DialogService } from '@core/services/dialog.service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { Observable, of, Subscription } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ConvocatoriaReunionListadoExportModalComponent } from '../modals/convocatoria-reunion-listado-export-modal/convocatoria-reunion-listado-export-modal.component';

const MSG_BUTTON_NEW = marker('btn.add.entity');
const MSG_CONFIRMATION_DELETE = marker('msg.delete.entity');
const MSG_SUCCESS_DELETE = marker('msg.delete.entity.success');
const CONVOCATORIA_REUNION_KEY = marker('eti.convocatoria-reunion');
const MSG_SUCCESS_ENVIADO = marker('msg.envio-comunicado.entity.success');
const MSG_ERROR_ENVIADO = marker('msg.envio-comunicado.entity.error');

@Component({
  selector: 'sgi-convocatoria-reunion-listado',
  templateUrl: './convocatoria-reunion-listado.component.html',
  styleUrls: ['./convocatoria-reunion-listado.component.scss']
})
export class ConvocatoriaReunionListadoComponent
  extends AbstractTablePaginationComponent<IConvocatoriaReunion> implements OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  FormGroupUtil = FormGroupUtil;

  displayedColumns: string[];
  totalElementos: number;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  textoCrear: string;
  textoDelete: string;
  textoDeleteSuccess: string;
  textoEnviadoSuccess: string;
  textoEnviadoError: string;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  convocatoriaReunion$: Observable<IConvocatoriaReunion[]> = of();
  private dialogSubscription: Subscription;
  private convocatoriaReunionDeleteSubscription: Subscription;

  mapEliminable: Map<number, boolean> = new Map();
  mapModificable: Map<number, boolean> = new Map();

  private limiteRegistrosExportacionExcel: string;

  constructor(
    private readonly convocatoriaReunionService: ConvocatoriaReunionService,
    private readonly dialogService: DialogService,
    protected readonly snackBarService: SnackBarService,
    private formBuilder: FormBuilder,
    private readonly translate: TranslateService,
    private matDialog: MatDialog,
    private readonly cnfService: ConfigService
  ) {
    super();

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(25%-10px)';
    this.fxFlexProperties.md = '0 1 calc(25%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(15%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.totalElementos = 0;
  }


  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IConvocatoriaReunion>> {
    return this.convocatoriaReunionService.findAll(this.getFindOptions(reset)).pipe(
      map(response => {
        const convocatorias = response.items;
        convocatorias.forEach(convocatoriaReunion => {
          this.suscripciones.push(this.convocatoriaReunionService.eliminable(convocatoriaReunion.id).subscribe((value) => {
            this.mapEliminable.set(convocatoriaReunion.id, value);
          }));
          this.suscripciones.push(this.convocatoriaReunionService.modificable(convocatoriaReunion.id).subscribe((value) => {
            this.mapModificable.set(convocatoriaReunion.id, value);
          }));
        });
        return response as SgiRestListResult<IConvocatoriaReunion>;
      })
    );
  }
  protected initColumns(): void {
    this.displayedColumns = ['comite', 'fechaEvaluacion', 'codigo', 'horaInicio', 'horaInicioSegunda', 'lugar', 'tipoConvocatoriaReunion', 'fechaEnvio', 'acciones'];
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
      .and('tipoConvocatoriaReunion.id', SgiRestFilterOperator.EQUALS, controls.tipoConvocatoriaReunion.value?.id?.toString())
      .and('fechaEvaluacion', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaEvaluacionDesde.value))
      .and('fechaEvaluacion', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaEvaluacionHasta.value));

    return filter;
  }

  protected resetFilters(): void {
    super.resetFilters();
    this.formGroup.controls.fechaEvaluacionDesde.setValue(null);
    this.formGroup.controls.fechaEvaluacionHasta.setValue(null);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    // Inicializa el formulario de busqueda
    this.formGroup = this.formBuilder.group({
      comite: new FormControl(null, []),
      tipoConvocatoriaReunion: new FormControl(null, []),
      fechaEvaluacionDesde: new FormControl(null, []),
      fechaEvaluacionHasta: new FormControl(null, [])
    });

    this.suscripciones.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('eti-exp-max-num-registros-excel-convocatoria-reunion-listado').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_REUNION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_NEW,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      CONVOCATORIA_REUNION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_CONFIRMATION_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

    this.translate.get(
      CONVOCATORIA_REUNION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDeleteSuccess = value);

    this.translate.get(
      CONVOCATORIA_REUNION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_ENVIADO,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoEnviadoSuccess = value);

    this.translate.get(
      CONVOCATORIA_REUNION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_ENVIADO,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoEnviadoError = value);
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
    this.dialogSubscription?.unsubscribe();
    this.convocatoriaReunionDeleteSubscription?.unsubscribe();
  }

  protected loadTable(reset?: boolean) {
    this.convocatoriaReunion$ = this.getObservableLoadTable(reset);
  }

  /**
   * Elimina la convocatoria reunion.
   * @param convocatoriaReunionId id de la convocatoria reunion a eliminar.
   * @param event evento lanzado.
   */
  borrar(convocatoriaReunionId: number, $event: Event): void {
    $event.stopPropagation();
    $event.preventDefault();

    this.dialogSubscription = this.dialogService.showConfirmation(
      this.textoDelete
    ).subscribe(
      (aceptado) => {
        if (aceptado) {
          this.convocatoriaReunionDeleteSubscription = this.convocatoriaReunionService
            .deleteById(convocatoriaReunionId).pipe(
              map(() => {
                return this.loadTable();
              })
            ).subscribe(() => {
              this.snackBarService.showSuccess(this.textoDeleteSuccess);
            },
              this.processError
            );
        }
        aceptado = false;
      });
  }

  /**
   * Envia la convocatoria reunion.
   * @param convocatoriaReunionId id de la convocatoria reunion a enviar.
   * @param event evento lanzado.
   */
  enviar(convocatoriaReunionId: number, $event: Event): void {
    this.convocatoriaReunionService.enviarComunicado(convocatoriaReunionId).subscribe(
      (response) => {
        if (response) {
          this.snackBarService.showSuccess(this.textoEnviadoSuccess);
          this.loadTable();
        } else {
          this.processError(new SgiError(this.textoEnviadoError));
        }
      },
      (error) => {
        if (error instanceof SgiError) {
          this.processError(error);
        }
        else {
          this.processError(new SgiError(this.textoEnviadoError));
        }
      }
    );
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
    this.matDialog.open(ConvocatoriaReunionListadoExportModalComponent, config);
  }
}
