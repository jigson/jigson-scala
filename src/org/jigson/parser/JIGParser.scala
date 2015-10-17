package org.jigson.parser

import scala.collection.mutable.{ Map => MMap }
import org.jigson.model.JIGArray
import org.jigson.model.JIGBoolean
import org.jigson.model.JIGNull
import org.jigson.model.JIGNumber
import org.jigson.model.JIGObject
import org.jigson.model.JIGString
import org.jigson.model.JIGValue
import org.parboiled2._
import org.parboiled2.ParserInput
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import org.jigson.model.JIGObject

class JIGParser(val input:ParserInput) extends Parser() {
 
  
  import CharPredicate.{Digit19, Digit, HexDigit,Alpha, AlphaNum}
  import JIGReader._
  
  def parse() = {
    Input.run()
  }
  
  
  def WhiteSpaceChar = CharPredicate(" \n\r\t\f")
  def QuoteBackslash = CharPredicate("\"\\")
  def CommaChar = CharPredicate(",;")
  def WhiteSpace = rule { (WhiteSpaceChar | LineComment | BlockComment).* }
  def WhiteSpaceComma = rule { (WhiteSpaceChar | LineComment | BlockComment | CommaChar).* }
  def Space = CharPredicate(" \t")
  def EOL = rule { (ch('\r') | ch('\n')).+ }
  
  def Input:Rule1[JIGValue] = rule { (Array | Object | Declaration) ~ WhiteSpace ~ EOI }
  
  def Declaration:Rule1[JIGObject] = rule {  Entries  ~> ((entries:Seq[EntryType]) => addEntries(entries,JIGObject())) }
  
  // Object
  def Object = rule { ObjectWithOneMerge | ObjectWithMultipleEntries }
  def ObjectWithMultipleEntries:Rule1[JIGObject] = rule  { WhiteSpace ~ "{" ~ Entries ~ WhiteSpace ~ "}" ~ WhiteSpace ~> ((entries:Seq[EntryType]) => addEntries(entries, JIGObject())) }
  def ObjectWithOneMerge:Rule1[JIGObject] = rule  { WhiteSpace ~ "{" ~ WhiteSpace ~ MergingObjectDeclaration ~ WhiteSpace ~ "}" ~ WhiteSpace ~> (convertObjectToEntries(_)) ~> ((entries:Seq[EntryType]) => addEntries(entries,JIGObject())) }
  
  // Array
  def Array:Rule1[JIGArray] = rule { WhiteSpace ~ anyOf("([") ~ WhiteSpace ~ pushSkipNextKeysFlag ~ ArrayValues ~ WhiteSpace ~ anyOf(")]") ~ popSkipNextKeysFlag ~ WhiteSpace  ~> JIGArray }
  def ArrayValues = rule { ArrayValue.*(WhiteSpaceComma) ~> (_.flatten) }
  def ArrayValue = rule { Value ~> (_::Nil) | "<" ~ ReferencedValue ~> (convertValueToValues(_)) }
  def convertValueToValues(value:JIGValue):List[JIGValue] = {
    value match {
      case JIGObject(attrs) => attrs.values.toList
      case JIGArray(values) => values.toList
      case default => default::Nil
    }
  }
  
  
  
  // Key - Value
  type EntryType = (List[String], JIGValue)
  def Entries = rule { (WhiteSpace ~ (MergingObject | Include  ~> (convertObjectToEntries(_))) | Entry ~> (_::Nil) ).+  ~> (_.flatten)}
  def Entry = rule { KeyValueEntry ~ WhiteSpaceComma }
  def KeyValueEntry = rule { Key ~> {k => pushKey(k);k} ~ EntrySeparator ~ Value ~ popKey ~ WhiteSpace ~> { (k, v) => registerValue(k, v); (k, v) } }
  def EntrySeparator = rule { WhiteSpace ~ anyOf(":=").? ~ WhiteSpace }
  def Key = rule { ReferencedKey | DottedKey | SimpleKey ~> (_::Nil)  }
  def SimpleKey = rule { UnwrappedString | SimpleKeyString  }
  
