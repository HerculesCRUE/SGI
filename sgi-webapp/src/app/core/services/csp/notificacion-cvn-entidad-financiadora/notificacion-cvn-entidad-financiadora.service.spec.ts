import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { NotificacionCvnEntidadFinanciadoraService } from './notificacion-cvn-entidad-financiadora.service';

describe('NotificacionCvnEntidadFinanciadoraService', () => {
  let service: NotificacionCvnEntidadFinanciadoraService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(NotificacionCvnEntidadFinanciadoraService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
