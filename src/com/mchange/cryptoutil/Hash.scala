package com.mchange.cryptoutil

import java.security.MessageDigest
import scala.collection.immutable

object Hash:
  trait Hasher[H <: Hash]:
    def hash[T : Byteable](t : T)      : H
    def withBytes[T : Byteable](t : T) : H
    def withHexBytes( hex : String )   : H

  object SHA3_256 extends Hasher[SHA3_256]:
    def hash[T : Byteable](t : T) : SHA3_256 =
      val md = MessageDigest.getInstance("SHA3-256") // see https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#messagedigest-algorithms
      immutable.ArraySeq.unsafeWrapArray( md.digest( t.toByteArray ) )
    def withBytes[T : Byteable](t : T) : SHA3_256 =
      t.toSeq.ensuring( _.length == 32, "A Hash.SHA3_256 has must contain exactly 32 bytes. It contains '${t.hex0x}', which is not the correct length.")
    def withHexBytes( hex : String ) : SHA3_256 =
      hex.decodeHexToSeq.ensuring( _.length == 32, "A Hash.SHA3_256 has must contain exactly 32 bytes. '${hex}' is not the correct length.")

  opaque type SHA3_256 <: Hash = immutable.Seq[Byte]

  extension ( h : Hash )
    def toByteArray : Array[Byte] = h.toArray
    def toSeq       : Seq[Byte]   = h
    def hex         : String      = _hex( h.toArray )
    def hex0x       : String      = _hex0x( h.toArray )

opaque type Hash = immutable.Seq[Byte]

