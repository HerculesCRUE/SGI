export interface IStatusWrapper<T> {
  value: T;
  status: Status;
}

export enum Status {
  CREATED,
  EDITED,
  DELETED,
  INITIAL
}

export class StatusWrapper<T> implements IStatusWrapper<T> {
  // tslint:disable-next-line: variable-name
  private _value: T;
  // tslint:disable-next-line: variable-name
  private _status: Status;

  constructor(value: T) {
    this._value = value;
    this._status = Status.INITIAL;
  }

  /**
   * The wrapped valued
   */
  get value(): T {
    return this._value;
  }

  /**
   * Returns the status
   */
  get status(): Status {
    return this._status;
  }

  /**
   * Returns true if is created
   */
  get created() {
    return this.status === Status.CREATED;
  }

  /**
   * Set as created
   */
  setCreated(): void {
    this._status = Status.CREATED;
  }

  /**
   * Returns true if is edited
   */
  get edited() {
    return this.status === Status.EDITED;
  }

  /**
   * Set edited
   */
  setEdited() {
    this._status = Status.EDITED;
  }

  /**
   * Returns true if is deleted
   */
  get deleted() {
    return this.status === Status.DELETED;
  }

  /**
   * Set deleted
   */
  setDeleted() {
    this._status = Status.DELETED;
  }

  /**
   * Returns true if have been changed after creation
   */
  get touched() {
    return this.status !== Status.INITIAL;
  }
}
