# kafkarest
Provides a Kafka Rest Interface.


## Java Implementation
Built with:
* Jetty webserver
* Jersey http rest
* Jersey Websocket

Dev tools:
* Netbeans Maven Java Project
* Java 8

Java Projects:
* kafkacommon, define a java utility class to interact with kafka
* kafkarest, expose webapi and websocket

Features:
* client authentication using the filter

TODO write the example


## Rest Methods
### get new messages
method: message  
type: HTTP GET  
description: get all new messages in a topic

#### Example (using Postman rest client)

**Request:**

    http://localhost:7070/api/message?query=%7B%0A%22topic%22%3A%22fast-messages%22%2C%0A%22consumerGroup%22%3A%22consumerGroup1%22%0A%7D

Notes: query parameter is the url encode (https://www.urlencoder.org/) of

    {
    "topic":"fast-messages",
    "consumerGroup":"consumerGroup1"
    }

**Response:**

    {
        "topic": "fast-messages",
        "messages": [
            {
                "message": "test from postman1",
                "offset": 3310
            },
            {
                "message": "test from postman2",
                "offset": 3311
            }
        ]
    }

### write message
method: message  
type: HTTP POST  
description: write a message in the topic

#### Example (using Postman rest client)

**Request:**  

    http://localhost:7070/api/message

**Request Body (application/json)**

    {
    	"producerId":"producerId1",
    	"topic":"fast-messages",
    	"message":"test from postman1"
    }


## WebSocket
Used to notify the client for new messages.

#### Example (using SimpleWebStocketClient a Firefox extension)

    ws://localhost:7070/messaging?topic=fast-messages&consumerGroup=cg1

When new messages are written to the topic:  

    {"topic":"fast-messages","messages":[{"message":"test from postman1","offset":4316}]}
    {"topic":"fast-messages","messages":[{"message":"test from postman1","offset":4317}]}
    {"topic":"fast-messages","messages":[{"message":"test from postman1","offset":4318}]}

