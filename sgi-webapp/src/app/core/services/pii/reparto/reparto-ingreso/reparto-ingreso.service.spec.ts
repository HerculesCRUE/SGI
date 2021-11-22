import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { RepartoIngresoService } from './reparto-ingreso.service';

describe('RepartoIngresoService', () => {
  let service: RepartoIngresoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(RepartoIngresoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
