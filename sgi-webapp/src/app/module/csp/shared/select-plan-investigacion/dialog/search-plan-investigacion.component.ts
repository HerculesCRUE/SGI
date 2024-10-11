import { NestedTreeControl } from '@angular/cdk/tree';
import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IPrograma } from '@core/models/csp/programa';
import { ProgramaService } from '@core/services/csp/programa.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, from, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast } from 'rxjs/operators';

export interface SearchPlanInvestigacionModalData {
  plan: IPrograma;
  programa: IPrograma;
  readonly: boolean;
}

class NodePrograma {
  parent: NodePrograma;
  programa: IPrograma;
  // tslint:disable-next-line: variable-name
  _childs: NodePrograma[];
  get childs(): NodePrograma[] {
    return this._childs;
  }

  constructor(programa: IPrograma) {
    this.programa = programa;
    this._childs = [];
  }

  addChild(child: NodePrograma) {
    child.parent = this;
    child.programa.padre = this.programa;
    this._childs.push(child);
    this.sortChildsByName();
  }

  removeChild(child: NodePrograma) {
    this._childs = this._childs.filter((programa) => programa !== child);
  }

  sortChildsByName(): void {
    this._childs = sortByName(this._childs);
  }
}

function sortByName(nodes: NodePrograma[]): NodePrograma[] {
  return nodes.sort((a, b) => {
    if (a.programa.nombre < b.programa.nombre) {
      return -1;
    }
    if (a.programa.nombre > b.programa.nombre) {
      return 1;
    }
    return 0;
  });
}

@Component({
  templateUrl: './search-plan-investigacion.component.html',
  styleUrls: ['./search-plan-investigacion.component.scss']
})
export class SearchPlanInvestigacionModalComponent extends DialogFormComponent<SearchPlanInvestigacionModalData> implements OnInit {

  programaTree$ = new BehaviorSubject<NodePrograma[]>([]);
  treeControl = new NestedTreeControl<NodePrograma>(node => node.childs);
  dataSource = new MatTreeNestedDataSource<NodePrograma>();
  private nodeMap = new Map<number, NodePrograma>();

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  checkedNode: NodePrograma;
  hasChild = (_: number, node: NodePrograma) => node.childs.length > 0;

  constructor(
    private readonly logger: NGXLogger,
    matDialogRef: MatDialogRef<SearchPlanInvestigacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SearchPlanInvestigacionModalData,
    private programaService: ProgramaService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, false);
  }

  ngOnInit() {
    super.ngOnInit();
    this.subscriptions.push(this.programaTree$.subscribe(
      (programas) => {
        this.dataSource.data = programas;
      }
    ));
    this.subscriptions.push(this.formGroup.get('plan').valueChanges.subscribe(
      (value) => {
        // Reset selected node on first user change
        if (value?.id !== this.data.plan?.id) {
          this.formGroup.get('programa').setValue(undefined);
          this.checkedNode = undefined;
        }
        this.loadTreePrograma(value?.id);
      })
    );
    this.loadTreePrograma(this.data.plan?.id);
  }

  private loadTreePrograma(programaId: number) {
    if (programaId) {
      this.checkedNode = undefined;
      const subscription = this.programaService.findAllHijosPrograma(programaId).pipe(
        switchMap(response => {
          this.programaTree$.next([]);
          this.nodeMap.clear();

          return from(response.items).pipe(
            mergeMap((programa) => {
              const node = new NodePrograma(programa);
              this.nodeMap.set(node.programa.id, node);
              return this.getChilds(node).pipe(map(() => node));
            })
          );
        })
      ).subscribe(
        (programa) => {
          const current = this.programaTree$.value;
          current.push(programa);
          this.publishNodes(current);
        },
        (error) => {
          this.logger.error(error);
        },
        () => {
          this.checkedNode = this.nodeMap.get(this.formGroup.get('programa').value);
          if (this.checkedNode) {
            this.expandNodes(this.checkedNode);
          }
        }
      );
      this.subscriptions.push(subscription);
    }
    else {
      this.programaTree$.next([]);
      this.nodeMap.clear();
    }
  }

  private getChilds(parent: NodePrograma): Observable<NodePrograma[]> {
    return this.programaService.findAllHijosPrograma(parent.programa.id).pipe(
      map((result) => {
        const childs: NodePrograma[] = result.items.map(
          (programa) => {
            const child = new NodePrograma(programa);
            child.parent = parent;
            this.nodeMap.set(child.programa.id, child);
            return child;
          });
        return childs;
      }),
      switchMap((nodes) => {
        parent.childs.push(...nodes);
        parent.sortChildsByName();
        if (nodes.length > 0) {
          return from(nodes).pipe(
            mergeMap((node) => {
              return this.getChilds(node);
            })
          );
        }
        return of([]);
      }),
      takeLast(1)
    );
  }

  private expandNodes(node: NodePrograma) {
    if (node && node.parent) {
      this.treeControl.expand(node.parent);
      this.expandNodes(node.parent);
    }
  }

  private publishNodes(rootNodes?: NodePrograma[]) {
    let nodes = rootNodes ? rootNodes : this.programaTree$.value;
    nodes = sortByName(nodes);
    this.programaTree$.next(nodes);
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      plan: new FormControl(this.data.plan),
      programa: new FormControl(this.data.programa?.id)
    });

    return formGroup;
  }

  protected getValue(): SearchPlanInvestigacionModalData {
    this.data.plan = this.formGroup.get('plan').value;
    this.data.programa = this.checkedNode?.programa;

    return this.data;
  }

  onCheckNode(node: NodePrograma, $event: MatCheckboxChange): void {
    this.checkedNode = $event.checked ? node : undefined;
    this.formGroup.get('programa').setValue(this.checkedNode?.programa);
  }

  doAction(): void {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      this.close(this.getValue());
    }
  }

}
