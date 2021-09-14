import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { InvencionGastoService } from './invencion-gasto.service';

describe('InvencionGastoService', () => {
  let service: InvencionGastoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(InvencionGastoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
