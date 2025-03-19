# 프로젝트 개요
도메인 이벤트를 기반으로 각 websocket 통신부에 push를 해야할 메세지를 dispatch해주는 역할

message broker를 통해 이벤트를 리스닝

websocket이 등록되어있는 channel에 발송해야 할 메세지 정보를 publish한다.

## 주의사항

- 필요 middle ware
  - redis (websocket 서버가 channel을 바꾸면 따라서 바꿔야함)
  - kafka (message broker가 바뀔시 바뀔 수 있음)

## 빌드 및 실행