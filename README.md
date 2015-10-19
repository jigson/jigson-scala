# jigson-scala

## Parse JIGSon string

Use the org.jigson.parser.Parser class.

```Scala
val jigsonStr:String = // JIGSon String 
val obj = new Parser(jigsonStr).parse()

```

## Parse a JIGSon file

Use the org.jigson.parser.Reader class

```Scala
Reader.readFile(filename) match {
    case Success(obj) => println(obj.toStyle());
    case _ => 
  }
```
## JIGSon styles

To export a JIGSon tree object, use different styles. Import the needed style using the package : org.jigson.style....
Then use the .toStyle() method on a JIGValue object.

Exemple :
```Scala
  // Import the style JSON
  import org.jigson.style.JSONStyle._
  
  val obj:JIGValue // parse or build a JIGValue
  
  val json:String = obj.toStyle()

```

Available styles :
- JSON style
- Single style 