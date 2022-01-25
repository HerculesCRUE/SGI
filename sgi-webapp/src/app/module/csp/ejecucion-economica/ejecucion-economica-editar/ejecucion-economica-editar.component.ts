import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { HttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { switchMap } from 'rxjs/operators';
import { EJECUCION_ECONOMICA_DATA_KEY } from '../ejecucion-economica-data.resolver';
import { EJECUCION_ECONOMICA_ROUTE_NAMES } from '../ejecucion-economica-route-names';
import { EjecucionEconomicaActionService, IEjecucionEconomicaData } from '../ejecucion-economica.action.service';

const MSG_BUTTON_EDIT = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.update.entity.success');
const MSG_ERROR = marker('error.update.entity');
const EJECUCION_ECONOMICA_KEY = marker('csp.ejecucion-economica');

@Component({
  selector: 'sgi-ejecucion-economica-editar',
  templateUrl: './ejecucion-economica-editar.component.html',
  styleUrls: ['./ejecucion-economica-editar.component.scss'],
  viewProviders: [
    EjecucionEconomicaActionService
  ]
})
export class EjecucionEconomicaEditarComponent extends ActionComponent implements OnInit {

  EJECUCION_ECONOMICA_ROUTE_NAMES = EJECUCION_ECONOMICA_ROUTE_NAMES;

  textoEditar: string;
  textoEditarSuccess: string;
  textoEditarError: string;

  private readonly data: IEjecucionEconomicaData;

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: EjecucionEconomicaActionService,
    dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(router, route, actionService, dialogService);
    this.data = route.snapshot.data[EJECUCION_ECONOMICA_DATA_KEY];
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      EJECUCION_ECONOMICA_KEY,
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
      EJECUCION_ECONOMICA_KEY,
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
      EJECUCION_ECONOMICA_KEY,
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
        this.logger.error(error);
        if (error instanceof HttpProblem) {
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

  get validacionGastos(): boolean {
    return this.data?.configuracion?.validacionGastos;
  }

  private returnUrl() {
    this.router.navigate(['../'], { relativeTo: this.activatedRoute });
  }
}
