import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { TipoEntidad } from '@core/models/csp/relacion-ejecucion-economica';
import { CAUSA_EXENCION_MAP } from '@core/models/csp/proyecto';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { CSP_ROUTE_NAMES } from '../../../csp-route-names';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { IRelacionEjecucionEconomicaWithIva, ProyectosFragment } from './proyectos.fragment';

@Component({
  selector: 'sgi-proyectos',
  templateUrl: './proyectos.component.html',
  styleUrls: ['./proyectos.component.scss']
})
export class ProyectosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: ProyectosFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = [
    'id',
    'proyectoSge.id',
    'nombre',
    'responsables',
    'fechaInicio',
    'fechaFin',
    'iva',
    'causaExencion',
    'sectorIva',
    'acciones'
  ];

  dataSource = new MatTableDataSource<IRelacionEjecucionEconomicaWithIva>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get TipoEntidad() {
    return TipoEntidad;
  }

  get CAUSA_EXENCION_MAP() {
    return CAUSA_EXENCION_MAP;
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

    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.relaciones$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
