import { TestBed } from '@angular/core/testing';

import { LayoutService } from './layout.service';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TranslateService } from '@ngx-translate/core';
import TestUtils from '@core/utils/test-utils';
import { TranslateTestingModule } from 'ngx-translate-testing';

describe('LayoutService', () => {
  let service: LayoutService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
      ],

    });
    service = TestBed.inject(LayoutService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
