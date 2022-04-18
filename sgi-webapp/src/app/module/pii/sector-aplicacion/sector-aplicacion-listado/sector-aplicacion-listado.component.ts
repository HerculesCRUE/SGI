import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { ISectorAplicacion } from '@core/models/pii/sector-aplicacion';
import { DialogService } from '@core/services/dialog.service';
import { SectorAplicacionService } from '@core/services/pii/sector-aplicacion/sector-aplicacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestFilter, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { SectorAplicacionModalComponent } from '../sector-aplicacion-modal/sector-aplicacion-modal.component';

const MSG_ERROR = marker('error.load');
const MSG_SAVE_SUCCESS = marker('msg.save.entity.success');
const MSG_UPDATE_SUCCESS = marker('msg.update.entity.success');
const MSG_REACTIVE = marker('msg.reactivate.entity');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_ERROR_DEACTIVATE = marker('error.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.deactivate.entity.success');
const SECTOR_APLICACION_KEY = marker('pii.sector-aplicacion');

@Component({
  selector: 'sgi-sector-aplicacion-listado',
  templateUrl: './sector-aplicacion-listado.component.html',
  styleUrls: ['./sector-aplicacion-listado.component.scss']
})
export class SectorAplicacionListadoComponent extends AbstractTablePaginationComponent<ISectorAplicacion> implements OnInit {

  sectorAplicacion$: Observable<ISectorAplicacion[]>;

  msgParamEntity = {};

  textoCrearSuccess: string;
  textoUpdateSuccess: string;
  textoDesactivar: string;
  textoReactivar: string;
  textoErrorDesactivar: string;
  textoSuccessDesactivar: string;
  textoSuccessReactivar: string;
  textoErrorReactivar: string;

  constructor(
    private readonly logger: NGXLogger,
    private readonly sectorAplicacionService: SectorAplicacionService,
    protected readonly snackBarService: SnackBarService,
    private readonly matDialog: MatDialog,
    private readonly dialogService: DialogService,
    private readonly translate: TranslateService) {
    super(snackBarService, MSG_ERROR);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      SECTOR_APLICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      SECTOR_APLICACION_KEY,
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
      SECTOR_APLICACION_KEY,
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
      SECTOR_APLICACION_KEY,
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
      SECTOR_APLICACION_KEY,
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
      SECTOR_APLICACION_KEY,
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
      SECTOR_APLICACION_KEY,
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
      SECTOR_APLICACION_KEY,
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
      SECTOR_APLICACION_KEY,
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

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<ISectorAplicacion>> {
    const observable$ = this.sectorAplicacionService.findTodos(this.getFindOptions(reset));
    return observable$;
  }

  protected initColumns(): void {
    this.columnas = ['nombre', 'descripcion', 'activo', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.sectorAplicacion$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    return undefined;
  }

  /**
   * Abre un modal para añadir o actualizar un Sector de Aplicación
   *
   * @param sectorAplicacion Sector de Aplicación
   */
  openModal(sectorAplicacion?: ISectorAplicacion): void {
    const config: MatDialogConfig<ISectorAplicacion> = {
      data: sectorAplicacion
    };
    const dialogRef = this.matDialog.open(SectorAplicacionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: ISectorAplicacion) => {
        if (result) {
          this.snackBarService.showSuccess(sectorAplicacion ? this.textoUpdateSuccess : this.textoCrearSuccess);
          this.loadTable();
        }
      });
  }

  /**
   * Desactivar un registro de Sector de Aplicación
   * @param sectorAplicacion  Sector de Aplicación.
   */
  deactivateSectorAplicacion(sectorAplicacion: ISectorAplicacion): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.sectorAplicacionService.desactivar(sectorAplicacion.id);
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
          if (error instanceof SgiError) {
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
   * Activar un registro de Sector de Aplicación
   * @param sectorAplicacion  Sector de Aplicación.
   */
  activateSectorAplicacion(sectorAplicacion: ISectorAplicacion): void {
    const subcription = this.dialogService.showConfirmation(this.textoReactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.sectorAplicacionService.activar(sectorAplicacion.id);
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
          if (error instanceof SgiError) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(this.textoErrorReactivar);
          }
        }
      );
    this.suscripciones.push(subcription);
  }
}
