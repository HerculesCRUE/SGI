import { Component, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IViaProteccion } from '@core/models/pii/via-proteccion';
import { SgiRestListResult, SgiRestFilter } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { ROUTE_NAMES } from '@core/route.names';
import { MSG_PARAMS } from '@core/i18n';
import { switchMap } from 'rxjs/operators';
import { NGXLogger } from 'ngx-logger';
import { MatDialog } from '@angular/material/dialog';
import { DialogService } from '@core/services/dialog.service';
import { ViaProteccionService } from '@core/services/pii/via-proteccion/via-proteccion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { HttpProblem } from '@core/errors/http-problem';
import { ViaProteccionModalComponent } from '../via-proteccion-modal/via-proteccion-modal.component';

const MSG_ERROR = marker('error.load');
const MSG_CREATE = marker('btn.add.entity');
const MSG_SAVE_SUCCESS = marker('msg.save.entity.success');
const MSG_SAVE_ERROR = marker('error.save.entity');
const MSG_UPDATE_ERROR = marker('error.update.entity');
const MSG_UPDATE_SUCCESS = marker('msg.update.entity.success');
const MSG_REACTIVE = marker('msg.reactivate.entity');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_ERROR_DEACTIVATE = marker('error.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.deactivate.entity.success');
const VIA_PROTECCION_KEY = marker('pii.via-proteccion');

@Component({
  selector: 'sgi-via-proteccion-listado',
  templateUrl: './via-proteccion-listado.component.html',
  styleUrls: ['./via-proteccion-listado.component.scss']
})
export class ViaProteccionListadoComponent extends AbstractTablePaginationComponent<IViaProteccion> implements OnInit {

  ROUTE_NAMES = ROUTE_NAMES;
  viasProteccion$: Observable<IViaProteccion[]>;
  msgParamEntity = {};

  textCrear: string;
  textCrearSuccess: string;
  textCrearError: string;
  textUpdateSuccess: string;
  textUpdateError: string;
  textDesactivar: string;
  textReactivar: string;
  textErrorDesactivar: string;
  textSuccessDesactivar: string;
  textSuccessReactivar: string;
  textErrorReactivar: string;

  constructor(
    private logger: NGXLogger,
    private viaProteccionService: ViaProteccionService,
    protected snackBarService: SnackBarService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private translate: TranslateService
  ) {
    super(snackBarService, MSG_ERROR);
  }

  protected createObservable(): Observable<SgiRestListResult<IViaProteccion>> {
    return this.viaProteccionService.findTodos(this.getFindOptions());
  }

  protected initColumns(): void {
    this.columnas = ['nombre', 'descripcion', 'paisEspecifico', 'extensionInternacional', 'variosPaises', 'activo', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.viasProteccion$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    throw new Error('Method not implemented.');
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {

    this.translate.get(
      VIA_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value: string) => this.msgParamEntity = { entity: value });

    this.translate.get(
      VIA_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_CREATE,
          { entity: value, ...this.msgParamEntity }
        );
      })
    ).subscribe((value: string) => this.textCrear = value);

    this.translate.get(
      VIA_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value: string) => this.textCrearSuccess = value);

    this.translate.get(
      VIA_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value: string) => this.textCrearError = value);

    this.translate.get(
      VIA_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value: string) => this.textUpdateSuccess = value);

    this.translate.get(
      VIA_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value: string) => this.textUpdateError = value);

    this.translate.get(
      VIA_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value: string) => this.textDesactivar = value);

    this.translate.get(
      VIA_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value: string) => this.textErrorDesactivar = value);

    this.translate.get(
      VIA_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value: string) => this.textSuccessDesactivar = value);

    this.translate.get(
      VIA_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value: string) => this.textReactivar = value);

    this.translate.get(
      VIA_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value: string) => this.textSuccessReactivar = value);

    this.translate.get(
      VIA_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value: string) => this.textErrorReactivar = value);

  }

  /**
   * Desactivar Via de Proteccion.
   * @param viaProteccion Via de Proteccion
   */
  deactivateViaProteccion(viaProteccion: IViaProteccion): void {
    const subcription = this.dialogService.showConfirmation(this.textDesactivar).pipe(
      switchMap((accept) => accept ? this.viaProteccionService.desactivar(viaProteccion.id)
        : of())).subscribe(() => {
          this.snackBarService.showSuccess(this.textSuccessDesactivar);
          this.loadTable();
        }, (error) => {
          this.logger.error(error);
          if (error instanceof HttpProblem) {
            this.snackBarService.showError(error);
          } else {
            this.snackBarService.showError(this.textErrorDesactivar);
          }
        }
        );
    this.suscripciones.push(subcription);
  }

  /**
   * Activar un registro de Via de Proteccion
   * @param viaProteccion  Via de Proteccion
   */
  activateViaProteccion(viaProteccion: IViaProteccion): void {
    const subcription = this.dialogService.showConfirmation(this.textReactivar)
      .pipe(switchMap((accept) => {
        return accept ? this.viaProteccionService.activar(viaProteccion.id)
          : of();
      })).subscribe(() => {
        this.snackBarService.showSuccess(this.textSuccessReactivar);
        this.loadTable();
      }, (error) => {
        this.logger.error(error);
        if (error instanceof HttpProblem) {
          this.snackBarService.showError(error);
        } else {
          this.snackBarService.showError(this.textErrorReactivar);
        }
      }
      );
    this.suscripciones.push(subcription);
  }

  /**
   * Abre un modal para añadir o actualizar una Via de Proteccion
   * @param viaProteccion Via de Proteccion
   */
  openModal(viaProteccion?: IViaProteccion): void {
    const config = {
      panelClass: 'sgi-dialog-container',
      data: viaProteccion
    };
    const dialogRef = this.matDialog.open(ViaProteccionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: IViaProteccion) => {
        if (result) {
          const subscription = viaProteccion ? this.viaProteccionService.update(viaProteccion.id, result) :
            this.viaProteccionService.create(result);

          subscription.subscribe(() => {
            this.snackBarService.showSuccess(viaProteccion ? this.textUpdateSuccess : this.textCrearSuccess);
            this.loadTable();
          }, (error) => {
            this.logger.error(error);
            if (error instanceof HttpProblem) {
              this.snackBarService.showError(error);
            } else {
              this.snackBarService.showError(viaProteccion ? this.textUpdateError : this.textCrearError);
            }
          });
        }
      });
  }

}
