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
import { PLAN_INVESTIGACION_ROUTE_NAMES } from '../plan-investigacion-route-names';
import { PlanInvestigacionActionService } from '../plan-investigacion.action.service';

const MSG_BUTTON_SAVE = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.save.entity.success');
const MSG_ERROR = marker('error.save.entity');
const PLAN_INVESTIGACION_KEY = marker('csp.plan-investigacion');

@Component({
  selector: 'sgi-plan-investigacion-crear',
  templateUrl: './plan-investigacion-crear.component.html',
  styleUrls: ['./plan-investigacion-crear.component.scss'],
  viewProviders: [
    PlanInvestigacionActionService
  ]
})
export class PlanInvestigacionCrearComponent extends ActionComponent {
  PLAN_INVESTIGACION_ROUTE_NAMES = PLAN_INVESTIGACION_ROUTE_NAMES;

  textoCrear: string;
  textoCrearSuccess: string;
  textoCrearError: string;

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
    public readonly actionService: PlanInvestigacionActionService,
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
      PLAN_INVESTIGACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_SAVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      PLAN_INVESTIGACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrearSuccess = value);

    this.translate.get(
      PLAN_INVESTIGACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrearError = value);
  }


  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      (error) => {
        this.logger.error(error);
        if (error instanceof SgiError) {
          if (!!!error.managed) {
            this.snackBarService.showError(error);
          }
        }
        else {
          this.snackBarService.showError(this.textoCrearError);
        }
      },
      () => {
        this.snackBarService.showSuccess(this.textoCrearSuccess);
        const planInvestigacionId = this.actionService.getFragment(this.actionService.FRAGMENT.DATOS_GENERALES).getKey();
        this.router.navigate([`../${planInvestigacionId}`], { relativeTo: this.activatedRoute });
      }
    );
  }

}
