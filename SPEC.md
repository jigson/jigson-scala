JIGSon
======

JIGSon is a JSon format based on the irish danse Jig. 
Like a father whould tell to his son :
> Json Is Gone Son 

By Maxime Lemaire and Cedric Lechevrel.

Be warned, this spec is still changing a lot. Until it's marked as 1.0, you
should assume that it is unstable and act accordingly.

Objectives
----------

JIGSon aims to be a configuration file format that is more permissive than JSon and also provide some new features.
JSon is JIGSon complient. A JIGSon parser is able to read a well formatted JSon file.

Table of contents
-----------------

- [Example](#user-content-example)
- [Spec](#user-content-spec)
- [Comment](#user-content-comment)
- [String](#user-content-string)
- [Number](#user-content-number)
- [Boolean](#user-content-boolean)
- [Null](#user-content-null)
- [Array](#user-content-array)
- [Object](#user-content-object)
- [Value reference and merging](#user-content-valuereference)
- [Implementations](#user-content-implementations)

Example
-------

```JSON

owner {	name = "Maxime Lemaire & Cedric Lechevrel" }

# Array of server configuration
servers [

  # Indentation (tabs and/or spaces) is allowed but not required
  alpha {
  	ip = "10.0.0.1"
  	dc = "eqdc10"
  }

  beta {
  	ip = "10.0.0.2"
  	dc = "eqdc10"
  }
]

database1 {
  server = "192.168.1.1"
  ports = [ 8001 8001 8002 ]
  connection_max = 5000
  enabled = true
}

database2 {
  server = "192.168.1.2"
  ports = [ 8001 8001 8002 ]
  connection_max = 5000
  enabled = true
}

#Array of databases using references
databases [ @database1 @database2 ]


```

Spec
----

* JIGSon is case sensitive.
* A JIGSon file must contain only UTF-8 encoded Unicode characters.
* Whitespace means tab or space.
* Newline means LF or CRLF.

### Structure of a JIGSon file

* A JIGSon file start with an object or an array declaration
* Unlike JSon, first brackets '{' are not required, it will create an object containing all the "key-value' declarations

```JSon
message = "hello world"
version = 0.1
```
will produce the same result as
```JSon
{
  message = "hello world"
  version = 0.1
}
```

```JSon
[ "hello world", 0.1 ]
```
will produce an array instead of an object.



### "key-value" format


* Separator between a key and a value can be '=' or ':' or just a space 
```JSon
message = "hello world",
// Same as
message : "hello world"
// Same as
message "hello world"
```

* Separator between key-value's can be a ',' or ';' or an end of line or just a space
```JSon
msg1 = "Hello world",
msg2 : "Happy world";
msg3 "A world owned by jig dansers"
```
is exactly identical as
```JSon
msg1 "Hello world" msg2 "Happy world" msg3 "A world owned by jig dansers"
```
Or
```JSon
msg1
"hello world"
msg2
"Happy world"
msg3
"A world owned by jig dansers"
```

Comment
-------

* Inline comments start with '//' or '#' or '--' and end with a end of line.
* Block comments start with '/*' or '"""' or '{-' or '%{' and end with '*/' or '"""' or '-}' or '}%'.

```JSON
key = "value" // This is an inline comment
# This one too

/* Block comment 
can have new lines */

b = "this" # Comments
c /* can be */ = /* every where */ "that"

``` 



String
------

Basic strings are surrounded by quotation marks. Any Unicode character may
be used except those that must be escaped: quotation mark, backslash, and the
control characters (U+0000 to U+001F).

```JSON
str = "I'm a string. \"You can quote me\". Name\tJos\u00E9\nLocation\tSF."
```

For convenience, some popular characters have a compact escape sequence.

```
\b         - backspace       (U+0008)
\t         - tab             (U+0009)
\n         - linefeed        (U+000A)
\f         - form feed       (U+000C)
\r         - carriage return (U+000D)
\"         - quote           (U+0022)
\\         - backslash       (U+005C)
\uXXXX     - unicode         (U+XXXX)
\UXXXXXXXX - unicode         (U+XXXXXXXX)
```

Any Unicode character may be escaped with the `\uXXXX` or `\UXXXXXXXX` forms.
The escape codes must be valid Unicode [scalar values](http://unicode.org/glossary/#unicode_scalar_value).

A multiline string is pretty easy to write :
```JSON
str = "this
is
a multi
line string"
```

Number
------

Like JSon spec, all number are considered float numbers.

```JSON
intValue = 15
floatValue = 15.02
negativeValue = -12.8
exponentialFormat = 15E+12

```

Boolean
-------

Boolean are simply 'true' or 'false' (not case sensitive)

```JSon
boolVal1 true
boolVal2 FALSE
```

Null
----

Easy as !
```JSON
nullValue = null
```

Arrays
------

Arrays are simply lists of values. Each value can be of a different type (object, null, value, boolean, string, number or array).
* An array start with a '(' or a '[' and end with a ')' or a ']'
* Separators between values can be ',' or ';' or just a space (as always, new lines and comment are permitted)

```JSon
arrayOfInt [ 1, 2, 3, 4 ]

arrayOfMixedTypes [ 1 true "a multi
line string" 
	[ 
		"a string in a nested array"
		12 
		false 
	] 
]

objectsInAnArray = [ {name "maxime" age 32 } { name "cedric" age 32 } ]   
 
```

Objects
-------

* Objects are composed by key-value pair and can contain any type of value. A key is a string.

```JSON
simpleKey = "hello world"
"a key with spaces and spéciaux caractères" = "happy world"
```

* An object start with a '{' and end with a '}'. Nesting is of course permitted.

```JIGSon
database1 {
  address { server "192.168.1.1"
  			ports [ 8000 8001 8002 ]
  }
  connection_max 5000
  enabled true
}

```

* Object can also be defined with by a pair dotted key-value. This way to write object is comparable to writing java properties files.

The above exemple can also be written in this following way : 

```JSON
database1.address.server = "192.168.1.1"
database1.address.ports = [ 8000 8001 8002 ]
database1.connection_max = 5000
database1.enable = true

// Dotted key can also be a string
dotted."key with"."a string" = true
// is same as :
dotted = { "key with" = { "a string" = true } } 
```



Value reference
---------------

Every JIGSon value can be referenced by the operator '@'. 

### Reference of a value :

```JSON
value1 = "hello world"
value2 = @value1
value3 = @"value1" // works too !
```

### Reference of a value inside an object

```JSON
obj { value1 = "hello world" }
value2 = @obj.value1 // value2 is eq to "hello world"
```

### Recursive reference of a value

```JSON
// Different servers
server1 = "192.168.0.1"
server2 = "192.168.0.2"

// Choosen server
choosenServer = "server1"

// Connection based on the choosen server.
connection = { server : @@choosenServer , port = 8000 }

/* 
Step by step,
Step 1 : 
connection.server = @@choosenServer
Step 2 : @choosenServer is computed
connection.server = @"server1"
Step 3 : @"server1" is computed
connection.server = "192.168.0.1"
*/
```

The next exemple shows the power of the references, but it might not happen so often to be in a such situation. 
```JSON 
addresses { dns1 = "localhost", dns2 = "jigson-serv" } }

localhost = "192.168.0.1
"jigson-serv" = "81.0.0.1"

proto = "dns2"

server { addressIP = @@addresses.@proto }

/*
Step 1 :
server.addressIP = @@addresses.@proto }
Step 2 : @proto is computed
server.addressIP = @@addresses.dns2
Step 3 : @addresses.dns2 is computed
server.addressIP = @"jigson-serv"
Finally Step 4 : @"jigson-serv" is computed
server.addressIP = "81.0.0.1"
*/
```


### Reference value for a key

```JSON
value1 = "message"
@value1 = "hello world"

/* 
This will create an entry : 
message = "hello world"
*/
```

### Merging objects

To merge objects, the above condition have to be reached :
* The @reference must be an object type (obvious)
* The @reference must inside an object (obvious)
* The @reference is at a key position
* To avoid ambiguous must be followed by a ',' or ';' or by an end of line

```JSON

server { ip "192.168.0.1", port 8080 }
httpServer { server @server, homePage "/index.html" }

// No merge because of the key. 
httpServer { 
	server { ip "192.168.0.1", port 8080}
	homePage "/index.html" 
	} 


httpServer { @server, homePage = "/index.html" }
// or
httpServer { 
	@server
	homePage "/index.html" }
// Will have as result a merged object :
httpServer { 
	ip : "192.168.0.1"
	port : 8080
	homePage : "/index.html" } 
// Amazing isn't it ?
``` 

### Reference of value and arrays

* Just use the @reference notation inside an array

```JSON
server1 { ip "192.168.0.1", port 8080 }
server2 { ip "192.168.0.2", port 9000 }
serverList [ @server1 @server2 ]
```

### Merging in arrays

* Use the <@reference notation inside an array
* the <@reference must be an array or an object (it merges all the values of the object)

```JSON
christophe { name = "Christophe" }
maxime { name = "Maxime" }
cedric { name = "Cedric" }
scott { name = "Scotty" }
phill { name = "Phill" }


systemsAdmins [ @christophe @maxime ]
networkAdmins [ @cedric @scott ]
admins [ 
	<@systemAdmins   // Merge an array 
	<@networkAdmin 	 // Merge an array
	@phill           // Add a referenced value
	{ name "Bil" } ] // Add a value
// admins whill have 6 objects

```

```JSON
protocoles {
	http : 8080
	ssh : 22
	ftp : 21
	torrent : "312-318" }

openPorts = [ <@protocoles ] // values of protocoles merged into the array
// openPorts = [ 8080 22 21 "312-318" ]
	
```


JIGSon : a life concept
-----------------------

Because we think that to get the peace in the world, we have to think from the basis. If humans get more time to danse the irish jig, the entire world would get so much better. By this fact, we thought about something boring and time consuming that make people crazy : writing configuration file ! This is the the deep purpose of JIGSon ! No more time spent to 
think about separators. No more time to launch again the app because the parser did not find the ','. Less time to write many time the same things thanks to the references. So much time saved to danse the irish jig.
However, like jig danse, try to learn one style by one. If you try all the available style in the same time prepare to be mind blown. Think about the best style for the best use case. Some are more human-readable, while others are more compressed.

Here are some different styles :

**slip jig** style (longer and more elegant) :
```JSon
dansers = [
	{ 
		name 	= "Maxime"
		age 	= 32
		level 	= {
			"light jig" 	= "expert"
			"slip jig" 		= "newbie"
			"single jig" 	= "newbie"
			"treble jig"	= "intermediate"
		}
	}
	{
		name = "Cedric"
		age = 32
		level = {
			"light jig" 	= "intermediate"
			"slip jig"		= "newbie"
			"single jig"	= "intermediate"
			"treble jig"	= "newbie"
		}
	}
]
```

**Light jig** style (short, speed, not easy to read) :
```JSon
dansers = [ 
	{ name "Maxime" age 32 level {
		 "light jig" "expert" "slip jig" "newbie" "single jig" "newbie" "treble jig" "intermediate" } }
	{ name "Cedric" age 32 level {
		"light jig" "intermediate" "slip jig" "newbie" "single jig" "intermediate" "treble jig" "newbie" } } ]

``` 

**Treble jig** style (hard jig, heavy jig, more structured, no mistake permitted)
```JSon
maxime = { name "Maxime", age 32 }
cedric = { name "Cedric", age 32 }
levels = { l1 "intermediate", l2 "newbie", l3 "expert" }
jigs = { light "light jig", slip "slip jig", single "single jig", treble "treble jig" }

dansers = [ 
	{ @maxime 
	  level { 
	  	@jigs.light @levels.l3
		@jigs.slip @levels.l1
		@jigs.single @levels.l1
		@jigs.treble @levels.l2 } }
	{ @cedric
	  level { 
	  	@jigs.light @levels.l2
	  	@jigs.slip @levels.l1
	  	@jigs.single @levels.l2
	  	@jigs.treble @levels.l1 } }
]
```

**Single jig** style (straight, alone, easy to learn, boring after many years of practice)
```JSon
maxime.name 				: "maxime"
maxime.age  				: 32
maxime.level."light jig" 	: "expert"
maxime.level."slip jig"		: "newbie"
maxime.level."single jig"	: "newbie"
maxime.level."treble jig"	: "intermediate"

cedric.name					: "cedric"
cedric.age					: 32
cedric.level."light jig"	: "intermediate"
cedric.level."slip jig"		: "newbie"
cedric.level."single jig"	: "intermediate"
cedric.level."treble jig"	: "newbie"

dansers 					: [ @maxime, @cedric ]

```

implementations
---------------

* Scala
* JS