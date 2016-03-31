/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2002-2013, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package scala.spores

import scala.reflect.macros.blackbox.Context

import scala.pickling._

trait SimpleSporePicklerImpl {

  def genSimpleSporePicklerUnpicklerTemplate(c: Context)
    (seedPicklerName: String, sporeType: c.Tree): c.Tree = {

    import c.universe._

    val utils = new PicklerUtils[c.type](c)
    val picklee = TermName("picklee")
    val readerName = TermName("reader")
    val builderName = TermName("builder")
    val className = TermName("className")
    val picklerName = c.freshName(TermName(seedPicklerName))

    q"""
      object $picklerName
          extends scala.pickling.Pickler[$sporeType] 
             with scala.pickling.Unpickler[$sporeType] {

        def tag = implicitly[scala.pickling.FastTypeTag[$sporeType]]

        def pickle(picklee: $sporeType, builder: scala.pickling.PBuilder): Unit = {

          builder.beginEntry(picklee, tag)
          ${utils.writeClassName(builderName, picklee)}
          ${utils.writeUnpicklerClassName(builderName, picklerName)}
          builder.endEntry()

        }

        def unpickle(tag: String, reader: scala.pickling.PReader): Any = {

          val className = ${utils.readClassName(readerName)}
          ${utils.createInstance(className, sporeType)}

        }
      }

      $picklerName
    """


  }

  def genSimpleSporePicklerImpl
      [T: c.WeakTypeTag, R: c.WeakTypeTag](c: Context): c.Tree = {

    import c.universe._

    val ttpe = weakTypeOf[T]
    val rtpe = weakTypeOf[R]
    debug(s"T: $ttpe, R: $rtpe")

    val sporeType = tq"scala.spores.Spore[$ttpe, $rtpe]"
    genSimpleSporePicklerUnpicklerTemplate(c)("SimpleSporePickler", sporeType)

  }

  def genSimpleSpore2PicklerImpl
      [T1: c.WeakTypeTag, T2: c.WeakTypeTag, R: c.WeakTypeTag]
      (c: Context): c.Tree = {

    import c.universe._

    val t1tpe = weakTypeOf[T1]
    val t2tpe = weakTypeOf[T2]
    val rtpe = weakTypeOf[R]
    debug(s"T1: $t1tpe, T2: $t2tpe, R: $rtpe")

    val sporeType = tq"scala.spores.Spore2[$t1tpe, $t2tpe, $rtpe]"
    genSimpleSporePicklerUnpicklerTemplate(c)("SimpleSpore2Pickler", sporeType)

  }

  def genSimpleSpore3PicklerImpl
      [T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, R: c.WeakTypeTag]
      (c: Context): c.Tree = {

    import c.universe._

    val t1tpe = weakTypeOf[T1]
    val t2tpe = weakTypeOf[T2]
    val t3tpe = weakTypeOf[T3]
    val rtpe = weakTypeOf[R]
    debug(s"T1: $t1tpe, T2: $t2tpe, T3: $t3tpe, R: $rtpe")

    val sporeType = tq"scala.spores.Spore3[$t1tpe, $t2tpe, $t3tpe, $rtpe]"
    genSimpleSporePicklerUnpicklerTemplate(c)("SimpleSpore3Pickler", sporeType)

  }

}
