import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ProyectoAgrupacionGastoService } from './proyecto-agrupacion-gasto.service';

describe('ProyectoAgrupacionGastoService', () => {
  let service: ProyectoAgrupacionGastoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoAgrupacionGastoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
