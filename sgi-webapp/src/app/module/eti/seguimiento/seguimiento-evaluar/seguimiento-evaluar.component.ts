import { Component, OnInit, ViewEncapsulation } from '@angular/core';
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
import { SeguimientoFormularioActionService } from '../../seguimiento-formulario/seguimiento-formulario.action.service';
import { SeguimientoEvaluarActionService } from '../seguimiento-evaluar.action.service';
import { SEGUIMIENTO_ROUTE_NAMES } from '../seguimiento-route-names';

const MSG_BUTTON_SAVE = marker('btn.ok');
const MSG_SUCCESS = marker('msg.save.entity.success');
const MSG_ERROR = marker('error.save.entity');
const SEGUIMIENTO_KEY = marker('eti.seguimiento');

@Component({
  selector: 'sgi-seguimiento-evaluar',
  templateUrl: './seguimiento-evaluar.component.html',
  styleUrls: ['./seguimiento-evaluar.component.scss'],
  encapsulation: ViewEncapsulation.None,
  viewProviders: [
    SeguimientoEvaluarActionService,
    {
      provide: SeguimientoFormularioActionService,
      useExisting: SeguimientoEvaluarActionService
    }
  ]
})
export class SeguimientoEvaluarComponent extends ActionComponent implements OnInit {
  SEGUIMIENTO_ROUTE_NAMES = SEGUIMIENTO_ROUTE_NAMES;
  textoCrear = MSG_BUTTON_SAVE;
  textoCrearSuccess: string;
  textoCrearError: string;

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    route: ActivatedRoute,
    router: Router,
    public actionService: SeguimientoEvaluarActionService,
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
      SEGUIMIENTO_KEY,
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
      SEGUIMIENTO_KEY,
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
      () => { },
      (error) => {
        this.logger.error(error);
        if (error instanceof SgiError) {
          this.snackBarService.showError(error);
        }
        else {
          this.snackBarService.showError(this.textoCrearError);
        }
      },
      () => {
        this.snackBarService.showSuccess(this.textoCrearSuccess);
      }
    );
  }
}
