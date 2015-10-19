package org.jigson.samples

import org.scalatest.Matchers
import org.jigson.model.JIGValue
import org.jigson.model.JIGObject
import org.jigson.model.JIGArray
import scala.util.Success
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SingleStyleTest extends TestParser with Matchers {
  import org.jigson.style.SingleStyle._
    testRule[JIGValue](
      title = "Test full input : object creation",
      input = """
{
  attr1 : true
  attr2 : 
  {
    attr21 : [ 0, 1, 2, 3, 4 ]
    attr22 : "chaine"
  }
}
""",
      rule = _.Input.run(),
      fvalidate = 
        _ match {
        case Success(obj) => 
          println("------------------------------------------------------")
          println(obj.toStyle()) 
        case _ => fail()
      }
      
  )
  
  testRule[JIGValue](
      title = "Test full input : array creation",
      input = """
[
  true
  "chaine2" 
  {
    attr21 : [ 0, 1, 2, 3, 4 ]
    attr22 : "chaine"
  }
]
""",
      rule = _.Input.run(),
      fvalidate = 
        _ match {
        case Success(obj) => 
          println("------------------------------------------------------")
          println(obj.toStyle()) 
        case _ => fail()
      }
      
  )
  
  testRule[JIGValue](
      title = "Test full input : object without brakets",
      input = """
  attr1 : true
  attr2 : 
  {
    attr21 : [ 0, 1, 2, 3, 4 ]
    attr22 : "chaine"
  }
""",
      rule = _.Input.run(),
      fvalidate = 
        _ match {
        case Success(obj) => 
          println("------------------------------------------------------")
          println(obj.toStyle()) 
        case _ => fail()
      }
      
  )

     testRule[JIGValue](
      title = "comlexe object jig treble style",
      input = """
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
""",
      rule = _.Input.run(),
      fvalidate =
        _ match {
        case Success(obj) => 
          println("------------------------------------------------------")
          println(obj.toStyle())   
        case _ => fail()
        
      }
  )

  testRule[JIGValue](
      title = "comlexe object jig light style",
      input = """
dansers = [ 
	{ name "Maxime" age 32 level {
		 "light jig" "expert" "slip jig" "newbie" "single jig" "newbie" "treble jig" "intermediate" } }
	{ name "Cedric" age 32 level {
		"light jig" "intermediate" "slip jig" "newbie" "single jig" "intermediate" "treble jig" "newbie" } } ]

""",
      rule = _.Input.run(),
      fvalidate =
        _ match {
        case Success(obj) => 
          println("------------------------------------------------------")
          println(obj.toStyle())  
        case _ => fail()
        
      }
  )

  testRule[JIGValue](
      title = "comlexe object jig single style",
      input = """
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
""",
      rule = _.Input.run(),
      fvalidate =
        _ match {
        case Success(obj) => 
          println("------------------------------------------------------")
          println(obj.toStyle())   
        case _ => fail()
        
      }
  )

  testRule[JIGValue](
      title = "comlexe object jig slip style",
      input = """
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
""",
      rule = _.Input.run(),
      fvalidate =
        _ match {
        case Success(obj) => 
          println("------------------------------------------------------")
          println(obj.toStyle())  
        case _ => fail()
        
      }
  )
  
  
}