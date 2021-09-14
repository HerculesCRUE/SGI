import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { HttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { ITramoReparto } from '@core/models/pii/tramo-reparto';
import { DialogService } from '@core/services/dialog.service';
import { TramoRepartoService } from '@core/services/pii/tramo-reparto/tramo-reparto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult, SgiRestFilter } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { TramoRepartoModalComponent } from '../tramo-reparto-modal/tramo-reparto-modal.component';

const MSG_ERROR = marker('error.load');
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
const TRAMO_REPARTO_KEY = marker('pii.tramo-reparto');

@Component({
  selector: 'sgi-tramo-reparto-listado',
  templateUrl: './tramo-reparto-listado.component.html',
  styleUrls: ['./tramo-reparto-listado.component.scss']
})
export class TramoRepartoListadoComponent extends AbstractTablePaginationComponent<ITramoReparto> implements OnInit {

  tramoReparto$: Observable<ITramoReparto[]>;

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

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly translate: TranslateService,
    private readonly tramoRepartoService: TramoRepartoService,
    private readonly dialogService: DialogService,
    private readonly matDialog: MatDialog,
  ) {
    super(snackBarService, MSG_ERROR);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected createObservable(): Observable<SgiRestListResult<ITramoReparto>> {
    const observable$ = this.tramoRepartoService.findTodos(this.getFindOptions());
    return observable$;
  }

  protected initColumns(): void {
    this.columnas = ['desde', 'porcentajeUniversidad', 'porcentajeInventores', 'activo', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.tramoReparto$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    throw new Error('Method not implemented.');
  }

  /**
   * Abre un modal para añadir o actualizar un Tramo de Reparto
   *
   * @param tramoReparto Tramo de Reparto
   */
  openModal(tramoReparto?: ITramoReparto): void {
    const config = {
      panelClass: 'sgi-dialog-container',
      data: tramoReparto
    };
    const dialogRef = this.matDialog.open(TramoRepartoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: ITramoReparto) => {
        if (result) {
          const subscription = tramoReparto ? this.tramoRepartoService.update(tramoReparto.id, result) :
            this.tramoRepartoService.create(result);

          subscription.subscribe(
            () => {
              this.snackBarService.showSuccess(tramoReparto ? this.textoUpdateSuccess : this.textoCrearSuccess);
              this.loadTable();
            },
            (error) => {
              this.logger.error(error);
              if (error instanceof HttpProblem) {
                this.snackBarService.showError(error);
              }
              else {
                this.snackBarService.showError(tramoReparto ? this.textoUpdateError : this.textoCrearError);
              }
            }
          );
        }
      });
  }

  /**
   * Desactivar un registro de Tramo de Reparto
   * @param tramoReparto  Tramo de Reparto.
   */
  deactivateTramoReparto(tramoReparto: ITramoReparto): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.tramoRepartoService.desactivar(tramoReparto.id);
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
          if (error instanceof HttpProblem) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(this.textoErrorDesactivar);
          }
        }
      );
    this.suscripciones.push(subcription);
  }

  /**
   * Activar un registro de Tramo de Reparto
   * @param tramoReparto  Tramo de Reparto.
   */
  activateTramoReparto(tramoReparto: ITramoReparto): void {
    const subcription = this.dialogService.showConfirmation(this.textoReactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.tramoRepartoService.activar(tramoReparto.id);
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
          if (error instanceof HttpProblem) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(this.textoErrorReactivar);
          }
        }
      );
    this.suscripciones.push(subcription);
  }


  private setupI18N(): void {
    this.translate.get(
      TRAMO_REPARTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      TRAMO_REPARTO_KEY,
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
      TRAMO_REPARTO_KEY,
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
      TRAMO_REPARTO_KEY,
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
      TRAMO_REPARTO_KEY,
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
      TRAMO_REPARTO_KEY,
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
      TRAMO_REPARTO_KEY,
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
      TRAMO_REPARTO_KEY,
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
      TRAMO_REPARTO_KEY,
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
      TRAMO_REPARTO_KEY,
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
      TRAMO_REPARTO_KEY,
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
}
