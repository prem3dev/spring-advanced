# Lv1. 코드 개선
### 1-1 : 코드 개선 퀴즈 - Early Return
package org.example.expert.domain.auth.service.AuthService; 의 'signup()'
### 1-2 : 리팩토링 퀴즈 - 불필요한 if-else 피하기
package org.example.expert.client.WeatherClient; 에 있는 'getTodayWeather()'
### 1-3 : 코드 개선 퀴즈 - Validation
package org.example.expert.domain.user.service.UserService; 의 'changePassword()'
package org.example.expert.domain.user.controller.UserController; 의 'changePassword()'
package org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
# Lv2. N+1 문제

1) 코드 분석

* 개요 

  TodoRepository의 findAllByOrderByModifiedAtDesc()는, 
todos table 전체 중 일정 범위를 페이징하고 수정 일자 기준 내림차순으로 정렬하여 조회하는 메서드이다.

  하지만 이러한 조회는 지연로딩 즉시로딩을 불문하고, Todo Entity를 전체 조회하는 쿼리 + 각각의 Todo Entity의 수만큼 users table에 접근하는
쿼리가 DB에 중복으로 전달되어 DB 성능을 낮추는 결과를 불러오게 된다.

* Fetch Join

  본 메서드 상의 @Query()로 지칭된 JPQL은 위와 같은 N+1 문제를 어느 정도 방지하기 위해 Fetch Join 기능을 사용하고 있다.
  
   Fetch Join이란, JPQL에서 성능 최적화를 위해 fetch join을 제공하며 연관된 엔티티나 컬렉션을 SQL 한번으로 조회할 수 있도록 해주는 기능이다.

  fetch join 후 모든 Entity가 영속성 컨텍스트로 관리되며 Proxy 객체가 아닌, 진짜 Entity 객체를 조회한다.
  
*  Fetch Join과 Paging

   Fetch join을 사용하면 paging을 메모리에서 수행한다.
   조회 쿼리에서 페이징을 수행하는 limit절이 삭제되고, 전체를 조회한 후 메모리 상에서 요구된 페이지의 조회처리가 수행된다.
   따라서 필요없는 데이터까지 전체 로드하게 될 뿐만 아니라 서버의 메모리까지 사용되어 성능 하락의 문제가 발생한다.
* 결론
  위 메서드는 중복 쿼리를 전달하여 DB 성능을 낮추는 N+1의 문제가 발생할 수 있으므로 JPQL에서 Fetch Join을 사용하여 이를 방지하였으나,
 이는 페이징에서 전체 데이터 로드 후 메모리 상의 필터링을 수행하게 함을 이유로, 적절하지 못한 N+1 방지 대책이다.
  
2) @EntityGraph 기반의 구현으로 수정
   package org.example.expert.domain.todo.repository; 의 'findAllByOrderByModifiedAtDesc()'
  
# Lv3 테스트 코드 연습
# Lv4. API 로깅
# Lv5. ‘내’가 정의한 문제와 해결 과정
# Lv 6. 테스트 커버리지