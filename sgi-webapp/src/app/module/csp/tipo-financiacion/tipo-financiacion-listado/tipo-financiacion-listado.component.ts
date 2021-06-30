import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoFinanciacion } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TipoFinanciacionService } from '@core/services/csp/tipo-financiacion.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { TipoFinanciacionModalComponent } from '../tipo-financiacion-modal/tipo-financiacion-modal.component';

const MSG_ERROR = marker('error.load');
const MSG_SAVE_ERROR = marker('error.save.entity');
const MSG_UPDATE_ERROR = marker('error.update.entity');
const MSG_SAVE_SUCCESS = marker('msg.save.entity.success');
const MSG_UPDATE_SUCCESS = marker('msg.update.entity.success');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.csp.deactivate.success');
const MSG_ERROR_DEACTIVATE = marker('error.csp.deactivate.entity');
const MSG_REACTIVE = marker('msg.csp.reactivate');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const TIPO_FINANCIACION_KEY = marker('csp.tipo-financiacion');

@Component({
  selector: 'sgi-tipo-financiacion-listado',
  templateUrl: './tipo-financiacion-listado.component.html',
  styleUrls: ['./tipo-financiacion-listado.component.scss']
})
export class TipoFinanciacionListadoComponent extends AbstractTablePaginationComponent<ITipoFinanciacion> implements OnInit {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  tipoFinanciaciones$: Observable<ITipoFinanciacion[]>;

  msgParamEntity = {};

  textoCrearSuccess: string;
  textoCrearError: string;
  textoUpdateSuccess: string;
  textoUpdateError: string;
  textoDesactivar: string;
  textoReactivar: string;
  textoErrorDesactivar: string;
  textoSuccessDesactivar: string;
  textoSuccessReactivar: string;
  textoErrorReactivar: string;

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly tipoFinanciacionService: TipoFinanciacionService,
    private matDialog: MatDialog,
    private readonly dialogService: DialogService,
    private readonly translate: TranslateService,
    private authService: SgiAuthService
  ) {
    super(snackBarService, MSG_ERROR);
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

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.formGroup = new FormGroup({
      activo: new FormControl('true')
    });
    this.filter = this.createFilter();
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      TIPO_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrearSuccess = value);

    this.translate.get(
      TIPO_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrearError = value);

    this.translate.get(
      TIPO_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoUpdateSuccess = value);

    this.translate.get(
      TIPO_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoUpdateError = value);

    this.translate.get(
      TIPO_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDesactivar = value);

    this.translate.get(
      TIPO_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorDesactivar = value);

    this.translate.get(
      TIPO_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoSuccessDesactivar = value);

    this.translate.get(
      TIPO_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoReactivar = value);

    this.translate.get(
      TIPO_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoSuccessReactivar = value);

    this.translate.get(
      TIPO_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorReactivar = value);

  }

  protected createObservable(): Observable<SgiRestListResult<ITipoFinanciacion>> {
    const observable$ = this.tipoFinanciacionService.findTodos(this.getFindOptions());
    return observable$;
  }

  protected initColumns(): void {
    if (this.authService.hasAuthority('CSP-TFNA-R')) {
      this.columnas = ['nombre', 'descripcion', 'activo', 'acciones'];
    } else {
      this.columnas = ['nombre', 'descripcion', 'acciones'];
    }
  }

  protected loadTable(reset?: boolean): void {
    this.tipoFinanciaciones$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    if (controls.activo.value !== 'todos') {
      return new RSQLSgiRestFilter('activo', SgiRestFilterOperator.EQUALS, controls.activo.value);
    }

    return undefined;
  }

  onClearFilters() {
    this.formGroup.controls.activo.setValue('true');
    this.onSearch();
  }

  /**
   * Abre un modal para añadir o actualizar un tipo de financiacion
   *
   * @param tipoFinanciacion Tipo de financiacion
   */
  openModal(tipoFinanciacion?: ITipoFinanciacion): void {
    const config = {
      panelClass: 'sgi-dialog-container',
      data: tipoFinanciacion ? Object.assign({}, tipoFinanciacion) : { activo: true } as ITipoFinanciacion
    };
    const dialogRef = this.matDialog.open(TipoFinanciacionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: ITipoFinanciacion) => {
        if (result) {
          const subscription = tipoFinanciacion ? this.tipoFinanciacionService.update(tipoFinanciacion.id, result) :
            this.tipoFinanciacionService.create(result);

          subscription.subscribe(
            () => {
              this.snackBarService.showSuccess(tipoFinanciacion ? this.textoUpdateSuccess : this.textoCrearSuccess);
              this.loadTable();
            },
            (error) => {
              this.logger.error(error);
              this.snackBarService.showError(tipoFinanciacion ? this.textoUpdateError : this.textoCrearError);
            }
          );
        }
      }
    );
  }

  /**
   * Desactivar tipo financiacion
   * @param tipoFinanciacion tipo financiacion
   */
  deactivateTipoFinanciacion(tipoFinanciacion: ITipoFinanciacion): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.tipoFinanciacionService.desactivar(tipoFinanciacion.id);
        } else {
          return of();
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(this.textoSuccessDesactivar);
          this.loadTable();
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(this.textoErrorDesactivar);
        }
      );
    this.suscripciones.push(subcription);
  }

  /**
   * Activamos un tipo financiacion desactivado
   * @param tipoFinanciacion tipo financiacion
   */
  activateTipoFinanciacion(tipoFinanciacion: ITipoFinanciacion): void {
    const subcription = this.dialogService.showConfirmation(this.textoReactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          tipoFinanciacion.activo = true;
          return this.tipoFinanciacionService.reactivar(tipoFinanciacion.id);
        } else {
          return of();
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(this.textoSuccessReactivar);
          this.loadTable();
        },
        (error) => {
          this.logger.error(error);
          tipoFinanciacion.activo = false;
          this.snackBarService.showError(this.textoErrorReactivar);
        }
      );
    this.suscripciones.push(subcription);
  }

}
