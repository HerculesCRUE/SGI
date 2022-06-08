import { FocusMonitor } from '@angular/cdk/a11y';
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
import { MatAutocompleteSelectedEvent, MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatChipInputEvent, MatChipList } from '@angular/material/chips';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SearchResult } from '@core/component/select-dialog/select-dialog.component';
import { IRecipient } from '@core/models/com/recipient';
import { IPersona } from '@core/models/sgp/persona';
import { PersonaService } from '@core/services/sgp/persona.service';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { catchError, debounceTime, map, startWith, switchMap, takeUntil } from 'rxjs/operators';
import { SearchPersonaModalComponent, SearchPersonaModalData } from 'src/app/esb/sgp/shared/select-persona/dialog/search-persona.component';

let nextUniqueId = 0;

/**
 * A regular expression that matches valid e-mail addresses.
 *
 * At a high level, this regexp matches e-mail addresses of the format `local-part@tld`, where:
 * - `local-part` consists of one or more of the allowed characters (alphanumeric and some
 *   punctuation symbols).
 * - `local-part` cannot begin or end with a period (`.`).
 * - `local-part` cannot be longer than 64 characters.
 * - `tld` consists of one or more `labels` separated by periods (`.`). For example `localhost` or
 *   `foo.com`.
 * - A `label` consists of one or more of the allowed characters (alphanumeric, dashes (`-`) and
 *   periods (`.`)).
 * - A `label` cannot begin or end with a dash (`-`) or a period (`.`).
 * - A `label` cannot be longer than 63 characters.
 * - The whole address cannot be longer than 254 characters.
 *
 * ## Implementation background
 *
 * This regexp was ported over from AngularJS (see there for git history):
 * https://github.com/angular/angular.js/blob/c133ef836/src/ng/directive/input.js#L27
 * It is based on the
 * [WHATWG version](https://html.spec.whatwg.org/multipage/input.html#valid-e-mail-address) with
 * some enhancements to incorporate more RFC rules (such as rules related to domain names and the
 * lengths of different parts of the address). The main differences from the WHATWG version are:
 *   - Disallow `local-part` to begin or end with a period (`.`).
 *   - Disallow `local-part` length to exceed 64 characters.
 *   - Disallow total address length to exceed 254 characters.
 *
 * See [this commit](https://github.com/angular/angular.js/commit/f3f5cf72e) for more details.
 */
