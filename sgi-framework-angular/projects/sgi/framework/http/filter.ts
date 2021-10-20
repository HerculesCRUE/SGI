interface Filter {
  /**
   * The value to apply
   */
  value: string | SgiRestFilter;
  /**
   * The condition to apply
   */
  condition?: SgiRestFilterCondition;
}

interface FilterClause extends Filter {
  /**
   * The entity field name
   */
  field: string;
  /**
   * The operator of the filter
   */
  operator: SgiRestFilterOperator;
  /**
   * The value to apply
   */
  value: string;
}

/**
 * Filter to apply
 */
export interface SgiRestFilter {
  /**
   * Add new AND condition to the filter
   *
   * @param field the entity field name
   * @param operator  the operator of the filter
   * @param value the value
   *
   * @returns filter instance to chaining more conditions
   */
  and(field: string, operator: SgiRestFilterOperator, value: string | string[]): SgiRestFilter;
  /**
   * Add new AND condition filter
   *
   * @param filter the filter to append
   *
   * @returns filter instance to chaining more conditions
   */
  and(filter: SgiRestFilter): SgiRestFilter;
  /**
   * Add new OR condition to the filter
   *
   * @param field the entity field name
   * @param operator  the operator of the filter
   * @param value the value
   *
   * @returns filter instance to chaining more conditions
   */
  or(field: string, operator: SgiRestFilterOperator, value: string | string[]): SgiRestFilter;
  /**
   * Add new OR condition filter
   *
   * @param filter the filter to append
   *
   * @returns filter instance to chaining more conditions
   */
  or(filter: SgiRestFilter): SgiRestFilter;
  /**
   * Check if the filter contains a condition for a field
   *
   * @param field the field name to check
   * @returns `true` if the filters contains a condition, `false` otherwise.
   */
  contains(field: string): boolean;
  /**
   * Remove filter conditions for a field
   *
   * @param field the field name to remove
   */
  remove(field: string): void;
  /**
   * Generate the query parameter value
   *
   * @returns the query parameter value
   */
  toString(): string;
}

function isFilterClause(filter: Filter | FilterClause): filter is FilterClause {
  return (filter as FilterClause).operator !== undefined;
}

/**
 * RSQL SgiRestFilter implementation
 */
export class RSQLSgiRestFilter implements SgiRestFilter {

  private filters: (Filter | FilterClause)[] = [];

  /**
   * Initialize the filter with the provided filter
   *
   * @param filter the filter
   */
  constructor(filter: SgiRestFilter);
  /**
   * Initialize the filter with the provided values
   *
   * @param field the entity field name
   * @param operator  the operator of the filter
   * @param value the value
   */
  constructor(field: string, operator: SgiRestFilterOperator, value: string | string[]);
  constructor(firsParameter: SgiRestFilter | string, operator?: SgiRestFilterOperator, value?: string | string[]) {
    if (typeof firsParameter === 'string') {
      if (!firsParameter || firsParameter === '') {
        throw Error('field is mandaroty');
      }
      if (!operator) {
        throw Error('operator is mandaroty');
      }
      const filteredValue = this.filterArrayEmptyValues(value);
      this.checkValue(operator, filteredValue);
      this.filters.push({
        field: firsParameter,
        operator,
        value: this.getFixedValue(operator, filteredValue)
      });
    }
    else {
      this.filters.push({
        value: firsParameter,
        condition: SgiRestFilterCondition.AND
      });
    }
  }

  private checkValue(operator: SgiRestFilterOperator, value: string | string[]): void {
    if (!value) {
      return;
    }
    if (operator.multiValue && !Array.isArray(value)) {
      throw Error('Multivalue operator only accept an array of values');
    }
    if (Array.isArray(value)) {
      if (!operator.multiValue && value.length > 1) {
        throw Error('Non multivalue operator only accept one argument');
      }
      if (operator.multiValue &&
        (operator === SgiRestFilterOperator.BETWEEN || operator === SgiRestFilterOperator.NOT_BETWEEN) && value.length !== 2) {
        throw Error(`The value must contains 2 values`);
      }
      if (operator.multiValue && (operator === SgiRestFilterOperator.IN || operator === SgiRestFilterOperator.NOT_IN) && value.length < 1) {
        throw Error(`The value must contains at least 1 value`);
      }
    }
  }

