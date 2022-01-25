import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { HttpProblem } from '@core/errors/http-problem';
import { IAnualidadGasto } from '@core/models/csp/anualidad-gasto';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { TranslateService } from '@ngx-translate/core';
import { from, Subscription } from 'rxjs';
import { map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoConsultaPresupuestoExportModalComponent } from './export/proyecto-consulta-presupuesto-export-modal.component';
import { IConsultaPresupuestoExportData } from './export/proyecto-consulta-presupuesto-export.service';
import { ProyectoConsultaPresupuestoFragment } from './proyecto-consulta-presupuesto.fragment';

const ANUALIDAD_GENERICA_KEY = marker('csp.proyecto-presupuesto.generica');

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

class RowTreePresupuesto extends RowTree<IAnualidadGasto> {

  constructor(item: IAnualidadGasto) {
    super(item);
  }

  compute(): void {
    if (this.childs.length) {
      this.childs.forEach(child => {
        child.compute();
      });
      this.childs.forEach(child => {
        this.item.importeConcedido += child.item.importeConcedido;
        this.item.importePresupuesto += child.item.importePresupuesto;
      });
    }
  }
}
const DIR_ASC = 1;

@Component({
  selector: 'sgi-proyecto-consulta-presupuesto',
  templateUrl: './proyecto-consulta-presupuesto.component.html',
  styleUrls: ['./proyecto-consulta-presupuesto.component.scss']
})
export class ProyectoConsultaPresupuestoComponent extends FragmentComponent implements OnInit, OnDestroy {
  public filterForm: FormGroup;
  private subscriptions: Subscription[] = [];
  formPart: ProyectoConsultaPresupuestoFragment;
  msgParamAnualidadGenerica: string;

  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesOne: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesEntidad: FxFlexProperties;

  displayedColumns = [
    'anualidad',
    'conceptoGasto',
    'aplicacionPresupuestaria',
    'codigoEconomico',
    'importePresupuesto',
    'importeConcedido'];
  dataSource: MatTableDataSource<RowTreePresupuesto> = new MatTableDataSource<RowTreePresupuesto>();
  private mapTree = new Map<string, RowTreePresupuesto>();

  constructor(
    public actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private readonly translate: TranslateService,
    private codigoEconomicoGastoService: CodigoEconomicoGastoService
  ) {
    super(actionService.FRAGMENT.CONSULTA_PRESUPUESTO, actionService);
    this.formPart = this.fragment as ProyectoConsultaPresupuestoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.initLayout();
    this.initFilterForm();

    this.subscriptions.push(this.formPart.anualidades$.subscribe(anualidades => {
      if (anualidades.length === 1 &&
        anualidades[0].anio == null) {
        this.filterForm.controls.anualidad.disable();
      }
    }
    ));

    this.subscriptions.push(
      this.formPart.anualidadesGastos$.pipe(
        map((gastosAnualidad: IAnualidadGasto[]) => {

          this.sortCollectionByProperty<IAnualidadGasto>(gastosAnualidad, DIR_ASC, 'codigoEconomico', 'id');
          this.sortCollectionByProperty<IAnualidadGasto>(gastosAnualidad, DIR_ASC, 'proyectoPartida', 'codigo');
          this.sortCollectionByProperty<IAnualidadGasto>(gastosAnualidad, DIR_ASC, 'conceptoGasto', 'nombre');
          this.sortCollectionByProperty<IAnualidadGasto>(gastosAnualidad, DIR_ASC, 'proyectoAnualidad', 'anio');

          const root = this.convertToRowTree(gastosAnualidad);
          const rows: RowTreePresupuesto[] = [];
          root.forEach(parent => {
            parent.compute();
            rows.push(...this.addChilds(parent));
          });
          return rows;
        })
      ).subscribe(presupuestos => {
        this.dataSource.data = presupuestos;
      }));
  }

