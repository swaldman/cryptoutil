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
    def hex                  : String     = _hex( t.toByteArray )
    def hex0x                : String     = _hex0x( t.toByteArray )
    def toBigInteger         : BigInteger = new BigInteger( t.toByteArray )
    def toUnsignedBigInteger : BigInteger = new BigInteger( 1, t.toByteArray )
    def toBigInt             : BigInt     = BigInt( toBigInteger )
    def toUnsignedBigInt     : BigInt     = BigInt( toUnsignedBigInteger )
    def ^( other : T ) : T = fromByteArray(xor(t.toByteArray,arr(other)))

private def _hex( ba : Array[Byte] ) : String =
  ByteUtils.toLowercaseHexAscii( ba ); // should we switch to the DatatypeConverter implementation of hex encoding/decoding?

private def _hex0x( ba : Array[Byte] ) : String =
  "0x" + _hex(ba)

private def arr[T]( t : T )(using Byteable[T]) = summon[Byteable[T]].toByteArray(t)

private def xor( _bytes : Array[Byte], other : Array[Byte] ) : Array[Byte] =
  require( _bytes.length == other.length, s"We can only xor sequences or arrays of the same length. [_bytes.length: ${_bytes.length}, other.length: ${other.length}]" )
  (0 until _bytes.length).map( i => (_bytes(i) ^ other(i)).toByte ).toArray


