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

[spring cloud contract](https://docs.spring.io/spring-cloud-contract/reference/getting-started/introducing-spring-cloud-contract.html)는 서로 간 최소한의 계약만을 정의해 `충분히` 검증 할 수 있도록 도와준다.  

충분히 검증한다는 것은, 진행되는 테스트가 계약에 맞게 통신이 정상적으로 이루어진다는 것을 의미할 뿐이지 외부 서비스의 영향을 받지 않는다는 것을 의미하지는 않는다.

> Contract tests are used to test contracts between applications, not to simulate full behavior.
>
> spring 레퍼런스에서도 계약만 테스트할 뿐이지 전체 동작을 시뮬레이션하는게 아니라고 한다.

[참고 링크](https://docs.spring.io/spring-cloud-contract/reference/getting-started/introducing-spring-cloud-contract.html#getting-started-introducing-spring-cloud-contract-purposes)

별다른 설정없이 HTTP 메시지를 스텁해 계약을 검증하고 있다.
스텁은 `groovy`, `yaml`, `java`로 작성할 수 있으며, 각각의 장단점이 있어보인다.
작성되는 형식은 다음처럼 동일하다.

![image](https://github.com/this-is-spear/hello-spring-contract/assets/92219795/253437f0-8b7e-4d6b-8e59-bcf92faf0b4c)

### 🛠️소비자 중심 계약

[해당 링크](https://martinfowler.com/articles/consumerDrivenContracts.html)를 읽고 추구하는 방향을 정리한 뒤 어떤 방식으로 계약을 검증할지 고민해볼 생각이다.

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
`spring-cloud-contract`를 사용하면 계약서를 [GIT 저장소](https://docs.spring.io/spring-cloud-contract/reference/using/provider-contract-testing-with-stubs-in-git.html)에서 관리할 수 있다.

```Java
@RegisterExtension
public StubRunnerExtension stubRunnerExtension = new StubRunnerExtension()
        .downloadStub("com.example","artifact-id", "0.0.1")
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
