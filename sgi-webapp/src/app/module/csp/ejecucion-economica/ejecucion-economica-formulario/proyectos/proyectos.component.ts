import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { Estado, ESTADO_MAP } from '@core/models/csp/estado-proyecto';
import { CAUSA_EXENCION_MAP } from '@core/models/csp/proyecto';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../../../csp-route-names';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { ProyectosFragment } from './proyectos.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const IDENTIFICADOR_SGE_KEY = marker('csp.proyecto-proyecto-sge.identificador-sge');

@Component({
  selector: 'sgi-proyectos',
  templateUrl: './proyectos.component.html',
  styleUrls: ['./proyectos.component.scss']
})
export class ProyectosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: ProyectosFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = [
    'proyecto.titulo',
    'proyecto.acronimo',
    'proyecto.fechaInicio',
    'proyecto.fechaFin',
    'proyecto.estado.estado',
    'proyecto.iva.iva',
    'proyecto.causaExencion',
    'proyectoSgeRef',
    'proyectoSge.sectorIva',
    'acciones'
  ];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<IProyectoProyectoSge>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  get Estado() {
    return Estado;
  }

  get CAUSA_EXENCION_MAP() {
    return CAUSA_EXENCION_MAP;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get CSP_ROUTE_NAMES() {
    return CSP_ROUTE_NAMES;
  }

  constructor(
    private actionService: EjecucionEconomicaActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.PROYECTOS, actionService);

    this.formPart = this.fragment as ProyectosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (element: IProyectoProyectoSge, property: string) => {
        switch (property) {
          case 'proyectoSgeRef':
            return element.proyectoSge.id;
          case 'proyecto.titulo':
            return element.proyecto.titulo;
          case 'proyecto.acronimo':
            return element.proyecto.acronimo;
          case 'proyecto.fechaInicio':
            return element.proyecto.fechaInicio;
          case 'proyecto.fechaFin':
            return element.proyecto.fechaFin;
          case 'proyecto.estado.estado':
            return element.proyecto.estado.estado;
          case 'proyectoSge.sectorIva':
            return element.proyectoSge.sectorIva;
          default:
            return element[property];
        }
      };

    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.proyectosSge$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      IDENTIFICADOR_SGE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      IDENTIFICADOR_SGE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
