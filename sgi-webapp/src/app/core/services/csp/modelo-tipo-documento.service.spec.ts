import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ModeloTipoDocumentoService } from './modelo-tipo-documento.service';

describe('ModeloTipoDocumentoService', () => {
  let service: ModeloTipoDocumentoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ModeloTipoDocumentoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
