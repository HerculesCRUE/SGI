import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { EQUIPO_MAP, IRolProyecto } from '@core/models/csp/rol-proyecto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { RolProyectoService } from '@core/services/csp/rol-proyecto/rol-proyecto.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MSG_SAVE_SUCCESS = marker('msg.save.entity.success');
const MSG_UPDATE_SUCCESS = marker('msg.update.entity.success');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.csp.deactivate.success');
const MSG_ERROR_DEACTIVATE = marker('error.csp.deactivate.entity');
const MSG_REACTIVE = marker('msg.csp.reactivate');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const ROL_EQUIPO_KEY = marker('csp.rol-equipo');
const MSG_BUTTON_NEW = marker('btn.add.entity');

@Component({
  selector: 'sgi-rol-equipo-listado',
  templateUrl: './rol-equipo-listado.component.html',
  styleUrls: ['./rol-equipo-listado.component.scss']
})
export class RolEquipoListadoComponent extends AbstractTablePaginationComponent<IRolProyecto> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  rolEquipo$: Observable<IRolProyecto[]>;

  msgParamEntity = {};

  textoCrearSuccess: string;
  textoUpdateSuccess: string;
  textoDesactivar: string;
  textoReactivar: string;
  textoErrorDesactivar: string;
  textoSuccessDesactivar: string;
  textoSuccessReactivar: string;
  textoErrorReactivar: string;
  textoCrear: string;

  showWarning = false;

  get EQUIPO_MAP() {
    return EQUIPO_MAP;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly rolProyectoService: RolProyectoService,
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
      equipo: new FormControl(''),
      activo: new FormControl('true')
    });
    this.filter = this.createFilter();
  }

  private setupI18N(): void {
    this.translate.get(
      ROL_EQUIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      ROL_EQUIPO_KEY,
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
      ROL_EQUIPO_KEY,
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
      ROL_EQUIPO_KEY,
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
      ROL_EQUIPO_KEY,
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
      ROL_EQUIPO_KEY,
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
      ROL_EQUIPO_KEY,
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
      ROL_EQUIPO_KEY,
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
      ROL_EQUIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorReactivar = value);

    this.translate.get(
      ROL_EQUIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_NEW,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IRolProyecto>> {
    return this.rolProyectoService.findTodos(this.getFindOptions(reset));
  }

  protected initColumns(): void {
    if (this.authService.hasAuthority('CSP-ROLE-R')) {
      this.columnas = ['nombre', 'abreviatura', 'equipo', 'rolPrincipal', 'orden', 'baremablePRC', 'activo', 'acciones'];
    } else {
      this.columnas = ['nombre', 'abreviatura', 'equipo', 'rolPrincipal', 'orden', 'baremablePRC', 'acciones'];
    }
  }

  protected loadTable(reset?: boolean): void {
    this.rolEquipo$ = this.getObservableLoadTable(reset);
    this.rolProyectoService.existsPrincipal().subscribe(res => this.showWarning = !res);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('nombre', SgiRestFilterOperator.LIKE_ICASE, controls.nombre.value);
    filter.and('equipo', SgiRestFilterOperator.EQUALS, controls.equipo.value);
    if (controls.activo.value !== 'todos') {
      filter.and('activo', SgiRestFilterOperator.EQUALS, controls.activo.value);
    }

    return filter;
  }

  protected resetFilters(): void {
    this.formGroup.controls.activo.setValue('true');
    this.formGroup.controls.nombre.setValue('');
    this.formGroup.controls.equipo.setValue('');
  }

  /**
   * Desactivamos registro de tipo regimen concurrencia
   * @param RolEquipo tipo regimen concurrencia
   */
  deactivateRolEquipo(RolEquipo: IRolProyecto): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.rolProyectoService.desactivar(RolEquipo.id);
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
   * Activamos un tipo regimen concurrencia
   * @param RolEquipo tipo regimen concurrencia
   */
  activateRolEquipo(RolEquipo: IRolProyecto): void {
    const subcription = this.dialogService.showConfirmation(this.textoReactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          RolEquipo.activo = true;
          return this.rolProyectoService.activar(RolEquipo.id);
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
          RolEquipo.activo = false;
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

