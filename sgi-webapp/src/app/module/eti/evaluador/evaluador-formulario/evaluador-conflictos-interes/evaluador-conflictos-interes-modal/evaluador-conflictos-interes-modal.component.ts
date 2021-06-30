import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IConflictoInteres } from '@core/models/eti/conflicto-interes';
import { IEvaluador } from '@core/models/eti/evaluador';
import { IPersona } from '@core/models/sgp/persona';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { TranslateService } from '@ngx-translate/core';
import { TipoColectivo } from '@shared/select-persona/select-persona.component';
import { Subscription } from 'rxjs';

const MSG_ERROR_FORM = marker('error.form-group');
const CONFLICTO_INTERES_KEY = marker('eti.evaluador.conflicto-interes');
const MSG_ERROR_CONFLICTO_REPETIDO = marker('error.eti.evaluador.conflicto-interes.duplicate');
const EVALUADOR_PERSONA_KEY = marker('title.eti.search.user');

@Component({
  selector: 'sgi-evaluador-conflictos-interes-modal',
  templateUrl: './evaluador-conflictos-interes-modal.component.html',
  styleUrls: ['./evaluador-conflictos-interes-modal.component.scss']
})
export class EvaluadorConflictosInteresModalComponent implements OnInit, OnDestroy {

  FormGroupUtil = FormGroupUtil;
  formGroup: FormGroup;
  fxLayoutProperties: FxLayoutProperties;

  personaChangeSuscripcion: Subscription;

  nuevaPersonaConflicto: IPersona;
  msgParamConflictoInteresEntity = {};
  msgParamPersonaEntity = {};

  get tipoColectivoEquipoTrabajo() {
    return TipoColectivo.EQUIPO_TRABAJO_ETICA;
  }

  constructor(
    @Inject(MAT_DIALOG_DATA) public conflictos: IConflictoInteres[],
    public readonly matDialogRef: MatDialogRef<EvaluadorConflictosInteresModalComponent>,
    private readonly snackBarService: SnackBarService,
    private readonly translate: TranslateService) {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'column';
  }

  ngOnInit(): void {
    this.initFormGroup();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      CONFLICTO_INTERES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConflictoInteresEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      EVALUADOR_PERSONA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPersonaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }
  /**
   * Inicializa el formGroup
   */
  private initFormGroup() {
    this.formGroup = new FormGroup({
      persona: new FormControl(null, [Validators.required]),
      identificador: new FormControl({ value: '', disabled: true }, [Validators.required]),
    });

    this.personaChangeSuscripcion =
      this.formGroup.controls.persona.valueChanges.subscribe((value) => {
        this.formGroup.controls.identificador.setValue(`${value.numeroDocumento}`);
      });
  }

  /**
   * Comprueba el formulario y envia el conflicto de interés resultante
   */
  saveOrUpdate() {
    const conflictoInteres = this.getDatosForm();
    if (FormGroupUtil.valid(this.formGroup)) {
      if (conflictoInteres) {
        const isRepetido =
          this.conflictos.some(conflictoListado =>
            conflictoInteres.personaConflicto.id === conflictoListado.personaConflicto.id);

        if (isRepetido) {
          this.snackBarService.showError(MSG_ERROR_CONFLICTO_REPETIDO);
          return;
        }
      }
      this.matDialogRef.close(conflictoInteres);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM);
    }
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   */
  private getDatosForm(): IConflictoInteres {
    const evaluadorObj: IEvaluador = {
      activo: null,
      cargoComite: null,
      comite: null,
      fechaAlta: null,
      fechaBaja: null,
      id: null,
      resumen: null,
      persona: null
    };
    const conflictoInteres: IConflictoInteres = {
      evaluador: evaluadorObj,
      id: null,
      personaConflicto: this.formGroup.controls.persona.value
    };
    return conflictoInteres;
  }

  ngOnDestroy(): void {
    this.personaChangeSuscripcion?.unsubscribe();
  }

}
