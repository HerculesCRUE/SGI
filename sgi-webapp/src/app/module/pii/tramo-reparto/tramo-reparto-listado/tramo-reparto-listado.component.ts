import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { HttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { ITramoReparto, Tipo } from '@core/models/pii/tramo-reparto';
import { DialogService } from '@core/services/dialog.service';
import { TramoRepartoService } from '@core/services/pii/tramo-reparto/tramo-reparto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult, SgiRestFilter, SgiRestFilterOperator, RSQLSgiRestFilter, SgiRestFindOptions } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ITramoRepartoModalData, TramoRepartoModalComponent } from '../tramo-reparto-modal/tramo-reparto-modal.component';

const MSG_ERROR = marker('error.load');
const MSG_SAVE_SUCCESS = marker('msg.save.entity.success');
const MSG_UPDATE_SUCCESS = marker('msg.update.entity.success');
const MSG_DELETE = marker('msg.delete.entity');
const MSG_ERROR_DELETE = marker('error.delete.entity');
const MSG_SUCCESS_DELETE = marker('msg.delete.entity.success');
const TRAMO_REPARTO_KEY = marker('pii.tramo-reparto');
const MSG_ERROR_TRAMO_NO_DELETABLE = marker('pii.tramo-reparto.no-deletable');

@Component({
  selector: 'sgi-tramo-reparto-listado',
  templateUrl: './tramo-reparto-listado.component.html',
  styleUrls: ['./tramo-reparto-listado.component.scss']
})
export class TramoRepartoListadoComponent extends AbstractTablePaginationComponent<ITramoReparto> implements OnInit {

  tramoReparto$: Observable<ITramoReparto[]>;

  msgParamEntity = {};

  textoCrearSuccess: string;
  textoUpdateSuccess: string;
  textoEliminar: string;
  textoErrorEliminar: string;
  textoSuccessEliminar: string;

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

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<ITramoReparto>> {
    const observable$ = this.tramoRepartoService.findAll(this.getFindOptions(reset));
    return observable$;
  }

  protected initColumns(): void {
    this.columnas = ['desde', 'porcentajeUniversidad', 'porcentajeInventores', 'acciones'];
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
    this.createPreConditionsObservable(tramoReparto).subscribe((response) => this.createModal(response));
  }

  private createPreConditionsObservable(tramoReparto: ITramoReparto): Observable<ITramoRepartoModalData> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('maxHasta', SgiRestFilterOperator.EQUALS, tramoReparto?.id?.toString() ?? 'max')
    };
    if (tramoReparto?.id) {
      return forkJoin({
        currentTramoReparto: of(tramoReparto),
        tramoRepartoMaxHasta: this.tramoRepartoService.findAll(options).pipe(map(({ items }) => items[0])),
        hasTramoRepartoInicial: this.tramoRepartoService.existTipoTramoReparto(Tipo.INICIAL),
        hasTramoRepartoFinal: this.tramoRepartoService.existTipoTramoReparto(Tipo.FINAL),
        isTramoRepartoModificable: this.tramoRepartoService.isTipoTramoRepartoModificable(tramoReparto.id)
      });
    } else {
      return forkJoin({
        currentTramoReparto: of(tramoReparto),
        tramoRepartoMaxHasta: this.tramoRepartoService.findAll(options).pipe(map(({ items }) => items[0])),
        hasTramoRepartoInicial: this.tramoRepartoService.existTipoTramoReparto(Tipo.INICIAL),
        hasTramoRepartoFinal: this.tramoRepartoService.existTipoTramoReparto(Tipo.FINAL)
      });
    }
  }

  private createModal(data: ITramoRepartoModalData): void {
    const config: MatDialogConfig<ITramoRepartoModalData> = {
      data
    };
    const dialogRef = this.matDialog.open(TramoRepartoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: ITramoReparto) => {
        if (result) {
          this.snackBarService.showSuccess(data ? this.textoUpdateSuccess : this.textoCrearSuccess);
          this.loadTable();
        }
      });
  }

  /**
   * Eliminar un registro de Tramo de Reparto
   * @param tramoReparto  Tramo de Reparto.
   */
  deleteTramoReparto(tramoReparto: ITramoReparto): void {
    this.tramoRepartoService.isTipoTramoRepartoModificable(tramoReparto.id).subscribe(isModificable => {
      if (isModificable) {
        this.openConfirmDeleteModal(tramoReparto);
      } else {
        this.snackBarService.showError(MSG_ERROR_TRAMO_NO_DELETABLE);
      }
    });
  }

  private openConfirmDeleteModal(tramoReparto: ITramoReparto): void {
    const subcription = this.dialogService.showConfirmation(this.textoEliminar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.tramoRepartoService.delete(tramoReparto.id);
        } else {
          return of();
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(this.textoSuccessEliminar);
          this.loadTable();
        },
        (error) => {
          this.logger.error(error);
          if (error instanceof HttpProblem) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(this.textoErrorEliminar);
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
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoEliminar = value);

    this.translate.get(
      TRAMO_REPARTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorEliminar = value);

    this.translate.get(
      TRAMO_REPARTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoSuccessEliminar = value);
  }
}
