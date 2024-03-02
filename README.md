# Hello! spring-cloud-contract

요즘 분산 서비스에서 서비스간의 계약을 검증하는 방법에 대해 고민하고 있다.
짜투리 시간을 활용해 정리하다보니 업데이트가 느리니 양해바란다.

## 학습

### 왜 학습하는지

올해 목표는 빠른 마이그레이션이 가능한 환경을 만드는 일이다.
최근 마이그레이션을 진행하면서 `open feign`과 `okhttp` 라이브러리 사이에서 버전 충돌로 인해 문제가 발생했다.
이런 문제를 빌드 전에 쉽게 검증할 수 있으면 좋겠다고 생각했고, `spring-cloud-contract`를 사용해서 문제를 해소해보려 한다.

### spring cloud contract가 나오게된 배경

분산 환경에서 서비스를 제공하게 되면서 서비스간의 계약을 유지하는 것이 중요해졌다.

계약을 유지하기 위해서는 테스트로 검증하는 것이 중요한데, 효율적인 테스트 작성 및 관리를 위해서는 테스트 경계를 고민해볼 필요가 있다.
시뮬레이션을 하기위해 모든 서버를 가동시켜 테스트하기에는 외부 서비스의 영향을 받아 고려할 검증 정책이 많아져 비용 증가 문제게 직결한다.

