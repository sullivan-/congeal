package congeal

import scala.reflect.macros.Context
import scala.reflect.macros.Universe
import scala.reflect.internal.MissingRequirementError

/** Utility for looking up a static class by fully qualified name.
  * 
  * It seems like i should be able to do this with c.mirror.staticClass, but I can't.
  */
private[congeal] trait StaticSymbolLookup {
  self: MacroImpl =>

  import c.universe._

  protected def staticSymbol(name: String): c.Symbol =
    staticSymbolByParts(name.split('.').toList)

  private def staticSymbolByParts(parts: List[String]): c.Symbol = {
    if (parts.size == 1) {
      c.mirror.staticClass(parts.head)
    }
    else {
      staticSymbolByParts0(outermostSymbol(parts.head), parts.tail)
    }
  }

  private def staticSymbolByParts0(outer: c.Symbol, parts: List[String]): c.Symbol = {
    val outerClass = if (outer.isModule) outer.asModule.moduleClass else outer
    if (parts.size == 1) {
      outerClass.typeSignature.member(TypeName(parts.head))
    }
    else {
      // for some reason, Scala compiler is dying here in tailcalls phase
      // temporize with local val to avoid tail call opt
      val inner = outerClass.typeSignature.member(TermName(parts.head))
      if (inner == NoSymbol) {
        NoSymbol
      }
      else {
        val x = staticSymbolByParts0(inner, parts.tail)
        x
      }
    }
  }

  private def outermostSymbol(name: String): c.Symbol = {
    try {
      c.mirror.staticPackage(name)
    }
    catch {
      case _: MissingRequirementError => c.mirror.staticModule(name)
    }
  }

}
