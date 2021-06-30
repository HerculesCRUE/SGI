import { Component, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { DialogService } from '@core/services/dialog.service';
import { TipoProteccionService } from '@core/services/pii/tipo-proteccion/tipo-proteccion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestFilter, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

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
const SECTOR_APLICACION_KEY = marker('pii.tipo-proteccion');

@Component({
  selector: 'sgi-pii-tipo-proteccion-listado',
  templateUrl: './pii-tipo-proteccion-listado.component.html',
  styleUrls: ['./pii-tipo-proteccion-listado.component.scss']
})
export class PiiTipoProteccionListadoComponent extends AbstractTablePaginationComponent<ITipoProteccion> implements OnInit {

  tipoProteccion$: Observable<ITipoProteccion[]>;
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
    private readonly tipoProteccionService: TipoProteccionService,
    protected readonly snackBarService: SnackBarService,
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
          MSG_SAVE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrearError = value);

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
          MSG_UPDATE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoUpdateError = value);

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

  protected createObservable(): Observable<SgiRestListResult<ITipoProteccion>> {
    const observable$ = this.tipoProteccionService.findTodos(this.getFindOptions());
    return observable$;
  }

  protected initColumns(): void {
    this.columnas = ['nombre', 'descripcion', 'tipoPropiedad', 'activo', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.tipoProteccion$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    throw new Error('Method not implemented.');
  }

  /**
   * Desactivar Tipo de Protección.
   * @param tipoProteccion Tipo de Protección.
   */
  deactivateTipoProteccion(tipoProteccion: ITipoProteccion): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.tipoProteccionService.desactivar(tipoProteccion.id);
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
   * Activar un registro de Tipo de Proteccion
   * @param tipoProteccion  Tipo de Proteccion
   */
  activateTipoProteccion(tipoProteccion: ITipoProteccion): void {
    const subcription = this.dialogService.showConfirmation(this.textoReactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.tipoProteccionService.activar(tipoProteccion.id);
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
          this.snackBarService.showError(this.textoErrorReactivar);
        }
      );
    this.suscripciones.push(subcription);
  }


}
