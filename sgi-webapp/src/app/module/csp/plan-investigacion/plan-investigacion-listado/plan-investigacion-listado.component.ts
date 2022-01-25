import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { HttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { IPrograma } from '@core/models/csp/programa';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ProgramaService } from '@core/services/csp/programa.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MSG_BUTTON_ADD = marker('btn.add.entity');
const MSG_ERROR = marker('error.load');
const MSG_REACTIVE = marker('msg.csp.reactivate');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_ERROR_DEACTIVATE = marker('error.csp.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.csp.deactivate.success');
const PLAN_INVESTIGACION_KEY = marker('csp.plan-investigacion');

@Component({
  selector: 'sgi-plan-investigacion-listado',
  templateUrl: './plan-investigacion-listado.component.html',
  styleUrls: ['./plan-investigacion-listado.component.scss']
})
export class PlanInvestigacionListadoComponent extends AbstractTablePaginationComponent<IPrograma> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;
  textoDesactivar: string;
  textoReactivar: string;
  textoErrorDesactivar: string;
  textoSuccessDesactivar: string;
  textoSuccessReactivar: string;
  textoErrorReactivar: string;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  programas$: Observable<IPrograma[]>;

  msgParamEntity = {};

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly programaService: ProgramaService,
    private readonly dialogService: DialogService,
    public authService: SgiAuthService,
    private readonly translate: TranslateService
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
      nombre: new FormControl(''),
      activo: new FormControl('true')
    });
    this.filter = this.createFilter();
  }

  private setupI18N(): void {
    this.translate.get(
      PLAN_INVESTIGACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_ADD,
          { entity: value }
        );
      })
    ).subscribe((value) => this.msgParamEntity = { entity: value });


    this.translate.get(
      PLAN_INVESTIGACION_KEY,
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
      PLAN_INVESTIGACION_KEY,
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
      PLAN_INVESTIGACION_KEY,
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
      PLAN_INVESTIGACION_KEY,
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
      PLAN_INVESTIGACION_KEY,
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
      PLAN_INVESTIGACION_KEY,
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

  onClearFilters() {
    this.formGroup.controls.activo.setValue('true');
    this.formGroup.controls.nombre.setValue('');
    this.onSearch();
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IPrograma>> {
    const observable$ = this.programaService.findTodos(this.getFindOptions(reset));
    return observable$;
  }

  protected initColumns(): void {
    let columns = ['nombre', 'descripcion', 'activo', 'acciones'];

    if (!this.authService.hasAuthorityForAnyUO('CSP-PRG-R')) {
      columns = columns.filter(column => column !== 'activo');
    }

    this.columnas = columns;
  }

  protected loadTable(reset?: boolean): void {
    this.programas$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('nombre', SgiRestFilterOperator.LIKE_ICASE, controls.nombre.value);
    if (controls.activo.value !== 'todos') {
      filter.and('activo', SgiRestFilterOperator.EQUALS, controls.activo.value);
    }

    return filter;
  }

  /**
   * Desactivar plan
   * @param plan plan
   */
  desactivePlan(plan: IPrograma): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar).pipe(
      switchMap((accept) => {
        if (accept) {
          return this.programaService.deactivate(plan.id);
        }
        return of();
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
   * Activamos un plan desactivado
   * @param plan plan
   */
  activePlan(plan: IPrograma): void {
    const suscription = this.dialogService.showConfirmation(this.textoReactivar).pipe(
      switchMap((accept) => {
        if (accept) {
          plan.activo = true;
          return this.programaService.activate(plan.id);
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
          plan.activo = false;
          if (error instanceof HttpProblem) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(this.textoErrorReactivar);
          }
        }
      );
    this.suscripciones.push(suscription);
  }

}
