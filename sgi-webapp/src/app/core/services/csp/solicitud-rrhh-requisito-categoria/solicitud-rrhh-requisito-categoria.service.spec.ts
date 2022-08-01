import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudRrhhRequisitoCategoriaService } from './solicitud-rrhh-requisito-categoria.service';

describe('SolicitudRrhhRequisitoCategoriaService', () => {
  let service: SolicitudRrhhRequisitoCategoriaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudRrhhRequisitoCategoriaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
