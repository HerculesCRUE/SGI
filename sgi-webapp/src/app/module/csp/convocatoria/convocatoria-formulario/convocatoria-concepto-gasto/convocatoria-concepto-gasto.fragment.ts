import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaConceptoGastoService } from '@core/services/csp/convocatoria-concepto-gasto.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ConvocatoriaConceptoGastoFragment extends FormFragment<IConvocatoriaConceptoGasto[]> {
  convocatoriaConceptoGastoPermitido$ = new BehaviorSubject<StatusWrapper<IConvocatoriaConceptoGasto>[]>([]);
  convocatoriaConceptoGastoNoPermitido$ = new BehaviorSubject<StatusWrapper<IConvocatoriaConceptoGasto>[]>([]);
  private convocatoriaConceptoGastoEliminados: StatusWrapper<IConvocatoriaConceptoGasto>[] = [];

  constructor(
    private fb: FormBuilder,
    key: number,
    private convocatoriaService: ConvocatoriaService,
    private convocatoriaConceptoGastoService: ConvocatoriaConceptoGastoService,
    public readonly: boolean,
    public canEdit: boolean
  ) {
    super(key, true);
    this.setComplete(true);
  }

  protected buildFormGroup(): FormGroup {
    const fb = this.fb.group({
      porcentajeCosteIndirecto: [null, [Validators.compose(
        [Validators.min(0), Validators.max(100)])]],
      costeIndirecto: [null, [IsEntityValidator.isValid()]]
    });
    return fb;
  }

  protected buildPatch(values: IConvocatoriaConceptoGasto[]): { [key: string]: any } {
    let porcentajeCosteIndirecto = null;
    let costeIndirecto = null;

    values.forEach(convocatoriaConceptoGasto => {
      if (convocatoriaConceptoGasto.porcentajeCosteIndirecto !== null) {
        costeIndirecto = convocatoriaConceptoGasto;
        porcentajeCosteIndirecto = convocatoriaConceptoGasto.porcentajeCosteIndirecto;
      }
    });

    this.convocatoriaConceptoGastoPermitido$.next(values.map(
      convocatoriaConceptoGastos => new StatusWrapper<IConvocatoriaConceptoGasto>(convocatoriaConceptoGastos))
    );
    return {
      porcentajeCosteIndirecto,
      costeIndirecto
    };
  }
  protected initializer(key: string | number): Observable<IConvocatoriaConceptoGasto[]> {
    if (key) {
      this.subscriptions.push(this.convocatoriaService.findAllConvocatoriaConceptoGastosNoPermitidos(key as number).pipe(
        map((response) => response.items)
      ).subscribe((convocatoriaConceptoGasto) => {
        this.convocatoriaConceptoGastoNoPermitido$.next(convocatoriaConceptoGasto.map(
          convocatoriaConceptoGastos => new StatusWrapper<IConvocatoriaConceptoGasto>(convocatoriaConceptoGastos))
        );
      }));

      return this.convocatoriaService.findAllConvocatoriaConceptoGastosPermitidos(key as number).pipe(
        map((response) => {
          if (response.items) {
            return response.items;
          }
        })
      );
    }
  }

  getValue(): IConvocatoriaConceptoGasto[] {
    throw new Error('Method not implemented');
  }

  addConvocatoriaConceptoGasto(convocatoriaConceptoGasto: IConvocatoriaConceptoGasto) {
    const wrapped = new StatusWrapper<IConvocatoriaConceptoGasto>(convocatoriaConceptoGasto);
    wrapped.setCreated();
    const permitido = wrapped.value.permitido;
    if (permitido) {
      const current = this.convocatoriaConceptoGastoPermitido$.value;
      current.push(wrapped);
      this.convocatoriaConceptoGastoPermitido$.next(current);
    } else {
      const current = this.convocatoriaConceptoGastoNoPermitido$.value;
      current.push(wrapped);
      this.convocatoriaConceptoGastoNoPermitido$.next(current);
    }
    this.setChanges(true);
  }

  deleteConvocatoriaConceptoGasto(wrapper: StatusWrapper<IConvocatoriaConceptoGasto>) {
    const permitido = wrapper.value.permitido;
    if (permitido) {
      const current = this.convocatoriaConceptoGastoPermitido$.value;
      const index = current.findIndex(
        (value: StatusWrapper<IConvocatoriaConceptoGasto>) => value === wrapper
      );
      if (index >= 0) {
        if (!wrapper.created) {
          this.convocatoriaConceptoGastoEliminados.push(current[index]);
        }
        current.splice(index, 1);
        this.convocatoriaConceptoGastoPermitido$.next(current);
        this.setChanges(true);
      }
    } else {
      const current = this.convocatoriaConceptoGastoNoPermitido$.value;
      const index = current.findIndex(
        (value: StatusWrapper<IConvocatoriaConceptoGasto>) => value === wrapper
      );
      if (index >= 0) {
        if (!wrapper.created) {
          this.convocatoriaConceptoGastoEliminados.push(current[index]);
        }
        current.splice(index, 1);
        this.convocatoriaConceptoGastoNoPermitido$.next(current);
        this.setChanges(true);
      }
    }
  }

  saveOrUpdate(): Observable<void> {
    this.applyCostesIndirectos();
    return merge(
      this.deleteConvocatoriaConceptoGastos(),
      this.updateConvocatoriaConceptoGastos(),
      this.createConvocatoriaConceptoGastos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteConvocatoriaConceptoGastos(): Observable<void> {
    if (this.convocatoriaConceptoGastoEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.convocatoriaConceptoGastoEliminados).pipe(
      mergeMap((wrapped) => {
        return this.convocatoriaConceptoGastoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.convocatoriaConceptoGastoEliminados = this.convocatoriaConceptoGastoEliminados.filter(
                deletedConvocatoriaConceptoGasto =>
                  deletedConvocatoriaConceptoGasto.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private createConvocatoriaConceptoGastos(): Observable<void> {
    const createdConvocatoriaConceptoGastos =
      this.convocatoriaConceptoGastoPermitido$.value.filter(
        (convocatoriaConceptoGastoPermitido) => convocatoriaConceptoGastoPermitido.created).concat(
          this.convocatoriaConceptoGastoNoPermitido$.value.filter(
            (convocatoriaConceptoGastoNoPermitido) => convocatoriaConceptoGastoNoPermitido.created)
        );
    if (createdConvocatoriaConceptoGastos.length === 0) {
      return of(void 0);
    }
    createdConvocatoriaConceptoGastos.forEach(
      (wrapper: StatusWrapper<IConvocatoriaConceptoGasto>) => wrapper.value.convocatoriaId = this.getKey() as number
    );
    return from(createdConvocatoriaConceptoGastos).pipe(
      mergeMap((wrappedConvocatoriaConceptoGastos) => {
        return this.convocatoriaConceptoGastoService.create(wrappedConvocatoriaConceptoGastos.value).pipe(
          map((updatedConvocatoriaConceptoGastos) => {
            const indexPermitido = this.convocatoriaConceptoGastoPermitido$.value.findIndex(
              (currentConvocatoriaConceptoGastos) => currentConvocatoriaConceptoGastos === wrappedConvocatoriaConceptoGastos);
            this.convocatoriaConceptoGastoPermitido$.value[indexPermitido] =
              new StatusWrapper<IConvocatoriaConceptoGasto>(updatedConvocatoriaConceptoGastos);
            this.convocatoriaConceptoGastoPermitido$.next(this.convocatoriaConceptoGastoPermitido$.value);

            const indexNoPermitido = this.convocatoriaConceptoGastoNoPermitido$.value.findIndex(
              (currentConvocatoriaConceptoGastos) => currentConvocatoriaConceptoGastos === wrappedConvocatoriaConceptoGastos);
            this.convocatoriaConceptoGastoNoPermitido$.value[indexNoPermitido] =
              new StatusWrapper<IConvocatoriaConceptoGasto>(updatedConvocatoriaConceptoGastos);
            this.convocatoriaConceptoGastoNoPermitido$.next(this.convocatoriaConceptoGastoNoPermitido$.value);
          })
        );
      }));
  }

  private updateConvocatoriaConceptoGastos(): Observable<void> {
    const updateConvocatoriaConceptoGastos = this.convocatoriaConceptoGastoPermitido$.value.filter(
      (convocatoriaConceptoGastoPermitido) => convocatoriaConceptoGastoPermitido.edited).concat(
        this.convocatoriaConceptoGastoNoPermitido$.value.filter(
          (convocatoriaConceptoGastoNoPermitido) => convocatoriaConceptoGastoNoPermitido.edited));
    if (updateConvocatoriaConceptoGastos.length === 0) {
      return of(void 0);
    }
    return from(updateConvocatoriaConceptoGastos).pipe(
      mergeMap((wrappedConvocatoriaConceptoGastos) => {
        return this.convocatoriaConceptoGastoService.update(
          wrappedConvocatoriaConceptoGastos.value.id, wrappedConvocatoriaConceptoGastos.value).pipe(
            map((updatedConvocatoriaConceptoGastos) => {
              const indexPermitido = this.convocatoriaConceptoGastoPermitido$.value.findIndex(
                (currentConvocatoriaConceptoGastos) => currentConvocatoriaConceptoGastos === wrappedConvocatoriaConceptoGastos);
              this.convocatoriaConceptoGastoPermitido$.value[indexPermitido] =
                new StatusWrapper<IConvocatoriaConceptoGasto>(updatedConvocatoriaConceptoGastos);

              const indexNoPermitido = this.convocatoriaConceptoGastoNoPermitido$.value.findIndex(
                (currentConvocatoriaConceptoGastos) => currentConvocatoriaConceptoGastos === wrappedConvocatoriaConceptoGastos);
              this.convocatoriaConceptoGastoNoPermitido$.value[indexNoPermitido] =
                new StatusWrapper<IConvocatoriaConceptoGasto>(updatedConvocatoriaConceptoGastos);
            })
          );
      })
    );
  }

  private applyCostesIndirectos() {
    if (this.getFormGroup()?.controls.costeIndirecto.value != null) {
      this.convocatoriaConceptoGastoPermitido$.value.forEach(wrapper => {
        if (wrapper.value.id === this.getFormGroup().controls.costeIndirecto.value.id
          && wrapper.value.porcentajeCosteIndirecto !== this.getFormGroup().controls.porcentajeCosteIndirecto.value) {
          wrapper.value.porcentajeCosteIndirecto = this.getFormGroup().controls.porcentajeCosteIndirecto.value;
          if (!wrapper.created) {
            wrapper.setEdited();
          }

          this.convocatoriaConceptoGastoPermitido$.value.filter(wrap =>
            wrap.value.id !== wrapper.value.id && wrap.value.porcentajeCosteIndirecto !== null)
            .map(
              wrapperBuscado => {
                wrapperBuscado.value.porcentajeCosteIndirecto = null;
                if (!wrapperBuscado.created) {
                  wrapperBuscado.setEdited();
                }
              });
        }
      });
    }
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.convocatoriaConceptoGastoPermitido$.value.some((wrapper) => wrapper.touched);
    return (this.convocatoriaConceptoGastoEliminados.length > 0 || touched);
  }
}
