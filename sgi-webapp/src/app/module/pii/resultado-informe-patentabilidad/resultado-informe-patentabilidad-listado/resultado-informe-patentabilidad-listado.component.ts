import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { MSG_PARAMS } from '@core/i18n';
import { IResultadoInformePatentibilidad } from '@core/models/pii/resultado-informe-patentabilidad';
import { DialogService } from '@core/services/dialog.service';
import { ResultadoInformePatentabilidadService } from '@core/services/pii/resultado-informe-patentabilidad/resultado-informe-patentabilidad.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { SgiRestFilter, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const RESULTADO_INFORME_PATENTABILIDAD_KEY = marker('pii.resultado-informe-patentabilidad');
const MSG_ERROR = marker('error.load');
const MSG_SAVE_SUCCESS = marker('msg.save.entity.success');
const MSG_SAVE_ERROR = marker('error.save.entity');
const MSG_UPDATE_ERROR = marker('error.update.entity');
const MSG_UPDATE_SUCCESS = marker('msg.update.entity.success');
const MSG_REACTIVE = marker('msg.csp.reactivate');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_ERROR_DEACTIVATE = marker('error.csp.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.csp.deactivate.success');
const FUENTE_FINANCIACION_KEY = marker('csp.fuente-financiacion');


@Component({
  selector: 'sgi-resultado-informe-patentabilidad-listado',
  templateUrl: './resultado-informe-patentabilidad-listado.component.html',
  styleUrls: ['./resultado-informe-patentabilidad-listado.component.scss']
})
export class ResultadoInformePatentabilidadListadoComponent
  extends AbstractTablePaginationComponent<IResultadoInformePatentibilidad> implements OnInit {
  resultadoInformePatentabilidad$: Observable<IResultadoInformePatentibilidad[]>;
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
    private matDialog: MatDialog,
    private readonly dialogService: DialogService,
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly resultadoInformePatentabilidadService: ResultadoInformePatentabilidadService,
    public authService: SgiAuthService,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, MSG_ERROR);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }


  /**
   * Desactivar un registro de Resultado de Informe de Patentabilidad
   * @param resultadoInformePatentabilidad  Resultado de Informe de Patentabilidad
   */
  deactivateResultadoInformePatentabilidad(resultadoInformePatentabilidad: IResultadoInformePatentibilidad): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.resultadoInformePatentabilidadService.desactivar(resultadoInformePatentabilidad.id);
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
 * Activar un registro de Resultado de Informe de Patentabilidad
 * @param resultadoInformePatentabilidad  Resultado de Informe de Patentabilidad
 */
  activateResultadoInformePatentabilidad(resultadoInformePatentabilidad: IResultadoInformePatentibilidad): void {
    const subcription = this.dialogService.showConfirmation(this.textoReactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.resultadoInformePatentabilidadService.activar(resultadoInformePatentabilidad.id);
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


  private setupI18N(): void {
    this.translate.get(
      RESULTADO_INFORME_PATENTABILIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      RESULTADO_INFORME_PATENTABILIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoCrearSuccess = value);

    this.translate.get(
      RESULTADO_INFORME_PATENTABILIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoCrearError = value);

    this.translate.get(
      RESULTADO_INFORME_PATENTABILIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoUpdateSuccess = value);

    this.translate.get(
      RESULTADO_INFORME_PATENTABILIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoUpdateError = value);

    this.translate.get(
      RESULTADO_INFORME_PATENTABILIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDesactivar = value);

    this.translate.get(
      RESULTADO_INFORME_PATENTABILIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoErrorDesactivar = value);

    this.translate.get(
      RESULTADO_INFORME_PATENTABILIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoSuccessDesactivar = value);

    this.translate.get(
      RESULTADO_INFORME_PATENTABILIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoReactivar = value);

    this.translate.get(
      RESULTADO_INFORME_PATENTABILIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoSuccessReactivar = value);

    this.translate.get(
      RESULTADO_INFORME_PATENTABILIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoErrorReactivar = value);




  }

  protected createFilter(): SgiRestFilter {
    throw new Error('Method not implemented.');
  }

  protected createObservable(): Observable<SgiRestListResult<IResultadoInformePatentibilidad>> {
    const observable$ = this.resultadoInformePatentabilidadService.findTodos(this.getFindOptions());
    return observable$;
  }
  protected initColumns(): void {
    this.columnas = ['nombre', 'descripcion', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.resultadoInformePatentabilidad$ = this.getObservableLoadTable(reset);
  }

}
