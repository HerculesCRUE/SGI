import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IConflictoInteres } from '@core/models/eti/conflicto-interes';
import { IEvaluador } from '@core/models/eti/evaluador';
import { IPersona } from '@core/models/sgp/persona';
import { TranslateService } from '@ngx-translate/core';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const CONFLICTO_INTERES_KEY = marker('eti.evaluador.conflicto-interes');
const MSG_ERROR_CONFLICTO_REPETIDO = marker('error.eti.evaluador.conflicto-interes.duplicate');
const EVALUADOR_PERSONA_KEY = marker('title.eti.search.user');

@Component({
  selector: 'sgi-evaluador-conflictos-interes-modal',
  templateUrl: './evaluador-conflictos-interes-modal.component.html',
  styleUrls: ['./evaluador-conflictos-interes-modal.component.scss']
})
export class EvaluadorConflictosInteresModalComponent extends DialogFormComponent<IConflictoInteres> implements OnInit {

  nuevaPersonaConflicto: IPersona;
  msgParamConflictoInteresEntity = {};
  msgParamPersonaEntity = {};

  get tipoColectivoEquipoTrabajo() {
    return TipoColectivo.EQUIPO_TRABAJO_ETICA;
  }

  constructor(
    @Inject(MAT_DIALOG_DATA) public conflictos: IConflictoInteres[],
    matDialogRef: MatDialogRef<EvaluadorConflictosInteresModalComponent>,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, false);
  }

  ngOnInit(): void {
    super.ngOnInit();
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
  protected buildFormGroup() {
    const formGroup = new FormGroup({
      persona: new FormControl(null, [Validators.required]),
    });
    return formGroup;
  }

  doAction(): void {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      const conflictoInteres = this.getValue();
      this.clearProblems();
      const isRepetido =
        this.conflictos.some(conflictoListado =>
          conflictoInteres.personaConflicto.id === conflictoListado.personaConflicto.id);

      if (isRepetido) {
        this.pushProblems(new SgiError(MSG_ERROR_CONFLICTO_REPETIDO));
      }
      else {
        this.close(conflictoInteres);
      }
    }
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   */
  protected getValue(): IConflictoInteres {
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

}