  private setupI18N(): void {

    this.translate.get(
      ANUALIDAD_GENERICA_KEY,
    ).subscribe((value) => this.msgParamAnualidadGenerica = value);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private initFilterForm(): void {
    this.filterForm = new FormGroup({
      anualidad: new FormControl(null),
      aplicacion: new FormControl(null),
      concepto: new FormControl(null)
    });
  }

  private initLayout(): void {
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  displayerAnualidad(anualidad: IProyectoAnualidad): string {
    return anualidad.anio + '';
  }

  displayerAplicacion(partida: IProyectoPartida): string {
    return partida.codigo;
  }

  displayerConcepto(concepto: IAnualidadGasto): string {
    return concepto.conceptoGasto.nombre;
  }

  private convertToRowTree(anualidadesGastos: IAnualidadGasto[]): RowTreePresupuesto[] {

    const root: RowTreePresupuesto[] = [];
    this.mapTree.clear();
    anualidadesGastos.forEach(element => {
      const keyAnualidad = `${element.proyectoAnualidad?.anio}`;
      let anualidad = this.mapTree.get(keyAnualidad);
      if (!anualidad) {
        anualidad = new RowTreePresupuesto(
          {
            proyectoAnualidad: element.proyectoAnualidad,
            importePresupuesto: 0,
            importeConcedido: 0
          } as IAnualidadGasto
        );
        this.mapTree.set(keyAnualidad, anualidad);
        root.push(anualidad);
      }

      const conceptoGastoTree = this.resolveConceptoGastoGroupingParent(element, anualidad);

      const rowTree = new RowTreePresupuesto({
        codigoEconomico: element.codigoEconomico,
        proyectoPartida: element.proyectoPartida,
        importePresupuesto: element.importePresupuesto,
        importeConcedido: element.importeConcedido
      } as IAnualidadGasto);
      this.resolveCodigoEconomico(rowTree);
      conceptoGastoTree.addChild(rowTree);
    });

    return root;
  }

  private resolveCodigoEconomico(rowTree: RowTreePresupuesto) {
    if (rowTree.item.codigoEconomico?.id) {
      this.subscriptions.push(
        this.codigoEconomicoGastoService
          .findById(rowTree.item.codigoEconomico.id).subscribe(codigo => rowTree.item.codigoEconomico = codigo));
    }
  }

  private resolveConceptoGastoGroupingParent(
    element: IAnualidadGasto,
    proyectoAgrupacionTree: RowTreePresupuesto): RowTreePresupuesto {

    const keyConceptoGastoTree = `${element.proyectoAnualidad.anio}-concepto-${element.conceptoGasto.id}`;
    let conceptoGastoTree = this.mapTree.get(keyConceptoGastoTree);
    if (!conceptoGastoTree) {
      conceptoGastoTree = new RowTreePresupuesto({
        conceptoGasto: element.conceptoGasto,
        importePresupuesto: 0,
        importeConcedido: 0
      } as IAnualidadGasto
      );
      this.mapTree.set(keyConceptoGastoTree, conceptoGastoTree);
      proyectoAgrupacionTree.addChild(conceptoGastoTree);
    }
    return conceptoGastoTree;
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

  private sortCollectionByProperty<T>(collection: T[], direction: number, property: string, nestedProperty?: string): void {
    collection.sort((a: T, b: T) => {
      if (a[property] && b[property]) {
        const aProp = nestedProperty ? a[property][nestedProperty] : a[property];
        const bProp = nestedProperty ? b[property][nestedProperty] : b[property];
        if (aProp === bProp) {
          return 0;
        }
        return aProp > bProp ? direction : -direction;
      } else {
        if (b[property]) {
          return -direction;
        } else {
          return direction;
        }
      }
    });
  }

  public filterTable(): void {
    let filteredAnualidadesGasto: IAnualidadGasto[] = [...this.formPart.pureAnualidadesGastos];
    const form = this.filterForm.controls;

    if (form.anualidad.value) {
      filteredAnualidadesGasto = filteredAnualidadesGasto
        .filter(anualidad => anualidad.proyectoAnualidad.anio === form.anualidad.value.anio);
    }

    if (form.aplicacion.value) {
      filteredAnualidadesGasto = filteredAnualidadesGasto
        .filter(anualidad => anualidad.proyectoPartida.codigo === form.aplicacion.value.codigo);
    }

    if (form.concepto.value) {
      filteredAnualidadesGasto = filteredAnualidadesGasto
        .filter(anualidad => anualidad.conceptoGasto.nombre === form.concepto.value.conceptoGasto.nombre);
    }

    this.formPart.anualidadesGastos$.next(filteredAnualidadesGasto);
  }

  openExportModal(): void {

    this.subscriptions.push(
      this.formPart.anualidadesGastos$.pipe(
        map((gastosAnualidad: IAnualidadGasto[]) => {

          this.sortCollectionByProperty<IAnualidadGasto>(gastosAnualidad, DIR_ASC, 'codigoEconomico', 'id');
          this.sortCollectionByProperty<IAnualidadGasto>(gastosAnualidad, DIR_ASC, 'proyectoPartida', 'codigo');
          this.sortCollectionByProperty<IAnualidadGasto>(gastosAnualidad, DIR_ASC, 'conceptoGasto', 'nombre');
          this.sortCollectionByProperty<IAnualidadGasto>(gastosAnualidad, DIR_ASC, 'proyectoAnualidad', 'anio');

          return gastosAnualidad;
        }),
        switchMap((anualidadesGastos) => {
          return from(anualidadesGastos).pipe(
            mergeMap(anualidadGasto => {
              return this.codigoEconomicoGastoService.findById(anualidadGasto.codigoEconomico.id).pipe(
                map((value) => {
                  anualidadGasto.codigoEconomico = value;
                  return anualidadesGastos;
                })
              );
            }),
            takeLast(1)
          );
        })
      ).subscribe(
        (anualidadesGastos) => {
          const exportData: IConsultaPresupuestoExportData = {
            data: anualidadesGastos,
            columns: []
          };
          const config = {
            data: exportData
          };
          this.matDialog.open(ProyectoConsultaPresupuestoExportModalComponent, config);
        },
        (error) => {
          if (error instanceof HttpProblem) {
            this.formPart.pushProblems(error);
          }
        })
    );
  }
}
