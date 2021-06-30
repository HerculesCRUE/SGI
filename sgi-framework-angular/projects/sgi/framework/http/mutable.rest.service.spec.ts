import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiBaseConverter } from '@sgi/framework/core';
import { SgiMutableRestService } from './mutable.rest.service';

const fakeEndpoint = 'http://localhost:8080/fake';

interface DummyDataSource {
  idSource: number;
  nameSource: string;
  surnameSource: string;
  ageSource: number;
}

interface DummyDataTarget {
  id: number;
  name: string;
  surname: string;
  age: number;
}

class DummyDataConverter extends SgiBaseConverter<DummyDataSource, DummyDataTarget> {
  toTarget(value: DummyDataSource): DummyDataTarget {
    return {
      age: value?.ageSource,
      id: value?.idSource,
      name: value?.nameSource,
      surname: value?.surnameSource
    };
  }
  fromTarget(value: DummyDataTarget): DummyDataSource {
    return {
      ageSource: value?.age,
      idSource: value?.id,
      nameSource: value?.name,
      surnameSource: value?.surname
    };
  }
}

class FakeService extends SgiMutableRestService<number, DummyDataSource, DummyDataTarget> {
  constructor(http: HttpClient) {
    super(FakeService.name, fakeEndpoint, http, new DummyDataConverter());
  }
}

describe('SgiMutableRestService', () => {

  // existing Target entities
  const dummyEntityList: DummyDataTarget[] = [
    { id: 1, name: 'George', surname: 'Michael', age: 51 },
    { id: 2, name: 'Janet', surname: 'Jackson', age: 48 },
    { id: 3, name: 'Emma', surname: 'Watson', age: 28 },
  ];
  // existing Source entities
  const dummyEntitySourceList: DummyDataSource[] = [
    { idSource: 1, nameSource: 'George', surnameSource: 'Michael', ageSource: 51 },
    { idSource: 2, nameSource: 'Janet', surnameSource: 'Jackson', ageSource: 48 },
    { idSource: 3, nameSource: 'Emma', surnameSource: 'Watson', ageSource: 28 },
  ];

  let service: FakeService;
  let httpMock: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = new FakeService(TestBed.inject(HttpClient));
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    // then: verify that there are not pending http calls
    httpMock.verify();
  });

  it('create() should POST and return data', () => {
    // given: a new Entity
    const newEntity: DummyDataTarget = { name: 'Erik' } as DummyDataTarget;
    // when: create method called
    service.create(newEntity).subscribe((res) => {
      // then: a new entity is created with the given name
      expect(res.name).toEqual('Erik');
    });

    // then: the right backend API is called
    const req = httpMock.expectOne(fakeEndpoint, 'POST to API');
    // then: the right HTTP method is used to call the backend API
    expect(req.request.method).toBe('POST');

    // “fire” the request with the mocked result
    req.flush({ idSource: 99, nameSource: 'Erik' });
  });

  it('update() should PUT and return data', () => {
    // given: an existing Entity
    const existingEntity = dummyEntityList[0];
    // when: entity udpated and update method called
    const updatedEntity = Object.assign({}, existingEntity);
    const updatedSourceEntity = Object.assign({}, dummyEntitySourceList[0]);
    updatedEntity.name = 'Erik';
    updatedSourceEntity.nameSource = 'Erik';
    service.update(existingEntity.id, updatedEntity).subscribe((res) => {
      // then: the entity is updated with the given values
      expect(res).toEqual(updatedEntity);
    });

    // then: the right backend API is called
    const req = httpMock.expectOne(`${fakeEndpoint}/${existingEntity.id}`, 'PUT to API');
    // then: the right HTTP method is used to call the backend API
    expect(req.request.method).toBe('PUT');

    // “fire” the request with the mocked result
    req.flush(updatedSourceEntity);
  });

  it('findById() should return data', () => {
    // given: existing entity
    const dummyEntity = dummyEntityList[0];

    // when: findOne method called with existing entity id
    service.findById(dummyEntity.id).subscribe((res) => {
      // then: the existing entity is returned
      expect(res).toEqual(dummyEntity);
    });

    // then: the right backend API is called
    const req = httpMock.expectOne(`${fakeEndpoint}/${dummyEntity.id}`, 'GET to API');
    // then: the right HTTP method is used to call the backend API
    expect(req.request.method).toBe('GET');

    // "fire" the request with the modcked result
    req.flush(dummyEntitySourceList[0]);
  });


  it('findAll() should GET ListResult', () => {
    // when: findAll method called
    service.findAll().subscribe((res) => {
      // then: a new entity is created with the given name
      expect(res.items).toEqual(dummyEntityList);
    });

    // then: the right backend API is called
    const req = httpMock.expectOne(`${fakeEndpoint}`, 'GET to API');
    // then: the right HTTP method is used to call the backend API
    expect(req.request.method).toBe('GET');

    // “fire” the request with the mocked result
    req.flush(dummyEntitySourceList);
  });

});
