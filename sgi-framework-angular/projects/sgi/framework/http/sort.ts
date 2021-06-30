interface SortClause {
  /**
   * The field to sort on
   */
  field: string;
  /**
   * The direction of the sort
   */
  direction: SgiRestSortDirection;
}

/**
 * Sort direction
 */
export class SgiRestSortDirection {
  /** Ascending */
  public static readonly ASC = new SgiRestSortDirection('asc');
  /** Descending */
  public static readonly DESC = new SgiRestSortDirection('desc');

  private constructor(
    private value: string
  ) { }

  /**
   * Maps values of MatSorte direction.
   * @param sortDirection MatSorter direction
   */
  public static fromSortDirection(sortDirection: '' | 'asc' | 'desc'): SgiRestSortDirection {
    if (sortDirection === 'asc') {
      return SgiRestSortDirection.ASC;
    }
    if (sortDirection === 'desc') {
      return SgiRestSortDirection.DESC;
    }
    return undefined;
  }

  public toString(): string {
    return this.value;
  }
}

/**
 * Sort to apply
 */
export interface SgiRestSort {
  /**
   * Add new condition the sort
   *
   * @param field the entity field name
   * @param direction the direction of the sort
   *
   * @returns sort instance to chaining more conditions
   */
  and(field: string, direction: SgiRestSortDirection): SgiRestSort;
  /**
   * Generate the sort parameter value
   *
   * @returns the sort parameter value
   */
  toString(): string;
}

/**
 * RSQL SgiRestSort implementation
 */
export class RSQLSgiRestSort implements SgiRestSort {

  private sorts: SortClause[] = [];

  /**
   * Add new condition to the sort
   *
   * @param field the entity field name
   * @param direction the direction of the sort
   *
   * @returns builder instance to chaining more conditions
   */
  constructor(field: string, direction: SgiRestSortDirection) {
    this.and(field, direction);
  }

  and(field: string, direction: SgiRestSortDirection): SgiRestSort {
    if (!field || field === '') {
      throw Error('field is mandaroty');
    }
    this.sorts.push({
      field,
      direction
    });
    return this;
  }

  public toString(): string {
    const sortValues: string[] = [];
    if (this.sorts) {
      this.sorts.forEach((sort) => {
        if (sort.field) {
          // If no declared direction, then ASC is used
          sortValues.push(sort.field + ',' + (sort.direction ? sort.direction.toString() : SgiRestSortDirection.ASC));
        }
      });
    }
    return sortValues.join(';');
  }
}