  private filterArrayEmptyValues(value: string | string[]): string | string[] {
    // Filter the array when contains more than one value to discard undefined or empty values
    if (Array.isArray(value) && value.length > 1) {
      return value.filter(v => (v || v !== ''));
    }
    return value;
  }

  private getEscapedValue(value: string): string {
    return value ? `"${value.replace(/\\/gm, '\\\\').replace(/"/gm, '\\"')}"` : value;
  }

  private getFixedValue(operator: SgiRestFilterOperator, value: string | string[]): string {
    if (operator === SgiRestFilterOperator.IS_NULL || operator === SgiRestFilterOperator.IS_NOT_NULL) {
      return '""';
    }

    let fixedValue: string;
    if (Array.isArray(value)) {
      fixedValue = value.map(val => this.getEscapedValue(val)).join(',');
    }
    else {
      fixedValue = this.getEscapedValue(value);
    }

    if (operator.multiValue) {
      return `(${fixedValue})`;
    }
    return fixedValue;
  }

  private addClausule(field: string, operator: SgiRestFilterOperator, value: string | string[], condition: SgiRestFilterCondition) {
    if (!field || field === '') {
      throw Error('field is mandaroty');
    }
    if (!operator) {
      throw Error('operator is mandaroty');
    }
    const filteredValue = this.filterArrayEmptyValues(value);
    this.checkValue(operator, filteredValue);
    this.filters.push({
      field,
      operator,
      value: this.getFixedValue(operator, filteredValue),
      condition
    });
  }

  /**
   * Add new AND condition to the filter
   *
   * @param field the entity field name
   * @param operator  the operator of the filter
   * @param value the value
   *
   * @returns filter instance to chaining more conditions
   */
  and(field: string, operator: SgiRestFilterOperator, value: string | string[]): SgiRestFilter;
  /**
   * Add new AND condition filter
   *
   * @param filter the filter to append
   *
   * @returns filter instance to chaining more conditions
   */
  and(filter: SgiRestFilter): SgiRestFilter;
  and(firsParameter: SgiRestFilter | string, operator?: SgiRestFilterOperator, value?: string | string[]) {
    if (typeof firsParameter === 'string') {
      this.addClausule(firsParameter, operator, value, SgiRestFilterCondition.AND);
    }
    else {
      this.filters.push({
        value: firsParameter,
        condition: SgiRestFilterCondition.AND
      });
    }
    return this;
  }

  /**
   * Add new OR condition to the filter
   *
   * @param field the entity field name
   * @param operator  the operator of the filter
   * @param value the value
   *
   * @returns filter instance to chaining more conditions
   */
  or(field: string, operator: SgiRestFilterOperator, value: string | string[]): SgiRestFilter;
  /**
   * Add new OR condition filter
   *
   * @param filter the filter to append
   *
   * @returns filter instance to chaining more conditions
   */
  or(filter: SgiRestFilter): SgiRestFilter;
  or(firsParameter: SgiRestFilter | string, operator?: SgiRestFilterOperator, value?: string | string[]): SgiRestFilter {
    if (typeof firsParameter === 'string') {
      this.addClausule(firsParameter, operator, value, SgiRestFilterCondition.OR);
    }
    else {
      this.filters.push({
        value: firsParameter,
        condition: SgiRestFilterCondition.OR
      });
    }
    return this;
  }

  contains(field: string): boolean {
    return this.filters.filter(filter => isFilterClause(filter)).some((filter: FilterClause) => filter.field === field);
  }