  def SimpleKeyString = rule { capture(Alpha ~ (AlphaNum | anyOf("@-_")).*) }  
  def DottedKey:Rule1[List[String]] = rule { SimpleKey.*(".") ~> ((seq:Seq[String]) => seq.toList) }
  
  def Value:Rule1[JIGValue] = rule { Null | JBoolean | Number | JString | Object | Array | ReferencedValue | Include }
  
  // Include
  def IncludePattern:Rule1[String] = rule { ("!include" | "!inc") ~ Space.* ~  UnwrappedString ~ WhiteSpace }
  def Include = rule { IncludePattern  ~> ((filename:String) => readNewFile(filename)) }
  
  def readNewFile(filename:String) : JIGValue =  {
     readFile(filename) match {
       case Success(obj) => obj
       case _ => fail("WARNING : Cannot include " + filename); JIGObject()
     }
  }
  
  
  // Reference
  def ReferencedKey:Rule1[List[String]] = rule { "@" ~ (ReferencedKey | DottedKeyWithRef | SimpleKey ~> (_::Nil)) ~> (findJIGValue(_)) ~> ((v:JIGValue) => v.toString::Nil) }
  def DottedKeyWithRef:Rule1[List[String]] = rule { SimpleKey ~ "." ~ (ReferencedKey | DottedKeyWithRef | SimpleKey ~> (_::Nil) ) ~> ((first:String, keys:List[String]) => first::keys)}
  def ReferencedValue:Rule1[JIGValue] = rule { "@" ~ (ReferencedKey | DottedKeyWithRef | SimpleKey ~> (_::Nil)) ~> (findJIGValue(_)) }
  def MergingObjectEnd = rule { (CharPredicate(" \t\f") | LineComment | BlockComment).* ~ CharPredicate("\n\r,;") }
  def MergingObjectDeclaration = rule { ReferencedValue  }
  def MergingObject = rule { MergingObjectDeclaration ~ MergingObjectEnd ~> (convertObjectToEntries(_)) }
  val definedKeys = MMap[String, JIGValue]();
  
  def addEntry(deepProp:List[String], value:JIGValue, to:JIGObject):JIGObject = {
    deepProp match {
      case last::Nil => to.attrs += last -> value; to
      case head::tail => 
        val obj = to.attrs.getOrElseUpdate(head, new JIGObject() )
        addEntry(tail, value, obj.asInstanceOf[JIGObject])
        to
      case Nil => to
    }
  }
  def addEntries(entries:Seq[EntryType], to:JIGObject) = {
    entries.foreach{case (k, e) => addEntry(k, e, to)}
    to  
  }
  
  def convertObjectToEntries(obj:JIGValue):Seq[EntryType] = {
    obj match {
      case JIGObject(attrs) => { for ((key, value) <- attrs) yield (key::Nil, value) } .toList
      case _ => fail("ERROR : Merging need a object reference"); Nil 
    }
  }
  
  var currentDeepKey:List[String] = Nil;
  var nextKeyCanBeReferenced:Int = 0; 

  def pushSkipNextKeysFlag = rule { run { nextKeyCanBeReferenced += 1; () } }
  def popSkipNextKeysFlag = rule { run { nextKeyCanBeReferenced -= 1; () } }
  
  def pushKey(key:List[String]) =  {
    if (nextKeyCanBeReferenced == 0 && !key.isEmpty) {
      currentDeepKey = key.mkString(".") :: currentDeepKey;
    }
  }

  def popKey = rule {
    run {
      if (nextKeyCanBeReferenced == 0 && currentDeepKey.nonEmpty) {
        currentDeepKey = currentDeepKey.tail;
      }
      ()
    }
  }
  
  def registerValue(key:List[String], value:JIGValue) = {
      val globalKey = {
        if (currentDeepKey.isEmpty) key.mkString(".")
        else currentDeepKey.mkString(".") + "." + key.mkString(".")
      }
      definedKeys += globalKey -> value
  }
  
  def findJIGValue(deepAttr:List[String]):JIGValue = {
    val refName = deepAttr.mkString(".")
    val value = definedKeys.get(refName)
    value match {
      case Some(res) => res
      case None => println("WARNING : Reference : " + refName + " not found"); JIGNull()
    }
  }
  
  
  // Comment
  
