import { SgiRestFilter } from './filter';
import { SgiRestSort } from './sort';

/**
 * Find options for a list of elements. Can be combined
 */
export interface SgiRestFindOptions {
  /**
   * To request a page
   */
  page?: SgiRestPageRequest;
  /**
   * To request a sort
   */
  sort?: SgiRestSort;
  /**
   * To request a filter
   */
  filter?: SgiRestFilter;
}

/**
 * Page to request
 */
export interface SgiRestPageRequest {
  /**
   * The number of elements per page
   */
  size: number;
  /**
   * The page index
   */
  index: number;
}

/**
 * Result of request for a list of elements
 */
export interface SgiRestListResult<T> {
  /**
   * Page information
   */
  page: SgiRestPage;
  /**
   * Total number of elements
   */
  total: number;
  /**
   * Returned elements
   */
  items: T[];
}

/**
 * Page information
 */
interface SgiRestPage {
  /**
   * The number of elements per page
   */
  size: number;
  /**
   * The page index
   */
  index: number;
  /**
   * Elements in the page
   */
  count: number;
  /**
   * Total number of pages
   */
  total: number;
}