const EMAIL_REGEXP =
  /^(?=.{1,254}$)(?=.{1,64}@)[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/;

@Component({
  selector: 'sgi-select-email-recipients',
  templateUrl: './select-email-recipients.component.html',
  styleUrls: ['./select-email-recipients.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectEmailRecipientsComponent
    }
  ]
})
export class SelectEmailRecipientsComponent implements
  OnDestroy, OnInit, AfterViewInit, DoCheck, ControlValueAccessor,
  MatFormFieldControl<IRecipient[]> {

  /** Input control for the autocomplete */
  readonly emailInputCtrl = new FormControl();
  /** Result of elements to display in the autocomplete */
  readonly searchResult$: Subject<IRecipient[]> = new BehaviorSubject<IRecipient[]>([]);
  /** Emits whenever the component is destroyed. */
  // tslint:disable-next-line: variable-name
  private readonly _destroy = new Subject<void>();

  /** True when the component is initialized and ready  */
  private ready = false;

  /** Embedded chipList of selected keywords */
  @ViewChild('chipList') private readonly chipList!: MatChipList;
  /** Embedded input for autocomplete */
  @ViewChild('emailInput') private readonly emailInput!: ElementRef<HTMLInputElement>;
  @ViewChild(MatAutocompleteTrigger) private readonly emailInputAuto!: MatAutocompleteTrigger;

  /** Unique id for this input. */
  private uid = `sgi-select-email-recipients-${nextUniqueId++}`;

  /** Whether the component is focused. */
  get focused(): boolean {
    return this._focused || (this.chipList?.focused ?? false);
  }
  // tslint:disable-next-line: variable-name
  private _focused = false;

  /** A name for this control that can be used by `mat-form-field`. */
  controlType = 'sgi-select-email-recipients';

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
  get value(): IRecipient[] {
    return this._value;
  }
  set value(newValue: IRecipient[]) {
    if (newValue === null) {
      this._value = [];
    }
    else {
      this._value = [...newValue];
    }

  }
  // tslint:disable-next-line: variable-name
  private _value: IRecipient[];

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

  get tooManyResults(): boolean {
    return this._tooManyResults;
  }
  // tslint:disable-next-line: variable-name
  private _tooManyResults = false;

  private dialogOpen = false;

  /** `View -> model callback called when value changes` */
  // tslint:disable-next-line: variable-name
  private _onChange: (value: any) => void = () => { };
  /** `View -> model callback called when component has been touched` */
  // tslint:disable-next-line: variable-name
  private _onTouched = () => { };

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private elementRef: ElementRef,
    private defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() public ngControl: NgControl,
    private personaService: PersonaService,
    private dialog: MatDialog,
    private focusMonitor: FocusMonitor
  ) {
    if (this.ngControl) {
      // Note: we provide the value accessor through here, instead of
      // the `providers` to avoid running into a circular import.
      this.ngControl.valueAccessor = this;
    }
  }

  ngOnInit() {
    this.emailInputCtrl.valueChanges.pipe(
      takeUntil(this._destroy),
      startWith(''),
      debounceTime(200),
      switchMap(value => this.search(value))
    ).subscribe(
      (response => {
        this.searchResult$.next(response.items);
        this._tooManyResults = response.more;
        this.stateChanges.next();
      })
    );

    this.focusMonitor.monitor(this.elementRef, true)
      .pipe(
        takeUntil(this._destroy)
      ).subscribe(
        (value) => {
          if (!!value && !this.disabled) {
            this._focused = !!value;
            this.stateChanges.next();
          }
          if (!!!value) {
            this._focused = !!value;
            if (!this.disabled && !this.dialogOpen) {
              this._onTouched();
              this.changeDetectorRef.markForCheck();
              this.stateChanges.next();
            }
            if (!this.dialogOpen && this.emailInputCtrl.value) {
              this.clearSearch();
            }
          }
        }
      );
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

  ngOnDestroy() {
    this.stateChanges.complete();
    this._destroy.next();
    this.focusMonitor.stopMonitoring(this.elementRef);
  }

  showDialog(): void {
    if (!this.disabled) {
      this.dialogOpen = true;
      const dialogRef = this.dialog.open<SearchPersonaModalComponent, SearchPersonaModalData>(SearchPersonaModalComponent, {
        data: {
          searchTerm: this.emailInputCtrl.value ?? '',
          extended: false,
          tipoColectivo: undefined,
          colectivos: [],
          selectionDisableWith: (persona: IPersona) => {
            if (!!!persona.emails.length) {
              return true;
            }
            return !persona.emails.some((email) => email.principal);
          }
        },
        minWidth: '70vw'
      });
      this.emailInputAuto.closePanel();
      if (this.empty) {
        this.clearSearch();
      }

      dialogRef.afterOpened().subscribe(() => {
        this.changeDetectorRef.markForCheck();
        this._onTouched();
      });

      dialogRef.afterClosed().subscribe((returnValue: IPersona) => {
        this.dialogOpen = false;
        this.emailInput.nativeElement.focus();
        if (returnValue) {
          this.clearSearch();
          const recipient = this.personaToRecipient(returnValue);
          if (!this.value.some((element) => element.address === recipient.address)) {
            this.propagateChanges([...this.value, recipient]);
          }
        }
        else if (this.emailInputCtrl.value) {
          this.emailInputAuto.openPanel();
        }
      });
    }
  }

  private personaToRecipient(persona: IPersona): IRecipient {
    return { name: `${persona.nombre} ${persona.apellidos}`, address: persona.emails?.find(email => email.principal)?.email };
  }

  private clearSearch(): void {
    this.emailInput.nativeElement.value = '';
    this.emailInputCtrl.setValue('');
  }

  private search(value: string): Observable<SearchResult<IRecipient>> {
    if (value?.length >= 3) {
      return this.personaService.findAutocomplete(value).pipe(
        map(response => {
          if (response.length > 10) {
            return {
              items: response.slice(0, 9).map(persona => {
                return this.personaToRecipient(persona);
              }),
              more: true
            };
          }
          return {
            items: response.map(persona => {
              return this.personaToRecipient(persona);
            }),
            more: false
          };
        }),
        catchError(() => this.buildEmptyResponse())
      );
    }
    else {
      return this.buildEmptyResponse();
    }
  }

  private buildEmptyResponse(): Observable<SearchResult<IRecipient>> {
    return of({
      items: [],
      more: false
    });
  }

  _add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value?.toLocaleLowerCase();

    if ((value || '').trim()) {
      if (EMAIL_REGEXP.test(value)) {
        if (!this.value.some((element) => element.address === value)) {
          this.propagateChanges([...this.value, { name: value, address: value }]);
        }
      }
    }

    if (input) {
      input.value = '';
    }

    this.emailInputCtrl.setValue(null);
  }

  _remove(recipient: IRecipient): void {
    const index = this.value.indexOf(recipient);

    if (index >= 0) {
      const current = [...this.value];
      current.splice(index, 1);
      this.propagateChanges(current);
    }
  }

  _selected(event: MatAutocompleteSelectedEvent): void {
    if (!this.value.some((element) => element.address === event.option.value.address)) {
      this.propagateChanges([...this.value, event.option.value]);
    }
    this.clearSearch();
  }

  private propagateChanges(value: IRecipient[]) {
    this.value = value;
    this._onChange(this.value);
    this.stateChanges.next();
    this.changeDetectorRef.markForCheck();
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
      this._onTouched = fn;
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
    return (!this.emailInputCtrl.value) && (!this.value || this.value.length === 0);
  }

  setDescribedByIds(ids: string[]) {
    this.chipList?.setDescribedByIds(ids);
  }

  onContainerClick(event: MouseEvent) {
    // Do nothing, monitored by focusMonitor
  }

  get shouldLabelFloat(): boolean {
    return !this.empty || this.focused;
  }

}
