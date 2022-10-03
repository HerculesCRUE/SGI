import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { ActionStatus } from '@core/services/action-service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Subject } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AUTORIZACION_ROUTE_NAMES } from '../autorizacion-route-names';
import { AutorizacionActionService } from '../autorizacion.action.service';

const AUTORIZACION_KEY = marker('csp.autorizacion');
const AUTORIZACION_SOLICITUD_KEY = marker('csp.autorizacion-solicitud');
const MSG_BUTTON_SAVE = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.update.entity.success');
const MSG_ERROR = marker('error.update.entity');
const MSG_SUCCESS_PRESENTAR = marker('msg.csp.autorizacion.presentar.success');
const MSG_ERROR_PRESENTAR = marker('error.csp.autorizacion.presentar');
const MSG_BUTTON_PRESENTAR = marker('btn.presentar.entity');
const MSG_BUTTON_CAMBIO_ESTADO = marker('btn.cambiar-estado');

@Component({
  selector: 'sgi-autorizacion-editar',
  templateUrl: './autorizacion-editar.component.html',
  styleUrls: ['./autorizacion-editar.component.scss'],
  viewProviders: [
    AutorizacionActionService
  ]
})
export class AutorizacionEditarComponent extends ActionComponent implements OnInit {
  AUTORIZACION_ROUTE_NAMES = AUTORIZACION_ROUTE_NAMES;

  textoEditar: string;
  textoEditarSuccess: string;
  textoEditarError: string;
  textoPresentar: string;
  textoCambioEstado = MSG_BUTTON_CAMBIO_ESTADO;
  disablePresentar$: Subject<boolean> = new BehaviorSubject<boolean>(true);
  disableCambioEstado = false;
  status: ActionStatus;

  get isInvestigador(): boolean {
    return this.actionService.isInvestigador;
  }

  get canEdit(): boolean {
    return this.actionService.canEdit;
  }

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: AutorizacionActionService,
    dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(router, route, actionService, dialogService);

    this.disablePresentar$.next(!this.actionService.presentable);

    this.subscriptions.push(
      this.actionService.status$.subscribe(
        status => {
          this.status = status;
          this.disableCambioEstado = (this.actionService.disableCambioEstado$.value || status.changes || status.errors);
          this.disablePresentar$.next(!this.actionService.presentable || status.changes || status.errors);
        }
      )
    );
    this.subscriptions.push(
      this.actionService.disableCambioEstado$.subscribe(
        disableCambioDeEstado => {
          this.disableCambioEstado = (disableCambioDeEstado || this.status.changes || this.status.errors);
        }
      )
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  setupI18N() {
    this.translate.get(
      AUTORIZACION_SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_SAVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoEditar = value);

    this.translate.get(
      AUTORIZACION_SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_PRESENTAR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoPresentar = value);

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
    ).subscribe((value) => this.textoEditarSuccess = value);

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
    ).subscribe((value) => this.textoEditarError = value);
  }

  saveOrUpdate(action: 'save' | 'presentar' | 'cambiar-estado'): void {
    if (action === 'presentar') {
      this.presentar();
    }
    else if (action === 'cambiar-estado') {
      this.actionService.openCambioEstado();
    }
    else {
      this.actionService.saveOrUpdate().subscribe(
        () => {
          // This is intentional
        },
        (error) => {
          this.logger.error(error);
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
  }

  private presentar(): void {
    this.actionService.presentar().subscribe(
      () => {
        // This is intentional
      },
      (error) => {
        this.logger.error(error);
        if (error instanceof SgiError) {
          this.snackBarService.showError(error);
        }
        else {
          this.snackBarService.showError(MSG_ERROR_PRESENTAR);
        }
      },
      () => {
        this.snackBarService.showSuccess(MSG_SUCCESS_PRESENTAR);
        this.router.navigate(['../'], { relativeTo: this.activatedRoute });
      }
    );
  }

}
