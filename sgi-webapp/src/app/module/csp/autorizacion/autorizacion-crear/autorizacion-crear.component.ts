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
import { AUTORIZACION_ROUTE_NAMES } from '../autorizacion-route-names';
import { AutorizacionActionService } from '../autorizacion.action.service';

const AUTORIZACION_KEY = marker('csp.autorizacion');
const MSG_BUTTON_SAVE = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.save.entity.success');
const MSG_ERROR = marker('error.save.entity');

@Component({
  selector: 'sgi-autorizacion-crear',
  templateUrl: './autorizacion-crear.component.html',
  styleUrls: ['./autorizacion-crear.component.scss'],
  viewProviders: [
    AutorizacionActionService
  ]
})
export class AutorizacionCrearComponent extends ActionComponent implements OnInit {
  AUTORIZACION_ROUTE_NAMES = AUTORIZACION_ROUTE_NAMES;

  textoCrear: string;
  textoCrearSuccess: string;
  textoCrearError: string;
  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: AutorizacionActionService,
    dialogService: DialogService,
    private readonly translate: TranslateService) {

    super(router, route, actionService, dialogService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  setupI18N() {
    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_SAVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoCrearSuccess = value);

    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoCrearError = value);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => {
        // This is intentional
      },
      (error) => {
        this.logger.error(error);
        if (error instanceof HttpProblem) {
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
        const autorizacionId = this.actionService.getFragment(this.actionService.FRAGMENT.DATOS_GENERALES).getKey();
        this.router.navigate([`../${autorizacionId}`], { relativeTo: this.activatedRoute });
      }
    );
  }

}
