import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { IRolProyectoColectivo } from '@core/models/csp/rol-proyecto-colectivo';
import { RolProyectoColectivoService } from '@core/services/csp/rol-proyecto-colectivo/rol-proyecto-colectivo.service';
import { DialogService } from '@core/services/dialog.service';
import { ColectivoService } from '@core/services/sgp/colectivo.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto/rol-proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { from, Observable, of, throwError, BehaviorSubject } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap } from 'rxjs/operators';
import { RolProyectoColectivoListado } from '../../rol-equipo-formulario/rol-equipo-colectivos/rol-equipo-colectivos.fragment';

const ROL_EQUIPO_COLECTIVO_KEY = marker('csp.rol-equipo-colectivo');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ColectivoModalData {
  rolEquipo: IRolProyecto;
  colectivos: IRolProyectoColectivo[];
}

@Component({
  selector: 'sgi-rol-equipo-colectivo-modal',
  templateUrl: './rol-equipo-colectivo-modal.component.html',
  styleUrls: ['./rol-equipo-colectivo-modal.component.scss']
})
export class RolEquipoColectivoModalComponent extends DialogFormComponent<IRolProyectoColectivo> implements OnInit {

  title = {};

  readonly colectivos$ = new BehaviorSubject<RolProyectoColectivoListado[]>([]);

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<RolEquipoColectivoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ColectivoModalData,
    protected snackBarService: SnackBarService,
    private readonly translate: TranslateService,
    private confirmDialogService: DialogService,
    private rolProyectoService: RolProyectoService,
    private colectivoService: ColectivoService
  ) {
    super(matDialogRef, true);

    const subscription = this.colectivoService.findAll().pipe(
      map(colectivos => {
        return colectivos.items.filter(colectivo => !this.data.colectivos.map(col => col.colectivoRef).includes(colectivo.id)).map(colectivo => {
          const rolProyectoColectivoListado = {
            colectivoRef: colectivo.id,
            colectivo: colectivo.nombre
          } as RolProyectoColectivoListado;
          return new StatusWrapper<RolProyectoColectivoListado>(rolProyectoColectivoListado as RolProyectoColectivoListado);
        });
      })
    ).subscribe((rolEquipoColectivo) => {
      this.colectivos$.next(rolEquipoColectivo.map(wrapper => wrapper.value));
    });

  }

  ngOnInit(): void {
    super.ngOnInit();
    this.matDialogRef.updateSize('20vw');
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      ROL_EQUIPO_COLECTIVO_KEY,
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

  protected getValue(): RolProyectoColectivoListado {
    const colectivo = this.formGroup.controls.colectivo.value;
    return {
      id: undefined,
      colectivoRef: colectivo.colectivoRef,
      rolProyectoId: this.data.rolEquipo?.id,
      colectivo: colectivo.colectivo
    } as RolProyectoColectivoListado;
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      colectivo: new FormControl({ value: null, disabled: false }),
    });
  }

  displayerColectivo(colectivo: RolProyectoColectivoListado): string {
    return colectivo?.colectivo ?? '';
  }


}
