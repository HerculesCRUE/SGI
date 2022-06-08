import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEquipoInstrumental } from '@core/models/csp/grupo-equipo-instrumental';
import { IGrupoLineaEquipoInstrumental } from '@core/models/csp/grupo-linea-equipo-instrumental';
import { IPersona } from '@core/models/sgp/persona';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const GRUPO_LINEA_EQUIPO_INSTRUMENTAL_KEY = marker('csp.grupo-linea-investigador');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface GrupoLineaEquipoInstrumentalModalData {
  titleEntity: string;
  idGrupo: number;
  selectedEntidades: IGrupoLineaEquipoInstrumental[];
  entidad: IGrupoLineaEquipoInstrumental;
  isEdit: boolean;
}

@Component({
  templateUrl: './grupo-linea-equipo-instrumental-modal.component.html',
  styleUrls: ['./grupo-linea-equipo-instrumental-modal.component.scss']
})
export class GrupoLineaEquipoInstrumentalModalComponent extends DialogFormComponent<GrupoLineaEquipoInstrumentalModalData> implements OnInit {

  textSaveOrUpdate: string;
  requiredFechaFin;

  equiposInstrumentales$ = new BehaviorSubject<StatusWrapper<IGrupoEquipoInstrumental>[]>([]);

  title: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    matDialogRef: MatDialogRef<GrupoLineaEquipoInstrumentalModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: GrupoLineaEquipoInstrumentalModalData,
    private readonly translate: TranslateService,
    private grupoService: GrupoService,
    private personaService: PersonaService,
  ) {
    super(matDialogRef, !!data?.isEdit);

    this.textSaveOrUpdate = this.data?.isEdit ? MSG_ACEPTAR : MSG_ANADIR;

    this.subscriptions.push(
      this.grupoService.findEquiposInstrumentales(this.data.idGrupo).pipe(
        map((response) => response.items),
      ).subscribe(
        equiposInstrumentales => {
          this.equiposInstrumentales$.next(equiposInstrumentales.map(
            equipoInstrumental => {
              equipoInstrumental.grupo = { id: this.data.idGrupo } as IGrupo;
              return new StatusWrapper<IGrupoEquipoInstrumental>(equipoInstrumental);
            }));
        },
        error => {
          this.logger.error(error);
        }
      )
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {


    if (this.data?.isEdit) {
      this.translate.get(
        this.data.titleEntity,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        this.data.titleEntity,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        equipoInstrumental: new FormControl(this.data?.entidad?.grupoEquipoInstrumental, [Validators.required]),
      }
    );

    return formGroup;
  }

  protected getValue(): GrupoLineaEquipoInstrumentalModalData {
    this.data.entidad.grupoEquipoInstrumental = this.formGroup.get('equipoInstrumental').value;
    if (!this.data.entidad.grupoEquipoInstrumental.nombre) {
      this.data.entidad.grupoEquipoInstrumental = this.formGroup.get('equipoInstrumental').value.value;
    }
    return this.data;
  }

  displayerEquipoInstrumental(equipoInstrumental: StatusWrapper<IGrupoEquipoInstrumental> | IGrupoEquipoInstrumental): string {
    const eq = equipoInstrumental as IGrupoEquipoInstrumental;
    if (eq?.nombre) {
      return eq?.nombre ? (eq?.nombre + ' - ' + eq?.numRegistro) : '';
    } else {
      const wrapperEq = equipoInstrumental as StatusWrapper<IGrupoEquipoInstrumental>;
      return wrapperEq?.value?.nombre ? (wrapperEq?.value?.nombre + ' - ' + wrapperEq?.value?.numRegistro) : '';
    }
  }

}
