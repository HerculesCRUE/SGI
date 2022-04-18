import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { switchMap } from 'rxjs/operators';
import { CONVOCATORIA_CONCEPTO_GASTO_ROUTE_NAMES } from '../convocatoria-concepto-gasto-route-names';
import { ConvocatoriaConceptoGastoActionService } from '../convocatoria-concepto-gasto.action.service';

const MSG_BUTTON_EDIT = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.update.entity.success');
const MSG_ERROR = marker('error.update.entity');
const CONVOCATORIA_CONCEPTO_GASTO_KEY = marker('csp.convocatoria-elegibilidad.concepto-gasto.concepto-gasto');

@Component({
  selector: 'sgi-convocatoria-concepto-gasto-editar',
  templateUrl: './convocatoria-concepto-gasto-editar.component.html',
  styleUrls: ['./convocatoria-concepto-gasto-editar.component.scss'],
  viewProviders: [
    ConvocatoriaConceptoGastoActionService
  ]
})
export class ConvocatoriaConceptoGastoEditarComponent extends ActionComponent {

  CONVOCATORIA_CONCEPTO_GASTO_ROUTE_NAMES = CONVOCATORIA_CONCEPTO_GASTO_ROUTE_NAMES;

  textoEditar: string;
  textoEditarSuccess: string;
  textoEditarError: string;

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: ConvocatoriaConceptoGastoActionService,
    dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(router, route, actionService, dialogService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_CONCEPTO_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_EDIT,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoEditar = value);

    this.translate.get(
      CONVOCATORIA_CONCEPTO_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoEditarSuccess = value);

    this.translate.get(
      CONVOCATORIA_CONCEPTO_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoEditarError = value);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      (error) => {
        if (error instanceof SgiError) {
          if (!!!error.managed) {
            this.snackBarService.showError(error);
          }
        }
        else {
          this.snackBarService.showError(this.textoEditarError);
        }
      },
      () => {
        this.snackBarService.showSuccess(this.textoEditarSuccess);
      }
    );
  }

  cancel(): void {
    this.returnUrl();
  }

  private returnUrl() {
    this.router.navigate(['../'], { relativeTo: this.activatedRoute });
  }
}
