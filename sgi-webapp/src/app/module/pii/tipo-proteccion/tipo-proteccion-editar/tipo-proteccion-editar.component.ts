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
import { Observable } from 'rxjs';
import { shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { PII_TIPO_PROTECCION_ROUTE_NAMES } from '../tipo-proteccion-route-names';
import { TipoProteccionActionService } from '../tipo-proteccion.action.service';

const MSG_BUTTON_SAVE = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.update.entity.success');
const MSG_ERROR = marker('error.update.entity');
const TIPO_PROTECCION_KEY = marker('pii.tipo-proteccion');
const SUBTIPO_PROTECCION_KEY = marker('pii.subtipo-proteccion');
@Component({
  selector: 'sgi-tipo-proteccion-editar',
  templateUrl: './tipo-proteccion-editar.component.html',
  styleUrls: ['./tipo-proteccion-editar.component.scss'],
  viewProviders: [
    TipoProteccionActionService
  ]
})
export class TipoProteccionEditarComponent extends ActionComponent implements OnInit {

  PII_TIPO_PROTECCION_ROUTE_NAMES = PII_TIPO_PROTECCION_ROUTE_NAMES;
  textoActualizar$: Observable<string>;
  tituloSubtipoProteccionPlural$: Observable<string>;
  textoEditarSuccess: string;
  textoEditarError: string;

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
    public readonly actionService: TipoProteccionActionService,
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
    const tituloSingular$ = this.translate.get(
      TIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(take(1), shareReplay(1));

    this.tituloSubtipoProteccionPlural$ = this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    );

    this.textoActualizar$ = tituloSingular$.pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_SAVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    );

    this.subscriptions.push(...[
      tituloSingular$.pipe(
        switchMap((value) => {
          return this.translate.get(
            MSG_SUCCESS,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.textoEditarSuccess = value),
      tituloSingular$.pipe(
        switchMap((value) => {
          return this.translate.get(
            MSG_ERROR,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.textoEditarError = value)
    ]);
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
          this.snackBarService.showError(this.textoEditarError);
        }
      },
      () => {
        this.snackBarService.showSuccess(this.textoEditarSuccess);
      }
    );
  }

}
