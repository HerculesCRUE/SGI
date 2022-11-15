import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudRrhhRequisitoCategoriaPublicService } from './solicitud-rrhh-requisito-categoria-public.service';

describe('SolicitudRrhhRequisitoCategoriaPublicService', () => {
  let service: SolicitudRrhhRequisitoCategoriaPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudRrhhRequisitoCategoriaPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
