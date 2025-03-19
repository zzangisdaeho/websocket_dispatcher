# 프로젝트 개요
도메인 이벤트를 기반으로 각 websocket 통신부에 push를 해야할 메세지를 dispatch해주는 역할

message broker를 통해 이벤트를 리스닝

websocket이 등록되어있는 channel에 발송해야 할 메세지 정보를 publish.

## 주의사항

- 필요 middle ware
  - redis (websocket 서버가 channel을 바꾸면 따라서 바꿔야함)
  - kafka (message broker가 바뀔시 바뀔 수 있음)


- profile
      
      구현체 profile 필수
      현재로써 broker는 kafka
      connection repository는 redis를 사용하고있습니다.
      고로 profile에 kafka와 redis를 추가해줘야 합니다.

      ex1) java -jar {app}.jar -spring.profiles.active={운영환경},kafka,redis
      ex2) java -Dspring.profiles.active={운영환경},kafka,redis -jar {app}.jar

## 빌드 및 실행
- docker build

        docker build -t websocket_dispatcher .

- docker run

        docker run -d --name websocket_dispatcher --network network-local -e SPRING_PROFILES_ACTIVE=docker,kafka,redis websocket_dispatcher:latest
        