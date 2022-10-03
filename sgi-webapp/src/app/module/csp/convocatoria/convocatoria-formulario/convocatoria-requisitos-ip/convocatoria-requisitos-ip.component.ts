import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { ISexo } from '@core/models/sgp/sexo';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { SexoService } from '@core/services/sgp/sexo.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { BehaviorSubject, Subscription } from 'rxjs';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { CategoriaProfesionalModalComponent, CategoriaProfesionalModalData } from '../../modals/categoria-profesional-modal/categoria-profesional-modal.component';
import { NivelAcademicoModalComponent, NivelAcademicoModalData } from '../../modals/nivel-academico-modal/nivel-academico-modal.component';
import { ConvocatoriaRequisitosIPFragment, RequisitoIPCategoriaProfesionalListado, RequisitoIPNivelAcademicoListado } from './convocatoria-requisitos-ip.fragment';

const MSG_DELETE_KEY = marker('msg.delete.entity');
const CONVOCATORIA_REQUISITOS_IP_MODALIDAD_CONTRATOS_KEY = marker('csp.convocatoria-requisito-ip.modalidad-contratos');
const CONVOCATORIA_CATEGORIA_PROFESIONAL_KEY = marker('csp.convocatoria.categoria-profesional');
const CONVOCATORIA_NIVEL_ACADEMICO_KEY = marker('csp.convocatoria.nivel-academico');

@Component({
  selector: 'sgi-convocatoria-requisitos-ip',
  templateUrl: './convocatoria-requisitos-ip.component.html',
  styleUrls: ['./convocatoria-requisitos-ip.component.scss']
})

