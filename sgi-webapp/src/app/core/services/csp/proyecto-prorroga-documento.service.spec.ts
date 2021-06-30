import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ProyectoProrrogaDocumentoService } from './proyecto-prorroga-documento.service';


describe('ProyectoProrrogaDocumento.Service', () => {
  let service: ProyectoProrrogaDocumentoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoProrrogaDocumentoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});