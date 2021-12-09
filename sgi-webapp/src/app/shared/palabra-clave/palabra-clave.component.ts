import { coerceBooleanProperty } from '@angular/cdk/coercion';
import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DoCheck,
  ElementRef,
  Input,
  OnDestroy,
  OnInit,
  Optional,
  Self,
  ViewChild
} from '@angular/core';
import { ControlValueAccessor, FormControl, NgControl } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatChipInputEvent, MatChipList } from '@angular/material/chips';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { PalabraClaveService } from '@core/services/sgo/palabra-clave.service';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult, SgiRestSortDirection } from '@sgi/framework/http';
import { BehaviorSubject, Observable, of, Subject, Subscription } from 'rxjs';
import { catchError, debounceTime, startWith, switchMap } from 'rxjs/operators';

let nextUniqueId = 0;

@Component({
  selector: 'sgi-palabra-clave',
  templateUrl: './palabra-clave.component.html',
  styleUrls: ['./palabra-clave.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: PalabraClaveComponent
    }
  ]
})
export class PalabraClaveComponent implements
  OnDestroy, OnInit, AfterViewInit, DoCheck, ControlValueAccessor,
  MatFormFieldControl<string[]> {

  /** Input control for the autocomplete */
  readonly palabraInputCtrl = new FormControl();
  /** Result of elements to display in the autocomplete */
  readonly searchResult$: Subject<string[]> = new BehaviorSubject<string[]>([]);

  /** Number of elements resolved in the service */
  get filterElements(): number {
    return this._filterElements;
  }
  // tslint:disable-next-line: variable-name
  private _filterElements = 0;

  /** Number of total elements that match the input */
  get totalElements(): number {
    return this._totalElements;
  }
  // tslint:disable-next-line: variable-name
  private _totalElements = 0;

  /** True when the component is initialized and ready  */
  private ready = false;
  /** Store subscriptions to be unsuscribed onDestroy */
  private subscriptions: Subscription[] = [];

  /** Embedded chipList of selected keywords */
  @ViewChild('chipList') private readonly chipList!: MatChipList;
  /** Embedded input for autocomplete */
  @ViewChild('palabraInput') private readonly palabraInput!: ElementRef<HTMLInputElement>;

  /** Unique id for this input. */
  private uid = `sgi-palabra-clave-${nextUniqueId++}`;

  // tslint:disable-next-line: variable-name
  _valueId = `sgi-palabra-clave-value-${nextUniqueId++}`;

  /** Whether the component is focused. */
  get focused(): boolean {
    return this.chipList?.focused ?? false;
  }

  /** A name for this control that can be used by `mat-form-field`. */
  controlType = 'sgi-palabra-clave';

  /** Placeholder to be shown if no value has been selected. */
  @Input()
  get placeholder(): string {
    return this.ready ? this.chipList.placeholder : this._placeholder;
  }
  set placeholder(value: string) {
    if (this.ready) {
      this.chipList.placeholder = value;
    }
    else {
      this._placeholder = value;
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _placeholder: string;

  /** Whether the component is required. */
  @Input()
  get required(): boolean {
    return this.ready ? this.chipList.required : this._required;
  }
  set required(value: boolean) {
    if (this.ready) {
      this.chipList.required = value;
      this.stateChanges.next();
    }
    else {
      this._required = coerceBooleanProperty(value);
    }
  }
  // tslint:disable-next-line: variable-name
  private _required = false;

  /** Value of the component. */
  @Input()
  get value(): string[] {
    return this._value;
  }
  set value(newValue: string[]) {
    if (newValue === null) {
      this._value = [];
    }
    else {
      this._value = [...newValue];
    }

  }
  // tslint:disable-next-line: variable-name
  private _value: string[];

  /** Unique id of the element. */
  @Input()
  get id(): string { return this._id; }
  set id(value: string) {
    this._id = value || this.uid;
  }
  // tslint:disable-next-line: variable-name
  private _id: string;

  @Input()
  get disabled(): boolean {
    return this.ready ? this.chipList.disabled : this._disabled;
  }
  set disabled(value: boolean) {
    if (this.ready) {
      this.chipList.disabled = value;
    }
    else {
      this._disabled = coerceBooleanProperty(value);
    }
  }
  // tslint:disable-next-line: variable-name
  private _disabled = false;

  get tabIndex(): number { return -1; }

  readonly stateChanges: Subject<void> = new Subject<void>();

  /** Whether the component is in an error state. */
  get errorState(): boolean {
    return this.ready ? this.chipList.errorState : this._errorState;
  }
  set errorState(value: boolean) {
    if (this.ready && this.chipList.errorState !== value) {
      this.chipList.errorState = value;
      this._errorState = value;
      this.stateChanges.next();
    }
    else if (this._errorState !== value) {
      this._errorState = value;
      this.stateChanges.next();
    }
  }
  // tslint:disable-next-line: variable-name
  private _errorState = false;

  /** Object used to control when error messages are shown. */
  @Input()
  set errorStateMatcher(errorStateMatcher: ErrorStateMatcher) {
    if (this.ready) {
      this.chipList.errorStateMatcher = errorStateMatcher;
    }
    else {
      this._errorStateMatcher = errorStateMatcher;
    }
  }
  // tslint:disable-next-line: variable-name
  private _errorStateMatcher: ErrorStateMatcher;

  /** `View -> model callback called when value changes` */
  // tslint:disable-next-line: variable-name
  private _onChange: (value: any) => void = () => { };
  /** `View -> model callback called when component has been touched` */
  // tslint:disable-next-line: variable-name
  private _onTouched = () => { };

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() public ngControl: NgControl,
    private palabraClaveService: PalabraClaveService
  ) {
    if (this.ngControl) {
      // Note: we provide the value accessor through here, instead of
      // the `providers` to avoid running into a circular import.
      this.ngControl.valueAccessor = this;
    }
  }

  ngOnInit() {
    this.palabraInputCtrl.valueChanges.pipe(
      startWith(''),
      debounceTime(200),
      switchMap(value => this.search(value))
    ).subscribe(
      (response => {
        this.searchResult$.next(response.items);
        this._filterElements = response.items.length;
        this._totalElements = response.total;
        this.stateChanges.next();
      })
    );
  }

  private search(value: string): Observable<SgiRestListResult<string>> {
    if (value?.length >= 3) {
      const filter = new RSQLSgiRestFilter('palabra', SgiRestFilterOperator.LIKE_ICASE, value);
      const sort = new RSQLSgiRestSort('palabra', SgiRestSortDirection.ASC);
      const findOptions: SgiRestFindOptions = {
        filter,
        sort,
        page: {
          index: 0,
          size: 5
        }
      };
      return this.palabraClaveService.findAll(findOptions).pipe(
        catchError(() => this.buildEmptyResponse())
      );
    }
    else {
      return this.buildEmptyResponse();
    }
  }

  private buildEmptyResponse(): Observable<SgiRestListResult<string>> {
    return of({
      items: [],
      page: { count: 0, index: 0, size: 0, total: 0 },
      total: 0
    } as SgiRestListResult<string>);
  }

  ngAfterViewInit(): void {
    this.chipList.ngControl = this.ngControl;
    // Defer setting the value in order to avoid the "Expression
    // has changed after it was checked" errors from Angular.
    Promise.resolve().then(() => {
      this.chipList.required = this._required;
      this.chipList.disabled = this._disabled;
      this.chipList.placeholder = this._placeholder;
      this.chipList.errorStateMatcher = this._errorStateMatcher || this.defaultErrorStateMatcher;
      this.chipList.errorState = this._errorState;
      this.chipList.registerOnChange(this._onChange);
      this.chipList.registerOnTouched(this._onTouched as () => {});
      this.ready = true;
      this.stateChanges.next();
    });
  }

  ngDoCheck(): void {
    if (this.ready) {
      // Call ngDoCheck to evaluate errorState
      this.chipList.ngDoCheck();
      this.errorState = this.chipList.errorState;
    }
  }

  _add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value?.toLocaleLowerCase();

    if ((value || '').trim()) {
      if (!this.value.some((element) => element === value)) {
        this.propagateChanges([...this.value, value]);
      }
    }

    if (input) {
      input.value = '';
    }

    this.palabraInputCtrl.setValue(null);
  }

  _remove(fruit: string): void {
    const index = this.value.indexOf(fruit);

    if (index >= 0) {
      const current = [...this.value];
      current.splice(index, 1);
      this.propagateChanges(current);
    }
  }

  _selected(event: MatAutocompleteSelectedEvent): void {
    if (!this.value.some((element) => element === event.option.value)) {
      this.propagateChanges([...this.value, event.option.value]);
      this.palabraInput.nativeElement.value = '';
      this.palabraInputCtrl.setValue('');
    }
  }

  private propagateChanges(value: string[]) {
    this.value = value;
    this._onChange(this.value);
    this.changeDetectorRef.markForCheck();
  }

  ngOnDestroy() {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  // Value Accesor ***************************
  writeValue(value: any): void {
    this.value = value;
    this.changeDetectorRef.markForCheck();
  }

  registerOnChange(fn: (value: any) => void): void {
    if (this.ready) {
      this.chipList.registerOnChange(fn);
    }
    else {
      this._onChange = fn;
    }
  }

  registerOnTouched(fn: () => {}): void {
    if (this.ready) {
      this.chipList.registerOnTouched(fn);
    }
    else {
      this._onTouched = fn;
    }
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  /** Whether the component has a value. */
  get empty(): boolean {
    return (!this.palabraInputCtrl.value) && (!this.value || this.value.length === 0);
  }

  setDescribedByIds(ids: string[]) {
    this.chipList?.setDescribedByIds(ids);
  }

  onContainerClick(event: MouseEvent) {
    this.chipList.onContainerClick(event);
  }

  get shouldLabelFloat(): boolean {
    return !this.empty || this.focused;
  }

}
