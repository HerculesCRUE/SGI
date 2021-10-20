import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { InvencionIngresoService } from './invencion-ingreso.service';

describe('InvencionIngresoService', () => {
  let service: InvencionIngresoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(InvencionIngresoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});