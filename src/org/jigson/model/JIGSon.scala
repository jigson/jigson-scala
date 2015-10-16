package org.jigson.model

import scala.collection.mutable.{ Map => MMap }
import scala.collection.mutable.{ Map => MMap }

sealed trait JIGValue {
  def as[T <: JIGValue : Manifest]():T = {
    if (manifest[T].runtimeClass.isInstance(this))
      manifest[T].runtimeClass.cast(this).asInstanceOf[T]
    else
      throw new ClassCastException();
  }
}
case class JIGBoolean(value : Boolean) extends JIGValue {
  override def toString = value.toString()
}
case class JIGString(value : String) extends JIGValue {
  override def toString = value
}
case class JIGNumber(value : Double) extends JIGValue {
  override def toString = value.toString()
}
case class JIGNull() extends JIGValue {
  def value = None
  override def toString = "null"
}
case class JIGObject(attrs : MMap[String, JIGValue] = MMap[String, JIGValue]()) extends JIGValue {
 
  override def toString = attrs.mkString("{", ",", "}")
  
  def get[T <: JIGValue : Manifest](attrName:String):T = {
    val res = attrs.get(attrName);
    res match {
      case Some(value) => 
           if (manifest[T].runtimeClass.isInstance(value))
             manifest[T].runtimeClass.cast(value).asInstanceOf[T]
           else
             throw new ClassCastException()
      case None => throw new ClassCastException()
    }
  }
  
  def getOption[T <: JIGValue : Manifest](attrName:String):Option[T] = {
    val res = attrs.get(attrName);
    res match {
      case Some(value) => 
           if (manifest[T].runtimeClass.isInstance(value))
             Some(manifest[T].runtimeClass.cast(value).asInstanceOf[T])
           else 
             None
      case None => None
    }
  }
}
case class JIGArray(values : Seq[JIGValue]) extends JIGValue {
  override def toString = values.mkString("(", ",", ")")
}



