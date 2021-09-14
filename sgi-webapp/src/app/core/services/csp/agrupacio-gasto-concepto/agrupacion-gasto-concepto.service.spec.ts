import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { AgrupacionGastoConceptoService } from './agrupacion-gasto-concepto.service';

describe('AgrupacionGastoConceptoService', () => {
  let service: AgrupacionGastoConceptoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(AgrupacionGastoConceptoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
