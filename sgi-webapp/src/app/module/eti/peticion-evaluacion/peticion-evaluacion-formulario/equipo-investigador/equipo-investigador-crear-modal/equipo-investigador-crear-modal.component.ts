import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { IPersona } from '@core/models/sgp/persona';
import { DatosAcademicosService } from '@core/services/sgp/datos-academicos.service';
import { VinculacionService } from '@core/services/sgp/vinculacion.service';
import { TranslateService } from '@ngx-translate/core';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const PERSONA_KEY = marker('eti.peticion-evaluacion.equipo-investigador.persona');

@Component({
  selector: 'sgi-equipo-investigador-crear-modal',
  templateUrl: './equipo-investigador-crear-modal.component.html',
  styleUrls: ['./equipo-investigador-crear-modal.component.scss']
})
export class EquipoInvestigadorCrearModalComponent extends DialogFormComponent<IEquipoTrabajo> implements OnInit {

  msgParamEntity = {};

  get tipoColectivoPersona() {
    return TipoColectivo.EQUIPO_TRABAJO_ETICA;
  }

  constructor(
    matDialogRef: MatDialogRef<EquipoInvestigadorCrearModalComponent>,
    private readonly translate: TranslateService,
    private vinculacionService: VinculacionService,
    private datosAcademicosService: DatosAcademicosService
  ) {
    super(matDialogRef, false);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.subscriptions.push(
      this.formGroup.controls.persona.valueChanges.subscribe((value) => {
        this.onSelectPersona(value);
      }));
  }

  private setupI18N(): void {
    this.translate.get(
      PERSONA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  /**
   * Setea el persona seleccionado a través del componente
   * @param persona persona seleccionada
   */
  private onSelectPersona(personaSeleccionada: IPersona): void {
    this.formGroup.controls.nombreCompleto.setValue(`${personaSeleccionada.nombre} ${personaSeleccionada.apellidos}`);

    this.subscriptions.push(
      this.vinculacionService.findByPersonaId(personaSeleccionada.id)
        .subscribe((vinculacion) => {
          personaSeleccionada.vinculacion = vinculacion;
          this.formGroup.controls.vinculacion.setValue(vinculacion?.categoriaProfesional.nombre);
        })
    );

    this.subscriptions.push(
      this.datosAcademicosService.findByPersonaId(personaSeleccionada.id)
        .subscribe((datosAcademicos) => {
          personaSeleccionada.datosAcademicos = datosAcademicos;
          this.formGroup.controls.nivelAcademico.setValue(datosAcademicos?.nivelAcademico.nombre);
        })
    );
  }

  protected getValue(): IEquipoTrabajo {
    const equipoTrabajo: IEquipoTrabajo = {
      id: null,
      peticionEvaluacion: null,
      persona: this.formGroup.controls.persona.value
    };
    return equipoTrabajo;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombreCompleto: new FormControl({ value: '', disabled: true }),
      vinculacion: new FormControl({ value: '', disabled: true }),
      nivelAcademico: new FormControl({ value: '', disabled: true }),
      persona: new FormControl(null, [Validators.required])
    });

    return formGroup;
  }

}
