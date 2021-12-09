import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { HttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoProcedimiento } from '@core/models/pii/tipo-procedimiento';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { TipoProcedimientoService } from '@core/services/pii/tipo-procedimiento/tipo-procedimiento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult, SgiRestFilter } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { TipoProcedimientoModalComponent } from '../tipo-procedimiento-modal/tipo-procedimiento-modal.component';

const MSG_ERROR = marker('error.load');
const MSG_CREATE = marker('btn.add.entity');
const MSG_SAVE_SUCCESS = marker('msg.save.entity.success');
const MSG_UPDATE_SUCCESS = marker('msg.update.entity.success');
const MSG_REACTIVE = marker('msg.reactivate.entity');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_ERROR_DEACTIVATE = marker('error.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.deactivate.entity.success');
const TIPO_PROCEDIMIENTO_KEY = marker('pii.tipo-procedimiento');

@Component({
  selector: 'sgi-tipo-procedimiento-listado',
  templateUrl: './tipo-procedimiento-listado.component.html',
  styleUrls: ['./tipo-procedimiento-listado.component.scss']
})
export class TipoProcedimientoListadoComponent extends AbstractTablePaginationComponent<ITipoProcedimiento> implements OnInit {

  ROUTE_NAMES = ROUTE_NAMES;
  tipoProcedimiento$: Observable<ITipoProcedimiento[]>;

  msgParamEntity = {};

  textoCrear: string;
  textoCrearSuccess: string;
  textoUpdateSuccess: string;
  textoDesactivar: string;
  textoReactivar: string;
  textoErrorDesactivar: string;
  textoSuccessDesactivar: string;
  textoSuccessReactivar: string;
  textoErrorReactivar: string;

  constructor(
    private logger: NGXLogger,
    private tipoProcedimientoService: TipoProcedimientoService,
    protected snackBarService: SnackBarService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private translate: TranslateService
  ) {
    super(snackBarService, MSG_ERROR);
  }

  private setupI18N(): void {

    this.translate.get(
      TIPO_PROCEDIMIENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      TIPO_PROCEDIMIENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_CREATE,
          { entity: value, ...this.msgParamEntity }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      TIPO_PROCEDIMIENTO_KEY,
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
      TIPO_PROCEDIMIENTO_KEY,
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
      TIPO_PROCEDIMIENTO_KEY,
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
      TIPO_PROCEDIMIENTO_KEY,
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
      TIPO_PROCEDIMIENTO_KEY,
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
      TIPO_PROCEDIMIENTO_KEY,
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
      TIPO_PROCEDIMIENTO_KEY,
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
      TIPO_PROCEDIMIENTO_KEY,
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

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<ITipoProcedimiento>> {
    const observable$ = this.tipoProcedimientoService.findTodos(this.getFindOptions(reset));
    return observable$;
  }
  protected initColumns(): void {
    this.columnas = ['nombre', 'descripcion', 'activo', 'acciones'];
  }
  protected loadTable(reset?: boolean): void {
    this.tipoProcedimiento$ = this.getObservableLoadTable(reset);
  }
  protected createFilter(): SgiRestFilter {
    throw new Error('Method not implemented.');
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  /**
   * Desactivar Tipo de Procedimiento.
   * @param tipoProcedimienton Tipo de Procedimiento.
   */
  deactivateTipoProcedimiento(tipoProcedimiento: ITipoProcedimiento): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar)
      .pipe(switchMap((accept) => accept ? this.tipoProcedimientoService.desactivar(tipoProcedimiento.id)
        : of())).subscribe(() => {
          this.snackBarService.showSuccess(this.textoSuccessDesactivar);
          this.loadTable();
        }, (error) => {
          this.logger.error(error);
          if (error instanceof HttpProblem) {
            this.snackBarService.showError(error);
          } else {
            this.snackBarService.showError(this.textoErrorDesactivar);
          }
        }
        );
    this.suscripciones.push(subcription);
  }

  /**
   * Activar un registro de Tipo de Procedimiento
   * @param tipoProcedimiento  Tipo de Procedimiento
   */
  activateTipoProcedimiento(tipoProcedimiento: ITipoProcedimiento): void {
    const subcription = this.dialogService.showConfirmation(this.textoReactivar)
      .pipe(switchMap((accept) => {
        return accept ? this.tipoProcedimientoService.activar(tipoProcedimiento.id)
          : of();
      })).subscribe(() => {
        this.snackBarService.showSuccess(this.textoSuccessReactivar);
        this.loadTable();
      }, (error) => {
        this.logger.error(error);
        if (error instanceof HttpProblem) {
          this.snackBarService.showError(error);
        } else {
          this.snackBarService.showError(this.textoErrorReactivar);
        }
      }
      );
    this.suscripciones.push(subcription);
  }


  /**
   * Abre un modal para añadir o actualizar un Tipo de Procedimiento
   * @param tipoProcedimiento Tipo de Protección
   */
  openModal(tipoProcedimiento?: ITipoProcedimiento): void {
    const config: MatDialogConfig<ITipoProcedimiento> = {
      data: tipoProcedimiento
    };
    const dialogRef = this.matDialog.open(TipoProcedimientoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: ITipoProcedimiento) => {
        if (result) {
          this.snackBarService.showSuccess(tipoProcedimiento ? this.textoUpdateSuccess : this.textoCrearSuccess);
          this.loadTable();
        }
      });
  }
}
