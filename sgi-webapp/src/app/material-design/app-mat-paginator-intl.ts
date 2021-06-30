import { MatPaginatorIntl } from '@angular/material/paginator';
import { TranslateService } from '@ngx-translate/core';
import { Injectable } from '@angular/core';

@Injectable()
export class AppMatPaginatorIntl extends MatPaginatorIntl {
  private ofLabel = 'of';

  constructor(private readonly translate: TranslateService) {
    super();
    this.load();
    translate.onLangChange.subscribe(() => {
      this.load();
      this.changes.next();
    });

    this.getRangeLabel = (page: number, pageSize: number, length: number) => {
      if (length === 0 || pageSize === 0) {
        return `0 ${this.ofLabel} ${length}`;
      }
      length = Math.max(length, 0);
      const startIndex = page * pageSize;
      const endIndex = startIndex < length ? Math.min(startIndex + pageSize, length) : startIndex + pageSize;
      return `${startIndex + 1} – ${endIndex} ${this.ofLabel} ${length}`;
    };
  }

  private load(): void {
    this.translate.get(
      'label.page.items'
    ).subscribe((value) => this.itemsPerPageLabel = value);
    this.translate.get(
      'label.page.next'
    ).subscribe((value) => this.nextPageLabel = value);
    this.translate.get(
      'label.page.prev'
    ).subscribe((value) => this.previousPageLabel = value);
    this.translate.get(
      'label.page.first'
    ).subscribe((value) => this.firstPageLabel = value);
    this.translate.get(
      'label.page.last'
    ).subscribe((value) => this.lastPageLabel = value);
    this.translate.get(
      'label.page.of'
    ).subscribe((value) => this.ofLabel = value);
  }
}
