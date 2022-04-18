import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { ActionComponent } from '@core/component/action.component';
import { NGXLogger } from 'ngx-logger';
import { SOLICITUD_PROTECCION_ROUTE_NAMES } from '../solicitud-proteccion-route-names';
import { SolicitudProteccionActionService } from '../solicitud-proteccion.action.service';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { switchMap } from 'rxjs/operators';

const MSG_BUTTON_SAVE = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.save.entity.success');
const MSG_ERROR = marker('error.save.entity');
const SOLICITUD_PROTECCION_KEY = marker('pii.solicitud-proteccion');

@Component({
  selector: 'sgi-solicitud-proteccion-crear',
  templateUrl: './solicitud-proteccion-crear.component.html',
  styleUrls: ['./solicitud-proteccion-crear.component.scss'],
  viewProviders: [
    SolicitudProteccionActionService
  ]
})
export class SolicitudProteccionCrearComponent extends ActionComponent implements OnInit {

  SOLICITUD_PROTECCION_ROUTE_NAMES = SOLICITUD_PROTECCION_ROUTE_NAMES;

  textCrear: string;
  textCrearSuccess: string;
  textCrearError: string;

  constructor(
    private logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: SolicitudProteccionActionService,
    dialogService: DialogService,
    private translate: TranslateService
  ) {
    super(router, route, actionService, dialogService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_SAVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textCrear = value);

    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textCrearSuccess = value);

    this.translate.get(
      SOLICITUD_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textCrearError = value);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      (error) => {
        this.logger.error(error);
        if (error instanceof SgiError) {
          if (!error.managed) {
            this.snackBarService.showError(error);
          }
        }
        else {
          this.snackBarService.showError(this.textCrearError);
        }
      },
      () => {
        this.snackBarService.showSuccess(this.textCrearSuccess);
        const solicitudId = this.actionService.getFragment(this.actionService.FRAGMENT.DATOS_GENERALES).getKey();
        this.router.navigate([`../${solicitudId}`], { relativeTo: this.activatedRoute });
      }
    );
  }

  cancel(): void {
    this.router.navigate(['../'], { relativeTo: this.activatedRoute });
  }

}
