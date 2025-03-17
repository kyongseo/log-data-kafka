### 요구사항
1. log_maker에 docker-compose 실행 시 log_data.log 라는 파일이 생김
2. log_data.log 는 초당 100개의 데이터를 생성함
3. log_data.log는 topic, message, created_at 3개의 데이터를 가지고 있음 
4. 해당 파일을 읽는 기능 
5. 로그는 지속적으로 쌓이기 때문에 새로 생성된 log를 파싱하는 기능 
6. Topic1, topic2, topic3에 맞춰서 kafka 에 전송하는 기능 
7. 4,5,6 은 producer 라는 파일로 동작하도록 기능 개발 
8. Consumer1, consumer2, consumer3 에서 각각 topic1, topic2, topic3의 데이터를 받아서 출력하는 기능 
9. consumer1,2,3은 받은 내용을 consum1.log, consum2.log, consum3.log 형태로 분리해서 저장

### 구현
- `producer는 .log 파일을 읽어와 topic에 로그 파일을 생성해서 전송 -> consumer는 topic에 있는 파일 데이터를 읽어와서 분리하여 파일 생성`

1. `KafkaProducerApp.java`
   1. Properties 객체 : `Properties prop = new Properties();`
      - String 형태로 저장되는 애플리케이션의 환경설정과 관련된 속성 저장
   2. ProducerRecord 객체 : `ProducerRecord<String, String> record = new ProducerRecord<>(topic, kafkaMessage);`
      - 키, 파티션을 지정하지 않은 경우 파타션은 라운드로빈(RR) 방식으로 할당

