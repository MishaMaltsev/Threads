# Assignment 4 Activity 1
## Description
The initail Performer code only has one function for adding strings to an array: 

## Protocol

### Requests
request: { "selected": <int: 1=add, 2=pop, 3=display, 4=count, 5=switch,
0=quit>, "data": <thing to send>}

  data <string>: add
  data <int> pop
  data <int> <int> switch but send as String
  data "" count, quit, and switch have an empty String



### Responses

sucess response: {"type": <"add",
"pop", "display", "count", "switch", "quit"> "data": <thing to return> }

type <String>: echoes original selected from request
data <string>: add = new list, pop = new list, display = current list, count = num elements, switch = new list with switched elements


error response: {"type": "error", "error"": <error string> }
Should give good error message if something goes wrong


## How to run the program
### Terminal
Base Code, please use the following commands:
```
    For Server, run "gradle runServer -Pport=9099 -q --console=plain"
    or
    "gradle runTask1"
    "gradle runTask2"
    "gradle runTask3"
```
```   
    For Client, run "gradle runClient -Phost=localhost -Pport=9099 -q --console=plain"
    or
    "gradle runClient"
```   

## Program Functionality
### Task 1
pop, display, count, and switch working as asked for in the requirements. Run it using the initial recommended way or by running "gradle runTask1" for the server and "gradle runClient" for the client on a local machine.

### Task 2
ThreadedServer works between different threads. Run it the same as mentioned above, but use "gradle runTask2" instead

### Task 3
ThreadPoolServer will try to limit count but full functionality is not there. it does keep track of the number of threads that have connected, but does not keep track of if one quits, another can be started. Run it the same as the previous with "gradle runTask3" for a default pool of 1. Add pool=(max num of threads) with "gradle runTask3" to choose how many threads are wanted. Full implementation currently does not work unfortunately.

### Video Link
https://youtu.be/P2ulE9Z_i7Y


