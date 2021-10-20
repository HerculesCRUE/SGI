import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { TipoCaducidadService } from './tipo-caducidad.service';

describe('TipoCaducidadService', () => {
  let service: TipoCaducidadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoCaducidadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
