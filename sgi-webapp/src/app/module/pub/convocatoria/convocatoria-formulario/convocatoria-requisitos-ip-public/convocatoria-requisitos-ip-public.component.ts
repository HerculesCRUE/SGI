import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { ISexo } from '@core/models/sgp/sexo';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SexoPublicService } from '@core/services/sgp/sexo-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Subscription } from 'rxjs';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';
import { ConvocatoriaRequisitosIPPublicFragment } from './convocatoria-requisitos-ip-public.fragment';

@Component({
  selector: 'sgi-convocatoria-requisitos-ip-public',
  templateUrl: './convocatoria-requisitos-ip-public.component.html',
  styleUrls: ['./convocatoria-requisitos-ip-public.component.scss']
})

export class ConvocatoriaRequisitosIPPublicComponent extends FormFragmentComponent<IConvocatoriaRequisitoIP> implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: ConvocatoriaRequisitosIPPublicFragment;
  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesOne: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesColumn: FxFlexProperties;
  fxFlexPropertiesEntidad: FxFlexProperties;

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
    protected actionService: ConvocatoriaPublicActionService,
    private readonly sexoService: SexoPublicService,
  ) {
    super(actionService.FRAGMENT.REQUISITOS_IP, actionService);
    this.formPart = this.fragment as ConvocatoriaRequisitosIPPublicFragment;

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

}
