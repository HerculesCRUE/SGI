import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { PaisValidadoService } from './pais-validado.service';


describe('PaisValidadoService', () => {
  let service: PaisValidadoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(PaisValidadoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
