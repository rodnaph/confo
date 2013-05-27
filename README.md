
# Easy Ambient Config [![Build Status](https://api.travis-ci.org/rodnaph/confo.png)](http://travis-ci.org/rodnaph/confo) [![Dependencies Status](http://clj-deps.herokuapp.com/github/rodnaph/confo/status.png)](http://clj-deps.herokuapp.com/github/rodnaph/confo)

Confo allows simple access to ambient configuration provided through environment variables.

## Usage

First include Confo where you need it and then fetch your applications configuration
with the _confo_ function.

```clojure
(ns my.project
  (:use confo.core))

(def config (confo :myproject))
```

The _config_ symbol will now be a hash-map, loaded with any environment variables
that match the name _myproject_.  So for instance you could have configuration
like this...

```
export MYPROJECT_USER="foo"
export MYPROJECT_BAR="some other value"
export MYPROJECT_WITH_BARS="bazzle"
```

And these will be available from as...

```clojure
(:user config)      ; => "foo"
(:bar config)       ; => "some other value"
(:with-bars config) ; => "bazzle"
```

## Defaults

You can also specify default values that will be used if any configuration is
not specified.

```clojure
(confo :myproject
       :port 123
       :name "Some Value")
```

## Type Coercion

When checking default values, Confo will also try to coerce the types of any
environment variables to the matching type of their default.  So for instance
if you need a port number to start your service on then you'll probably want
to configure a default...

```clojure
(def config (confo :myproject
                   :port 123))
```

So now, any port specified through the environment variable _MYPROJECT_PORT_ will
be coerced to an integer.

This is available for extension via a multimethod _confo.core/coerce_.

### Keyword Parameters

Defaults specified as keywords will be created as such.

```
export FOO_BUBBLE="bobble"
```

Will...

```clojure
(:bubble (confo :foo
                :bubble :default)) ; => :bobble
```

### Vector CSVs

Another "coercable" default is a CSV to a vector.  So...

```
export FOO_BAR="1,2,3"
```

Can become...

```clojure
(:bar (confo :foo 
             :bar [])) ; => ["1" "2" "3"]
```

## Installation

Confo is available from [Clojars](https://clojars.org/confo).

## Testing

Unit testing provided by [Midje](https://github.com/marick/Midje)

```
lein midje
```

