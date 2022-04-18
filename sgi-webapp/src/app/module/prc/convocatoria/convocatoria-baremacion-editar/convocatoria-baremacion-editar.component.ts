import { Component, OnInit } from '@angular/core';
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
import { ConvocatoriaBaremacionActionService } from '../convocatoria-baremacion.action.service';
import { CONVOCATORIA_ROUTE_NAMES } from '../convocatoria-route-names';

const MSG_BUTTON_SAVE = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.save.entity.success');
const MSG_ERROR = marker('error.save.entity');
const CONVOCATORIA_KEY = marker('prc.convocatoria');

@Component({
  selector: 'sgi-convocatoria-baremacion-editar',
  templateUrl: './convocatoria-baremacion-editar.component.html',
  styleUrls: ['./convocatoria-baremacion-editar.component.scss'],
  viewProviders: [
    ConvocatoriaBaremacionActionService
  ]
})
export class ConvocatoriaBaremacionEditarComponent extends ActionComponent implements OnInit {
  CONVOCATORIA_ROUTE_NAMES = CONVOCATORIA_ROUTE_NAMES;

  textoCrear: string;
  textoUpdateSuccess: string;
  textoUpdateError: string;

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
    public readonly actionService: ConvocatoriaBaremacionActionService,
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
      CONVOCATORIA_KEY,
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
      CONVOCATORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoUpdateSuccess = value);

    this.translate.get(
      CONVOCATORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoUpdateError = value);
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
          this.snackBarService.showError(this.textoUpdateError);
        }
      },
      () => {
        this.snackBarService.showSuccess(this.textoUpdateSuccess);
      }
    );
  }
}
