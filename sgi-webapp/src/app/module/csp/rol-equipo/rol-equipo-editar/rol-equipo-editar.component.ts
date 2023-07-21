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
import { ROL_EQUIPO_ROUTE_NAMES } from '../rol-equipo-route-names';
import { RolEquipoActionService } from '../rol-equipo.action.service';

const MSG_BUTTON_SAVE = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.update.entity.success');
const MSG_ERROR = marker('error.update.entity');
const ROL_EQUIPO_KEY = marker('csp.rol-equipo');

@Component({
  selector: 'sgi-rol-equipo-editar',
  templateUrl: './rol-equipo-editar.component.html',
  styleUrls: ['./rol-equipo-editar.component.scss'],
  viewProviders: [
    RolEquipoActionService
  ]
})
export class RolEquipoEditarComponent extends ActionComponent {

  ROL_EQUIPO_ROUTE_NAMES = ROL_EQUIPO_ROUTE_NAMES;

  textoCrear: string;
  textoUpdateSuccess: string;
  textoUpdateError: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
    public readonly actionService: RolEquipoActionService,
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
      ROL_EQUIPO_KEY,
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
      ROL_EQUIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
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
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoUpdateError = value);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      (err) => {
        this.logger.error(err);
        if (err instanceof SgiError) {
          if (!!!err.managed) {
            this.snackBarService.showError(err);
          }
        }
        else {
          this.snackBarService.showError(this.textoUpdateError);
        }
      },
      () => {
        this.snackBarService.showSuccess(this.textoUpdateSuccess);
      }
    );
  }
}
