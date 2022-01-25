import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { EstadoAutorizacionService } from './estado-autorizacion.service';

describe('EstadoAutorizacionService', () => {
  let service: EstadoAutorizacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(EstadoAutorizacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
