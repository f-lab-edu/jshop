# JSHOP

이커머스 서비스를 만들어보고 테스트를 통해 성능을 최대한으로 끌어올려보는 프로젝트 입니다.

## 프로젝트 목표

### 객체지향 설계에 대한 학습

* 객체지향의 특징과 SOLID 원칙을 지키는 설계를 경험

### 스프링과 JPA에 대한 학습

### 테스트를 통한 고품질 소프트웨어 개발

* 90% 커버리지의 단위 테스트와 통합테스트를 작성해 리팩토링 시에도 문제없이 수행 가능
* 성능 테스트로 부하 상황을 만들어 성능 최적화

### 의미있는 아키텍쳐 설계

* 목적에 맞는 아키텍쳐 설계

### git, github 기능을 사용한 협업 경험

* git-flow 정책으로 브랜치별로 격리된 기능 개발, 이를 통한 코드리뷰
* [프로젝트](https://github.com/orgs/f-lab-edu/projects/225/views/2)
  와 [위키](https://github.com/f-lab-edu/jshop/wiki)를 활용하여 문서화

## 아키텍쳐

![architecture](/images/architecture.png)

### ERD

![erd](/images/erd.png)

### 모니터링

![monitoring](/images/monitoring.png)

## 해결한 문제

* 스프링 시큐리티 적용, 테스트에서 스프링 시큐리티 적용
* 서비스 레이어 인가로직 중복 제거
* 재고 변경 동시성 문제 해결