  remove(field: string): void {
    const index = this.filters.filter(filter => isFilterClause(filter)).findIndex((filter: FilterClause) => filter.field === field);
    if (index > 0) {
      this.filters.splice(index, 1);
    }
  }

  public toString(): string {
    const filterValues: string[] = [];
    if (this.filters.length) {
      this.filters.forEach((filter) => {
        if (isFilterClause(filter)) {
          if (filter.field && filter.value && filter.operator && filter.operator !== SgiRestFilterOperator.NONE) {
            filterValues.push((filterValues.length ? filter.condition : '') + filter.field + filter.operator.toString() + filter.value);
          }
        }
        else {
          if (filter.value.toString().length) {
            filterValues.push((filterValues.length ? filter.condition : '') + '(' + filter.value.toString() + ')');
          }
        }
      });
    }
    return filterValues.join('');
  }
}

/**
 * Filter operator
 */
export class SgiRestFilterOperator {

  private constructor(
    private value: string,
    /** Indicate if the filter support multiple values */
    public readonly multiValue: boolean
  ) { }

  /** No filter */
  public static readonly NONE = new SgiRestFilterOperator(undefined, false);
  /** Filter by 'equals' operator */
  public static readonly EQUALS = new SgiRestFilterOperator('==', false);
  /** Filter by 'equals ignoring case' operator */
  public static readonly EQUALS_ICASE = new SgiRestFilterOperator('=ic=', false);
  /** Filter by 'notEquals' operator */
  public static readonly NOT_EQUALS = new SgiRestFilterOperator('!=', false);
  /** Filter by 'like' operator */
  public static readonly LIKE = new SgiRestFilterOperator('=ke=', false);
  /** Filter by 'notLike' operator */
  public static readonly NOT_LIKE = new SgiRestFilterOperator('=nk=', false);
  /** Filter by 'like ignoring case' operator */
  public static readonly LIKE_ICASE = new SgiRestFilterOperator('=ik=', false);
  /** Filter by 'notLike ignoring case' operator */
  public static readonly NOT_LIKE_ICASE = new SgiRestFilterOperator('=ni=', false);
  /** Filter by 'greather' operator */
  public static readonly GREATHER = new SgiRestFilterOperator('=gt=', false);
  /** Filter by 'greatherOrEqual' operator */
  public static readonly GREATHER_OR_EQUAL = new SgiRestFilterOperator('=ge=', false);
  /** Filter by 'lower' operator */
  public static readonly LOWER = new SgiRestFilterOperator('=lt=', false);
  /** Filter by 'lowerOrEqual' operator */
  public static readonly LOWER_OR_EQUAL = new SgiRestFilterOperator('=le=', false);
  /** Filter by 'between' operator */
  public static readonly BETWEEN = new SgiRestFilterOperator('=bt=', true);
  /** Filter by 'notBetween' operator */
  public static readonly NOT_BETWEEN = new SgiRestFilterOperator('=nb=', true);
  /** Filter by 'isNull' operator */
  public static readonly IS_NULL = new SgiRestFilterOperator('=na=', false);
  /** Filter by 'isNotNull' operator */
  public static readonly IS_NOT_NULL = new SgiRestFilterOperator('=nn=', false);
  /** Filter by 'in' operator */
  public static readonly IN = new SgiRestFilterOperator('=in=', true);
  /** Filter by 'notIn' operator */
  public static readonly NOT_IN = new SgiRestFilterOperator('=out=', true);

  public toString(): string {
    return this.value;
  }
}

/**
 * Filter condition
 */
class SgiRestFilterCondition {

  private constructor(
    private value: string
  ) { }

  /** AND condition */
  public static readonly AND = new SgiRestFilterCondition(';');
  /** OR condition */
  public static readonly OR = new SgiRestFilterCondition(',');

  public toString(): string {
    return this.value;
  }
}
