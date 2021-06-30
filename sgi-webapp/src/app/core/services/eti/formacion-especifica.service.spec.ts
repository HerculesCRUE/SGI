import { TestBed } from '@angular/core/testing';
import { FormacionEspecificaService } from './formacion-especifica.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';

describe('FormacionEspecificaService', () => {
  let service: FormacionEspecificaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(FormacionEspecificaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
