import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IRequisitoEquipoCategoriaProfesional } from '@core/models/csp/requisito-equipo-categoria-profesional';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { ISexo } from '@core/models/sgp/sexo';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SexoPublicService } from '@core/services/sgp/sexo-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Subscription } from 'rxjs';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';
import { ConvocatoriaRequisitosEquipoPublicFragment } from './convocatoria-requisitos-equipo-public.fragment';

@Component({
  selector: 'sgi-convocatoria-requisitos-equipo-public',
  templateUrl: './convocatoria-requisitos-equipo-public.component.html',
  styleUrls: ['./convocatoria-requisitos-equipo-public.component.scss']
})
export class ConvocatoriaRequisitosEquipoPublicComponent extends FormFragmentComponent<IConvocatoriaRequisitoEquipo> implements OnInit {
  formPart: ConvocatoriaRequisitosEquipoPublicFragment;
  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesOne: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesEntidad: FxFlexProperties;

  private subscriptions: Subscription[] = [];

  nivelAcademicoDataSource = new MatTableDataSource<StatusWrapper<IRequisitoEquipoNivelAcademico>>();
  displayedColumnsNivelAcademico = ['nombre', 'acciones'];
  @ViewChild('sortNivelAcademico', { static: true }) sortNivelAcademico: MatSort;

  categoriaProfesionalDataSource = new MatTableDataSource<StatusWrapper<IRequisitoEquipoCategoriaProfesional>>();
  displayedColumnsCategoriaProfesional = ['nombre', 'acciones'];
  @ViewChild('sortCategoriaProfesional', { static: true }) sortCategoriaProfesional: MatSort;

  sexos$ = new BehaviorSubject<ISexo[]>([]);

  constructor(
    protected actionService: ConvocatoriaPublicActionService,
    private readonly sexoService: SexoPublicService
  ) {
    super(actionService.FRAGMENT.REQUISITOS_EQUIPO, actionService);
    this.formPart = this.fragment as ConvocatoriaRequisitosEquipoPublicFragment;

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

    this.subscriptions.push(this.formPart.nivelesAcademicos$.subscribe(elements => {
      this.nivelAcademicoDataSource.data = elements;
    }));

    this.subscriptions.push(this.formPart.categoriasProfesionales$.subscribe(elements => {
      this.categoriaProfesionalDataSource.data = elements;
    }));

    this.subscriptions.push(this.sexoService.findAll().subscribe(values => {
      this.sexos$.next(values.items);
    }));

    this.nivelAcademicoDataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IRequisitoEquipoNivelAcademico>, property: string) => {
        switch (property) {
          case 'nombre':
            return wrapper.value.nivelAcademico.nombre;
          default:
            return wrapper.value[property];
        }
      };

    this.nivelAcademicoDataSource.sort = this.sortNivelAcademico;

    this.categoriaProfesionalDataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IRequisitoEquipoCategoriaProfesional>, property: string) => {
        switch (property) {
          case 'nombre':
            return wrapper.value.categoriaProfesional.nombre;
          default:
            return wrapper.value[property];
        }
      };

    this.categoriaProfesionalDataSource.sort = this.sortCategoriaProfesional;
  }

}
