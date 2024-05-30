import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { IRelacionEjecucionEconomicaWithResponsables } from '../../ejecucion-economica.action.service';

@Component({
  selector: 'sgi-ejecucion-economica-table-proyectos-relacionados',
  templateUrl: './table-proyectos-relacionados.component.html',
  styleUrls: ['./table-proyectos-relacionados.component.scss']
})
export class TableProyectosRelacionadosComponent implements OnInit {

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = [
    'id',
    'proyectoSge.id',
    'codigoExterno',
    'nombre',
    'responsables',
    'fechaInicio',
    'fechaFin',
    'fechaFinDefinitiva'
  ];

  readonly dataSource = new MatTableDataSource<IRelacionEjecucionEconomicaWithResponsables>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  private _isEjecucionEconomicaGruposEnabled: boolean;

  @Input()
  set data(value: IRelacionEjecucionEconomicaWithResponsables[]) {
    this.dataSource.data = value;
  }

  @Input()
  set isEjecucionEconomicaGruposEnabled(value: boolean) {
    this._isEjecucionEconomicaGruposEnabled = value;
  }

  get isEjecucionEconomicaGruposEnabled(): boolean {
    return this._isEjecucionEconomicaGruposEnabled ?? false;
  }

  constructor() { }

  ngOnInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

}
