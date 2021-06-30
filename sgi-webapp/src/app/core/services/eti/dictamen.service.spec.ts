import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { DictamenService } from './dictamen.service';

describe('DictamenService', () => {
  let service: DictamenService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(DictamenService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
