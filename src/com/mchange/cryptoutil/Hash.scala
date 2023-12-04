package com.mchange.cryptoutil

import java.security.MessageDigest
import scala.collection.immutable

object Hash:
  object SHA3_256:
    def apply[T : Byteable](t : T) : SHA3_256 =
      val md = MessageDigest.getInstance("SHA3-256") // see https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#messagedigest-algorithms
      immutable.ArraySeq.unsafeWrapArray( md.digest( t.toByteArray ) )
  opaque type SHA3_256 <: Hash = immutable.Seq[Byte]

  extension ( h : Hash )
    def toByteArray : Array[Byte] = h.toArray
    def toSeq       : Seq[Byte]   = h
    def hex         : String      = _hex( h.toArray )
    def hex0x       : String      = _hex0x( h.toArray )
    

opaque type Hash = immutable.Seq[Byte]

