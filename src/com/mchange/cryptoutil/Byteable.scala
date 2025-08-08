package com.mchange.cryptoutil

import scala.collection.immutable

import java.math.BigInteger

import com.mchange.lang.ByteUtils

object Byteable:
  given ofByteArray : Byteable[Array[Byte]] with
    extension (bytes : Array[Byte]) def toByteArray : Array[Byte] = bytes.clone()
    def fromByteArray( bytes : Array[Byte] ) : Array[Byte] = bytes.clone()
  given ofSeqByte : Byteable[Seq[Byte]] with
    extension ( bytes : Seq[Byte] ) def toByteArray : Array[Byte] = bytes.toArray
    def fromByteArray( bytes : Array[Byte] ) : Seq[Byte] = immutable.ArraySeq.unsafeWrapArray(bytes.clone())
  given ofArraySeqByte : Byteable[immutable.ArraySeq[Byte]] with
    extension ( bytes : immutable.ArraySeq[Byte] ) def toByteArray : Array[Byte] = bytes.toArray
    def fromByteArray( bytes : Array[Byte] ) : immutable.ArraySeq[Byte] = immutable.ArraySeq.unsafeWrapArray(bytes.clone())

trait Byteable[T]:
  extension (t : T) def toByteArray : Array[Byte]
  def fromByteArray( bytes : Array[Byte] ) : T

  extension( t : T )( using Byteable[T] )
    def toSeq                : Seq[Byte]  = immutable.ArraySeq.unsafeWrapArray[Byte]( t.toByteArray ) // Seq[Byte] has a built-in toSeq, so there's no useless call
    def base64               : String     = B64Encoder.encodeToString( t.toByteArray )
    def base64url            : String     = B64UrlEncoder.encodeToString( t.toByteArray )
    def hex                  : String     = _hex( t.toByteArray )
    def hex0x                : String     = _hex0x( t.toByteArray )
    def toBigInteger         : BigInteger = new BigInteger( t.toByteArray )
    def toUnsignedBigInteger : BigInteger = new BigInteger( 1, t.toByteArray )
    def toBigInt             : BigInt     = BigInt( toBigInteger )
    def toUnsignedBigInt     : BigInt     = BigInt( toUnsignedBigInteger )
    def ^( other : T ) : T = fromByteArray(xor(t.toByteArray,arr(other)))



