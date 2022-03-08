import { Component, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { SolicitudProyectoEntidadService } from '@core/services/csp/solicitud-proyecto-entidad/solicitud-proyecto-entidad.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ISolicitudProyectoPresupuetoModalData, SolicitudProyectoPresupuestoListadoExportModalComponent } from '../../solicitud/modals/solicitud-proyecto-presupuesto-listado-export-modal/solicitud-proyecto-presupuesto-listado-export-modal.component';

const TITLE_PRESUPUESTO_COMPLETO = marker('title.csp.presupuesto-completo');
const TITLE_PRESUPUESTO_ENTIDAD = marker('title.csp.presupuesto-entidad');

export interface SolicitudPresupuestoModalData {
  idSolicitudProyecto: number;
  presupuestos: ISolicitudProyectoPresupuesto[];
  entidadId?: string;
  global: boolean;
}

abstract class RowTree<T> {
  level: number;
  get expanded(): boolean {
    return this._expanded;
  }
  // tslint:disable-next-line: variable-name
  private _expanded = false;
  item: T;
  childs: RowTree<T>[] = [];
  parent: RowTree<T>;

  constructor(item: T) {
    this.item = item;
    this.level = 0;
  }

  addChild(child: RowTree<T>): void {
    child.parent = this;
    child.level = this.level + 1;
    this.childs.push(child);
  }

  abstract compute(): void;

  expand() {
    this._expanded = true;
  }

  collapse() {
    this._expanded = false;
    this.childs.forEach(child => child.collapse());
  }
}

class RowTreePresupuesto extends RowTree<ISolicitudProyectoPresupuesto> {

  constructor(item: ISolicitudProyectoPresupuesto) {
    super(item);
  }

  compute(): void {
    if (this.childs.length) {
      this.childs.forEach(child => {
        child.compute();
      });
      this.childs.forEach(child => {
        this.item.importeSolicitado += child.item.importeSolicitado;
        this.item.importePresupuestado += child.item.importePresupuestado;
      });
    }
  }
}

@Component({
  templateUrl: './solicitud-presupuesto-modal.component.html',
  styleUrls: ['./solicitud-presupuesto-modal.component.scss']
})
export class SolicitiudPresupuestoModalComponent {

  readonly columnas: string[];
  private readonly columnasGlobal = ['anualidad', 'conceptoGasto', 'observaciones', 'importePresupuestado', 'importeSolicitado'];
  private readonly columnasEntidad = ['anualidad', 'nombre', 'fuenteFinanciacion', 'ajena', 'conceptoGasto', 'observaciones', 'importePresupuestado', 'importeSolicitado'];

  dataSource = new MatTableDataSource<RowTreePresupuesto>();
  readonly title: string;

  private mapTree = new Map<string, RowTreePresupuesto>();

  constructor(
    public matDialogRef: MatDialogRef<SolicitiudPresupuestoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public readonly data: SolicitudPresupuestoModalData,
    solicitudService: SolicitudService,
    private readonly solicitudProyectoEntidadService: SolicitudProyectoEntidadService,
    private readonly empresaService: EmpresaService,
    private matDialog: MatDialog,
  ) {
    this.title = this.data.entidadId ? TITLE_PRESUPUESTO_ENTIDAD : TITLE_PRESUPUESTO_COMPLETO;
    this.columnas = this.data.global ? this.columnasGlobal : this.columnasEntidad;
    let findOptions: SgiRestFindOptions;
    if (!this.data.global && this.data.entidadId) {
      findOptions = {
        filter: new RSQLSgiRestFilter('entidadRef', SgiRestFilterOperator.EQUALS, this.data.entidadId)
      };
    }
    const presupuestos$ = this.data.global
      ? of(this.data.presupuestos)
      : solicitudService.findAllSolicitudProyectoPresupuesto(data.idSolicitudProyecto, findOptions).pipe(map(response => response.items));

    presupuestos$.pipe(
      map(presupuestos => {

        this.sortRows(presupuestos)

        const root = this.data.global ? this.toGlobal(presupuestos) : this.toEntidad(presupuestos);
        const regs: RowTreePresupuesto[] = [];
        root.forEach(r => {
          r.compute();
          regs.push(...this.addChilds(r));
        });
        return regs;
      })
    ).subscribe(
      (presupuestos) => {
        this.dataSource.data = presupuestos;
      }
    );
  }

  private toEntidad(presupuestos: ISolicitudProyectoPresupuesto[]): RowTreePresupuesto[] {
    const root: RowTreePresupuesto[] = [];
    presupuestos.forEach(element => {
      const keyAnualidad = `${element.anualidad}`;
      const keyEntidad = `${keyAnualidad}-${element?.solicitudProyectoEntidad?.id}`;
      let anualidad = this.mapTree.get(keyAnualidad);
      if (!anualidad) {
        anualidad = new RowTreePresupuesto(
          {
            anualidad: element.anualidad,
            importePresupuestado: 0,
            importeSolicitado: 0
          } as ISolicitudProyectoPresupuesto
        );
        this.mapTree.set(keyAnualidad, anualidad);
        root.push(anualidad);
      }
      let entidad = this.mapTree.get(keyEntidad);
      if (!entidad) {
        entidad = new RowTreePresupuesto(
          {
            solicitudProyectoEntidad: element.solicitudProyectoEntidad,
            importePresupuestado: 0,
            importeSolicitado: 0
          } as ISolicitudProyectoPresupuesto
        );
        this.mapTree.set(keyEntidad, entidad);
        anualidad.addChild(entidad);

        if (entidad.item.solicitudProyectoEntidad?.id) {
          this.solicitudProyectoEntidadService.findById(entidad.item.solicitudProyectoEntidad.id).pipe(
            switchMap(solicitudProyectoEntidad => {
              entidad.item.solicitudProyectoEntidad = solicitudProyectoEntidad;

              const empresaId = solicitudProyectoEntidad.solicitudProyectoEntidadFinanciadoraAjena?.empresa?.id
                ?? solicitudProyectoEntidad.convocatoriaEntidadFinanciadora?.empresa?.id
                ?? solicitudProyectoEntidad.convocatoriaEntidadGestora?.empresa?.id;

              if (empresaId) {
                return this.empresaService.findById(empresaId);
              }
              return of(null);
            })
          ).subscribe(
            (empresa) => {
              if (empresa) {
                if (entidad.item.solicitudProyectoEntidad.solicitudProyectoEntidadFinanciadoraAjena) {
                  entidad.item.solicitudProyectoEntidad.solicitudProyectoEntidadFinanciadoraAjena.empresa = empresa;
                } else if (entidad.item.solicitudProyectoEntidad.convocatoriaEntidadFinanciadora) {
                  entidad.item.solicitudProyectoEntidad.convocatoriaEntidadFinanciadora.empresa = empresa;
                } else if (entidad.item.solicitudProyectoEntidad.convocatoriaEntidadGestora) {
                  entidad.item.solicitudProyectoEntidad.convocatoriaEntidadGestora.empresa = empresa;
                }
              }
            }
          );
        }
      }
      entidad.addChild(new RowTreePresupuesto(
        {
          conceptoGasto: element.conceptoGasto,
          observaciones: element.observaciones,
          importePresupuestado: element.importePresupuestado,
          importeSolicitado: element.importeSolicitado
        } as ISolicitudProyectoPresupuesto
      ));
    });
    return root;
  }

  private toGlobal(presupuestos: ISolicitudProyectoPresupuesto[]): RowTreePresupuesto[] {
    const root: RowTreePresupuesto[] = [];
    presupuestos.forEach(element => {
      const keyAnualidad = `${element.anualidad}`;
      let anualidad = this.mapTree.get(keyAnualidad);
      if (!anualidad) {
        anualidad = new RowTreePresupuesto(
          {
            anualidad: element.anualidad,
            importePresupuestado: 0,
            importeSolicitado: 0
          } as ISolicitudProyectoPresupuesto
        );
        this.mapTree.set(keyAnualidad, anualidad);
        root.push(anualidad);
      }
      anualidad.addChild(new RowTreePresupuesto(
        {
          conceptoGasto: element.conceptoGasto,
          observaciones: element.observaciones,
          importePresupuestado: element.importePresupuestado,
          importeSolicitado: element.importeSolicitado
        } as ISolicitudProyectoPresupuesto
      ));
    });
    return root;
  }

  private addChilds(root: RowTreePresupuesto): RowTreePresupuesto[] {
    const childs: RowTreePresupuesto[] = [];
    childs.push(root);
    if (root.childs.length) {
      root.childs.forEach(child => {
        childs.push(...this.addChilds(child));
      });
    }
    return childs;
  }

  private sortRows(rows: ISolicitudProyectoPresupuesto[]): void {
    rows.sort((a, b) => {
      return b.anualidad.toLocaleString().localeCompare(a.anualidad.toLocaleString())
        || a.conceptoGasto?.nombre.localeCompare(b.conceptoGasto?.nombre);
    });
  }

  openExportModal(): void {
    const data: ISolicitudProyectoPresupuetoModalData = {
      solicitudId: this.data.idSolicitudProyecto
    };

    const config = {
      data
    };
    this.matDialog.open(SolicitudProyectoPresupuestoListadoExportModalComponent, config);
  }

}