  def LineComment = rule { ("//" | "#" | "--") ~ (!EOL ~ ANY).* ~ EOL }
  def BlockCommentStart = rule { "/*" | "\"\"\"" | "{-" | "%{" }
  def BlockCommentEnd = rule { "*/" | "\"\"\"" | "-}" | "}%" }
  def BlockComment =   rule { BlockCommentStart ~ (!BlockCommentEnd ~ ANY).* ~ BlockCommentEnd }
  
  // Usage of a StringBuilder to convert the escaped chars
  def JString:Rule1[JIGString] = rule { UnwrappedString ~> JIGString }
  def sb:StringBuilder = new StringBuilder;
  def clearSB = rule { run { sb.clear } }
  def pushSB = rule { push(sb.toString()) }  
  def appendLastChar = rule { run { sb.append(lastChar); () } }
  def appendChar(c:Char) = rule { run { sb.append(c); () } }
  def UnwrappedString = rule { '"' ~ clearSB ~ Characters ~ '"' ~ WhiteSpace ~ pushSB }  
  def Characters = rule { (NormalChar | EscapedChar ).* }
  def NormalChar = rule { !QuoteBackslash ~ ANY ~ appendLastChar  }
  def EscapedChar = rule { ch('\\') ~ (
    ch('b') ~ appendChar('\b') |
    ch('f') ~ appendChar('\f') |
    ch('n') ~ appendChar('\n') |
    ch('r') ~ appendChar('\r') |
    ch('t') ~ appendChar('\t') |
    ch('"') ~ appendChar('"') |
    ch('\\') ~ appendChar('\\') |
    ch('/') ~ appendChar('/') |
    Unicode
  )}
  def Unicode = rule { ch('u') ~ capture(HexDigit ~ HexDigit ~ HexDigit ~ HexDigit) ~> {c => sb.append(java.lang.Integer.parseInt(c, 16).toChar); ()} }

  // Number Value
  
  def Number:Rule1[JIGNumber] = rule { NumberExpression ~> JIGNumber }
  def NumberExpression:Rule1[Double] = rule { NumberTerm ~ ( Plus ~ NumberTerm ~> ((_:Double) + _) | Minus ~ NumberTerm ~> ((_:Double) - _)).* }
  def NumberTerm = rule { NumberFactor ~ (Times ~ NumberFactor ~> ((_:Double) * _) | Divided ~ NumberFactor ~> ((_:Double) / _)).* }
  def NumberFactor = rule { NumberPattern | '(' ~ NumberExpression ~ ')' }
  def NumberPattern = rule { capture(Integer ~ Decimal.? ~ Exponent.? ) ~> ( _.toDouble) ~ WhiteSpace }
  def Plus = rule { WhiteSpaceChar.* ~ "+" ~ WhiteSpaceChar.* }
  def Minus = rule { WhiteSpaceChar.* ~ "-" ~ WhiteSpaceChar.* }
  def Times = rule { WhiteSpaceChar.* ~ "*" ~ WhiteSpaceChar.* }
  def Divided = rule { WhiteSpaceChar.* ~ "/" ~ WhiteSpaceChar.* }
  def Integer = rule { '-'.? ~ (Digit19 ~ Digits | Digit) }
  def Digits = rule { Digit.+ }
  def Decimal = rule { '.' ~ oneOrMore(Digit) }
  def Exponent = rule { ignoreCase('e') ~ anyOf("+-").? ~ Digits  }

  // Null Value
  
  def Null:Rule1[JIGNull] = rule { atomic(ignoreCase("null")) ~ WhiteSpace ~> JIGNull }  
  
  // Boolean Value
  
  def JBoolean:Rule1[JIGBoolean] = rule { (True | False) ~> JIGBoolean }
  def True = rule { atomic(ignoreCase("true")) ~ WhiteSpace ~ push(true) }
  def False = rule { atomic(ignoreCase("false")) ~ WhiteSpace  ~ push(false) }

  
}