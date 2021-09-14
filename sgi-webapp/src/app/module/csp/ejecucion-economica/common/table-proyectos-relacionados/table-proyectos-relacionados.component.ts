import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { IProyectoRelacion } from '../../ejecucion-economica-formulario/desglose-economico.fragment';

@Component({
  selector: 'sgi-ejecucion-economica-table-proyectos-relacionados',
  templateUrl: './table-proyectos-relacionados.component.html',
  styleUrls: ['./table-proyectos-relacionados.component.scss']
})
export class TableProyectosRelacionadosComponent implements OnInit {

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = [
    'proyectoSgeRef',
    'proyectoAnualidad.proyecto.codigoExterno',
    'proyectoAnualidad.proyecto.titulo',
    'proyectoAnualidad.proyecto.fechaInicio',
    'proyectoAnualidad.proyecto.fechaFin',
    'nombreIP'
  ];

  readonly dataSource = new MatTableDataSource<IProyectoRelacion>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  @Input()
  set data(value: IProyectoRelacion[]) {
    this.dataSource.data = value;
  }

  constructor() { }

  ngOnInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

}
