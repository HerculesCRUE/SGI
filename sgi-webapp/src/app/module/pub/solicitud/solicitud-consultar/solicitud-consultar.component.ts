import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiError, SgiProblem } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { SolicitudPublicService } from '@core/services/csp/solicitud-public.service';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { tap } from 'rxjs/operators';

const MSG_GENERIC_ERROR_TITLE = marker('error.generic.title');
const MSG_GENERIC_ERROR_CONTENT = marker('error.generic.message');
const SOLICITUD_NUM_DOCUMENTO = marker('pub.solicitud.consultar.numero-documento');
const SOLICITID_UUID = marker('pub.solicitud.consultar.uuid-solicitud');


@Component({
  selector: 'sgi-solicitud-consultar',
  templateUrl: './solicitud-consultar.component.html',
  styleUrls: ['./solicitud-consultar.component.scss']
})
export class SolicitudConsultarComponent implements OnInit, OnDestroy {
  protected subscriptions: Subscription[] = [];
  formGroup: FormGroup;

  readonly problems$: BehaviorSubject<SgiProblem[]>;
  get problems(): SgiProblem[] {
    return this.problems$?.value;
  }

  msgParamNumeroDocumento = {};
  msgParamUUID = {};

  constructor(
    private readonly router: Router,
    private readonly activatedRoute: ActivatedRoute,
    private solicitudService: SolicitudPublicService,
    private readonly translate: TranslateService
  ) {
    this.problems$ = new BehaviorSubject<SgiProblem[]>([]);
  }

  ngOnInit(): void {
    this.setupI18N();

    this.formGroup = new FormGroup({
      numeroDocumento: new FormControl(undefined, Validators.required),
      uuid: new FormControl(undefined, Validators.required)
    });
  }


  ngOnDestroy(): void {
    this.subscriptions.forEach(x => x.unsubscribe());
  }

  onSearch(): void {
    if (!this.formGroup.valid) {
      return;
    }

    const numeroDocumento = this.formGroup.controls.numeroDocumento.value;
    const uuid = this.formGroup.controls.uuid.value;

    this.solicitudService.getPublicId(uuid, numeroDocumento)
      .pipe(
        tap(() => { },
          () => this.clearProblems(),
          () => this.clearProblems()
        ),
      ).subscribe(value => {
        this.router.navigate([value], { relativeTo: this.activatedRoute });
      },
        this.processError);

  }

  onClearFilters(): void {
    this.formGroup.reset();
  }

  private readonly processError: (error: Error) => void = (error: Error) => {
    if (error instanceof SgiError) {
      if (!error.managed) {
        error.managed = true;
        this.pushProblems(error);
      }
    }
    else {
      // Error incontrolado
      const sgiError = new SgiError(MSG_GENERIC_ERROR_TITLE, MSG_GENERIC_ERROR_CONTENT);
      sgiError.managed = true;
      this.pushProblems(sgiError);
    }
  }

  private pushProblems(problem: SgiProblem | SgiProblem[]): void {
    const current = this.problems$.value;
    if (Array.isArray(problem)) {
      this.problems$.next([...current, ...problem]);
    }
    else if (problem) {
      this.problems$.next([...current, problem]);
    }
  }

  private clearProblems(): void {
    this.problems$.next([]);
  }

  private setupI18N(): void {

    this.translate.get(
      SOLICITUD_NUM_DOCUMENTO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNumeroDocumento = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITID_UUID,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamUUID = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

  }

}
