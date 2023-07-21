import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { TipoAmbitoGeograficoService } from './tipo-ambito-geografico.service';


describe('TipoAmbitoGeograficoService', () => {
  let service: TipoAmbitoGeograficoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoAmbitoGeograficoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
