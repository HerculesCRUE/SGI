import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { InvencionDocumentoService } from './invencion-documento.service';


describe('InvencionDocumentoService', () => {
  let service: InvencionDocumentoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(InvencionDocumentoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