[spring cloud contract](https://docs.spring.io/spring-cloud-contract/reference/getting-started/introducing-spring-cloud-contract.html)
는 서로 간 최소한의 계약만을 정의해 `충분히` 검증 할 수 있도록 도와준다.

충분히 검증한다는 것은, 진행되는 테스트가 계약에 맞게 통신이 정상적으로 이루어진다는 것을 의미할 뿐이지 외부 서비스의 영향을 받지 않는다는 것을 의미하지는 않는다.

> Contract tests are used to test contracts between applications, not to simulate full behavior.
>
> spring 레퍼런스에서도 계약만 테스트할 뿐이지 전체 동작을 시뮬레이션하는게 아니라고 한다. - [참고 링크](https://docs.spring.io/spring-cloud-contract/reference/getting-started/introducing-spring-cloud-contract.html#getting-started-introducing-spring-cloud-contract-purposes)

`spring contract`가 추구하는 동작은 다음처럼 공급자는 `검증된 계약`을 제공하고 소비자는 `검증된 계약`으로 동작을 테스트한다.

![image](https://github.com/this-is-spear/hello-spring-contract/assets/92219795/4793c10c-1a2c-41f0-a870-bbdb7d9ebb2d)

공급자의 동작 먼저 살펴보겠다. 
우리는 RestAssuredMockMvc 클래스가 제공하는 standaloneSetup 메서드만을 이용해 세팅을 완료하면 알아서 테스트를 진행해준다.

![image](https://github.com/this-is-spear/hello-spring-contract/assets/92219795/d5accc71-a2ab-4241-8755-749d53b913e7)

> build 후 build 파일에 generated-test-sources 에 접근하면 계약에 맞게 생성된 테스트 코드를 확인할 수 있다.

공급자 관련 세팅,,, 정말 어렵다. 잘 안되는데, [샘플 코드](https://github.com/spring-cloud-samples/spring-cloud-contract-samples)를 잘 확인하면서 진행해보자.
특히나 빌드 관련 파일이 말썽이다.

추가로 유의할 점은 `contracts` 내부 폴더 이름을 `prefix`로하는 `Base` 테스트 클래스가 필요하다는 점이다.
즉, `fraoud` 폴더는 `FraoudBase.kt`를 필요로 한다.

```text
.
├── kotlin
│   └── tis
│       └── producer
│           └── FraoudBase.kt
└── resources
    └── contracts
        └── fraoud
            └── fraud-check.groovy
```

이렇게 구성하면 `build`를 실행할때마다 올바른 계약인지 검증하게 된다.

### 소비자 중심 계약

> [해당 링크](https://martinfowler.com/articles/consumerDrivenContracts.html)는 서비스 공급자의 계약이 변경될 때마다 발생하는 결함을 어떻게 해결할지 고민한
> 글이다.

기업이 추구하는건 민첩성이다. 기업은 서비스 지향 아키텍처(SOA)를 유지하며 변경 비용을 줄여 민첩성을 높이고 있다.

> 서비스 지향 아키텍처란 비즈니스 문제를 서비스 단위로 제공하면서 플랫폼과 언어를 넘나들며 재사용하거나 복잡한 업무를 수행 할 수 있는 구조를 말한다.

[참고 링크](https://aws.amazon.com/ko/what-is/service-oriented-architecture/)

**초기 개발은 민첩하다.**

종종 민첩성을 높이기 위해 공급자가 생성되기 전에 소비자가 완성도어야 하는 케이스도 있다.
그런 상황에서 계약으로 이루어진 독립된 서비스라면 계약만을 가지고 동시에 작업이 가능할테다.

![image](https://github.com/this-is-spear/hello-spring-contract/assets/92219795/fc21f2e8-9fee-49e6-ba7f-41231515e2db)

**변경에는 민첩하지 못하다.**

서비스 독립성을 위해 계약에 의존적이기 때문에 계약 변경에 자유롭지 못하다.
소비자와 공급자는 독립적인 서비스를 제공하지만, 계약에 의존적이다.

![image](https://github.com/this-is-spear/hello-spring-contract/assets/92219795/28498928-1f84-463f-a423-1e84528af516)

계약은 서비스 독립성을 유지시켜 주지만, 계약에 의존적이며 서비스 제공자와 소비사 간 의도하지 않은 결합이 발생할 수 있다.
이런 문제가 곧 서비스에 대한 부담으로 이어지며 서비스 지향 아키텍처를 유지할 수록 민첩하지 못하는 이유다.

**즉, 공급자 계약 변경으로 소비자들의 행동을 어떻게 제어할것인지가 관건이다.**
글에서는 [네트워크의 견고함의 원칙](https://ko.wikipedia.org/wiki/%EA%B2%AC%EA%B3%A0%ED%95%A8%EC%9D%98_%EC%9B%90%EC%B9%99)을
모티브로 `소비자는 모든 응답을 너그럽게 받고 안에서 값을 검증하는게 어떻겠냐`라는게 결론이다.

어떻게 너그럽게 받을지는 계약의 종류에 따라 다르다.
글에서는 계약의 종류를 공급자, 소비자, 소비자 중심 계약 세 가지로 표현한다.

| Contract        | Open    | Complete   | Number   | Authority         | Bounded    |
|-----------------|---------|------------|----------|-------------------|------------|
| Provider        | Closed  | Complete   | Single   | Authoritative     | Space/time |
| Consumer        | Open    | Incomplete | Multiple | Non-authoritative | Space/time |
| Consumer-Driven | Closed	 | Complete   | Single   | Non-authoritative | Consumers  |

생산자 계약은 제공하는 값은 비즈니스 자체를 의미(complete)하며 비즈니스에 필요한 값만 제공(closed)한다.

![image](https://github.com/this-is-spear/hello-spring-contract/assets/92219795/a1b6d66e-52ef-4909-ab71-9c21807f121a)

소비자 계약은 제공자가 제공하는 값은 비즈니스 이외의 값을 의미(incomplete)하며 비즈니스외적인 모든 값을 제공(open)한다.

![image](https://github.com/this-is-spear/hello-spring-contract/assets/92219795/36104f1c-ffac-4cfa-814e-29bd63c64d8b)

소비자 중심 계약은 소비자의 기대와 욕구를 충족시켜주는 공급자 계약이다. 
즉, 공급자는 소비자가 원하는 값을 제공해야하며 그 값은 비즈니스에 필요한 값만 추가되어야 한다.

![image](https://github.com/this-is-spear/hello-spring-contract/assets/92219795/ff1160ff-c67a-4e20-8ab7-6d7cc51c0f4e)

쉽게 이해해보면 소비자가 원하는 값으로 계약이 진행된다. 소비자가 원하는 값이 변경되면 계약이 변경된다. 그것이 곧 소비자 중심 계약이다.

소비자 중심 계약 덕분에 계약의 변경 횟수를 줄일 수 있고, 계약 변경으로 발생하는 결함 영향도를 줄일 수 있다.
특별한 인사이트를 찾았다기 보다는 왜 우리가 이렇게 설계하고 있었는지 쉽게 이해할 수 있었다.

### 🛠️사용 방법

해당 레포에서 사용해보면서 간단하게 정리해볼 생각이다.

## 고민할 리스트

주변 블로그만 봐도 간단 사용법만 나와서 큰 도움이 안됐다.
심지어 업데이트되어 편리하게 제공하는 `API`가 많아졌지만 전부 옛날 버전 정보만 나와서 신뢰도가 더 떨어졌다.
그래서 문서를 읽고 내가 처한 상황에서는 어떻게 진행할지를 정리해서 공유해보려 한다.
고민한 내용은 다음과 같다.

- 분산 서비스에서 어떻게 계약을 검증할 데이터를 관리할 것인가?
- 버전에 따라 달라지는 경우는 어떻게 수행해야할까?
- 테스트컨테이너로 격리됐을 때는 어떻게 진행해야 할까?

### 분산 서비스에서 어떻게 계약을 검증할 데이터를 관리할 것인가?

분산 서비스는 동일한 계약에 맞게 동작을 해야 한다. 여러 서버에 걸쳐 동일한 계약서를 가지는건 어렵다.
`spring-cloud-contract`를 사용하면
계약서를 [GIT 저장소](https://docs.spring.io/spring-cloud-contract/reference/using/provider-contract-testing-with-stubs-in-git.html)
에서 관리할 수 있다.

```Java

@RegisterExtension
public StubRunnerExtension stubRunnerExtension = new StubRunnerExtension()
        .downloadStub("com.example", "artifact-id", "0.0.1")
        .repoRoot("git://git@github.com:spring-cloud-samples/spring-cloud-contract-nodejs-contracts-git.git")
        .stubsMode(StubRunnerProperties.StubsMode.REMOTE);
```

클라우드 저장소에 관리하게 된다면 다음처럼 사용하기 위해 조회하는 과정을 거치게 된다.

![image](https://github.com/this-is-spear/hello-spring-contract/assets/92219795/084a0697-6515-4c83-b69b-caf2a2cc1f20)

그런데 공급자가 변경된다면 소비자의 동작은 새롭게 변경된 계약서를 확인해야하지만 그렇지 않게 된다.

![image](https://github.com/this-is-spear/hello-spring-contract/assets/92219795/dce54f58-53bb-4324-8e88-b0b8fbe31481)

이런 문제로 생각한 고민은 다음과 같다.

- 공급자가 변경될때마다 수동으로 변경하면 무조건 실수가 발생한다.
- 소비자는 이전 계약서를 기반으로 빌드될텐데, 새로운 계약서로 빌드된 공급자와 통신할 수 없게 된다.

첫 번째 고민부터 살펴보겠다.
우선 수동으로 업데이트하게 되면 분명 누락으로 인해 예상치 못한 문제가 발생할 수 있다.

![image](https://github.com/this-is-spear/hello-spring-contract/assets/92219795/a0c951b7-d189-4705-b6b2-a62181f73a55)

그래서 고안한 건 자기 자신을 검증해 이전 계약서와 틀려진 부분이 있다면 최신화 할 수 있도록 빌드 전에 검증하는 프로세스를 만들면 된다.

![image](https://github.com/this-is-spear/hello-spring-contract/assets/92219795/35d60a2f-4bda-4355-9a8c-18a3e18de9f3)

두 번째 고민은 이전 계약서로 빌드된 소비자는 새로운 계약서로 빌드된 공급자와 통신할 수 없는 문제다.

![image](https://github.com/this-is-spear/hello-spring-contract/assets/92219795/c5c6fc1d-0046-43ac-babe-d81f6079e732)

이런 문제를 해결하기 위해서는 주기적으로 빌드해서 실패하는지를 검증하는 로직을 추가해야 한다.

![image](https://github.com/this-is-spear/hello-spring-contract/assets/92219795/f115282e-9a7d-4815-807c-db7af8e1283a)

거기에다 나는 하나의 일에만 집중하는 걸 좋아하다보니 코드 작성에만 집중할 수 있도록 복잡한 과정을 자동화해볼 생각이다.

### 🛠️버전에 따라 달라지는 경우는 어떻게 수행해야할까?

버전마다 관리할 필요성이 있다.

### 🛠️테스트컨테이너로 격리됐을 때는 어떻게 진행해야 할까?

고민중이다.

## 진행하면서 배운점

### 🛠️CRLF 오랜만이야

`CRLF`로 작성된 문서를 `GIT`이 `LF`로 변경하게 되면서 경고를 하게 되는데 오랜만에 보는 키워드라 복기해봤다.

```text
warning: in the working copy of 'gradlew.bat', CRLF will be replaced by LF the next time Git touches it
```
