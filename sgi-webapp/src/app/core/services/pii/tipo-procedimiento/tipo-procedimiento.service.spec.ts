import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { TipoProcedimientoService } from './tipo-procedimiento.service';

describe('TipoProcedimientoService', () => {
  let service: TipoProcedimientoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoProcedimientoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
