import { FormGroup } from '@angular/forms';
import { IApartado } from '@core/models/eti/apartado';
import { IBloque } from '@core/models/eti/bloque';
import { IComentario } from '@core/models/eti/comentario';
import { IComite } from '@core/models/eti/comite';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { FORMULARIO, resolveFormularioByTipoEvaluacionAndComite } from '@core/models/eti/formulario';
import { IMemoria } from '@core/models/eti/memoria';
import { IRespuesta } from '@core/models/eti/respuesta';
import { ITarea } from '@core/models/eti/tarea';
import { ITipoDocumento } from '@core/models/eti/tipo-documento';
import { ESTADO_MEMORIA } from '@core/models/eti/tipo-estado-memoria';
import { TIPO_EVALUACION } from '@core/models/eti/tipo-evaluacion';
import { Module } from '@core/module';
import { Fragment, Group } from '@core/services/action-service';
import { ApartadoService } from '@core/services/eti/apartado.service';
import { BloqueService } from '@core/services/eti/bloque.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { FormularioService } from '@core/services/eti/formulario.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { RespuestaService } from '@core/services/eti/respuesta.service';
import { DatosAcademicosService } from '@core/services/sgp/datos-academicos.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { FormlyFormOptions } from '@ngx-formly/core';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of, zip } from 'rxjs';
import { catchError, endWith, map, mergeAll, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface IBlock {
  bloque: IBloque;
  formlyData: IFormlyData;
  questions: IQuestion[];
  loaded$: BehaviorSubject<boolean>;
  selected: boolean;
}

interface IQuestion {
  apartado: IApartadoWithRespuestaAndComentario;
  childs: IQuestion[];
}

interface IFormlyData {
  formGroup: FormGroup;
  fields: SgiFormlyFieldConfig[];
  options: FormlyFormOptions;
  model: any;
}

interface IApartadoWithRespuestaAndComentario extends IApartado {
  respuesta: IRespuesta;
  comentario: IComentario;
  respuestaAnterior: IRespuesta;
}

export abstract class MemoriaFormlyFormFragment extends Fragment {

  protected comite: IComite;
  protected memoria: IMemoria;
  protected tareas: ITarea[];
  private comentarios: Map<number, IComentario>;
  private _comentariosGenerales: IComentario[] = [];

  public blocks$: BehaviorSubject<IBlock[]> = new BehaviorSubject<IBlock[]>([]);
  public selectedIndex$: BehaviorSubject<number> = new BehaviorSubject<number>(undefined);

  private fieldsDocumentacion = new Map<number, SgiFormlyFieldConfig>();

  private readonly: boolean;

  // Almacena el formState de todos los bloques para poder hacer referencia entre ellos
  private formStateGlobal: any = {};

  private lastCompletedBlock: number;

  private formularioTipo: FORMULARIO;

  get comentariosGenerales(): IComentario[] {
    return this._comentariosGenerales;
  }

  isReadonly(): boolean {
    return this.readonly;
  }

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    comite: IComite,
    readonly: boolean,
    private tipoEvaluacion: TIPO_EVALUACION,
    private readonly moduloInv: boolean,
    protected formularioService: FormularioService,
    protected memoriaService: MemoriaService,
    protected evaluacionService: EvaluacionService,
    protected bloqueService: BloqueService,
    protected respuestaService: RespuestaService,
    protected peticionEvaluacionService: PeticionEvaluacionService,
    protected vinculacionService: VinculacionService,
    protected datosAcademicosService: DatosAcademicosService,
    protected personaService: PersonaService,
    protected apartadoService: ApartadoService
  ) {
    super(key);
    this.comite = comite;
    this.readonly = readonly;
    this.memoria = {} as IMemoria;
    this.tareas = [];

    this.subscriptions.push(this.selectedIndex$.subscribe(
      (index) => {
        if (index !== undefined) {
          this.loadBlock(index);
        }
      }
    ));
  }

  protected abstract isEditable(): boolean;

  protected loadTareas(idMemoria: number, idPeticionEvaluacion: number): Observable<ITarea[]> {

    let tareas$: Observable<ITarea[]> = of([]);
    tareas$ = this.peticionEvaluacionService.findTareas(idPeticionEvaluacion).pipe(
      map((tareas) => {
        return tareas.items.filter(tarea => tarea.memoria.id === idMemoria);
      }),
      mergeMap(tareas => {
        if (tareas.length === 0) {
          return of([]);
        }
        return from(tareas).pipe(
          map((element) => {
            return this.personaService.findById(element.equipoTrabajo.persona.id).pipe(
              map((persona) => {
                element.equipoTrabajo.persona = persona;
                return element;
              }),
              switchMap(() => {
                return this.vinculacionService.findByPersonaId(element.equipoTrabajo.persona.id).pipe(
                  map((vinculacion) => {
                    element.equipoTrabajo.persona.vinculacion = vinculacion;
                    return element;
                  }),
                  catchError(() => of(element))
                );
              }),
              switchMap(() => {
                return this.datosAcademicosService.findByPersonaId(element.equipoTrabajo.persona.id).pipe(
                  map((datosAcademicos) => {
                    element.equipoTrabajo.persona.datosAcademicos = datosAcademicos;
                    return element;
                  }),
                  catchError(() => of(element))
                );
              }),
              catchError(() => of(element))
            );
          }),
          mergeAll(),
          map(() => tareas)
        );
      })
    );

    return tareas$;
  }

  protected onInitialize(): Observable<void> {
    if (this.getKey() && this.comite) {
      return this.memoriaService.findById(this.getKey() as number).pipe(
        map((memoria) => {
          this.memoria = memoria;
          if (!this.isEditable() && !this.readonly) {
            this.readonly = true;
          }
          return memoria;
        }),
        switchMap((memoria) => {
          return this.loadTareas(memoria.id, memoria.peticionEvaluacion.id).pipe(
            map((tareas) => {
              this.tareas = tareas;
              return memoria;
            }));
        }),
        switchMap((memoria) => {
          return this.loadComentarios(memoria.id, this.tipoEvaluacion).pipe(
            map((comentarios) => {
              this.comentarios = comentarios;
              return void 0;
            }));
        }),
        switchMap(() => {
          return this.respuestaService.findLastByMemoriaId(this.memoria.id).pipe(
            map(respuesta => {
              if (!!respuesta?.id) {
                this.lastCompletedBlock = respuesta.apartado.bloque.orden - 1;
              }
              else {
                this.lastCompletedBlock = 0;
              }
            })
          );
        }),
        switchMap(() => {
          return this.loadFormulario(this.tipoEvaluacion, this.comite);
        })
      );
    }
    else {
      return of(void 0);
    }
  }

  public getLastFilledBlockIndex(): number {
    let inMemory = 0;
    this.blocks$.value.forEach((block, index) => {
      if (block.loaded$.value) {
        inMemory = index;
      }
    });
    if (this.lastCompletedBlock > inMemory) {
      return this.lastCompletedBlock;
    }
    return inMemory;
  }

  public performChecks(markAllTouched?: boolean) {
    this.blocks$.value.forEach((block) => {
      if (block.loaded$.value) {
        block.formlyData.fields.forEach((field) => {
          if (field.group) {
            field.group.forceUpdate(markAllTouched);
          }
        });
      }
    });
  }

  saveOrUpdate(): Observable<void> {
    const respuestas: IRespuesta[] = [];
    let hasLastBloqueSavedRespuestas = false;
    let formLoadComplete = true;

    this.blocks$.value.forEach((block, index) => {
      const respuestasBloque = this.getRespuestasBloque(block);
      respuestas.push(...respuestasBloque);

      formLoadComplete = formLoadComplete && block.loaded$.value;

      if (index === this.blocks$.value.length - 1) {
        hasLastBloqueSavedRespuestas = this.hasBloqueSavedRespuestas(block, respuestasBloque);
      }
    });

    if (respuestas.length === 0) {
      return of(void 0);
    }

    return merge(
      this.updateRespuestas(respuestas.filter((respuesta) => respuesta.id !== undefined)),
      this.createRespuestas(respuestas.filter((respuesta) => respuesta.id === undefined)),
    ).pipe(
      takeLast(1),
      switchMap(() => {
        if (this.isFormularioM20()) {
          const respuestaEvaluacionRetrospectiva = respuestas.find(respuesta =>
            this.isRespuestaEvaluacionRetrospectivaAndFilled(respuesta));

          if (!!respuestaEvaluacionRetrospectiva) {
            return this.respuestaService.updateDatosRetrospectiva(respuestaEvaluacionRetrospectiva.id);
          }
        }

        return of(void 0);
      }),
      switchMap(() => {
        this.refreshBlockChanges();

        if ((formLoadComplete && !hasLastBloqueSavedRespuestas) || this.isFormularioMemoriaModificacion(this.memoria) || this.memoria.estadoActual.id === ESTADO_MEMORIA.SUBSANACION) {
          return this.formularioService.completado(this.memoria.id, this.formularioTipo);
        }

        return of(void 0);
      })
    );
  }

  public refreshMemoria(memoria: IMemoria): void {
    Object.assign(this.memoria, memoria);
    const lastBlock = this.getLastFilledBlockIndex();
    this.blocks$.value.forEach((block, index) => {
      if (index <= lastBlock) {
        if (block.loaded$.value) {
          // Asumimos que siempre que se refresque el valor, y se haya cargado algo es porque hay un cambio. 
          // Aunque no sabemos si tras un cambio ha retornado al valor original. Al menos el usuario tendrá feedback.
          this.setChanges(true);
          this.refreshFormlyModelValues(true, block.formlyData.model, block.formlyData.options.formState, block.formlyData.fields, block.questions);
        }
      }
    });
  }

  private refreshFormlyModelValues(
    firstLevel: boolean,
    model: any,
    formState: any,
    formlyFieldConfig: SgiFormlyFieldConfig[],
    questions: IQuestion[]
  ): void {
    questions.forEach(question => {
      const firstFieldConfig = question.apartado.esquema ? question.apartado.esquema[0] : {};
      const key = firstFieldConfig.key as string;
      const fieldConfig = firstFieldConfig.fieldGroup;
      if (firstLevel && key) {
        if (this.isEditable()) {
          this.evalExpressionModelValue(question.apartado.esquema, model[key], formState);
        }
        if (question.childs.length) {
          const isFirstLevel = !firstLevel ? question.childs.length > 0 : firstLevel;
          this.refreshFormlyModelValues(isFirstLevel, key ? model[key] : model, formState, fieldConfig ? fieldConfig : formlyFieldConfig, question.childs);
        }
      }
    });
  }

  private getRespuestas(question: IQuestion): IRespuesta[] {
    const respuestas: IRespuesta[] = [];
    let respuesta = {};
    question.apartado.esquema.forEach((field) => {
      respuesta = Object.assign(respuesta, field.model);
    });
    question.apartado.respuesta.valor = respuesta;
    respuestas.push(question.apartado.respuesta);

    if (!question.apartado.respuesta.id) {
      question.apartado.respuesta.memoria = { id: this.getKey() as number } as IMemoria;
      question.apartado.respuesta.apartado = { id: question.apartado.id } as IApartado;
    }

    const fieldDocumentacion = this.fieldsDocumentacion.get(question.apartado.id);
    if (fieldDocumentacion) {
      const id = fieldDocumentacion.model[fieldDocumentacion.key as string];
      if (id) {
        question.apartado.respuesta.tipoDocumento = { id } as ITipoDocumento;
      }
      else {
        question.apartado.respuesta.tipoDocumento = null;
      }
    }
    else {
      question.apartado.respuesta.tipoDocumento = null;
    }
    question.childs.forEach((child) => {
      respuestas.push(...this.getRespuestas(child));
    });
    return respuestas;
  }

  private createRespuestas(respuestas: IRespuesta[]): Observable<void> {
    return from(respuestas).pipe(
      mergeMap((respuesta) => {
        return this.respuestaService.create(respuesta).pipe(
          map((updated) => {
            Object.assign(respuesta, updated);
          })
        );
      }),
      endWith()
    );
  }

  private updateRespuestas(respuestas: IRespuesta[]): Observable<void> {
    return from(respuestas).pipe(
      mergeMap((respuesta) => {
        return this.respuestaService.update(respuesta.id, respuesta).pipe(
          map((updated) => {
            Object.assign(respuesta, updated);
          })
        );
      }),
      endWith()
    );
  }

  private loadFormulario(tipoEvaluacion: TIPO_EVALUACION, comite: IComite): Observable<void> {
    this.formularioTipo = resolveFormularioByTipoEvaluacionAndComite(tipoEvaluacion, comite);
    return this.formularioService.findById(this.formularioTipo).pipe(
      switchMap((formulario) => {
        return this.formularioService.getBloques(formulario.id);
      }),
      map((response) => {
        return this.toBlocks(response.items);
      }),
      tap(
        (blocks) => {
          this.blocks$.next(blocks);
          this.loadBlock(this.selectedIndex$.value);
        }, (error) => {
          this.logger.error(error);
        }),
      map(() => {
        return void 0;
      })
    );
  }

  /**
   * Convert an array of IApartadoRespuesta to an array o IBlock
   *
   * @param apartadosRespuesta The array of IApartadoRespuesta to convert
   */
  private toBlocks(bloques: IBloque[]): IBlock[] {
    const blocks: IBlock[] = [];
    const bloqueModels: any[] = [];

    bloques.forEach((bloque) => {
      const block: IBlock = {
        bloque,
        formlyData: {
          formGroup: new FormGroup({}),
          fields: [],
          model: {},
          options: {}
        },
        questions: [],
        selected: false,
        loaded$: new BehaviorSubject<boolean>(false)
      };
      bloqueModels[bloque.orden] = block.formlyData.model;
      block.formlyData.options.formState = {
        mainModel: block.formlyData.model,
        memoria: this.memoria,
        tareas: this.tareas,
        bloques: bloqueModels
      };
      blocks.push(block);
    });
    return blocks;
  }

  private loadComentarios(idMemoria: number, tipoEvaluacion: TIPO_EVALUACION): Observable<Map<number, IComentario>> {
    return this.memoriaService.getEvaluacionesMemoria(idMemoria).pipe(
      map((response) => {
        return response.items.
          filter(e => (e.tipoEvaluacion.id as TIPO_EVALUACION) === tipoEvaluacion).
          reduce((prev, current) => {
            if (prev.version <= current.version) {
              return current;
            }
            return prev;
          }, { version: -1 } as IEvaluacion);
      }),
      switchMap((value) => {
        if (!value.id) {
          return of([] as IComentario[]);
        }
        return this.evaluacionService.getComentariosGestor(value.id).pipe(map(response => response));
      }),
      tap((comentarios) => {
        this._comentariosGenerales = comentarios.filter(c => !!!c.apartado.bloque.formulario?.id);
      }),
      map((comentarios) => {
        const apartadoComentario = new Map<number, IComentario>();
        comentarios
          .filter(c => !!c.apartado.bloque.formulario?.id)
          .forEach((comentario) => apartadoComentario.set(comentario.apartado.id, comentario));
        return apartadoComentario;
      })
    );
  }

  /**
   * Load all questions, including responses, of an block by their index in blocks$. 
   *
   * Only is loaded if have been loaded previously.
   *
   * @param index Index to load
   */
  private loadBlock(index: number): void {
    const block = this.blocks$.value[index];
    if (block && !block.loaded$.value) {
      this.bloqueService.getApartados(block.bloque.id).pipe(
        map((apartados) => {
          return apartados.items.map((ap) => ap as IApartadoWithRespuestaAndComentario);
        }),
        map((apartadosRespuestas) => {
          apartadosRespuestas.forEach((apartado) => apartado.comentario = this.comentarios.get(apartado.id));
          return apartadosRespuestas;
        }),
        switchMap((apartadosRespuesta) => {
          const respuestasApartados: Observable<IApartadoWithRespuestaAndComentario>[] = [];
          apartadosRespuesta.forEach(apartado => {
            respuestasApartados.push(this.respuestaService.findByMemoriaIdAndApartadoId(this.getKey() as number, apartado.id).pipe(
              map((respuesta) => {
                apartado.respuesta = respuesta ? respuesta : { valor: {} } as IRespuesta;
                return apartado;
              }),
              switchMap((aptdo) => {
                if (this.memoria.memoriaOriginal) {
                  return this.respuestaService.findByMemoriaIdAndApartadoId(this.memoria.memoriaOriginal.id, apartado.id).pipe(
                    map((respuesta) => {
                      aptdo.respuestaAnterior = respuesta ? respuesta : { valor: {} } as IRespuesta;
                      return aptdo;
                    }));
                } else {
                  return of(aptdo);
                }
              }),
            ));
          });
          return zip(...respuestasApartados);
        }),
        switchMap((apartados) => {
          return this.fillBlock(block, apartados);
        })
      ).subscribe(
        (value) => {
          this.fillFormlyData(true, value.formlyData.model, value.formlyData.options.formState, value.formlyData.fields, value.questions);

          this.formStateGlobal = { ...this.formStateGlobal, ...block.formlyData.model };
          block.formlyData.options.formState.mainModel = this.formStateGlobal;

          value.formlyData.fields.forEach((f) => {
            if (f.group) {
              this.subscriptions.push(f.group.status$.subscribe((status) => {
                this.evalStatusChange();
              }));
            }
          });
          block.selected = true;
          block.loaded$.next(true);
        }
      );
    }
    else if (block) {
      block.selected = true;
    }
  }

  /**
   * Fill a block with received apartados, also load childs of apartados because received apartados are considered parents.
   *
   * The apartados are converted to questions, and ordered according to their orden field
   *
   * @param apartados The list of parent apartado  of the block
   */
  private fillBlock(block: IBlock, apartados: IApartadoWithRespuestaAndComentario[]): Observable<IBlock> {
    const firstLevel = apartados
      .sort((a, b) => a.orden - b.orden)
      .map((apartadoRespuesta) => {
        const iQuestion: IQuestion = {
          apartado: apartadoRespuesta,
          childs: [],
        };
        return iQuestion;
      });
    block.questions.push(...firstLevel);
    return from(firstLevel).pipe(
      mergeMap((question) => {
        return this.getQuestionChilds(question).pipe(
          map((childs) => {
            question.childs.push(...childs);
            return block;
          }));
      }),
      endWith(block)
    );
  }

  /**
   * Recursively get childs of a question.
   *
   * @param question The question where the childs are searched
   */
  private getQuestionChilds(question: IQuestion): Observable<IQuestion[]> {
    return this.apartadoService.getHijos(question.apartado.id).pipe(
      map((apartados) => {
        return apartados.items.map((ap) => ap as IApartadoWithRespuestaAndComentario);
      }),
      map((apartadosRespuestas) => {
        apartadosRespuestas.forEach((apartado) => apartado.comentario = this.comentarios.get(apartado.id));
        return apartadosRespuestas;
      }),
      switchMap((apartadosRespuesta) => {
        const respuestasApartados: Observable<IApartadoWithRespuestaAndComentario>[] = [];
        apartadosRespuesta.forEach(apartado => {
          respuestasApartados.push(this.respuestaService.findByMemoriaIdAndApartadoId(this.getKey() as number, apartado.id).pipe(
            map((respuesta) => {
              apartado.respuesta = respuesta ? respuesta : { valor: {} } as IRespuesta;
              return apartado;
            })
          ));
        });
        return zip(...respuestasApartados);
      }),
      map((hijos) => {
        return hijos
          .sort((a, b) => a.orden - b.orden)
          .map((hijo) => {
            const iQuestion: IQuestion = {
              apartado: hijo,
              childs: [],
            };
            return iQuestion;
          });
      }),
      switchMap((value) => {
        question.childs.push(...value);
        if (value.length > 0) {
          return from(value).pipe(
            mergeMap((iq) => {
              return this.getQuestionChilds(iq);
            })
          );
        }
        else {
          return of([]);
        }
      })
    );
  }

  /**
   * Fill the Formly data recursively. The received questions are loaded as nested fieldConfigs
   * of the provided fieldConfig.
   *
   * @param firstLevel Indicates if it's the first iteration
   * @param formlyFieldConfig The Formly field config onto load questions
   * @param questions  The questions to load
   */
  private fillFormlyData(
    firstLevel: boolean,
    model: any,
    formState: any,
    formlyFieldConfig: SgiFormlyFieldConfig[],
    questions: IQuestion[]
  ): void {
    questions.forEach(question => {
      const firstFieldConfig = question.apartado.esquema ? question.apartado.esquema[0] : {};
      const key = firstFieldConfig.key as string;
      const fieldConfig = firstFieldConfig.fieldGroup;
      if (firstLevel && key) {
        model[key] = question.apartado.respuesta.valor;
        if (this.isEditable()) {
          this.evalExpressionModelValue(question.apartado.esquema, model[key], formState);
        }
      }
      else {
        model = Object.assign(model, question.apartado.respuesta.valor);
      }
      if (!firstFieldConfig.templateOptions) {
        firstFieldConfig.templateOptions = {};
      }
      firstFieldConfig.templateOptions.comentario = question.apartado.comentario;
      firstFieldConfig.templateOptions.modified = Boolean(question.apartado.respuestaAnterior) ? this.isRespuestaApartadoModified(question.apartado.respuesta, question.apartado.respuestaAnterior) : false;
      firstFieldConfig.group = new Group();

      this.evalExpressionLock(firstFieldConfig, model, formState);

      this.replaceTemplatePlaceholders(firstFieldConfig, formState);

      if (
        this.readonly
        || (!this.readonly && (this.comentarios.size || this.comentariosGenerales.length) && !question.apartado.comentario)
        || (firstFieldConfig.templateOptions.locked)
      ) {
        firstFieldConfig.templateOptions.locked = true;
        if (firstFieldConfig.fieldGroup) {
          firstFieldConfig.fieldGroup.forEach((esquema) => {
            if (esquema.templateOptions) {
              esquema.templateOptions.disabled = true;
            }
            else {
              esquema.templateOptions = {
                disabled: true
              };
            }
          });
        }
      }
      const fieldsDocumentacion = this.getFieldsDocumentacion(question.apartado.esquema);
      if (fieldsDocumentacion.length) {
        if (fieldsDocumentacion.length > 1) {
          throw Error('Un apartado no puede contener más de un campo de tipo documento');
        }
        this.fieldsDocumentacion.set(question.apartado.id, fieldsDocumentacion[0]);
      }
      formlyFieldConfig.push(...question.apartado.esquema);
      if (question.childs.length) {
        const isFirstLevel = !firstLevel ? question.childs.length > 0 : firstLevel;
        this.fillFormlyData(isFirstLevel, key ? model[key] : model, formState, fieldConfig ? fieldConfig : formlyFieldConfig, question.childs);
      }
    });
  }

  private getFieldsDocumentacion(formlyFieldConfig: SgiFormlyFieldConfig[]): SgiFormlyFieldConfig[] {
    const fields: SgiFormlyFieldConfig[] = [];
    if (formlyFieldConfig.length) {
      formlyFieldConfig.forEach(field => {
        if (field.type === 'documento') {
          fields.push(field);
        }
        if (field.fieldGroup) {
          fields.push(...this.getFieldsDocumentacion(field.fieldGroup));
        }
      });
    }
    return fields;
  }

  private evalStatusChange(): void {
    let changes = false;
    let errors = false;
    this.blocks$.value.forEach((block) => {
      block.formlyData.fields.forEach((f) => {
        if (f.group) {
          changes = changes || f.group.hasChanges();
          errors = errors || f.group.hasErrors();
        }
      });
    });
    this.setChanges(changes);
    this.setErrors(errors);
  }

  private evalExpressionModelValue(fieldConfig: SgiFormlyFieldConfig[], model: any, formState: any, parentKey?: string) {
    fieldConfig.forEach(fg => {
      if (fg.key && fg.templateOptions?.expressionModelValue) {
        if (parentKey) {
          this.evalKeyParentExpressionModelValue(model, parentKey, fg, formState);
        } else {
          const f = this.evalStringExpression(fg.templateOptions.expressionModelValue, ['model', 'formState', 'field']);
          model[fg.key as string] = this.evalExpression(f, { fg }, [{ model }, formState, fg]);
        }
      }
      if (fg.key && (fg.fieldGroup || fg.fieldArray)) {
        if (fg.fieldArray?.fieldGroup) {
          this.evalExpressionModelValue(fg.fieldArray.fieldGroup, model, formState, fg.key as string);
        } else {
          this.evalExpressionModelValue(fg.fieldGroup, model, formState);
        }
      }
    });
  }

  private evalKeyParentExpressionModelValue(model: any, parentKey: string, fg: SgiFormlyFieldConfig, formState: any) {
    if (!model[parentKey]) {
      model[parentKey] = [];
    }

    const expressionModelValue = fg.templateOptions.expressionModelValue;

    const regularExpressionFieldIterable = /\$1/;
    const matchIterationExpressionModelValue = expressionModelValue.match(regularExpressionFieldIterable);

    if (matchIterationExpressionModelValue) {
      const fromIndex = expressionModelValue.indexOf('.');
      const toIndex = expressionModelValue.indexOf(matchIterationExpressionModelValue[0]);
      const fieldIteration = expressionModelValue.substring(fromIndex + 1, toIndex - 1);
      if (formState[fieldIteration] && formState[fieldIteration] instanceof Array) {
        if (formState[fieldIteration].length === 0 || (formState[fieldIteration].length < model[parentKey].length)) {
          model[parentKey] = [];
        }
        formState[fieldIteration].forEach((element, i) => {
          const newExpressionModelValue = expressionModelValue.replace(regularExpressionFieldIterable, i);

          const f = this.evalStringExpression(newExpressionModelValue, ['model', 'formState', 'field']);

          if (model[parentKey] && !model[parentKey][i]) {
            model[parentKey][i] = {};
          }

          model[parentKey][i][fg.key as string] = this.evalExpression(f, { fg }, [{ model }, formState, fg]);
        });

      }
    }
  }

  private evalExpressionLock(fieldConfig: SgiFormlyFieldConfig, model: any, formState: any): void {
    if (fieldConfig.templateOptions.expressionLock) {
      const f = this.evalStringExpression(fieldConfig.templateOptions.expressionLock, ['model', 'formState', 'field']);
      fieldConfig.templateOptions.locked = !!this.evalExpression(f, { fieldConfig }, [{ model }, formState, fieldConfig]);
    }
  }

  private evalStringExpression(expression: string, argNames: string[]) {
    try {
      return Function(...argNames, `return ${expression};`) as any;
    } catch (error) {
      this.logger.error(error);
    }
  }

  // tslint:disable-next-line: ban-types
  private evalExpression(expression: Function, thisArg: any, argVal: any[]): any {
    return expression.apply(thisArg, argVal);
  }

  private replaceTemplatePlaceholders(fieldConfig: SgiFormlyFieldConfig, formState: any): void {
    if (fieldConfig.fieldGroup.length) {
      fieldConfig.fieldGroup
        .filter(field => !!field.templateOptions && field.templateOptions.replaceTemplatePlaceholders)
        .forEach(field => {
          field.template = field.template
            .replace('#PETICION_EVALUACION_ID#', formState.memoria.peticionEvaluacion.id)
            .replace('#MEMORIA_ID#', formState.memoria.id)
            .replace('#MODULE_PATH#', this.moduloInv ? Module.INV.path : Module.ETI.path);
        });
    }
  }

  /**
   * Comprueba si el formulario es de tipo M20
   *
   * @returns si el formulario es de tipo M20 o no
   */
  private isFormularioM20(): boolean {
    return this.formularioTipo === FORMULARIO.M20;
  }

  /**
   * Comprueba si la respuesta es la respuesta para la evaluacion de la retrospectiva
   *
   * @param respuesta una respuesta
   * @returns si es la respuesta buscada o no
   */
  private isRespuestaEvaluacionRetrospectivaAndFilled(respuesta: IRespuesta): boolean {
    return !!respuesta.valor?.evaluacionRetrospectivaRadio && ['si', 'no'].includes(respuesta.valor?.evaluacionRetrospectivaRadio);
  }


  /**
   * Recupera las respuestas del bloque
   *
   * @param block un bloque
   * @returns la lista de respuestas del bloque
   */
  private getRespuestasBloque(block: IBlock): IRespuesta[] {
    return block.questions
      .map(question => this.getRespuestas(question))
      .reduce((respuestasBloque, respuestasQuestion) => respuestasBloque.concat(respuestasQuestion), []);
  }

  /**
   * Comprueba si se esta respondiendo a las preguntas del bloque por primera vez o ya tiene respuestas previamente constestadas
   *
   * @param block el bloque que se quiere comprobar
   * @returns true si se esta rellenado el bloque por primera vez
   */
  private hasBloqueSavedRespuestas(block: IBlock, respuestas: IRespuesta[]): boolean {
    return !!block.loaded$.value && respuestas.length > 0 && respuestas.some(respuesta => !!respuesta.id);
  }

  private isFormularioMemoriaModificacion(memoria: IMemoria): boolean {
    return Boolean(memoria.memoriaOriginal);
  }

  private isRespuestaApartadoModified(respuesta: IRespuesta, respuestaAnterior: IRespuesta): boolean {
    return JSON.stringify(respuesta?.valor) !== JSON.stringify(respuestaAnterior?.valor);
  }

  private refreshBlockChanges() {
    this.blocks$.value.forEach((block) => {
      block.formlyData.fields.forEach((f) => {
        if (f.group) {
          f.group.refreshInitialState();
        }
      });
    });
  }
}
