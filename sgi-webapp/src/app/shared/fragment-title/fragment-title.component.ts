import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { LayoutService, Title } from '@core/services/layout.service';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';

@Component({
  selector: 'sgi-fragment-title',
  templateUrl: './fragment-title.component.html',
  styleUrls: ['./fragment-title.component.scss']
})
export class FragmentTitleComponent implements OnInit, OnDestroy {
  @Input()
  title: string;

  @Input()
  titleArray: (string | I18nFieldValue)[];

  routeTitle: Title;

  private subscriptions: Subscription[] = [];

  constructor(private layoutService: LayoutService, private readonly translate: TranslateService) { }

  ngOnInit(): void {
    this.subscriptions.push(this.layoutService.title$.subscribe((title) => {
      const routeTitle: Title = {
        key: title?.key,
        params: Object.assign({}, title?.params)
      };

      this.resolvedParams(routeTitle as Title)
    }
    ));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private resolvedParams(title: Title): any {
    if (title?.params?.entity) {
      this.translate.get(
        title.params.entity,
        { count: title.params?.count }
      ).subscribe((value) => {
        title.params.entity = value
        this.routeTitle = title;
      });
    }
    else {
      this.routeTitle = title;
    }
  }
}
