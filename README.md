# car

Simple car game for 2 clojurians. Goal is collect clojure logos by controlling the car. The car can be controlled by 2 functions: speed and direction. Each clojurian controls either speed or direction. Clojurians control their functions by connecting to server via nREPL and executing code.

## Usage

Build cljs source:
```shell
lein cljsbuild once
```
Start repl:
```shell
lein repl
```
Start web server:
```clojure
(require 'car.server)
(car.server/start-server)
```
Open link: http://localhost:8080 and check instructions. Your fellow clojurian should open same page using your ip, e.g. http://192.168.0.42:8080.

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
