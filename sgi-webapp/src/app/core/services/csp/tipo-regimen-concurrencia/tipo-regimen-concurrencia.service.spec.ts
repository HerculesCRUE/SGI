import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { TipoRegimenConcurrenciaService } from './tipo-regimen-concurrencia.service';


describe('TipoRegimenConcurrenciaService', () => {
  let service: TipoRegimenConcurrenciaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoRegimenConcurrenciaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
