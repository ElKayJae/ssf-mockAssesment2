Json body sent from postman:

{"name":"abc", 
"email":"123@mail.com", 
"tsyms":"SGD,USD,EUR",
"cryptoList":
[{"crypto":"BTC", "quantity":1.5},{"crypto":"ETH", "quantity":5},{"crypto":"ADA", "quantity":20}]
}

...............

please add your own passwords under main/resources/application.properties

crypto.compare.apikey=
spring.redis.host=
spring.redis.port=
spring.redis.password=

.............