export class ConvocatoriaRequisitosIPComponent extends FormFragmentComponent<IConvocatoriaRequisitoIP> implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: ConvocatoriaRequisitosIPFragment;
  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesOne: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesColumn: FxFlexProperties;
  fxFlexPropertiesEntidad: FxFlexProperties;

  msgParamCategoriaProfesionalEntity = {};
  msgParamModalidadContratoEntity = {};
  msgParamNivelAcademicoEntity = {};
  textoDeleteCategoriaProfesional: string;
  textoDeleteNivelAcademico: string;

  nivelAcademicoDataSource = new MatTableDataSource<StatusWrapper<IRequisitoIPNivelAcademico>>();
  displayedColumnsNivelAcademico = ['nombre', 'acciones'];
  @ViewChild('sortNivelAcademico', { static: true }) sortNivelAcademico: MatSort;

  categoriaProfesionalDataSource = new MatTableDataSource<StatusWrapper<IRequisitoIPCategoriaProfesional>>();
  displayedColumnsCategoriaProfesional = ['nombre', 'acciones'];
  @ViewChild('sortCategoriaProfesional', { static: true }) sortCategoriaProfesional: MatSort;

  disableAddCategoriaProfesional$ = new BehaviorSubject<boolean>(false);
  disableVinculacionProfesional$ = new BehaviorSubject<boolean>(false);

  sexos$ = new BehaviorSubject<ISexo[]>([]);

  constructor(
    protected actionService: ConvocatoriaActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    public translate: TranslateService,
    private readonly sexoService: SexoService,
    private authService: SgiAuthService
  ) {
    super(actionService.FRAGMENT.REQUISITOS_IP, actionService);
    this.formPart = this.fragment as ConvocatoriaRequisitosIPFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '1';

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '4';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit() {
    super.ngOnInit();
    this.setupI18N();

    this.subscriptions.push(this.formPart.nivelesAcademicos$.subscribe(elements => {
      this.nivelAcademicoDataSource.data = elements;
    }));

    this.subscriptions.push(this.formPart.categoriasProfesionales$.subscribe(elements => {
      this.categoriaProfesionalDataSource.data = elements;
      this.checkCategoriasProfesionales();
    }));

    this.subscriptions.push(this.formGroup.controls.vinculacionUniversidad.valueChanges.subscribe(value => {
      this.checkCategoriasProfesionales();
    }));

    this.subscriptions.push(this.sexoService.findAll().subscribe(values => {
      this.sexos$.next(values.items);
    }));

    this.nivelAcademicoDataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IRequisitoIPNivelAcademico>, property: string) => {
        switch (property) {
          case 'nombre':
            return wrapper.value.nivelAcademico.nombre;
          default:
            return wrapper.value[property];
        }
      };

    this.nivelAcademicoDataSource.sort = this.sortNivelAcademico;

    this.categoriaProfesionalDataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IRequisitoIPCategoriaProfesional>, property: string) => {
        switch (property) {
          case 'nombre':
            return wrapper.value.categoriaProfesional.nombre;
          default:
            return wrapper.value[property];
        }
      };

    this.categoriaProfesionalDataSource.sort = this.sortCategoriaProfesional;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_REQUISITOS_IP_MODALIDAD_CONTRATOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe(
      (value) => this.msgParamModalidadContratoEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      CONVOCATORIA_CATEGORIA_PROFESIONAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe(
      (value) => this.msgParamCategoriaProfesionalEntity =
        { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      CONVOCATORIA_NIVEL_ACADEMICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe(
      (value) => {
        this.msgParamNivelAcademicoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR };
        this.translate.get(
          MSG_DELETE_KEY,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        ).subscribe((valueDelete) => this.textoDeleteNivelAcademico = valueDelete);
      }
    );

    this.translate.get(
      CONVOCATORIA_CATEGORIA_PROFESIONAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe(
      (value) => {
        this.msgParamCategoriaProfesionalEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR };
        this.translate.get(
          MSG_DELETE_KEY,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        ).subscribe((valueDelete) => this.textoDeleteCategoriaProfesional = valueDelete);
      }
    );
  }

  deleteNivelAcademico(wrapper: StatusWrapper<RequisitoIPNivelAcademicoListado>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDeleteNivelAcademico).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteNivelAcademico(wrapper);
          }
        }
      )
    );
  }

  deleteCategoriaProfesional(wrapper: StatusWrapper<RequisitoIPCategoriaProfesionalListado>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDeleteCategoriaProfesional).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteCategoriaProfesional(wrapper);
          }
        }
      )
    );
  }

  /**
   * Apertura de modal de nivel académico
   */
  openModalNivelAcademico(): void {
    const data: NivelAcademicoModalData = {
      selectedEntidades: this.nivelAcademicoDataSource.data.map(element => element.value.nivelAcademico),
    };

    const config = {
      data
    };
    const dialogRef = this.matDialog.open(NivelAcademicoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (nivelSeleccionado) => {
        if (nivelSeleccionado) {
          this.formPart.addNivelAcademico(nivelSeleccionado);
        }
      }
    );
  }

  /**
   * Apertura de modal de categorías profesionales
   */
  openModalClasificacionProfesional(): void {
    const data: CategoriaProfesionalModalData = {
      selectedEntidades: this.categoriaProfesionalDataSource.data.map(element => element.value.categoriaProfesional),
    };
    const config = {
      data
    };
    const dialogRef = this.matDialog.open(CategoriaProfesionalModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (categoriaProfesionalSeleccionada) => {
        if (categoriaProfesionalSeleccionada) {
          this.formPart.addCategoriaProfesional(categoriaProfesionalSeleccionada);
        }
      }
    );
  }

  private checkCategoriasProfesionales() {
    if (!this.formPart.hasEditPerm) {
      return;
    }

    // En caso de que se marque Sí en vinculación universidad se habilitará el botón de añadir categoría profesional
    this.disableAddCategoriaProfesional$.next(!this.formGroup.controls.vinculacionUniversidad.value);

    // Si vinculación universidad toma valor "sí" y ya han sido añadidas categorías profesionales
    // no se permitirá modificar el campo  vinculación universidad hasta que no se hayan eliminado
    // todas las categorías de la restricción.
    // Siempre que este campo se muestre en modo solo lectura  se mostrará el icono de información con el tooltip
    if (this.categoriaProfesionalDataSource.data && this.categoriaProfesionalDataSource.data.length > 0
      && this.formGroup.controls.vinculacionUniversidad.value) {
      this.formGroup.controls.vinculacionUniversidad.disable({ emitEvent: false });
      this.disableVinculacionProfesional$.next(true);
    } else {
      this.formGroup.controls.vinculacionUniversidad.enable({ emitEvent: false });
      this.disableVinculacionProfesional$.next(false);
    }
  }

}
