import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Subject } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { CONVOCATORIA_ROUTE_NAMES } from '../convocatoria-route-names';
import { ConvocatoriaActionService } from '../convocatoria.action.service';

const CONVOCATORIA_KEY = marker('csp.convocatoria');
const MSG_BUTTON_EDIT = marker('btn.save.entity');
const MSG_BUTTON_REGISTRAR = marker('csp.convocatoria.registrar');
const MSG_SUCCESS = marker('msg.update.entity.success');
const MSG_ERROR = marker('error.update.entity');
const MSG_SUCCESS_REGISTRAR = marker('msg.csp.convocatoria.registrar.success');
const MSG_ERROR_REGISTRAR = marker('error.csp.convocatoria.registrar');

@Component({
  selector: 'sgi-convocatoria-editar',
  templateUrl: './convocatoria-editar.component.html',
  styleUrls: ['./convocatoria-editar.component.scss'],
  providers: [
    ConvocatoriaActionService
  ]
})
export class ConvocatoriaEditarComponent extends ActionComponent implements OnInit {
  CONVOCATORIA_ROUTE_NAMES = CONVOCATORIA_ROUTE_NAMES;

  textoCrear: string;
  textoRegistrar = MSG_BUTTON_REGISTRAR;
  textoEditarSuccess: string;
  textoEditarError: string;

  disableRegistrar$: Subject<boolean> = new BehaviorSubject<boolean>(true);
  private registrable = false;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: ConvocatoriaActionService,
    dialogService: DialogService,
    private convocatoriaService: ConvocatoriaService,
    private readonly translate: TranslateService
  ) {
    super(router, route, actionService, dialogService);
    this.disableRegistrar$.next(true);
    this.subscriptions.push(
      this.convocatoriaService.registrable(this.actionService.id).subscribe(
        registrable => {
          this.registrable = registrable;
          this.disableRegistrar$.next(!registrable);
        }
      )
    );
    this.subscriptions.push(this.actionService.status$.subscribe(
      status => {
        this.disableRegistrar$.next(!this.registrable || actionService.readonly || status.changes || status.errors);
      }
    ));
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
          MSG_BUTTON_EDIT,
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
    ).subscribe((value) => this.textoEditarSuccess = value);

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
    ).subscribe((value) => this.textoEditarError = value);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(this.textoEditarError);
      },
      () => {
        this.snackBarService.showSuccess(this.textoEditarSuccess);
        this.router.navigate(['../'], { relativeTo: this.activatedRoute });
      }
    );
  }

  registrar(): void {
    this.actionService.registrar().subscribe(
      () => { },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_REGISTRAR);
      },
      () => {
        this.snackBarService.showSuccess(MSG_SUCCESS_REGISTRAR);
        this.router.navigate(['../'], { relativeTo: this.activatedRoute });
      }
    );
  }

}
