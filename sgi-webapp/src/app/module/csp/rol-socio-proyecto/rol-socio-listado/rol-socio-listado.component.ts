import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IRolSocio } from '@core/models/csp/rol-socio';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { RolSocioService } from '@core/services/csp/rol-socio/rol-socio.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { RolSocioModalComponent } from '../rol-socio-modal/rol-socio-modal.component';

const MSG_SAVE_SUCCESS = marker('msg.save.entity.success');
const MSG_UPDATE_SUCCESS = marker('msg.update.entity.success');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.csp.deactivate.success');
const MSG_ERROR_DEACTIVATE = marker('error.csp.deactivate.entity');
const MSG_REACTIVE = marker('msg.csp.reactivate');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const ROL_SOCIO_PROYECTO_KEY = marker('csp.rol-socio');

@Component({
  selector: 'sgi-rol-socio-listado',
  templateUrl: './rol-socio-listado.component.html',
  styleUrls: ['./rol-socio-listado.component.scss']
})
export class RolSocioListadoComponent extends AbstractTablePaginationComponent<IRolSocio> implements OnInit {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  rolesSocioProyecto$: Observable<IRolSocio[]>;

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
    protected readonly snackBarService: SnackBarService,
    private readonly rolSocioProyectoService: RolSocioService,
    private matDialog: MatDialog,
    private readonly dialogService: DialogService,
    private readonly translate: TranslateService,
    private readonly authService: SgiAuthService
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

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.formGroup = new FormGroup({
      nombre: new FormControl(''),
      abreviatura: new FormControl(''),
      coordinador: new FormControl(''),
      activo: new FormControl('true')
    });
    this.filter = this.createFilter();
  }

  private setupI18N(): void {
    this.translate.get(
      ROL_SOCIO_PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      ROL_SOCIO_PROYECTO_KEY,
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
      ROL_SOCIO_PROYECTO_KEY,
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
      ROL_SOCIO_PROYECTO_KEY,
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
      ROL_SOCIO_PROYECTO_KEY,
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
      ROL_SOCIO_PROYECTO_KEY,
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
      ROL_SOCIO_PROYECTO_KEY,
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
      ROL_SOCIO_PROYECTO_KEY,
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
      ROL_SOCIO_PROYECTO_KEY,
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

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IRolSocio>> {
    return this.rolSocioProyectoService.findTodos(this.getFindOptions(reset));
  }

  protected initColumns(): void {
    if (this.authService.hasAuthority('CSP-ROLS-R')) {
      this.columnas = ['nombre', 'abreviatura', 'coordinador', 'activo', 'acciones'];
    } else {
      this.columnas = ['nombre', 'acciones'];
    }
  }

  protected loadTable(reset?: boolean): void {
    this.rolesSocioProyecto$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('nombre', SgiRestFilterOperator.LIKE_ICASE, controls.nombre.value);
    if (controls.activo.value !== 'todos') {
      filter.and('activo', SgiRestFilterOperator.EQUALS, controls.activo.value);
    }

    return filter;
  }

  protected resetFilters(): void {
    this.formGroup.controls.activo.setValue('true');
    this.formGroup.controls.nombre.setValue('');
  }

  /**
   * Abre un modal para añadir o actualizar un rol de socio de proyecto
   *
   * @param rolSocioProyecto rol de socio de proyecto
   */
  openModal(rolSocioProyecto?: IRolSocio): void {
    const config: MatDialogConfig<IRolSocio> = {
      data: rolSocioProyecto ? Object.assign({}, rolSocioProyecto) : { activo: true } as IRolSocio
    };
    const dialogRef = this.matDialog.open(RolSocioModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: IRolSocio) => {
        if (result) {
          this.snackBarService.showSuccess(rolSocioProyecto ? this.textoUpdateSuccess : this.textoCrearSuccess);
          this.loadTable();
        }
      }
    );
  }

  /**
   * Desactivamos registro rol de socio de proyecto
   * @param rolSocioProyecto rol de socio de proyecto
   */
  deactivateRolSocio(rolSocioProyecto: IRolSocio): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.rolSocioProyectoService.desactivar(rolSocioProyecto.id);
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
            this.processError(error);
          }
          else {
            this.processError(new SgiError(this.textoErrorDesactivar));
          }
        }
      );
    this.suscripciones.push(subcription);
  }


  /**
   * Activamos un rol de socio de proyecto
   * @param rolSocioProyecto rol de socio de proyecto
   */
  activateRolSocio(rolSocioProyecto: IRolSocio): void {
    const subcription = this.dialogService.showConfirmation(this.textoReactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          rolSocioProyecto.activo = true;
          return this.rolSocioProyectoService.activar(rolSocioProyecto.id);
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
          rolSocioProyecto.activo = false;
          if (error instanceof SgiError) {
            this.processError(error);
          }
          else {
            this.processError(new SgiError(this.textoErrorReactivar));
          }
        }
      );
    this.suscripciones.push(subcription);
  }

}

