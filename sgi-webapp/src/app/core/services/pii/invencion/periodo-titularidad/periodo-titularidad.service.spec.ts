import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { PeriodoTitularidadService } from './periodo-titularidad.service';

describe('PeriodoTitularidadService', () => {
  let service: PeriodoTitularidadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(PeriodoTitularidadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
